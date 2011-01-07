/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Tools.Column.Forms.SelectFilterField;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ssoldatos
 */
public class FilterColumn implements Runnable {

  private final MainForm m;
  private long start;
  private long end;
  private int results = 0;

  public FilterColumn(MainForm m) {
    this.m = m;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      start = System.currentTimeMillis();
      m.setTextAreaText("");
      m.setOutput("Filtering column...");
      start();
      end = System.currentTimeMillis();
      m.appendOutput("\nFiltered results : " + results);
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
      MainForm.glassPane.deactivate();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
  }

  private void start() throws IOException {
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);
    String str;
    int lines = 0;
    String outString = "";
    String[] curLineArr;
    while ((str = in.readLine()) != null) {
      lines++;
      outString = "";
      m.appendToCurrentOutput("Lines read: " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      outString = str;
      //append the new line to textarea
      if (applyFilters(str)) {
        results++;
        m.appendToSampleArea(outString + "\n");
      }
      output.println(outString);
    }

    // Close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
  }

  private boolean applyFilters(String str) {
    boolean isShown;
    String[] fields = str.split(String.valueOf(m.getDelimeter()), -1);
    for (int i = 0; i < fields.length; i++) {
      Filter f = MainForm.isColumnFiltered(i);
      if (f != null) {
        int rule = f.getType();
        String text = f.getText();
        boolean caseSensitive = f.isCaseSensitive();
        String field = fields[i];
        switch (rule) {
          case SelectFilterField._STARTS_WITH_:
            if (field.startsWith(text)
                || (!caseSensitive
                && field.toLowerCase(new Locale("el")).startsWith(text.toLowerCase(new Locale("el"))))) {
              isShown = true;
            } else {
              return false;
            }
            break;
          case SelectFilterField._EQUALS_:
            if (field.equals(text)
                || (!caseSensitive
                && field.toLowerCase(new Locale("el")).equals(text.toLowerCase(new Locale("el"))))) {
              isShown = true;
            } else {
              return false;
            }
            break;
          case SelectFilterField._DOES_NOT_EQUAL_:
            if (!field.equals(text)
                || (!caseSensitive
                && !field.toLowerCase(new Locale("el")).equals(text.toLowerCase(new Locale("el"))))) {
              isShown = true;
            } else {
              return false;
            }
            break;
          case SelectFilterField._INCLUDES_:
            if (field.indexOf(text) > -1
                || (!caseSensitive
                && field.toLowerCase(new Locale("el")).indexOf(text.toLowerCase(new Locale("el"))) > -1)) {
              isShown = true;
            } else {
              return false;
            }
            break;
          case SelectFilterField._DOES_NOT_INCLUDE_:
            if (field.indexOf(text) == -1
                || (!caseSensitive
                && field.toLowerCase(new Locale("el")).indexOf(text.toLowerCase(new Locale("el"))) == -1)) {
              isShown = true;
            } else {
              return false;
            }
            break;
          case SelectFilterField._ENDS_WITH_:
            if (field.endsWith(text)
                || (!caseSensitive
                && field.toLowerCase(new Locale("el")).endsWith(text.toLowerCase(new Locale("el"))))) {
              isShown = true;
            } else {
              return false;
            }
            break;
          case SelectFilterField._NUMERIC_SMALLER:
            try{
            if (Integer.parseInt(field.trim()) < Integer.parseInt(text)) {
              isShown = true;
            } else {
              return false;
            }
            } catch(NumberFormatException ex){
              return false;
            }
            break;
             case SelectFilterField._NUMERIC_EQUAL:
            try{
            if (Integer.parseInt(field.trim()) == Integer.parseInt(text)) {
              isShown = true;
            } else {
              return false;
            }
            } catch(NumberFormatException ex){
              return false;
            }
            break;
             case SelectFilterField._NUMERIC_BIGGER:
            try{
            if (Integer.parseInt(field.trim()) > Integer.parseInt(text)) {
              isShown = true;
            } else {
              return false;
            }
            } catch(NumberFormatException ex){
              return false;
            }
            break;
          default:
            return false;
        }
      } else {
        isShown = true;
      }
    }


    return true;
  }
}
