/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Correct;

import DirectMail.Options.DmOptions;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.connection.MyDBCConnection;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.Elta;

/**
 *
 * @author ssoldatos
 */
public class FillEmptyAddresses implements Runnable {

  private MainForm m;
  private Statement stmt;
  private int addressField;
  private int cityField;
  private long start;
  private long end;
  private int filled = 0;
  private int notFilled = 0;
  private int empty = 0;
  private DmOptions options;

  /**
   * 
   * @param m
   */
  public FillEmptyAddresses(MainForm m) {
    try {
      this.m = m;
      this.options = MainForm.options;
      MyDBCConnection.connect(options.toString(DmOptions.HOST),
          options.toString(DmOptions.DATABASE),
          options.toString(DmOptions.DB_USER),
          options.toString(DmOptions.DB_PASSWORD));stmt = MyDBCConnection.myConnection.createStatement();
      addressField = m.getAddressField();
      cityField = m.getCityField();
    } catch (SQLException ex) {
      Logger.getLogger(FillEmptyAddresses.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      start = System.currentTimeMillis();
      fill();
      // init main parameters
      m.init(true);
      m.appendOutput("");
      end = System.currentTimeMillis();
       m.appendOutput(
          "\n========\n" +
          "Total     :" + m.getCustomers() + "\n" +
          "Empty     :" + empty + "\n" +
          "Filled    :" + filled + "\n" +
          "Not Filled:" + notFilled + "\n" );
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
      MainForm.glassPane.deactivate();
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
  }

  private void fill() throws IOException, SQLException {
    m.setOutput("Filling empty addresses");
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);

    m.setTextAreaText(null);
    String str;
    int lines = 0;
    String[] currLineArr;
    String address;
    String city;
    String newLine;
    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      //System.out.println(m.delimeter);
      currLineArr = str.split("" + m.getDelimeter(), -1);
      address = currLineArr[addressField];
      city = currLineArr[cityField];
      if (address.trim().equals("")) {
        empty++;
        if (!Elta.isCity(city.trim(), stmt)) {
          filled++;
          currLineArr[addressField] = city;
        } else {
          notFilled++;
        }
      }
      newLine = ArrayFunctions.join(currLineArr, m.getDelimeter());
      if (lines <= options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newLine + "\n");
      }
      output.println(newLine);
    }
    // close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
  }
}
