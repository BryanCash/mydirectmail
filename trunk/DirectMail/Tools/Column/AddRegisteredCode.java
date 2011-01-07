/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.Tools.Column.Forms.SelectRegisteredCodes;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import soldatos.functions.EncodingFunctions;
import soldatos.functions.FileFunctions;
import soldatos.functions.StringFunctions;
import soldatos.messages.Messages;

/**
 *
 * @author ssoldatos
 */
public class AddRegisteredCode implements Runnable {

  private MainForm m;
  private ArrayList<String> registeredList;
  private long start, end;
  private File registeredCodesFile = null;

  public AddRegisteredCode(MainForm m) {
    this.m = m;
  }

  @Override
  public void run() {
    try {
      start = System.currentTimeMillis();
      m.setOutput("Adding Registered codes");
      MainForm.glassPane.activate(null);
      SelectRegisteredCodes sel = new SelectRegisteredCodes();
      if (sel.way == SelectRegisteredCodes.CANCEL) {
      } else {
        if (sel.way == SelectRegisteredCodes.FILE) {
          registeredList = readFile();
          if (registeredList.size() < m.getCustomers()) {
            soldatos.messages.Messages.customError("Not enough codes", "Registered codes are less than file customers");
          }
        } else if (sel.way == SelectRegisteredCodes.NUMBERS) {
          registeredList = createList(sel.from, sel.to, sel.prefix, sel.suffix);
        }
        commit();
        m.appendOutput("\nRegistered Codes were Added!!!");
        end = System.currentTimeMillis();
        m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
      }
    } catch (InterruptedException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  @SuppressWarnings("unchecked")
  private ArrayList<String> readFile() throws IOException {
    JFileChooser f = new JFileChooser(m.getCurrentDirectory());
    f.setDialogType(JFileChooser.OPEN_DIALOG);
    f.setDialogTitle("Open the registered codes file");
    f.setFileSelectionMode(JFileChooser.FILES_ONLY);
    f.setMultiSelectionEnabled(false);
    f.showOpenDialog(null);
    registeredCodesFile = f.getSelectedFile();
    @SuppressWarnings("unchecked")
    ArrayList<String> regList = FileFunctions.readFileToArrayList(registeredCodesFile.getCanonicalPath(), true);
    return regList;
  }

  private void commit() throws IOException, InterruptedException, InvocationTargetException, Exception {
    BufferedReader in;
    PrintWriter output;
    String str = "";
    int lines = 0;
    StringBuffer remainings = new StringBuffer();

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);
    m.setTextAreaText(null);

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }

      String code = registeredList.get(lines-1).trim();
      if (checkCode(code)) {
        output.println(str + m.getDelimeter() + code);
        if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
          m.appendToSampleArea(str + m.getDelimeter() + code + "\n");
        }
      } else {
        Messages.customError("Wrong code", "Wrong code : " + code);
        throw new Exception("Wrong Code " + code);
      }
    }
    // close files
    in.close();
    output.close();

    // Add the new field header

    EventQueue.invokeAndWait(new UpdateHeader("" + m.getFields(), m.getFields(), m));

    //change to the new tmpfile name
    DmFunctions.swapFiles(m);

    // init main parameters
    m.init(true);

    for (int i = lines; i < registeredList.size()-1; i++) {
      remainings.append(registeredList.get(i)).append("\r\n");
    }
    String remainingsFile;
    if(registeredCodesFile != null){
    remainingsFile = registeredCodesFile.getParent() + "/remaining_codes_" + registeredCodesFile.getName();
    } else {
    remainingsFile = m.getCurrentDirectory() +"/remaining_codes.txt";
    }
    FileFunctions.createFile(remainingsFile, remainings.toString());

  }

  private boolean checkCode(String code) {
    if (code.length() != 13) {
      return false;
    }
    return true;
  }

  private ArrayList<String> createList(long from, long to, String prefix, String suffix) {
    ArrayList<String> regList = new ArrayList<String>();
    for(long i = from ; i < to + 1; i++){
      String number = StringFunctions.padLeft(i,8,"0");
      regList.add(prefix+ number+ EncodingFunctions.mod11DsrxWeighted(number, EncodingFunctions.ELTA_REGISTERED_WEIGHT) +suffix);
    }
    return regList;
  }
}
