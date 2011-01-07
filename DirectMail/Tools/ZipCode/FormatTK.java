/*
 * FormatTK.java
 *
 * Created on 14 Αύγουστος 2007, 11:49 πμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.ZipCode;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.*;
import DirectMail.Options.DmOptions;
import soldatos.functions.SwingFunctions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class FormatTK implements Runnable {

  private MainForm m;

  /** Creates a new instance of FormatTK
   * @param m 
   */
  public FormatTK(MainForm m) {
    this.m = m;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      format();
      MainForm.glassPane.deactivate();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  public void format() throws IOException {
    BufferedReader in;
    PrintWriter output;
    String str, curNewLine = "";
    String curLineArr[] = new String[m.getFields()];
    String tkField = "";
    int lines = 0;
    long start, end;

    start = System.currentTimeMillis();
    m.setOutput("Formating TK");
    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

    m.setTextAreaText("");

    while ((str = in.readLine()) != null) {
      lines++;
      curNewLine = "";
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }

      curLineArr = str.split("" + m.getDelimeter(), -1);
      for (int i = 0; i < curLineArr.length; i++) {
        if (i == m.getTkField()) {
          curLineArr[i] = formatTKField(curLineArr[i]);
        }
        if (i < curLineArr.length - 1) {
          curNewLine += curLineArr[i] + m.getDelimeter();
        } else {
          curNewLine += curLineArr[i];
        }
      }
      //append the new line to textarea
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(curNewLine + "\n");
      }
      output.println(curNewLine);
    }
    m.setFormatedTK(true);
    // close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
    m.appendOutput("\nTK was formated");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }

  private String formatTKField(String tk) {

    tk = tk.replaceAll(" ", "");
    tk = tk.replaceAll("-", "");
    //tk = tk.replaceAll(".","");
    tk = tk.replaceAll("_", "");
    tk = tk.replaceAll("@", "");
    tk = StringFunctions.padRight(tk, 10, " ");
    // NO @ ANYMORE
    //tk = "@" + tk;
    return tk;
  }
}
