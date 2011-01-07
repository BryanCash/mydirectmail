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
import java.util.logging.Logger;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.FileFunctions;

/**
 *
 * @author ssoldatos
 */
public class DeleteFromFile implements Runnable {

  private MainForm m;
  private int fieldToMatch;
  private String[] list;
  long start, end;
  private int excluded = 0;
  private int remained = 0;

  public DeleteFromFile(MainForm m, int fieldToMatch, String[] list) {
    this.m = m;
    this.fieldToMatch = fieldToMatch;
    this.list = list;

  }

  @Override
  public void run() {
    try {
      start = System.currentTimeMillis();
      delete();
      end = System.currentTimeMillis();
       m.appendOutput("\n"+excluded + " customers were excluded.");
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (IOException ex) {
      Logger.getLogger(DeleteFromFile.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void delete() throws IOException {
    String str;
    int lines = 0;
    start = System.currentTimeMillis();
    m.setOutput("Deleting From File");
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);
    PrintWriter excludes = FileFunctions.createOutputStream(m.getCurrentDirectory() + "/excludes_from_list.txt" , false);

    m.setTextAreaText(null);

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      String[] fields = str.split(""+m.getDelimeter(),-1);
      if (!ArrayFunctions.isInArray(fields[fieldToMatch],list)) {
        remained++;
        if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
          m.appendToSampleArea(str + "\n");
        }
        output.println(str);
      } else {
        excluded++;
        excludes.println(str);
      }
    }
    m.setCustomers(remained);
    // close files
    in.close();
    output.close();
    excludes.close();
    // Reduce customers
    m.setCustomers(m.getCustomers() - 1);
    // change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
  }
}
