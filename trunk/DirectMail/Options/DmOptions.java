/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Options;

import DirectMail.Main.MainForm;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import soldatos.exceptions.OptionFormatException;
import soldatos.functions.FileFunctions;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import soldatos.constants.Delimeters;
import soldatos.messages.Messages;
import soldatos.options.Options;

/**
 *
 * @author ssoldatos
 */
public class DmOptions extends Options {

  public static final String _INI_FILE_ = "Direct_mail.ini";
  public static final String TMP_DB = "directmail_tmp";
  public static final String _NOTHING_ = "Nothing";
  public static final String _NUMBER_OF_FIELDS_ = "Number of Fields";
  public static final String _LENGTH_OF_LINES_ = "Length of lines";
  public static final String _BOTH_ = "Both";
  public static int _MAX_NUM_OF_ERRORS_ = 20;
  public static String _JAR_DIR_= ".";
  public static String[] _EXTENSIONS_ = new String[] {"txt","arf","lbl","csv","tsv","xls"};
  public static String[] _TEXT_FILES_ = new String[] {"txt","arf","lbl","csv","tsv"};
  public static String[] _EXCEL_FILES_ = new String[] {"xls"};

  public static String MAX_SAMPLE_LINES = "MAX_SAMPLE_LINES";
  public static String MAX_FIELD_LENGTH = "MAX_FIELD_LENGTH";
  public static String ADDRESSING_MALE = "ADDRESSING_MALE";
  public static String ADDRESSING_FEMALE = "ADDRESSING_FEMALE";
  public static String ADDRESSING_COMPANIES = "ADDRESSING_COMPANIES";
  public static String ADDRESSING_UNKNOWN = "ADDRESSING_UNKNOWN";
  public static String HOST = "HOST";
  public static String DATABASE = "DATABASE";
  public static String DB_PREFIX = "DB_PREFIX";
  public static String DB_USER = "DB_USER";
  public static String DB_PASSWORD = "DB_PASSWORD";
  public static String ELTA_SORT_ORDER = "ELTA_SORT_ORDER";
  public static String SHOW_OUTPUT_PROGRESS = "SHOW_OUTPUT_PROGRESS";
  public static String ASK_FOR_FILE_ENCODING = "ASK_FOR_FILE_ENCODING";
  public static String USE_NOT_ACCENTED_SURNAME = "USE_NOT_ACCENTED_SURNAME";
  public static String USE_SURNAME_TO_GET_GENDER = "USE_SURNAME_TO_GET_GENDER";
  public static String MAX_NUMBER_OF_UNSORTED_ENVELOPES = "MAX_NUMBER_OF_UNSORTED_ENVELOPES";
  public static String MAX_NUMBER_OF_PAGES_IN_ENVELOPE = "MAX_NUMBER_OF_PAGES_IN_ENVELOPE";
  public static String ADD_ADDRESSING_TO_UNKNOWN_GENDER = "ADD_ADDRESSING_TO_UNKNOWN_GENDER";
  public static String REMEMBER_LAST_OPENED_DIR = "REMEMBER_LAST_OPENED_DIR";
  public static String HOME_DIR = "HOME_DIR";
  public static String SKIN_COLOR = "SKIN_COLOR";
  public static String CAPITAL_ADDRESSING = "CAPITAL_ADDRESSING";
  public static String DEFAULT_DIR = "DEFAULT_DIR";
  public static String FONT = "FONT";
  public static String FONT_SIZE = "FONT_SIZE";
  public static String DIVIDER_LOCATION = "DIVIDER_LOCATION";
  public static String LAST_OPENED_FILE = "LAST_OPENED_FILE";
  public static String LOAD_LAST_OPENED_FILE = "LOAD_LAST_OPENED_FILE";
  public static String CHECK_ON_SAVING = "CHECK_ON_SAVING";
  public static String NOT_ACCENTED_ADDRESSING = "NOT_ACCENTED_ADDRESSING";
  public static String HOSTS_LIST = "HOSTS_LIST";
  public static String UPDATE = "UPDATE";
  public static String LAF = "LAF";
  public static String SQL_HOST = "SQL_HOST";
  public static String SQL_DB = "SQL_DB";
  public static String SQL_USERNAME = "SQL_USERNAME";
  public static String SQL_PASSWORD = "SQL_PASSWORD";
  public static String INTEGRATE = "INTEGRATE";


  /**
   * Create an option object
   * @param jarDir The location of the jar file
   * @throws IOException If the ini file could not be read/written/found
   * @throws Exception
   */
  public DmOptions(String jarDir) throws IOException, Exception {
    super(jarDir + "/" + _INI_FILE_, Delimeters.EQUALS, soldatos.constants.Delimeters.BUTTON);
    _JAR_DIR_ = jarDir.replaceAll("\\\\", "/");
    if (!isOptionsLoaded) {
      writeDefaultIniFile();
      loadOptions();
    }
  }

  @Override
  public Color toColor(String key) {
    Color color = Color.LIGHT_GRAY;
    try {
      color = super.toColor(key);
    } catch (OptionFormatException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      return color;
    }
  }

  @Override
  public void save() {
    try {
      super.save();
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
      Messages.customError("Error!!!", "Could not save the options file");
    }
  }

  /**
   * Check if a string is an option
   * @param name The string to check
   * @return true if it is an option
   */
  public boolean isOption(String name) {
    try {
      Field f = DmOptions.class.getDeclaredField(name);
      return true;
    } catch (NoSuchFieldException ex) {
      return false;
    } catch (SecurityException ex) {
      return false;
    }

  }

  public String getLookAndFeel() {
    String laf;

    LookAndFeelInfo lafs[] = UIManager.getInstalledLookAndFeels();
    String[] lafNames = new String[lafs.length];
    for (int i = 0; i < lafs.length; i++) {
      if (lafs[i].getName().equals(toString(DmOptions.LAF))) {
        return lafs[i].getClassName();
      }
    }
    setOption(LAF, lafs[0].getName());
    save();
    return lafs[0].getClassName();
  }

  public static void writeDefaultIniFile() throws IOException {
    PrintWriter out = FileFunctions.createOutputStream(new File(_JAR_DIR_ + "/" + _INI_FILE_), false);
    out.println(MAX_SAMPLE_LINES + "= 30");
    out.println(MAX_FIELD_LENGTH + "= 35");
    out.println(ADDRESSING_MALE + "= Αγαπητέ κύριε");
    out.println(ADDRESSING_FEMALE + "= Αγαπητή κυρία");
    out.println(ADDRESSING_COMPANIES + "= Αγαπητοί κύριοι");
    out.println(ADDRESSING_UNKNOWN + "= Αγαπητέ/τή κύριε/κυρία");
    out.println(HOST + "= localhost");
    out.println(DATABASE + "= directmail");
    out.println(DB_PREFIX + "= spyros");
    out.println(DB_USER + "= root");
    out.println(DB_PASSWORD + "=");
    out.println(ELTA_SORT_ORDER + "=ASC");
    out.println(SHOW_OUTPUT_PROGRESS + "=false");
    out.println(ASK_FOR_FILE_ENCODING + "=true");
    out.println(USE_NOT_ACCENTED_SURNAME + "=true");
    out.println(USE_SURNAME_TO_GET_GENDER + "=true");
    out.println(MAX_NUMBER_OF_UNSORTED_ENVELOPES + "=5");
    out.println(MAX_NUMBER_OF_PAGES_IN_ENVELOPE + "=40");
    out.println(ADD_ADDRESSING_TO_UNKNOWN_GENDER + "=true");
    out.println(REMEMBER_LAST_OPENED_DIR + "= true");
    out.println(HOME_DIR + "= ");
    out.println(SKIN_COLOR + "= 244,244,244");
    out.println(CAPITAL_ADDRESSING + "= false");
    out.println(DEFAULT_DIR + "= ");
    out.println(FONT + "= SansSerif");
    out.println(FONT_SIZE + "= 12");
    out.println(DIVIDER_LOCATION + "=400");
    out.println(LAST_OPENED_FILE + "=");
    out.println(LOAD_LAST_OPENED_FILE + "=false");
    out.println(CHECK_ON_SAVING + "=Both");
    out.println(NOT_ACCENTED_ADDRESSING + "=false");
    out.println(HOSTS_LIST + "=localhost,root," + soldatos.constants.Delimeters.BUTTON + "dias,root,me_lene_grftpk_root");
    out.println(UPDATE + "=false");
    out.println(LAF + "=");
    out.println(SQL_HOST + "=VmSQLsrv");
    out.println(SQL_DB + "=compak");
    out.println(SQL_USERNAME + "=sa");
    out.println(SQL_PASSWORD+  "=unicef2005");
    out.println(INTEGRATE+  "=false");
    out.close();
  }
}
