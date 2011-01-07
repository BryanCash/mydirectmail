/*
 * CombineTheFields.java
 *
 * Created on 18 Σεπτέμβριος 2007, 11:21 πμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.Help.Functions.UpdateHeader;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.regex.Matcher;

/**
 *
 * @author ssoldatos
 */
public class CombineTheFields implements Runnable {

  private boolean first[],  second[];
  private BufferedReader in;
  private PrintWriter output;
  private MainForm m;
  private boolean trim;

  /**
   * Combines two fields
   * @param first a boolean array where the only true field indicates the first field
   * to combine
   * @param second first a boolean array where the only true field indicates the second field
   * to combine
   * @param m The Mainform
   * @param trim If trim should be applied
   */
  public CombineTheFields(boolean first[], boolean second[], MainForm m, boolean trim) {
    this.m = m;
    this.first = first;
    this.second = second;
    this.trim = trim;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      commitCombineFields();
      MainForm.glassPane.deactivate();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void commitCombineFields() throws InterruptedException, IOException, InvocationTargetException {
    int deleteCol = 0;
    long start, end;
    int lines = 0, tkShift = 0;
    String currLineArr[];
    String str = "", newOutLine = "", currFirst = "", currSecond = "";

    start = System.currentTimeMillis();
    m.setOutput("Combining the fields");
    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);
    m.appendOutput("\nReading the file and \ncombining the fields");
    m.setTextAreaText(null);

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      //System.out.println(m.delimeter);
      currLineArr = str.split("" + m.getDelimeter(), -1);
      newOutLine = "";
      for (int i = 0; i < currLineArr.length; i++) {
        if (first[i]) {
          currFirst = currLineArr[i];
          newOutLine += "dummyDummydummy" + m.getDelimeter();
        } else if (second[i]) {
          deleteCol = i;
          currSecond = currLineArr[i];
          if (m.getTkField() != -1) {
            if (i < m.getTkField()) {
              tkShift = 1;
            }
          }
        } else {
          newOutLine += currLineArr[i] + m.getDelimeter();
        }
      }
      if (trim) {
        newOutLine = newOutLine.replaceFirst("dummyDummydummy", (Matcher.quoteReplacement(currFirst.trim()) + " " + Matcher.quoteReplacement(currSecond.trim())).trim());
      } else {
        newOutLine = newOutLine.replaceFirst("dummyDummydummy", ((Matcher.quoteReplacement(currFirst.trim()).equals("") ? "" : Matcher.quoteReplacement(currFirst).trim() + " ") + Matcher.quoteReplacement(currSecond)));
      }
      newOutLine = newOutLine.substring(0, newOutLine.length() - 1);
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    // close files
    in.close();
    output.close();


    EventQueue.invokeAndWait(new UpdateHeader(deleteCol, m));


    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // move the tk field
    if (m.getTkField() != -1) {
      m.setTkField(m.getTkField() - tkShift);
    //TODO Low Change it to check if tkfield < namefield
    }

    //Display the end gender and stop timers
    m.appendOutput("\nFields combined!!!");
    // init main parameters
    m.init(true);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }
}
