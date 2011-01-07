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
public class CheckFileLength implements Runnable {

  private MainForm m;
  private boolean errorOccured;
  private int errors = 0;

  /** Creates a new instance of CheckFileFields
   * @param m 
   */
  public CheckFileLength(MainForm m) {
    this.m = m;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      checkLength();
      MainForm.glassPane.deactivate();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void checkLength() throws IOException {
    BufferedReader in;
    int lines = 0, curLength;
    int length = m.getLineLength();
    String str;
    String currLineArr[];
    long start, end;
    String errorLines = "";

    start = System.currentTimeMillis();
    m.setOutput("Checking the file lines length");

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    boolean shownMess = false;

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      curLength = str.length();
      if (length == curLength) {
      } else {
        if (errors < DmOptions._MAX_NUM_OF_ERRORS_) {
          errorLines += lines + " has a length of " + curLength + " instead of " + length + "\n";
          m.appendOutput("\nFile length checking failed at line " + lines);
        } else {
          if (!shownMess) {
            m.appendOutput("\nToo many errors. Stop reporting");
            shownMess = true;
          }
        }
        errorOccured = true;
        errors++;
        
      //end = System.currentTimeMillis();
      //m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
      //in.close();
      //return;
      }
    }
    m.updateProgress(0);
    if (errorOccured) {
      Errors.customError("Wrong line length", "There were " + errors + " error(s) in lines lengths");
      if (errors < DmOptions._MAX_NUM_OF_ERRORS_) {
        m.appendOutput("\n" + errorLines);
      }
    } else {
      m.appendOutput("\nFile length is OK!!!");
    }
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    in.close();
  }
}
