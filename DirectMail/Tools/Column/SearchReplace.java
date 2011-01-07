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
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 *
 * @author ssoldatos
 */
public class SearchReplace implements Runnable {

  private MainForm m;
  private int field;
  private String search;
  private String replace;
  private long start;
  private long end;
  private boolean useRegExp;
  private String searchStr;
  private Pattern myPattern;
  private int replacements = 0;

  public SearchReplace(MainForm m, int field, String search, String replace, boolean useRegExp) {
    this.m = m;
    this.field = field;
    this.search = search;
    this.replace = replace;
    this.useRegExp = useRegExp;
    if (useRegExp) {
      searchStr = search;
    } else {
      searchStr = Pattern.quote(search);
    }
    myPattern = Pattern.compile(searchStr);

  }

  @Override
  public void run() {
    start = System.currentTimeMillis();
    m.setTextAreaText("");
    m.setOutput("Searching and replacing");
    try {
      readFile();
    } catch (IOException ex) {
        MainForm.myLog.log(Level.WARNING, null, ex);
    }

    m.appendOutput("\nReplaced '" + search + "' to '" + replace + "'");
    m.appendOutput("\nReplacments : " + replacements);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
  }

  private String doReplace(String str) {
    String replacedString;
    replacedString = str.replaceAll(searchStr, replace);
    return replacedString;
  }

  private void readFile() throws IOException {
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
      // split fields
      curLineArr = str.split("" + m.getDelimeter(), -1);
      for (int i = 0; i < curLineArr.length; i++) {
        if (i == field) {
          String oldFieldValue = curLineArr[i];
          curLineArr[i] = doReplace(curLineArr[i]);
          if(!curLineArr[i].equals(oldFieldValue)){
            replacements ++;
          }
        }
        if (i < curLineArr.length - 1) {
          outString += curLineArr[i] + m.getDelimeter();
        } else {
          outString += curLineArr[i];
        }
      }
      //append the new line to textarea
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
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
}

