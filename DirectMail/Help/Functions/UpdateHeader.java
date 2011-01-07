/*
 * UpdateHeader.java
 *
 * Created on 29 Οκτώβριος 2007, 7:11 μμ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Help.Functions;

import DirectMail.*;
import DirectMail.Main.MainForm;

/**
 *
 * @author ssoldatos
 */
public class UpdateHeader implements Runnable {

  private MainForm m;
  private int field = -1;
  private String header = null;
  private String rename = null;

  /**
   * Removes a header at position field
   * @param field The position of the header to remove
   * @param m  The main form
   */
  public  UpdateHeader(int field, MainForm m) {
    this.m = m;
    this.field = field;
    
  }

  /**
   * Insert a new header at position field with the title header
   * @param header The title of the new field
   * @param field The position to insert the header
   * @param m The main form
   */
  public UpdateHeader(String header, int field, MainForm m) {
    this.m = m;
    this.field = field;
    this.header = header;
    
  }

  /**
   * Renames a header to string header
   * @param rename The rename field
   * @param header The new Header title
   * @param field The field to rename
   * @param m The Main Form
   */
  public  UpdateHeader(String rename, String header, int field, MainForm m) {
    this.m = m;
    this.field = field;
    this.header = header;
    this.rename = rename;
   
  }

  @Override
  public synchronized void run() {
   if(header != null && rename != null && field != -1){
      m.getHeaderTitles().setElementAt(header, field);
   } else if (header != null &&  field != -1){
     m.getHeaderTitles().insertElementAt(header, field);
   }else if ( field != -1){
     m.getHeaderTitles().removeElementAt(field);
   }
  }
}
