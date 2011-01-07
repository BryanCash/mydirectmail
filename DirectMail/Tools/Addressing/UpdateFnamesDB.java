/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Addressing;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import soldatos.connection.MyDBCConnection;
import soldatos.connection.MyDBCFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class UpdateFnamesDB implements Runnable {

  private MainForm m;
  private Statement stmt;
  private long start, end;
  private int updates;

  /**
   * 
   * @param m
   */
  public UpdateFnamesDB(MainForm m) {
    this.m = m;
    MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
        MainForm.options.toString(DmOptions.DATABASE),
        MainForm.options.toString(DmOptions.DB_USER),
        MainForm.options.toString(DmOptions.DB_PASSWORD));

  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      start = System.currentTimeMillis();
      m.appendOutput("\nUpdating fnames DB");
      start();
      m.appendOutput("\nDB Updated");
      m.appendOutput("\nRecords Updated: " + updates);
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void start() throws SQLException {
    ArrayList<String> names = new ArrayList<String>();
    stmt = MyDBCConnection.myConnection.createStatement();
    String sql = "SELECT CapsOnomastiki FROM fnames WHERE plainGreek = '' OR plainGreek IS NULL";
    ResultSet rs = stmt.executeQuery(sql);
    int totals = MyDBCFunctions.getResultSetNumberOfRows(rs);
    while (rs.next()) {
      names.add(rs.getString("CapsOnomastiki"));
    }
    for (String name : names) {
      String plain = StringFunctions.translateToPlainGreek(name);
      sql = "UPDATE fnames SET plainGreek = '"+ plain+"' WHERE CapsOnomastiki ='"+name+"'";
      stmt.executeUpdate(sql);
      updates++;
      m.updateProgress(updates * 100 / totals);
      if (updates % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), updates, totals);
        //System.out.println(m.delimeter);
      }
    }
    m.progressBar.setValue(0);
  }
}
