/*
 * CheckFileFields.java
 *
 * Created on 3 Αύγουστος 2007, 1:41 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.CheckFile;

import soldatos.connection.MyDBCConnection;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import soldatos.connection.MyDBCFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class List13 implements Runnable {

  private MainForm m;
  private BufferedReader in;
  private PrintWriter output;
  private MyDBCConnection mdbc;
  private Statement stmt;
  private ResultSet rs;
  private int excludes = 0;
  private PrintWriter dafermosOut;
  private boolean checkTK;
  private boolean checkAddress;

  /** Creates a new instance of CheckFileFields
   * @param m
   * @param checkAddress
   * @param checkTk
   * @throws SQLException
   * @throws IOException
   */
  public List13(MainForm m, boolean checkAddress, boolean checkTk) throws SQLException, IOException {
    this.m = m;
    this.checkAddress = checkAddress;
    this.checkTK = checkTk;
    in = DmFunctions.createBufferedReader(m);
    output = DmFunctions.createPrinterWriter(m, in);
    dafermosOut = soldatos.functions.FileFunctions.createOutputStream(m.getCurrentDirectory() + "/excludes.txt", false);
    MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
        MainForm.options.toString(DmOptions.DATABASE),
        MainForm.options.toString(DmOptions.DB_USER),
        MainForm.options.toString(DmOptions.DB_PASSWORD));
    stmt = MyDBCConnection.myConnection.createStatement();
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      checkList13();
      MainForm.glassPane.deactivate();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }


  }

  private void checkList13() throws IOException, SQLException {
    int lines = 0, curLength;
    int length = m.getLineLength();
    String str;
    String currLineArr[];
    long start, end;

    start = System.currentTimeMillis();
    m.setOutput("Checking the file for names in List 13");
    String[] fields;
    while ((str = in.readLine()) != null) {
      String curFullname = "";
      String curSurname = "";
      String curFirstName = "";
      String curAddress = "";
      String curTk = "";
      Boolean isInList = false;
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      fields = str.split(String.valueOf(m.getDelimeter()), -1);
      for (int i = 0; i < fields.length; i++) {
        if (m.getFullnameField() == i) {
          curFullname = fields[i];
        } else if (m.getSurnameField() == i) {
          curSurname = fields[i];
        } else if (m.getFirstnameField() == i) {
          curFirstName = fields[i];
        } else if (m.getAddressField() == i) {
          curAddress = fields[i];
        } else if (m.getTkField() == i) {
          curTk = DmFunctions.prepareTK(fields[i]);
        }
      }

      isInList = checkList(curFullname, curSurname, curFirstName, curAddress, curTk);

      if (!isInList) {
        output.println(str);
      } else {
        dafermosOut.println(str);
      }
    }
    m.updateProgress(0);
    m.appendOutput("\nFile checked!!!");
    m.appendOutput("\nThere where " + excludes + " customers excludes");
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    in.close();
    output.close();
    dafermosOut.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
    m.setCustomers(lines - excludes);
  }

  private Boolean checkList(String curFullname, String curSurname,
      String curFirstName, String curAddress, String curTk) throws SQLException {
    String surname = "";
    String firstname = "";
    if (curFullname.equals("")) {
      surname = curSurname.trim();
      try {
        firstname = curFirstName.trim().substring(0, 3);
      } catch (StringIndexOutOfBoundsException ex) {
        firstname = curFirstName.trim();
      }
    } else {
      curFullname = curFullname.replaceAll("-", " ").replaceAll("_", " ");
      String[] names = curFullname.split(" ", -1);
      surname = names[0].trim();
      if (names.length < 2) {
      } else {
        try {
          firstname = names[1].trim().substring(0, 3);
        } catch (IndexOutOfBoundsException ex) {
          firstname = names[1].trim();
        }
      }
    }
    String fullName = surname + " " + firstname;
    fullName = MyDBCFunctions.prepareText(fullName);
    int nameLength = fullName.length();
    String data = curAddress + " ΤΚ " + curTk.replaceAll(" ", curTk);


    curTk = StringFunctions.deleteDoubleSpaces(curTk).trim();
    curAddress = StringFunctions.deleteDoubleSpaces(curAddress).trim().replaceAll("'", "");
    curAddress = MyDBCFunctions.prepareText(curAddress);
    int dataLength = data.length();

//                String sql = "SELECT * FROM dafermos WHERE LEFT(fullname,"+nameLength+") = '" + fullName + "' " +
//                " AND LEFT(data,"+dataLength+") = '" + data + "'";


    String addressSql = "";
    String tkSql = "";
    if (checkTK) {
      tkSql = "AND INSTR(data,'" + curTk + "')";
    }
    if (checkAddress) {
      addressSql = "AND INSTR(data,'" + curAddress + "')";
    }
    String sql = "SELECT * FROM dafermos WHERE LEFT(fullname," + nameLength + ") = '" +
        fullName + "' " + tkSql + addressSql;
    //System.out.println(sql);

    rs = stmt.executeQuery(sql);

    if (rs.next()) {
      excludes++;
      return true;
    }
    return false;
  }
}
