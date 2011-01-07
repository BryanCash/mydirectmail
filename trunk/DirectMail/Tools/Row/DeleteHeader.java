/*
 * To change this template, choose Tools | Templates
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
public class DeleteHeader implements Runnable {

  private PrintWriter output;
  private BufferedReader in;
  private MainForm m;

  /**
   * 
   * @param m
   */
  public DeleteHeader(MainForm m) {
    this.m = m;
  }


  @Override
public void run(){
    try {
      delHeader();
    } catch (IOException ex) {
        MainForm.myLog.log(Level.WARNING, null, ex);
    }
}

  private void delHeader() throws IOException {
    long start, end;
    String str;
    int lines = 0;

    start = System.currentTimeMillis();
    m.setOutput("Deleting First Line");

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
      if (lines > 1) {
        if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
          m.appendToSampleArea(str + "\n");
        }
        output.println(str);
      }
    }
    // close files
    in.close();
    output.close();
    // Reduce customers
    m.setCustomers(m.getCustomers() - 1);
    // change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }
}
