/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DirectMail.Help.Functions;

import DirectMail.Main.MainForm;

/**
 *
 * @author ssoldatos
 */
public class UpdateSampleTable implements Runnable {
  private MainForm m;
  private String row;

  public  UpdateSampleTable(MainForm m, String row) {
    this.m = m;
    this.row = row;
  }

  @Override
  public synchronized void run() {
    String[] stringArr;

    row = row.replaceAll("\n", "");
    if (!row.isEmpty()) {
      stringArr = row.split("" + m.getDelimeter(), -1);
      m.getSampleText().addElement(stringArr);
    }
  }

}
