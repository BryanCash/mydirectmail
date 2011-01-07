/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Options;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class UpdateSampleRows implements Runnable {

  private MainForm m;
  private int numRows = 0;
  private long start;
  private long end;
  private int startLine = 0;
  private int endLine = 0;

  /**
   * Updates the number of rows shown in the sample panel
   * @param m The mainform
   * @param samplesShown
   */
  public UpdateSampleRows(MainForm m, String samplesShown) {
    this.m = m;
    String[] arr;
    try {
      if (StringFunctions.isNumber(samplesShown)) {
        this.numRows = Integer.parseInt(samplesShown);
      } else {
        arr = samplesShown.split("-", -1);
        if (arr.length == 2) {
          if (Integer.parseInt(arr[0]) > Integer.parseInt(arr[1])) {
            startLine = Integer.parseInt(arr[1]);
            endLine = Integer.parseInt(arr[0]);
          } else {
            startLine = Integer.parseInt(arr[0]);
            endLine = Integer.parseInt(arr[1]);
          }
        }
      }
    } catch (NumberFormatException ex) {
      MainForm.myLog.log(Level.WARNING, "Wrong numbers for samples shown");
      MainForm.glassPane.deactivate();
    }
  }

  @Override
  public void run() {
    try {
      if((numRows ==0) && (startLine ==0 && endLine ==0)){
        return;
      }
      MainForm.glassPane.activate(null);
      start = System.currentTimeMillis();
      m.setOutput("Updating the number of sample lines");
      m.setTextAreaText("");
      updateTheRows();

      //DmFunctions.swapFiles(m);
      m.init(true);

      m.appendOutput("\nNumber of sample lines updated");
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }

  }

  private void updateTheRows() throws IOException {
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    //PrintWriter output = DmFunctions.createPrinterWriter(m, in);
    String str;
    int lines = 0;
    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      //append the new line to textarea
      if (lines <= numRows || ( lines >= startLine && lines <= endLine )) {
        m.appendToSampleArea(str + "\n");
      }
     // output.println(str);
    }
    // close files
    in.close();
  //  output.close();
  }
}
