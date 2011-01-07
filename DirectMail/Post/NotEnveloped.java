/*
 * NotEnveloped.java
 *
 * Created on 23 Ιούλιος 2007, 5:01 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Post;

import DirectMail.Help.Components.Errors;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.*;
import DirectMail.Options.DmOptions;
import java.io.*;
import java.util.logging.Level;

/**
 * Creates a file with the not enveloped customers.
 */
public class NotEnveloped implements Runnable {

  /**
   * The {@link BufferedReader} which reads the {@link File}
   */
  BufferedReader in;
  /**
   * The {@link PrintWriter} that writes to the output {@link File}
   */
  PrintWriter output;
  /**
   * The position of the enveloped or not flag
   */
  private MainForm m;

  /**
   *  The constructor.<br>
   *  Reads the file and deletes the enveloped customers (those that the character after<br>
   *  the <code>EOL!<-</code> is not blank.
   * @param m 
   */
  public NotEnveloped(MainForm m) {
    this.m = m;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      notEnv();
      MainForm.glassPane.deactivate();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally{
      MainForm.glassPane.deactivate();
    }
  }

  private void notEnv() throws IOException {
    String firstLine;
    long start, end;

    start = System.currentTimeMillis();
    String str;
    int position, i = 0, lines = 0;

    firstLine = m.getFirstLine();
    position = firstLine.indexOf("<-") + 2;
    if (position < 2) {
      Errors.customError("Wroing File!!!", "This is not a report file.\nAborting...");
    } else {
      m.setTextAreaText("");
      m.setOutput("Getting the not enveloped customers");

      //create the bufferred reader
      in = DmFunctions.createBufferedReader(m);
      // create the print writer
      output = DmFunctions.createPrinterWriter(m, in);
      while ((str = in.readLine()) != null) {
        lines++;
        m.appendToCurrentOutput("Lines read : " + lines);
        if (str.charAt(position) == ' ') {
          if (i <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
            m.appendToSampleArea(str + "\n");
          }
          output.println(str);
          i++;
        }
      }
      m.setNotEnveloped(i);
      m.setCustomers(lines);
      in.close();
      output.close();
      //change to the new tmpfile name
      DmFunctions.swapFiles(m);
      //init main parameters
      m.init(true);
      m.appendOutput("\nNot enveloped list generated");
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

    }
  }
}
