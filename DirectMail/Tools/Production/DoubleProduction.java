/*
 * DoubleProduction.java
 *
 * Created on 12 Σεπτέμβριος 2007, 1:14 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.Production;

import DirectMail.Help.Components.Errors;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.logging.Level;

/**
 *
 * @author ssoldatos
 */
public class DoubleProduction implements Runnable {

  private BufferedReader in;
  private PrintWriter output;
  private Vector<String> customers = new Vector<String>();
  long start, end;
  private MainForm m;

  /** Creates a new instance of DoubleProduction
   * @param m
   */
  public DoubleProduction(MainForm m) {
    this.m = m;
    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    try {
      // create the print writer
      output = new PrintWriter(new BufferedWriter(new FileWriter(m.getCurrentDirectory() +
          "/" + m.getOpenedFile().replace(".txt", "") + "_double.txt")));
    } catch (IOException ex) {
      Errors.IOError(ex.getMessage());
      MainForm.myLog.log(Level.SEVERE, "i/o error", ex);
    }
  }

  @Override
  public void run() {
    start = System.currentTimeMillis();
    m.appendOutput("\nStarting process");

    makingDuplex();

    end = System.currentTimeMillis();
    m.appendOutput("\nDouble production file was created: " +
        m.getCurrentDirectory() + "\\" + m.getOpenedFile().replace(".txt", "") + "_double.txt");
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    MainForm.trayIcon.showInfoMessage("Double production",
        "Double production file was created at:\n" +
        m.getCurrentDirectory() + "\\" + m.getOpenedFile().replace(".txt", "") + "_double.txt\n" +
        "Execution time : " + DmFunctions.execTime(start, end));
  }

  private void makingDuplex() {
    String outline = "", str;
    int total = 0, lines = 0;
    int numOfPages;

    try {
      while ((str = in.readLine()) != null) {
        lines++;
        outline = "";
        m.appendToCurrentOutput("Lines read: " + lines);
        m.updateProgress(lines * 100 / m.getCustomers());
        if (lines % 50 == 0) {
          DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
        }
        customers.addElement(str);
      }
      if (customers.size() % 2 == 0) {
        numOfPages = customers.size() / 2;
      } else {
        numOfPages = (customers.size() / 2) + 1;
        String dummy = customers.get(0);
        dummy = dummy.replaceAll("[^"+m.getDelimeter()+"]", " ");
        customers.addElement(dummy);
      }
      for (int i = 0; i < numOfPages; i++) {
        outline = customers.elementAt(i) + m.getDelimeter() + customers.elementAt(i + (numOfPages));

        output.println(outline);
      }
      m.updateProgress(0);
      output.close();
      //change to the new tmpfile name
      DmFunctions.swapFiles(m);
    } catch (IOException ex) {
      Errors.IOError(ex.getMessage());
      ex.printStackTrace();
    }
  }
}
