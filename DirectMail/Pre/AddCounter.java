/*
 * AddCounter.java
 *
 * Created on 28 Ιούλιος 2007, 6:32 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Pre;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.*;
import DirectMail.Options.DmOptions;
import soldatos.functions.SwingFunctions;
import java.awt.EventQueue;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import soldatos.functions.StringFunctions;

/**
 * Adds a "0" padded counter in every line
 * @author ssoldatos
 */
public class AddCounter implements Runnable {

  String prod[] = {"8 digits(add 11)", "6 digits"};
  private String addString = "";
  private MainForm m;

  /**
   * Reads the file line by line and adds an ascending counter in the form of 000000
   * @param m 
   */
  public AddCounter(MainForm m) {
    this.m = m;
    //String addWhat = (String) JOptionPane.showInputDialog(null, "Select production line", "Production Line", JOptionPane.QUESTION_MESSAGE, null, prod, 0);
    //if (addWhat == null) {
    //  addString = null;
    //} else {
    //  if (addWhat.equals("8 digits(add 11)")) {
        addString = "11";
    //  } else if (addWhat.equals("6 digits")) {
    //    addString = "";
    //  }
    //}
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      add();
      MainForm.glassPane.deactivate();
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

  public void add() throws IOException, InterruptedException, InvocationTargetException {
    BufferedReader in;
    PrintWriter output;
    String str, curNewLine = "";
    int lines = 0;
    long start, end;

    start = System.currentTimeMillis();
    m.setOutput("Adding counter");

    if (addString == null) {
      m.appendOutput("\nAdding counter canceled");
      return;
    }

    m.setTextAreaText("");

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);
    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      curNewLine = StringFunctions.padLeft("" + lines, 6, "0") +
          addString +
          m.getDelimeter() + str + m.getDelimeter() + "END" ;
      //append the new line to textarea
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(curNewLine + "\n");
      }
      output.println(curNewLine);
    }
    // close files
    in.close();
    output.close();

    // Add thecounter header
    EventQueue.invokeAndWait(new UpdateHeader("COUNTER", 0, m));
    EventQueue.invokeAndWait(new UpdateHeader("", m.getFields()+1, m));

    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // add one to the tkField if any
    //if (m.getTkField() != -1) m.setTkField(m.getTkField()+1);
    // init main parameters
    m.init(true);
    m.appendOutput("\nCounter added");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }
}
