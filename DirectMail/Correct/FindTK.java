/*
 * FindTK.java
 *
 * Created on 24 Σεπτέμβριος 2007, 7:02 πμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Correct;

import soldatos.exceptions.ArrayLengthException;
import soldatos.connection.MyDBCConnection;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import soldatos.functions.StringFunctions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import soldatos.connection.MyDBCFunctions;
import soldatos.functions.Elta;
import soldatos.functions.FileFunctions;

/**
 *
 * @author ssoldatos
 */
public class FindTK implements Runnable {

  private Statement stmt;
  private int zipRight = 0;
  private int zipWrong = 0;
  private int zipFound = 0;
  private int zipNotFound = 0;
  private int lines = 0;
  private MyDBCConnection mdbc;
  private String store = "";
  private ResultSet rs = null;
  private String newTK = "";
  private String sql = "";
  private String addressNum;
  private String city = "";
  private String address = "";
  private String numberStr = "";
  private int number = 0;
  private String letters = "";
  int roadNumKind = 0;
  private MainForm m;
  private DmOptions options;
  private int checkWay;
  //private PrintWriter notFound;

  /** Creates a new instance of FindTK
   * @param m
   * @throws SQLException
   */
  public FindTK(MainForm m) throws SQLException {
    this.options = MainForm.options;
    this.m = m;
    this.addressNum = "";
    this.numberStr = "";
    this.letters = "";
    MyDBCConnection.connect(options.toString(DmOptions.HOST),
        options.toString(DmOptions.DATABASE),
        options.toString(DmOptions.DB_USER),
        options.toString(DmOptions.DB_PASSWORD));
    this.stmt = MyDBCConnection.myConnection.createStatement();

  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      checkWay = JOptionPane.showOptionDialog(
          null,
          "How to check TK?",
          "Check TK",
          JOptionPane.YES_NO_CANCEL_OPTION,
          JOptionPane.INFORMATION_MESSAGE,
          null,
          new String[]{
          "ELTA Database",
          "As 5digit number",
          "Don't check",
          "Cancel"},
          "Database");
      if (checkWay != 3) {
        commitFindTK();
      }
      MainForm.glassPane.deactivate();
      //notFound.close();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
    //notFound.close();
  }

  private void commitFindTK() throws IOException, ArrayLengthException {
    String currAddressNum = "";
    String currCity = "";
    String currTK = "";
    String currCountry = "";
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    String currLineArr[];
    String pSql;
    long start, end;


    start = System.currentTimeMillis();
    m.setOutput("Finding the TK");

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);
    m.appendOutput("\nReading the file and \nfinding the Zip Codes");
    m.setTextAreaText(null);

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
        ////System.out.println(m.delimeter);
      }
      currLineArr = str.split("" + m.getDelimeter(), -1);
      newOutLine = "";
      for (int i = 0; i < m.getFields(); i++) {
        if (i == m.getAddressField()) {
          currAddressNum = currLineArr[i];
        } else if (i == m.getCityField()) {
          currCity = currLineArr[i];
        } else if (i == m.getTkField()) {
          currTK = currLineArr[i];
        } else if (i == m.getAbroadField()) {
          currCountry = currLineArr[i];
        } else {
          currLineArr[i] = currLineArr[i];
        }
      }
      if (StringFunctions.isGreece(currCountry)) {
        try {
          newTK = findTheTK(currAddressNum, currCity, DmFunctions.prepareTK(currTK));
        } catch (SQLException ex) {
          MainForm.myLog.log(Level.WARNING, "SQL Exception while finding TK", ex);
          newTK = currTK;
        }
      } else {
        newTK = currTK;
      }
      //System.out.println("The New tk = " + newTK);

      for (int i = 0; i < m.getFields(); i++) {
        if (i == m.getTkField()) {
          currLineArr[i] = newTK;
        }
        newOutLine += currLineArr[i] + m.getDelimeter();
      }
      newOutLine = newOutLine.substring(0, newOutLine.length() - 1);

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

    //Write the report
    String rep = "\r\n================\r\n";
    rep += "TK REPORT\r\n";
    rep += "Total Records       :" + StringFunctions.padLeft(m.getCustomers(), 10, " ") + "\r\n";
    rep += "Right Zip Codes     :" + StringFunctions.padLeft(zipRight, 10, " ") + "\r\n";
    rep += "Wrong Zip Codes     :" + StringFunctions.padLeft(zipWrong, 10, " ") + "\r\n";
    rep += "\r\n";
    rep += "Found Zip Codes     :" + StringFunctions.padLeft(zipFound, 10, " ") + "\r\n";
    rep += "Not Found Zip Codes :" + StringFunctions.padLeft(zipNotFound, 10, " ") + "\r\n";

    FileFunctions.createFile(m.getCurrentDirectory() + "/searchReport.txt", rep, true);
    MainForm.myLog.log(Level.INFO, rep);
    //Display the end gender and stop timers
    m.appendOutput("\nFinding TK executed!!!");
    m.appendOutput("\nRight TK: " + zipRight);
    m.appendOutput("\nWrong TK: " + zipWrong);
    m.appendOutput("\nTK found: " + zipFound);
    m.appendOutput("\nTK not found: " + zipNotFound);
    // init main parameters
    m.init(true);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    MainForm.trayIcon.showInfoMessage("Finding TK",
        "Finding TK finished\n"
        + "\nRight TK: " + zipRight
        + "\nWrong TK: " + zipWrong
        + "\nTK found: " + zipFound
        + "\nTK not found: " + zipNotFound + "\n"
        + "Execution time : " + DmFunctions.execTime(start, end));

  }

  private String findTheTK(String addressNum, String city, String TK) throws SQLException, ArrayLengthException {

    city = StringFunctions.convertToUpperCase(city);
    addressNum = StringFunctions.convertToUpperCase(addressNum);
    city = StringFunctions.deleteDoubleSpaces(city);
    addressNum = StringFunctions.deleteDoubleSpaces(addressNum);


    newTK = "";
    breakAddress(addressNum);

    //System.out.println(address + " "+ number + " " + city);
    checkIfTKIsRight(TK, address, number, city);
    if (newTK.equals("")) {
      //check if road is a city
      newTK = "@@@@@";
      zipNotFound++;
    } else {
      if (!newTK.replaceAll(" ", "").equals(TK.replaceAll(" ", "").replaceAll("-", ""))) {
        zipFound++;
      }
    }

    return newTK;


  }

  private void checkIfTKIsRight(String TK, String address, int number, String city) throws SQLException, ArrayLengthException {
    boolean isRight = false;
    TK = TK.replaceAll(" ", "").replaceAll("-", "");
    if (checkWay == 0) {
      isRight = Elta.isTK(TK, stmt);
    } else if(checkWay ==1) {
      isRight = Elta.isTK(TK);
    } else {
      isRight = false;
    }
    if (isRight) {
      newTK = TK;
      zipRight++;
    } else {
      zipWrong++;
      checkIfOneTKInCity(city, address, number);
    }
  }

  private void checkIfOneTKInCity(String city, String address, int number) throws SQLException {

    String cityQ_trans = StringFunctions.translateToPlainGreek(city);
    cityQ_trans = MyDBCFunctions.prepareText(cityQ_trans);
    sql = "SELECT DISTINCT tk FROM elta WHERE city_translated = '" + cityQ_trans + "'";
    rs = stmt.executeQuery(sql);
    int count = MyDBCFunctions.getResultSetNumberOfRows(rs);
    if (count == 1) {
      rs.next();
      newTK = rs.getString(1);
    } else {
      checkIfOneRoadOnly(city, address, number);
    }


  }

  private void checkIfOneRoadOnly(String city, String road, int number) throws SQLException {
    String roadQ_trans = StringFunctions.translateToPlainGreek(road);
    roadQ_trans = MyDBCFunctions.prepareText(roadQ_trans);
    sql = "SELECT DISTINCT tk FROM elta WHERE road_translated = '" + roadQ_trans + "'";

    rs = stmt.executeQuery(sql);
    int count = MyDBCFunctions.getResultSetNumberOfRows(rs);
    if (count == 1) {
      rs.next();
      newTK = rs.getString(1);
    } else {
      checkIfRoadAndCity(city, address, number);
    }
  }

  private void checkIfRoadAndCity(String city, String road, int number) throws SQLException {

    String roadQ_trans = StringFunctions.translateToPlainGreek(road);
    roadQ_trans = MyDBCFunctions.prepareText(roadQ_trans);
    String cityQ_trans = StringFunctions.translateToPlainGreek(city);
    cityQ_trans = MyDBCFunctions.prepareText(cityQ_trans);
    //System.out.println(addressQ+ " " + numberQ +" "+ cityQ);
    sql = makeTheBigQuery(roadQ_trans, number, cityQ_trans, false);

    //System.out.println(sql);

    rs = stmt.executeQuery(sql);
    rs.last();
    if (rs.getRow() == 1) {
      newTK = rs.getString(1);
    } else {
      if (roadQ_trans.length() > 2) {
        sql = makeTheBigQuery(roadQ_trans, number, cityQ_trans, true);
        rs = stmt.executeQuery(sql);
        rs.last();
        if (rs.getRow() == 1) {
          newTK = rs.getString(1);
        }
      }
    }
  }

  private void breakAddress(String addressNum) {
    String addressTmp = "";
    String finalAddress = "";
    String cornersArr[];
    String corners[];
    String addressArr[];
    String finalAddressArr[] = new String[2];
    String currCorner;
    String currAddress = "";
    address = "";
    number = 0;

    // clear -
    addressNum = addressNum.replaceAll("-", " ");
    // break corners to 2 streets
    corners = addressNum.split("&", -1);

    // check if each part has a number and get the road with a number
    for (int i = 0; i < corners.length; i++) {
      cornersArr = corners[i].split(" ", -1);
      for (int j = 0; j < cornersArr.length; j++) {
        currCorner = cornersArr[j];
        if (!currCorner.trim().equals("")) {
          if ((StringFunctions.isNumber(currCorner)) || (StringFunctions.isNumber(currCorner.substring(0, currCorner.length() - 1)))) {
            finalAddress = corners[i].trim();
          }
        }
      }
      if (!finalAddress.equals("")) {
        break;
      } else {
        finalAddress = addressNum.trim();
      }
    }

    addressArr = finalAddress.split(" ", -1);
    // check if each part of the road is string or number
    for (int i = 0; i < addressArr.length; i++) {
      currAddress = addressArr[i];
      if (!currAddress.trim().equals("")) {
        if ((!StringFunctions.isNumber(currAddress)) && (!StringFunctions.isNumber(currAddress.substring(0, currAddress.length() - 1)))) {
          addressTmp += " " + currAddress;
        } else {
          numberStr = currAddress;
        }
      }
    }

    // trim final results
    address = addressTmp.trim();
    numberStr = numberStr.trim();

    try {
      // get rid of the numbers letter
      if (!StringFunctions.isNumber(numberStr) && numberStr.length() > 0) {
        numberStr = numberStr.substring(0, numberStr.length() - 1);
        roadNumKind = 2;
        number = Integer.parseInt(numberStr);
      } else if (numberStr.length() > 0) {
        roadNumKind = Integer.parseInt(numberStr) % 2;
        number = Integer.parseInt(numberStr);
      } else {
        roadNumKind = 0;
        number = 0;
      }
    } catch (NumberFormatException ex) {
      roadNumKind = 0;
      number = 0;
    }


  }

  private String makeTheBigQuery(String roadQ_trans, int number, String cityQ_trans, boolean lazy) {
    String lazySql;

    if (!lazy) {
      lazySql = " road_translated = '" + roadQ_trans + "' AND ";
    } else {
      String lazyRoad = roadQ_trans.substring(0, roadQ_trans.length() - 2);
      lazySql = " INSTR(road_translated , '" + lazyRoad + "') AND ";
    }

    sql = "SELECT DISTINCT tk FROM elta WHERE " + lazySql
        + " city_translated = '" + cityQ_trans + "' AND  ( number_kind = 2  OR "
        + " (number_kind = " + roadNumKind + " AND number_start <= " + number
        + " AND number_end >= " + number + "))";
    return sql;
  }
}
