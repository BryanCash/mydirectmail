/*
 * MultiplyLines.java
 *
 * Created on 10 Οκτώβριος 2007, 7:28 πμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.Row;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

/**
 *
 * @author ssoldatos
 */
public class MultiplyLines implements Runnable {

  int multiplier = 1;
  private MainForm m;

  /** Creates a new instance of MultiplyLines
   * @param multiplier
   * @param m 
   */
  public MultiplyLines(int multiplier, MainForm m) {
    this.m = m;
    this.multiplier = multiplier;
  }

  @Override
  public void run(){
    try {
      mult();
    } catch (IOException ex) {
        MainForm.myLog.log(Level.WARNING, null, ex);
    }
  }

  private void mult() throws IOException {
    BufferedReader in;
    PrintWriter output;
    String str = "";
    int lines = 0, horCounter = 0, vertCounter = 0, mainCounter = 1, newLines = 0;
    long start, end;
    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);


    start = System.currentTimeMillis();
    m.setOutput("Multiply lines by " + multiplier);


    m.setTextAreaText("");

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      for (int i = 0; i < multiplier; i++) {
        output.println(str);
        if ((lines - 1) * multiplier + i <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
          m.appendToSampleArea(str + "\n");
        }
      }
    }
    // close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    // change the number of lines to the new one
    m.init(true);
    m.appendOutput("\nLines Are multiplied by " + multiplier);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }
}
