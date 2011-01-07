/*
 * CheckConnectionToDB.java
 *
 * Created on 5 Νοέμβριος 2007, 1:38 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Help.Functions;

import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import soldatos.connection.MyDBCConnection;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.swing.ImageIcon;

/**
 *
 * @author ssoldatos
 */
public class CheckConnectionToDB implements Runnable {

  String databases[];
  private MainForm m;
  private DmOptions options;

  /** Creates a new instance of CheckConnectionToDB
   * @param databases
   * @param m 
   */
  public CheckConnectionToDB(String databases[], MainForm m) {
    this.databases = databases;
    this.options = MainForm.options;
    this.m = m;
  }

  @Override
  public void run() {
    MyDBCConnection tmp;
    int errors = 0;
    m.setOutput("Please wait while checking connections.");
    for (int i = 0; i < databases.length; i++) {

      MyDBCConnection.connect(options.toString(DmOptions.HOST),
          options.toString(DmOptions.DATABASE),
          options.toString(DmOptions.DB_USER),
          options.toString(DmOptions.DB_PASSWORD));
      m.appendOutput("\nChecking connection to: " + options.toString(DmOptions.DATABASE));
      if (MyDBCConnection.isConnected) {
        switch (i) {
          case 0:
            //  MainForm.mainDatabaseIcon.setIcon(new ImageIcon(MainForm.mainDatabaseIcon.getClass().getResource("/DirectMail/Images/correct.gif")));
            MainForm.mainHostIcon.setIcon(new ImageIcon(MainForm.mainHostIcon.getClass().getResource("/DirectMail/Images/correct.gif")));
            m.setMainConnectionToDB(true);
            m.mainHostIcon.setToolTipText("Connection to MySQL succeded");
            //   MainForm.mainDatabaseIcon.setText(Options.toString(Options.DATABASE));
            m.appendOutput(" - OK");
            break;

        }
        // MainForm.labelCurrentHost.setText(Options.toString(Options.HOST));
      } else {
        errors++;
        switch (i) {
          case 0:
            // MainForm.mainDatabaseIcon.setIcon(new ImageIcon(MainForm.mainDatabaseIcon.getClass().getResource("/DirectMail/Images/error.gif")));
            MainForm.mainHostIcon.setIcon(new ImageIcon(MainForm.mainHostIcon.getClass().getResource("/DirectMail/Images/error.gif")));
            m.setMainConnectionToDB(false);
            m.mainHostIcon.setToolTipText("Connection to MySQL failed");
            //MainForm.mainDatabaseIcon.setText(Options.toString(Options.DATABASE));
            m.appendOutput(" - ERROR");
            break;

        }
      }
      try {
        MyDBCConnection.destroy();
      } catch (SQLException ex) {
        MainForm.myLog.log(Level.WARNING, null, ex);
      }
    }
    MyDBCConnection.createMsSQLConnection();
    try {
       MyDBCConnection.connect(
          MainForm.options.toString(DmOptions.SQL_HOST),
          MainForm.options.toString(DmOptions.SQL_DB),
          MainForm.options.toString(DmOptions.SQL_USERNAME),
          MainForm.options.toString(DmOptions.SQL_PASSWORD),false);
      SQLConnection(MyDBCConnection.isConnected);
      MyDBCConnection.destroy();
    } catch (ClassNotFoundException ex) {
      SQLConnection(false);
    } catch (SQLException ex) {
      SQLConnection(false);
    }
    
    MyDBCConnection.createMySQLConnection();
    if (errors > 0) {
      // MainForm.fixConnectionsButton.setVisible(true);
    } else {
      // MainForm.fixConnectionsButton.setVisible(false);
    }
    m.appendOutput("\nChecking completed");
  }

  private void SQLConnection(boolean connection) {
    if (connection) {
      m.isSQLConnected = true;
      m.erpIcon.setIcon(new ImageIcon(MainForm.mainHostIcon.getClass().getResource("/DirectMail/Images/correctErp.png")));
      m.erpIcon.setToolTipText("Connection to ERP succeded");
      m.appendOutput("\nConnection to ERP - OK");
    } else {
      m.isSQLConnected = false;
      m.erpIcon.setIcon(new ImageIcon(MainForm.mainHostIcon.getClass().getResource("/DirectMail/Images/errorErp.png")));
      m.erpIcon.setToolTipText("Connection to ERP failed");
      m.appendOutput("\nConnection to ERP - ERROR");
    }
  }
}

