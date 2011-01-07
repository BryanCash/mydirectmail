/*
 * NotEnvelopedIds.java
 *
 * Created on 25 Ιούλιος 2007, 10:32 πμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Post;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.*;
import DirectMail.Options.DmOptions;
import java.io.*;
import java.util.logging.Level;

/**
 * Creates a file with the id of the not enveloped customers.
 */
public class NotEnvelopedIds implements Runnable {

  /**
   * The {@link BufferedReader} that reads the {@link File}
   */
  BufferedReader in;
  /**
   * The {@link PrintWriter} that writes to the output {@link File}
   */
  PrintWriter output;
  private MainForm m;

  /**
   *  The constructor.<br>
   *  Reads the file and prints the IDs of the not enveloped customers (those that the character after<br>
   *  the <code>EOL!<-</code> is blank.
   * @param m 
   */
  public NotEnvelopedIds(MainForm m) {
    this.m = m;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      notEnvId();
      MainForm.glassPane.deactivate();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally{
      MainForm.glassPane.deactivate();
    }
  }

  private void notEnvId() throws IOException {
    long start, end;

    start = System.currentTimeMillis();
    m.setOutput("Getting the not enveloped customers IDs");
    String str;
    int pos, i = 0, lines = 0;
    m.setTextAreaText(null);

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);
    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      pos = str.indexOf("<-") + 2;
      if (str.charAt(pos) == ' ') {
        if (i <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
          m.appendToSampleArea(str.substring(0, 6) + "\n");
        }
        output.println(str.substring(0, 6));
        i++;
      }
    }
    m.setNotEnveloped(i);
    m.setCustomers(lines);
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
    m.appendOutput("\nNot enveloped Ids list generated");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }
}
