/*
 * Functions.java
 *
 * Created on 19 иОЩКИОР 2007, 12:08 ЛЛ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Help.Functions;

import DirectMail.Help.Functions.ComputeMaxLength;
import DirectMail.*;
import DirectMail.Help.Components.Errors;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import soldatos.functions.SwingFunctions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javax.swing.*;
import org.apache.poi.hssf.record.formula.functions.Replace;
import soldatos.constants.Language;
import soldatos.exceptions.ArrayLengthException;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.FileFunctions;
import soldatos.functions.StringFunctions;
import soldatos.options.Options;

/**
 * Useful functions for the PCadd Application
 * @author ssoldatos
 */
public class DmFunctions {

  /**
   * Finds the delimeter of a given {@link String}
   * @param m 
   * @param fl A delimeted line
   * @return The delimeter
   */
  public static char findDelimeter(MainForm m, String fl) {
    char[] dels = soldatos.constants.Delimeters.toCharArray();
    if (m.getDelimeter() != '\u0000') {
      return m.getDelimeter();
    }
    for (int i = 0; i < dels.length; i++) {
      if (fl.indexOf(dels[i]) > -1) {
        //System.out.println(MainForm.del[i]);
        return dels[i];
      }
    }
    // else default del tab
    return '\u0009';
  }

  /**
   * Gets the longest String of the column field
   * @param m The MainForm
   * @param field the column to search
   * @return the longest string
   */
  public static String getLongestField(MainForm m, int field) {
    String str = "";
    ComputeMaxLength c = new ComputeMaxLength(m, field);
    c.run();
    return c.getBiggerString();
  }

  public static int getResultSetNumberOfRows(ResultSet rs) throws SQLException {
    int rows = 0;
    rs.last();
    rows = rs.getRow();
    rs.beforeFirst();
    return rows;
  }

  /**
   * Gets the filename without the extension of a given {@link File}
   * @param fFile The File which name we want
   * @return The Filename
   */
  public static String getFilename(File fFile) {
    String fname = fFile.getName();
    int len = fname.length();

    return fname.substring(0, len - 4);
  }

  /**
   * Search for an {@link Integer} in an {@link Array}
   * @param arraytosearch The Array to search in
   * @param inttofind The integer we are looking for
   * @return True if it's found alse false
   */
  public static boolean isInArray(int[] arraytosearch, int inttofind) {
    for (int i = 0; i < arraytosearch.length; i++) {
      if (arraytosearch[i] == inttofind) {
        return true;
      }
    }
    return false;
  }

  /**
   * Search for an {@link Integer} in an {@link Array} and returns the index
   * @param arraytosearch The Array to search in
   * @param inttofind The integer we are looking for
   * @return index if it's found else -1
   */
  public static int getIndexInArray(int[] arraytosearch, int inttofind) {
    for (int i = 0; i < arraytosearch.length; i++) {
      if (arraytosearch[i] == inttofind) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Passes the contents of the temporary {@link File} to the original {@link File}
   * @param m 
   */
  public static void swapFiles(MainForm m) {
    m.getOrigFile().delete();
    m.setOrigFileName(m.getTmpFileName());
    m.setTmpFileName(m.getTmpFileName());
    File tmpFile = new File(m.getTmpFileName());
    tmpFile.deleteOnExit();
    m.setOrigFile(tmpFile);
    m.getOrigFile().deleteOnExit();
  }

  /**
   * Creates a new temporary file and makes a reader to read the original file
   * @param m 
   * @return A {@link BufferedReader}
   */
  public static BufferedReader createBufferedReader(MainForm m) {
    // create the new tmp file
    BufferedReader in = null;
    try {
      m.setTmpFileName(FileFunctions.createRandomName());
      m.setTmpFile(new File(m.getTmpFileName()));
      m.getTmpFile().deleteOnExit();
      in = new BufferedReader(new FileReader(DmOptions._JAR_DIR_+"tmp/" + m.getOrigFileName()));
    } catch (FileNotFoundException ex) {
      Errors.IOError(ex.getMessage());
      ex.printStackTrace();
    }
    return in;
  }

  /**
   * Reads from a bufferedReader and writes to the temporary File
   * @param m 
   * @param in The {@link BufferedReader}
   * @return A {@link PrintWriter}
   */
  public static PrintWriter createPrinterWriter(MainForm m, BufferedReader in) {
    PrintWriter output = null;
    try {
      output = new PrintWriter(DmOptions._JAR_DIR_+"tmp/"+ m.getTmpFileName());
    } catch (FileNotFoundException ex) {
      Errors.IOError(ex.getMessage());
      ex.printStackTrace();
    }
    return output;
  }

  /**
   * Prepares a TK String by removing @." ",- and _
   * @param string The TK String
   * @return The prepared TK String
   */
  public static String prepareTK(String string) {
    string = string.replaceAll("@", "");
    string = string.replaceAll(" ", "");
    string = string.replaceAll("-", "");
    string = string.replaceAll("_", "");
    return string;
  }

  /**
   * Calculates the execution time of the script
   * @param start the starting time in milliseconds
   * @param end the ending time in milliseconds
   * @return The execution time
   */
  public static String execTime(long start, long end) {
    String execTime = "";
    int hours, mins, secs, mill;
    long t, a;

    a = (end - start);
    t = (end - start) / 1000;
    mill = (int) a % 1000;
    secs = (int) t % 60;
    mins = (int) t / 60;
    hours = (int) t / 3600;

    execTime = StringFunctions.padLeft("" + hours, 2, "0") + ":"
        + StringFunctions.padLeft("" + mins, 2, "0") + ":"
        + StringFunctions.padLeft("" + secs, 2, "0") + ","
        + StringFunctions.padLeft("" + mill, 3, "0");
    return execTime;
  }

  /**
   * Reads a file into a {@link String}
   * @param filename The filename of the file to read
   * @return A {@link String} with the contents of the file
   */
  public static String readFileToString(String filename) {
    FileReader fr = null;
    StringBuffer b = null;
    BufferedReader myInput = null;
    String s;
    try {
      fr = new FileReader(filename);
      b = new StringBuffer();
      myInput = new BufferedReader(fr);
      while ((s = myInput.readLine()) != null) {
        b.append(s);
        b.append("\n");
      }
    } catch (IOException ex) {
      Errors.IOError(ex.getMessage());
      ex.printStackTrace();
    }
    return b.toString();
  }

  public static void calcRemainingTime(MainForm m, long start, long now, int lines, int customers) {
    String secondsStr, minutesStr, hoursStr, hoursPassedStr, minutesPassedStr, secondsPassedStr;
    long timeLeft;
    long timePassed;
    int secondsLeft;
    int allSecondsPassed;
    int hours, minutes, seconds, hoursPassed, minutesPassed, secondsPassed;
    timePassed = now - start;
    timeLeft = ((timePassed * customers) / lines) - timePassed;
    secondsLeft = (int) timeLeft / 1000;
    allSecondsPassed = (int) (timePassed / 1000);

    hours = (int) secondsLeft / 3600;
    minutes = (int) (secondsLeft % 3600) / 60;
    seconds = (int) (secondsLeft % 60);

    hoursPassed = (int) allSecondsPassed / 3600;
    minutesPassed = (int) (allSecondsPassed % 3600) / 60;
    secondsPassed = (int) (allSecondsPassed % 60);

    hoursStr = StringFunctions.padLeft(hours, 2, "0");
    minutesStr = StringFunctions.padLeft(minutes, 2, "0");
    secondsStr = StringFunctions.padLeft(seconds, 2, "0");

    hoursPassedStr = StringFunctions.padLeft(hoursPassed, 2, "0");
    minutesPassedStr = StringFunctions.padLeft(minutesPassed, 2, "0");
    secondsPassedStr = StringFunctions.padLeft(secondsPassed, 2, "0");

    m.setRemainingTime("E.T.A.: " + hoursStr + ":" + minutesStr + ":" + secondsStr
        + "(T.P.: " + hoursPassedStr + ":" + minutesPassedStr + ":" + secondsPassedStr +")");
  }

  public static File getSelectedFile(String dir, String filter[], String title, String message) {
    String[] files;
    int i = 0;
    Object name;

    File f;

    String[] templates;
    final String[] fileFilter = filter;

    files = new File(dir).list(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {
        for (int i = 0; i
            < fileFilter.length; i++) {
          if (name.endsWith(fileFilter[i])) {
            return true;
          }

        }
        return false;
      }
    });

    if (files.length == 0) {
      Errors.customError("Select File", "No files to select");
      return null;
    } else {
      name = JOptionPane.showInputDialog(null,
          message,
          title,
          JOptionPane.PLAIN_MESSAGE,
          null,
          files, files[0]);

      f = new File(dir + "/" + String.valueOf(name));
      return f;
    }

  }

  public static String checkForNoAccent(String addressing) throws ArrayLengthException {
    if (MainForm.options.toBoolean(DmOptions.NOT_ACCENTED_ADDRESSING)
        && !MainForm.options.toBoolean(DmOptions.CAPITAL_ADDRESSING)) {
      addressing = StringFunctions.searchAndReplace(addressing,
          Language.GREEK_ACCENTED,
          Language.GREEK_NOT_ACCENTED);
    }
    return addressing;
  }

  public static String correctLektikoCity(String c) {
    c = c.trim();
    c = c.replaceAll("\\([^)]*\\)", "");

    // c=c.replaceAll("\\.", " ");
    c = StringFunctions.deleteDoubleSpaces(c);

    if (c.endsWith("с") && c.startsWith("ац")) {
      c = c.replaceFirst("ац |аци |ацио |ац. |аци. |ацио. ", "ациос ");
    } else if ((c.endsWith("а") || c.endsWith("г")) && c.startsWith("ац")) {
      c = c.replaceFirst("ац |аци |ац. |аци. ", "ациа ");
    } else if (c.endsWith("ои") && c.startsWith("ац")) {
      c = c.replaceFirst("ац |аци |ац. |аци. ", "ациои ");
    }


    return c;

  }

  public static String correctLektikoAddress(String c) {


    String a = "";
    String r = "";

    c = c.replaceAll("\\.", " ");
    c = c.replaceAll("кеыж ", "");
    c = c.replaceAll("кеы ", "");
    c = c.replaceAll("ке ", "");
    c = c.replaceAll("к ", "");

    /**
    if (c.length() > 2) {
    a = getAddress(c);
    r = a;
    if (a.endsWith("ос")) {
    r = a.substring(0, a.length() - 2) + "ас";
    } else if (a.endsWith("ас")) {
    r = a.substring(0, a.length() - 2) + "ос";
    }
    }
    c = c.replace(a, r);
     **/
    return c;
  }

  public static String getAddress(String addressNum) {
    String[] cornersArr, corners;
    String currCorner;
    String finalAddress = "", addressTmp = "";

    // clear -
    addressNum = addressNum.replaceAll("-", " ");
    // break corners to 2 streets
    corners = addressNum.split("&", -1);



    // check if each part has a number and get the road with a number
    for (int i = 0; i < corners.length; i++) {
      cornersArr = corners[i].split(" ", -1);
      for (int j = 0; j < cornersArr.length; j++) {
        currCorner = cornersArr[j];
        if (!currCorner.trim().equals("")) {
          if ((StringFunctions.isNumber(currCorner)) || (StringFunctions.isNumber(currCorner.substring(0, currCorner.length() - 1)))) {
            finalAddress = corners[i].trim();
          }
        }
      }
      if (!finalAddress.equals("")) {
        break;
      } else {
        finalAddress = addressNum.trim();
      }
    }
    String[] addressArr = finalAddress.split(" ", -1);
    String currAddress;
    String numberStr = "";
    // check if each part of the road is string or number
    for (int i = 0; i < addressArr.length; i++) {
      currAddress = addressArr[i];
      if (!currAddress.trim().equals("")) {
        if ((!StringFunctions.isNumber(currAddress)) && (!StringFunctions.isNumber(currAddress.substring(0, currAddress.length() - 1)))) {
          addressTmp += " " + currAddress;
        } else {
          numberStr = currAddress;
        }
      }
    }
    // trim final results
    String address = addressTmp.trim();
    numberStr = numberStr.trim();

    return address;

  }

  public static String correctCity(String c) throws ArrayLengthException {

    return c;
//    c = c.replace("ахгмаиым", "ахгма");
//
//    if (c.endsWith("гс")) {
//      // c = c.substring(0, c.length() - 2) + "г";
//    } else if (c.endsWith("оу")) {
//      c = c.substring(0, c.length() - 2);
//    }
//
//
//    String[] search = {"аваямым", "ахгмаи", "фыцяажоу", "йеяатсимио", "петя/кгс",
//      "цакатсиоу", "буяымос", "лет/жысг", "иымиас", "ахгмым", "жакгяоу", "ьувийоу", "кауяиоу",
//      "папацоу", "жик/жеиа", "пеияаиа", "гкиоупокгс", "слуямгс", "йаккихеас"};
//    String[] replace = {"аваямес", "ахгма", "фыцяажос", "йеяатсими", "петяоупокг",
//      "цакатси", "буяымас", "леталояжысг", "иымиа", "ахгма", "жакгяо", "ьувийо", "кауяио",
//      "папацос", "жикадекжеиа", "пеияаиас", "гкиоупокг", "слуямг", "йаккихеа"};
//
//    c = StringFunctions.searchAndReplace(c, search, replace);
//
//    if (c.length() > 4) {
//      c = c.substring(0, c.length() - 3);
//    }
//    return c;
  }

  public static boolean isTextFile(File f) {
    String ext = FileFunctions.getExtension(f).toLowerCase();
    return ArrayFunctions.isInArray(ext, DmOptions._TEXT_FILES_);
  }

  public static boolean isExcelFile(File f) {
    String ext = FileFunctions.getExtension(f).toLowerCase();
    return ArrayFunctions.isInArray(ext, DmOptions._EXCEL_FILES_);
  }

  public static boolean isValidFile(File f) {
    String ext = FileFunctions.getExtension(f).toLowerCase();
    return ArrayFunctions.isInArray(ext, DmOptions._EXTENSIONS_);
  }


  private DmFunctions() {
  }
}
