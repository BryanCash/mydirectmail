/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.Help.Functions.UpdateHeader;
import soldatos.functions.SwingFunctions;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import soldatos.functions.DecimalFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class AmountConvert implements Runnable {

  private String[] intPart;
  private String decimalPart;
  private String euroPart;
  private String thousandPart;
  private String millionPart;
  private long end;
  private long start;
  private String monades[] = {"", "Ένα", "Δύο", "Τρία", "Τέσσερα", "Πέντε", "Έξι", "Επτά", "Οκτώ", "Εννέα"};
  private String monades_2[] = {"", "Μία", "Δύο", "Τρεις", "Τέσσερις", "Πέντε", "Έξι", "Επτά", "Οκτώ", "Εννέα"};
  private String dekades[] = {"", "Δέκα", "Είκοσι", "Τριάντα", "Σαράντα", "Πενήντα", "Εξήντα", "Εβδομήντα", "Ογδόντα", "Εννενήντα"};
  private String ekatontades[] = {"", "Εκατό", "Διακόσια", "Τριακόσια", "Τετρακόσια", "Πεντακόσια", "Εξακόσια", "Επτακόσια", "Οκτακόσια", "Εννιακόσια"};
  private String ekatontades_2[] = {"", "Εκατό", "Διακόσιες", "Τριακόσιες", "Τετρακόσιες", "Πεντακόσιες", "Εξακόσιες", "Επτακόσιες", "Οκτακόσιες", "Εννιακόσιες"};
  private MainForm m;

  /**
   * 
   * @param m
   */
  public AmountConvert(MainForm m) {
    this.m = m;
  }

  @Override
  public void run() {
    try {
      MainForm.glassPane.activate(null);
      commitConverting();
      MainForm.glassPane.deactivate();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  private void commitConverting() throws IOException, InterruptedException, InvocationTargetException, Exception {
    String str;
    int lines = 0;
    String[] currLineArr;
    String newOutLine;
    start = System.currentTimeMillis();
    m.setOutput("Converting amounts");
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);

    //InfoFrame.labelInfo.setText("Getting the gender");
    //Read the file and insert the rows
    m.appendOutput("\nReading the file and converting amounts");
    m.setTextAreaText(null);

    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      //System.out.println(m.delimeter);
      currLineArr = str.split("" + m.getDelimeter(), -1);
      newOutLine = "";
      for (int i = 0; i < m.getFields(); i++) {
        if (i == m.getAmountField()) {
          currLineArr[i] = convert(currLineArr[i].trim());
        } else {
          currLineArr[i] = currLineArr[i];
        }
        if (i < m.getFields() - 1) {
          newOutLine += currLineArr[i] + m.getDelimeter();
        } else {
          newOutLine += currLineArr[i];
        }
      }
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    // close files
    in.close();
    output.close();
    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // move the tk field
    if (m.getTkField() != -1) {
      if (m.getTkField() > m.getAmountField()) {
        m.setTkField(m.getTkField() + 1);
      }
    }

    // Add the gender header
    EventQueue.invokeAndWait(new UpdateHeader("TEXT AMOUNT", m.getAmountField() + 1, m));

    m.appendOutput("\nConverting finished!!!");
    m.init(true);
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    MainForm.trayIcon.showInfoMessage("Converting amount",
        " Convertin numeric amount to text finished\n" +
        " Execution time : " + DmFunctions.execTime(start, end));
  }

  private String convert(String amount) throws Exception {
    String converted = "";
    String am;
    decimalPart = "";
    euroPart = "";
    thousandPart = "";
    millionPart = "";

    if (amount.length() < 3) {
      am = StringFunctions.padLeft(amount, 3, "0");
    } else {
      am = amount;
    }
    //check if there are 2 dec digits
    String coma = am.substring(am.length() - 3, am.length() - 2);
    if (coma.equals(".") || coma.equals(",")) {
      am = am.replaceAll(",", "").replaceAll("\\.", "");
    } else {
      coma = am.substring(am.length() - 2, am.length() - 1);
      if (coma.equals(".") || coma.equals(",")) {
        am = am.replaceAll(",", "").replaceAll("\\.", "") + "0";
      } else {
        am = am.replaceAll(",", "").replaceAll("\\.", "") + "00";
      }
    }
    am = DecimalFunctions.decimalFormat(am, 2);
    String tmpDec[] = am.split(",");
    decimalPart = tmpDec[1];
    intPart = tmpDec[0].split("\\.");

    // Create Decimal Part
    decimalPart = createDecimalPart(decimalPart);


    for (int i = 0; i < intPart.length; i++) {
      if (i == intPart.length - 1) {
        // Create euro part
        euroPart = createEuroPart(intPart[i]);
      } else if (i == intPart.length - 2) {
        thousandPart = createThousandPart(intPart[i]);
      } else if (i == intPart.length - 3) {
        millionPart = createMillionPart(intPart[i]);
      }
    }
    converted = millionPart + thousandPart + euroPart + decimalPart;
    converted = StringFunctions.deleteDoubleSpaces(converted);
    return amount + m.getDelimeter() + converted.trim();
  }

  private String createDecimalPart(String decimalPart) {
    String out = "";
    String text;

    if (decimalPart.equals("00")) {
      out = "";
    } else if (decimalPart.equals("01")) {
      out = "Ένα Λεπτό ";
    } else {
      text = dekades[Integer.parseInt(decimalPart.substring(0, 1))] + " " +
          monades[Integer.parseInt(decimalPart.substring(1, 2))];
      text = checkSpecial(text);
      out = text + "Λεπτά ";
    }

    return out;
  }

  private String createEuroPart(String euroPart) {
    String out = " Ευρώ";
    String text;

    String and = decimalPart.equals("") ? "" : "και ";
    euroPart = StringFunctions.padLeft(euroPart, 3, "0");

    if (Integer.parseInt(euroPart) == 0) {
      out = " Ευρώ";
    } else if (Integer.parseInt(euroPart) == 1) {
      out = "Ένα Ευρώ " + and;
    } else {
      text = dekades[Integer.parseInt(euroPart.substring(1, 2))] + " " +
          monades[Integer.parseInt(euroPart.substring(2, 3))];
      text = checkSpecial(text);
      text = ekatontades[Integer.parseInt(euroPart.substring(0, 1))] + " " + text;

      out = text + "Ευρώ " + and;
    }
    return out;
  }

  private String createThousandPart(String thousandPart) {
    String out = "";
    String text;

    thousandPart = StringFunctions.padLeft(thousandPart, 3, "0");
    if (Integer.parseInt(thousandPart) == 0) {
      out = "";
    } else if (Integer.parseInt(thousandPart) == 1) {
      out = "Χίλια ";
    } else {
      text = dekades[Integer.parseInt(thousandPart.substring(1, 2))] + " " +
          monades_2[Integer.parseInt(thousandPart.substring(2, 3))];
      text = checkSpecial(text);
      text = ekatontades_2[Integer.parseInt(thousandPart.substring(0, 1))] + " " + text + " ";

      out = text + "Χιλιάδες ";
    }

    return out;
  }

  private String createMillionPart(String millionPart) {
    String out = "";
    String text;

    millionPart = StringFunctions.padLeft(millionPart, 3, "0");

    if (Integer.parseInt(millionPart) == 0) {
      out = "";
    } else if (Integer.parseInt(millionPart) == 1) {
      out = "Ένα Εκατομμύριο ";
    } else {
      text = dekades[Integer.parseInt(millionPart.substring(1, 2))] + " " +
          monades[Integer.parseInt(millionPart.substring(2, 3))];
      text = checkSpecial(text);
      text = ekatontades[Integer.parseInt(millionPart.substring(0, 1))] + " " + text + " ";

      out = text + "Εκατομμύρια ";
    }

    return out;
  }

  private String checkSpecial(String text) {
    String special = text;

    if (text.equals("Δέκα Ένα") || text.equals("Δέκα Μία")) {
      special = "Ένδεκα";
    } else if (text.equals("Δέκα Δύο")) {
      special = "Δώδεκα";
    }
    return special + " ";
  }
}
