/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.Options.DmOptions;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.connection.MyDBCConnection;
import soldatos.functions.ArrayFunctions;

/**
 *
 * @author ssoldatos
 */
public class replaceFromDb implements Runnable {

  private final MainForm m;
  private final String database;
  private final String table;
  private final String joinnedField;
  private final String valueField;
  private final String notFoundValue;
  private final boolean createNewField;
  private long start;
  private long end;
  private Statement stmt;

  /**
   *
   * @param m
   * @param database
   * @param table
   * @param joinnedField
   * @param valueField
   * @param notFoundValue
   * @param createNewField
   */
  public replaceFromDb(MainForm m, String database, String table, String joinnedField, String valueField, String notFoundValue, boolean createNewField) {
    this.m = m;
    this.database = database;
    this.table = table;
    this.joinnedField = joinnedField;
    this.valueField = valueField;
    this.notFoundValue = notFoundValue;
    this.createNewField = createNewField;
     

  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      start = System.currentTimeMillis();
      m.setOutput("Replacing field value from DB");
      MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
        MainForm.options.toString(DmOptions.DATABASE),
        MainForm.options.toString(DmOptions.DB_USER),
        MainForm.options.toString(DmOptions.DB_PASSWORD));
    stmt = MyDBCConnection.myConnection.createStatement();
      commit();
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  public void commit() throws IOException, InterruptedException, InvocationTargetException, SQLException {
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    int lines = 0;
    String currLineArr[];

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
      if (!createNewField) {
        currLineArr[m.selectedColumn] = getValueFromDb(currLineArr[m.selectedColumn]);
      } else {
        currLineArr[m.selectedColumn] += m.getDelimeter() + getValueFromDb(currLineArr[m.selectedColumn]);
      }

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
    if (createNewField) {
      EventQueue.invokeAndWait(new UpdateHeader("" + (m.selectedColumn), m.selectedColumn + 1, m));
    }

    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
    m.appendOutput("\nField Replaced!!!");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
  }

  private String getValueFromDb(String curValue) throws SQLException {
    String newValue = notFoundValue.equals("original") ? curValue : notFoundValue;
    String sql = "SELECT " + valueField + " FROM " + database + "." + table
        + " WHERE " + joinnedField + " = '" + curValue + "' LIMIT 1";
    ResultSet rs = stmt.executeQuery(sql);
    while(rs.next()){
      return rs.getString(1);
    }
    return newValue;
  }
}

