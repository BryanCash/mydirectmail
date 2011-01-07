/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class CreateRandomCode implements Runnable {

  private final MainForm m;
  private final int digits;
  private final boolean letters;
  private long start;
  private long end;

  public CreateRandomCode(MainForm m, int digits, boolean letters) {
    this.m = m;
    this.digits = digits;
    this.letters = letters;

  }

  @Override
  public void run() {
    try {
      start = System.currentTimeMillis();
      m.setOutput("Creating random codes");
      commit();
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
      soldatos.messages.Messages.customError("Creating random digits", "Digits needed are more than digits evailable");
    }
  }

  private void commit() throws IOException, InterruptedException, InvocationTargetException, Exception {
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);
    HashMap<String, String> codes = createCodes();
    ArrayList<String> codesList = new ArrayList<String>(codes.values());
    m.setTextAreaText(null);
    String str;
    int lines = 0;
    String newOutLine = "";

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      newOutLine = str + m.getDelimeter() + codesList.get(lines - 1);
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    // close files
    in.close();
    output.close();
    // Add the new field header
    EventQueue.invokeAndWait(new UpdateHeader("" + (m.getFields() - 1), m.getFields(), m));

    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
    m.appendOutput("\nRandom code was created");
  }

  private HashMap<String, String> createCodes() throws Exception {
    HashMap<String, String> codes = new HashMap<String, String>();
    if (letters) {
      if (m.getCustomers() > Math.pow(62 , digits)) {
        throw new Exception("Digits needed are more than digits evailable");
      }
      while (codes.size() < m.getCustomers()) {
        String code = StringFunctions.createRandomString(digits);
        codes.put(code, code);
      }

    } else {
      if (m.getCustomers() > Math.pow(10 , digits)) {
        throw new Exception("Digits needed are more than digits evailable");
      }
      while (codes.size() < m.getCustomers()) {
        String code = StringFunctions.createRandomNumber(digits);
        codes.put(code, code);
      }
    }
    return codes;
  }
}
