/*
 * GetTheGender.java
 *
 * Created on 2 Αύγουστος 2007, 12:37 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.Addressing;

import soldatos.connection.MyDBCConnection;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.Tools.Addressing.Forms.StrictGender;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.logging.Level;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.Addressing;

/**
 *
 * @author ssoldatos
 */
public class GetGender implements Runnable {

  private Statement stmt;
  private PreparedStatement pstmt;
  private int genderNotFound = 0;
  private int genderMale = 0;
  private int genderFemale = 0;
  private int genderCompany = 0;
  private int genderFound = 0;
  private int lines = 0;
  private MyDBCConnection mdbc;
  private int nameField = -1;
  private MainForm m;
  private int surnameField = -1;
  private int position = 0;
  private String[] companiesArr;
  private boolean strict;

  /** Creates a new instance of GetTheGender
   * @param m 
   */
  public GetGender(MainForm m) {
    this.m = m;
    nameField = m.getFirstnameField();
    surnameField = m.getSurnameField();
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
          MainForm.options.toString(DmOptions.DATABASE),
          MainForm.options.toString(DmOptions.DB_USER),
          MainForm.options.toString(DmOptions.DB_PASSWORD));
      stmt = MyDBCConnection.myConnection.createStatement();
      StrictGender s = new StrictGender();
      
      if (s.strict != -1) {
        strict = s.strict == 0 ? false : true ;
        commitGetGender();
      }
      MainForm.glassPane.deactivate();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  /**
   * Reads the file line by line
   * Breaks each line to fields and breaks the fields marked for breaking to shorter
   * ones
   */
  private void commitGetGender() throws InterruptedException, IOException, InvocationTargetException, SQLException {
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    String currLineArr[];
    String pSql;
    long start, end;

    start = System.currentTimeMillis();
    m.setOutput("Getting the gender");

    stmt = MyDBCConnection.myConnection.createStatement();

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

    //InfoFrame.labelInfo.setText("Getting the gender");
    //Read the file and insert the rows
    m.appendOutput("\nReading the file and \nfinding the gender");


    m.setTextAreaText(null);

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
        //System.out.println(m.delimeter);
      }
      currLineArr = str.split("" + m.getDelimeter(), -1);
      newOutLine = "";
      String surname = "", name = "";
      surname = currLineArr[surnameField];
      name = currLineArr[nameField];
      int gender = Addressing.getTheGender(name, surname, stmt, strict);
      if (gender == 0) {
        genderNotFound++;
      } else if (gender == 1) {
        genderMale++;
        genderFound++;
      } else if (gender == 2) {
        genderFemale++;
        genderFound++;
      } else if (gender == 3) {
        genderCompany++;
        genderFound++;
      }


      position = surnameField > nameField ? surnameField : nameField;
      currLineArr[position] = currLineArr[position] + m.getDelimeter() + gender;

      newOutLine += ArrayFunctions.join(currLineArr, m.getDelimeter());
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    // close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // move the tk field
    if (m.getTkField() != -1) {
      if (m.getTkField() > position) {
        m.setTkField(m.getTkField() + 1);
      }
    }

    // Add the gender header

    EventQueue.invokeAndWait(new UpdateHeader("GENDER", position + 1, m));
    m.setGenderField(nameField + 1);


    //Display the end gender and stop timers
    m.appendOutput("\nGender OK!!!");
    m.appendOutput("\nGenders found : " + genderFound);
    m.appendOutput("\nMales         : " + genderMale);
    m.appendOutput("\nFemales       : " + genderFemale);
    m.appendOutput("\nCompanies     : " + genderCompany);
    m.appendOutput("\n");
    m.appendOutput("\nGenders not found: " + genderNotFound);
    // init main parameters
    m.init(true);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    MainForm.trayIcon.showInfoMessage("Getting Gender",
        "Getting the gender finished\n"
        + "Gender found : " + genderFound + "\n"
        + "Males : " + genderMale + "\n"
        + "Females : " + genderFemale + "\n"
        + "Companies : " + genderCompany + "\n"
        + "Gender not found : " + genderNotFound + "\n"
        + "Execution time : " + DmFunctions.execTime(start, end));
  }
}
