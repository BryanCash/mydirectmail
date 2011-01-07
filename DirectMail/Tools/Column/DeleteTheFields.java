/*
 * DeleteTheFields.java
 *
 * Created on 2 Αύγουστος 2007, 12:52 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Help.Components.Errors;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.Help.Functions.UpdateHeader;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import soldatos.functions.ArrayFunctions;

/**
 *
 * @author ssoldatos
 */
public class DeleteTheFields implements Runnable {

  boolean fieldsToDelete[];
  int deletes = 0;
  int fieldToDelete = -1;
  private MainForm m;
 private int numOfDeletedFields = 0;
  /**
   *
   * @param fieldsToDelete
   * @param m
   */
  public DeleteTheFields(boolean fieldsToDelete[], MainForm m) {
    this.m = m;
    this.fieldsToDelete = fieldsToDelete;
    for (int i = 0; i < m.getFields(); i++) {
      if (fieldsToDelete[i]) {
        numOfDeletedFields ++;
      }
    }
  }

  /**
   *
   * @param fieldToDelete
   * @param m
   */
  public DeleteTheFields(int fieldToDelete, MainForm m) {
    this.m = m;
    this.fieldToDelete = fieldToDelete;
    fieldsToDelete = new boolean[m.getFields()];
    numOfDeletedFields = 1;
    for (int i = 0; i < m.getFields(); i++) {
      if (i == fieldToDelete) {
        fieldsToDelete[i] = true;
      } else {
        fieldsToDelete[i] = false;
      }
    }
  }

  @Override
  public void run() {
    commitDelete();
  }

  /**
   * Reads the file line by line
   * Breaks each line to fields and breaks the fields marked for breaking to shorter
   * ones
   */
  private void commitDelete() {
    char currDel;
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    int lines = 0;
    String currLineArr[];
    long start, end;
    String[] newLineArr = new String[m.getFields()-numOfDeletedFields];
    start = System.currentTimeMillis();
    m.setOutput("Deleting Fields");


    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

    m.setTextAreaText(null);
    try {
      while ((str = in.readLine()) != null) {
        lines++;
        m.appendToCurrentOutput("Lines read : " + lines);
        m.updateProgress(lines * 100 / m.getCustomers());
        if (lines % 50 == 0) {
          DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
        }
        //System.out.println(m.delimeter);
        currLineArr = str.split("" + m.getDelimeter(), -1);
        newOutLine = "";
        int j=0;
        for (int i = 0; i < m.getFields(); i++) {
          if (fieldsToDelete[i]) {
            // Check if this field is before the TK
            //if (i < m.getTkField()) {
            //  deletes++;
           // }
          } else {
            newLineArr[j] = currLineArr[i];
            j++;
//            if (i < m.getFields() - 1) {
//              newOutLine += currLineArr[i] + m.getDelimeter();
//            } else if (!fieldsToDelete[i]) {
//              newOutLine += currLineArr[i];
//            }
            
          }
          newOutLine = ArrayFunctions.join(newLineArr, m.getDelimeter());

        }
        // delete the last delimeter if we deleted the last field
        //if (newOutLine.endsWith("" + m.getDelimeter()) && fieldsToDelete[m.getFields() - 1]) {
        //  newOutLine = newOutLine.substring(0, newOutLine.length() - 1);
        //}
        if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
          m.appendToSampleArea(newOutLine + "\n");
        }
        output.println(newOutLine);
      }
      // close files
      in.close();
      output.close();

      for (int i = 0; i < fieldsToDelete.length; i++) {
        if (fieldsToDelete[i]) {
          try {
            EventQueue.invokeAndWait(new UpdateHeader(i, m));
          } catch (InterruptedException ex) {
            ex.printStackTrace();
          } catch (InvocationTargetException ex) {
            ex.printStackTrace();
          }
        }
      }

      //change to the new tmpfile name
      DmFunctions.swapFiles(m);
      // move the tk field
     // if (m.getTkField() != -1) {
     //   m.setTkField(m.getTkField() - deletes / m.getCustomers());
     // }
      // init main parameters
      m.init(true);
      m.appendOutput("\n" + deletes / m.getCustomers() + ((deletes == 1) ? " field" : " fields") + " deleted!!!");
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (IOException ex) {
      Errors.IOError(ex.getMessage());
      ex.printStackTrace();
    }



  }
}
