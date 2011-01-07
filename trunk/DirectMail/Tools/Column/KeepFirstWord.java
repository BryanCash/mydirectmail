/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.functions.ArrayFunctions;

/**
 *
 * @author ssoldatos
 */
public class KeepFirstWord implements Runnable {

  private final int field;
  private final MainForm m;

  public KeepFirstWord(int field, MainForm m) {
    this.field = field;
    this.m = m;

  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      keep();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, "IO error", ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void keep() throws IOException {
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    int lines = 0;
    String currLineArr[];
    long start, end;

    start = System.currentTimeMillis();
    m.setOutput("Keeping first word");


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
      currLineArr[field] = processKeeping(currLineArr[field]);
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

    // init main parameters
    m.init(true);
    m.appendOutput("\nFirst word kept!!!");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
  }

  private String processKeeping(String word) {
    word = word.replaceAll("[_ -]", "-");


    return word.split("-", -1)[0];
  }
}
