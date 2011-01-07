/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Correct;

import DirectMail.Correct.Forms.SelectCitySubstitute;
import DirectMail.Options.DmOptions;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Help.Functions.UpdateHeader;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Pattern;
import soldatos.connection.MyDBCConnection;
import soldatos.connection.MyDBCFunctions;
import soldatos.exceptions.ArrayLengthException;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.Elta;
import soldatos.functions.FileFunctions;
import soldatos.functions.StringFunctions;
import soldatos.messages.Debug;
import soldatos.tools.CorrectWord;

/**
 *
 * @author ssoldatos
 */
public class CorrectAddressCity implements Runnable {

  private MainForm m;
  private Statement stmt;
  private long start;
  private long end;
  private int addressField;
  private int cityField;
  public static boolean learningMode = true;
  private SelectCitySubstitute s;
  private int citiesCorrected = 0;
  private int citiesWrong = 0;
  private int citiesRight = 0;
  private int roadsCorrected = 0;
  private int roadsWrong = 0;
  private int roadsRight = 0;
  private int c = 0;
  private boolean fixed = false;
  private boolean printCorrect = true;
  private boolean printWrong = true;
  private boolean printRight = true;
  private DmOptions options;
  private int tkField;
  private String[] wordsFound;
  private int cityTime = 0;
  private int addressTime = 0;
  private String cityFlag;
  private String addressFlag;
  private HashMap<String, Report> hash = new HashMap<String, Report>();
  private ArrayList<Report> arrayList = new ArrayList<Report>();
  public String newAddress;

  public CorrectAddressCity(MainForm m) {
    try {
      this.m = m;
      this.options = MainForm.options;
      MyDBCConnection.connect(options.toString(DmOptions.HOST),
          options.toString(DmOptions.DATABASE),
          options.toString(DmOptions.DB_USER),
          options.toString(DmOptions.DB_PASSWORD));
      stmt = MyDBCConnection.myConnection.createStatement();
      addressField = m.getAddressField();
      cityField = m.getCityField();
      tkField = m.getTkField();
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      start = System.currentTimeMillis();
      //EltaFunctions.createEltaTables(stmt);
      correct();
      // init main parameters
      m.init(true);
      String report = "\r\n========\r\n" +
          "Cities        : \r\n" +
          "Total         :" + m.getCustomers() + "\r\n" +
          "Right         :" + citiesRight + "\r\n" +
          "Corrected     :" + citiesCorrected + "\r\n" +
          "Not Corrected :" + citiesWrong + "\r\n" +
          "Time          :" + cityTime + " ms \r\n" +
          "\r\n========\r\n" +
          "Roads         : \r\n" +
          "Total         :" + m.getCustomers() + "\r\n" +
          "Right         :" + roadsRight + "\r\n" +
          "Corrected     :" + roadsCorrected + "\r\n" +
          "Not Corrected :" + roadsWrong + "\r\n" +
          "Time          :" + addressTime + " ms";
      m.appendOutput(report);
      FileFunctions.createFile(m.getCurrentDirectory()+"/searchReport.txt", report,true);
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
      MainForm.glassPane.deactivate();
    } catch (ArrayLengthException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void correct() throws IOException, InterruptedException, InvocationTargetException, SQLException, ArrayLengthException {
    String str, newOutLine = "";
    int lines = 0;
    String[] currLineArr;

    m.setOutput("Correcting addresses and cities");
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);
    //Create cities, addresses files
    PrintWriter cityOut = FileFunctions.createOutputStream(m.getCurrentDirectory() + "/cities_report.txt", false);
    PrintWriter addressOut = FileFunctions.createOutputStream(m.getCurrentDirectory() + "/address_report.txt", false);

    m.setTextAreaText(null);

    while ((str = in.readLine()) != null) {
      cityFlag = "";
      addressFlag = "";
      lines++;
      fixed = false;
      newAddress = "";
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      //System.out.println(m.delimeter);
      currLineArr = str.split("" + m.getDelimeter(), -1);
      String city = "", newCity = "";
      if (cityField != -1) {
        city = currLineArr[cityField].trim();
        Debug.start();
        newCity = getCorrectedCity(city, lines, str, currLineArr[tkField].trim());
        cityTime += Debug.getDuration();
        if (!newAddress.equals("")){
          currLineArr[addressField] = newAddress;
        }
        if (newCity.equals("")) {
          newCity = currLineArr[cityField].trim();
          cityFlag = "Wrong";
        } else if (newCity.equals(city)) {
          cityFlag = "Right";
        } else if (!newCity.equals(city)) {
          cityFlag = "Corrected";
        }
        cityOut.print(lines + "\t" + city + "\t" + cityFlag + "\t" + newCity + "\r\n");
        new Report(1, city, newCity, cityFlag);
        currLineArr[cityField] = currLineArr[cityField].trim() +
            m.getDelimeter() + newCity;
      }
      addressFlag = "";
      if (addressField != -1) {
        Debug.start();
        String address = currLineArr[addressField].trim();
        String newAddress = getCorrectedAddress(currLineArr[addressField].trim(), newCity);
        if (newAddress.equals("")) {
          // correct À≈Ÿ÷ klp
          String address2 = DmFunctions.correctLektikoAddress(currLineArr[addressField].trim());
          if (false) {
            //if (!address2.equals(currLineArr[addressField].trim())) {
            fixed = true;
            newAddress = getCorrectedAddress(
                address2, newCity);
            if (newAddress.equals("")) {
              currLineArr[addressField] = currLineArr[addressField].trim() +
                  m.getDelimeter() + currLineArr[addressField].trim() + m.getDelimeter() + addressFlag;
            } else {
              currLineArr[addressField] = currLineArr[addressField].trim() +
                  m.getDelimeter() + newAddress.trim();
            }
          } else {
            currLineArr[addressField] = currLineArr[addressField].trim() +
                m.getDelimeter() + currLineArr[addressField].trim();
          }
        } else {
          if(newAddress.indexOf("–À¡‘≈…¡") > -1 ){
            if(!isPlateia(currLineArr[addressField].trim())){
              newAddress = newAddress.replaceAll("–À¡‘≈…¡", "");
            }
          }
          currLineArr[addressField] = currLineArr[addressField].trim() +
              m.getDelimeter() + newAddress.trim();
        }
        addressTime += Debug.getDuration();
        addressOut.print(lines + "\t" + address + "\t" + addressFlag + "\t" + newAddress + "\r\n");
        new Report(0, address, newAddress, addressFlag);
      }


      newOutLine = ArrayFunctions.join(currLineArr, m.getDelimeter());
      if (lines <= options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    // Add the new field header

    if (cityField != -1 && addressField == -1) {
      EventQueue.invokeAndWait(new UpdateHeader("rename", "Field ", cityField, m));
      EventQueue.invokeAndWait(new UpdateHeader("CITY", cityField + 1, m));
      m.setCityField(cityField + 1);
    } else if (cityField == -1 && addressField != -1) {
      EventQueue.invokeAndWait(new UpdateHeader("rename", "Field ", addressField, m));
      EventQueue.invokeAndWait(new UpdateHeader("ADDRESS", addressField + 1, m));
      m.setAddressField(addressField + 1);
    } else {
      if (cityField > addressField) {
        EventQueue.invokeAndWait(new UpdateHeader("rename", "Field ", addressField, m));
        EventQueue.invokeAndWait(new UpdateHeader("ADDRESS", addressField + 1, m));
        m.setAddressField(addressField + 1);
        EventQueue.invokeAndWait(new UpdateHeader("rename", "Field ", cityField + 1, m));
        EventQueue.invokeAndWait(new UpdateHeader("CITY", cityField + 2, m));
        m.setCityField(cityField + 2);
      } else {
        EventQueue.invokeAndWait(new UpdateHeader("rename", "Field ", cityField, m));
        EventQueue.invokeAndWait(new UpdateHeader("CITY", cityField + 1, m));
        m.setCityField(cityField + 1);
        EventQueue.invokeAndWait(new UpdateHeader("rename", "Field ", addressField + 1, m));
        EventQueue.invokeAndWait(new UpdateHeader("ADDRESS", addressField + 2, m));
        m.setAddressField(addressField + 2);
      }
    }


    //Append report
    addressOut.print("\r\n\r\n\r\n===============REPORT==================\r\n");
    cityOut.print("\r\n\r\n\r\n===============REPORT==================\r\n");

    Iterator it = hash.keySet().iterator();
    while (it.hasNext()){
      Report r = hash.get((String)it.next());
      arrayList.add(r);
    }
    Collections.sort(arrayList);
    Iterator i = arrayList.iterator();
    while(i.hasNext()){
      Report r = (Report) i.next();
      if (r.type == 0){
        addressOut.print(r.orWord + "\t" + r.state + "\t" + r.newWord + "\t"+r.count+"\r\n");
      } else {
        cityOut.print(r.orWord + "\t" + r.state + "\t" + r.newWord + "\t"+r.count+"\r\n");
      }
    }

    // close files
    in.close();
    output.close();
    cityOut.close();
    addressOut.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);

  }

  private String getCorrectedAddress(String address, String city) throws SQLException {
    //System.out.println("==================================================="+ address );
    address = StringFunctions.deleteDoubleSpaces(address.replaceAll("\\.", " "));
    city = city.replaceAll("\\'", "");
    city = city.replaceAll("\\\\", "");
    address = address.replaceAll("\\'", "");
    address = address.replaceAll("\\\\", "");
    String newAddressGreek = "", newAddressTrans;
    String justAddress = DmFunctions.getAddress(address);
    if (justAddress.equals("") || justAddress.length() < 3 ||
        StringFunctions.isContainingEnglishChar(address) ) {
      roadsWrong++;
      addressFlag = "Wrong";
      return "";
    }
    //newAddressGreek = DmFunctions.correctLektikoAddress(justAddress);
    newAddressGreek = justAddress;
    newAddressTrans = StringFunctions.translateToPlainGreek(newAddressGreek.trim());
    String[] addressArrTrans = StringFunctions.deleteDoubleSpaces(newAddressTrans).trim().split(" ");
    String[] addressArrGreek = StringFunctions.deleteDoubleSpaces(newAddressGreek).trim().split(" ");
    boolean[] found = new boolean[addressArrTrans.length];
    ResultSet rs;
    String sql;
    sql = "SELECT DISTINCT road FROM elta_roads WHERE road_translated = '" +
        MyDBCFunctions.prepareText(newAddressTrans) + "'";
    String tmpSql = sql + "\n";
    rs = stmt.executeQuery(sql);
    int count = MyDBCFunctions.getResultSetNumberOfRows(rs);
    int pos = MyDBCFunctions.findInResultSet(newAddressGreek, rs, 1);
    if (count == 1 || pos > 0) {
      if (count == 1) {
        rs.next();
      } else {
        rs.absolute(pos);
      }
      newAddressGreek = rs.getString(1);
      String newAddress = address.replaceFirst(Pattern.quote(justAddress), newAddressGreek);
      if (newAddress.equals(address) && !fixed) {
        if (printRight) {
          //System.out.println(tmpSql);
          //System.out.println("RIGHT: " + justAddress + " | " + newAddressGreek + " | " + newAddress);
          //System.out.println("\n");
        }
        roadsRight++;
        addressFlag = "Right";
        return newAddress;
      } else {
        if (printCorrect) {
          //System.out.println(tmpSql);
          //System.out.println("CORRE: " + justAddress + " | " + newAddressGreek + " | " + newAddress);
          //System.out.println("\n");
        }
        roadsCorrected++;
        addressFlag = "Corrected";
        return newAddress;
      }
    } else {
    }
    int numberOfWords = 0;
    String where = "FROM";
    String[] arrWhere = new String[addressArrTrans.length];
    for (int i = 0; i < addressArrTrans.length; i++) {
      sql = "SELECT word, word_translated FROM elta_unique_roads " +
          "WHERE word_translated = '" + MyDBCFunctions.prepareText(addressArrTrans[i]) + "'" +
          " AND LENGTH(word)> 2 ";
      tmpSql += sql + "\n";
      rs = stmt.executeQuery(sql);
      count = MyDBCFunctions.getResultSetNumberOfRows(rs);
      pos = MyDBCFunctions.findInResultSet(addressArrGreek[i], rs, 1);
      if (count == 1 || pos > 0) {
        if (count == 1) {
          rs.next();
        } else {
          rs.absolute(pos);
        }
        found[i] = true;
        addressArrGreek[i] = rs.getString(1);
        //numberOfWords++;
        where += " word = '" + MyDBCFunctions.prepareText(addressArrGreek[i]) + "' OR ";
        if (i == 0) {
          arrWhere[i] = "(SELECT DISTINCT road_id FROM elta_roads_index WHERE REPLACE(word,'.','') = '" +
              MyDBCFunctions.prepareText(addressArrGreek[i]) + "')  As w" + i;
        } else {
          arrWhere[i] = "(SELECT DISTINCT road_id FROM elta_roads_index WHERE REPLACE(word,'.','') = '" +
              MyDBCFunctions.prepareText(addressArrGreek[i]) + "')  As w" + i +
              " ON w0.road_id = w" + i + ".road_id";
        }
      } else {
        // Query 4-9 ms
        rs = stmt.executeQuery("SELECT word, word_translated FROM elta_unique_roads WHERE REPLACE(word_translated,'.','') LIKE '" +
            MyDBCFunctions.prepareText(addressArrTrans[i]) + "%'");
        count = MyDBCFunctions.getResultSetNumberOfRows(rs);
        if (count == 1) {
          found[i] = true;
          rs.next();
          addressArrTrans[i] = rs.getString(1);
          addressArrGreek[i] = rs.getString(1);
          if (i == 0) {
            arrWhere[i] = "(SELECT DISTINCT road_id FROM elta_roads_index WHERE REPLACE(word_translated,'.','') = '" +
                MyDBCFunctions.prepareText(addressArrTrans[i]) + "' ) AS w" + i;
          } else {
            arrWhere[i] = "(SELECT DISTINCT road_id FROM elta_roads_index WHERE REPLACE(word_translated,'.','') = '" +
                MyDBCFunctions.prepareText(addressArrTrans[i]) + "' ) AS w" + i +
                " ON w0.road_id = w" + i + ".road_id";
          }

        } else {
          if (i == 0) {
            arrWhere[i] = "(SELECT DISTINCT road_id FROM elta_roads_index WHERE REPLACE(word_translated,'.','') LIKE '" +
                MyDBCFunctions.prepareText(addressArrTrans[i]) + "%' ) AS w" + i;
          } else {
            arrWhere[i] = "(SELECT DISTINCT road_id FROM elta_roads_index WHERE  REPLACE(word_translated,'.','') LIKE '" +
                MyDBCFunctions.prepareText(addressArrTrans[i]) + "%' ) AS w" + i +
                " ON w0.road_id = w" + i + ".road_id";
          }
        }
      }
    }
    where = ArrayFunctions.join(arrWhere, " INNER JOIN ");
    String whereCity = "";
    if (!city.equals("")) {
      String cityTrans = StringFunctions.translateToPlainGreek(MyDBCFunctions.prepareText(city));
      whereCity = " AND city_translated = '" + MyDBCFunctions.prepareText(cityTrans) + "'";
    }

    sql = "SELECT DISTINCT w0.road_id FROM " + where;
    //System.out.println(sql);
    tmpSql += sql + "\n";
    rs = stmt.executeQuery(sql);
    String where_id = " (";
    boolean hasRows = false;
    while (rs.next()) {
      hasRows = true;
      where_id += " id = " + rs.getInt(1) + " OR ";
    }
    where_id += " 1 = 2 )";

    sql = "SELECT DISTINCT road,road_translated FROM elta WHERE " + where_id + " GROUP BY tk";
    tmpSql += sql + "\n";
    rs = stmt.executeQuery(sql);
    count = MyDBCFunctions.getResultSetNumberOfRows(rs);
    if (count == 1) {
      rs.next();
      newAddressGreek = rs.getString(1);
    } else {
      sql = "SELECT DISTINCT road,road_translated FROM elta WHERE " + where_id +
         " " + whereCity + " GROUP BY tk";
      tmpSql += sql + "\n";
      rs = stmt.executeQuery(sql);
      count = MyDBCFunctions.getResultSetNumberOfRows(rs);
      pos = MyDBCFunctions.findInResultSet(ArrayFunctions.join(addressArrTrans, " "), rs, 2);
      if (count == 0) {
      }
      if (count == 1 || pos > 0) {
        if (count == 1) {
          rs.next();
        } else {
          rs.absolute(pos);
        }
        newAddressGreek = rs.getString(1);
      } else {
        newAddressGreek = ArrayFunctions.join(addressArrGreek, " ");
      }
    }
    String newAddress = address;
    newAddress = address.replace(justAddress, newAddressGreek);
    if (newAddress.equals(address) && !fixed) {
      roadsWrong++;
      addressFlag = "Wrong";
      if (printWrong) {
        //System.out.println(tmpSql);
        //System.out.println("WRONG: " + justAddress + " | " + newAddressGreek + " | " + newAddress);
        //System.out.println("\n");
      }
      return "";
    } else {
      if (printCorrect) {
        //System.out.println(tmpSql);
        //System.out.println("CORRE: " + justAddress + " | " + newAddressGreek + " | " + newAddress);
        //System.out.println("\n");
      }
      roadsCorrected++;
      addressFlag = "Corrected";
      return newAddress;
    }
  }

  private String getCorrectedCity(String city, int lineNumber, String line, String tk) throws SQLException, ArrayLengthException {
    city = city.trim();
    city = city.replaceAll("\\'", "");
    city = city.replaceAll("\\\\", "");
    String newCity = city;
    ResultSet rs;
    CharSequence xxx= "XXX";
    CharSequence unknown = "¡√ÕŸ”";
    // MainForm.myLog.log(Level.INFO,"in method");
    if (city.trim().equals("") || city.length() < 3 || city.contains(unknown) ||
        city.contains(xxx) || StringFunctions.isContainingEnglishChar(city)) {
      citiesWrong++;
      return "";
    }
    String sql = "";
    sql = "SELECT DISTINCT city FROM elta_cities WHERE city_translated ='" +
        MyDBCFunctions.prepareText(StringFunctions.translateToPlainGreek(newCity)) + "'";
    rs = stmt.executeQuery(sql);
    if (rs.next()) {
      newCity = rs.getString(1);
      if (!city.equals(newCity)) {
        citiesCorrected++;
      } else {
        citiesRight++;
      }
      return newCity;
    }
    rs.close();

    HashSet similarCities = new HashSet();

    sql = "SELECT DISTINCT CityRight FROM elta_substitutes WHERE CityWrong ='" +
        MyDBCFunctions.prepareText(newCity) + "'";
    rs = stmt.executeQuery(sql);
    if (rs.next()) {
      newCity = rs.getString(1);
      citiesCorrected++;
      return newCity;
    }
    rs.close();

    CorrectWord cc = new CorrectWord();
    cc.setTable("elta_cities");
    cc.setColumn("city");
    cc.setTmpTable(DmOptions.TMP_DB + "." + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmpWords");
    cc.correct(city, stmt);
    wordsFound = cc.wordsFound;
    if (wordsFound.length == 1 &&
        checkTkAlso(wordsFound[0], tk)) {
      newCity = wordsFound[0];
      citiesCorrected++;
      //citiesWrong--;
      return newCity;
    } else {
    }
    String newCityCorrected = DmFunctions.correctLektikoCity(city);
    newCity = newCityCorrected;
    newCity = StringFunctions.translateToPlainGreek(newCity.trim());
    newCity = MyDBCFunctions.prepareText(newCity);
//    sql = "SELECT DISTINCT city, 0 FROM elta_cities WHERE city_translated ='" + newCity + "' UNION " +
//        " SELECT DISTINCT road , 1 FROM elta_roads WHERE road_translated ='" + newCity + "'";
    sql = "SELECT DISTINCT city, city_translated FROM elta_cities WHERE " +
        "city_translated LIKE '" + MyDBCFunctions.prepareText(newCity) + "%'";
    rs = stmt.executeQuery(sql);
    int count = MyDBCFunctions.getResultSetNumberOfRows(rs);
    int pos = MyDBCFunctions.findInResultSet(newCity, rs, 2);
    if (count == 1 || pos > 0) {
      if (count == 1) {
        rs.next();
      } else {
        rs.absolute(pos);
      }

      newCity = rs.getString(1);
      citiesCorrected++;
      return newCity;
    } else {
      if (learningMode) {
        for (int i = 0; i < wordsFound.length; i++) {
          if (!similarCities.contains(wordsFound[i])) {
            similarCities.add(wordsFound[i]);
          }

        }
        if (newCity.length() > 4) {
          String cityClipped = city.substring(0, city.length() - 2);
          String newCityClipped = newCity.substring(0, newCity.length() - 2);
          //newCityClipped = StringFunctions.translateToPlainGreek(newCity.trim());
          cityClipped = MyDBCFunctions.prepareText(cityClipped);
          newCityClipped = MyDBCFunctions.prepareText(newCityClipped);
//          sql = "SELECT DISTINCT city, LEFT(city_translated,1) AS letter FROM elta_cities " +
//              " WHERE INSTR(city_translated,'" + newCity + "') " +
//              " OR ( " +
//              " INSTR(city_translated,SUBSTRING('" + newCityClipped + "' FROM LOCATE(' ', '" + newCityClipped + "'))) " +
//              " AND LOCATE(' ', '" + newCityClipped + "') > 0)" +
//              " UNION SELECT DISTINCT road , LEFT(road_translated,1) AS letter FROM elta_roads " +
//              " WHERE INSTR(road_translated,'" + newCity + "') " +
//              " OR ( " +
//              " INSTR(road_translated,SUBSTRING('" + newCityClipped + "' FROM LOCATE(' ', '" + newCityClipped + "'))) " +
//              " AND LOCATE(' ', '" + newCityClipped + "') > 0)" +
//              " ORDER BY " +
//              " IF(letter = LEFT('" + newCityClipped + "',1),0,1) ASC";
          sql = "SELECT DISTINCT city, LEFT(city_translated,1) AS letter FROM elta_cities " +
              " WHERE INSTR(city_translated,'" + MyDBCFunctions.prepareText(city) + "') " +
              " OR INSTR(city_translated,'" + cityClipped + "') " +
              " OR ( " +
              " INSTR(city_translated,SUBSTRING('" + cityClipped +
              "' FROM LOCATE(' ', '" + cityClipped + "'))) " +
              " AND LOCATE(' ', '" + cityClipped + "') > 0)";
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            if (!similarCities.contains(rs.getString(1))) {
              similarCities.add(rs.getString(1));
            }
          }
          rs.close();
          sql = "SELECT DISTINCT city, LEFT(city_translated,1) AS letter FROM elta_cities " +
              " WHERE INSTR(city_translated,'" + MyDBCFunctions.prepareText(newCity) + "') " +
              " OR INSTR(city_translated,'" + newCityClipped + "') " +
              " OR ( " +
              " INSTR(city_translated,SUBSTRING('" + newCityClipped +
              "' FROM LOCATE(' ', '" + newCityClipped + "'))) " +
              " AND LOCATE(' ', '" + newCityClipped + "') > 0)";
          rs = stmt.executeQuery(sql);
          while (rs.next()) {
            if (!similarCities.contains(rs.getString(1))) {
              similarCities.add(rs.getString(1));
            }
          }

          rs.close();
        }


        Vector similar = new Vector(similarCities);
        s = new SelectCitySubstitute(
            m, true, this, city,
            similar, lineNumber,
            line);
        newAddress = s.getAddress();
        if (s.getSelected().equals("") || s.getSelected().equals("null")) {
          citiesWrong++;
          //System.out.println(city+" "+newCity);
          return "";
        } else {
          if (s.fix) {
            citiesCorrected++;
            return s.getSelected();
          }
          sql = "INSERT INTO elta_substitutes SET CityWrong ='" +
              MyDBCFunctions.prepareText(city) + "', CityRight = '" + s.getSelected() + "'";
          stmt.executeUpdate(sql);
          citiesCorrected++;
          return s.getSelected();
        }
      }
      citiesWrong++;
      //System.out.println(city+" "+newCity);
      return "";
    }


  }

  private boolean checkTkAlso(String foundCity, String orTk) throws SQLException {
    if (orTk.length() < 2) {
      return false;
    }
    orTk = orTk.replaceAll(" ", "");
    String orTkStart = orTk.substring(0, 2);
    String[] newTk = Elta.findZipCodesOfCity(foundCity, stmt);
    for (int i = 0; i < newTk.length; i++) {
      if (newTk[i].startsWith(orTkStart)) {
        return true;
      }
    }
    return false;
  }

  private boolean isPlateia(String address) {
    String[] plateia = {"–.","– ","–À.","–À ","–À¡‘ ","–À¡‘."};
    if(ArrayFunctions.includesArrayField(address, plateia)){
      return true;
    } else {
      return false;
    }
  }


  class Report implements Comparable{
    int count = 1 ;
    // 0:address, 1:city
    int type;
    String orWord;
    String newWord;
    //0:right, 1:corrected, 2:wrong
    String state;

    Report(int type, String orWord, String newWord, String state){
      this.newWord = newWord;
      this.state = state;
      this.type = type;
      this.orWord = orWord;
      if(hash.containsKey(orWord)){
        Report r  = hash.get(orWord);
        r.count++;
        hash.put(orWord, r);
      } else {
        hash.put(orWord, this);
      }

    }

    @Override
    public int compareTo(Object o) {
      Report r = (Report) o;
      if(this.count > r.count){
        return 1;
      }else if(this.count > r.count){
        return 0;
      }
      return -1;
    }
    
  }
}
