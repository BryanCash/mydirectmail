/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Pre;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Help.Functions.UpdateHeader;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.Pre.Forms.SelectCustAndOrderCode;
import java.awt.EventQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class AddFlags implements Runnable {

  private MainForm m;
  private long start;
  private long end;
  private Statement stmt = null;
  private BufferedReader in;
  private PrintWriter output;
  private String custCode, orderCode;
  private boolean abort = false;

  public AddFlags(MainForm m) {
    this.m = m;
    this.m = m;
    SelectCustAndOrderCode codes = new SelectCustAndOrderCode(m.isSQLConnected);
    if (codes.isAborted) {
      abort = true;
    } else if (codes.isDefault) {
      custCode = "C_ID";
      orderCode = "ORDER";
    } else {
      custCode = codes.custCode;
      if (custCode.length() != 6 || !StringFunctions.isNumber(custCode.trim())) {
        soldatos.messages.Messages.customError(
            "Wrong customer code",
            "The customer code must be a 6digit number\n"
            + "The default C_ID String will be used");
        custCode = "C_ID";
      }
      orderCode = codes.orderCode;
      if (!StringFunctions.isNumber(orderCode)) {
        soldatos.messages.Messages.customError(
            "Wrong order code",
            "The order code must be a number\n"
            + "The default ORDER String will be used");
        orderCode = "ORD_CO";
      }
    }
  }

  @Override
  public void run() {
    start = System.currentTimeMillis();
    if (abort) {
      MainForm.glassPane.deactivate();
    } else {
      try {
        //create the bufferred reader
        in = DmFunctions.createBufferedReader(m);
        // create the print writer
        output = DmFunctions.createPrinterWriter(m, in);
        start = System.currentTimeMillis();
        MainForm.glassPane.activate(null);
        String str, outline;
        int lines = 0;
         m.setTextAreaText("");
        while ((str = in.readLine()) != null) {
          lines++;
          m.appendToCurrentOutput("Lines read: " + lines);
          m.updateProgress(lines * 100 / m.getCustomers());
          if (lines % 50 == 0) {
            DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
          }
          outline = "0" + "" + m.getDelimeter()
              + "REP!<-                    !" + m.getDelimeter()
              + custCode + m.getDelimeter()
              + orderCode + m.getDelimeter()
              + "0000000000" + m.getDelimeter()
              + str;
          if (lines <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
            m.appendToSampleArea(outline + "\n");
          }
          output.println(outline);
        }
        m.updateProgress(0);
        in.close();
        output.close();
        EventQueue.invokeAndWait(new UpdateHeader("ELTA FLAG", 0, m));
        EventQueue.invokeAndWait(new UpdateHeader("REP", 1, m));
        EventQueue.invokeAndWait(new UpdateHeader("CUST ID", 2, m));
        EventQueue.invokeAndWait(new UpdateHeader("ORDER CODE", 3, m));
        EventQueue.invokeAndWait(new UpdateHeader("FLAGS", 4, m));
        //change to the new tmpfile name
        DmFunctions.swapFiles(m);
        
        end = System.currentTimeMillis();
        m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
        MainForm.trayIcon.showInfoMessage("Adding flags without sorting",
            " Flags are added\n"
            + " Execution time : " + DmFunctions.execTime(start, end));
        m.init(true);
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
  }
}
