/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Help.Functions;

import DirectMail.Main.MainForm;
import DirectMail.Help.Forms.Tooltip;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.table.JTableHeader;

/**
 *
 * @author ssoldatos
 */
public class ComputeMaxLength implements Runnable {

  /**
   * @return the biggerString
   */
  public String getBiggerString() {
    return biggerString;
  }

  private MainForm m;
  private int index;
  private BufferedReader in;
  private int maxLength = 0;
  private JTableHeader header;
  private Tooltip ttip;
  private String biggerString = "";

  public ComputeMaxLength(MainForm m, int index, JTableHeader header, Tooltip ttip) {
    this.m = m;
    this.index = index;
    this.header = header;
    this.ttip = ttip;
  }

  public ComputeMaxLength(MainForm m, int index) {
    this.m = m;
    this.index = index;
    this.header = null;
    this.ttip = null;
  }

  @Override
  public void run() {
    try {
      computeLength();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    }
  }

  private void computeLength() throws IOException {
    in = DmFunctions.createBufferedReader(m);
    String str;
    String[] curArr;
    int curMaxLength;

    while ((str = in.readLine()) != null) {
      // split fields
      curArr = str.split("" + m.getDelimeter(), -1);
      curMaxLength = curArr[index].trim().length();
      if (curMaxLength > getMaxLength()) {
        biggerString = curArr[index].trim();
        setMaxLength(curMaxLength);
      }
    }
    m.setOutput("Longest String: \"" + getBiggerString() + "\"");
    in.close();
    //header.setToolTipText("Max Length= " + getMaxLength());
    if (ttip != null) {
      ttip.labelMaxLength.setText("Max Length= " + getMaxLength());
    }
  }

  public int getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }
}
