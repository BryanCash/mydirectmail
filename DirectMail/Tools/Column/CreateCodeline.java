/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Tools.Column;

import DirectMail.Options.DmOptions;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Help.Functions.UpdateHeader;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.exceptions.InvalidNumberException;
import soldatos.functions.EncodingFunctions;
import soldatos.functions.StringFunctions;
import soldatos.functions.SwingFunctions;

/**
 *
 * @author ssoldatos
 */
public class CreateCodeline implements Runnable {

  private MainForm m;
  private int amountField;
  private int customerCodeField;
  private String customerCodeBefore;
  private String customerCodeAfter;
  private String eltaAccount;
  private String type;
  private long start;
  private long end;
  private String customerPadString;

  public CreateCodeline(MainForm m, int amountField,
      int customerCodeField, String customerCodeBefore,
      String customerCodeAfter, String eltaAccount, String type,
      String customerPadString) {
    this.m = m;
    this.amountField = amountField;
    this.customerCodeField = customerCodeField;
    this.customerCodeBefore = customerCodeBefore;
    this.customerCodeAfter = customerCodeAfter;
    this.eltaAccount = eltaAccount;
    this.type = type;
    this.customerPadString = customerPadString;
  }

  @Override
  public void run() {
    try {
      start = System.currentTimeMillis();
      m.setOutput("Creating the codeline");
      commit();
      end = System.currentTimeMillis();
      m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    } catch (InterruptedException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InvocationTargetException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }

  }

  private void commit() throws IOException, InterruptedException, InvocationTargetException {
    //create the bufferred reader
    BufferedReader in = DmFunctions.createBufferedReader(m);
    // create the print writer
    PrintWriter output = DmFunctions.createPrinterWriter(m, in);

    m.setTextAreaText(null);
    String str;
    int lines = 0;
    String newOutLine = "";
    String[] currLineArr;
    String curAmmount = "";
    String curCustomerCode = "";
    String codeline;
    while ((str = in.readLine()) != null) {
      lines++;
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      currLineArr = str.split("" + m.getDelimeter(), -1);
      if (amountField != -1) {
        curAmmount = currLineArr[amountField];
      }
      if (customerCodeField != -1) {
        curCustomerCode = currLineArr[customerCodeField];
      }
      codeline = createTheCodeline(curAmmount, curCustomerCode);
      newOutLine = str + m.getDelimeter() + codeline;
      if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(newOutLine + "\n");
      }
      output.println(newOutLine);
    }
    // close files
    in.close();
    output.close();
    // Add the new field header
    EventQueue.invokeAndWait(new UpdateHeader("" + (m.getFields()-1), m.getFields(), m));

    //change to the new tmpfile name
    DmFunctions.swapFiles(m);
    // init main parameters
    m.init(true);
    m.appendOutput("\nCodeline was created");

  }

  private String createTheCodeline(String curAmmount, String curCustomerCode) {
    String codeline = "";
    String intTaxiPaymentCode = customerCodeBefore + curCustomerCode + customerCodeAfter;
    String taxiPaymentCode;
    if(!customerPadString.equals("")){
    taxiPaymentCode = 
        StringFunctions.padLeft(customerCodeBefore +
        curCustomerCode +
        customerCodeAfter,15,customerPadString);
    } else {
      taxiPaymentCode =
        customerCodeBefore +
        curCustomerCode +
        customerCodeAfter;
    }
    String taxiPaymentCodeCD = String.valueOf(EncodingFunctions.mod10(intTaxiPaymentCode));
    String strCurAmmount;
    String strCurAmmountCD;
    if(!curAmmount.equals("")){
      strCurAmmount = curAmmount.replaceAll("\\.", "").replaceAll(",", "");
      strCurAmmountCD = String.valueOf(EncodingFunctions.mod10(strCurAmmount)) + ">";
    } else{
      strCurAmmount = "";
      strCurAmmountCD = "";
    }

    return StringFunctions.padLeft(">" + taxiPaymentCode + taxiPaymentCodeCD + "<",23," ") +
        StringFunctions.padLeft(strCurAmmount + strCurAmmountCD, 13, " ") +
        StringFunctions.padLeft(eltaAccount, 24, " ") +
        "<"+ StringFunctions.padLeft(type, 3, " ") +">";
  }
}
