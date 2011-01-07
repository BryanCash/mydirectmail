/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Help.Components;

import DirectMail.Help.Action;
import DirectMail.Help.Common;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.Tools.File.Import.Forms.ImportExcel;
import DirectMail.Tools.File.PFile;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.iharder.dnd.FileDrop;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.FileFunctions;
import soldatos.functions.StringFunctions;
import soldatos.messages.Messages;

/**
 *
 * @author ssoldatos
 */
public class FileDropListener implements FileDrop.Listener {

  private final MainForm m;

  public FileDropListener(MainForm m) {
    this.m = m;
  }

  @Override
  public void filesDropped(File[] files) {
    if (files.length > 1) {
      for (int i = 0; i < files.length; i++) {
        File file = files[i];
        if (ArrayFunctions.isInArray(FileFunctions.getExtension(file), DmOptions._TEXT_FILES_)) {
          Messages.customError("Merge files", "You can only merge text files");
          return;
        }
      }
      m.action.mergeFiles(m, files);
    } else {
      if (!m.getOpenedFile().equals("")) {
        if (closeCurrentFile(files[0])) {
          Common.closeCurrentFile(m, m.getCurrentUser(), files[0]);
        }
      } else {
        if (ArrayFunctions.isInArray(FileFunctions.getExtension(files[0]), DmOptions._EXCEL_FILES_)) {
          loadExcelFile(files[0]);
        }
 else if (ArrayFunctions.isInArray(FileFunctions.getExtension(files[0]), DmOptions._TEXT_FILES_)) {
          loadTxtFile(files[0]);
        } else {
          Messages.customError("Open file", "You can only open text or excel files");
          return;
        }
      }
    }
  }

  private void loadTxtFile(File file) {
    m.setPFile(new PFile(file, m));
    m.getHeaderTitles().removeAllElements();
    //EventQueue.invokeLater(p);
    Thread t = new Thread(m.getPFile());
    t.start();
    m.revertFileMenuItem.setEnabled(true);
    m.revertToolbar.setEnabled(true);
  }

  private void loadExcelFile(File file) {
    try {
      m.setPFile(new PFile(file, m));
      m.getHeaderTitles().removeAllElements();
      ImportExcel exc = new ImportExcel(m, file.getCanonicalPath());
      //EventQueue.invokeLater(p);
      Thread t = new Thread(exc);
      t.start();
      m.revertFileMenuItem.setEnabled(true);
      m.revertToolbar.setEnabled(true);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
  }

  private boolean closeCurrentFile(File file) {
    int result = JOptionPane.showConfirmDialog(
        null,
        "There's a file already open\nDo you want to close it?",
        "alert",
        JOptionPane.YES_NO_OPTION);
    if (result == JOptionPane.YES_OPTION) {
      return true;
    }
    return false;
  }
}
