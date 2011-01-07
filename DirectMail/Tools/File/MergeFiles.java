/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.File;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import soldatos.exceptions.CheckFileException;
import soldatos.exceptions.DelimeterNotFoundException;
import soldatos.functions.FileFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class MergeFiles implements Runnable {

  private final MainForm m;
  private final File[] filesToMerge;
  private final File save;
  private long start;
  private long end;
  private long totalSize = 0;
  long curSize = 0;
  private int numFields = -1;
  private PrintWriter out;

  /**
   * 
   * @param m
   * @param filesToMerge
   * @param save
   */
  public MergeFiles(MainForm m, File[] filesToMerge, File save) {
    this.m = m;
    this.filesToMerge = filesToMerge;
    this.save = save;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      start = System.currentTimeMillis();
      m.setTextAreaText("");
      m.setOutput("Merging files...");
      out = FileFunctions.createOutputStream(save, false);
      start();
      m.updateProgress(0);
      end = System.currentTimeMillis();
      m.appendOutput("\nFiles were merged to " + save);
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
      MainForm.glassPane.deactivate();
      PFile p = new PFile(save, m);
      Thread t = new Thread(p);
      t.start();
    } catch (CheckFileException ex) {
      out.close();
      save.delete();
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (FileNotFoundException ex) {
      out.close();
      save.delete();
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (UnsupportedEncodingException ex) {
      out.close();
      save.delete();
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (DelimeterNotFoundException ex) {
      out.close();
      save.delete();
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      out.close();
      save.delete();
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
  }

  private void start() throws FileNotFoundException, UnsupportedEncodingException, IOException, DelimeterNotFoundException, CheckFileException {
    for (int i = 0; i < filesToMerge.length; i++) {
      File curFile = filesToMerge[i];
      totalSize += curFile.length();
    }
    for (int i = 0; i < filesToMerge.length; i++) {
      File curFile = filesToMerge[i];
      checkFile(curFile, i);
    }
    out.close();
  }

  private void checkFile(File f, int number) throws FileNotFoundException, UnsupportedEncodingException, IOException, DelimeterNotFoundException, CheckFileException {
    BufferedReader in = FileFunctions.createInputStream(f);
    String line;
    int curLine = 0;
    char del = '~';
    int fields;
    boolean firstLine = true;
     m.appendOutput("\nMerging file: " + f);
    while ((line = in.readLine()) != null) {
      curSize += line.length() + 2;
      int progress = (int) ((curSize * 100) / totalSize);
      m.updateProgress(progress);
      curLine++;
      if (firstLine) {
        del = StringFunctions.findDelimeter(line);
        firstLine = false;
        if (number == 0) {
          numFields = line.split(String.valueOf(del), -1).length;
        }
      }
      fields = line.split(String.valueOf(del), -1).length;
      if (fields != numFields) {
        m.appendOutput("\nWrong Number of fields in file " + f + " and line " + curLine +
            "\nThere should be " + numFields + " but there were found " + fields);
        m.appendOutput("\nAborting...");
        throw new soldatos.exceptions.CheckFileException(
            "\nWrong Number of fields in file " + f + " and line " + curLine +
            "\nThere should be " + numFields + " but there were found " + fields);
      }
      out.print(line + "\r\n");
    }
    in.close();
  }
}
