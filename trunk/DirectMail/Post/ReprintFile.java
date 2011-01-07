/*
 * ReprintFile.java
 *
 * Created on 2 Αύγουστος 2007, 1:21 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Post;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.*;
import DirectMail.Options.DmOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

/**
 *
 * @author ssoldatos
 */
public class ReprintFile implements Runnable {

  private int reprintIds[];
  private int reprintPages[];
  private MainForm m;

  /** Creates a new instance of ReprintFile
   * @param reprintIds
   * @param reprintPages
   * @param m 
   */
  public ReprintFile(int reprintIds[], int reprintPages[], MainForm m) {
    this.m = m;
    this.reprintIds = reprintIds;
    this.reprintPages = reprintPages;
  }

  @Override
  public void run() {
    try {
      makeReprint(reprintIds);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    }

  }

  private void makeReprint(int[] rIds) throws IOException {
    int curIndex;
    long start, end;

    start = System.currentTimeMillis();
    m.setOutput("Making the reprint file");

    BufferedReader in;
    PrintWriter output;
    String s = m.getTextAreaText();
    String str;
    int curCounter, i = 0, lines = 0;

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    // create the print writer
    output = DmFunctions.createPrinterWriter(m, in);

    m.setTextAreaText(null);

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      //m.updateProgress(lines*100/m.getCustomers());
      curCounter = Integer.parseInt(str.substring(0, 6));
      //System.out.println(curCounter+" "+str);
      curIndex = DmFunctions.getIndexInArray(rIds, curCounter);
      if (curIndex > -1) {
        for (int j = 0; j < getNumOfEnvelopes(reprintPages[curIndex]); j++) {
          if (i <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
            m.appendToSampleArea(str + "\n");
          }
          output.println(str);
        }
        i++;
      }
    }
    m.setReprints(i);
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));

  }

  private int getNumOfEnvelopes(int pages) {
    int envelopes = 1;
    int max = MainForm.options.toInt(DmOptions.MAX_NUMBER_OF_PAGES_IN_ENVELOPE);


    if (pages >= max) {
      envelopes = (pages % max == 0) ? pages / max : (pages / max) + 1;
    } else {
      envelopes = 1;
    }
    return envelopes;
  }
}
