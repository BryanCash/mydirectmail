/*
 * MultiplePrinting.java
 *
 * Created on 30 Αύγουστος 2007, 8:05 πμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.Production;

import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Help.Components.Errors;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import soldatos.functions.SwingFunctions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.logging.Level;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.StringFunctions;

/**
 *
 * @author ssoldatos
 */
public class MultiplePrinting implements Runnable {

  int hor = 1;
  int vert = 1;
  boolean addCounter = false;
  boolean cutAndStack = false;
  long start, end;
  BufferedReader in;
  PrintWriter output;
  private static Vector<String> customers = new Vector<String>();
  private static int j;
  private MainForm m;
  private int remains = 0;

  /** Creates a new instance of MultiplePrinting
   * @param hor
   * @param vert
   * @param addCounter
   * @param cutAndStack 
   * @param m
   */
  public MultiplePrinting(int hor, int vert, boolean addCounter, boolean cutAndStack, MainForm m) {
    this.m = m;
    this.hor = hor;
    this.vert = vert;
    this.addCounter = addCounter;
    this.cutAndStack = cutAndStack;
    customers.clear();
  }

  @Override
  public void run() {
    try {
      mult();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    }
  }

  private void mult() throws IOException {

    start = System.currentTimeMillis();
    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);

    // create the print writer
    output = new PrintWriter(new BufferedWriter(new FileWriter(m.getCurrentDirectory()
        + "//" + m.getOpenedFile().replace(".txt", "") + "_multiple.txt")));


    if (!cutAndStack) {
      noCutAndStackMultiplePrinting();
    } else {
      cutAndStackMultiplePrinting();
    }
    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    MainForm.trayIcon.showInfoMessage("Multiple printing",
        "Multiple printing file was created at:\n"
        + m.getCurrentDirectory() + "/" + m.getOpenedFile().replace(".txt", "") + "_multiple.txt\n"
        + "Execution time : " + DmFunctions.execTime(start, end));
  }

  private void noCutAndStackMultiplePrinting() throws IOException {
    String str = "", tmpStr = "", counterStr = "";
    String empty = "";
    int lines = 0, horCounter = 0, vertCounter = 0, mainCounter = 1, newLines = 0;

    m.setOutput("Creating a multiple printing " + hor + " by " + vert);

    //m.setTextAreaText("");

    while ((str = in.readLine()) != null) {
      lines++;
      vertCounter++;
      
      m.appendToCurrentOutput("Lines read : " + lines);
      m.updateProgress(lines * 100 / m.getCustomers());
      if (lines % 50 == 0) {
        DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
      }
      if(lines == 1){
        empty = str.replaceAll("[^" + m.getDelimeter() + "]", " ");
      }
      tmpStr += str + m.getDelimeter();

      // Change line
      if (vertCounter == vert) {
        // New file's lines
        newLines++;
        // Update Counter
        if (horCounter == hor) {
          mainCounter++;
          horCounter = 0;
        }
        vertCounter = 0;
        horCounter++;

        counterStr = (addCounter) ? StringFunctions.padLeft("" + mainCounter, 6, "0") + m.getDelimeter() : "";
        // delete the last del
        tmpStr = tmpStr.substring(0, tmpStr.length() - 1);
        output.println(counterStr + tmpStr);
        tmpStr = "";
      }

    }
    // Print remaining lines
    if (!tmpStr.equals("")) {
      String dummyStr = "";
      newLines++;
      if (vertCounter == vert) {
        mainCounter++;
        vertCounter = 0;
      } else {
        String[] dummy = new String[vert - vertCounter];
        for (int i = 0; i < vert - vertCounter; i++) {
          dummy[i] = empty;
        }
        dummyStr = ArrayFunctions.join(dummy, m.getDelimeter());

      }
      tmpStr = (dummyStr.equals("")) 
          ? tmpStr.substring(0, tmpStr.length() - 1)
          : (tmpStr + dummyStr) ;
      counterStr = (addCounter) ? StringFunctions.padLeft("" + mainCounter, 6, "0") + m.getDelimeter() : "";
      if (lines / hor <= MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
        m.appendToSampleArea(counterStr + tmpStr + "\n");
      }
      output.println(counterStr + tmpStr);
    }


    // close files
    in.close();
    output.close();
    //change to the new tmpfile name
    //Functions.swapFiles();
    // init main parameters
    // change the number of lines to the new one
    //m.setCustomers(newLines);
    m.init(true);
    m.appendOutput("\nFile is converted for multiple printing and saved at:" + m.getCurrentDirectory()
        + "/" + m.getOpenedFile().replace(".txt", "") + "_multiple.txt");
    if (addCounter) {
      m.appendOutput("\nCounter is added");
    }


  }

  private void cutAndStackMultiplePrinting() throws IOException {
    String counterStr;
    int mainCounter = 0;
    int numOfPages = 0;
    String line;
    int customersInPage;
    int multiplier = 0;
    customersInPage = hor * vert;
    String curCustomer = "";

    m.setOutput("Creating a cut and stack multiple printing " + hor + " by " + vert);

    while ((line = in.readLine()) != null) {
      customers.addElement(line);
    }
    // String dummy = "";
    // for(int i = 0 ; i < remains ; i++){
    //   dummy = dummy.replaceAll("[^"+m.getDelimeter()+"]", " ");
    //   customers.addElement(dummy);
    // }

    if (customers.size() % customersInPage == 0) {
      numOfPages = customers.size() / customersInPage;
    } else {
      numOfPages = (customers.size() / customersInPage) + 1;
      for (int i = 0; i < customersInPage; i++) {
        customers.addElement(customers.get(0).replaceAll("[^" + m.getDelimeter() + "]", " "));
      }
    }

    for (j = 0; j < numOfPages; j++) {
      multiplier = 0;
      mainCounter++;
      for (int k = 0; k < hor; k++) {
        curCustomer = "";
        for (int l = 0; l < vert; l++) {
          curCustomer += customers.elementAt(j + (multiplier * numOfPages)) + m.getDelimeter();
          multiplier++;
        }
        counterStr = (addCounter) ? StringFunctions.padLeft("" + mainCounter, 6, "0") + m.getDelimeter() : "";
        output.println(counterStr + curCustomer);
      }
    }
    in.close();
    output.close();
    m.init(true);
    m.appendOutput("\nFile is converted for cut and stack multiple printing and saved at:" + m.getCurrentDirectory()
        + "//" + m.getOpenedFile().replace(".txt", "") + "_multiple.txt");
    if (addCounter) {
      m.appendOutput("\nCounter is added");
    }

  }
}
