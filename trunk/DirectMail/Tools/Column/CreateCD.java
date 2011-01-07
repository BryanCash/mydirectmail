/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Help.Components.Errors;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import soldatos.functions.SwingFunctions;
import soldatos.messages.Logging;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.EncodingFunctions;

/**
 *
 * @author ssoldatos
 */
public class CreateCD implements Runnable {

  private MainForm m;
  private int fieldForCD;
  private String CDType;
  private long start,  end;

  public CreateCD(MainForm m, int fieldForCD, String CDType) {
    this.m = m;
    this.fieldForCD = fieldForCD;
    this.CDType = CDType;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      createTheCD();
      MainForm.glassPane.deactivate();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void createTheCD() throws IOException {
    String str, newOutLine = "";
    int lines = 0;
    String[] currLineArr;
    start = System.currentTimeMillis();
    m.setOutput("Creating the check digit");
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);

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
      if (getCD(currLineArr[fieldForCD]) != null) {
        currLineArr[fieldForCD] = getCD(currLineArr[fieldForCD]);
      } else {
        return;
      }
      newOutLine = ArrayFunctions.join(currLineArr, m.getDelimeter());
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
    // init main parameters
    m.init(true);
    m.appendOutput("\nCheck digit was appended to the field " + fieldForCD);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
  }

  private String getCD(String string) {
    if (CDType.equals("Mod10")) {
      if (EncodingFunctions.mod10(string) != -1) {
        return string + EncodingFunctions.mod10(string);
      } else {
        Errors.customError("Error", "The String " + string + " is not a number");
        return null;
      }
    } else {
      Errors.customError("Error", "Wrong type of Check Digit");
      return null;
    }

  }
}
