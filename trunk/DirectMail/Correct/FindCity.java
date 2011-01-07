/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Correct;

import soldatos.connection.MyDBCConnection;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.Help.Functions.UpdateHeader;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class FindCity implements Runnable {

  private MainForm m;
  private Statement stmt;
  private DmOptions options;
  private int blankCities = 0;
  private int foundCities = 0;

  public FindCity(MainForm m) throws SQLException {
    this.m = m;
    this.options = MainForm.options;
    MyDBCConnection.connect(options.toString(DmOptions.HOST),
        options.toString(DmOptions.DATABASE),
        options.toString(DmOptions.DB_USER),
        options.toString(DmOptions.DB_PASSWORD));
    stmt = MyDBCConnection.getMyConnection().createStatement();

  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      findTheCity();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void findTheCity() throws SQLException, InterruptedException, InvocationTargetException, IOException {
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    String currLineArr[];
    String pSql;
    long start, end;

    start = System.currentTimeMillis();
    m.setOutput("Finding the city");


    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

    //InfoFrame.labelInfo.setText("Getting the gender");
    //Read the file and insert the rows
    m.appendOutput("\nReading the file and \nfinding the city");


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
      String newCity = currLineArr[m.getCityField()].trim();
      if (currLineArr[m.getCityField()].trim().equals("")) {
        blankCities++;
      }
      if (currLineArr[m.getCityField()].trim().equals("")) {
        newCity = soldatos.functions.Elta.findCityByTk(currLineArr[m.getTkField()], stmt);
         if (!newCity.equals("")) {
          foundCities++;
        } else {
          newCity = currLineArr[m.getCityField()].trim();
        }
      }
      currLineArr[m.getCityField()] += m.getDelimeter() + newCity;
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

    EventQueue.invokeAndWait(new UpdateHeader("", m.getCityField() + 1, m));


    //Display the end gender and stop timers
    m.appendOutput("\nCities blank: " + blankCities);
    m.appendOutput("\nCities found: " + foundCities);
    m.appendOutput("\nCities not found: " + (blankCities - foundCities));
    // init main parameters
    m.init(true);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    MainForm.trayIcon.showInfoMessage("Getting Cities",
        "Getting the Cities finished\n" +
        "Cities blank: " + blankCities + "\n" +
        "Cities found : " + foundCities + "\n" +
        "Cities not found : " + (blankCities - foundCities) + "\n" +
        "Execution time : " + DmFunctions.execTime(start, end));

  }
}
