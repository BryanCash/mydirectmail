/*
 * GetTheAddressing.java
 *
 * Created on 2 Αύγουστος 2007, 12:37 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.Addressing;

import DirectMail.Tools.Addressing.Forms.AddressingForm;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.Help.Functions.UpdateHeader;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.logging.Level;
import soldatos.connection.MyDBCConnection;
import soldatos.exceptions.AddressingException;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.Addressing;

/**
 *
 * @author ssoldatos
 */
public class VocativeAddressing implements Runnable {

  private int lines = 0;
  private MainForm m;
  private long start;
  private long end;
  private String comma;
  private boolean useSurname;
  private boolean useFirstname;
  private boolean addAddressingToUnknown;
  private boolean isCapitilized;
  private String companiesText;
  private String maleText;
  private String femaleText;
  private boolean addAccent;
  private String unKnownText;

  /** Creates a new instance of GetTheGender
   * @param m
   * @param options
   */
  public VocativeAddressing(MainForm m, AddressingForm options) throws SQLException {
    this.m = m;
    useSurname = options.checkbox_surname.isSelected();
    useFirstname = options.checkbox_firstname.isSelected();
    comma = options.checkbox_addComma.isSelected() ? "," : "";
    addAddressingToUnknown = options.checkbox_addAddressingToUnknown.isSelected();
    isCapitilized = options.checkbox_Capitilized.isSelected();
    companiesText = options.textfield_companies.getText();
    unKnownText = options.textfield_unknown.getText();
    maleText = options.textfield_male.getText();
    femaleText = options.textfield_female.getText();
    addAccent = options.checkbox_addAccent.isSelected();
    if (addAccent) {
      //Create statement
      MyDBCConnection.connect(
          MainForm.options.toString(DmOptions.HOST),
          MainForm.options.toString(DmOptions.DATABASE),
          MainForm.options.toString(DmOptions.DB_USER),
          MainForm.options.toString(DmOptions.DB_PASSWORD));
      m.stmt = MyDBCConnection.myConnection.createStatement();
    }
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      commitGetVocative();
      MainForm.glassPane.deactivate();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void commitGetVocative() throws IOException, InterruptedException, InvocationTargetException, AddressingException, SQLException {
    String str = "";
    String currLineArr[];
    String newOutLine;

    start = System.currentTimeMillis();
    m.setOutput("Getting the vocative addressing");
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);

    //InfoFrame.labelInfo.setText("Getting the gender");
    //Read the file and insert the rows
    m.appendOutput("\nReading the file and \ncreating the vocative addressing");
    m.setTextAreaText(null);

    while ((str = in.readLine()) != null) {
      String sName = "";
      String fName = "";
      int currGender = 0;
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
        //System.out.println(m.delimeter);
      }
      currLineArr = str.split("" + m.getDelimeter(), -1);
      newOutLine = "";
      currGender = Addressing.getGender(currLineArr[m.getGenderField()]);
      fName = currLineArr[m.getFirstnameField()];
      sName = currLineArr[m.getSurnameField()];
      currLineArr[m.getGenderField()] = currLineArr[m.getGenderField()] +
          m.getDelimeter() + getTheVocative(fName, sName, currGender);
      newOutLine = ArrayFunctions.join(currLineArr, m.getDelimeter());
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
      m.setTkField(m.getTkField() + 1);
      //TODO Low Change it to check if tkfield < namefield
    }

    // Add the addressing header

    EventQueue.invokeAndWait(new UpdateHeader("ADDRESSING", m.getGenderField() + 1, m));
    //Display the end gender and stop timers
    m.appendOutput("\nVocative Addressing OK!!!");
    // init main parameters
    m.init(true);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    MainForm.trayIcon.showInfoMessage("Getting Vocative Addressing",
        "Getting vocative addressing finished\n" +
        "Execution time : " + DmFunctions.execTime(start, end));


  }

  private String getTheVocative(String firstname, String surname, int gender) throws AddressingException, SQLException {
    String genSurname = "", genFirstname = "";
    if (gender == 0) {
      if (addAddressingToUnknown) {
        String addressing = "";
        addressing =
            ((useSurname ? Addressing.checkAccent(surname, addAccent, m.stmt, Addressing.SURNAME) : "") + " " +
            (useFirstname ? Addressing.checkAccent(firstname, addAccent, m.stmt, Addressing.FIRSTNAME) : "")).trim();
        addressing = Addressing.checkCapitilized(addressing,
            isCapitilized);
        return unKnownText + " " + addressing + comma;
      } else {
        return unKnownText + comma;
      }
    }
    if (gender == 3) {
      return companiesText + comma;
    }

    if (useSurname) {
      surname = addAccent ? Addressing.checkAccent(surname, addAccent, m.stmt, Addressing.SURNAME) : surname;
      genSurname = (gender == 1)
          ? Addressing.getVocativeAddressing(surname, Addressing.MALE_ADDRESSING, Addressing.SURNAME, m.stmt, addAccent)
          : Addressing.getVocativeAddressing(surname, Addressing.FEMALE_ADDRESSING, Addressing.SURNAME, m.stmt, addAccent);
    } else {
      genSurname = "";
    }
    if (useFirstname) {
      firstname = addAccent ? Addressing.checkAccent(firstname, addAccent, m.stmt, Addressing.FIRSTNAME) : firstname;
      genFirstname = (gender == 1)
          ? Addressing.getVocativeAddressing(firstname, Addressing.MALE_ADDRESSING, Addressing.FIRSTNAME, m.stmt, addAccent)
          : Addressing.getVocativeAddressing(firstname, Addressing.FEMALE_ADDRESSING, Addressing.FIRSTNAME, m.stmt, addAccent);
    } else {
      genFirstname = "";
    }
    String gen = "";
    if (gender == 1) {
      gen = maleText + " " +
          Addressing.checkCapitilized((genSurname + " " + genFirstname).trim(),
          isCapitilized) +
          comma;
    } else if (gender == 2) {
      gen = femaleText + " " +
          Addressing.checkCapitilized((genSurname + " " + genFirstname).trim(),
          isCapitilized) +
          comma;
    }

    return gen;
  }
}
