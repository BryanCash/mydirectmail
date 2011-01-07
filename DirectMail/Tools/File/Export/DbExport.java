/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.File.Export;

import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.Tools.File.Export.Forms.SelectExportDB;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import soldatos.connection.MyDBCConnection;
import soldatos.connection.MyDBCFunctions;
import soldatos.functions.FileFunctions;

/**
 *
 * @author ssoldatos
 */
public class DbExport {

  private final MainForm m;
  private final SelectExportDB sedb;

  public DbExport(MainForm m, SelectExportDB sedb) throws FileNotFoundException, UnsupportedEncodingException, IOException, SQLException {
    this.m = m;
    this.sedb = sedb;
    export();

  }

  private void export() throws FileNotFoundException, UnsupportedEncodingException, IOException, SQLException {
    final String inFile = DmOptions._JAR_DIR_ + "tmp" + "/" + m.getOrigFileName();
    final String dump = DmOptions._JAR_DIR_ + "tmp" + "/dump.txt";
    new File(dump).deleteOnExit();
    BufferedReader in = FileFunctions.createInputStream(inFile);
    PrintWriter out = FileFunctions.createOutputStream(dump, false);
    boolean firstline = true;
    String line = "";
    while ((line = in.readLine()) != null) {
      if (sedb.createColumnNames && firstline) {
        firstline = false;
      } else {
        out.println(line);
      }
    }
    in.close();
    out.close();
    String[] columns = new String[m.getFields()];
    m.progressBar.setIndeterminate(true);
    String database = sedb.database;
    final String table = sedb.tablename;
    String engine = "InnoDB";
    String charset = m.getCharacterSet();
    MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
        database,
        sedb.username,
        sedb.password);
    m.stmt = MyDBCConnection.myConnection.createStatement();
    if (sedb.createColumnNames) {
      line = m.getFirstLine();
      String[] cols = line.split("" + m.getDelimeter(), -1);
      for (int i = 0; i < cols.length; i++) {
        columns[i] = cols[i].replaceAll(" ", "_");
      }
    } else {
      for (int i = 0; i < m.getFields(); i++) {
        columns[i] = m.getHeaderTitles().elementAt(i).toLowerCase().replaceAll(" ", "_");
      }
    }
    if (sedb.combo_tables.getSelectedIndex() == 0)  {
      MyDBCFunctions.createTable(
          m.stmt, database, table, columns, engine, charset, true);
    }
    if(!sedb.append){
      m.stmt.executeUpdate("TRUNCATE " + table);
    }
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        try {
         MyDBCFunctions.loadDataInFile(m.stmt, dump, table, String.valueOf(m.getDelimeter()), "", System.getProperty("line.separator"));
        } catch (SQLException ex) {
          MainForm.myLog.log(Level.SEVERE, null, ex);
          soldatos.messages.Messages.SQLError("Error while loading data in Database");
        }
      }
    });
    soldatos.messages.Messages.customMessage("Table Created", "File was exported in table " + database + "." + table);

  }
  //public DbExpo
}
