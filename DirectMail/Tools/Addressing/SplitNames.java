/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Addressing;

import DirectMail.Tools.Addressing.Forms.AskForSplitNames;
import DirectMail.Tools.Addressing.Forms.SelectOrder;
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
import javax.swing.JCheckBox;
import soldatos.connection.MyDBCConnection;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.StringFunctions;
import soldatos.functions.Addressing;

/**
 *
 * @author ssoldatos
 */
public class SplitNames implements Runnable {

  private MainForm m;
  private DmOptions options;
  private long start;
  private Statement stmt;
  private long end;
  private int companies, noSurname, noFirstname, splitOK;
  private int order;
  private boolean ask;
  private boolean dontBother;
  public static final int CANCEL = 0;
  public static final int SURNAME_NAME = 1;
  public static final int NAME_SURNAME = 2;
  public static final int UNKNOWN = 3;
  public static final int SURNAME_INDEX = Addressing.SURNAME_INDEX;
  public static final int FIRSTNAME_INDEX = Addressing.FIRSTNAME_INDEX;
  public static final int COMPANY_INDEX = Addressing.COMPANY_INDEX;
  public static final String COMPANY = Addressing.COMPANY;

  /**
   *
   * @param m
   */
  public SplitNames(MainForm m) {
    this.m = m;
    this.options = MainForm.options;
  }

  @Override
  public void run() {
    try {
      SelectOrder so = new SelectOrder();
      order = so.getSelection();
      ask = so.isAsk();
      if (order == CANCEL) {
        MainForm.glassPane.deactivate();
      } else {
        start = System.currentTimeMillis();
        MainForm.glassPane.activate(null);
        MyDBCConnection.connect(options.toString(DmOptions.HOST), options.toString(DmOptions.DATABASE), options.toString(DmOptions.DB_USER), options.toString(DmOptions.DB_PASSWORD));
        stmt = MyDBCConnection.getMyConnection().createStatement();
        splitTheNames();

        end = System.currentTimeMillis();
        m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
        //Display the end gender and stop timers
        m.appendOutput("\nNames Splitted!!!");
        m.appendOutput("\nCompanies: " + companies);
        m.appendOutput("\nSurname / Firstname: " + splitOK);
        m.appendOutput("\nNo Surname: " + noSurname);
        m.appendOutput("\nNo Firstname: " + noFirstname);

        MainForm.trayIcon.showInfoMessage("Splitting fullnames",
            "Names Splitted!!!\n"
            + "Companies: " + companies + "\n"
            + "Surname / Firstname: " + splitOK + "\n"
            + "No Surname: " + noSurname + "\n"
            + "No Firstname: " + noFirstname + "\n"
            + "Execution time : " + DmFunctions.execTime(start, end));
      }
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

  private void splitTheNames() throws InterruptedException, InvocationTargetException, IOException, SQLException {
    BufferedReader in;
    PrintWriter output = null;
    String str, newOutLine;
    String currLineArr[];
    m.setOutput("Spliting the fullname");

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

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
      currLineArr = str.split("" + m.getDelimeter(), -1);
      newOutLine = "";
      if (currLineArr[m.getFullnameField()].indexOf("тгкелавос") > -1) {
        System.out.println("dsd");
      }
      String newNames[] = Addressing.splitNames(currLineArr[m.getFullnameField()], stmt);
      if (!newNames[COMPANY_INDEX].equals("1")
          && order != UNKNOWN
          && (newNames[SURNAME_INDEX].equals("")
          || newNames[FIRSTNAME_INDEX].equals(""))) {
        newNames = correct(newNames);
      }
      if (newNames[SURNAME_INDEX].equals("")
          && newNames[FIRSTNAME_INDEX].equals("")) {
        newNames[SURNAME_INDEX] = currLineArr[m.getFullnameField()];
      }
      updateCounters(newNames);
      currLineArr[m.getFullnameField()] = currLineArr[m.getFullnameField()].trim() + m.getDelimeter() + newNames[0] + m.getDelimeter() + newNames[1];
      newOutLine = ArrayFunctions.join(currLineArr, m.getDelimeter());
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


    // Add the new fields

    EventQueue.invokeAndWait(new UpdateHeader("SURNAME", m.getFullnameField() + 1, m));
    EventQueue.invokeAndWait(new UpdateHeader("FIRST NAME", m.getFullnameField() + 2, m));

    // init main parameters
    m.init(true);
  }

  private void updateCounters(String[] newNames) {
    if (newNames[COMPANY_INDEX].trim().equals(COMPANY)) {
      companies++;
    } else {
      if (newNames[SURNAME_INDEX].trim().equals("")) {
        noSurname++;
      } else if (newNames[FIRSTNAME_INDEX].trim().equals("")) {
        noFirstname++;
      } else {
        splitOK++;
      }
    }
  }

  private String[] correct(String[] newNames) throws SQLException {
    String[] correctNames = new String[3];
    correctNames[SURNAME_INDEX] = newNames[SURNAME_INDEX].trim();
    correctNames[FIRSTNAME_INDEX] = newNames[FIRSTNAME_INDEX].trim();
    correctNames[COMPANY_INDEX] = newNames[COMPANY_INDEX];
    String surname = newNames[SURNAME_INDEX].trim();
    String firstname = newNames[FIRSTNAME_INDEX].trim();

    if (surname.equals("")) {
      String[] firstNames = firstname.split(" ");
      if (firstNames.length == 1) {
        correctNames[SURNAME_INDEX] = "";
        correctNames[FIRSTNAME_INDEX] = firstNames[0];
      } else if (firstNames.length == 2) {
        if (order == SURNAME_NAME) {
          correctNames[SURNAME_INDEX] = firstNames[SURNAME_INDEX];
          correctNames[FIRSTNAME_INDEX] = firstNames[FIRSTNAME_INDEX];
          return correctNames;
        } else if (order == NAME_SURNAME) {
          correctNames[SURNAME_INDEX] = firstNames[FIRSTNAME_INDEX];
          correctNames[FIRSTNAME_INDEX] = firstNames[SURNAME_INDEX];
          return correctNames;
        }
      } else if (firstNames.length > 2) {
        correctNames[SURNAME_INDEX] = "";
        correctNames[FIRSTNAME_INDEX] = "";
        if (order == SURNAME_NAME) {
          correctNames[SURNAME_INDEX] = firstNames[SURNAME_INDEX];
          for (int i = 1; i < firstNames.length; i++) {
            correctNames[FIRSTNAME_INDEX] += firstNames[i] + " ";
          }
          return correctNames;
        } else if (order == NAME_SURNAME) {
          correctNames[SURNAME_INDEX] = firstNames[firstNames.length-1];
          for (int i = 0; i < firstNames.length - 1; i++) {
            correctNames[FIRSTNAME_INDEX] += firstNames[i] + " ";
          }
          return correctNames;
        }
      }
    } else {
      String[] surNames = surname.split(" ");
      if (surNames.length == 1) {
        correctNames[SURNAME_INDEX] = surNames[0];
        correctNames[FIRSTNAME_INDEX] = "";
      } else if (surNames.length == 2) {
        if (order == SURNAME_NAME) {
          correctNames[SURNAME_INDEX] = surNames[SURNAME_INDEX];
          correctNames[FIRSTNAME_INDEX] = surNames[FIRSTNAME_INDEX];
          return correctNames;
        } else if (order == NAME_SURNAME) {
          correctNames[SURNAME_INDEX] = surNames[FIRSTNAME_INDEX];
          correctNames[FIRSTNAME_INDEX] = surNames[SURNAME_INDEX];
          return correctNames;
        }
      } else if (surNames.length > 2) {
        if (StringFunctions.allCharsAreEnglish(surNames[SURNAME_INDEX])) {
          if (order == SURNAME_NAME) {
            correctNames[SURNAME_INDEX] = surNames[SURNAME_INDEX];
            for (int i = 1; i < surNames.length; i++) {
              correctNames[FIRSTNAME_INDEX] += surNames[i] + " ";
            }
            return correctNames;
          } else if (order == NAME_SURNAME) {
            correctNames[SURNAME_INDEX] = surNames[surNames.length-1];
            for (int i = 0; i < surNames.length - 1; i++) {
              correctNames[FIRSTNAME_INDEX] += surNames[i] + " ";
            }
            return correctNames;
          }
        }
      }
    }




    JCheckBox[] checks;
    ResultSet rs;

    if (ask && !dontBother && !m.checkbox_dontBotherMe.isSelected()) {
      AskForSplitNames a = new AskForSplitNames(newNames);
      correctNames[1] = "";
      correctNames[0] = "";
      if ((checks = a.getChecks()) != null) {
        if (a.isCompany()) {
          for (int i = 0; i < checks.length; i++) {
            JCheckBox jCheckBox = checks[i];
            if (jCheckBox.isSelected()) {
              String company = jCheckBox.getText();
              stmt.execute("INSERT INTO companies (word) VALUES ('" + company.trim() + "') "
                  + "ON DUPLICATE KEY UPDATE word = word");
            }
          }
          correctNames[0] = newNames[0].trim().equals("") ? newNames[1] : newNames[0];
          correctNames[2] = "1";
        } else {
          for (int i = 0; i < checks.length; i++) {
            JCheckBox jCheckBox = checks[i];
            if (jCheckBox.isSelected()) {
              String name = jCheckBox.getText();
              correctNames[1] += name + " ";
              rs = stmt.executeQuery("SELECT CapsOnomastiki FROM fnames WHERE CapsOnomastiki = '" + name.trim() + "'");
              if (!rs.next()) {
                stmt.execute("INSERT INTO fnames (CapsOnomastiki) VALUES ('" + name.trim() + "')");
              }
            } else {
              correctNames[0] += jCheckBox.getText() + " ";
            }
          }
        }
      } else {
        if (a.isCompany()) {
          correctNames[0] = newNames[0].trim().equals("") ? newNames[1] : newNames[0];
          correctNames[2] = "1";
        }
      }
      dontBother = a.isDontBother();
    }
    return correctNames;
  }
}
