package DirectMail.Pre;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.*;
import DirectMail.Options.DmOptions;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import java.util.logging.Level;
import javax.swing.JOptionPane;

/**
 * Adds the zip code flag at the end of each line
 *
 */
public class ZipCode implements Runnable {

  /**
   * The constructor of the ZipCode class.<br>
   * Takes the first line of the file and splits it by each field.<br>
   * Then check each field if it is azipcode field.<br>
   * Once it founds the zipcode field it makes the new file with the zipcode flag
   * appended at the end of each line.
   */
  long start, end;
  private MainForm m;

  /**
   * 
   * @param m
   */
  public ZipCode(MainForm m) {
    this.m = m;
  }

  @Override
  public void run() {
    try {
      zCode();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    }
  }

  private void zCode() throws InterruptedException, InvocationTargetException, IOException {


    start = System.currentTimeMillis();
    m.setOutput("Adding a zip code field at the end of each line");
    //FileChooseApp.getDelimeter();
    //check for no tab , ; or # delimeter
    if (m.getDelimeter() == '\u0000') {
      JOptionPane.showMessageDialog(null, "No delimeter was found in the file.\nAborting...", "Delimeter error", JOptionPane.ERROR_MESSAGE);
      m.setTextAreaText(null);
    }

    //firstLine = Functions.getFirstLine(m.getTextAreaText());
    //firstLineArr = firstLine.split(m.delimeter);
    //System.out.println(firstLine+ " "+m.delimeter);

    makeNewFile(m.getTkField());
    m.appendOutput("\nZip code field added");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
  }

  /**
   *  Makes the new file with the zipcode flag appended to
   *  the end of each line.
   * @param zipField The field of the Zipcode
   */
  private void makeNewFile(int zipField) throws InterruptedException, InvocationTargetException, IOException {
    BufferedReader in;
    PrintWriter output;
    String str;
    int pistonFlag = 0;
    int counter = 0, currCount = 0;
    int lines = 0;
    String currLineArr[];
    String currZip, prevZip = "dummy";

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
      currZip = currLineArr[zipField].trim();

      if (!currZip.equals(prevZip)) {
        currCount = 0;
        pistonFlag++;
        if (pistonFlag == 10) {
          pistonFlag = 0;
        }
      }
      currCount++;
      prevZip = currZip;

      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(str + m.getDelimeter() + "@" + pistonFlag + m.getDelimeter() + "EOL!<-                    !" + "\n");
      }
      output.println(str + m.getDelimeter() + "@" + pistonFlag + m.getDelimeter() + "EOL!<-                    !");
    }
    // close files
    in.close();
    output.close();

    // Add the  header

    EventQueue.invokeAndWait(new UpdateHeader("ZIP CODE FLAG", m.getFields(), m));
    EventQueue.invokeAndWait(new UpdateHeader("EOL", m.getFields() + 1, m));

    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);

  }
}
