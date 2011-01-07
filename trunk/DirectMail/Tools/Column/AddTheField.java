/*
 * AddTheField.java
 *
 * Created on 8 Οκτώβριος 2007, 7:31 πμ
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

/**
 *
 * @author ssoldatos
 */
public class AddTheField implements Runnable {

  private boolean fieldBefore[];
  private String text;
  private MainForm m;
  private boolean addFirst;

  /** Creates a new instance of AddTheField
   * @param fieldBefore
   * @param text
   * @param m 
   */
  public AddTheField(boolean fieldBefore[], String text, MainForm m) {
    this.m = m;
    this.fieldBefore = fieldBefore;
    this.text = text;
  }

  /** Creates a new instance of AddTheField
   * @param intFieldBefore
   * @param text
   * @param m
   */
  public AddTheField(int intFieldBefore, String text, MainForm m) {
    boolean arr[] = new boolean[m.getFields()];
    for (int i = 0; i < m.getFields(); i++) {
      if (i == intFieldBefore) {
        arr[i] = true;
      } else {
        arr[i] = false;
      }
    }
    // An prosthetoume prin to proto pedio
    if (intFieldBefore == -1) {
      addFirst = true;
    }
    this.m = m;
    this.fieldBefore = arr;
    this.text = text;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      addField();
      MainForm.glassPane.deactivate();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void addField() throws InterruptedException, InvocationTargetException, IOException {
    int field = -1;
    int adds = 0;
    char currDel;
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    int lines = 0;
    String currLineArr[];
    long start, end;

    start = System.currentTimeMillis();
    m.setOutput("Adding Field");


    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

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
      if (addFirst) {
        newOutLine = text + m.getDelimeter() + str;
        adds++;
        field = -1;
      } else {
        for (int i = 0; i < m.getFields(); i++) {
          if (fieldBefore[i]) {
            // Check if this field is before the TK
            if (i < m.getTkField()) {
              adds++;
            }
            currLineArr[i] += m.getDelimeter() + text;
            field = i;
          }
          if (i < m.getFields() - 1) {
            newOutLine += currLineArr[i] + m.getDelimeter();
          } else {
            newOutLine += currLineArr[i];
          }
        }
      }
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    // close files
    in.close();
    output.close();

    // Add the new field header

    EventQueue.invokeAndWait(new UpdateHeader("" + (field), field + 1, m));




    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // move the tk field
    if (m.getTkField() != -1) {
      m.setTkField(m.getTkField() + adds / m.getCustomers());
    }
    // init main parameters
    m.init(true);
    m.appendOutput("\nField Added!!!");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
  }
}
