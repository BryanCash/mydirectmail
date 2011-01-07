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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.connection.MyDBCConnection;
import soldatos.exceptions.ArrayLengthException;
import soldatos.functions.ArrayFunctions;

/**
 *
 * @author ssoldatos
 */
public class GetHolidays implements Runnable {

  private final MainForm m;
  private final String dateFormat;
  private long start, end;
  private int holidaysFound;
  private int holidaysNotFound;
  private java.sql.Statement stmt;

  public GetHolidays(MainForm m, String dateFormat) {
    this.m = m;
    this.dateFormat = dateFormat;

  }

  @Override
  public void run() {
    try {
      start = System.currentTimeMillis();
      MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
          MainForm.options.toString(DmOptions.DATABASE),
          MainForm.options.toString(DmOptions.DB_USER),
          MainForm.options.toString(DmOptions.DB_PASSWORD));
      stmt = MyDBCConnection.getMyConnection().createStatement();

      getTheHoliday();
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (ArrayLengthException ex) {
      Logger.getLogger(GetHolidays.class.getName()).log(Level.SEVERE, null, ex);
    } catch (SQLException ex) {
      Logger.getLogger(GetHolidays.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(GetHolidays.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(GetHolidays.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      Logger.getLogger(GetHolidays.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }


  }

  private void getTheHoliday() throws SQLException, IOException, InterruptedException, InvocationTargetException, ArrayLengthException {
    BufferedReader in;
    PrintWriter output;
    String str, newOutLine;
    String fields[];
    String pSql;

    int addedField = 0;


    m.setOutput("Getting the addressing");

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

    //InfoFrame.labelInfo.setText("Getting the gender");
    //Read the file and insert the rows
    m.appendOutput("\nReading the file and finding the holidays");


    m.setTextAreaText(null);
    int lines = 0;



    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
        //System.out.println(m.delimeter);
      }
      fields = str.split("" + m.getDelimeter(), -1);
      newOutLine = "";
      if (m.getFirstnameField() > -1) {
        fields[m.getFirstnameField()] = getTheHolidays(fields[m.getFirstnameField()]);
        addedField = m.getFirstnameField();
      } else {
        fields[m.getFullnameField()] = getTheHolidays(fields[m.getFullnameField()]);
        addedField = m.getFullnameField();
      }

      newOutLine = ArrayFunctions.join(fields, m.getDelimeter());
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    // close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);


    // Add the gender header

    EventQueue.invokeAndWait(new UpdateHeader("", addedField + 1, m));
    //Display the end gender and stop timers
    m.appendOutput("\nHolidays found: " + holidaysFound);
    m.appendOutput("\nHolidays not found: " + holidaysNotFound);
    // init main parameters
    m.init(true);

  }

  private String getTheHolidays(String firstOrFullname) throws SQLException, ArrayLengthException {
    String holiday = "";
    String name = "", nameR = "";
    int day = 0, month = 0;

    if (m.getFullnameField() > -1) {
      String[] names = firstOrFullname.split(" ");
      name = names[names.length - 1];
    } else if (m.getFirstnameField() > -1) {
      name = firstOrFullname;
    }
    nameR = name.replaceAll("_", " ");
    nameR = nameR.replaceAll("-", " ");
    nameR = nameR.replaceAll("/", " ");
    nameR = nameR.replaceAll("\\\\", " ");
    String[] names = nameR.split(" ", -1);
    ResultSet rs;

    String holidayName = names[0];
    //prepare statements
    String sql = "SELECT Day, Month FROM fnames WHERE CapsOnomastiki = '" + holidayName + "'";


    rs = stmt.executeQuery(sql);
    if (rs.next()) {
      day = rs.getInt(1);
      month = rs.getInt(2);
      holidaysFound++;
      SimpleDateFormat sdf = new SimpleDateFormat(dateFormat,new Locale("el"));
      Calendar c = Calendar.getInstance();
      c.set(2009, month - 1, day);
      Date d = c.getTime();
      holiday = sdf.format(d);
    } else {
      day = 0;
      month = 0;
      holidaysNotFound++;
      holiday = "";
    }

    holiday = soldatos.functions.Calendar.convertGreekDateToGenitive(holiday);

    return firstOrFullname + m.getDelimeter() + holiday;
  }
}
