/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Correct;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.Options.DmOptions;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.connection.MyDBCConnection;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.Elta;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class FindCounty implements Runnable {

  private MainForm m;
  private DmOptions options;
  private Statement stmt;
  private long start, end;
  private int countiesFound = 0;
  private int countiesNotFound = 0;

  /**
   *
   * @param m
   */
  public FindCounty(MainForm m) {
    this.m = m;
    this.options = MainForm.options;
  }

  @Override
  public void run() {
    try {
      start = System.currentTimeMillis();
      MainForm.glassPane.activate(null);
      MyDBCConnection.connect(options.toString(DmOptions.HOST), options.toString(DmOptions.DATABASE), options.toString(DmOptions.DB_USER), options.toString(DmOptions.DB_PASSWORD));
      stmt = MyDBCConnection.getMyConnection().createStatement();
      findTheCounty();
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
      MainForm.trayIcon.showInfoMessage("Getting Counties",
        "Getting the Counties finished\n" +
        "Counties found : " + countiesFound + "\n" +
        "Counties not found : " + countiesNotFound + "\n" +
        "Execution time : " + DmFunctions.execTime(start, end));
    } catch (InterruptedException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void findTheCounty() throws InterruptedException, InvocationTargetException, IOException, SQLException {
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    String currLineArr[];
    m.setOutput("Finding the county");

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

    //InfoFrame.labelInfo.setText("Getting the gender");
    //Read the file and insert the rows
    m.appendOutput("\nReading the file and \nfinding the county");


    m.setTextAreaText(null);
    int lines = 0;

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
        //System.out.println(m.delimeter);
      }
      currLineArr = str.split("" + m.getDelimeter(), -1);
      newOutLine = "";
      String county = "";
      if(m.getAbroadField() == -1){
        county = soldatos.functions.Elta.findCountyByTk(currLineArr[m.getTkField()], stmt);
      } else {
        if(StringFunctions.isGreece(currLineArr[m.getAbroadField()])){
          county = soldatos.functions.Elta.findCountyByTk(currLineArr[m.getTkField()], stmt);
        }
      }
      if(county.equals("")){
        countiesNotFound++;
      } else {
        countiesFound++;
      }
      currLineArr[m.getTkField()] = currLineArr[m.getTkField()].trim() + m.getDelimeter() + county;
      newOutLine = ArrayFunctions.join(currLineArr, m.getDelimeter());
      if (lines <= options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    // close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);


    // Add the city field

    EventQueue.invokeAndWait(new UpdateHeader("", m.getTkField() + 1, m));


    //Display the end gender and stop timers
    m.appendOutput("\nCounties found: " + countiesFound);
    m.appendOutput("\nCounties not found: " + countiesNotFound);
    // init main parameters
    m.init(true);
  }
}
