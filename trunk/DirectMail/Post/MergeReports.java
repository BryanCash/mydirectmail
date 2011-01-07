/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Post;

import DirectMail.Post.Forms.GetFlagPosition;
import DirectMail.Options.DmOptions;
import DirectMail.Help.Components.WorkingPanel;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import soldatos.functions.FileFunctions;

/**
 *
 * @author ssoldatos
 */
public class MergeReports implements Runnable {

  private MainForm m;
  private File[] files;
  private File mergedFile;
  private ArrayList arrList_0;
  private ArrayList arrList_1;
  private long start;
  private long end;
  private int records;
  private PrintWriter out;
  private int position;

  /**
   * Merges 2 report files
   * @param m The MainForm
   * @param files The report files to merge
   */
  public MergeReports(MainForm m, File[] files) {
    this.m = m;
    this.files = files;
    mergedFile = new File(MainForm.options.toString(DmOptions.HOME_DIR) + "/merged_report.txt");
  }

  @Override
  public void run() {
    start = System.currentTimeMillis();
    try {
      if (readReports()) {
        out = FileFunctions.createOutputStream(mergedFile, false);
        createMergedReport();
      }
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, "Error while reading report files", ex);
    } finally {
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
      out.close();
      MainForm.glassPane.deactivate();
    }
  }

  private void createMergedReport() {
    for (int i = 0; i < arrList_0.size(); i++) {
      m.appendToCurrentOutput("Lines read: " + i);
      m.updateProgress(i * 100 / m.getCustomers());
      if (i % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), i+1, m.getCustomers());
      }
      if(i==0){
       GetFlagPosition g = new  GetFlagPosition(m,true,arrList_0.get(i).toString());
        position = g.position;
      }
      String str = arrList_0.get(i).toString();
      String flag = str.substring(position, position+1);
      if (flag.equals("#")) {
        out.print(str + "\r\n");
      } else {
        out.print(arrList_1.get(i).toString() + "\r\n");
      }
    }
    m.updateProgress(0);

  }

  private boolean readReports() throws IOException {
    m.appendOutput("\nImporting reports");
    WorkingPanel w = new WorkingPanel(m, "Importing Reports", "Please wait while importing reports");
    arrList_0 = FileFunctions.readFileToArrayList(files[0].getCanonicalPath(),false);
    arrList_1 = FileFunctions.readFileToArrayList(files[1].getCanonicalPath(),false);
    w.dispose();
    m.appendOutput("\nReports imported");
    if (arrList_0.size() == arrList_1.size()) {
      m.setCustomers(arrList_0.size());
      m.appendOutput("\nImported " + arrList_0.size() + " records of each report\n" +
          "Creating the merged report");
      return true;
    } else {
      m.appendOutput("\nDifferent number of records in each report :\n" +
          "Report 1: " + arrList_0.size() + "\n" +
          "Report 2: " + arrList_1.size());
    }
    return false;
  }
}
