/*
 * PFile.java
 *
 * Created on 23 Éïýëéïò 2007, 10:37 ðì
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package DirectMail.Tools.File;

import DirectMail.*;
import DirectMail.Help.Components.Errors;
import DirectMail.Help.Components.JavaFilter;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Tools.File.Forms.ChooseEncoding;
import DirectMail.Post.Forms.SelectReprintFile;
import DirectMail.Options.DmOptions;
import java.lang.reflect.InvocationTargetException;
import soldatos.exceptions.DelimeterNotFoundException;
import java.io.*;
import java.util.logging.Level;
import javax.swing.*;
import DirectMail.Tools.Addressing.Forms.EditNames;
import DirectMail.Tools.CheckFile.CheckFileFields;
import DirectMail.Tools.CheckFile.CheckFileLength;
import soldatos.constants.OptionTypes;
import soldatos.functions.FileFunctions;
import soldatos.functions.StringFunctions;
import soldatos.messages.Messages;

/**
 * Methods for reading and writing files
 * @author ssoldatos
 */
public class PFile implements Runnable {

  /**
   * A {@link String} holding a sample of the {@link File}
   */
  private static String file_string;
  private static boolean createRevert;
  /**
   * The file's filename
   */
  private static String filename;
  public static String OpenedFileName;
  private String what;
  private String where;
  private boolean append;
  private static boolean revert = false;
  private static boolean undo = false;
  private MainForm m;
  private File dragedFile;

  public PFile(String what, String where, boolean append, MainForm m) {
    this.m = m;
    this.what = what;
    this.where = where;
    this.append = append;
  }

  public PFile(File file, MainForm m) {

    this.m = m;
    this.what = "open";
    this.where = "main";
    this.append = false;
    this.dragedFile = file;

  }

  @Override
  public void run() {
    MainForm.glassPane.activate(null);
    try {
      if (what.equals("open")) {
        openFile(where, append);
      } else if (what.equals("save")) {
        saveFile();
      } else if (what.equals("revert")) {
        m.setOutput("Reverting...");
        revertFile();
        revert = true;
      } else if (what.equals("undo")) {
        m.setOutput("Undo...");
        undoFile();
        undo = true;
      }
    } catch (IOException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } catch (DelimeterNotFoundException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } catch (InterruptedException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } catch (InvocationTargetException ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    } finally {
      MainForm.glassPane.deactivate();
    }
  }

  public void undoFile() throws IOException, DelimeterNotFoundException {
    File fFile;


    fFile = new File(DmOptions._JAR_DIR_+"tmp/undo_" + (m.getUndoLevel() - 1));
    // Invoke the readFile method in this class
    file_string = readFile(fFile, "main", false);

    if (file_string != null) {
      // load the file
      // file_string = file_string.replaceAll("\t","\u00bb");
      m.setTextAreaText(file_string);
      m.init(true);
    } else {
      Errors.customError("Cannot undo", "There was a problem while undoing");
    }
  }

  public void revertFile() throws IOException, DelimeterNotFoundException {
    File fFile = new File(DmOptions._JAR_DIR_+"tmp/undo_0");

    // Invoke the readFile method in this class
    file_string = readFile(fFile, "main", false);

    if (file_string != null) {

      // load the file
      // file_string = file_string.replaceAll("\t","\u00bb");
      m.setTextAreaText(file_string);
      m.init(false);
    } else {
      Errors.customError("Cannot revert", "There was a problem while reverting to the original file");
    }
  }

  /**
   * Opens a file
   * @param target The textArea to hold the sample of the file
   * @param append Appends the file to the previously opened one or not
   * @throws IOException
   * @throws DelimeterNotFoundException
   */
  public void openFile(String target, boolean append) throws IOException, DelimeterNotFoundException {
    boolean status = false;
    if (dragedFile == null) {
      status = commitOpen(target, append);
    } else {
      status = commitOpenByDragAndDrop();
    }
    if (!status) {
     // JOptionPane.showMessageDialog(null, "Error opening file!", "File Open Error", JOptionPane.ERROR_MESSAGE);
    } else {
    }
  }

  /***
   * Clears quotes " from a line
   * @param line The line to clean
   * @param d The delimeter
   * @return The clan line
   */
  private String clearQuotes(String line, char d) {
    if (line.startsWith("\"") && line.endsWith("\"")) {
      line = line.substring(1, line.length() - 1);
    }
    //if (line.endsWith("\"")) {
    //  line = line.substring(0, line.length() - 1);
   // }
    //line = line.replaceAll("\"" + d, String.valueOf(d));
    //line = line.replaceAll(d + "\"", String.valueOf(d));
    line = line.replaceAll("\"\"", "\"");
    return line;
  }

  /**
   * Commits the opening
   * @param target The textArea to hold the sample of the file
   * @param append Appends the file to the previously opened one or not
   * @return If the opening was succesful
   */
  @SuppressWarnings({"deprecation", "deprecation"})
  private boolean commitOpen(String target, boolean append) throws IOException, DelimeterNotFoundException {


    JFileChooser fc = new JFileChooser(){

      @Override
      public void rescanCurrentDirectory() {
        
      }
        
    };

    fc.setDialogTitle("Open File");
    // Choose only files, not directories
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    // Start in current directory
    fc.setCurrentDirectory(new File(MainForm.options.toString(DmOptions.HOME_DIR).equals(".") ||
        MainForm.options.toString(DmOptions.HOME_DIR).trim().equals("")
        ? MainForm.options.toString(DmOptions.DEFAULT_DIR)
        : MainForm.options.toString(DmOptions.HOME_DIR)));
    // Set filter for Java source files.
    JavaFilter fJavaFilter = new JavaFilter();
    fc.setFileFilter(fJavaFilter);
    fc.setAcceptAllFileFilterUsed(false);
    // Now open chooser
    int result = fc.showOpenDialog(null);

    if (result == JFileChooser.CANCEL_OPTION) {
      m.setCurrentDirectory(MainForm.options.toString(DmOptions.HOME_DIR));
      return true;
    } else if (result == JFileChooser.APPROVE_OPTION) {
      File fFile = new File("");

      fFile = fc.getSelectedFile();
      filename = FileFunctions.getBaseFilename(fFile);
      OpenedFileName = filename;
      m.setOpenedFile(fFile.getName());
      m.setCurrentDirectory(fc.getSelectedFile().getParent().replace("\\", "/"));
      MainForm.options.setOption(DmOptions.HOME_DIR, OptionTypes.STRING, m.getCurrentDirectory().replace("\\", "/"));
      //System.out.println(m.currentDirectory);

      // Invoke the readFile method in this class
      file_string = readFile(fFile, target, append);

      if (file_string != null) {

        // load the file
        // file_string = file_string.replaceAll("\t","\u00bb");
        if (target.equals("main")) {
          if (append) {
            m.appendToSampleArea(file_string);
          } else {
            m.setTextAreaText(file_string);
          }

          m.init(false);
          m.setCombinedTK(false);
        } else if (target.equals("names")) {
          if (append) {
            EditNames.namesTextArea.append(file_string);
          } else {
            EditNames.namesTextArea.setText(file_string);
          }
        } else if (target.equals("reprint")) {
          if (append) {
            SelectReprintFile.appendTextAreaReprint(file_string);
          } else {
            SelectReprintFile.setTextAreaReprint(file_string);
            m.setCustomers(0);
          }
        }
      } else {
        return false;
      }
    } else {
      return false;
    }

    return true;
  }

  /**
   * Commits the opening by drag n drop
   * @param filename the file to open
   * @return If the opening was succesful
   */
  boolean commitOpenByDragAndDrop() throws IOException, DelimeterNotFoundException {
    if(!DmFunctions.isValidFile(dragedFile)){
      Messages.customError("Open File", "This is not a valid file. You can only open text and excel files.");
      return false;
    }
    filename = FileFunctions.getBaseFilename(dragedFile);
    OpenedFileName = filename;
    m.setOpenedFile(dragedFile.getName());
    try {
      m.setCurrentDirectory(dragedFile.getParent().replace("\\", "/"));
    } catch (NullPointerException ex) {
    }
    MainForm.options.setOption(DmOptions.HOME_DIR, m.getCurrentDirectory().replace("\\", "/"));
    //System.out.println(m.currentDirectory);

    // Invoke the readFile method in this class
    file_string = readFile(dragedFile, "main", false);

    if (file_string != null) {

      // load the file
      // file_string = file_string.replaceAll("\t","\u00bb");

      m.setTextAreaText(file_string);
    }

    m.init(false);
    m.setCombinedTK(false);



    return true;
  }

  /**
   * Reads a file to a sample string
   * @param fFile The file to read
   * @param target The textArea to hold the sample of the file
   * @param append Appends the file to the previously opened one or not
   * @return The sample string of the file
   */
  String readFile(File orFile, String target, boolean append) throws IOException, DelimeterNotFoundException {
    File tmp = null;
    StringBuffer fileBuffer;
    String fileString = null;
    String line, tmpFilename;
    int samples = 0;
    int customers = 0;
    int progress = 0;
    long size = 0L;
    long read = 0L;
    char d = 0;
    int numFields = 0;
    int errorsInFields = 0;
    StringBuffer buffer = new StringBuffer();
    PrintWriter errorLog;
    File fFile;
    if (!orFile.isFile()) {
      return "";
    }
    fFile = convertEncoding(orFile);


    FileReader in = new FileReader(fFile);
    BufferedReader dis = new BufferedReader(in);

    fileBuffer = new StringBuffer();
    tmpFilename = FileFunctions.createRandomName();

    size = fFile.length();
    if (target.equals("main")) {
      m.setOrigFileName(tmpFilename);
      File tmpOpenFile = new File(tmpFilename);
      m.setOrigFile(tmpOpenFile);
      tmpOpenFile.deleteOnExit();
      createRevert = true;
      MainForm.myLog.log(Level.SEVERE,tmpFilename);
      MainForm.myLog.log(Level.SEVERE,tmpOpenFile.getAbsolutePath());
    } else if (target.equals("reprint")) {
      SelectReprintFile.rFile = new File(tmpFilename);
      SelectReprintFile.rFile.deleteOnExit();
    }

    PrintWriter output = new PrintWriter(DmOptions._JAR_DIR_+"tmp/" + tmpFilename);
    m.setOutput("Opening File: " + m.getOpenedFile());
    m.setProgressText("Opening file: " + m.getOrigFileName());
    int lineNumber = 0;
    while ((line = dis.readLine()) != null) {
      lineNumber++;
      if (lineNumber == 1 && m.getEncodingIn().equals("UTF-8")) {
        if (line.startsWith("ï»¿")) {
          line = line.replaceFirst("ï»¿", "");
        }
        if (line.startsWith("?")) {
          line = line.replaceFirst("\\?", "");
        }
      }
      if (customers == 0) {
        try {
          d = StringFunctions.findDelimeter(line);
        } catch (DelimeterNotFoundException ex) {
          int ask = Messages.confirm("No Delimeter", "No Delimeter was found\nLoad file in one column?");
          if (ask == JOptionPane.NO_OPTION){
          m.setOutput("Failed to open file " + m.getOpenedFile());
          m.setProgressText("0%");
          m.updateProgress(0);
          MainForm.myLog.log(Level.SEVERE, null, ex);
          //Messages.customError("No Delimeter", "No known delimeter is found in the file");
          output.close();
          in.close();
          throw new DelimeterNotFoundException("Delimeter was not found");
          } else {
            d = '\u0000';
          }
        }
        numFields = line.split("" + d, -1).length;
      }


      if (!line.replaceAll("" + d, "").trim().equals("")) {
        read += line.length() + 1;

        if(m.getOpenedFile().endsWith(".csv")){
          line = StringFunctions.parseCsvLine(line, d, " ");
        }
        line = clearQuotes(line, d);
        if (line.split(String.valueOf(d), -1).length != numFields) {
          buffer.append("Wrong number of fields in line " + (customers + 1) + "(fields " + line.split(String.valueOf(d), -1).length + " instead of " + numFields + "\n");
          errorsInFields++;
        }
        output.println(line);
        progress = (int) (read * 100 / size);
        m.updateProgress(progress);
        if (target.equals("main")) {
          if (samples < MainForm.options.toInt(DmOptions.MAX_SAMPLE_LINES)) {
            fileBuffer.append(line + "\n");
            samples++;
          }
        } else {
          fileBuffer.append(line + "\n");
        }

        customers++;
      }
    }

    if (errorsInFields > 0) {
      errorLog = FileFunctions.createOutputStream(new File(m.getCurrentDirectory() + "/importErrors.txt"), false);
      errorLog.print(buffer.toString());
      errorLog.close();
      Errors.customError("Wrong number of fields", "There were erros in number of fields in " + errorsInFields + " lines\n" +
          "Check the errors log");
      MainForm.myLog.log(Level.SEVERE, "There were erros in number of fields in " + errorsInFields + " lines");
    }
    m.setProgressText("0%");
    m.updateProgress(0);
    output.close();
    in.close();

    fileString = fileBuffer.toString();
    m.appendToCurrentOutput("File opened");
    // Enable Menus
    //m.updateToolbars();
    if (createRevert) {
      // First make a copy of the file
    }
    if (append) {
      m.setCustomers(customers + m.getCustomers());
    } else {
      m.init(false);
      m.setCustomers(customers);
    }


    return fileString;
  }

  private File convertEncoding(File orFile) throws FileNotFoundException, UnsupportedEncodingException, IOException {
    File fFile = new File(DmOptions._JAR_DIR_ + "tmp/imp.tmp");
    fFile.deleteOnExit();
    // Set up byte streams.
    InputStream in = new FileInputStream(orFile);

    OutputStream out = new FileOutputStream(fFile);
    String from = "Cp1253";

    if (MainForm.options.toBoolean(DmOptions.ASK_FOR_FILE_ENCODING)) {
      String[] encodings = {"Cp1253", "UTF-8"};
      ChooseEncoding c = new ChooseEncoding();
      from = c.encoding;
    } else {
      from = FileFunctions.getFileEncoding(orFile);
    }


    m.setEncodingIn(from);
    m.setEncodingOut(System.getProperty("file.encoding"));
    //m.encodingOut = from;
    m.textBoxEncodingIn.setText(m.getEncodingIn());
    m.textBoxEncodingOut.setText(m.getEncodingOut());


    if (m.getEncodingOut().equals("windows-1253") || m.getEncodingOut().equals("Cp1253")) {
      m.setCharacterSet("greek");
      m.setCollation("greek_general_ci");
    } else {
      m.setCharacterSet("utf8");
      m.setCollation("utf8_general_ci");
    }


    // Set up character streams.
    Reader r = new BufferedReader(new InputStreamReader(in, m.getEncodingIn()));
    Writer w = new BufferedWriter(new OutputStreamWriter(out, m.getEncodingOut()));

    // Copy characters from input to output. The InputStreamReader
    // converts from the input encoding to Unicode, and the
    // OutputStreamWriter converts from Unicode to the output encoding.
    // Characters that cannot be represented in the output encoding are
    // output as '?'
    char[] buffer = new char[4096];
    int len;
    while ((len = r.read(buffer)) != -1) // Read a block of input.
    {
      w.write(buffer, 0, len); // And write it out.
    }
    r.close(); // Close the input.
    w.close(); // Flush and close output.

    return fFile;
  }

  /**
   * Saves a file
   * @throws IOException
   * @throws InterruptedException
   * @throws InvocationTargetException
   */
  public void saveFile() throws IOException, InterruptedException, InvocationTargetException {
    boolean status = commitSaveFile();
//    if (!status) {
//      JOptionPane.showMessageDialog(null, "File is not saved!!", "File Save Error", JOptionPane.ERROR_MESSAGE);
//    } else {
//    }
  }

  /**
   * Commits the saving
   * @return If the saving was succesful
   */
  private boolean commitSaveFile() throws IOException, InterruptedException, InvocationTargetException {
    File file = null;
    JFileChooser fc = new JFileChooser(){

      @Override
      public void rescanCurrentDirectory() {

      }

    };

    // Start in current directory
    fc.setCurrentDirectory(new File(m.getCurrentDirectory()));

    // Set filter for Java source files.
    JavaFilter fJavaFilter = new JavaFilter();

    fc.setFileFilter(fJavaFilter);

    // Set to a default name for save.
    File fFile = new File("_" + m.getCustomers() + ".txt");

    fc.setSelectedFile(fFile);

    // Open chooser dialog
    int result = fc.showSaveDialog(null);

    if (result == JFileChooser.CANCEL_OPTION) {
      return false;
    } else if (result == JFileChooser.APPROVE_OPTION) {
      fFile = fc.getSelectedFile();

      if (fFile.exists()) {
        int response = JOptionPane.showConfirmDialog(null, "Overwrite existing file?", "Confirm Overwrite",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.CANCEL_OPTION) {
          return false;
        } else {
          fFile.delete();
        }
      }
      return writeFile(fFile);
    } else {
      return false;
    }
  }

  /**
   * Writes a file
   * @param fFile The file to write to
   * @return If the writing was succesful
   */
  private boolean writeFile(File fFile) throws IOException, InterruptedException, InvocationTargetException {
    String line;
    File tmp;
    //Show a working panel

//    WorkingPanel w = new WorkingPanel(m,
//        "Saving File",
//        "Please wait while the file " + fFile.getName() + " is being saved");
    m.IndeterminateProgress(true);
    m.setProgressText("Saving File: " + fFile.getName());

    if (MainForm.options.toString(DmOptions.CHECK_ON_SAVING).equals(DmOptions._BOTH_) ||
        MainForm.options.toString(DmOptions.CHECK_ON_SAVING).equals(DmOptions._NUMBER_OF_FIELDS_)) {
      // First check the file for field errors
      SwingUtilities.invokeAndWait(new Runnable() {

        @Override
        public void run() {
          CheckFileFields cff = new CheckFileFields(m);
          Thread t = new Thread(cff);
          t.start();
        }
      });
    }
    if (MainForm.options.toString(DmOptions.CHECK_ON_SAVING).equals(DmOptions._BOTH_) ||
        MainForm.options.toString(DmOptions.CHECK_ON_SAVING).equals(DmOptions._LENGTH_OF_LINES_)) {
      // First check the file for field errors
      SwingUtilities.invokeAndWait(new Runnable() {

        @Override
        public void run() {
          CheckFileLength cfl = new CheckFileLength(m);
          Thread t = new Thread(cfl);
          t.start();
        }
      });
    }

    // First make a copy of the file
    tmp = new File(DmOptions._JAR_DIR_+"tmp/tmp");
    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(tmp)));
    FileReader in = new FileReader(DmOptions._JAR_DIR_+"tmp/" + m.getOrigFileName());
    BufferedReader dis = new BufferedReader(in);
    while ((line = dis.readLine()) != null) {
      out.println(line);
    }

    out.flush();
    out.close();
    in.close();
    tmp.deleteOnExit();



    //Then rename the copy
    tmp.renameTo(fFile);
    m.IndeterminateProgress(false);
    m.setProgressText("0%");
    //w.dispose();
    m.appendOutput("\nFile " + fFile.getName() + " is saved");

    return true;
  }
}
//~ Formatted by Jindent --- http://www.jindent.com

