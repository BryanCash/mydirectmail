/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Help;

import DirectMail.Help.Components.MyTableHeaderRenderer;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Help.Functions.UpdateSampleTable;
import DirectMail.Help.Components.Users;
import DirectMail.Options.DmOptions;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.TableColumn;
import soldatos.constants.OptionTypes;
import soldatos.functions.FileFunctions;
import soldatos.messages.Messages;

/**
 *
 * @author ssoldatos
 */
public class Common {

  public static void appendToCurrentOutput(MainForm m, String string) {
    if (MainForm.options.toBoolean(DmOptions.SHOW_OUTPUT_PROGRESS)) {
      if (m.getCurrent) {
        m.currentText = m.textAreaOutput.getText();
      }
      m.setOutput(m.currentText + "\n" + string);
      m.getCurrent = false;
    }
    m.textAreaOutput.setCaretPosition(m.textAreaOutput.getText().length());
  }

  public static void appendOutput(MainForm m, String message) {
    String logMess = message.replaceFirst("\n", "");
    logMess = message.replaceAll("\n", " - ");
    MainForm.myLog.log(Level.INFO, logMess);
    m.textAreaOutput.append(message);
    m.textAreaOutput.setCaretPosition(m.textAreaOutput.getText().length());
  }

  public static void setOutput(MainForm m, String message) {
    String logMess = message.replaceFirst("\n", "");
    logMess = message.replaceAll("\n", " - ");
    MainForm.myLog.log(Level.INFO, logMess);
    m.textAreaOutput.setText(message);
    m.getCurrent = true;
    m.textAreaOutput.setCaretPosition(m.textAreaOutput.getText().length());
  }

  public static void deleteTmp(MainForm m) {
    File dir, filesArr[];
    dir = new File(DmOptions._JAR_DIR_+"tmp/");
    filesArr = dir.listFiles();
    for (int i = 0; i < filesArr.length; i++) {
      if (filesArr[i].getName().startsWith("tmp_") || filesArr[i].getName().startsWith("undo_")) {
        if (filesArr[i].delete()) {
        } else {
        }
      }
    }
  }

  public static void restartProgram(MainForm m) {
    m.stmt = null;
    m.dispose();
    //delete all temp files

    Common.deleteTmp(m);

    // get some options
    MainForm.options.setOption(DmOptions.DIVIDER_LOCATION, m.splitPane_main.getDividerLocation());
    MainForm.options.setOption(DmOptions.LAST_OPENED_FILE, OptionTypes.STRING, m.getOpenedFile());

    // Write the options file
    MainForm.options.save();
  }

  public static void closeProgram(MainForm m) throws IOException {
    Common.restartProgram(m);
    MainForm.myLog.log(Level.INFO, "Application Ending");
    System.exit(0);
  }

  public static synchronized void rearrangeHeaders(MainForm m) {
    try{
    unMarkFields(m);
    for (int i = 0; i < m.getFields(); i++) {
      m.getColModel().getColumn(i).setHeaderValue(m.getHeaderTitles().elementAt(i));
      if (m.getHeaderTitles().elementAt(i).equals("ZIP CODE")) {
        m.setTkField(i);
      } else if (m.getHeaderTitles().elementAt(i).equals("CITY")) {
        m.setCityField(i);
      } else if (m.getHeaderTitles().elementAt(i).equals("FULLNAME")) {
        m.setFullnameField(i);
      } else if (m.getHeaderTitles().elementAt(i).equals("FIRST NAME")) {
        m.setFirstnameField(i);
      } else if (m.getHeaderTitles().elementAt(i).equals("SURNAME")) {
        m.setSurnameField(i);
      } else if (m.getHeaderTitles().elementAt(i).equals("GENDER")) {
        m.setGenderField(i);
      } else if (m.getHeaderTitles().elementAt(i).equals("ADDRESS")) {
        m.setAddressField(i);
      } else if (m.getHeaderTitles().elementAt(i).equals("AMOUNT")) {
        m.setAmountField(i);
      } else if (m.getHeaderTitles().elementAt(i).equals("COUNTRY")) {
        m.setAbroadField(i);
      } else if (m.getHeaderTitles().elementAt(i).equals("NUM OF TK")) {
      } else if (m.getHeaderTitles().elementAt(i).equals("ZIP CODE FLAG")) {
      } else if (m.getHeaderTitles().elementAt(i).equals("REP")) {
      } else if (m.getHeaderTitles().elementAt(i).equals("COUNTER")) {
        m.setCounterField(i);
      } else if (m.getHeaderTitles().elementAt(i).equals("ADDRESSING")) {
      } else if (m.getHeaderTitles().elementAt(i).equals("CUST ID")) {
      } else if (m.getHeaderTitles().elementAt(i).equals("ORDER CODE")) {
      } else if (m.getHeaderTitles().elementAt(i).equals("FLAGS")) {
      } else if (m.getHeaderTitles().elementAt(i).equals("ELTA FLAG")) {
        m.setIsTKRightField(i);
      } else if (m.getHeaderTitles().elementAt(i).equals("CUSTOMER CODE")) {
        m.setCustomerCodeField(i);
      } else {
        m.getColModel().getColumn(i).setHeaderValue(i);
        m.getHeaderTitles().setElementAt(String.valueOf(i), i);
      }

    }
    m.sampleTable.getTableHeader().resizeAndRepaint();
    Common.renderSampleTableHeaders(m);
    }catch (ArrayIndexOutOfBoundsException ex){
      
    }
  }

  public static void unMarkFields(MainForm m) {
    m.setTkField(-1);
    m.setCityField(-1);
    m.setFullnameField(-1);
    m.setFirstnameField(-1);
    m.setSurnameField(-1);
    m.setGenderField(-1);
    m.setAddressField(-1);
    m.setAmountField(-1);
    m.setAbroadField(-1);
    m.setIsTKRightField(-1);
  }

  public static void adjustSampleTable(MainForm m, boolean forceAdjustment) {
    if (!forceAdjustment) {
      if (m.sampleTable.getWidth() > m.sampleTablePane.getWidth()) {
        m.sampleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      } else {
        m.sampleTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      }

    } else {
      if (m.sampleTable.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF) {
        m.sampleTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
      } else {
        if (m.getFields() > 10) {
          m.sampleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
          m.sampleTable.setPreferredSize(new Dimension(
              m.sampleTablePane.getWidth() + 300, m.sampleTable.getHeight()));
        }

      }
    }
  }

  public static void renderSampleTableHeaders(final MainForm m) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        try {
          Thread.sleep(200);
        } catch (InterruptedException ex) {
          Logger.getLogger(Common.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < m.getFields(); i++) {
          TableColumn col = m.sampleTable.getColumnModel().getColumn(i);
          String title = m.getHeaderTitles().elementAt(i);
          col.setHeaderRenderer(new MyTableHeaderRenderer(m, title, i));
        }

      }
    });
  }

  public static void appendToSampleArea(MainForm m, String string) {
    try {
      UpdateSampleTable up = new UpdateSampleTable(m, string);
      EventQueue.invokeAndWait(up);
    } catch (InterruptedException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }

  }

  public static void setTextAreaText(MainForm m, String text) {
    String lineArr[], FieldArr[];
    char delShown;
    // Clear the table
    m.model.setRowCount(0);
    m.getSampleText().clear();
    if (text != null) {
      if (text.length() > 0) {
        lineArr = text.split("\n", -1);
        for (int i = 0; i < lineArr.length; i++) {
          delShown = DmFunctions.findDelimeter(m, lineArr[i]);
          FieldArr = lineArr[i].split("" + delShown, -1);
          m.getSampleText().addElement(FieldArr);
        }
      }
    }
  }

  public static void closeCurrentFile(MainForm m, Users users, File fileToLoad) {
    try {
      Common.deleteTmp(m);
      m.dispose();
      MainForm.trayIcon.hide();
      new MainForm(users, fileToLoad, null);
    } catch (ClassNotFoundException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (UnsupportedLookAndFeelException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (AWTException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
  }

  public static void closeCurrentFile(MainForm m, Users users, ActionEvent evt) {
    try {
      //deleteTmp();
      //dropTmpTables();
      //setVisible(false);
      //dispose();
      Common.restartProgram(m);
      MainForm.trayIcon.hide();
      new MainForm(users, null, evt);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (AWTException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (UnsupportedLookAndFeelException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }

  }

  public static String checkForUpdates(MainForm m) throws FileNotFoundException, UnsupportedEncodingException, IOException, Exception {
    String path = m.updatesPath;
    File d = new File(path);
    String changes = "\n";
    if (d.isDirectory()) {
      String listDirs[] = d.list();
      for (int i = 0; i < listDirs.length; i++) {
        String dir = listDirs[i];
        if (new File(path + "/" + dir).isDirectory() && dir.startsWith("DirectMail")) {
          try {
            m.updatedVersion = dir.split(" ")[1];
          } catch (ArrayIndexOutOfBoundsException ex) {
          }
          if (m.getFullVersion().compareTo(m.updatedVersion) < 0) {
            if (!MainForm.options.toBoolean(DmOptions.UPDATE)) {
              if (new File(path + "/" + dir + "/changes.txt").isFile()) {
                BufferedReader in = FileFunctions.createInputStream(path + "/" + dir + "/changes.txt");
                String line;
                while ((line = in.readLine()) != null) {
                  changes += line + "\n";
                }
              }
              Messages.customMessage("Direct Mail Update",
                  "An Update of Direct Mail is available\n" +
                  "Current Version:  " + m.getFullVersion() + "\n" +
                  "Updated Version: " + m.updatedVersion + "\n" +
                  "\nChanges:" +
                  changes);
              MainForm.options.setOption(DmOptions.UPDATE, "true");
              MainForm.options.save();
              MainForm.options.loadOptions();
            }
            return changes;
          }

        }
      }
      MainForm.options.setOption(DmOptions.UPDATE, "false");
      MainForm.options.save();
      MainForm.options.loadOptions();
    } else {
    }
    return null;
  }

   public static Image getFDImage() {
    java.net.URL imgURL = MainForm.class.getResource("/DirectMail/Images/directMail.png");
    if (imgURL != null) {
      return new ImageIcon(imgURL).getImage();
    } else {
      return null;
    }

  }
}
