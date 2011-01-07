/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DirectMail.Tools.Column;

import DirectMail.Options.DmOptions;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Help.Functions.UpdateHeader;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ssoldatos
 */
public class DuplicateField implements Runnable{
  private int column;
  private MainForm m;
  long start, end;

  public DuplicateField(int selectedColumn, MainForm m) {
    this.column = selectedColumn;
    this.m = m;
  }

  @Override
  public void run() {
    try {
      duplicate();
    } catch (IOException ex) {
      Logger.getLogger(DuplicateField.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(DuplicateField.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      Logger.getLogger(DuplicateField.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void duplicate() throws IOException, InterruptedException, InvocationTargetException {
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    int lines = 0;
    String currLineArr[];

    start = System.currentTimeMillis();
    m.setOutput("Duplicating Field");


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
      
        for (int i = 0; i < m.getFields(); i++) {
          if (i == column) {
            currLineArr[i] += m.getDelimeter() + currLineArr[i];
          }
          if (i < m.getFields() - 1) {
            newOutLine += currLineArr[i] + m.getDelimeter();
          } else {
            newOutLine += currLineArr[i];
          }
        }
      
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    // close files
    in.close();
    output.close();

    // Add the new field header

    EventQueue.invokeAndWait(new UpdateHeader("" + (column), column + 1, m));




    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // move the tk field
//    if (m.getTkField() != -1) {
//      m.setTkField(m.getTkField() + adds / m.getCustomers());
//    }
    // init main parameters
    m.init(true);
    m.appendOutput("\nField Duplicated!!!");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
  }
}
