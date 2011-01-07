/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Row;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.Options.DmOptions;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.connection.MyDBCConnection;
import soldatos.connection.MyDBCFunctions;
import soldatos.functions.ArrayFunctions;

/**
 *
 * @author ssoldatos
 */
public class MultiSort implements Runnable {

  private MainForm m;
  private ArrayList<SortOptions> options;
  private long start;
  private Statement stmt;
  private long end;

  public MultiSort(MainForm m, ArrayList<SortOptions> options) {
    this.m = m;
    this.options = options;

  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      start = System.currentTimeMillis();
      m.setOutput("Replacing field value from DB");
      MyDBCConnection.connect(
          MainForm.options.toString(DmOptions.HOST),
          DmOptions.TMP_DB,
          MainForm.options.toString(DmOptions.DB_USER),
          MainForm.options.toString(DmOptions.DB_PASSWORD));
      stmt = MyDBCConnection.myConnection.createStatement();
      commit();
    } catch (IOException ex) {
     MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void commit() throws IOException, SQLException {

    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    int lines = 0;
    String currLineArr[];
    File bulk = new File(DmOptions._JAR_DIR_+"tmp/" + m.getOrigFileName());
    String[] cols = new String[m.getFields()];
      for (int i = 0; i < m.getFields(); i++) {
        cols[i] = "Field_" + i;
      }
    String table = MainForm.options.toString(DmOptions.DB_PREFIX) + "_multisort" ;
    MyDBCFunctions.createTable(
          stmt, DmOptions.TMP_DB, table, cols, "InnoDB", m.getCharacterSet(), true);
    MyDBCFunctions.loadDataInFile(stmt, bulk, 
        table, String.valueOf(m.getDelimeter()) , "", soldatos.constants.System.LINE_FEED);

    retrieveRecs();
  }

  private void retrieveRecs() throws SQLException {
    BufferedReader in = DmFunctions.createBufferedReader(m);
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);
    String outline = "";
    int lines = 0;
    String order ="";
    for (Iterator<SortOptions> it = options.iterator(); it.hasNext();) {
      SortOptions op = it.next();
      if(!op.dir.equals("CLEAR")){
          order += "Field_" + op.field + " " + op.dir + ", ";
      }
    }
    order = order.substring(0, order.length()-2);
    String sql = "SELECT * FROM " + DmOptions.TMP_DB +
        "." + MainForm.options.toString(DmOptions.DB_PREFIX) +
        "_multisort ORDER BY " + order;
    ResultSet rs = stmt.executeQuery(sql);
    m.setTextAreaText("");
    while (rs.next()) {
      lines++;
      String[] outStr = new String[m.getFields()];
      for (int i = 0; i < m.getFields(); i++) {
        outStr[i] = rs.getString(i+1);
      }
      outline = ArrayFunctions.join(outStr, m.getDelimeter());
      m.appendToCurrentOutput("Lines read : " + lines);
      m.progressBar.setIndeterminate(false);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(outline + "\n");
      }
      output.println(outline);
    }

    m.setCustomers(lines);
    output.close();
    m.updateProgress(0);
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
     m.appendOutput("\nFields Sorted!!!");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
  }
}
