/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Correct;

import DirectMail.Correct.Forms.SelectNameSubstitute;
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
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.connection.MyDBCConnection;
import soldatos.connection.MyDBCFunctions;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class CorrectFirstNames implements Runnable {

  private MainForm m;
  private Statement stmt;
  private long start;
  private long end;
  private int namesRight = 0;
  private int namesCorrected = 0;
  private int namesWrong = 0;
  private boolean dontbother = false;

  /**
   *
   * @param m
   */
  public CorrectFirstNames(MainForm m) {
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
      stmt = MyDBCConnection.myConnection.createStatement();
      start();
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (InterruptedException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void start() throws SQLException, InterruptedException, InvocationTargetException, IOException {
    m.setOutput("Correcting first names");
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);

    m.setTextAreaText(null);
    String str;
    int lines = 0;
    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      //System.out.println(m.delimeter);
      String[] currLineArr = str.split("" + m.getDelimeter(), -1);
      String origName = currLineArr[m.getFirstnameField()];
      currLineArr[m.getFirstnameField()] = correctName(origName);
      String newOutLine = ArrayFunctions.join(currLineArr, m.getDelimeter());
      output.println(newOutLine);
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newOutLine + "\n");
      }
    }
    // close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // move the tk field
    if (m.getTkField() != -1) {
      if (m.getTkField() > m.getGenderField()) {
        m.setTkField(m.getTkField() + 1);
      }
    }

    // Add the name
    EventQueue.invokeAndWait(new UpdateHeader("rename", "Field ", m.getFirstnameField(), m));
    EventQueue.invokeAndWait(new UpdateHeader("FIRSTNAME", m.getFirstnameField() + 1, m));
    m.setFirstnameField(m.getFirstnameField() + 1);
    // init main parameters
    m.init(true);
    m.appendOutput(
        "\n========\n" +
        "Firstnames : \n" +
        "Total      :" + m.getCustomers() + "\n" +
        "Right      :" + namesRight + "\n" +
        "Corrected  :" + namesCorrected + "\n" +
        "Wrong      :" + namesWrong + "\n");
  }

  private String correctName(String origName) throws SQLException {

    String[] names = origName.split("[ \\-&\\.]", -1);

    for (int i = 0; i < names.length && !names[i].trim().equals(""); i++) {
      String sql = "SELECT CapsOnomastiki FROM fnames " +
          "WHERE CapsOnomastiki ='" +
          MyDBCFunctions.prepareText(names[i]) + "' LIMIT 1";
      ResultSet rs = stmt.executeQuery(sql);
      if (rs.next()) {
        namesRight++;
      } else {
        sql = "SELECT right_name FROM names_substitutes WHERE wrong_name ='" +
            MyDBCFunctions.prepareText(names[i]) + "' LIMIT 1";
        rs = stmt.executeQuery(sql);
        if (rs.next()) {
          namesWrong++;
          namesCorrected++;
          names[i] = rs.getString(1);
        } else {
          namesWrong++;
          String namePlain = MyDBCFunctions.prepareText(StringFunctions.translateToPlainGreek(names[i]));
          sql = "SELECT CapsOnomastiki FROM fnames WHERE plainGreek ='" + namePlain + "' LIMIT 1";
          rs = stmt.executeQuery(sql);
          if (rs.next()) {
            namesCorrected++;
            names[i] = rs.getString("CapsOnomastiki");
          } else {
            if (!dontbother && !m.checkbox_dontBotherMe.isSelected()) {
              SelectNameSubstitute sn = new SelectNameSubstitute(names[i]);
              dontbother = sn.dontBother;
              if (sn.correctName != null) {
                namesCorrected++;
                sql = "INSERT INTO names_substitutes (wrong_name,right_name) VALUES " +
                    "('" + MyDBCFunctions.prepareText(names[i]) + "','" + MyDBCFunctions.prepareText(sn.correctName) + "')";
                stmt.executeUpdate(sql);
                names[i] = sn.correctName;
              }
            }
          }
        }
      }
    }
    return origName + m.getDelimeter() + ArrayFunctions.join(names, " ");
  }
}
