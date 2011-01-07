/*
 * FixedFile.java
 *
 * Created on 23 Ιούλιος 2007, 12:11 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Pre;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Tools.Column.Forms.PadPanel;
import DirectMail.Options.DmOptions;
import DirectMail.Help.Functions.ComputeMaxLength;
import java.io.*;
import java.util.logging.Level;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.StringFunctions;

/**
 *  FixedFile class makes a tab, ; or # delimeter file fixed
 *  by computing the maximum size of each field.
 * @author ssoldatos
 */
public class FixedFile implements Runnable {

  /**
   * An array holding the maximum lengths of each field
   */
  private int[] maxLengths;
  long start, end;
  int column = -1;
  private MainForm m;

  /**
   * The Constructor.
   * Counts the fields of each line and sets its length to 0
   * Then computes the max length of each field and generates the nes fixed length file.
   * @param m
   */
  public FixedFile(MainForm m) {
    this.m = m;
  }

  public FixedFile(int column, MainForm m) {
    this.m = m;
    this.column = column;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      fix();
      MainForm.glassPane.deactivate();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, "File I/O exception", ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void fix() throws IOException {
    if (column == -1) {
      makeAllFieldsFixed();
    } else {
      makeFieldFixed();
    }
  }

  private void makeAllFieldsFixed() throws IOException {
    String longFields = "";
    start = System.currentTimeMillis();
    m.setOutput("Computing the maximum length of each field");
    maxLengths = new int[m.getFields()];
    for (int i = 0; i < m.getFields(); i++) {
      maxLengths[i] = 0;
    }
    maxLengths = getFieldsMaxSize();


    m.appendOutput("\nMaking each line fixed");
    fixFile(maxLengths);
    m.appendOutput("\nFile is fixed length now");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    // Alert for long fields
    for (int i = 0; i < m.getFields(); i++) {
      if (maxLengths[i] > MainForm.options.toInt(DmOptions.MAX_FIELD_LENGTH)) {
        longFields += "\n" + m.getHeaderTitles().get(i) + " (" + maxLengths[i] + ")";
      }
    }
    if (!longFields.equals("")) {
      soldatos.messages.Messages.customError("Long Fields", "Long Fields:" + longFields + "\n");

    }
  }

  /**
   * Computes the maximum length of each field.
   * @return int[] An array with the maximum lengths.
   */
  private int[] getFieldsMaxSize() throws IOException {
    String str = "";
    String[] curArr;
    BufferedReader in = null;

    in = DmFunctions.createBufferedReader(m);

    while ((str = in.readLine()) != null) {

      // split fields
      curArr = str.split("" + m.getDelimeter(), -1);
      for (int i = 0; i < m.getFields(); i++) {
        if (curArr[i].length() > maxLengths[i]) {
          maxLengths[i] = curArr[i].length();
        }
      }
    }

    return maxLengths;
  }

  /**
   * Creates the new fixed length file.
   * @param maxLengths The maximum lengths of the fields
   */
  private void fixFile(int[] maxLengths) throws IOException {
    BufferedReader in;
    PrintWriter output;
    String str;
    String[] curArr;
    String curNewLine = null;
    int counter = 0;
    int lines = 0;

    m.setTextAreaText("");

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);


    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read: " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      // split fields
      curArr = str.split("" + m.getDelimeter(), -1);
      // pad fields
      //for (int i=0 ; i < m.getFields() ; i++){
      //  curArr[i] = Functions.padRight(curArr[i],maxLengths[i]," ");
      //}
      // create the new line
      curNewLine = "";
      for (int i = 0; i < m.getFields(); i++) {
        curArr[i] = StringFunctions.padRight(curArr[i], maxLengths[i], " ");
        if (i < m.getFields() - 1) {
          curNewLine += curArr[i] + m.getDelimeter();
        } else {
          curNewLine += curArr[i];
        }
      }

      //append the new line to textarea
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(curNewLine + "\n");
      }
      output.println(curNewLine);
    }
    // close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);

  }

  private int getLongestField(int field) {
    ComputeMaxLength c = new ComputeMaxLength(m, field);
    c.run();
    return c.getMaxLength();
  }

  private void makeFieldFixed() throws IOException {
    String curNewLine;
    String[] curArr;
    int lines = 0;
    String str;
    PrintWriter output;
    BufferedReader in;
    String lengthStr;
    int length = 0;

    start = System.currentTimeMillis();
    m.setOutput("Making field " + column + " fixed length");
    PadPanel pad = new PadPanel(getLongestField(column));
    if (pad.cancel) {
      return;
    }
    boolean reduce = pad.checkBox_reduce.isSelected();
    length = pad.length;
    String padStr = pad.str;
    int padding = pad.padding;

    m.setTextAreaText("");
    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);


    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read: " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      // split fields
      curArr = str.split("" + m.getDelimeter(), -1);
      // create the new line
      curNewLine = "";
      for (int i = 0; i < m.getFields(); i++) {
        if (i == column) {
          if (reduce) {
            if (padding == soldatos.constants.Padding.RIGHT_PAD) {
              curArr[i] = StringFunctions.padRight(curArr[i], length, padStr);
            } else {
              curArr[i] = StringFunctions.padLeft(curArr[i], length, padStr);
            }
          } else {
            if (curArr[i].trim().length() < length) {
              if (padding == soldatos.constants.Padding.RIGHT_PAD) {
                curArr[i] = StringFunctions.padRight(curArr[i], length, padStr);
              } else {
                curArr[i] = StringFunctions.padLeft(curArr[i], length, padStr);
              }
            }
          }
        } else {
          curArr[i] = curArr[i];
        }
      }
      curNewLine = ArrayFunctions.join(curArr, m.getDelimeter());
      //append the new line to textarea
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(curNewLine + "\n");
      }
      output.println(curNewLine);
    }
    // close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);


    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));


  }
}
