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

/**
 *
 * @author ssoldatos
 */
public class FillField implements Runnable {
  private MainForm m;
  private int filler;
  private int fieldToFill;
  private long start;
  private long end;
  private int fills = 0;

  /**
   *
   * @param m
   * @param selectedIndex
   * @param selectedColumn
   */
  public FillField(MainForm m, int selectedIndex, int selectedColumn) {
    this.m = m;
    this.filler = selectedIndex;
    this.fieldToFill = selectedColumn;
  }

  @Override
  public void run() {
    try {
      fill();
    } catch (IOException ex) {
      Logger.getLogger(FillField.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void fill() throws IOException {
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    int lines = 0;
    String currLineArr[];

    start = System.currentTimeMillis();
    m.setOutput("Filling Field " + m.getHeaderTitles().elementAt(fieldToFill) + " from " + m.getHeaderTitles().elementAt(filler));


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
      currLineArr = str.split("" + m.getDelimeter(), -1);
      newOutLine = "";
      if(currLineArr[fieldToFill].trim().equals("")){
        currLineArr[fieldToFill] = currLineArr[filler];
        fills++;
      }

      newOutLine   = ArrayFunctions.join(currLineArr, m.getDelimeter());
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    // close files
    in.close();
    output.close();

    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // move the tk field
//    if (m.getTkField() != -1) {
//      m.setTkField(m.getTkField() + adds / m.getCustomers());
//    }
    // init main parameters
    m.init(true);
    m.appendOutput("\n" + fills + " fields Filled!!!");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
  }

}
