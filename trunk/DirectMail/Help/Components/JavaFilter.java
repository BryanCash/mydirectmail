/*
 * JavaFilter.java
 *
 * Created on 21 Ã‹˙ÔÚ 2007, 6:32 ÏÏ
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package DirectMail.Help.Components;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Options.DmOptions;
import java.io.*;
import soldatos.functions.ArrayFunctions;

/**
 *
 * @author ssoldatos
 */


/** Filter to work with JFileChooser to select java file types. **/
public class JavaFilter extends javax.swing.filechooser.FileFilter {
  /**
	 * Accepts a file or not
	 * @param f The file to accept
	 * @return If the file is accepted or not
	 */
  @Override
  public boolean accept(File f) {
    if(f.isDirectory()){
      return true;
    }
    if(DmFunctions.isTextFile(f)){
      return true;
    }
    return false;
  }

  
  /**
   * Description of the accepted files
   * @return The description of the file
   */
  @Override
  public String getDescription() {
    String extensions = "Text files (";
    for (int i = 0; i < DmOptions._TEXT_FILES_.length; i++) {
      String ext = DmOptions._TEXT_FILES_[i];
      if(i<DmOptions._TEXT_FILES_.length-1){
      extensions+="*."+ext+", ";
      } else {
        extensions+="*."+ext;
      }
    }
    return extensions+")";
  }
} // class JavaFilter
