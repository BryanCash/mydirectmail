/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import soldatos.functions.SwingFunctions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import soldatos.functions.ArrayFunctions;

/**
 *
 * @author ssoldatos
 */
public class MoveColumn implements Runnable {

  private MainForm m;
  private int from;
  private int to;
  private long start;
  private long end;
  private String fromName;
  private String toName;

  public MoveColumn(MainForm m, int from, int to) {
    this.m = m;
    this.from = from;
    this.to = to;
  }

  @Override
  public void run() {
    try {
      start = System.currentTimeMillis();
      fromName = String.valueOf(m.sampleTable.getColumnModel().getColumn(to).getHeaderValue());
      toName = String.valueOf(m.sampleTable.getColumnModel().getColumn(from).getHeaderValue());

      m.setTextAreaText("");
      m.setOutput("Moving column");
      moveTheColumn();
      m.appendOutput("Column moved from " + from + " to " + to);
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    }


  }

  private void moveTheColumn() throws IOException {
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);
    String str;
    int lines = 0;
    List curArr;
    String tmp;
    String outString;
    String fromString;
    ArrayList arr;

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read: " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      // split fields
      curArr = Arrays.asList(str.split("" + m.getDelimeter(), -1));
      arr = new ArrayList(curArr);
      fromString = (String) arr.get(from);
      arr.remove(from);
      arr.add(to, fromString);
      outString = ArrayFunctions.join(arr, m.getDelimeter());
      //append the new line to textarea
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(outString + "\n");
      }
      output.println(outString);
    }

    String fromTitle = m.getHeaderTitles().get(from);
    m.getHeaderTitles().remove(from);
    m.getHeaderTitles().add(to, fromTitle);

    // Close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);


  }
}
