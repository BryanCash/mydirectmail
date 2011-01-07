/*
 * CheckFileFields.java
 *
 * Created on 3 Αύγουστος 2007, 1:41 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.CheckFile;

import DirectMail.Help.Components.Errors;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;

/**
 *
 * @author ssoldatos
 */
public class CheckFileFields implements Runnable {

  private MainForm m;
  private boolean errorOccured;
  private int errors = 0;
  private boolean shownMess;

  /** Creates a new instance of CheckFileFields
   * @param m 
   */
  public CheckFileFields(MainForm m) {
    this.m = m;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      check();
      MainForm.glassPane.deactivate();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  public void check() throws IOException {
    BufferedReader in;
    int lines = 0, curNumFields;
    int fields = m.getFields();
    String str;
    String currLineArr[];
    long start, end;
    String errorLines = "";
    start = System.currentTimeMillis();
    m.setOutput("Checking the file fields");

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      curNumFields = str.split("" + m.getDelimeter(), -1).length;
      if (fields == curNumFields) {
      } else {
        errorOccured = true;
        errors ++;
        if (errors < DmOptions._MAX_NUM_OF_ERRORS_) {
        errorLines += "Line " + lines + " has " + curNumFields + " fields instead of " + fields + "\n";
        m.appendOutput("\nFile field checking failed at line " + lines);
        } else {
          if (!shownMess) {
            m.appendOutput("\nToo many errors. Stop reporting");
            shownMess = true;
          }
        }
      }
    }
    if (errorOccured) {
      Errors.customError("Wrong number of fields", "There were errors in the number of fields\n" +
          "Check the output window");
     // m.appendOutput("\n" + errorLines);
    } else {
      m.appendOutput("\nFile fields are OK!!!");
    }
    m.updateProgress(0);

    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    in.close();
  }
}
