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
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class TrimFields implements Runnable {

  private final MainForm m;
  private long start;
  private long end;

  public TrimFields(MainForm m) {
    this.m = m;

  }

  @Override
  public void run() {
    try {
      start = System.currentTimeMillis();
      m.setTextAreaText("");
      m.setOutput("Trimming fields");
      start();
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
  }

  private void start() throws IOException {
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);
    String str;
    int lines = 0;
    String outString = "";
    String[] curLineArr;


    while ((str = in.readLine()) != null) {
      lines++;
      outString = "";
      m.appendToCurrentOutput("Lines read: " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      // split fields
      curLineArr = str.split("" + m.getDelimeter(), -1);
      for (int i = 0; i < curLineArr.length; i++) {
        curLineArr[i] = curLineArr[i].trim();
        curLineArr[i] = StringFunctions.deleteDoubleSpaces(curLineArr[i]);
        
      }
      outString = ArrayFunctions.join(curLineArr, m.getDelimeter());
      //append the new line to textarea
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(outString + "\n");
      }
      output.println(outString);
    }
    // Close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);

  }
}
