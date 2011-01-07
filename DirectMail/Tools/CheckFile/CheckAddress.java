/*
 * CheckAddress.java
 *
 * Created on 18 Σεπτέμβριος 2007, 7:07 πμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.CheckFile;

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
import java.sql.*;
import java.util.logging.Level;
import soldatos.functions.Elta;
import soldatos.functions.FileFunctions;

/**
 *
 * @author ssoldatos
 */
public class CheckAddress implements Runnable {

  boolean fieldsToCheck[], export;
  int errors[];
  int checks;
  private String sql;
  private Statement stmt;
  private MyDBCConnection conn;
  String exportFilename;
  private MainForm m;
  private MyDBCConnection mdbc;
  private PrintWriter report;
  private PrintWriter wrongOut;
  private boolean delete;
  private int errorsTk = 0;
  private int wrongLines = 0;
  private final boolean checkAgainstDB;

  /**
   * 
   * @param fieldsToCheck
   * @param checks
   * @param export
   * @param exportFilename
   * @param m
   * @param delete 
   * @throws SQLException
   */
  public CheckAddress(boolean fieldsToCheck[], int checks,
      boolean export, String exportFilename, MainForm m, boolean delete, boolean checkAgainstDB) throws SQLException {
    this.m = m;
    this.fieldsToCheck = fieldsToCheck;
    this.checks = checks;
    this.export = export;
    this.exportFilename = exportFilename;
    this.errors = new int[m.getFields()];
    this.delete = delete;
    this.checkAgainstDB = checkAgainstDB;

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
      commitCheck();
      writeReport();
      MainForm.glassPane.deactivate();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void commitCheck() throws IOException, SQLException {
    BufferedReader in;
    PrintWriter output;
    String str;
    int lines = 0;
    String currLineArr[];
    long start, end;
    boolean isWrongLine = false;

    start = System.currentTimeMillis();
    m.setOutput("Checking Fields");


    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

    m.setTextAreaText(null);

    if (export) {
      wrongOut = FileFunctions.createOutputStream(m.getCurrentDirectory() + "/" + exportFilename, false);
    }
    report = FileFunctions.createOutputStream(m.getCurrentDirectory() + "/report_check_addresses.txt", false);
    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      isWrongLine = false;
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      //System.out.println(m.delimeter);
      currLineArr = str.split("" + m.getDelimeter(), -1);
      for (int i = 0; i < m.getFields(); i++) {
        if (fieldsToCheck[i]) {
          String curField = currLineArr[i].trim();
          if (curField.equals("") ||
              (i == m.getTkField() &&
              (checkAgainstDB ? !Elta.isTK(curField, stmt) : !Elta.isTK(curField)))) {
            if (curField.equals("")) {
              errors[i]++;
            } else {
              errorsTk++;
            }
            isWrongLine = true;
          }
        }
      }
      if (isWrongLine) {
        wrongLines++;
        if (export) {
          wrongOut.println(str);
        }
      }
      if (isWrongLine && delete) {
      } else {
        if (lines - wrongLines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
          m.appendToSampleArea(str + "\n");
        }
        output.println(str);
      }

    }
    // close files
    in.close();
    output.close();
    if (delete) {
      m.setCustomers(lines - wrongLines);
    }
    if (export) {
      wrongOut.close();
      String oldFilename = exportFilename;
      String newFilename = oldFilename.replace(".txt", "_" + wrongLines + ".txt");
      File tmpold = new File(m.getCurrentDirectory() + "//" + oldFilename);
      File tmpnew = new File(m.getCurrentDirectory() + "//" + newFilename);
      tmpold.renameTo(tmpnew);
    }

    m.updateProgress(0);

    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init
    m.init(true);
    String rep = writeReport();
    m.appendOutput("\n" + rep);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
  }

  private String writeReport() {
    String rep = "";
    rep = checks + ((checks == 1) ? " πεδίο" : " πεδία") + " ελέγχθησαν και " +
        +wrongLines + " εγγραφές με ελλειπή στοιχεία βρέθηκαν\n\n";
    for (int i = 0; i < m.getFields(); i++) {

      if (i == m.getTkField()) {
        rep += "Κενά πεδία στον ΤΚ : " + errors[i] + "\n";
        rep += "Λάθος πεδία στον ΤΚ : " + errorsTk + "\n";
      } else {
        if (errors[i] > 0) {
          rep += "Κενά στο " + m.getHeaderTitles().get(i) + " = " + errors[i] + "\n";
        }
      }
    }
    report.print(rep);
    report.close();
    return rep;
  }
}
