/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Update;

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

/**
 *
 * @author ssoldatos
 */
public class UpdateErp implements Runnable {

  private final MainForm m;
  private ArrayList<Customer> customers = new ArrayList<Customer>();
  long start, end;
  /**
   *
   * @param m
   */
  public UpdateErp(MainForm m) {
    this.m = m;

  }

  @Override
  public void run() {
    try {
      m.appendOutput("\nUpdating Customers");
      MainForm.glassPane.activate(null);
      m.progressBar.setIndeterminate(true);
      start = System.currentTimeMillis();
      update();
      end = System.currentTimeMillis();
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
      m.progressBar.setIndeterminate(false);
      m.appendOutput("\nCustomers updated");
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    }
  }

  private void update() throws SQLException {
    MyDBCConnection.createMsSQLConnection();
     MyDBCConnection.connect(
          MainForm.options.toString(DmOptions.SQL_HOST),
          MainForm.options.toString(DmOptions.SQL_DB),
          MainForm.options.toString(DmOptions.SQL_USERNAME),
          MainForm.options.toString(DmOptions.SQL_PASSWORD) );
    Statement stmt = MyDBCConnection.getMyConnection().createStatement();
    String sql = "SELECT vendor, description, vendortype, afm FROM PAvendor WHERE vendor <>''";
    ResultSet rs = stmt.executeQuery(sql);
    while (rs.next()) {
      customers.add(new Customer(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4)));
    }
    rs.close();
    MyDBCConnection.destroy();
    MyDBCConnection.createMySQLConnection();
    MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
        MainForm.options.toString(DmOptions.DATABASE),
        MainForm.options.toString(DmOptions.DB_USER),
        MainForm.options.toString(DmOptions.DB_PASSWORD));
    stmt = MyDBCConnection.getMyConnection().createStatement();
    Iterator<Customer> it = customers.iterator();
    while (it.hasNext()) {
      Customer cust = it.next();
      sql = "INSERT INTO directmail.pavendor (vendor, description, vendortype, afm) " +
          "VALUES ('" + cust.code + "','" + MyDBCFunctions.prepareText(cust.description) + "','" + cust.type + "','"+cust.afm+"') " +
          "ON DUPLICATE KEY UPDATE description = '" +MyDBCFunctions.prepareText(cust.description) + "'" +
          ", afm='" + cust.afm +"'";
      stmt.executeUpdate(sql);
    }
  }

  class Customer {

    String code;
    String description;
    int type;
    String afm;

    Customer(String code, String description, int type, String afm) {
      this.code = code;
      this.description = description;
      this.type = type;
      this.afm = afm;
    }
  }
}
