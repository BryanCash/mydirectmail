/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Production;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.functions.FileFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class MakeSplitDouble implements Runnable {

  private final int numOfLines;
  private final MainForm m;
  private long start;
  private long end;
  private ArrayList<String> customers = new ArrayList<String>();
  private int offset;

  public MakeSplitDouble(int numOfLines, MainForm m) {
    this.numOfLines = numOfLines;
    this.m = m;
    offset = numOfLines%2==0 ? numOfLines/2 : (numOfLines/2) + 1;

  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      start = System.currentTimeMillis();
      m.appendOutput("\nStarting process");
      commit();
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, "io exception", ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void commit() throws IOException {
    int startCounter = 0;
    int endCounter = 0;
    int lines = 0;
    BufferedReader in = DmFunctions.createBufferedReader(m);
    String str;
    String outline;
    String originalFile = FileFunctions.getBaseFilename(
        new File(m.getCurrentDirectory() + "/" + m.getOpenedFile()));
    PrintWriter output = null;

    while ((str = in.readLine()) != null) {
      lines++;
      outline = "";
      m.appendToCurrentOutput("Lines read: " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      customers.add(str);
    }
    in.close();
    if (customers.size() % 2 != 0) {
      customers.add("");
    }
    String filename;
    int max = customers.size();
    for (int curCounter =0; curCounter < max; curCounter++) {
      if (curCounter % numOfLines == 0) {
        startCounter = curCounter;
        if(curCounter != 0) {
          output.close();
        }
        if (customers.size() - curCounter - numOfLines >= 0) {
          endCounter = startCounter + numOfLines;
        } else {
          endCounter = customers.size();
        }
        offset = (endCounter-startCounter) / 2;
        filename = originalFile + "_" + StringFunctions.padLeft((startCounter + 1),6,"0") +
            "_" + StringFunctions.padLeft(endCounter,6,"0") + ".arf";
        output = FileFunctions.createOutputStream(m.getCurrentDirectory() +"/" +filename, false);
        System.out.println("Created file: " + filename);
      }
      if(curCounter < (endCounter-offset)){
      output.print(customers.get(curCounter) + m.getDelimeter() + customers.get(curCounter + offset)+"\r\n");
      }
    }
    output.close();
    m.updateProgress(0);
    //change to the new tmpfile name
    //DmFunctions.swapFiles(m);
  }
}
