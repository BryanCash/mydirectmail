/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class ExtractZipcode implements Runnable {

  private MainForm m;
  private long start;
  private long end;
  private int found = 0;
  private int notFound = 0;
  private String REGEX = "(‘\\. \\.)|(‘ )|(T\\.K\\.)|(TK)|(‘·. .)";

  public ExtractZipcode(MainForm m) {
    this.m = m;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      start = System.currentTimeMillis();
      m.setTextAreaText("");
      m.setOutput("Formating...");
      split();
      end = System.currentTimeMillis();
      m.appendOutput("\nField splitted");
      m.appendOutput("\nFields with Zipcode : " + found);
      m.appendOutput("\nFields without Zipcode : " + notFound);
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (InterruptedException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void split() throws IOException, InterruptedException, InvocationTargetException {
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    int lines = 0;
    String currLineArr[];

    start = System.currentTimeMillis();
    m.setOutput("SExtracting Zipcode");


    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

    m.setTextAreaText(null);
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
      String address = currLineArr[m.getAddressField()];
      address = StringFunctions.deleteDoubleSpaces(address.replaceAll(REGEX, " "));
      String[] zip = extractZipCode(address);
      if (zip[0].equals("")) {
        notFound++;
      } else {
        found++;
      }
      String newAddress = zip[1];

      currLineArr[m.getAddressField()] = StringFunctions.deleteDoubleSpaces(newAddress) + m.getDelimeter() + zip[0];
      newOutLine = ArrayFunctions.join(currLineArr, m.getDelimeter());
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    // close files
    in.close();
    output.close();

    // Add the new field header

    EventQueue.invokeAndWait(new UpdateHeader("ZIP CODE" + (m.getAddressField()), m.getAddressField() + 1, m));

    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // move the tk field
//    if (m.getTkField() != -1) {
//      m.setTkField(m.getTkField() + adds / m.getCustomers());
//    }
    // init main parameters
    m.init(true);
  }

  private String[] extractZipCode(String str) {
    String[] zip = {"", ""};
    String[] arr = str.split(" ");
    for (int i = 0; i < arr.length; i++) {
      String string = arr[i].trim();
      if (StringFunctions.isNumber(string)) {
        if (string.length() == 5) {
          zip[0] = string;
          arr[i] = "";
          zip[1] = ArrayFunctions.join(arr, " ");
          return zip;
        } else if (i < arr.length - 1) {
          String s = string + arr[i + 1];
          zip = extractZipCode(s);
          if (!zip[0].equals("")) {
            zip[0] = s;
            arr[i] = "";
            arr[i+1] = "";
            zip[1] = ArrayFunctions.join(arr, " ");
            return zip;
          }
        }
      } else if (string.indexOf("-") > -1) {
        String s = string.replace("-", "");
        zip = extractZipCode(s);
        if (!zip[0].equals("")) {
          zip[0] = s;
          arr[i] = "";
          zip[1] = ArrayFunctions.join(arr, " ");
          return zip;
        }
      }
    }
    zip[0]="";
    zip[1] =str;
    return zip;
  }
}
