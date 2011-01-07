/*
 * CheckControlChrs.java
 *
 * Created on 4 Αύγουστος 2007, 11:57 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.CheckFile;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ssoldatos
 */
public class CheckControlChrs implements Runnable {

  int replacements = 0;
  Pattern pat;
  Matcher match;
  private MainForm m;

  /** Creates a new instance of CheckControlChrs
   * @param m 
   */
  public CheckControlChrs(MainForm m) {
    this.m = m;
  }



  @Override
  public void run(){
    try {
      MainForm.glassPane.activate(null);
      check();
      MainForm.glassPane.deactivate();
    } catch (IOException ex) {
      Logger.getLogger(CheckControlChrs.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }
  private void check() throws IOException {

    BufferedReader in;
    PrintWriter output;
    int lines = 0, curLength;
    String str = null, buffer = "";
    String strOut = null;
    long start, end;
    char c;
    start = System.currentTimeMillis();
    m.setOutput("Checking the file for Control characters");

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);
    m.setTextAreaText("");
      while ((str = in.readLine()) != null) {
        lines++;
        m.appendToCurrentOutput("Lines read : " + lines);
        m.updateProgress(lines * 100 / m.getCustomers());
        if (lines % 50 == 0) {
          DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
        }
        strOut = "";
        for (int i = 0; i < str.length(); i++) {
          c = str.charAt(i);
          if ((Character.isISOControl((int) c) && c != '\t') ||
              ((c == '\u0023') && c != m.getDelimeter()) ||
              (c == '\u003B') && c != m.getDelimeter()) {
            buffer += "Dec: " + (int) c + "\n";
            c = ' ';
            replacements++;
          }
          strOut += c;
        }


        if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
          m.appendToSampleArea(strOut + "\n");
        }
        output.println(strOut);
      }
      m.appendOutput("\n" + replacements + " Control characters are replaced by spaces");
      m.appendOutput("\n" + "Ctrl characters:\n" + buffer);
      // close files
      in.close();
      output.close();
      //change to the new tmpfile name
      DmFunctions.swapFiles(m);
      // init main parameters
      m.init(true);
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
  }
}