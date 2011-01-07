/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import soldatos.exceptions.ArrayLengthException;
import soldatos.functions.SwingFunctions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import soldatos.constants.Language;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class ConvertGreek implements Runnable {

  private int field;
  private MainForm m;
  private long start;
  private long end;
  private int replaces = 0;

  public ConvertGreek(int field, MainForm m) {
    this.field = field;
    this.m = m;
  }

  @Override
  public void run() {
    try {
      start = System.currentTimeMillis();
      m.setTextAreaText("");
      m.setOutput("Converting to Greek");
      readFile();
      m.appendOutput("\nConverted " + replaces + " names to Greek");
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (ArrayLengthException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
        MainForm.myLog.log(Level.WARNING, null, ex);
    }
  }

  private String convert(String string) throws ArrayLengthException {
    String newString = string;
    // An exei special agglikous xaraktires einai aggliko onoma
    if (StringFunctions.isContainingSpecialEnglishChar(string)){
      return newString;
    } // Allios theorise to elliniko kai kane metatropi
    else {
      newString = StringFunctions.searchAndReplace(string, Language.ENGLISH_COMMON_CHARS, Language.GREEK_COMMON_CHARS);
      if (!newString.equals(string)){
        replaces++;
      }
      return newString;
    }
  }

 

  private void readFile() throws IOException, ArrayLengthException {
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
        if (i == field || field == -1) {
          curLineArr[i] = convert(curLineArr[i]);
        }
      }
      outString = ArrayFunctions.join(curLineArr, m.getDelimeter());
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
