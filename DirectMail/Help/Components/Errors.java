/*
 * Errors.java
 *
 * Created on 19 Σεπτέμβριος 2007, 7:27 πμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Help.Components;

import DirectMail.Help.Components.MyDraggable;
import DirectMail.Main.MainForm;
import javax.swing.JOptionPane;

/**
 * Custom errors class
 * @author ssoldatos
 */
public class Errors {

  /** Creates a new instance of Errors */
  public Errors() {
  }

  /**
   * Outputs an I/O error message
   * @param message The message to show
   */
  public static void IOError(String message) {
    customError("I/O Error", message);
  }

  /**
   * Outputs an SQL error message
   * @param message The message to show
   */
  public static void SQLError(String message) {
    customError("SQL Error", message);
  }

  /**
   * Outputs a Bad Location error message
   * @param message The message to show
   */
  public static void BadLocationError(String message) {
    customError("Bad Location Error", message);
  }

  /**
   * Outputs an PDF Document error message
   * @param message The message to show
   */
  public static void DocumentError(String message) {
    customError("Document Error", message);
  }

  /**
   * Outputs a PDF bad element error message
   * @param message The message to show
   */
  public static void BadElementError(String message) {
    customError("Bad Element Error", message);
  }

  /**
   * Outputs a No open file error message
   */
  public static void NoOpenedFileError() {
    customError("File error", "No File is open.\nAborting...");
  }

  /**
   * Outputs a File has filters error
   */
  public static void FiltersError() {
    customError("Filters error", "The file has filters\nRemove filters to proceed.");
  }



  /**
   * Outputs a No TK Selected error message
   */
  public static void NoTkSelectedError() {
    customError("TK error", "You haven't marked a TK field");
  }

  /**
   * Outputs a No Elta Fields Selected error message
   */
  public static void NoEltaFieldsSelectedError() {
    customError("ELTA error", "You haven't marked all ELTA fields (TK, Address, City)");
  }

  /**
   * Displays an {@link Error} message to the user
   * @param title The title of the error message
   * @param message The error message
   */
  public static void customError(String title, String message) {
    boolean active = false;
    if (MainForm.glassPane.isActivated()) {
      active = true;
    } else {
      MainForm.glassPane.activate(null);
    }
    JOptionPane.showMessageDialog(null,
        message,
        title,
        JOptionPane.ERROR_MESSAGE);
    if (!active) {
      MainForm.glassPane.deactivate();
    }
  }
}
