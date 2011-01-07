/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Help;

import DirectMail.Tools.Column.MoveColumn;
import DirectMail.Help.Components.Errors;
import DirectMail.Help.Functions.CheckConnectionToDB;
import DirectMail.Tools.Column.Filter;
import DirectMail.Help.Forms.Help;
import DirectMail.Help.Forms.About;
import DirectMail.Tools.File.Template;
import DirectMail.Tools.File.PFile;
import DirectMail.Options.Forms.OptionsPane;
import DirectMail.Tools.ZipCode.Forms.SelectLabelQuantities;
import DirectMail.Tools.File.Forms.SelectSamples;
import DirectMail.Tools.File.Export.*;
import DirectMail.Tools.Column.Forms.SelectFilterField;
import DirectMail.Tools.Column.Forms.SelectHolidayFormat;
import DirectMail.Tools.File.Export.Forms.SelectExportDB;
import DirectMail.Tools.Production.Forms.SelectDuplex;
import DirectMail.Tools.File.Forms.SelectSplitFile;
import DirectMail.Tools.Production.Forms.SelectMultiplePrinting;
import DirectMail.Tools.Production.Forms.SelectSplitDouble;
import DirectMail.Tools.Row.Forms.DeleteFromListPanel;
import DirectMail.Tools.Row.Forms.SelectDeleteDupl;
import DirectMail.Tools.Row.Forms.SelectMultiplyLines;
import DirectMail.Tools.Row.Forms.MultisortingForm;
import DirectMail.Tools.Column.Forms.ChooseFiller;
import DirectMail.Tools.Column.Forms.SelectAddField;
import DirectMail.Tools.Column.Forms.SelectSearchReplace;
import DirectMail.Tools.Column.Forms.replaceFromDbForm;
import DirectMail.Tools.Column.Forms.CombineFieldsSelection;
import DirectMail.Tools.Column.Forms.CreateCodelinePanel;
import DirectMail.Tools.Column.Forms.SelectUnique;
import DirectMail.Tools.Column.Forms.SelectCD;
import DirectMail.Tools.Column.Forms.SelectDeleteFields;
import DirectMail.Tools.File.Import.Forms.ImportDb;
import DirectMail.Tools.File.Import.Forms.ImportExcel;
import DirectMail.Tools.Update.UpdateErp;
import DirectMail.Tools.Update.UpdateList13;
import DirectMail.Tools.File.MergeFiles;
import DirectMail.Tools.Column.replaceFromDb;
import DirectMail.Tools.Row.MultiSort;
import DirectMail.Tools.Row.DeleteHeader;
import DirectMail.Tools.Column.SearchReplace;
import DirectMail.Tools.Column.ConvertGreek;
import DirectMail.Tools.Column.Capitilize;
import DirectMail.Tools.Column.AmountConvert;
import DirectMail.Tools.Column.KeepFirstWord;
import DirectMail.Tools.Column.AddTheField;
import DirectMail.Tools.Column.CombineTheFields;
import DirectMail.Tools.Column.DuplicateField;
import DirectMail.Tools.Column.FindUniqueFields;
import DirectMail.Tools.Column.DeleteTheFields;
import DirectMail.Tools.Column.FilterColumn;
import DirectMail.Tools.Column.SortByField;
import DirectMail.Tools.Production.DoubleProduction;
import DirectMail.Tools.CheckFile.Forms.SelectList13;
import DirectMail.Tools.CheckFile.Forms.SelectCheckAddress;
import DirectMail.Post.Forms.SelectReprintFile;
import DirectMail.Pre.Forms.SelectShiftFields;
import DirectMail.Tools.Addressing.Forms.AddressingForm;
import DirectMail.Tools.Addressing.Forms.EditNames;
import DirectMail.Correct.*;
import DirectMail.Main.*;
import DirectMail.Options.*;
import DirectMail.Post.MergeReports;
import DirectMail.Post.NotEnveloped;
import DirectMail.Post.NotEnvelopedIds;
import DirectMail.Pre.*;
import DirectMail.Tools.Addressing.*;
import DirectMail.Tools.CheckFile.CheckControlChrs;
import DirectMail.Tools.CheckFile.CheckFileFields;
import DirectMail.Tools.CheckFile.CheckFileLength;
import DirectMail.Tools.CheckFile.CheckTk;
import DirectMail.Tools.Column.AddRegisteredCode;
import DirectMail.Tools.Column.CreateRandomCode;
import DirectMail.Tools.Column.FormatDecimal;
import DirectMail.Tools.Column.Forms.SelectDecimalFormat;
import DirectMail.Tools.Column.ExtractZipcode;
import DirectMail.Tools.Column.Forms.SelectRandomCode;
import DirectMail.Tools.Column.TrimFields;
import DirectMail.Tools.Production.Forms.MultiplePagesForm;
import DirectMail.Tools.Production.MultiplePages;
import DirectMail.Tools.ZipCode.FormatTK;
import DirectMail.Tools.ZipCode.Forms.TkReport;
import DirectMail.Users.Forms.AddUser;
import DirectMail.Users.Forms.EditUser;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.TableModel;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.FileFunctions;
import soldatos.functions.StringFunctions;
import soldatos.lookaandfeel.MyFont;
import soldatos.lookaandfeel.Skin;
import soldatos.messages.Messages;

/**
 *
 * @author ssoldatos
 */
public class Action {

  Thread t;
  public static boolean interrupt = false;

  boolean beforeRun() {
    return true;
  }

  private void run(final Runnable r) {
    if (MainForm.filters.size() > 0) {
      Errors.FiltersError();
    } else {
      if (beforeRun()) {
        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            t = new Thread(r);
            t.start();
          }
        });
      }
    }
  }

  private void run(final Runnable r, boolean force) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        t = new Thread(r);
        t.start();
      }
    });
  }

  private boolean isOpenFile(MainForm m) {
    if (m.getFields() == 0) {
      Errors.NoOpenedFileError();
      return false;
    } else if (MainForm.filters.size() > 0) {
      Errors.FiltersError();
      return false;
    } else {
      return true;
    }
  }

  public boolean isOpenFile(MainForm m, boolean mess) {
    if (m.getFields() == 0) {
      if (mess) {
        Errors.NoOpenedFileError();
      }
      return false;
    } else {
      return true;
    }
  }

  public void capitalize(final MainForm m, final int col) {
    if (isOpenFile(m)) {
      run(new Capitilize(m, col));
    }
  }

  public void checkList13(final MainForm m) {
    if (isOpenFile(m)) {
      if ((m.getFullnameField() > -1) || (m.getSurnameField() > -1 && m.getFirstnameField() > -1)
          && (m.getAddressField() > -1) || (m.getTkField() > -1)) {
        new SelectList13(m);
      } else {
        Errors.customError("Fields not set", "You haven't marked a field as fullname , or lastname / firstname and address or tk");
      }
    }
  }

  public void findCity(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.getTkField() > -1 && m.getCityField() > -1) {
        try {
          run(new FindCity(m));
        } catch (SQLException ex) {
          MainForm.myLog.log(Level.SEVERE, null, ex);
        }
      } else {
        Errors.customError("No TK or City", "You haven't marked a field as a TK or a city field");
      }
    }
  }

  public void convertGreek(final MainForm m, final int column) {
    run(new ConvertGreek(column, m));
  }

  public void shiftFields(final MainForm m) {
    if (isOpenFile(m)) {
      new SelectShiftFields(m);
    }
  }

  public void searchReplace(final MainForm m, final int selectedColumn) {
    if (MainForm.filters.size() == 0) {
      new SelectSearchReplace(m, selectedColumn);
    } else {
      Errors.FiltersError();
    }

  }

  public void checkDigit(final MainForm m) {
    if (isOpenFile(m)) {
      new SelectCD(m);
    }
  }

  public void lastNameAccusativeAddressing(final MainForm m) throws SQLException {
    if (isOpenFile(m)) {
      if (m.getGenderField() > -1) {
        if (m.getSurnameField() > -1 && m.getFirstnameField() > -1) {
          AddressingForm af = new AddressingForm("Accussative Addressing", m);
          if (af.isOK) {
            run(new AccussativeAddressing(m, af));
          }
        } else {
          Errors.customError("No firstname/surname", "You haven't marked fields as surname/firstname");
        }
      } else {
        Errors.customError("No gender", "You haven't marked a field as a gender field");
      }
    }
  }

  public void popUpSort(final MainForm m, final int selectedColumn, final boolean ascending) {
    try {
      run(new SortByField(selectedColumn, m, ascending));
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
  }

  public void closeFile(MainForm m) {
    Common.closeCurrentFile(m, m.getCurrentUser(), new File("./"));
  }

  public void addColumn(final MainForm m, final int col) {
    run(new AddTheField(col, "", m));
  }

  public void codeline(final MainForm m) {
    if (isOpenFile(m)) {
      new CreateCodelinePanel(m);
    }
  }

  public void correctAddresCity(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.getCityField() != -1 || m.getAddressField() != -1) {
        if (m.getTkField() > -1) {
          run(new CorrectAddressCity(m));
        } else {
          Errors.customError("TK Field not selected", "Tk field is not selected");
        }
      } else {
        Errors.customError("Fields not selected", "Address or city field is not selected");
      }
    }

  }

  public void updateSampleRows(final MainForm m) {
    if (isOpenFile(m)) {
      try {
        if (!m.textField_updateRows.getText().equals("Sample Rows")
            && StringFunctions.isNumber(m.textField_updateRows.getText().replaceAll("-", ""))) {
          final String samples = m.textField_updateRows.getText();
          run(new UpdateSampleRows(m, samples));
          //m.textField_updateRows.setText("Sample Rows");
          m.textField_updateRows.setForeground(Color.LIGHT_GRAY);
        }
      } catch (NumberFormatException ex) {
        Errors.customError("Not a number", "The number of lines should be a numeric value");
      }
    }
  }

  public void deleteFromFile(final MainForm m) {
    if (isOpenFile(m)) {
      new DeleteFromListPanel(m);
    }
  }

  public void exportToDB(final MainForm m) {
    if (isOpenFile(m)) {
      SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          try {
            MainForm.glassPane.activate(null);
            SelectExportDB sedb = new SelectExportDB(m);
            if (sedb.export == true) {
              DbExport exp = new DbExport(m, sedb);
           }
          } catch (SQLException ex) {
            MainForm.myLog.log(Level.SEVERE, "SQL exception while exporting file", ex);
            soldatos.messages.Messages.customError("SQL Error", ex.getMessage());
          } catch (IOException ex) {
            MainForm.myLog.log(Level.SEVERE, "IO exception while exporting file", ex);
            Messages.customError("IO Error", ex.getMessage());
          } finally {
            MainForm.glassPane.deactivate();
            m.progressBar.setIndeterminate(false);
          }
        }
      });
    }
  }

  public void exportToList13(final MainForm m) {
    if (m.getFields() == 0) {
      Errors.NoOpenedFileError();
    } else if (m.getFields() != 7) {
      Errors.customError("Wrong number of fields", "There must be 7 fields in the file");
    } else {
      run(new UpdateList13(m));
    }
  }

  public void keepFirstWord(final MainForm m) {
    run(new KeepFirstWord(m.selectedColumn, m));
  }

  public void correctFirstName(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.getFirstnameField() > -1) {
        run(new CorrectFirstNames(m));
      } else {
        Errors.customError("Firstname Field error", "Firstname field is not selected");
      }
    }
  }

  public void testEnvelopes(final MainForm m) {
    if (isOpenFile(m)) {
      run(new TestEnvelopes(m));
    }
  }

  public void findCounty(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.getTkField() > -1) {
        run(new FindCounty(m));
      } else {
        Errors.NoTkSelectedError();
      }
    }
  }

  public void splitNames(final MainForm m) {
    if (m.getFullnameField() > -1) {
      run(new SplitNames(m));
    } else {
      Errors.customError("No Fullname field", "No fullname field is selected");
    }
  }

  public void sortByNumOfTk(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.getTkField() > -1) {
        run(new SortByNum(m));
      } else {
        Errors.NoTkSelectedError();
      }
    }
  }

  public void unique(final MainForm m) {
    final SelectUnique s = new SelectUnique(m.selectedColumn, m);
    if (s.file != null) {
      run(new FindUniqueFields(m, m.selectedColumn, s.file, s.bg, s.trim));
    }
  }

  public void updateFirstNamesDB(final MainForm m) {
    run(new UpdateFnamesDB(m));
  }

  public void holidays(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.getFullnameField() == -1 && m.getFirstnameField() == -1) {
        Errors.customError("Selected fields needed", "You must select the firstname or the fullname");
      } else {
        new SelectHolidayFormat(m);
      }
    }
  }

  public void doubleSplitted(final MainForm m) {
    if (isOpenFile(m)) {
      new SelectSplitDouble(m);
    }
  }

  public void fillBlankFields(MainForm m) {
    ChooseFiller cf = new ChooseFiller(m.selectedColumn, m);
  }

  public void changeDB(MainForm m) {
    String selHost = (String) m.combobox_database.getSelectedItem();
    String[] hosts = MainForm.options.toArray(DmOptions.HOSTS_LIST);
    for (int i = 0; i < hosts.length; i++) {
      String host = hosts[i];
      String h[] = host.split(",", -1);
      if (h[0].equals(selHost)) {
        String username = h[1];
        String password = h[2];
        MainForm.options.setOption(DmOptions.HOST, h[0]);
        MainForm.options.setOption(DmOptions.DB_USER, username);
        MainForm.options.setOption(DmOptions.DB_PASSWORD, password);
        MainForm.options.save();
        run(new CheckConnectionToDB(m.getDatabases(), m));
        return;
      }
    }

  }

  public void duplicate(final MainForm m) {
    run(new DuplicateField(m.selectedColumn, m));
  }

  public void fillEmptyAddresses(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.getCityField() != -1 && m.getAddressField() != -1) {
        run(new FillEmptyAddresses(m));
      } else {
        Errors.customError("Fields not selected", "Address and city fields are not selected");
      }
    }
  }

  public void mergeReports(final MainForm m) {
    MainForm.glassPane.activate(null);
    JFileChooser fc = new JFileChooser(MainForm.options.toString(DmOptions.HOME_DIR));
    fc.setDialogTitle("Select the report files");
    fc.setDialogType(JFileChooser.OPEN_DIALOG);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setMultiSelectionEnabled(true);
    if (fc.showDialog(null, "OK") == JFileChooser.APPROVE_OPTION) {
      final File[] files = fc.getSelectedFiles();
      if (files.length != 2) {
        MainForm.myLog.log(Level.WARNING, "You must select 2 report files for merging");
        Messages.customError("Cannot merge!!!", "You must select 2 report files for merging");
        MainForm.glassPane.deactivate();
      } else {
        MainForm.options.setOption(DmOptions.HOME_DIR, files[0].getParent());
        run(new MergeReports(m, files));
      }
    } else {
      MainForm.glassPane.deactivate();
    }
  }

  public void trim(final MainForm m) {
    if (MainForm.filters.size() == 0) {
      run(new SearchReplace(m, m.selectedColumn, "^[ \t]+|[ \t]+$", "", true));
    } else {
      Errors.FiltersError();
    }
  }

  public void combineTkCity(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.getTkField() > -1) {
        if (m.getCityField() > -1) {
          final boolean[] first = new boolean[m.getFields()];
          final boolean[] second = new boolean[m.getFields()];
          first[m.getTkField()] = true;
          second[m.getCityField()] = true;
          run(new CombineTheFields(first, second, m, false));
        } else {
          Errors.customError("No city field", "You haven't marked any field as a city field");
        }
      } else {
        Errors.NoTkSelectedError();
      }
    }
  }

  public void convertAmount(final MainForm m) {
    if (m.getAmountField() > -1) {
      run(new AmountConvert(m));
    } else {
      Errors.customError("No amount field is selected", "You must mark a field as amount field.");
    }
  }

  public void deleteDuplicates(final MainForm m) {
    new SelectDeleteDupl(m);
  }

  public void importDB(final MainForm m) {
    run(new ImportDb(m));
  }

  public void importExcel(final MainForm m) {
    run(new ImportExcel(m));
  }

  public void moveColumn(MainForm m, int from, int to) {
    int ans = JOptionPane.showConfirmDialog(null,
        "Move column " + from + " to " + to + "?",
        "Move column",
        JOptionPane.OK_CANCEL_OPTION);
    //ok = 0, canc = 2;
    if (ans == JOptionPane.CANCEL_OPTION) {
      m.sampleTable.moveColumn(to, from);
    } else {
      run(new MoveColumn(m, from, to));
    }
  }

  public void editUser() {
    try {
      run(new EditUser());
    } catch (FileNotFoundException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
  }

  public void addUser() {
    run(new AddUser());
  }

  public void deleteFirstLine(final MainForm m) {
    run(new DeleteHeader(m));
  }

  public void saveFile(final MainForm m) {
    m.setPFile(new PFile("save", "", false, m));
    run(m.getPFile());
  }

  public void openFile(final MainForm m, ActionEvent evt) {
    if (!m.getOrigFileName().equals("")) {
      Common.closeCurrentFile(m, m.getCurrentUser(), evt);
    } else {
      m.setPFile(new PFile("open", "main", false, m));
      m.getHeaderTitles().removeAllElements();
      run(m.getPFile());
      m.revertFileMenuItem.setEnabled(true);
      m.revertToolbar.setEnabled(true);
    }
  }

  public void fixedFile(final MainForm m) {
    if (isOpenFile(m)) {
      run(new FixedFile(m));
    }
  }

  public void notEnveloped(final MainForm m) {
    if (isOpenFile(m)) {
      run(new NotEnveloped(m));
    }
  }

  public void reprintFile(final MainForm m) {
    if (isOpenFile(m)) {
      new SelectReprintFile(m);
    }
  }

  public void notEnvelopedIds(final MainForm m) {
    if (isOpenFile(m)) {
      run(new NotEnvelopedIds(m));
    }
  }

  public void zipCodeFlag(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.getTkField() > 0) {
        if (m.isCombinedTK()) {
          Errors.customError("Error creating TK flag", "You can't create the TK flag when TK and city are combined");
        } else {
          run(new ZipCode(m));
        }
      } else {
        Errors.NoTkSelectedError();
      }
    }
  }

  public void sortByEltaFlag(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.getTkField() > 0 && m.getAddressField() > 0 && m.getCityField() > 0) {
        if (m.isMainConnectionToDB()) {
          try {
            run(new SortByEltaFlag(m.getFields(), m));
          } catch (SQLException ex) {
            MainForm.myLog.log(Level.WARNING, null, ex);
          }
        } else {
          Errors.customError("Error connecting to Database", "There was an error while connecting to the main database");
        }
      } else {
        Errors.NoEltaFieldsSelectedError();
      }
    }
  }

  public void addCounter(final MainForm m) {
    if (isOpenFile(m)) {
      run(new AddCounter(m));
    }
  }

  public void reApplyStyle(final MainForm m) {
    MainForm.options.getOptionsMap();
    Skin.applySkin(MainForm.options.toColor(DmOptions.SKIN_COLOR));
    MyFont.setMyFont(MainForm.options.toString(DmOptions.FONT), MainForm.options.toInt(DmOptions.FONT_SIZE));
    Common.deleteTmp(m);
    m.dispose();
    MainForm.trayIcon.hide();
    try {
      new MainForm(m.getCurrentUser(), null, null);
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

  public void options(final MainForm m) {
    MainForm.glassPane.activate(null);
    new OptionsPane(m);
    MainForm.glassPane.deactivate();
  }

  public void changeDelimeter(final MainForm m) {
    if (isOpenFile(m)) {
      run(new ChangeDelimeter(m));
    }
  }

  public void changeDelimeter(final MainForm m, char del) {
    if (isOpenFile(m)) {
      run(new ChangeDelimeter(m, del));
    }
  }

  public void getGenderNew(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.isMainConnectionToDB()) {
        if (m.getSurnameField() > -1 && m.getFirstnameField() > -1) {
          run(new GetGender(m));
        } else {
          Errors.customError("No firstname /surname fields", "You haven't marked a field as a surname and a firstname field");
        }
      } else {
        Errors.customError("Error connecting to Database", "There was an error while connecting to the names database");
      }

    }
  }

  public void help(final MainForm m) {
    new Help(m);
  }

  public void about(final MainForm m) {
    try {
      new About(m);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
  }

  public void tkReport(final MainForm m) {
    if (isOpenFile(m)) {
      m.saveFileMenuItem.setEnabled(true);
      if (m.getTkField() > 0) {
        if (m.isCombinedTK()) {
          Errors.customError("Error in generating report", "Cannot generate a TK report when the TK and the city are combined");
        } else {
          if (m.isMainConnectionToDB()) {
            try {
              run(new TkReport(m));
            } catch (SQLException ex) {
              MainForm.myLog.log(Level.SEVERE, null, ex);
            }
          } else {
            Errors.customError("Error connecting to Database", "There was an error while connecting to the main database");
          }
        }
      } else {
        Errors.NoTkSelectedError();
      }
    }
  }

  public void lastNameVocativeAddressing(final MainForm m) throws SQLException {
    if (isOpenFile(m)) {
      if (m.isMainConnectionToDB()) {
        // run gender in a new thread
        if (m.getGenderField() > -1) {
          if (m.getSurnameField() > -1 && m.getFirstnameField() > -1) {
            AddressingForm af = new AddressingForm("Vocative Addressing", m);
            if (af.isOK) {
              run(new VocativeAddressing(m, af));
            }
          } else {
            Errors.customError("No surname and firstname", "You haven't marked fields as a surname and a firstname field");
          }
        } else {
          Errors.customError("No gender", "You haven't marked a field as a gender field");
        }
      } else {
        Errors.customError("Error connecting to Database", "There was an error while connecting to the names database");
      }
    }
  }

  public void checkFields(final MainForm m) {
    if (isOpenFile(m)) {
      run(new CheckFileFields(m));
    }
  }

  public void checkLength(final MainForm m) {
    if (isOpenFile(m)) {
      run(new CheckFileLength(m));
    }
  }

  public void breakField(final MainForm m) {
    run(new BreakTheFields(m.selectedColumn, m));
  }

  public void padField(final MainForm m) {
    run(new FixedFile(m.selectedColumn, m));
  }

  public void deleteColumn(final MainForm m) {
    run(new DeleteTheFields(m.selectedColumn, m));
  }

  public void formWindowOpen(final MainForm m) {
    run(new CheckConnectionToDB(m.getDatabases(), m));
  }

  public void createTemplate(final MainForm m) {
    run(new Template("create", m));
  }

  public void applyTemplate(final MainForm m) {
    if (isOpenFile(m)) {
      run(new Template("apply", m));
    }
  }

  public void saveTemplate(final MainForm m) {
    if (isOpenFile(m)) {
      run(new Template("save", m));
    }
  }

  public void multiplyLines(final MainForm m) {
    if (isOpenFile(m)) {
      new SelectMultiplyLines(m);
    }
  }

  public void addField(final MainForm m) {
    if (isOpenFile(m)) {
      new SelectAddField(m);
    }
  }

  public void undo(final MainForm m) {
    m.setUndoStatus(true);
    m.getHeaderTitles().removeAllElements();
    m.setPFile(new PFile("undo", "main", false, m));
    run(m.getPFile());
  }

  public void findTk(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.isCombinedTK()) {
        JOptionPane.showMessageDialog(null,
            "Cannot find TK when it's combined!!!",
            "Error formating TK",
            JOptionPane.ERROR_MESSAGE);

      } else {
        //saveFileMenuItem.setEnabled(true);
        if (m.getTkField() > 0) {
          if (m.isMainConnectionToDB()) {
            //run numoftk in diff thread
            if (m.getAddressField() > -1) {
              if (m.getCityField() > -1) {
                try {
                  run(new FindTK(m));
                } catch (SQLException ex) {
                  MainForm.myLog.log(Level.SEVERE, null, ex);
                }
              } else {
                Errors.customError("No City", "You haven't mark a field as a city field");
              }
            } else {
              Errors.customError("No Address", "You haven't mark a field as an address field");
            }
          } else {
            Errors.customError("Error connecting to Database", "There was an error while connecting to the tk database");
          }
        } else {
          Errors.NoTkSelectedError();
        }
      }
    }
  }

  public void combineFields(final MainForm m) {
    if (isOpenFile(m)) {
      new CombineFieldsSelection(m);
    }
  }

  public void checkAddress(final MainForm m) {
    if (isOpenFile(m)) {
      new SelectCheckAddress(m);
    }
  }

  public void revert(final MainForm m) {
    m.setPFile(new PFile("revert", "main", false, m));
    m.getHeaderTitles().removeAllElements();
    run(m.getPFile());
  }

  public void doubleProduction(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.isMainConnectionToDB()) {
        run(new DoubleProduction(m));
      } else {
        Errors.customError("Error connecting to Database", "There was an error while connecting to the main database");
      }
    }
  }

  public void tkLabelsFromPcadd(final MainForm m) {
    m.setPFile(new PFile("open", "main", false, m));
    run(m.getPFile());
    m.setLabeling(true);
  }

  public void tkLabels(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.getTkField() > 0) {
        if (m.runNumOfTk) {
          new SelectLabelQuantities(m);
        } else {
          JOptionPane.showMessageDialog(null,
              "You haven't sorted the file by Num Of TK",
              "Error -File not sorted",
              JOptionPane.ERROR_MESSAGE);

        }
      } else {
        Errors.NoTkSelectedError();
      }
    }
  }

  public void multiplePrinting(final MainForm m) {
    if (isOpenFile(m)) {
      new SelectMultiplePrinting(m);
    }
  }

  public void lastNameGenitiveAddressing(final MainForm m) throws SQLException {
    if (isOpenFile(m)) {
      if (m.isMainConnectionToDB()) {
        if (m.getGenderField() > -1) {
          if (m.getSurnameField() > -1 && m.getFirstnameField() > -1) {
            AddressingForm af = new AddressingForm("Genitive Addressing", m);
            if (af.isOK) {
              run(new GenitiveAddressing(m, af));
            }
          } else {
            Errors.customError("No firstname/surname", "You haven't marked surname/firstname fields");
          }
        } else {
          Errors.customError("No gender", "You haven't marked a field as a gender field");
        }
      } else {
        Errors.customError("Error connecting to Database", "There was an error while connecting to the names database");
      }
    }
  }

  public void addNames(final MainForm m) {
    try {
      run(new EditNames(m));
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
  }

  public void duplex(final MainForm m) {
    if (isOpenFile(m)) {
      new SelectDuplex(m);
    }
  }

  public void formatTk(final MainForm m) {
    if (isOpenFile(m)) {
      m.saveFileMenuItem.setEnabled(true);
      if (m.isCombinedTK()) {
        JOptionPane.showMessageDialog(null,
            "Cannot format a combined TK!!!",
            "Error formating TK",
            JOptionPane.ERROR_MESSAGE);

      } else {
        if (m.getTkField() > 0) {
          run(new FormatTK(m));
        } else {
          Errors.NoTkSelectedError();
        }
      }
    }
  }

  public void splitFile(final MainForm m) {
    if (isOpenFile(m)) {
      new SelectSplitFile(m);
    }
  }

  public void checkTk(final MainForm m) {
    if (isOpenFile(m)) {
      if (m.getTkField() > 0) {
        try {
          run(new CheckTk(m));
        } catch (SQLException ex) {
          MainForm.myLog.log(Level.SEVERE, null, ex);
        }
      } else {
        Errors.NoTkSelectedError();
      }
    }
  }

  public void checkControlChars(final MainForm m) {
    if (isOpenFile(m)) {
      run(new CheckControlChrs(m));
    }
  }

  public void logs(final MainForm m) {
    final Desktop d = Desktop.getDesktop();
    if (!Desktop.isDesktopSupported()) {
      Messages.customError("Sorry!!!", "Your OS does not support this function");
    } else {
      if (!d.isSupported(Desktop.Action.OPEN)) {
        Messages.customError("Sorry!!!", "Your OS does not support this function");
      } else {
        try {
          d.open(new File(DmOptions._JAR_DIR_ + "direct_mail_0.html"));
        } catch (IOException ex) {
          MainForm.myLog.log(Level.SEVERE, null, ex);
        }
      }
    }
  }

  public void deleteFields(MainForm m) {
    if (isOpenFile(m)) {
      new SelectDeleteFields(m);
    }
  }

  public void updateErp(MainForm m) {
    if (m.isSQLConnected) {
      run(new UpdateErp(m));
    } else {
      Messages.customError("NO Connection", "Couldn't connect to ERP database");
    }
  }

  public void createSamples(MainForm m) {
    new SelectSamples(m);
  }

  public void mergeFiles(MainForm m, File[] filesToMerge) {
    File dir;
    // Choose Merge files
    if(filesToMerge == null || filesToMerge.length ==0){
    JFileChooser mergeFileChooser = new JFileChooser(m.getCurrentDirectory());
    mergeFileChooser.setDialogTitle("Choose files to merge");
    mergeFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
    mergeFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    mergeFileChooser.setMultiSelectionEnabled(true);
    mergeFileChooser.showDialog(m, "Merge");
    dir = mergeFileChooser.getSelectedFiles()[0].getParentFile();
    filesToMerge = mergeFileChooser.getSelectedFiles();
    } else {
      dir = filesToMerge[0].getParentFile();
    }
    //Choose save file
    JFileChooser saveFileChooser = new JFileChooser(dir);
    saveFileChooser.setDialogTitle("Save the merged file");
    saveFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
    saveFileChooser.showDialog(m, "Merge");
    File save = saveFileChooser.getSelectedFile();
    
    MergeFiles merge = new MergeFiles(m, filesToMerge, save);
    run(merge);
  }

  public void filterColumn(MainForm m, int column) {
    int rule = -1;
    String text = "";
    boolean caseSensitive = false;
    boolean remove = false;
    Filter f = MainForm.isColumnFiltered(column);
    if (f == null && column != -1) {
      SelectFilterField sf = new SelectFilterField();
      rule = sf.rule;
      text = sf.text;
      caseSensitive = sf.caseSensitive;
      remove = false;
    } else {
      remove = true;
      rule = SelectFilterField._STARTS_WITH_;
      text = "";
    }
    if (rule != -1) {
      if (!remove) {
        MainForm.filters.add(new Filter(column, rule, text, caseSensitive));
        m.removeFiltersMenuItem.setEnabled(true);
        m.saveFilteredResultsMenuItem.setEnabled(true);
        m.removeFiltersToolbar.setEnabled(true);
      } else {
        MainForm.filters.remove(f);
        if (MainForm.filters.size() == 0) {
          m.removeFiltersMenuItem.setEnabled(false);
          m.saveFilteredResultsMenuItem.setEnabled(false);
          m.removeFiltersToolbar.setEnabled(false);
        }
      }
      run(new FilterColumn(m), true);

    }

  }

  public void saveFilteredResults(MainForm m) throws IOException {
    String output = "";

    JFileChooser sv = new JFileChooser();
    sv.setDialogType(JFileChooser.SAVE_DIALOG);
    sv.setCurrentDirectory(new File(m.getCurrentDirectory()));
    sv.setDialogTitle("Save Filtered Results");
    sv.showDialog(m, "Save");
    if (sv.getSelectedFile() == null) {
      return;
    }
    File f = sv.getSelectedFile();

    TableModel tableModel = m.sampleTable.getModel();
    int rows = tableModel.getRowCount();
    int cols = tableModel.getColumnCount();

    for (int i = 0; i < rows; i++) {
      String val[] = new String[cols];
      for (int j = 0; j < cols; j++) {
        val[j] = String.valueOf(tableModel.getValueAt(i, j));
      }
      output += ArrayFunctions.join(val, m.getDelimeter()) + "\r\n";
    }

    FileFunctions.createFile(f, output);
    Messages.customMessage("File saved", "The filtered results are saved");
  }

  public void replaceFromDb(MainForm m) {
    if (isOpenFile(m)) {
      if (m.isMainConnectionToDB()) {
        replaceFromDbForm rdb = new replaceFromDbForm(m);
        if (!rdb.cancel) {
          replaceFromDb r = new replaceFromDb(m, rdb.database, rdb.table, rdb.joinnedField, rdb.valueField,
              rdb.notFoundValue, rdb.createNewField);
          run(r);
        }
      } else {
        Errors.customError("Error connecting to Database", "There was an error while connecting to the names database");
      }
    }
  }

  public void multiSortingForm(MainForm m) {
    if (isOpenFile(m)) {
      if (m.isMainConnectionToDB()) {
        MultisortingForm mul = new MultisortingForm(m);
        if (!mul.cancel) {
          MultiSort mult = new MultiSort(m, mul.options);
          run(mult);
        }
      } else {
        Errors.customError("Error connecting to Database", "There was an error while connecting to the names database");
      }
    }
  }

  public Action() {
  }

  public void decimalFormat(MainForm m) {
    SelectDecimalFormat s = new SelectDecimalFormat();
    DecimalFormat decFormat = s.decFormat;
    FormatDecimal fd = new FormatDecimal(m,decFormat);
    run(fd);
  }

  public void trimFields(MainForm m) {
    if (isOpenFile(m)) {
      run(new TrimFields(m));
    }
  }

  public void addFlags(MainForm m){
    if (isOpenFile(m)) {
      run(new AddFlags(m));
    }
  }

  public void multiplePages(MainForm m) {
    if (isOpenFile(m)) {
      run(new MultiplePages(m));
    }
  }

  public void addRegisteredCode(MainForm m){
    if (isOpenFile(m)) {
      run(new AddRegisteredCode(m));
    }
  }

  public void extractZipCode(MainForm m) {
    if (isOpenFile(m)) {
      if(m.getAddressField() > -1){
      run(new ExtractZipcode(m));
      } else {
        Errors.customError("Splitting City/Zipcode", "Address field is not selected");
      }
    }
  }

  public void createRandomCode(MainForm m){
    SelectRandomCode sel = new SelectRandomCode();
    if(sel.digits != 0){
       run(new  CreateRandomCode(m,sel.digits,sel.letters));
    }
  }
}
