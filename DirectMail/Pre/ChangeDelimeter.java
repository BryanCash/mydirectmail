/*
 * ChangeDelimeter.java
 *
 * Created on 31 Ιούλιος 2007, 11:25 πμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Pre;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.*;
import DirectMail.Options.DmOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import soldatos.functions.ArrayFunctions;

/**
 *
 * @author ssoldatos
 */
public class ChangeDelimeter implements Runnable {

  private char newDel;
  private char oldDel;
  private MainForm m;

  /** Creates a new instance of ChangeDelimeter
   * @param m 
   */
  public ChangeDelimeter(MainForm m) {
    this.m = m;
    this.oldDel = m.getDelimeter();
  }

   public ChangeDelimeter(MainForm m, char del) {
    this.m = m;
    this.oldDel = m.getDelimeter();
    this.newDel = del;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      findNewDelimeter();
      m.setDelimeter(newDel);
      replaceDelimeter();
      MainForm.glassPane.deactivate();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }

  }

  private void findNewDelimeter() {
    if(newDel != '\u0000'){
      return;
    }
    char[] dels = soldatos.constants.Delimeters.toCharArray();
    for (int i = 0; i < dels.length; i++) {
      if (m.getDelimeter() == dels[i]) {
        if (i == dels.length - 1) {
          newDel = dels[0];
          return;
        } else {
          newDel = dels[i + 1];
          return;
        }
      }
    }
  }

  private void replaceDelimeter() throws IOException {
    BufferedReader in;
    PrintWriter output;
    String str = "";
    int lines = 0;
    long start, end;
    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);


    start = System.currentTimeMillis();
    m.setOutput("Changing delimeter from " + oldDel + " to " + newDel);


    m.setTextAreaText("");

    while ((str = in.readLine()) != null) {
      lines++;
      // replace the del
      String[] strArr = str.split(""+oldDel, -1);

      str = ArrayFunctions.join(strArr, newDel);
      //append the new line to textarea
      if (lines < MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        // m.appendTextAreaText(str.replace(newDel, m.getDelimeter()) + "\n");
        m.appendToSampleArea(str + "\n");
      }
      output.println(str);
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
    }
    // close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.setDelimeter(newDel);
    m.init(true);
    m.appendOutput("\nDelimeter changed");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }
}
