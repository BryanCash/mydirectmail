/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import soldatos.connection.MyDBCConnection;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 *
 * @author ssoldatos
 */
public class SortByField implements Runnable {

  private int field;
  private MainForm m;
  private BufferedReader in;
  private PrintWriter output;
  private Statement stmt;
  private long start;
  private ResultSet rs;
  private long end;
  private MyDBCConnection mdbc;
  private boolean asc;
  private String order;

  public SortByField(int field, MainForm m, boolean asc) throws SQLException {
    this.field = field;
    this.m = m;
    order = asc ? "ASC" : "DESC";
    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

    MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
        MainForm.options.toString(DmOptions.DATABASE),
        MainForm.options.toString(DmOptions.DB_USER),
        MainForm.options.toString(DmOptions.DB_PASSWORD));
    stmt = MyDBCConnection.myConnection.createStatement();

    m.setTextAreaText(null);
  }

  @Override
  public void run() {
    try {
      sortFields();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    }
  }

  private void sortFields() throws SQLException, IOException {

    start = System.currentTimeMillis();

    // Droping the tables
    m.appendOutput("\nDroping the tmp tables if any");
    dropTmpTables();
    //Create the new empty table
    m.appendOutput("\nCreating the tables");
    createTmpTables();
    fillTmpTable();
    m.appendOutput("\nDB Updated!!!");
    //sort
    m.appendOutput("\nSorting the file");
    sort();

    m.init(true);
    m.appendOutput("\nFile is sorted");
    // Droping the tables
    m.appendOutput("\nDroping the tables");
    dropTmpTables();
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    MainForm.trayIcon.showInfoMessage("Sorting By Field",
        "File is sorted by Field " + field + "\n" +
        "Execution time : " + DmFunctions.execTime(start, end));
    return;


  }

  private void createTmpTables() throws SQLException {
    String sql = "";
    //create main table

    sql = "CREATE TABLE IF NOT EXISTS "+ DmOptions.TMP_DB +"." + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp(" +
        "`id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT," +
        "`data` TEXT NOT NULL," +
        "`sortField` VARCHAR(200) NOT NULL," +
        " PRIMARY KEY (`id`)) ENGINE = INNODB CHARACTER SET " + m.getCharacterSet() +
        " COLLATE " + m.getCollation();
    stmt.executeUpdate(sql);


  }

  private void dropTmpTables() throws SQLException {
    String sql = "DROP TABLE IF EXISTS  " + DmOptions.TMP_DB + "." +
        MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp";
    stmt.executeUpdate(sql);
  }

  private void fillTmpTable() throws SQLException, IOException {
    int j = 0;
    String str, sql = "", insertStmt = "";
    String strArr[] = new String[m.getFields()];
    int lines = 0;

    m.appendOutput("\nUpdating DB");
    String sortField = "";
    PrintWriter tmpOut;


    tmpOut = new PrintWriter(new BufferedWriter(new FileWriter(DmOptions._JAR_DIR_ + "data.tmp")));
    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read: " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      strArr = str.split("" + m.getDelimeter(), -1);
      insertStmt = "";
      for (int i = 0; i < strArr.length; i++) {
        strArr[i] = strArr[i].trim();
        if (i == field) {
          sortField = soldatos.connection.MyDBCFunctions.prepareText(strArr[i]);
        }
      }
      str = soldatos.connection.MyDBCFunctions.prepareText(str, false);
      tmpOut.println(lines + "<|>" + str + "<|>" + sortField);
    }
    tmpOut.close();
    String path = (DmOptions._JAR_DIR_ + "data.tmp");
    sql = "LOAD DATA LOCAL INFILE '" + path + "' INTO TABLE "  + DmOptions.TMP_DB + "." +
        MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp CHARACTER SET " + m.getCharacterSet() +
        " FIELDS TERMINATED BY '<|>' LINES TERMINATED BY '\n'";
    //System.out.println(sql);
    stmt.executeUpdate(sql);
    File f = new File(DmOptions._JAR_DIR_+"data.tmp");
    f.delete();

    m.updateProgress(0);
    in.close();
  }

  private void sort() throws SQLException {
    String sql;

    sql = "SELECT data FROM  " + DmOptions.TMP_DB + "."  + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp ORDER BY sortfield " + order + "";
    rs = stmt.executeQuery(sql);
    String outline;
    int lines = 0;

    while (rs.next()) {
      outline = "";
      lines++;
      outline = rs.getString(1).replaceAll("’", "A");
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(outline + "\n");
      }
      output.println(outline);
    }
    m.IndeterminateProgress(false);
    //m.setCustomers(lines);
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
  }
}

