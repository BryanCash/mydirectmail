/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Pre;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.Options.DmOptions;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class ShiftFields implements Runnable {

  private int from;
  private int to;
  private MainForm m;
  private int add;

  public ShiftFields(MainForm m, int from, int to) {
    this.from = from;
    this.to = to;
    this.m = m;
  }

  @Override
  public void run() {
    try {
      commitShift();
      MainForm.glassPane.deactivate();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void commitShift() throws IOException, InterruptedException, InvocationTargetException {
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    int lines = 0;
    String currLineArr[];
    long start, end;


    start = System.currentTimeMillis();
    m.setOutput("Shifting Fields");

    m.setTextAreaText(null);

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

    while ((str = in.readLine()) != null) {
      String tmpBefore = "";
      String tmpAfter = "<---///--->" + m.getDelimeter();
      String tmpShift = "";
      boolean pcadd = false;
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      //System.out.println(m.delimeter);
      currLineArr = str.split("" + m.getDelimeter(), -1);
      newOutLine = "";
      String tmp = "";
      for (int i = 0; i < m.getFields(); i++) {
        if (i < from) {
          tmpBefore += currLineArr[i] + m.getDelimeter();
        } else if (i > to) {
          tmpAfter += currLineArr[i] + m.getDelimeter();
        } else {
          tmpShift += currLineArr[i] + m.getDelimeter();

        }
      }
      tmpShift = shiftRight(tmpShift.substring(0, tmpShift.length() - 1));
      if (tmpAfter.length() > 0) {
        tmpAfter = m.getDelimeter() + tmpAfter.substring(0, tmpAfter.length() - 1);
      }
      newOutLine = tmpBefore + tmpShift + tmpAfter;
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        //EventQueue.invokeLater (new UpdateSampleTable(m, newOutLine));
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    m.updateProgress(0);
    // close files
    in.close();
    output.close();

    for (int i = 0; i < add; i++) {
      EventQueue.invokeAndWait(new UpdateHeader("", from + i, m));
    }
     EventQueue.invokeAndWait(new UpdateHeader("", from + 9 , m));
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // move the tk field
    //if (m.getTkField() != -1) m.setTkField(m.getTkField()+breaks);
    // init main parameters
    m.init(true);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }

  private String shiftLeft(String s) {
    String out = "";
    boolean shift = false;
    String[] sArr = s.split("" + m.getDelimeter(), -1);
    for (int i = 0; i < sArr.length; i++) {
      if (i < sArr.length - 1 && sArr[i].trim().equals("") && !sArr[i + 1].trim().equals("")) {
        sArr[i] = sArr[i + 1];
        sArr[i + 1] = "";
        shift = true;
      }
      if (i < sArr.length - 1) {
        out += StringFunctions.padRight(sArr[i], MainForm.options.toInt(DmOptions.MAX_FIELD_LENGTH), " ") + m.getDelimeter();
      } else {
        out += StringFunctions.padRight(sArr[i], MainForm.options.toInt(DmOptions.MAX_FIELD_LENGTH), " ");
      }
    }
    if (shift) {
      return (shiftLeft(out));
    } else {
      return out;
    }
  }

  private String shiftRight(String s) {
    String out = "";
    boolean shift = false;
    String[] sArr = s.split("" + m.getDelimeter(), -1);
    for (int i = 0; i < sArr.length; i++) {
      if (i > 0 && sArr[i].trim().equals("") && !sArr[i - 1].trim().equals("")) {
        sArr[i] = StringFunctions.padRight(sArr[i - 1], MainForm.options.toInt(DmOptions.MAX_FIELD_LENGTH), " ");
        sArr[i - 1] = "";
        shift = true;
      } else {
        sArr[i] = StringFunctions.padRight(sArr[i], MainForm.options.toInt(DmOptions.MAX_FIELD_LENGTH), " ");
      }
    }
    out = ArrayFunctions.join(sArr, m.getDelimeter());
    if (shift) {
      return (shiftRight(out));
    } else {
      String more = "";
      if (sArr.length < 8) {
        add = 8 - sArr.length;
        for (int i = 0; i < add; i++) {
          more += StringFunctions.padLeft("", MainForm.options.toInt(DmOptions.MAX_FIELD_LENGTH), " ") +
              m.getDelimeter();
        }
      }
      return more + out;
    }
  }
}
