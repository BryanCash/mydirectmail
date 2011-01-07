/*
 * SplitTheFile.java
 *
 * Created on 7 Αύγουστος 2007, 7:24 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.File;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.io.BufferedOutputStream;
import java.util.logging.Logger;
import soldatos.functions.SwingFunctions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import soldatos.functions.FileFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class SplitTheFile implements Runnable {

  int num1 = 0, num2 = 0;

  ;
  String action = "";
  private MainForm m;
  private ArrayList<OutputFiles> outputfiles;

  /** Constructor for Num To Num
   * @param num1
   * @param num2
   * @param m 
   */
  public SplitTheFile(int num1, int num2, MainForm m) {
    this.m = m;
    this.num1 = num1;
    this.num2 = num2;

  }

  /** Constructor for other actions
   * @param num1
   * @param action
   * @param m 
   */
  public SplitTheFile(int num1, String action, MainForm m) {
    this.m = m;
    this.num1 = num1;
    this.action = action;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      commit();
      MainForm.glassPane.deactivate();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void commit() throws IOException {

    if (num1 > 0 && num2 > 0) {
      splitFromNumToNum();
    } else {
      if (action.equals("StartToNum")) {
        splitStartToNum();
      } else if (action.equals("NumToEnd")) {
        splitNumToEnd();
      } else if (action.equals("EveryNum")) {
        splitEveryNum();
      } else if (action.equals("Field")) {
        splitByField();
      }
    }

  }

  private void splitByField() throws IOException {
    long start, end;
    String str;
    String prevFieldValue, val;
    String[] fields;
    outputfiles = new ArrayList<OutputFiles>();
    start = System.currentTimeMillis();
    m.setOutput("Spliting the file by field " + num1);
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    m.setTextAreaText(null);
    int lines = 0;
    int samples = 0;


    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      samples++;
      if (samples <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(str + "\n");
      }
      fields = str.split(String.valueOf(m.getDelimeter()), -1);
      val = fields[num1].trim();
      OutputFiles of;
      if (!outputFilesContains(val)) {
         of = new OutputFiles(val);
        of.createStream();
        outputfiles.add(of);
      } else {
        of = getOutPutFile(val);
      }
      of.writer.println(str);
    }


    // close files
    in.close();
    for (Iterator<OutputFiles> it = outputfiles.iterator(); it.hasNext();) {
      OutputFiles outputFile = it.next();
      PrintWriter p = outputFile.writer;
      p.close();
    }
    //change to the new tmpfile name
    //Functions.swapFiles();
    // init main parameters
    m.init(true);
    m.appendOutput("\nFile splitted");
    m.appendOutput("\nFiles are saved");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    MainForm.trayIcon.showInfoMessage("Splitting file",
        "File " + m.getOpenedFile() + " is splitted by field\n"
        + "Execution time : " + DmFunctions.execTime(start, end));
  }

  private void splitStartToNum() throws IOException {
    PrintWriter out = null;
    String currentName = null;
    BufferedReader in;
    PrintWriter output;
    String str;
    int lines = 0, samples = 0;
    long start, end;

    start = System.currentTimeMillis();
    m.setOutput("Spliting the file from the start to " + num1);


    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);



    currentName = m.getOpenedFile().substring(0, m.getOpenedFile().length() - 4)
        + "_" + "000001" + "-" + StringFunctions.padLeft("" + num1, 6, "0")
        + ".txt";
    out = new PrintWriter(new BufferedWriter(new FileWriter(m.getCurrentDirectory() + "//" + currentName)));


    m.setTextAreaText(null);

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / num1);
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      if (lines <= num1) {
        samples++;
        if (samples <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
          m.appendToSampleArea(str + "\n");
        }
        out.println(str);
      }
    }
    // close files
    in.close();
    out.close();
    //change to the new tmpfile name
    //Functions.swapFiles();
    // init main parameters
    m.init(true);
    m.setCustomers(samples);
    m.appendOutput("\nFile splitted");
    m.appendOutput("\nFile " + currentName + " is saved");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    MainForm.trayIcon.showInfoMessage("Splitting file",
        "File " + currentName + " is splitted\n"
        + "Execution time : " + DmFunctions.execTime(start, end));

  }

  private void splitNumToEnd() throws IOException {
    PrintWriter out = null;
    String currentName = null;
    BufferedReader in;
    String str;
    int lines = 0, samples = 0;
    long start, end;

    start = System.currentTimeMillis();
    m.setOutput("Spliting the file from " + num1 + " to the end");


    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);


    currentName = m.getOpenedFile().substring(0, m.getOpenedFile().length() - 4)
        + "_"
        + StringFunctions.padLeft("" + num1, 6, "0")
        + "-"
        + StringFunctions.padLeft("" + m.getCustomers(), 6, "0")
        + ".txt";
    out = new PrintWriter(new BufferedWriter(new FileWriter(m.getCurrentDirectory() + "//" + currentName)));

    m.setTextAreaText(null);

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / (m.getCustomers() - num1));
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      if (lines >= num1) {
        samples++;
        if (samples <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
          m.appendToSampleArea(str + "\n");
        }
        out.println(str);
      }
    }
    // close files
    in.close();
    out.close();
    //change to the new tmpfile name
    //Functions.swapFiles();
    // init main parameters
    m.init(true);
    m.setCustomers(samples);
    m.appendOutput("\nFile splitted");
    m.appendOutput("\nFile " + currentName + " is saved");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }

  private void splitEveryNum() throws IOException {
    String buffer = null;
    PrintWriter out = null;
    BufferedReader in;
    String str, currentName = "";
    int lines = 0, samples = 0, endC = 0, startC = 0;
    long start, end;

    start = System.currentTimeMillis();
    m.setOutput("Spliting the file every " + num1 + " lines");


    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);

    m.setTextAreaText(null);
    startC = 1;
    endC = num1 < m.getCustomers() ? num1 : m.getCustomers();
    currentName = m.getOpenedFile().substring(0, m.getOpenedFile().length() - 4)
        + "_"
        + StringFunctions.padLeft("" + startC, 6, "0")
        + "-"
        + StringFunctions.padLeft("" + endC, 6, "0")
        + ".txt";

    out = new PrintWriter(new BufferedWriter(new FileWriter(m.getCurrentDirectory() + "//" + currentName)));



    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / (m.getCustomers()));
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      if (samples % num1 == 0) {
        out.close();
        startC = samples + 1;
        endC = samples + num1 < m.getCustomers() ? samples + num1 : m.getCustomers();
        currentName = m.getOpenedFile().substring(0, m.getOpenedFile().length() - 4)
            + "_"
            + StringFunctions.padLeft("" + startC, 6, "0")
            + "-"
            + StringFunctions.padLeft("" + endC, 6, "0")
            + ".txt";
        buffer += "\nFile " + currentName + " is saved";
        m.appendToCurrentOutput("\nFile " + currentName + " is saved");
        out = new PrintWriter(new BufferedWriter(new FileWriter(m.getCurrentDirectory() + "//" + currentName)));
      }
      samples++;
      if (samples <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(str + "\n");
      }
      out.println(str);
    }
    // close files
    in.close();
    out.close();
    //change to the new tmpfile name
    //Functions.swapFiles();
    // init main parameters
    m.init(true);
    m.setCustomers(samples);
    m.appendOutput("\nFile splitted");
    m.appendOutput(buffer);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }

  private void splitFromNumToNum() throws IOException {
    PrintWriter out = null;
    String currentName = null;
    BufferedReader in;
    String str;
    int lines = 0, samples = 0;
    long start, end;

    start = System.currentTimeMillis();
    m.setOutput("Spliting the file from " + num1 + " to " + num2);


    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);


    currentName = m.getOpenedFile().substring(0, m.getOpenedFile().length() - 4)
        + "_"
        + StringFunctions.padLeft("" + num1, 6, "0")
        + "-"
        + StringFunctions.padLeft("" + num2, 6, "0")
        + ".txt";
    out = new PrintWriter(new BufferedWriter(new FileWriter(m.getCurrentDirectory() + "//" + currentName)));

    m.setTextAreaText(null);

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / (num2 - num1));
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      if (lines >= num1 && lines <= num2) {
        samples++;
        if (samples <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
          m.appendToSampleArea(str + "\n");
        }
        out.println(str);
      }
    }
    // close files
    in.close();
    out.close();
    //change to the new tmpfile name
    //Functions.swapFiles();
    // init main parameters
    m.init(true);
    m.setCustomers(samples);
    m.appendOutput("\nFile splitted");
    m.appendOutput("\nFile " + currentName + " is saved");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }

  private boolean outputFilesContains(String val) {
    for (Iterator<OutputFiles> it = outputfiles.iterator(); it.hasNext();) {
      OutputFiles outputFile = it.next();
      if (outputFile.value.equals(val)) {
        return true;
      }
    }
    return false;
  }

  private OutputFiles getOutPutFile(String val) {
    for (Iterator<OutputFiles> it = outputfiles.iterator(); it.hasNext();) {
      OutputFiles of = it.next();
      if (of.value.equals(val)) {
        return of;
      }
    }
    MainForm.myLog.log(Level.SEVERE,"printWriter for " + val + " was not found");
    return null;
  }

  class OutputFiles {

    public String value;
    public String filename;
    public PrintWriter writer;

    OutputFiles(String value) {

      this.value = value;
      this.filename = m.getOpenedFile().substring(0, m.getOpenedFile().lastIndexOf(".")) + "_" + value + m.getOpenedFile().substring(m.getOpenedFile().lastIndexOf("."));

    }

    public void createStream() {
      try {
        this.writer = FileFunctions.createOutputStream(m.getCurrentDirectory() + "/" + this.filename, false);
      } catch (IOException ex) {
        MainForm.myLog.log(Level.SEVERE, null, ex);
      }
    }
  }
}
