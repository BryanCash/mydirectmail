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
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class FormatDecimal implements Runnable {

  private final MainForm m;
  private final DecimalFormat decFormat;
  private long start;
  private long end;
  private int errors = 0;
  private String errorLog = "";

  public FormatDecimal(MainForm m, DecimalFormat decFormat) {
    this.m = m;
    this.decFormat = decFormat;

  }

  @Override
  public void run() {
    try {
      start = System.currentTimeMillis();
      m.setTextAreaText("");
      m.setOutput("Formating...");
      start();
      end = System.currentTimeMillis();
      m.appendOutput("\nField formated. There were " + errors + " erros in formating");
      if (errors > 0) {
        m.appendOutput("\nErrors in fields:\n");
        m.appendOutput(errorLog);
      }
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
      // Format
      String[] fields = str.split(String.valueOf(m.getDelimeter()), -1);
      fields[m.selectedColumn] = format(fields[m.selectedColumn]);
      outString = ArrayFunctions.join(fields, m.getDelimeter());

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

  private String format(String decimal) {
    String dec = decimal;
    if (dec.indexOf(",") > -1) {
      if (dec.indexOf(".") > -1) {
        dec = dec.replaceAll("\\.", "");
        dec = dec.replaceAll(",", ".");
      } else {
        if (StringFunctions.substringCount(",", decimal) > 1) {
          return decimal;
        }
        dec = dec.replaceAll(",", ".");
      }
    }

    try {
      double dStr = Double.parseDouble(dec);
      String newStr = decFormat.format(dStr).replaceAll(" ", "");
      return newStr;
    } catch (NumberFormatException ex) {
      errors++;
      if (errors < 100) {
        errorLog += "'" + decimal + "'" + "\n";
      }
      return decimal;
    }
  }
}
