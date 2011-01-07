/*
 * CheckTk.java
 *
 * Created on 7 Αύγουστος 2007, 9:27 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.CheckFile;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.Help.Functions.UpdateHeader;
import soldatos.connection.MyDBCConnection;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.logging.Level;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class CheckTk implements Runnable {

  private Statement stmt;
  private MyDBCConnection mdbc;
  long start, end;
  /**
   * The {@link BufferedReader} that reads the {@link File}
   */
  private BufferedReader in;
  private PrintWriter out;
  private int rightTk = 0;
  private int wrongTk;
  private MainForm m;

  /** Creates a new instance of CheckTk
   * @param m
   * @throws SQLException
   */
  public CheckTk(MainForm m) throws SQLException {
    this.m = m;
    wrongTk = m.getCustomers();
    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    out = DmFunctions.createPrinterWriter(m, in);



    MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
        MainForm.options.toString(DmOptions.DATABASE),
        MainForm.options.toString(DmOptions.DB_USER),
        MainForm.options.toString(DmOptions.DB_PASSWORD));
    stmt = MyDBCConnection.myConnection.createStatement();



  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      check();
      MainForm.glassPane.deactivate();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void check() throws InterruptedException, IOException, InvocationTargetException, SQLException {

    m.setTextAreaText(null);
    start = System.currentTimeMillis();
    // Make the connection
    m.setOutput("Connecting");

    m.appendOutput("\nChecking the TK : ");
    rightTk = checkTheTk();
    wrongTk = m.getCustomers() - rightTk;

    m.appendOutput("\nRight TK : " + rightTk);
    m.appendOutput("\nWrong TK : " + wrongTk);

    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
  }

  private int checkTheTk() throws InterruptedException, IOException, InvocationTargetException, SQLException {
    Object outline;
    int tkFound = 0;
    ResultSet rs = null;
    String sql, add = null;
    String currTk = "";
    String currAddress = "";
    String currCity = "";
    String[] strArr;
    int lines = 0;
    String str = null;
    boolean right = false;
    int intTk;
    boolean abroad = false, cyprus = false;
    while ((str = in.readLine()) != null) {
      lines++;

      m.appendToCurrentOutput("Lines read: " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      strArr = str.split("" + m.getDelimeter(), -1);
      for (int i = 0; i < m.getFields(); i++) {
        if (i == m.getTkField()) {
          strArr[i] = DmFunctions.prepareTK(strArr[i]);
          if (strArr[i].length() == 5) {
            currTk = strArr[i];
          } else {
            currTk = "";
          }
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
      if (!currTk.isEmpty() && !abroad) {
        sql = "SELECT tk FROM elta WHERE tk = '" + currTk + "'";

        rs = stmt.executeQuery(sql);


        if (rs.next()) {
          tkFound++;
          right = true;
        } else {
          right = false;
        }

      } else {
        right = false;
      }


      int addrL = currAddress.trim().length();
      int cityL = currCity.trim().length();

      if (abroad) {
        add = "0";
      } else if (cyprus) {
        add = "1";
      } else if (!right) {
        add = "2";
      } else {
        if (addrL == 0 || addrL > MainForm.options.toInt(DmOptions.MAX_FIELD_LENGTH) ||
            cityL == 0 || cityL > MainForm.options.toInt(DmOptions.MAX_FIELD_LENGTH) - 6) {
          add = "3";
        } else {
          intTk = Integer.parseInt(currTk.replaceAll(" ", ""));
          if (intTk > 10000 && intTk < 20000) {
            add = "4";
          } else if (intTk >= 50000 && intTk < 64000) {
            add = "5";
          } else {
            add = "6";
          }
        }
      }
      outline = StringFunctions.deleteDoubleSpaces(str) + m.getDelimeter() + add;
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(outline + "\n");
      }
      out.println(outline);
    }
    m.updateProgress(0);
    in.close();
    out.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // Add the right tk header

    EventQueue.invokeAndWait(new UpdateHeader("ELTA FLAG", m.getFields(), m));


    m.init(true);

    return tkFound;
  }
}
