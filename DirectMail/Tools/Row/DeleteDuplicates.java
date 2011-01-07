/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Row;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import soldatos.connection.MyDBCConnection;
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
import soldatos.connection.MyDBCFunctions;

/**
 *
 * @author ssoldatos
 */
public class DeleteDuplicates implements Runnable {

  private boolean[] fieldsToCheck;
  private boolean export;
  private String exportFilename;
  private BufferedReader in;
  private Statement stmt;
  private long start;
  private long end;
  private MyDBCConnection mdbc;
  private String duplFields = "";
  private String joins = "";
  private PrintWriter output;
  private ResultSet rs;
  private int duplicatesFound;
  private PrintWriter tmpOut;
  private MainForm m;
  private boolean lastName;
  private boolean firstName3Chars;
  private boolean deleteDupls;

  /**
   *
   * @param fieldsToCheck
   * @param export
   * @param exportFilename
   * @param m
   * @param lastName
   * @param firstName3Chars
   * @param deleteDupls
   * @throws SQLException
   */
  public DeleteDuplicates(boolean[] fieldsToCheck, boolean export,
      String exportFilename, MainForm m, boolean lastName,
      boolean firstName3Chars, boolean deleteDupls) throws SQLException {

    this.m = m;
    this.fieldsToCheck = fieldsToCheck;
    this.export = export;
    this.exportFilename = exportFilename;
    this.lastName = lastName;
    this.firstName3Chars = firstName3Chars;
    this.deleteDupls = deleteDupls;
    in = DmFunctions.createBufferedReader(m);
    output = DmFunctions.createPrinterWriter(m, in);
    MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
        MainForm.options.toString(DmOptions.DATABASE),
        MainForm.options.toString(DmOptions.DB_USER),
        MainForm.options.toString(DmOptions.DB_PASSWORD));
    stmt = MyDBCConnection.myConnection.createStatement();


    for (int i = 0; i < fieldsToCheck.length; i++) {
      if (fieldsToCheck[i]) {
        duplFields += "field_" + i + ", ";
        joins += "goodRows.field_" + i + " = badRows.field_" + i + " AND ";
      }
    }
    duplFields = duplFields.substring(0, duplFields.length() - 2);
    joins = joins.substring(0, joins.length() - 5);


  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      start = System.currentTimeMillis();
      m.appendOutput("\nStarting process");
      m.progressBar.setIndeterminate(true);
      dropTable();
      createTable();
      m.appendOutput("\nChecking Duplicates");
      insertInTable();
      addData();
      m.appendOutput("\n"+ (deleteDupls ? "Deleting " : "Checking ") +" Duplicates");
      if (export) {
        saveDeletedLines();
      }
      deleteDupls();
      m.appendOutput("\nGetting unique records");
      retrieveRecs();
      //dropTable();
      end = System.currentTimeMillis();
      m.appendOutput("\nDuplicates : " + duplicatesFound);
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
      MainForm.trayIcon.showInfoMessage( (deleteDupls ? "Deleting " : "Checking ") +" duplicates",
          (deleteDupls ? "Deleting " : "Checking ") + "of duplicate records finished\n" +
          "Duplicates found : " + duplicatesFound +"\n" +
          "Execution time : " + DmFunctions.execTime(start, end));
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      m.progressBar.setIndeterminate(false);
      MainForm.glassPane.deactivate();
    }

  }

  private void createTable() throws SQLException {

    String index = "", fields = "";
    for (int i = 0; i < m.getFields(); i++) {
      if (i < m.getFields() - 1) {
        fields += "field_" + i + " VARCHAR(100) NOT NULL,";
        index += "INDEX index_" + i + " (`field_" + i + "`), ";
      } else {
        fields += "field_" + i + " VARCHAR(100) NOT NULL,  data VARCHAR(1024) NOT NULL,";
        index += "INDEX index_" + i + " (`field_" + i + "`) ";
      }
    }
    String sql = "CREATE TABLE IF NOT EXISTS "+ DmOptions.TMP_DB +"." +
        MainForm.options.toString(DmOptions.DB_PREFIX) +
        "_deletedupl  (" + fields + index + " )ENGINE = MEMORY CHARACTER SET " + m.getCharacterSet() +
        " COLLATE " + m.getCollation();
    //System.out.println(sql);
    stmt.executeUpdate(sql);

  }

  private void dropTable() throws SQLException {
    String sql = "DROP TABLE IF EXISTS " + DmOptions.TMP_DB + "." +
        MainForm.options.toString(DmOptions.DB_PREFIX) + "_deletedupl";
    stmt.executeUpdate(sql);
  }

  private void insertInTable() throws SQLException, IOException {
    String bulk = "";
    PrintWriter out;
    String str, outString;
    String[] curLineArr;
    BufferedReader inBulk;
    if (lastName) {
      inBulk = soldatos.functions.FileFunctions.createInputStream(new File(DmOptions._JAR_DIR_+"tmp/" + m.getOrigFileName()));
      out = soldatos.functions.FileFunctions.createOutputStream(new File(DmOptions._JAR_DIR_+"bulk.txt"), false);
      while ((str = inBulk.readLine()) != null) {
        outString = "";
        curLineArr = str.split("" + m.getDelimeter(), -1);
        for (int i = 0; i < m.getFields(); i++) {

          if (i == m.getSurnameField()) {

            if (curLineArr[i].endsWith("ÏÓ") ||
                curLineArr[i].endsWith("ÏÕ") ||
                curLineArr[i].endsWith("ÇÓ") ||
                curLineArr[i].endsWith("ÁÓ")) {
              curLineArr[i] = curLineArr[i].substring(0, curLineArr[i].length() - 2);
            } else if (curLineArr[i].endsWith("Ç") ||
                curLineArr[i].endsWith("Á")) {
              curLineArr[i] = curLineArr[i].substring(0, curLineArr[i].length() - 1);

            }
          }
          if (i < m.getFields() - 1) {
            outString += curLineArr[i] + m.getDelimeter();
          } else {
            outString += curLineArr[i];
          }
        }
        out.println(outString);
      }

      bulk = "./bulk.txt";
    } else if (firstName3Chars) {
      inBulk = soldatos.functions.FileFunctions.createInputStream(new File("./tmp/" + m.getOrigFileName()));
      out = soldatos.functions.FileFunctions.createOutputStream(new File("./bulk.txt"), false);
      while ((str = inBulk.readLine()) != null) {
        outString = "";
        curLineArr = str.split("" + m.getDelimeter(), -1);
        for (int i = 0; i < m.getFields(); i++) {

          if (i == m.getFirstnameField()) {
            curLineArr[i] = curLineArr[i].substring(0, 3);
          }
          if (i < m.getFields() - 1) {
            outString += curLineArr[i] + m.getDelimeter();
          } else {
            outString += curLineArr[i];
          }
        }
        out.println(outString);
      }

      bulk =  DmOptions._JAR_DIR_+"bulk.txt";

    } else {
      bulk = DmOptions._JAR_DIR_+"tmp/" + m.getOrigFile();
    }
    String sql = "LOAD DATA LOCAL INFILE '" + bulk + "' INTO TABLE "  + DmOptions.TMP_DB + "." +
        MainForm.options.toString(DmOptions.DB_PREFIX) +
        "_deletedupl " +
        "CHARACTER SET " + m.getCharacterSet() + " FIELDS TERMINATED BY '"+m.getDelimeter()+"' LINES TERMINATED BY '\n'";
    stmt.executeUpdate(sql);

  }

  private void addData() throws SQLException, IOException {
    String sql = "ALTER TABLE " + DmOptions.TMP_DB +
        "." + MainForm.options.toString(DmOptions.DB_PREFIX) + "_deletedupl " +
        " ADD COLUMN `id` INTEGER UNSIGNED NOT NULL DEFAULT NULL AUTO_INCREMENT , ADD PRIMARY KEY (`id`);";
    stmt.executeUpdate(sql);
    String str;
    int line = 0;

    while ((str = in.readLine()) != null) {
      line++;
      str = soldatos.connection.MyDBCFunctions.prepareText(str, false);
      try{
      stmt.executeUpdate("UPDATE " +
          DmOptions.TMP_DB + "." +
          MainForm.options.toString(DmOptions.DB_PREFIX) + "_deletedupl " +
          "SET data = '" + str + "'" +
          "WHERE id = " + line);
      } catch(SQLException ex){
        System.out.println(str);
      }
    }
  }

  private void saveDeletedLines() throws SQLException, IOException {
    String sql = "SELECT badRows.* FROM " + DmOptions.TMP_DB +
        "." + MainForm.options.toString(DmOptions.DB_PREFIX) +
        "_deletedupl as badRows INNER JOIN (" +
        "SELECT * FROM " + DmOptions.TMP_DB + "." +
        MainForm.options.toString(DmOptions.DB_PREFIX) + "_deletedupl " +
        "GROUP BY " + duplFields + " HAVING COUNT(*) > 1) " +
        "AS goodRows ON " +
        joins +
        " AND goodRows.id <> badRows.id";
    rs = stmt.executeQuery(sql);
    tmpOut = new PrintWriter(new BufferedWriter(new FileWriter(m.getCurrentDirectory() + "/" + exportFilename)));
    while (rs.next()) {
      tmpOut.println(rs.getString("data"));
    }
    tmpOut.close();
  }

  private void deleteDupls() throws SQLException {
    if(deleteDupls){
    String sql = "DELETE " + DmOptions.TMP_DB + ".badRows.* FROM " + DmOptions.TMP_DB + "." +
        MainForm.options.toString(DmOptions.DB_PREFIX) +
        "_deletedupl as badRows INNER JOIN (" +
        "SELECT * FROM " + DmOptions.TMP_DB +
        "." + MainForm.options.toString(DmOptions.DB_PREFIX) + "_deletedupl " +
        "GROUP BY " + duplFields + " HAVING COUNT(*) > 1) " +
        "AS goodRows ON " +
        joins +
        " AND goodRows.id <> badRows.id";
    //System.out.println(sql);
    duplicatesFound = stmt.executeUpdate(sql);
    } else {
      String sql = "SELECT badRows.* FROM " + DmOptions.TMP_DB + "." +
        MainForm.options.toString(DmOptions.DB_PREFIX) +
        "_deletedupl as badRows INNER JOIN (" +
        "SELECT * FROM " + DmOptions.TMP_DB +
        "." + MainForm.options.toString(DmOptions.DB_PREFIX) + "_deletedupl " +
        "GROUP BY " + duplFields + " HAVING COUNT(*) > 1) " +
        "AS goodRows ON " +
        joins +
        " AND goodRows.id <> badRows.id";
      rs = stmt.executeQuery(sql);
      duplicatesFound = MyDBCFunctions.getResultSetNumberOfRows(rs);
    }
  }

  private void retrieveRecs() throws SQLException {
    String outline = "";
    int lines = 0;
    String sql = "SELECT * FROM " + DmOptions.TMP_DB +
        "." + MainForm.options.toString(DmOptions.DB_PREFIX) +
        "_deletedupl";
    rs = stmt.executeQuery(sql);
    m.setTextAreaText("");
    while (rs.next()) {
      lines++;
      outline = rs.getString("data").replaceAll("’", "A");
      m.appendToCurrentOutput("Lines read : " + lines);
      m.progressBar.setIndeterminate(false);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(outline + "\n");
      }
      output.println(outline);
    }

    m.setCustomers(lines);
    output.close();
    m.updateProgress(0);
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
  }
}
