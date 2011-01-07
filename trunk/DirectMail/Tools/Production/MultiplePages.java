/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Production;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.Tools.Production.Forms.MultiplePagesForm;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.FileFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class MultiplePages implements Runnable {

  private MainForm m;
  private long start;
  private long end;
  private int pages;
  private int[] duplex;

  public MultiplePages(MainForm m) {
    this.m = m;
  }

  @Override
  public void run() {
    MainForm.glassPane.activate(null);
    start = System.currentTimeMillis();
    m.appendOutput("\nStarting process");
    MultiplePagesForm f = new MultiplePagesForm(m);
    if (f.cancel) {
      m.appendOutput("\nAborting process");
      MainForm.glassPane.deactivate();
    } else {
      try {
        pages = f.pages;
        duplex = f.duplex;
        commit();
      } catch (IOException ex) {
        MainForm.myLog.log(Level.SEVERE, null, ex);
      } catch (InterruptedException ex) {
        MainForm.myLog.log(Level.SEVERE, null, ex);
      } catch (InvocationTargetException ex) {
        MainForm.myLog.log(Level.SEVERE, null, ex);
      } finally {
        MainForm.glassPane.deactivate();
      }
    }
  }

  private void commit() throws IOException, InterruptedException, InvocationTargetException {
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    String filename = m.getOpenedFile().substring(0, m.getOpenedFile().indexOf(".")) + "_" + m.getCustomers() + "_mult.arf";
    PrintWriter multiple = FileFunctions.createOutputStream(m.getCurrentDirectory() + "/" + filename, false);

    m.setTextAreaText("");
    String str;
    int lines = 0;

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      String[] fields = str.split(String.valueOf(m.getDelimeter()), -1);
      String counter;
      if (m.getCounterField() > -1) {
        counter = fields[m.getCounterField()].substring(0, 6);
      } else {
        counter = StringFunctions.padLeft(lines, 6, "0");
      }
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      for (int i = 0; i < pages; i++) {
        String newCounterField = "";
        if(m.getCounterField() > -1){
          fields[m.getCounterField()] = counter + (i + 1) + pages;
        } else {
          newCounterField = counter + (i + 1) + pages + m.getDelimeter();
        }
        String newLine = "PAGE:" + (i + 1) + m.getDelimeter() + "<FRNT>" + m.getDelimeter() + newCounterField +ArrayFunctions.join(fields, m.getDelimeter());
        multiple.println(newLine);
        if (ArrayFunctions.isInArray(duplex, (i + 1))) {
          newLine = "PAGE:" + (i + 1) + m.getDelimeter() + "<BACK>" + m.getDelimeter() + newCounterField + ArrayFunctions.join(fields, m.getDelimeter());
          multiple.println(newLine);
        }
      }
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(str + "\n");
      }

    }
    // close files
    in.close();
    multiple.close();

    m.init(true);

    //change to the new tmpfile name
    // init main parameters
    m.appendOutput("\nFile " + filename + " saved");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
  }
}
