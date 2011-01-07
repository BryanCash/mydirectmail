/*
 * ChangeDelimeter.java
 *
 * Created on 31 Ιούλιος 2007, 11:25 πμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.Production;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.*;
import DirectMail.Help.Functions.UpdateHeader;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

/**
 *
 * @author ssoldatos
 */
public class MakeDuplex implements Runnable {

  private char newDel;
  int numOfLines = 1;
  private PrintWriter pcadd;
  private MainForm m;

  /** Creates a new instance of ChangeDelimeter
   * @param numOfLines
   * @param m 
   */
  public MakeDuplex(int numOfLines, MainForm m) {
    this.m = m;
    this.numOfLines = numOfLines;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      makeTheDuplex(numOfLines);
      MainForm.glassPane.deactivate();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }

  }

  private void makeTheDuplex(int numOfLines) throws InterruptedException, IOException, InvocationTargetException {
    String duplexLine;
    BufferedReader in;
    PrintWriter output;
    String str = "", buffer = "";
    String[] duplexArr;
    int lines = 0;
    long start, end;
    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

    // create pcad print writer
    pcadd = new PrintWriter(new BufferedWriter(new FileWriter(m.getCurrentDirectory() + "/PCADD.txt")));


    start = System.currentTimeMillis();
    m.setOutput("Making the file duplex");


    m.setTextAreaText("");

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      buffer += str + "\n";
      if ((lines % numOfLines) == 0) {
        duplexLine = duplexing(buffer);
        buffer = "";
        //append the new line to textarea
        duplexArr = duplexLine.split("\n", -1);
        for (int j = 0; j < duplexArr.length; j++) {
          if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
            m.appendToSampleArea(duplexArr[j] + "");
          }
        }
        output.print(duplexLine);
      }
      pcadd.println(str);

    }
    // close files
    in.close();
    output.close();
    pcadd.close();

    // Add the duplex header
    EventQueue.invokeAndWait(new UpdateHeader("PAGE", 0, m));

    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
    m.appendOutput("\nFile ready for Duplex printing");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }

  private String duplexing(String str) {
    String duplexLine = "", front = "", back = "";
    String strArr[];

    strArr = str.split("\n", -1);
    for (int i = 0; i < strArr.length && !strArr[i].trim().equals(""); i++) {
      //front += strArr[i].replaceFirst(""+m.getDelimeter(),m.getDelimeter()+"<FRNT>"+m.getDelimeter())+"\n";
      //back += strArr[i].replaceFirst(""+m.getDelimeter(),m.getDelimeter()+"<BACK>"+m.getDelimeter())+"\n";
      front += "<FRNT>" + m.getDelimeter() + strArr[i] + "\n";
      back += "<BACK>" + m.getDelimeter() + strArr[i] + "\n";
    }
    duplexLine += front + back;

    return duplexLine.replace("\n\n", "\n");
  }
}
