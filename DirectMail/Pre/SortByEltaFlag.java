/*
 * NumOfTK.java
 *
 * Created on 26 Ιούλιος 2007, 11:35 πμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Pre;

import soldatos.connection.MyDBCConnection;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.Pre.Forms.SelectCustAndOrderCode;
import DirectMail.Options.DmOptions;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Level;
import soldatos.functions.StringFunctions;

/**
 * Sorts a file by the total number of TK
 * @author ssoldatos
 */
public class SortByEltaFlag implements Runnable {

  /**
   * The {@link BufferedReader} that reads the {@link File}
   */
  private BufferedReader in;
  /**
   * The {@link PrintWriter} that writes to the output {@link File}
   */
  private PrintWriter output;
  /**
   * The number of fields in each line
   */
  private int fields;
  /**
   * The Batch used for sending all the insert queries at once
   */
  private boolean batch;
  long start, end;
  private Statement stmt;
  private MyDBCConnection mdbc;
  private MainForm m;
  private int hasTKRight;
  private String custCode, orderCode;
  private boolean abort;

  /**
   * The constructor that creates the reader and the writer
   * @param fields The number of the fields
   * @param m
   * @throws SQLException
   */
  public SortByEltaFlag(int fields, MainForm m) throws SQLException {
    this.m = m;
    this.fields = fields;
    this.hasTKRight = m.getIsTKRightField();


    SelectCustAndOrderCode codes = new SelectCustAndOrderCode(m.isSQLConnected);
    if (codes.isAborted) {
      abort = true;
      return;
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
    MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
        MainForm.options.toString(DmOptions.DATABASE),
        MainForm.options.toString(DmOptions.DB_USER),
        MainForm.options.toString(DmOptions.DB_PASSWORD));
    stmt = MyDBCConnection.myConnection.createStatement();

    m.setTextAreaText(null);


  }

  @Override
  public void run() {
    if (abort) {
      MainForm.glassPane.deactivate();
    } else {
      try {
        MainForm.glassPane.activate(null);
        //create the bufferred reader
        in = DmFunctions.createBufferedReader(m);
        // create the print writer
        output = DmFunctions.createPrinterWriter(m, in);
        eltaFlag();
        MainForm.glassPane.deactivate();
      } catch (Exception ex) {
        MainForm.myLog.log(Level.WARNING, null, ex);
      } finally {
        MainForm.glassPane.deactivate();
      }
    }
  }

  private void eltaFlag() throws SQLException, IOException, InterruptedException, InvocationTargetException {

    start = System.currentTimeMillis();

    // Droping the tables
    m.appendOutput("\nDroping the tmp tables if any");
    dropTmpTables();

    //Create the new empty table
    m.appendOutput("\nCreating the tables");
    createTmpTables();

    //Read the file and insert the rows
    fillTmpTable();
    m.appendOutput("\nDB Updated!!!");

    //Query by num of tk
    runQuery();
    m.appendOutput("\nQuery Run!!!");

    m.init(true);

    // Droping the tables
    m.appendOutput("\nDroping the tables");
    //dropTmpTables();

    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    MainForm.trayIcon.showInfoMessage("Sorting By Elta flag",
        " File is sorted by elta flag\n"
        + " Execution time : " + DmFunctions.execTime(start, end));
    return;
  }

  /**
   * Creates the temporary tables
   */
  private void createTmpTables() throws SQLException {
    String sql = "";
    sql = "CREATE TABLE IF NOT EXISTS " + DmOptions.TMP_DB + "." + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp("
        + "`id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,"
        + "`data` VARCHAR(1000) NOT NULL,"
        + "`tk` VARCHAR(45) NOT NULL,"
        + "`isright` INTEGER(1) NOT NULL,"
        + " PRIMARY KEY (`id`), INDEX ind(`tk`)) ENGINE=MEMORY CHARACTER SET " + m.getCharacterSet()
        + " COLLATE " + m.getCollation();

    stmt.executeUpdate(sql);

  }

  /**
   * Fills the temporary tables fron lines from the file
   */
  private void fillTmpTable() throws IOException, SQLException {
    int j = 0;
    String str, sql = "", insertStmt = "";
    String strArr[] = new String[fields];
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
      strArr = str.split("" + m.getDelimeter(), -1);
      insertStmt = "";
      for (int i = 0; i < strArr.length; i++) {
        strArr[i] = strArr[i].trim();
        if (i == m.getTkField()) {
          tk = DmFunctions.prepareTK(strArr[i]);
        } else if (i == m.getIsTKRightField()) {
          isRight = strArr[i];
        } else if (i == m.getAddressField()) {
          currAddress = StringFunctions.deleteDoubleSpaces(strArr[i]);
        } else if (i == m.getCityField()) {
          currCity = StringFunctions.deleteDoubleSpaces(strArr[i]);
        } else if (i == m.getAbroadField()) {
          if (strArr[i].trim().equals("")) {
            abroad = false;
            cyprus = false;
          } else if (StringFunctions.isAbroad(strArr[i].trim())) {
            abroad = true;
            cyprus = false;
          } else if (StringFunctions.isCyprus(strArr[i].trim())) {
            abroad = false;
            cyprus = true;
          } else {
            abroad = false;
            cyprus = false;
          }
        }
      }
      if (hasTKRight == -1) {
        if (abroad) {
          isRight = "0";
        } else if (cyprus) {
          isRight = "1";
        } else {
          int addrL = currAddress.trim().length();
          int cityL = currCity.trim().length();
          try {
            if ((m.getAddressField() > - 1 && m.getCityField() > -1)
                && (addrL == 0 || addrL > MainForm.options.toInt(DmOptions.MAX_FIELD_LENGTH)
                || cityL == 0 || cityL > MainForm.options.toInt(DmOptions.MAX_FIELD_LENGTH) - 6)) {
              isRight = "3";
            } else if (StringFunctions.isNumber(tk.replaceAll(" ", ""))) {
              intTk = Integer.parseInt(tk.replaceAll(" ", ""));
              if (intTk >= 10000 && intTk < 20000) {
                isRight = "4";
              } else if (intTk >= 50000 && intTk < 64000) {
                isRight = "5";
              } else if (intTk < 10000 || intTk > 85900) {
                isRight = "2";
              } else {
                isRight = "6";
              }
            } else {
              isRight = "2";
            }
          } catch (NumberFormatException ex) {
            isRight = "2";
          }
        }

        m.setIsTKRightField(m.getFields());
      }
      str = soldatos.connection.MyDBCFunctions.prepareText(str, false);
      if (hasTKRight == -1) {
        tmpOut.println(lines + "<|>"
            + isRight + m.getDelimeter()
            + "REP!<-                    !" + m.getDelimeter()
            + custCode + m.getDelimeter()
            + orderCode + m.getDelimeter()
            + "0000000000" + m.getDelimeter()
            + str + "<|>" + tk + "<|>" + isRight);
      } else {
        tmpOut.println(lines + "<|>" + str + "<|>" + tk + "<|>" + isRight);
      }

    }
    tmpOut.close();
    sql = "LOAD DATA LOCAL INFILE '" + DmOptions._JAR_DIR_+"data.tmp' INTO TABLE " + DmOptions.TMP_DB + "."
        + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp CHARACTER SET "
        + m.getCharacterSet() + " FIELDS TERMINATED BY '<|>' LINES TERMINATED BY '\n'";
    stmt.executeUpdate(sql);
    File f = new File(DmOptions._JAR_DIR_+"data.tmp");
    f.delete();
    m.updateProgress(0);
    in.close();


  }

  /**
   * Drops the temporary tables after the work is done
   */
  private void dropTmpTables() throws SQLException {


    String sql = "DROP TABLE IF EXISTS  "
        + DmOptions.TMP_DB + "." + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp";
    stmt.executeUpdate(sql);
    sql = "DROP TABLE IF EXISTS  " + DmOptions.TMP_DB + "."
        + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmpnumoftk";
    stmt.executeUpdate(sql);

  }

  /**
   * Runs the final query on the joined tables
   */
  private void runQuery() throws InterruptedException, InvocationTargetException, SQLException {
    String sql, outline, select = "";
    ResultSet rs;
    int lines = 0;


    sql = "SELECT data FROM  " + DmOptions.TMP_DB + "."
        + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp ORDER BY  "
        + DmOptions.TMP_DB + "." + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp.isright, "
        + DmOptions.TMP_DB + "." + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp.tk ASC";
    //System.out.println(sql);
    m.setTextAreaText("");
    m.IndeterminateProgress(true);
    m.setProgressText("Sorting");

    rs = stmt.executeQuery(sql);
    while (rs.next()) {
      outline = "";
      lines++;
      outline =
          // StringFunctions.padLeft("" + lines, 6, "0") + "11" + m.getDelimeter() +
          rs.getString(1).replaceAll("’", "A");

      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(outline + "\n");
      }
      output.println(outline);
    }
    m.IndeterminateProgress(false);
    //m.setCustomers(lines);
    output.close();

    // Add the num of tk header

    // EventQueue.invokeAndWait(new UpdateHeader("COUNTER", 0, m));
    EventQueue.invokeAndWait(new UpdateHeader("ELTA FLAG", 0, m));
    EventQueue.invokeAndWait(new UpdateHeader("REP", 1, m));
    EventQueue.invokeAndWait(new UpdateHeader("CUST ID", 2, m));
    EventQueue.invokeAndWait(new UpdateHeader("ORDER CODE", 3, m));
    EventQueue.invokeAndWait(new UpdateHeader("FLAGS", 4, m));

    //change to the new tmpfile name
    DmFunctions.swapFiles(m);

  }

  /**
   * Runs a query to compute the totals of each TK
   */
  private void runNumOfTkQuery() throws SQLException {
    String sql, sql2;
    String rTk, rNumOfTk;
    Statement stmt2 = null;
    sql = "INSERT INTO  " + DmOptions.TMP_DB + "."
        + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmpnumoftk (tk, numoftk)"
        + " SELECT tk AS tk, COUNT(tk) "
        + "AS numoftk FROM  " + DmOptions.TMP_DB + "." + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmp GROUP BY tk";
    ResultSet rs;

    stmt.executeUpdate(sql);


  }
}
