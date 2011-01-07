/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.FieldView;
import soldatos.functions.FileFunctions;

/**
 *
 * @author ssoldatos
 */
public class FindUniqueFields implements Runnable {

  private final MainForm m;
  private final int field;
  private final String file;
  private final String bg;
  private long start;
  private long end;
  private final boolean trim;
  private boolean multibg;
  private String[] bgs;

  /**
   * 
   * @param m
   * @param field
   * @param file
   * @param bg
   * @param trim
   */
  public FindUniqueFields(MainForm m, int field, String file, String bg, boolean trim) {
    this.m = m;
    this.field = field;
    this.file = file;
    this.bg = bg;
    this.trim = trim;
    if (bg != null) {
      bgs = bg.split("\n", -1);
      if (bgs.length > 1) {
        multibg = true;
      }
    }
  }

  @Override
  public void run() {
    MainForm.glassPane.activate(null);
    try {
      start = System.currentTimeMillis();
      m.setOutput("Geting the unique fields...");
      getUnique();
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void getUnique() throws IOException {
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);
    PrintWriter unique = FileFunctions.createOutputStream(m.getCurrentDirectory() + "/" + file, false);
    String str;
    int lines = 0;
    Map map = new HashMap();


    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read: " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      String[] fields = str.split("" + m.getDelimeter(), -1);
      String value = fields[field];
      if (trim) {
        value = value.trim();
      }
      map.put(value, "");
      //append the new line to textarea
      output.println(str);
    }
    Set uniqueSet = map.keySet();
    ArrayList list = new ArrayList(uniqueSet);
    Collections.sort(list);
    if (bg != null) {
      unique.print("NAME,BACKGROUND\r\n");
    }
    for (int i = 0; i < list.size(); i++) {
      String fieldVal = (String) list.get(i);
      if (bg != null) {
        if (!multibg) {
          unique.print("\"" + fieldVal + "\",\"" + bg + "\"\r\n");
        } else {
          for (int j = 0; j < bgs.length; j++) {
            unique.print("\"" + fieldVal + "\",\"" + bgs[j].trim() + "\"\r\n");
          }
        }
      } else {
        unique.print(fieldVal + "\r\n");
      }
    }
    // Close files
    in.close();
    output.close();
    unique.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
  }
}
