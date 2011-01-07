/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Update;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import soldatos.connection.MyDBCConnection;
import soldatos.connection.MyDBCFunctions;
import soldatos.functions.Elta;
import soldatos.functions.FileFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class UpdateList13 implements Runnable {

  private MainForm m;
  private Statement stmt;
  private int notInserted = 0;
  private long start;
  private long end;
  private int inserted = 0;

  public UpdateList13(MainForm m) {
    this.m = m;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
          MainForm.options.toString(DmOptions.DATABASE),
          MainForm.options.toString(DmOptions.DB_USER),
          MainForm.options.toString(DmOptions.DB_PASSWORD));
      stmt = MyDBCConnection.myConnection.createStatement();
      m.setOutput("Exporting list13 file to the database");
      start = System.currentTimeMillis();
      export();
      end = System.currentTimeMillis();
      m.appendOutput("\nNot inserted: " + notInserted);
      m.appendOutput("\nInserted: " + inserted);
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
      MainForm.glassPane.deactivate();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void export() throws SQLException, FileNotFoundException, UnsupportedEncodingException, IOException {
    String dump = DmOptions._JAR_DIR_ + "tmp/" + m.getOrigFileName();
    BufferedReader in = FileFunctions.createInputStream(dump);
    String line;
    String fullname, fathername, address, city, tk;
    String sql;
    ResultSet rs;
    String data;
    int lines = 0;
    while ((line = in.readLine()) != null) {
      lines++;
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      String[] fields = line.split(";", -1);
      fullname = StringFunctions.capitilize(fields[0], "el", false);
      fathername = StringFunctions.capitilize(fields[1], "el", false);
      address = StringFunctions.capitilize(fields[2], "el", false);
      city = StringFunctions.capitilize(fields[3], "el", false);
      tk = fields[4];

      sql = "SELECT fullname FROM dafermos where fullname ='" + MyDBCFunctions.prepareText(fullname) +
          "' AND fathername ='" + MyDBCFunctions.prepareText(fathername) + "'" +
          " AND INSTR(data,'" + MyDBCFunctions.prepareText(address) + "')";
      rs = stmt.executeQuery(sql);
      if (MyDBCFunctions.getResultSetNumberOfRows(rs) > 0) {
        notInserted++;
      } else {
        System.out.println(sql);
        rs.close();
        inserted++;
        fullname = MyDBCFunctions.prepareText(fullname);
        fathername = MyDBCFunctions.prepareText(fathername);
        data = MyDBCFunctions.prepareText(address+" ‘  " + DmFunctions.prepareTK(tk) + " " + city);
        sql = "INSERT INTO dafermos (fullname, fathername, data)" +
            " VALUES ('"+fullname+"','"+fathername+"','"+data+"')";
        
        stmt.execute(sql);
      }
    }
    m.updateProgress(0);
  }
}
