/*
 * Template.java
 *
 * Created on 11 Ïêôþâñéïò 2007, 10:39 ðì
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.File;

import DirectMail.*;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Help.Components.Errors;
import DirectMail.Main.MainForm;
import DirectMail.Tools.File.Forms.CreateTemplate;
import DirectMail.Options.DmOptions;
import soldatos.functions.SwingFunctions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import javax.swing.JOptionPane;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class Template implements Runnable {

  private String action = "";
  long start = 0;
  private int fieldsLen[];
  long end;
  private MainForm m;

  /** Creates a new instance of Template
   * @param action
   * @param m
   */
  public Template(String action, MainForm m) {
    this.action = action;
    this.m = m;
  }

  public Template(MainForm m, String action, int fieldsLen[]) {
    this.m = m;
    this.action = action;
    this.fieldsLen = fieldsLen;
  }

  @Override
  public void run() {
    MainForm.glassPane.activate(null);
    if (action.equals("save")) {
      saveTemplate();
    } else if (action.equals("apply")) {
      applyTemplate();
    } else if (action.equals("create")) {
      createTemplate();
    } else if (action.equals("saveCreated")) {
      saveTemplate();
    }
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    MainForm.glassPane.deactivate();
  }

  private void saveTemplate() {
    String name = "";
    File saveFile = null;
    int overwrite;

    name = JOptionPane.showInputDialog(null, "Type the name of the template", "Save Template", JOptionPane.PLAIN_MESSAGE);

    saveFile = new File("templates/" + name + ".dmt");
    if (saveFile.exists()) {
      overwrite = JOptionPane.showConfirmDialog(null, name + " template already exists.\nOverwrite it?", "File Exists", JOptionPane.OK_CANCEL_OPTION);
      if (overwrite == JOptionPane.OK_OPTION) {
        if (action.equals("save")) {
          saveToFile(name);
        } else if (action.equals("saveCreated")) {
          saveToFile(name, fieldsLen);
        }
      } else {
        Errors.customError("Template not saved", "The template was not saved!!!");
      }
    } else {
      if (action.equals("save")) {
        saveToFile(name);
      } else if (action.equals("saveCreated")) {
        saveToFile(name, fieldsLen);
      }
    }

  }

  private void saveToFile(String name) {
    PrintWriter out;
    String FieldsArr[] = new String[m.getFields()];

    start = System.currentTimeMillis();
    m.setOutput("\nSaving as template " + name);
    try {
      out = new PrintWriter(new BufferedWriter(new FileWriter("templates/" + name + ".dmt")));
      FieldsArr = m.getFirstLine().split("" + m.getDelimeter(), -1);
      for (int i = 0; i < FieldsArr.length; i++) {
        out.println(StringFunctions.padLeft(i, 2, " ") + ":" + FieldsArr[i].length());
      }
      out.close();
    } catch (IOException ex) {
      ex.printStackTrace();
      Errors.IOError(ex.getMessage());
    }
  }

  private void applyTemplate() {
    int i = 0;
    Object name;
    String[] templates;
    String line;
    FileReader in;
    Vector<String> fieldLength = new Vector<String>();
    templates = new File("templates/").list();


    name = JOptionPane.showInputDialog(null,
        "Select the template to apply",
        "Select Template",
        JOptionPane.PLAIN_MESSAGE,
        null,
        templates, templates[0]);
    if (name.equals("null")){
      return;
    }
    try {
      in = new FileReader("templates/" + String.valueOf(name));
      BufferedReader dis = new BufferedReader(in);
      while ((line = dis.readLine()) != null) {
        fieldLength.addElement(String.valueOf(line.substring(3)));
        i++;
      }
      m.setOutput("\nApplying the template " + String.valueOf(name));
      applyTheTemplate(fieldLength);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private void applyTheTemplate(Vector fieldLength) {
    String curNewLine;
    String[] curArr;
    int lines = 0;
    String str;
    PrintWriter output;
    BufferedReader in;

    m.setTextAreaText("");
    start = System.currentTimeMillis();
    try {
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
        for (int i = 0; i < m.getFields(); i++) {
          curArr[i] = StringFunctions.padRight(curArr[i], Integer.parseInt((String) fieldLength.elementAt(i)), " ");
        }
        // create the new line
        curNewLine = "";
        for (int i = 0; i < m.getFields(); i++) {
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
    } catch (IOException ex) {
      Errors.IOError(ex.getMessage());
      ex.printStackTrace();
    }
  }

  private void createTemplate() {
    CreateTemplate ct = new CreateTemplate(m);
  }

  private void saveToFile(String name, int fieldsLen[]) {
    PrintWriter out;
    String FieldsArr[] = new String[m.getFields()];
    start = System.currentTimeMillis();
    m.setOutput("\nSaving as template " + name);
    try {
      out = new PrintWriter(new BufferedWriter(new FileWriter("templates/" + name + ".dmt")));
      for (int i = 0; i < fieldsLen.length; i++) {
        out.println(StringFunctions.padLeft(i, 2, " ") + ":" + fieldsLen[i]);
      }
      out.close();
    } catch (IOException ex) {
      ex.printStackTrace();
      Errors.IOError(ex.getMessage());
    }
  }
}
