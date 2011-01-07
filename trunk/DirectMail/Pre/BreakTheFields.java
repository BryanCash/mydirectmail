/*
 * BreakTheFields.java
 *
 * Created on 2 Αύγουστος 2007, 12:52 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Pre;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.*;
import DirectMail.Options.DmOptions;
import DirectMail.Help.Functions.ComputeMaxLength;
import soldatos.functions.SwingFunctions;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.StringFunctions;

/**
 * Breaking long fields to two shorter ones
 * @author ssoldatos
 */
public class BreakTheFields implements Runnable {

  /**
   * An boolean array of the file fields.
   * True if the field needs breaking , false otherwise
   */
  int fieldToBreak;
  private MainForm m;
  long start, end;
  private int longestField;
  private int pieces;

  /**
   * Commits the breaking of the field
   * @param fieldToBreak 
   * @param m 
   */
  public BreakTheFields(int fieldToBreak, MainForm m) {
    this.m = m;
    this.fieldToBreak = fieldToBreak;

  }

  @Override
  public void run() {
    try {
      start = System.currentTimeMillis();
      longestField = getLongestField();
      pieces = longestField / MainForm.options.toInt(DmOptions.MAX_FIELD_LENGTH) + 1;
      commitBreak();
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    }

  }

  /**
   * Reads the file line by line
   * Breaks each line to fields and breaks the fields marked for breaking to shorter
   * ones
   */
  private void commitBreak() throws InterruptedException, InvocationTargetException, IOException {
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    int lines = 0;
    String currLineArr[];
    m.setOutput("Breaking Field to " + pieces + " fields");
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
      //System.out.println(m.delimeter);
      currLineArr = str.split("" + m.getDelimeter(), -1);
      newOutLine = "";

      for (int i = 0; i < m.getFields(); i++) {
        if (i == fieldToBreak) {
          currLineArr[i] = breakField(currLineArr[i]);
        } else {
          currLineArr[i] = currLineArr[i];
        }
        if (i < m.getFields() - 1) {
          newOutLine += currLineArr[i] + m.getDelimeter();
        } else {
          newOutLine += currLineArr[i];
        }
      }

      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        //EventQueue.invokeLater (new UpdateSampleTable(m, newOutLine));
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    m.updateProgress(0);
    // close files
    in.close();
    output.close();

    // INSERT THE HEADERS AT THE NEW CREATED FIELDS


    EventQueue.invokeAndWait(new UpdateHeader("rename", "BROKEN " + fieldToBreak, fieldToBreak, m));
    for (int i = 0; i < pieces -1; i++) {
      EventQueue.invokeAndWait(new UpdateHeader("BROKEN " + fieldToBreak, fieldToBreak + i, m));
    }
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // move the tk field
    //if (m.getTkField() != -1) m.setTkField(m.getTkField()+breaks);
    // init main parameters
    m.init(true);
  }

  /**
   * The method that does the "breaking"
   * @param string The string to break
   * @return The broken string
   */
  private String breakField(String string) {
    String brokenField = "";
    brokenField =
        StringFunctions.shiftRight(
        ArrayFunctions.join(
        StringFunctions.breakLine(
        string, MainForm.options.toInt(DmOptions.MAX_FIELD_LENGTH), pieces), m.getDelimeter()),
        ""+m.getDelimeter(), MainForm.options.toInt(DmOptions.MAX_FIELD_LENGTH));

    return brokenField;
  }

  private int getLongestField() {
    ComputeMaxLength c = new ComputeMaxLength(m, fieldToBreak);
    c.run();
    return c.getMaxLength();
  }
}
