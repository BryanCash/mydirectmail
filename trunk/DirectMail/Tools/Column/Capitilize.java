/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import soldatos.functions.SwingFunctions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class Capitilize implements Runnable {

  private MainForm m;
  private long start;
  private long end;
  private int col;

  public Capitilize(MainForm m, int col) {
    this.m = m;
    this.col = col;
  }

  @Override
  public void run() {
    try {
      start = System.currentTimeMillis();
      m.setTextAreaText("");
      m.setOutput("Capitilizing...");
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
      outString = capitilize(str);
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

  private String capitilize(String line) {
    String[] lineArr;
    if(col == -1){
    line = StringFunctions.capitilize(line,"el",false);
    } else {
      lineArr = line.split(String.valueOf(m.getDelimeter()), -1);
      for (int i = 0 ; i < lineArr.length ; i++){
        if(i == col){
          lineArr[i] = StringFunctions.capitilize(lineArr[i],"el",false);
        } else {

        }
      }
      line = ArrayFunctions.join(lineArr, m.getDelimeter());
    }
    return line;
  }
}
