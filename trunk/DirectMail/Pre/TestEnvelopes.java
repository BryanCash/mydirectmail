/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Pre;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.FileFunctions;

/**
 *
 * @author ssoldatos
 */
public class TestEnvelopes implements Runnable {

  private MainForm m;
  long start, end;
  private ArrayList<Fills> fills = new ArrayList<Fills>();

  public TestEnvelopes(MainForm m) {
    this.m = m;

  }

  @Override
  public void run() {
    try {
      start = System.currentTimeMillis();
      MainForm.glassPane.activate(null);
      getIds();
      MainForm.glassPane.deactivate();
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (NumberFormatException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void getIds() throws IOException, NumberFormatException {
    BufferedReader in;
    String str = "";
    int lines = 0;

    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    m.setOutput("Getting the lines with the most filled fields");


    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      if (!str.trim().equals("")) {
        fills.add(new Fills(str));
      }
    }
    Collections.sort(fills, new Comparator() {

      @Override
      public int compare(Object o1, Object o2) {
        Fills f1 = (Fills) o1;
        Fills f2 = (Fills) o2;
        if (f1.getFilledFields() == f2.getFilledFields()) {
          return 0;
        } else if (f1.getFilledFields() < f2.getFilledFields()) {
          return 1;
        } else {
          return -1;
        }
      }
    });
    int[] ids = new int[5];
    for (int i = 0; i < ids.length; i++) {
      ids[i] = fills.get(i).id;
    }
    // close files
    in.close();
    Arrays.sort(ids);
    String envelopes = ArrayFunctions.join(ids, ", ");
    m.appendOutput("\nIDs: " + envelopes);
    FileFunctions.createFile(m.getCurrentDirectory()+"/envelope_ids.txt",envelopes);
  }

  class Fills {

    private int id = 0;
    private int filledFields = 0;

    Fills(String line) throws NumberFormatException {
      String[] fields = line.split("" + m.getDelimeter(), -1);
      for (int i = 0; i < fields.length; i++) {
        String field = fields[i];
        if (i == 0) {
          try {
            id = Integer.parseInt(field.substring(0, 6));
          } catch (NumberFormatException ex) {
            MainForm.myLog.log(Level.SEVERE, "The first field is not a number");
            throw new NumberFormatException();
          }
        }
        if (!field.trim().equals("")) {
          filledFields++;
        }
      }
    }

    /**
     * @return the filledFields
     */
    public int getFilledFields() {
      return filledFields;
    }
  }
}
