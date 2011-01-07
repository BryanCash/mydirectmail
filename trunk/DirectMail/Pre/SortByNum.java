/*
 * NumOfTK.java
 *
 * Created on 26 Ιούλιος 2007, 11:35 πμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Pre;

import DirectMail.Pre.Forms.SelectCustAndOrderCode;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.Options.DmOptions;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import soldatos.connection.MyDBCConnection;
import soldatos.functions.StringFunctions;

/**
 * Sorts a file by the total number of TK
 * @author ssoldatos
 */
public class SortByNum implements Runnable {

  private final MainForm m;
  private long start;
  private long end;
  private Statement stmt = null;
  private BufferedReader in;
  private PrintWriter output;
  private String custCode, orderCode;
  private boolean abort = false;

  public SortByNum(MainForm m) {
    this.m = m;
    SelectCustAndOrderCode codes = new SelectCustAndOrderCode(m.isSQLConnected);
    if (codes.isAborted) {
      abort = true;
    } else if (codes.isDefault) {
      custCode = "C_ID";
      orderCode = "ORDER";
    } else {
      custCode = codes.custCode;
      if (custCode.length() != 6 || !StringFunctions.isNumber(custCode.trim())) {
        soldatos.messages.Messages.customError(
            "Wrong customer code",
            "The customer code must be a 6digit number\n"
            + "The default C_ID String will be used");
        custCode = "C_ID";
      }
      orderCode = codes.orderCode;
      if (!StringFunctions.isNumber(orderCode)) {
        soldatos.messages.Messages.customError(
            "Wrong order code",
            "The order code must be a number\n"
            + "The default ORDER String will be used");
        orderCode = "ORD_CO";
      }
    }

  }

  @Override
  public void run() {
    if (abort) {
       MainForm.glassPane.deactivate();
    } else {
      try {
        //create the bufferred reader
        in = DmFunctions.createBufferedReader(m);
        // create the print writer
        output = DmFunctions.createPrinterWriter(m, in);
        start = System.currentTimeMillis();
        MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
            MainForm.options.toString(DmOptions.DATABASE),
            MainForm.options.toString(DmOptions.DB_USER),
            MainForm.options.toString(DmOptions.DB_PASSWORD));

        stmt = MyDBCConnection.myConnection.createStatement();
        MainForm.glassPane.activate(null);
        dropTmpTables();
        createTmpTables();
        fillTmpTable();
        sort();
        m.init(true);
        end = System.currentTimeMillis();
        m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
        MainForm.trayIcon.showInfoMessage("Sorting By Number of TK",
            " File is sorted by number of TK\n"
            + " Execution time : " + DmFunctions.execTime(start, end));
      } catch (InterruptedException ex) {
        MainForm.myLog.log(Level.SEVERE, null, ex);
      } catch (InvocationTargetException ex) {
        MainForm.myLog.log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
        MainForm.myLog.log(Level.SEVERE, null, ex);
      } catch (SQLException ex) {
        MainForm.myLog.log(Level.SEVERE, null, ex);
      } finally {
        MainForm.glassPane.deactivate();
      }
    }
  }

  private void sort() throws SQLException, InterruptedException, InvocationTargetException {
    String sql, outline, select = "";
    ResultSet rs;
    int lines = 0;

    sql = "SELECT id, data, s.tk, j.tot FROM " + DmOptions.TMP_DB + "."
        + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp s "
        + "LEFT JOIN (SELECT tk, count(tk) as tot FROM " + DmOptions.TMP_DB + "."
        + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp GROUP by tk) j"
        + " ON s.tk = j.tk ORDER BY j.tot, j.tk";
    //System.out.println(sql);
    m.setTextAreaText("");
    m.IndeterminateProgress(true);
    m.setProgressText("Sorting");

    rs = stmt.executeQuery(sql);
    int flag = -1;
    String prevTk = "%";
    while (rs.next()) {
      outline = "";
      lines++;
      String data = rs.getString(2).replaceAll("’", "A");
      String tk = rs.getString(3).trim();
      // num of tk
      //outline = rs.getString(2).replaceAll("’", "A") + m.getDelimeter() +  StringFunctions.padLeft(rs.getInt(4), 6, "0");
      // flag
      if (!tk.equals(prevTk)) {
        flag = (flag + 1) % 10;
        prevTk = tk;
      } else {
      }
      //outline = data + m.getDelimeter() +  flag;
      outline = flag + "" + m.getDelimeter()
          + "REP!<-                    !" + m.getDelimeter()
          + custCode + m.getDelimeter()
          + orderCode + m.getDelimeter()
          + "0000000000" + m.getDelimeter()
          + data;

      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(outline + "\n");
      }
      output.println(outline);
    }
    m.IndeterminateProgress(false);
    //m.setCustomers(lines);
    output.close();

    // Add the num of tk header
    EventQueue.invokeAndWait(new UpdateHeader("ELTA FLAG", 0, m));
    EventQueue.invokeAndWait(new UpdateHeader("REP", 1, m));
    EventQueue.invokeAndWait(new UpdateHeader("CUST ID", 2, m));
    EventQueue.invokeAndWait(new UpdateHeader("ORDER CODE", 3, m));
    EventQueue.invokeAndWait(new UpdateHeader("FLAGS", 4, m));

    //change to the new tmpfile name
    DmFunctions.swapFiles(m);


  }

  private void dropTmpTables() throws SQLException {
    String sql = "DROP TABLE IF EXISTS  " + DmOptions.TMP_DB + "."
        + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp";
    stmt.executeUpdate(sql);
  }

  private void createTmpTables() throws SQLException {
    String sql = "";
    sql = "CREATE TABLE IF NOT EXISTS " + DmOptions.TMP_DB + "."
        + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp("
        + "`id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,"
        + "`data` VARCHAR(1000) NOT NULL,"
        + "`tk` VARCHAR(45) NOT NULL,"
        + " PRIMARY KEY (`id`), INDEX ind(`tk`)) ENGINE=MEMORY CHARACTER SET " + m.getCharacterSet()
        + " COLLATE " + m.getCollation();

    stmt.executeUpdate(sql);

  }

  private void fillTmpTable() throws IOException, SQLException {
    int j = 0;
    String str, sql = "", insertStmt = "";
    int lines = 0;

    m.appendOutput("\nUpdating DB");
    String tk = "";
    PrintWriter tmpOut;
    String isRight;
    int intTk;
    String currAddress = "";
    String currCity = "";
    boolean abroad = false;
    boolean cyprus = false;


    tmpOut = new PrintWriter(new BufferedWriter(new FileWriter(DmOptions._JAR_DIR_+"data.tmp")));
    while ((str = in.readLine()) != null) {
      lines++;
      isRight = "-1";
      m.appendToCurrentOutput("Lines read: " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      String[] strArr = str.split("" + m.getDelimeter(), -1);
      insertStmt = "";
      for (int i = 0; i < strArr.length; i++) {
        tk = DmFunctions.prepareTK(strArr[m.getTkField()]);
      }
      str = soldatos.connection.MyDBCFunctions.prepareText(str, false);
      tmpOut.println(lines + "<|>" + str + "<|>" + tk);
    }
    tmpOut.close();
    sql = "LOAD DATA LOCAL INFILE '" + DmOptions._JAR_DIR_ + "data.tmp' INTO TABLE " + DmOptions.TMP_DB + "."
        + MainForm.options.toString(DmOptions.DB_PREFIX)
        + "_tmp CHARACTER SET "
        + m.getCharacterSet() + " FIELDS TERMINATED BY '<|>' LINES TERMINATED BY '\n'";
    stmt.executeUpdate(sql);
    File f = new File(DmOptions._JAR_DIR_+"data.tmp");
    f.delete();

    m.updateProgress(0);
    in.close();


  }
}

