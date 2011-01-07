/*
 * MainForm.java
 *
 * Created on 23 Ιούλιος 2007, 9:35 πμ
 */
package DirectMail.Main;

import DirectMail.Help.Components.myTableColumnListener;
import DirectMail.Help.Components.myTableHeaderListener;
import DirectMail.Help.Components.Users;
import DirectMail.Help.Components.MyTrayIcon;
import DirectMail.Help.Components.InfoThread;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Tools.File.PFile;
import DirectMail.Tools.ZipCode.Forms.SelectLabelQuantities;
import DirectMail.Tools.File.Import.Forms.ImportExcel;
import DirectMail.Users.Forms.Login;
import DirectMail.Help.Action;
import DirectMail.Help.Common;
import DirectMail.Help.Components.FileDropListener;
import DirectMail.Tools.Column.Filter;
import DirectMail.Help.Components.MyDisabledGlassPane;
import DirectMail.Options.DmOptions;
import DirectMail.Options.Forms.OptionsPane;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import soldatos.lookaandfeel.MyFont;
import soldatos.lookaandfeel.Skin;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.print.Printable;
import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.*;
import net.iharder.dnd.FileDrop;
import soldatos.constants.Delimeters;
import soldatos.functions.FileFunctions;
import soldatos.messages.Messages;

/**
 *
 * @author  ssoldatos
 */
public class MainForm extends JFrame {

  public static final long serialVersionUID = 1L;
  private char delimeter = '\u0000';
  private int customers = 0;
  private String firstLine = "";
  private int fields = 0;
  private int tkField = -1;
  private int reprints = 0;
  private String reprintIDs = null;
  private File origFile = null;
  private int lineLength = 0;
  private File tmpFile = null;
  private String origFileName = "";
  private String tmpFileName = "";
  private int notEnveloped = 0;
  public boolean runNumOfTk = false;
  public boolean getCurrent = true;
  private String currentDirectory = ".";
  public String currentText = "";
  private String openedFile = "";
  private boolean combinedTK = false;
  private int combinedTKCount = 0;
  private Thread t;
  private boolean formatedTK = false;
  private boolean labeling = false;
  private int undoId = 0;
  private int undoLevel = -1;
  private boolean undoStatus = false;
  public DefaultTableModel model = new DefaultTableModel();
  private Vector<String[]> sampleText = new Vector<String[]>(0, 1);
  public int selectedColumn = -1;
  private TableColumnModel colModel;
  private boolean mainConnectionToDB = false;
  private String databases[] = new String[1];
  public Statement stmt;
  public static Statement dbStmt;
  private int genderField = -1;
  private int fullnameField = -1;
  private int amountField = -1;
  private int firstnameField = -1;
  private int surnameField = -1;
  private int cityField = -1;
  private int addressField = -1;
  private int abroadField = -1;
  private int counterField = -1;
  private int customerCodeField = -1;
  private Vector<String> headerTitles = new Vector<String>();
  //private Vector<Vector> headerTitlesBackUp = new Vector<Vector>();
  private Users currentUser;
  private PFile pFile;
  private int isTKRightField = -1;
  public static MyTrayIcon trayIcon;
  private ArrayList<Integer> moveColumnFrom = new ArrayList<Integer>();
  private ArrayList<Integer> moveColumnTo = new ArrayList<Integer>();
  private String version = "1.3";
  public String build = "414";
  private String fullVersion;
  private String encodingIn;
  private String encodingOut;
  private String characterSet;
  private String collation;
  public static Logger myLog;
  public static MyDisabledGlassPane glassPane = new MyDisabledGlassPane(Color.gray, WIDTH);
  private DefaultComboBoxModel databasesModel;
  public static DmOptions options;
  public String updatedVersion;
  public static int LOOK_AND_FEEL = 0;
  boolean popUp_dropDownShowing = false;
  public boolean isSQLConnected = false;
  public static ArrayList<Filter> filters = new ArrayList<Filter>();
  public Action action = new Action();
  public String updatesPath = "//dias/develop/JAVA PROJECTS/DIRECT MAIL";

  public MainForm(Users cUser, final File fileToLoad, java.awt.event.ActionEvent evt) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException, AWTException {
    String changes = null;
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

      @Override
      public void uncaughtException(Thread t, Throwable e) {
        MainForm.myLog.log(Level.SEVERE, "Uncaught Exception", e);
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        //Messages.customError("Error", result.toString());
        textArea_extra.setText(result.toString());

        tabPanel.add(extraPanel, 2);
        tabPanel.setIconAt(2, new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/error.gif")));
        //infoPanel.setTitleAt(2, "Exceptions");
        tabPanel.setTabComponentAt(2, ExtraTab);
        tabPanel.setSelectedIndex(2);
        glassPane.deactivate();
      }
    });
    fullVersion = getFullVersion();
    currentUser = cUser;

    setIconImage(Common.getFDImage());
    encodingOut = System.getProperty("file.encoding");
    encodingIn = System.getProperty("file.encoding");
    try {
      String jarFile = FileFunctions.getJarDir(this);
      myLog.log(Level.INFO, "Reading ini file from: " + jarFile + DmOptions._INI_FILE_);
      options = new DmOptions(jarFile);
      //tmp
      File tmpDir = new File(DmOptions._JAR_DIR_ + "tmp");
      if (!tmpDir.exists()) {
        tmpDir.mkdir();
      }
      //templates
      File templatesDir = new File(DmOptions._JAR_DIR_ + "templates");
      if (!templatesDir.exists()) {
        templatesDir.mkdir();
      }


    } catch (Exception ex) {
      String jarFile = FileFunctions.getJarDir(this);
      myLog.log(Level.WARNING, "Couldn't load options file - Writing default ini", ex);
      DmOptions.writeDefaultIniFile();
      try {
        options = new DmOptions(jarFile);
      } catch (Exception ex2) {
        myLog.log(Level.SEVERE, "Couldn't load options file", ex2);
      }
    }
    try {
      // Check updates
      changes = checkForUpdates();
    } catch (Exception ex) {
      myLog.log(Level.WARNING, "Couldn't load options file", ex);
    }

    //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    UIManager.setLookAndFeel(options.getLookAndFeel());

    Skin.applySkin(options.toColor(DmOptions.SKIN_COLOR));
    MyFont.setMyFont(options.toString(DmOptions.FONT), options.toInt(DmOptions.FONT_SIZE));
    glassPane = new MyDisabledGlassPane(Skin.getColor_5(), 128);
    glassPane.setMainForm(this);
    JRootPane root = SwingUtilities.getRootPane(this);
    root.setGlassPane(glassPane);
    databasesModel = new DefaultComboBoxModel(OptionsPane.getHostsKeys(options));
    // System.out.println(bgColor);
    initComponents();
    //Hide some gui
    //tabPanel.remove(2);
    //



    //Set some gui heights
    splitPane_main.setDividerLocation(options.toInt(DmOptions.DIVIDER_LOCATION));
    validate();
    pack();

    //Add update tab
    if (options.toBoolean(DmOptions.UPDATE)) {
      tabPanel.add(extraPanel, tabPanel.getTabCount());
      tabPanel.setTitleAt(tabPanel.getTabCount() - 1, "Update");
      tabPanel.setSelectedIndex(tabPanel.getTabCount() - 1);
      tabPanel.setTabComponentAt(tabPanel.getTabCount() - 1, ExtraTab);
      extraTabLabel.setText("Update");
      ExtraTab.revalidate();
      textArea_extra.setText("There's an updated version of Direct Mail.\n"
          + "Current version : " + fullVersion + "\n"
          + "Updated Version : " + updatedVersion + "\n"
          + "\nChanges:"
          + changes);
    }

    //int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    //int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    //setSize(width, height);

    //labelCurrentHost.setText(Options.toString(Options.HOST));
    sampleTable.setEnabled(false);
    setLocationRelativeTo(null);
    setVisible(true);
    setExtendedState(MAXIMIZED_BOTH);

    loadDatabasesNames();

    colModel = sampleTable.getColumnModel();
    colModel.setColumnSelectionAllowed(false);
    //Hide some popup items
    //popUpAddColAfter.setVisible(false);
    //popUpAddColBefore.setVisible(false);

    //Crete the logfile
    //dmLog = new Logging("", "./DirectMail.log");
    // Thread log = new Thread(dmLog);
    // log.start();
    myLog.log(Level.INFO, "===============================");
    myLog.log(Level.INFO, "Application starting");


    InfoThread mem = new InfoThread();
    EventQueue.invokeLater(mem);

    // Create the tray icon
    trayIcon = new MyTrayIcon(MainForm.this);

    //Add listener for jtable column move
    sampleTable.getColumnModel().addColumnModelListener(new myTableColumnListener(this, sampleTable));
    sampleTable.getTableHeader().addMouseListener(new myTableHeaderListener(this, sampleTable));


    //load file
    if (fileToLoad != null) {
      if (!fileToLoad.getName().endsWith(".xls")) {
        pFile = new PFile(fileToLoad, this);
        headerTitles.removeAllElements();
        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            t = new Thread(pFile);
            t.start();
          }
        });
      } else {
        final String exFile = fileToLoad.getCanonicalPath();
        headerTitles.removeAllElements();
        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            ImportExcel exc = new ImportExcel(MainForm.this, exFile);
            t = new Thread(exc);
            t.start();
          }
        });
      }
      revertFileMenuItem.setEnabled(true);
      revertToolbar.setEnabled(true);
    } else {
      final String lastOpenedFile = options.toString(DmOptions.HOME_DIR) + "/"
          + options.toString(DmOptions.LAST_OPENED_FILE);
      if (new File(lastOpenedFile).isFile()
          && Login.firstTime
          && options.toBoolean(DmOptions.LOAD_LAST_OPENED_FILE)) {
        Login.firstTime = false;
        if (Messages.confirm("Load file?", "Load last opened file :\n"
            + lastOpenedFile) == JOptionPane.YES_OPTION) {
          // If not excel
          if (!lastOpenedFile.endsWith(".xls")) {
            pFile = new PFile(new File(lastOpenedFile), this);
          } else {
            SwingUtilities.invokeLater(new Runnable() {

              @Override
              public void run() {
                ImportExcel exc = new ImportExcel(MainForm.this, lastOpenedFile);
                t = new Thread(exc);
                t.start();
              }
            });
          }
          headerTitles.removeAllElements();
          //EventQueue.invokeLater(p);
          t = new Thread(getPFile());
          t.start();
          revertFileMenuItem.setEnabled(true);
          revertToolbar.setEnabled(true);

        } else {
          openFileMenuItemActionPerformed(evt);
        }
      }
    }

    if (evt != null) {
      openFileMenuItemActionPerformed(evt);
    }

    Border bord = new LineBorder(Color.red, 3);
    new FileDrop(sampleTable, bord, new FileDropListener(this)); // end FileDrop.Listener
    new FileDrop(sampleTablePane, bord, new FileDropListener(this)); // end FileDrop.Listener
  }

  public void MoveTableColumn(int from, int to) {
    action.moveColumn(this, from, to);
  }

  private String checkForUpdates() {
    try {
      return Common.checkForUpdates(this);
    } catch (Exception ex) {
      return null;
    }
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    extraPanel = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    textArea_extra = new javax.swing.JTextArea();
    popUp = new javax.swing.JPopupMenu();
    markField = new javax.swing.JMenu();
    markFieldUnmark = new javax.swing.JMenuItem();
    jSeparator4 = new javax.swing.JSeparator();
    markFieldTK = new javax.swing.JMenuItem();
    markFieldAddress = new javax.swing.JMenuItem();
    markFieldCity = new javax.swing.JMenuItem();
    markFieldGender = new javax.swing.JMenuItem();
    markFieldFirstname = new javax.swing.JMenuItem();
    markFieldSurname = new javax.swing.JMenuItem();
    markFieldFullName = new javax.swing.JMenuItem();
    markFieldAmount = new javax.swing.JMenuItem();
    markFieldCountryFlag = new javax.swing.JMenuItem();
    markFieldCustCodeField = new javax.swing.JMenuItem();
    tableEdit = new javax.swing.JMenu();
    popUpDeleteFirstLine = new javax.swing.JMenuItem();
    popUpDeleteCol = new javax.swing.JMenuItem();
    popUpDuplicate = new javax.swing.JMenuItem();
    popUpAddColAfter = new javax.swing.JMenuItem();
    popUpAddColBefore = new javax.swing.JMenuItem();
    filtersMenu = new javax.swing.JMenu();
    filterMenuItem = new javax.swing.JMenuItem();
    removeFiltersMenuItem = new javax.swing.JMenuItem();
    saveFilteredResultsMenuItem = new javax.swing.JMenuItem();
    dataActions = new javax.swing.JMenu();
    popUpSortAsc = new javax.swing.JMenuItem();
    popUpSortDesc = new javax.swing.JMenuItem();
    uniqueMenuItem = new javax.swing.JMenuItem();
    columnActions = new javax.swing.JMenu();
    popUpBreakField = new javax.swing.JMenuItem();
    popupPadField = new javax.swing.JMenuItem();
    popUpSearchReplace = new javax.swing.JMenuItem();
    popUpConvertGreek = new javax.swing.JMenuItem();
    popUpCapitalize = new javax.swing.JMenuItem();
    popUpFillBlanks = new javax.swing.JMenuItem();
    popUpKeepFirstName = new javax.swing.JMenuItem();
    popUpTrim = new javax.swing.JMenuItem();
    popUpReplaceFromDb = new javax.swing.JMenuItem();
    popUpFormatDecimal = new javax.swing.JMenuItem();
    ExtraTab = new javax.swing.JPanel();
    extraTabPanel = new javax.swing.JPanel();
    extraTabLabel = new javax.swing.JLabel();
    extraTabButton = new javax.swing.JButton();
    popUp_dropDown = new javax.swing.JPopupMenu();
    popUpSortByElta = new javax.swing.JMenuItem();
    popUpSortByNumOfTk = new javax.swing.JMenuItem();
    popUpAddFlags = new javax.swing.JMenuItem();
    filetoolbar = new javax.swing.JToolBar();
    openToolbar = new javax.swing.JButton();
    undoToolbar = new javax.swing.JButton();
    revertToolbar = new javax.swing.JButton();
    saveToolbar = new javax.swing.JButton();
    FileToolbar = new javax.swing.JButton();
    jLabel13 = new javax.swing.JLabel();
    numOfTKToolbar = new javax.swing.JButton();
    addCounterToolbar = new javax.swing.JButton();
    FormatTKToolbar = new javax.swing.JButton();
    CombineTKCityToolbar = new javax.swing.JButton();
    fixedToolbar = new javax.swing.JButton();
    shiftFieldsToolbar = new javax.swing.JButton();
    combo_delimeter = new javax.swing.JComboBox();
    testEnvelopeToolbar = new javax.swing.JButton();
    samplesToolbar = new javax.swing.JButton();
    jLabel14 = new javax.swing.JLabel();
    combineFieldsToolbar1 = new javax.swing.JButton();
    getGenderToolbar1 = new javax.swing.JButton();
    lastNameVocativeAddressingToolbar1 = new javax.swing.JButton();
    removeFiltersToolbar = new javax.swing.JButton();
    splitPane_main = new javax.swing.JSplitPane();
    samplePanel = new javax.swing.JPanel();
    sampleTablePane = new javax.swing.JScrollPane();
    sampleTable = new javax.swing.JTable();
    outputPanel = new javax.swing.JPanel();
    panel_quickOptions = new javax.swing.JPanel();
    textField_updateRows = new javax.swing.JTextField();
    combobox_checkOnSaving = new javax.swing.JComboBox();
    jLabel3 = new javax.swing.JLabel();
    jLabel7 = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    combobox_database = new javax.swing.JComboBox();
    mainHostIcon = new javax.swing.JButton();
    jLabel9 = new javax.swing.JLabel();
    textField_maxLength = new javax.swing.JTextField();
    jLabel10 = new javax.swing.JLabel();
    comboBox_sampleFontSize = new javax.swing.JComboBox();
    erpIcon = new javax.swing.JButton();
    jLabel11 = new javax.swing.JLabel();
    checkbox_showOutPutProggress = new javax.swing.JCheckBox();
    jLabel12 = new javax.swing.JLabel();
    checkbox_dontBotherMe = new javax.swing.JCheckBox();
    panel_output = new javax.swing.JPanel();
    jScrollPane3 = new javax.swing.JScrollPane();
    textAreaOutput = new javax.swing.JTextArea();
    buttonClearOutput = new javax.swing.JButton();
    progressBar = new javax.swing.JProgressBar();
    jLabel2 = new javax.swing.JLabel();
    remainingTimeLabel = new javax.swing.JLabel();
    labelRow = new javax.swing.JLabel();
    labelCol = new javax.swing.JLabel();
    tabPanel = new javax.swing.JTabbedPane();
    genOptionsPanel = new javax.swing.JPanel();
    jPanel1 = new javax.swing.JPanel();
    textBoxFields = new javax.swing.JTextField();
    textBoxDelimeter = new javax.swing.JTextField();
    textBoxTmpFilename = new javax.swing.JTextField();
    labelFields = new javax.swing.JLabel();
    labelDelimeter = new javax.swing.JLabel();
    labelTmpFilename = new javax.swing.JLabel();
    jLabel1 = new javax.swing.JLabel();
    textBoxOpenedFile = new javax.swing.JTextField();
    labelEncodingIn = new javax.swing.JLabel();
    labelEncodingOut = new javax.swing.JLabel();
    labelCustomers = new javax.swing.JLabel();
    textBoxEncodingIn = new javax.swing.JTextField();
    textBoxEncodingOut = new javax.swing.JTextField();
    textBoxCustomers = new javax.swing.JTextField();
    memoryPanel = new javax.swing.JPanel();
    tf_maxMemory = new javax.swing.JTextField();
    jLabel4 = new javax.swing.JLabel();
    jLabel5 = new javax.swing.JLabel();
    tf_totalMemory = new javax.swing.JTextField();
    tf_usedMemory = new javax.swing.JTextField();
    jLabel6 = new javax.swing.JLabel();
    layeredPane = new javax.swing.JLayeredPane();
    mainMenuBar = new JMenuBar() {

      public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        for (int i = 0; i < getSubElements().length; i++) {
          MenuElement menuElement = getSubElements()[i];
          ((Component) menuElement).setEnabled(enabled);
        }
      }
    };
    fileMenu = new javax.swing.JMenu();
    openFileMenuItem = new javax.swing.JMenuItem();
    importMenuItem = new javax.swing.JMenu();
    importExcellMenuItem = new javax.swing.JMenuItem();
    importDbMenuItem = new javax.swing.JMenuItem();
    jMenu1 = new javax.swing.JMenu();
    exportDBMenuItem = new javax.swing.JMenuItem();
    undoMenuItem = new javax.swing.JMenuItem();
    appendFileMenuItem = new javax.swing.JMenuItem();
    saveFileMenuItem = new javax.swing.JMenuItem();
    revertFileMenuItem = new javax.swing.JMenuItem();
    closeFileMenuItem = new javax.swing.JMenuItem();
    templatesMenu = new javax.swing.JMenu();
    createTemplateMenuItem = new javax.swing.JMenuItem();
    applyTemplateMenuItem = new javax.swing.JMenuItem();
    saveTemplateMenuItem = new javax.swing.JMenuItem();
    jSeparator1 = new javax.swing.JSeparator();
    quitMenuItem = new javax.swing.JMenuItem();
    preEnvelopingMenu = new javax.swing.JMenu();
    sortByEltaFlagMenuItem = new javax.swing.JMenuItem();
    sortByNumOfTkMenuItem = new javax.swing.JMenuItem();
    addFlags_menuItem = new javax.swing.JMenuItem();
    fixedFileActionMenuItem = new javax.swing.JMenuItem();
    formatTkActionMenuItem = new javax.swing.JMenuItem();
    combineTkCityMenuItem = new javax.swing.JMenuItem();
    zipCodesFlagActionMenuItem = new javax.swing.JMenuItem();
    addCounterActionMenuItem = new javax.swing.JMenuItem();
    shiftFieldsMenuItem = new javax.swing.JMenuItem();
    changeDelimeterMenuItem = new javax.swing.JMenuItem();
    envelopeTestMenuItem = new javax.swing.JMenuItem();
    jMenuItem2 = new javax.swing.JMenuItem();
    jSeparator5 = new javax.swing.JSeparator();
    addressFileMenuItem = new javax.swing.JMenuItem();
    postEnvelopingMenu = new javax.swing.JMenu();
    notEnvelopedActionsMenuItem = new javax.swing.JMenuItem();
    notEnvelopedIDsMenuItem = new javax.swing.JMenuItem();
    reprintFileActionMenuItem = new javax.swing.JMenuItem();
    mergeReportsActionMenu = new javax.swing.JMenuItem();
    zipCodeMenu = new javax.swing.JMenu();
    tkReportMenuItem = new javax.swing.JMenuItem();
    tkLabelsMenuItem = new javax.swing.JMenuItem();
    tkLabelsFromPCADDMenuItem = new javax.swing.JMenuItem();
    correctMenu = new javax.swing.JMenu();
    findMenu = new javax.swing.JMenu();
    findTKMenuItem = new javax.swing.JMenuItem();
    tkFindCity = new javax.swing.JMenuItem();
    findCountyMenuItem = new javax.swing.JMenuItem();
    correctmenu = new javax.swing.JMenu();
    correctAddressCity = new javax.swing.JMenuItem();
    menuItem_correcrFirstnames = new javax.swing.JMenuItem();
    fillEmptyAddresses = new javax.swing.JMenuItem();
    extractZipCode = new javax.swing.JMenuItem();
    addressingMenu = new javax.swing.JMenu();
    menuSplitNames = new javax.swing.JMenuItem();
    getGenderMenuItem = new javax.swing.JMenuItem();
    menuFirstname = new javax.swing.JMenu();
    firstNameVocativeAddressingMenuItem = new javax.swing.JMenuItem();
    menuLastname = new javax.swing.JMenu();
    lastNameVocativeAddressingMenuItem = new javax.swing.JMenuItem();
    lastnameGenitiveAddressingMenuItem = new javax.swing.JMenuItem();
    lastNameAccusativeAddressingMenuItem = new javax.swing.JMenuItem();
    addNamesMenuItem = new javax.swing.JMenuItem();
    checkFileMenu = new javax.swing.JMenu();
    checkTKMenuItem = new javax.swing.JMenuItem();
    checkFieldsMenuItem = new javax.swing.JMenuItem();
    checkLengthMenuItem = new javax.swing.JMenuItem();
    checkControlChrMenuItem = new javax.swing.JMenuItem();
    checkAddressMenuItem = new javax.swing.JMenuItem();
    checkList13MenuItem = new javax.swing.JMenuItem();
    toolsMenu = new javax.swing.JMenu();
    fieldToolsMenu = new javax.swing.JMenu();
    combineFieldsMenuItem = new javax.swing.JMenuItem();
    addFieldMenuItem = new javax.swing.JMenuItem();
    deleteFieldsMenuItem = new javax.swing.JMenuItem();
    multisortingMenuItem = new javax.swing.JMenuItem();
    fileToolsMenu = new javax.swing.JMenu();
    splitFileMenuItem = new javax.swing.JMenuItem();
    jMenuItem3 = new javax.swing.JMenuItem();
    productionToolsMenu = new javax.swing.JMenu();
    duplexMenuItem = new javax.swing.JMenuItem();
    doubleProductionMenuItem = new javax.swing.JMenuItem();
    multiplePrintingMenuItem = new javax.swing.JMenuItem();
    doubleSplittedMenuItem = new javax.swing.JMenuItem();
    multiplePagesMenuItem = new javax.swing.JMenuItem();
    linesToolsMenu = new javax.swing.JMenu();
    multiplyLinesMenuItem = new javax.swing.JMenuItem();
    deleteDuplicatesMenuItem = new javax.swing.JMenuItem();
    deleteFromFileMenuItem = new javax.swing.JMenuItem();
    convertMenu = new javax.swing.JMenu();
    capitilizeMenuItem = new javax.swing.JMenuItem();
    convertGreekMenuItem = new javax.swing.JMenuItem();
    convertAmountMenuItem = new javax.swing.JMenuItem();
    trimFieldsMenuItem = new javax.swing.JMenuItem();
    jMenu2 = new javax.swing.JMenu();
    codelineMenu = new javax.swing.JMenuItem();
    menuItemCheckDigit = new javax.swing.JMenuItem();
    menuItemHolidays = new javax.swing.JMenuItem();
    jMenuItem4 = new javax.swing.JMenuItem();
    jMenuItem5 = new javax.swing.JMenuItem();
    jMenu3 = new javax.swing.JMenu();
    exportList13MenuItem = new javax.swing.JMenuItem();
    menuItem_updateFnamesDB = new javax.swing.JMenuItem();
    jMenuItem1 = new javax.swing.JMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    optionsMenuItem = new javax.swing.JMenuItem();
    usersMenu = new javax.swing.JMenu();
    addUserMenuItem = new javax.swing.JMenuItem();
    editUserMenuItem = new javax.swing.JMenuItem();
    helpMenu = new javax.swing.JMenu();
    helpHelpMenuItem = new javax.swing.JMenuItem();
    logsHelpMenuItem = new javax.swing.JMenuItem();
    aboutHelpMenuItem = new javax.swing.JMenuItem();
    menuItem_update = new javax.swing.JMenuItem();

    textArea_extra.setColumns(20);
    textArea_extra.setRows(5);
    jScrollPane1.setViewportView(textArea_extra);

    javax.swing.GroupLayout extraPanelLayout = new javax.swing.GroupLayout(extraPanel);
    extraPanel.setLayout(extraPanelLayout);
    extraPanelLayout.setHorizontalGroup(
      extraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(extraPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
        .addContainerGap())
    );
    extraPanelLayout.setVerticalGroup(
      extraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(extraPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    popUp.setInvoker(sampleTable);
    popUp.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
      public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
      }
      public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
      }
      public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
        popUpPopupMenuWillBecomeVisible(evt);
      }
    });

    markField.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/markField.png"))); // NOI18N
    markField.setText("Mark Field As...");
    markField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markFieldActionPerformed(evt);
      }
    });

    markFieldUnmark.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/unMarkField.png"))); // NOI18N
    markFieldUnmark.setText("Unmark");
    markFieldUnmark.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markFieldUnmarkActionPerformed(evt);
      }
    });
    markField.add(markFieldUnmark);
    markField.add(jSeparator4);

    markFieldTK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/markTk.png"))); // NOI18N
    markFieldTK.setText("Zip Code");
    markFieldTK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markFieldTKActionPerformed(evt);
      }
    });
    markField.add(markFieldTK);

    markFieldAddress.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/markAddress.png"))); // NOI18N
    markFieldAddress.setText("Address");
    markFieldAddress.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markFieldAddressActionPerformed(evt);
      }
    });
    markField.add(markFieldAddress);

    markFieldCity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/markCity.png"))); // NOI18N
    markFieldCity.setText("City");
    markFieldCity.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markFieldCityActionPerformed(evt);
      }
    });
    markField.add(markFieldCity);

    markFieldGender.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/markGender.png"))); // NOI18N
    markFieldGender.setText("Gender");
    markFieldGender.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markFieldGenderActionPerformed(evt);
      }
    });
    markField.add(markFieldGender);

    markFieldFirstname.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/markNames.png"))); // NOI18N
    markFieldFirstname.setText("Firstname");
    markFieldFirstname.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markFieldFirstnameActionPerformed(evt);
      }
    });
    markField.add(markFieldFirstname);

    markFieldSurname.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/markNames.png"))); // NOI18N
    markFieldSurname.setText("Surname");
    markFieldSurname.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markFieldSurnameActionPerformed(evt);
      }
    });
    markField.add(markFieldSurname);

    markFieldFullName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/markNames.png"))); // NOI18N
    markFieldFullName.setText("Fullname");
    markFieldFullName.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markFieldFullNameActionPerformed(evt);
      }
    });
    markField.add(markFieldFullName);

    markFieldAmount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/markAmount.png"))); // NOI18N
    markFieldAmount.setText("Amount");
    markFieldAmount.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markFieldAmountActionPerformed(evt);
      }
    });
    markField.add(markFieldAmount);

    markFieldCountryFlag.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/markCountry.png"))); // NOI18N
    markFieldCountryFlag.setText("Country");
    markFieldCountryFlag.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markFieldCountryFlagActionPerformed(evt);
      }
    });
    markField.add(markFieldCountryFlag);

    markFieldCustCodeField.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/markCustCode.png"))); // NOI18N
    markFieldCustCodeField.setText("Customer Code");
    markFieldCustCodeField.setToolTipText("");
    markFieldCustCodeField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        markFieldCustCodeFieldActionPerformed(evt);
      }
    });
    markField.add(markFieldCustCodeField);

    popUp.add(markField);

    tableEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/tableEdit.png"))); // NOI18N
    tableEdit.setText("Table Editing");

    popUpDeleteFirstLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/deleteFirstLine.png"))); // NOI18N
    popUpDeleteFirstLine.setText("Delete First Line");
    popUpDeleteFirstLine.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpDeleteFirstLineActionPerformed(evt);
      }
    });
    tableEdit.add(popUpDeleteFirstLine);

    popUpDeleteCol.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/deleteField.png"))); // NOI18N
    popUpDeleteCol.setText("Delete Column");
    popUpDeleteCol.setIconTextGap(0);
    popUpDeleteCol.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpDeleteColActionPerformed(evt);
      }
    });
    tableEdit.add(popUpDeleteCol);

    popUpDuplicate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/duplicate.png"))); // NOI18N
    popUpDuplicate.setText("Duplicate Column");
    popUpDuplicate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpDuplicateActionPerformed(evt);
      }
    });
    tableEdit.add(popUpDuplicate);

    popUpAddColAfter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/addColAfter.png"))); // NOI18N
    popUpAddColAfter.setText("Add column after");
    popUpAddColAfter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpAddColAfterActionPerformed(evt);
      }
    });
    tableEdit.add(popUpAddColAfter);

    popUpAddColBefore.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/addColBefore.png"))); // NOI18N
    popUpAddColBefore.setText("Add column before");
    popUpAddColBefore.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpAddColBeforeActionPerformed(evt);
      }
    });
    tableEdit.add(popUpAddColBefore);

    popUp.add(tableEdit);

    filtersMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/filter.png"))); // NOI18N
    filtersMenu.setText("Filters");
    filtersMenu.addMenuListener(new javax.swing.event.MenuListener() {
      public void menuCanceled(javax.swing.event.MenuEvent evt) {
      }
      public void menuDeselected(javax.swing.event.MenuEvent evt) {
      }
      public void menuSelected(javax.swing.event.MenuEvent evt) {
        filtersMenuMenuSelected(evt);
      }
    });

    filterMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/filter.png"))); // NOI18N
    filterMenuItem.setText("Filter Field");
    filterMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        filterMenuItemActionPerformed(evt);
      }
    });
    filtersMenu.add(filterMenuItem);

    removeFiltersMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/removeFilter.png"))); // NOI18N
    removeFiltersMenuItem.setText("Remove All Filters");
    removeFiltersMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        removeFiltersMenuItemActionPerformed(evt);
      }
    });
    filtersMenu.add(removeFiltersMenuItem);
    removeFiltersMenuItem.setEnabled(false);

    saveFilteredResultsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/saveFilteredResults.png"))); // NOI18N
    saveFilteredResultsMenuItem.setText("Save Filtered Results");
    saveFilteredResultsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveFilteredResultsMenuItemActionPerformed(evt);
      }
    });
    filtersMenu.add(saveFilteredResultsMenuItem);
    saveFilteredResultsMenuItem.setEnabled(false);

    popUp.add(filtersMenu);

    dataActions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/columnEdit.png"))); // NOI18N
    dataActions.setText("Data Actions");
    dataActions.addMenuListener(new javax.swing.event.MenuListener() {
      public void menuCanceled(javax.swing.event.MenuEvent evt) {
      }
      public void menuDeselected(javax.swing.event.MenuEvent evt) {
      }
      public void menuSelected(javax.swing.event.MenuEvent evt) {
        dataActionsMenuSelected(evt);
      }
    });

    popUpSortAsc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/sortAsc.png"))); // NOI18N
    popUpSortAsc.setText("Sort by field (ASC)");
    popUpSortAsc.setActionCommand("Item");
    popUpSortAsc.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpSortAscActionPerformed(evt);
      }
    });
    dataActions.add(popUpSortAsc);

    popUpSortDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/sortDesc.png"))); // NOI18N
    popUpSortDesc.setText("Sort by field (DESC)");
    popUpSortDesc.setActionCommand("Item");
    popUpSortDesc.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpSortDescActionPerformed(evt);
      }
    });
    dataActions.add(popUpSortDesc);

    uniqueMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/unique.png"))); // NOI18N
    uniqueMenuItem.setText("Unique Records");
    uniqueMenuItem.setToolTipText("<html>Finds the unique records of a field and exports them to a file<br />Also can export a file of unique names used for creation of IOVI splices");
    uniqueMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        uniqueMenuItemActionPerformed(evt);
      }
    });
    dataActions.add(uniqueMenuItem);

    popUp.add(dataActions);

    columnActions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/columnAction.png"))); // NOI18N
    columnActions.setText("Column Actions");

    popUpBreakField.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/breakFields.png"))); // NOI18N
    popUpBreakField.setText("Break Long Field");
    popUpBreakField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpBreakFieldActionPerformed(evt);
      }
    });
    columnActions.add(popUpBreakField);

    popupPadField.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/pad.png"))); // NOI18N
    popupPadField.setText("Pad field");
    popupPadField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popupPadFieldActionPerformed(evt);
      }
    });
    columnActions.add(popupPadField);

    popUpSearchReplace.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/searchReplace.png"))); // NOI18N
    popUpSearchReplace.setText("Search & Replace");
    popUpSearchReplace.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpSearchReplaceActionPerformed(evt);
      }
    });
    columnActions.add(popUpSearchReplace);

    popUpConvertGreek.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/convertGreek.png"))); // NOI18N
    popUpConvertGreek.setText("Convert To Greek");
    popUpConvertGreek.setToolTipText("Converts English characters to Greek");
    popUpConvertGreek.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpConvertGreekActionPerformed(evt);
      }
    });
    columnActions.add(popUpConvertGreek);

    popUpCapitalize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/capitilize.png"))); // NOI18N
    popUpCapitalize.setText("Capitalize");
    popUpCapitalize.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpCapitalizeActionPerformed(evt);
      }
    });
    columnActions.add(popUpCapitalize);

    popUpFillBlanks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/fillBlanks.png"))); // NOI18N
    popUpFillBlanks.setText("Fill Blank Fields");
    popUpFillBlanks.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpFillBlanksActionPerformed(evt);
      }
    });
    columnActions.add(popUpFillBlanks);

    popUpKeepFirstName.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/KeepFirst.png"))); // NOI18N
    popUpKeepFirstName.setText("Keep first word");
    popUpKeepFirstName.setToolTipText("Keeps the first word of the field");
    popUpKeepFirstName.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpKeepFirstNameActionPerformed(evt);
      }
    });
    columnActions.add(popUpKeepFirstName);

    popUpTrim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/trim.png"))); // NOI18N
    popUpTrim.setText("Trim field");
    popUpTrim.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpTrimActionPerformed(evt);
      }
    });
    columnActions.add(popUpTrim);

    popUpReplaceFromDb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/replaceFromDb.png"))); // NOI18N
    popUpReplaceFromDb.setText("Replace from DB");
    popUpReplaceFromDb.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpReplaceFromDbActionPerformed(evt);
      }
    });
    columnActions.add(popUpReplaceFromDb);

    popUpFormatDecimal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/decimalFormat.png"))); // NOI18N
    popUpFormatDecimal.setText("Decimal Format");
    popUpFormatDecimal.setToolTipText("Formats a numeric field");
    popUpFormatDecimal.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpFormatDecimalActionPerformed(evt);
      }
    });
    columnActions.add(popUpFormatDecimal);

    popUp.add(columnActions);

    ExtraTab.setMaximumSize(new java.awt.Dimension(107, 16));
    ExtraTab.setMinimumSize(new java.awt.Dimension(50, 16));
    ExtraTab.setOpaque(false);
    ExtraTab.setPreferredSize(new java.awt.Dimension(100, 16));

    extraTabPanel.setOpaque(false);

    extraTabLabel.setFont(extraTabLabel.getFont().deriveFont(extraTabLabel.getFont().getStyle() | java.awt.Font.BOLD));
    extraTabLabel.setForeground(new java.awt.Color(255, 255, 255));
    extraTabLabel.setText("Exception");
    extraTabLabel.setMaximumSize(new java.awt.Dimension(56, 20));
    extraTabLabel.setPreferredSize(new java.awt.Dimension(56, 20));

    extraTabButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/header_delete_small.png"))); // NOI18N
    extraTabButton.setBorder(null);
    extraTabButton.setBorderPainted(false);
    extraTabButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    extraTabButton.setOpaque(false);
    extraTabButton.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
    extraTabButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    extraTabButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        extraTabButtonActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout extraTabPanelLayout = new javax.swing.GroupLayout(extraTabPanel);
    extraTabPanel.setLayout(extraTabPanelLayout);
    extraTabPanelLayout.setHorizontalGroup(
      extraTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, extraTabPanelLayout.createSequentialGroup()
        .addComponent(extraTabLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(extraTabButton, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(6, 6, 6))
    );
    extraTabPanelLayout.setVerticalGroup(
      extraTabPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(extraTabButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 16, Short.MAX_VALUE)
      .addComponent(extraTabLabel, 0, 0, Short.MAX_VALUE)
    );

    javax.swing.GroupLayout ExtraTabLayout = new javax.swing.GroupLayout(ExtraTab);
    ExtraTab.setLayout(ExtraTabLayout);
    ExtraTabLayout.setHorizontalGroup(
      ExtraTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(extraTabPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
    );
    ExtraTabLayout.setVerticalGroup(
      ExtraTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(ExtraTabLayout.createSequentialGroup()
        .addComponent(extraTabPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    popUpSortByElta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/sortEltaFlag.png"))); // NOI18N
    popUpSortByElta.setText("Sort By ELTA");
    popUpSortByElta.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpSortByEltaActionPerformed(evt);
      }
    });
    popUp_dropDown.add(popUpSortByElta);

    popUpSortByNumOfTk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/sortByNumOfTk.png"))); // NOI18N
    popUpSortByNumOfTk.setText("Sort By NumOfTK");
    popUpSortByNumOfTk.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpSortByNumOfTkActionPerformed(evt);
      }
    });
    popUp_dropDown.add(popUpSortByNumOfTk);

    popUpAddFlags.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/popup/sortEltaFlag.png"))); // NOI18N
    popUpAddFlags.setText("Don't Sort");
    popUpAddFlags.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        popUpAddFlagsActionPerformed(evt);
      }
    });
    popUp_dropDown.add(popUpAddFlags);

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("GRAFOTYPIKI S.A. - Direct Mail Application v" + fullVersion);
    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    setMinimumSize(new java.awt.Dimension(970, 700));
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
      public void windowIconified(java.awt.event.WindowEvent evt) {
        formWindowIconified(evt);
      }
      public void windowOpened(java.awt.event.WindowEvent evt) {
        formWindowOpened(evt);
      }
    });

    filetoolbar.setRollover(true);

    openToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/open.gif"))); // NOI18N
    openToolbar.setToolTipText("Open File");
    openToolbar.setFocusable(false);
    openToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    openToolbar.setIconTextGap(6);
    openToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    openToolbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        openToolbarActionPerformed(evt);
      }
    });
    filetoolbar.add(openToolbar);

    undoToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/undo.gif"))); // NOI18N
    undoToolbar.setToolTipText("Undo last action");
    undoToolbar.setEnabled(false);
    undoToolbar.setFocusable(false);
    undoToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    undoToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    undoToolbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        undoToolbarActionPerformed(evt);
      }
    });
    filetoolbar.add(undoToolbar);

    revertToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/revert.gif"))); // NOI18N
    revertToolbar.setToolTipText("Revert");
    revertToolbar.setEnabled(false);
    revertToolbar.setFocusable(false);
    revertToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    revertToolbar.setIconTextGap(6);
    revertToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    revertToolbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        revertToolbarActionPerformed(evt);
      }
    });
    filetoolbar.add(revertToolbar);

    saveToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/save.gif"))); // NOI18N
    saveToolbar.setToolTipText("Save File");
    saveToolbar.setFocusable(false);
    saveToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    saveToolbar.setIconTextGap(6);
    saveToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    saveToolbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveToolbarActionPerformed(evt);
      }
    });
    filetoolbar.add(saveToolbar);

    FileToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/quit.gif"))); // NOI18N
    FileToolbar.setToolTipText("Exit");
    FileToolbar.setFocusable(false);
    FileToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    FileToolbar.setIconTextGap(6);
    FileToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    FileToolbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        FileToolbarActionPerformed(evt);
      }
    });
    filetoolbar.add(FileToolbar);

    jLabel13.setText("         ");
    filetoolbar.add(jLabel13);

    numOfTKToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/numOfTK.gif"))); // NOI18N
    numOfTKToolbar.setToolTipText("Sort by Areas & Number of Zip Codes");
    numOfTKToolbar.setFocusable(false);
    numOfTKToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    numOfTKToolbar.setIconTextGap(6);
    numOfTKToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    numOfTKToolbar.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        numOfTKToolbarMouseReleased(evt);
      }
    });
    filetoolbar.add(numOfTKToolbar);

    addCounterToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/addCounter.gif"))); // NOI18N
    addCounterToolbar.setToolTipText("Add counter");
    addCounterToolbar.setFocusable(false);
    addCounterToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    addCounterToolbar.setIconTextGap(6);
    addCounterToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    addCounterToolbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addCounterToolbarActionPerformed(evt);
      }
    });
    filetoolbar.add(addCounterToolbar);

    FormatTKToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/formatTK.gif"))); // NOI18N
    FormatTKToolbar.setToolTipText("Format TK");
    FormatTKToolbar.setFocusable(false);
    FormatTKToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    FormatTKToolbar.setIconTextGap(6);
    FormatTKToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    FormatTKToolbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        FormatTKToolbarActionPerformed(evt);
      }
    });
    filetoolbar.add(FormatTKToolbar);

    CombineTKCityToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/combineTkCity.gif"))); // NOI18N
    CombineTKCityToolbar.setToolTipText("Combine TK & City");
    CombineTKCityToolbar.setFocusable(false);
    CombineTKCityToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    CombineTKCityToolbar.setIconTextGap(6);
    CombineTKCityToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    CombineTKCityToolbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        CombineTKCityToolbarActionPerformed(evt);
      }
    });
    filetoolbar.add(CombineTKCityToolbar);

    fixedToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/fixed.gif"))); // NOI18N
    fixedToolbar.setToolTipText("Make File fixed length");
    fixedToolbar.setFocusable(false);
    fixedToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    fixedToolbar.setIconTextGap(6);
    fixedToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    fixedToolbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fixedToolbarActionPerformed(evt);
      }
    });
    filetoolbar.add(fixedToolbar);

    shiftFieldsToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/shiftFields.gif"))); // NOI18N
    shiftFieldsToolbar.setToolTipText("Shift Fields");
    shiftFieldsToolbar.setFocusable(false);
    shiftFieldsToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    shiftFieldsToolbar.setIconTextGap(6);
    shiftFieldsToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    shiftFieldsToolbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        shiftFieldsToolbarActionPerformed(evt);
      }
    });
    filetoolbar.add(shiftFieldsToolbar);

    combo_delimeter.setModel(new DefaultComboBoxModel(Delimeters.toStringArray()));
    combo_delimeter.setBorder(null);
    combo_delimeter.setMaximumSize(new java.awt.Dimension(54, 27));
    combo_delimeter.setMinimumSize(new java.awt.Dimension(54, 27));
    combo_delimeter.setOpaque(false);
    combo_delimeter.setPreferredSize(new java.awt.Dimension(54, 27));
    combo_delimeter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        combo_delimeterActionPerformed(evt);
      }
    });
    filetoolbar.add(combo_delimeter);

    testEnvelopeToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/testEnvelope.png"))); // NOI18N
    testEnvelopeToolbar.setToolTipText("<html> Searches the <b>final</b> pcadd file and gets the 5 id's with the most label information. The id's are displayed in the output textarea");
    testEnvelopeToolbar.setFocusable(false);
    testEnvelopeToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    testEnvelopeToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    testEnvelopeToolbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        testEnvelopeToolbarActionPerformed(evt);
      }
    });
    filetoolbar.add(testEnvelopeToolbar);

    samplesToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/samples.png"))); // NOI18N
    samplesToolbar.setToolTipText("<html>\n<b>Create a file with samples</b><br />\nYou select the fields of which you want unique samples <br />\nand the maximum number of samples<br /> \n(leaving the textfield blank results in getting the<br />\n maximum samples returned by the query)");
    samplesToolbar.setFocusable(false);
    samplesToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    samplesToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    samplesToolbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        samplesToolbarActionPerformed(evt);
      }
    });
    filetoolbar.add(samplesToolbar);

    jLabel14.setText("        ");
    filetoolbar.add(jLabel14);

    combineFieldsToolbar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/combineFields.png"))); // NOI18N
    combineFieldsToolbar1.setToolTipText("Combine Fields");
    combineFieldsToolbar1.setFocusable(false);
    combineFieldsToolbar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    combineFieldsToolbar1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    combineFieldsToolbar1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        combineFieldsToolbar1ActionPerformed(evt);
      }
    });
    filetoolbar.add(combineFieldsToolbar1);

    getGenderToolbar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/getSex.gif"))); // NOI18N
    getGenderToolbar1.setToolTipText("Get gender");
    getGenderToolbar1.setFocusable(false);
    getGenderToolbar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    getGenderToolbar1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    getGenderToolbar1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        getGenderToolbar1ActionPerformed(evt);
      }
    });
    filetoolbar.add(getGenderToolbar1);

    lastNameVocativeAddressingToolbar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/addressing.gif"))); // NOI18N
    lastNameVocativeAddressingToolbar1.setToolTipText("Get vocative addressing");
    lastNameVocativeAddressingToolbar1.setFocusable(false);
    lastNameVocativeAddressingToolbar1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    lastNameVocativeAddressingToolbar1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    lastNameVocativeAddressingToolbar1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lastNameVocativeAddressingToolbar1ActionPerformed(evt);
      }
    });
    filetoolbar.add(lastNameVocativeAddressingToolbar1);

    removeFiltersToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/removeFilter.png"))); // NOI18N
    removeFiltersToolbar.setToolTipText("Get vocative addressing");
    removeFiltersToolbar.setEnabled(false);
    removeFiltersToolbar.setFocusable(false);
    removeFiltersToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    removeFiltersToolbar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    removeFiltersToolbar.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        removeFiltersToolbarActionPerformed(evt);
      }
    });
    filetoolbar.add(removeFiltersToolbar);

    getContentPane().add(filetoolbar, java.awt.BorderLayout.NORTH);

    splitPane_main.setBorder(null);
    splitPane_main.setDividerLocation(300);
    splitPane_main.setDividerSize(11);
    splitPane_main.setForeground(new java.awt.Color(0, 0, 0));
    splitPane_main.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
    splitPane_main.setResizeWeight(1.0);
    splitPane_main.setAutoscrolls(true);
    splitPane_main.setContinuousLayout(true);
    splitPane_main.setMinimumSize(new java.awt.Dimension(650, 435));
    splitPane_main.setName("split"); // NOI18N
    splitPane_main.setOneTouchExpandable(true);
    splitPane_main.setPreferredSize(new java.awt.Dimension(868, 435));

    samplePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("File Sample"));
    samplePanel.setMinimumSize(new java.awt.Dimension(480, 200));
    samplePanel.setPreferredSize(new java.awt.Dimension(800, 600));
    samplePanel.setRequestFocusEnabled(false);

    sampleTablePane.setMinimumSize(new java.awt.Dimension(0, 0));
    sampleTablePane.setPreferredSize(new java.awt.Dimension(800, 200));

    sampleTable.setFont(new java.awt.Font("Monospaced", 0, 12));
    sampleTable.setModel(model);
    sampleTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
    sampleTable.setColumnSelectionAllowed(true);
    sampleTable.setDragEnabled(true);
    sampleTable.setMaximumSize(new java.awt.Dimension(2000, 2000));
    sampleTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    sampleTable.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseReleased(java.awt.event.MouseEvent evt) {
        sampleTableMouseReleased(evt);
      }
    });
    sampleTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseMoved(java.awt.event.MouseEvent evt) {
        sampleTableMouseMoved(evt);
      }
    });
    sampleTablePane.setViewportView(sampleTable);

    javax.swing.GroupLayout samplePanelLayout = new javax.swing.GroupLayout(samplePanel);
    samplePanel.setLayout(samplePanelLayout);
    samplePanelLayout.setHorizontalGroup(
      samplePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(samplePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(sampleTablePane, javax.swing.GroupLayout.DEFAULT_SIZE, 888, Short.MAX_VALUE)
        .addContainerGap())
    );
    samplePanelLayout.setVerticalGroup(
      samplePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(samplePanelLayout.createSequentialGroup()
        .addComponent(sampleTablePane, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
        .addContainerGap())
    );

    splitPane_main.setLeftComponent(samplePanel);

    outputPanel.setMaximumSize(new java.awt.Dimension(32767, 350));
    outputPanel.setMinimumSize(new java.awt.Dimension(900, 300));
    outputPanel.setPreferredSize(new java.awt.Dimension(900, 350));

    panel_quickOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Quick Options"));
    panel_quickOptions.setPreferredSize(new java.awt.Dimension(300, 276));

    textField_updateRows.setForeground(new java.awt.Color(204, 204, 204));
    textField_updateRows.setText("Sample Rows");
    textField_updateRows.setToolTipText("Update sample number of rows");
    textField_updateRows.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        textField_updateRowsFocusGained(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        textField_updateRowsFocusLost(evt);
      }
    });
    textField_updateRows.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        textField_updateRowsKeyReleased(evt);
      }
    });

    combobox_checkOnSaving.setModel(new DefaultComboBoxModel(new Object[] {DirectMail.Options.DmOptions._NOTHING_,DirectMail.Options.DmOptions._LENGTH_OF_LINES_,DirectMail.Options.DmOptions._NUMBER_OF_FIELDS_,DirectMail.Options.DmOptions._BOTH_}));
    combobox_checkOnSaving.setSelectedItem(options.toString(DmOptions.CHECK_ON_SAVING));
    combobox_checkOnSaving.setName(DirectMail.Options.DmOptions.CHECK_ON_SAVING);
    combobox_checkOnSaving.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        combobox_checkOnSavingActionPerformed(evt);
      }
    });

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText("Check On Saving");

    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel7.setText("Samples shown");

    jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel8.setText("DB Host");

    combobox_database.setModel(databasesModel);
    combobox_database.setSelectedItem(options.toString(DmOptions.HOST));
    combobox_database.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        combobox_databaseActionPerformed(evt);
      }
    });

    mainHostIcon.setToolTipText("Connection to MySQL Database");
    mainHostIcon.setBorder(null);
    mainHostIcon.setBorderPainted(false);
    mainHostIcon.setContentAreaFilled(false);
    mainHostIcon.setDefaultCapable(false);
    mainHostIcon.setFocusPainted(false);
    mainHostIcon.setFocusable(false);
    mainHostIcon.setIconTextGap(0);
    mainHostIcon.setName("0"); // NOI18N
    mainHostIcon.setRequestFocusEnabled(false);
    mainHostIcon.setRolloverEnabled(false);
    mainHostIcon.setVerifyInputWhenFocusTarget(false);

    jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel9.setText("Max Length of Field");

    textField_maxLength.setText(options.toString(DmOptions.MAX_FIELD_LENGTH));
    textField_maxLength.setToolTipText("Change the maximum length of each field");
    textField_maxLength.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        textField_maxLengthFocusGained(evt);
      }
    });
    textField_maxLength.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        textField_maxLengthKeyReleased(evt);
      }
    });

    jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel10.setText("Sample Font size");

    comboBox_sampleFontSize.setEditable(true);
    comboBox_sampleFontSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "8", "9", "10", "11", "12", "13", "14", "15", "16" }));
    comboBox_sampleFontSize.setSelectedItem(options.toInt(DmOptions.FONT_SIZE));
    comboBox_sampleFontSize.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        comboBox_sampleFontSizeActionPerformed(evt);
      }
    });

    erpIcon.setToolTipText("Connection to ERP Database");
    erpIcon.setBorder(null);
    erpIcon.setBorderPainted(false);
    erpIcon.setContentAreaFilled(false);
    erpIcon.setDefaultCapable(false);
    erpIcon.setIconTextGap(0);
    erpIcon.setName("0"); // NOI18N

    jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel11.setText("Show output progress");

    checkbox_showOutPutProggress.setSelected(options.toBoolean(DmOptions.SHOW_OUTPUT_PROGRESS));
    checkbox_showOutPutProggress.setMargin(new java.awt.Insets(0, 0, 0, 0));
    checkbox_showOutPutProggress.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkbox_showOutPutProggressActionPerformed(evt);
      }
    });

    jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel12.setText("Don't bother me");

    checkbox_dontBotherMe.setSelected(options.toBoolean(DmOptions.SHOW_OUTPUT_PROGRESS));
    checkbox_dontBotherMe.setMargin(new java.awt.Insets(0, 0, 0, 0));
    checkbox_dontBotherMe.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkbox_dontBotherMeActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout panel_quickOptionsLayout = new javax.swing.GroupLayout(panel_quickOptions);
    panel_quickOptions.setLayout(panel_quickOptionsLayout);
    panel_quickOptionsLayout.setHorizontalGroup(
      panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(panel_quickOptionsLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
          .addGroup(panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel10)
            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel3)
            .addComponent(jLabel7))
          .addGroup(panel_quickOptionsLayout.createSequentialGroup()
            .addGap(22, 22, 22)
            .addComponent(erpIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(mainHostIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(comboBox_sampleFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(checkbox_showOutPutProggress)
          .addComponent(checkbox_dontBotherMe)
          .addGroup(panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
            .addComponent(textField_updateRows, javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(combobox_checkOnSaving, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(combobox_database, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(textField_maxLength, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)))
        .addContainerGap(33, Short.MAX_VALUE))
    );

    panel_quickOptionsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel11, jLabel12, jLabel3, jLabel7, jLabel9});

    panel_quickOptionsLayout.setVerticalGroup(
      panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(panel_quickOptionsLayout.createSequentialGroup()
        .addGroup(panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
          .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(textField_updateRows, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
          .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(combobox_checkOnSaving, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
          .addGroup(panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
            .addComponent(combobox_database, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(mainHostIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(erpIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
          .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(textField_maxLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
          .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(comboBox_sampleFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
          .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(checkbox_showOutPutProggress))
        .addGap(10, 10, 10)
        .addGroup(panel_quickOptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
          .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(checkbox_dontBotherMe))
        .addGap(86, 86, 86))
    );

    panel_output.setBorder(javax.swing.BorderFactory.createTitledBorder("Output"));

    jScrollPane3.setAutoscrolls(true);
    jScrollPane3.setMaximumSize(new java.awt.Dimension(32767, 63));
    jScrollPane3.setMinimumSize(new java.awt.Dimension(500, 0));
    jScrollPane3.setPreferredSize(new java.awt.Dimension(143, 40));

    textAreaOutput.setColumns(20);
    textAreaOutput.setEditable(false);
    textAreaOutput.setFont(new java.awt.Font("Monospaced", 0, 12));
    jScrollPane3.setViewportView(textAreaOutput);

    buttonClearOutput.setText("Clear");
    buttonClearOutput.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonClearOutputActionPerformed(evt);
      }
    });

    progressBar.setBackground(new java.awt.Color(102, 102, 102));
    progressBar.setStringPainted(true);

    jLabel2.setText("Progress:");

    remainingTimeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    remainingTimeLabel.setText("E.T.A.: 00:00:00");

    javax.swing.GroupLayout panel_outputLayout = new javax.swing.GroupLayout(panel_output);
    panel_output.setLayout(panel_outputLayout);
    panel_outputLayout.setHorizontalGroup(
      panel_outputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(panel_outputLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(panel_outputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_outputLayout.createSequentialGroup()
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(panel_outputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(labelRow, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(labelCol, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addGroup(panel_outputLayout.createSequentialGroup()
            .addComponent(buttonClearOutput)
            .addGap(6, 6, 6)
            .addComponent(jLabel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(remainingTimeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)))
        .addContainerGap())
    );
    panel_outputLayout.setVerticalGroup(
      panel_outputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_outputLayout.createSequentialGroup()
        .addGroup(panel_outputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
          .addGroup(panel_outputLayout.createSequentialGroup()
            .addComponent(labelRow, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(labelCol, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(panel_outputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonClearOutput)
          .addComponent(jLabel2)
          .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(remainingTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)))
    );

    panel_outputLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {progressBar, remainingTimeLabel});

    tabPanel.setMinimumSize(new java.awt.Dimension(566, 124));
    tabPanel.setPreferredSize(new java.awt.Dimension(566, 124));

    genOptionsPanel.setMaximumSize(new java.awt.Dimension(300, 105));
    genOptionsPanel.setMinimumSize(new java.awt.Dimension(187, 105));
    genOptionsPanel.setPreferredSize(new java.awt.Dimension(187, 105));

    jPanel1.setPreferredSize(new java.awt.Dimension(280, 105));

    textBoxFields.setEditable(false);
    textBoxFields.setMinimumSize(new java.awt.Dimension(4, 16));

    textBoxDelimeter.setEditable(false);
    textBoxDelimeter.setMaximumSize(new java.awt.Dimension(60, 20));
    textBoxDelimeter.setMinimumSize(new java.awt.Dimension(40, 20));
    textBoxDelimeter.setPreferredSize(new java.awt.Dimension(40, 20));

    textBoxTmpFilename.setEditable(false);
    textBoxTmpFilename.setMaximumSize(new java.awt.Dimension(160, 20));
    textBoxTmpFilename.setMinimumSize(new java.awt.Dimension(160, 20));
    textBoxTmpFilename.setPreferredSize(new java.awt.Dimension(160, 20));

    labelFields.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelFields.setText("Fields :");

    labelDelimeter.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelDelimeter.setText("Delimeter :");

    labelTmpFilename.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelTmpFilename.setText("Tmp Filename :");

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText("File :");

    textBoxOpenedFile.setEditable(false);
    textBoxOpenedFile.setMaximumSize(new java.awt.Dimension(160, 20));
    textBoxOpenedFile.setMinimumSize(new java.awt.Dimension(160, 20));
    textBoxOpenedFile.setPreferredSize(new java.awt.Dimension(160, 20));

    labelEncodingIn.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelEncodingIn.setText("Input Encoding:");

    labelEncodingOut.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelEncodingOut.setText("Output Encoding :");

    labelCustomers.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelCustomers.setText("Customers :");

    textBoxEncodingIn.setEditable(false);
    textBoxEncodingIn.setPreferredSize(new java.awt.Dimension(85, 20));

    textBoxEncodingOut.setEditable(false);
    textBoxEncodingOut.setPreferredSize(new java.awt.Dimension(85, 20));

    textBoxCustomers.setEditable(false);
    textBoxCustomers.setPreferredSize(new java.awt.Dimension(85, 20));

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
          .addComponent(labelDelimeter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
          .addComponent(labelTmpFilename, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addComponent(textBoxDelimeter, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
              .addGap(3, 3, 3)
              .addComponent(labelFields, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(textBoxFields, javax.swing.GroupLayout.DEFAULT_SIZE, 53, Short.MAX_VALUE))
            .addComponent(textBoxTmpFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(textBoxOpenedFile, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
            .addComponent(labelEncodingOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(labelEncodingIn, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(labelCustomers, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(textBoxEncodingIn, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
          .addComponent(textBoxEncodingOut, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
          .addComponent(textBoxCustomers, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(textBoxOpenedFile, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(labelEncodingIn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(textBoxEncodingIn, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
          .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(textBoxTmpFilename, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
          .addComponent(labelEncodingOut, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(textBoxEncodingOut, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
          .addComponent(labelTmpFilename, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelCustomers, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(textBoxCustomers, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
          .addComponent(textBoxFields, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
          .addComponent(labelFields, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(textBoxDelimeter, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
          .addComponent(labelDelimeter, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(22, 22, 22))
    );

    jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, labelDelimeter, labelFields, labelTmpFilename});

    jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {labelCustomers, labelEncodingIn, labelEncodingOut});

    jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {textBoxCustomers, textBoxDelimeter, textBoxEncodingIn, textBoxEncodingOut, textBoxFields, textBoxTmpFilename});

    javax.swing.GroupLayout genOptionsPanelLayout = new javax.swing.GroupLayout(genOptionsPanel);
    genOptionsPanel.setLayout(genOptionsPanelLayout);
    genOptionsPanelLayout.setHorizontalGroup(
      genOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(genOptionsPanelLayout.createSequentialGroup()
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 525, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(16, Short.MAX_VALUE))
    );
    genOptionsPanelLayout.setVerticalGroup(
      genOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
    );

    tabPanel.addTab("General", genOptionsPanel);

    memoryPanel.setMaximumSize(new java.awt.Dimension(32767, 50));
    memoryPanel.setPreferredSize(new java.awt.Dimension(198, 50));

    tf_maxMemory.setBackground(Skin.getSkinColor());
    tf_maxMemory.setEditable(false);
    tf_maxMemory.setFont(tf_maxMemory.getFont().deriveFont(tf_maxMemory.getFont().getSize()-1f));
    tf_maxMemory.setBorder(null);
    tf_maxMemory.setMaximumSize(new java.awt.Dimension(100, 15));
    tf_maxMemory.setMinimumSize(new java.awt.Dimension(100, 15));
    tf_maxMemory.setOpaque(false);
    tf_maxMemory.setPreferredSize(new java.awt.Dimension(100, 15));

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText("Max:");

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText("Total:");

    tf_totalMemory.setBackground(Skin.getSkinColor());
    tf_totalMemory.setEditable(false);
    tf_totalMemory.setFont(tf_totalMemory.getFont().deriveFont(tf_totalMemory.getFont().getSize()-1f));
    tf_totalMemory.setHorizontalAlignment(javax.swing.JTextField.LEFT);
    tf_totalMemory.setBorder(null);
    tf_totalMemory.setMaximumSize(new java.awt.Dimension(100, 15));
    tf_totalMemory.setMinimumSize(new java.awt.Dimension(100, 15));
    tf_totalMemory.setOpaque(false);
    tf_totalMemory.setPreferredSize(new java.awt.Dimension(100, 15));

    tf_usedMemory.setBackground(Skin.getSkinColor());
    tf_usedMemory.setEditable(false);
    tf_usedMemory.setFont(tf_usedMemory.getFont().deriveFont(tf_usedMemory.getFont().getSize()-1f));
    tf_usedMemory.setHorizontalAlignment(javax.swing.JTextField.LEFT);
    tf_usedMemory.setBorder(null);
    tf_usedMemory.setMaximumSize(new java.awt.Dimension(100, 15));
    tf_usedMemory.setMinimumSize(new java.awt.Dimension(100, 15));
    tf_usedMemory.setOpaque(false);
    tf_usedMemory.setPreferredSize(new java.awt.Dimension(100, 15));

    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel6.setText("Used:");

    layeredPane.setBackground(new java.awt.Color(51, 255, 0));
    layeredPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
    layeredPane.setMaximumSize(new java.awt.Dimension(140, 20));
    layeredPane.setMinimumSize(new java.awt.Dimension(100, 20));
    layeredPane.setOpaque(true);
    layeredPane.setPreferredSize(new java.awt.Dimension(140, 20));

    javax.swing.GroupLayout memoryPanelLayout = new javax.swing.GroupLayout(memoryPanel);
    memoryPanel.setLayout(memoryPanelLayout);
    memoryPanelLayout.setHorizontalGroup(
      memoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(memoryPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(memoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(layeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addGroup(memoryPanelLayout.createSequentialGroup()
            .addGroup(memoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(jLabel6)
              .addComponent(jLabel5)
              .addComponent(jLabel4))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(memoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(tf_usedMemory, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(tf_totalMemory, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(tf_maxMemory, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
        .addContainerGap(379, Short.MAX_VALUE))
    );
    memoryPanelLayout.setVerticalGroup(
      memoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(memoryPanelLayout.createSequentialGroup()
        .addGroup(memoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(tf_maxMemory, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(memoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel5)
          .addComponent(tf_totalMemory, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(memoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel6)
          .addComponent(tf_usedMemory, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(layeredPane, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(78, 78, 78))
    );

    tabPanel.addTab("Memory", memoryPanel);

    javax.swing.GroupLayout outputPanelLayout = new javax.swing.GroupLayout(outputPanel);
    outputPanel.setLayout(outputPanelLayout);
    outputPanelLayout.setHorizontalGroup(
      outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(outputPanelLayout.createSequentialGroup()
        .addGroup(outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(outputPanelLayout.createSequentialGroup()
            .addGap(12, 12, 12)
            .addComponent(tabPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 546, Short.MAX_VALUE))
          .addComponent(panel_output, javax.swing.GroupLayout.Alignment.TRAILING, 0, 558, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(panel_quickOptions, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE))
    );
    outputPanelLayout.setVerticalGroup(
      outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, outputPanelLayout.createSequentialGroup()
        .addGroup(outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(panel_quickOptions, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
          .addGroup(outputPanelLayout.createSequentialGroup()
            .addComponent(panel_output, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(tabPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap())
    );

    splitPane_main.setBottomComponent(outputPanel);

    getContentPane().add(splitPane_main, java.awt.BorderLayout.CENTER);

    mainMenuBar.setBorder(null);
    mainMenuBar.setMaximumSize(new java.awt.Dimension(500, 21));
    mainMenuBar.setOpaque(false);
    mainMenuBar.setPreferredSize(new java.awt.Dimension(500, 21));

    fileMenu.setBorder(null);
    fileMenu.setMnemonic('F');
    fileMenu.setText("File");

    openFileMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
    openFileMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/open.gif"))); // NOI18N
    openFileMenuItem.setMnemonic('o');
    openFileMenuItem.setText("Open");
    openFileMenuItem.setToolTipText("<html>Opens the file browser for selecting a file to import in the application");
    openFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        openFileMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(openFileMenuItem);

    importMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/import.gif"))); // NOI18N
    importMenuItem.setText("Import");

    importExcellMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/excel.gif"))); // NOI18N
    importExcellMenuItem.setText("Excel File");
    importExcellMenuItem.setToolTipText("Imports an excel file in the application");
    importExcellMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        importExcellMenuItemActionPerformed(evt);
      }
    });
    importMenuItem.add(importExcellMenuItem);

    importDbMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/database.gif"))); // NOI18N
    importDbMenuItem.setText("Database");
    importDbMenuItem.setToolTipText("Imports data from a database in the application");
    importDbMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        importDbMenuItemActionPerformed(evt);
      }
    });
    importMenuItem.add(importDbMenuItem);

    fileMenu.add(importMenuItem);

    jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/export.png"))); // NOI18N
    jMenu1.setText("Export");

    exportDBMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/database.png"))); // NOI18N
    exportDBMenuItem.setText(" mySQL DB");
    exportDBMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        exportDBMenuItemActionPerformed(evt);
      }
    });
    jMenu1.add(exportDBMenuItem);

    fileMenu.add(jMenu1);

    undoMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/undo.gif"))); // NOI18N
    undoMenuItem.setText("Undo");
    undoMenuItem.setToolTipText("Undoes the last action");
    undoMenuItem.setEnabled((undoId == 0) ? false : true);
    undoMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        undoMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(undoMenuItem);

    appendFileMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/append.gif"))); // NOI18N
    appendFileMenuItem.setText("Append File");
    appendFileMenuItem.setToolTipText("Appends a file to the open one");
    appendFileMenuItem.setEnabled(false);
    appendFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        appendFileMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(appendFileMenuItem);

    saveFileMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
    saveFileMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/save.gif"))); // NOI18N
    saveFileMenuItem.setMnemonic('s');
    saveFileMenuItem.setText("Save as...");
    saveFileMenuItem.setToolTipText("saves a file");
    saveFileMenuItem.setEnabled(false);
    saveFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveFileMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(saveFileMenuItem);

    revertFileMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/revert.gif"))); // NOI18N
    revertFileMenuItem.setText("Revert");
    revertFileMenuItem.setToolTipText("Reloads the opened file");
    revertFileMenuItem.setEnabled(false);
    revertFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        revertFileMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(revertFileMenuItem);

    closeFileMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/close.gif"))); // NOI18N
    closeFileMenuItem.setText("Close");
    closeFileMenuItem.setToolTipText("Closes the current open file");
    closeFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        closeFileMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(closeFileMenuItem);

    templatesMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/template.gif"))); // NOI18N
    templatesMenu.setText("Templates");

    createTemplateMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/createTemplate.gif"))); // NOI18N
    createTemplateMenuItem.setText("Create Template");
    createTemplateMenuItem.setToolTipText("Creates a PCADD template");
    createTemplateMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        createTemplateMenuItemActionPerformed(evt);
      }
    });
    templatesMenu.add(createTemplateMenuItem);

    applyTemplateMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/applyTemplate.gif"))); // NOI18N
    applyTemplateMenuItem.setText("Apply Template");
    applyTemplateMenuItem.setToolTipText("Applies a template to the current file");
    applyTemplateMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        applyTemplateMenuItemActionPerformed(evt);
      }
    });
    templatesMenu.add(applyTemplateMenuItem);

    saveTemplateMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/saveTemplate.gif"))); // NOI18N
    saveTemplateMenuItem.setText("Save as Template");
    saveTemplateMenuItem.setToolTipText("Saves the current file as template");
    saveTemplateMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveTemplateMenuItemActionPerformed(evt);
      }
    });
    templatesMenu.add(saveTemplateMenuItem);

    fileMenu.add(templatesMenu);

    jSeparator1.setName("separator"); // NOI18N
    fileMenu.add(jSeparator1);

    quitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
    quitMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/quit.gif"))); // NOI18N
    quitMenuItem.setMnemonic('q');
    quitMenuItem.setText("Quit");
    quitMenuItem.setToolTipText("Exits the program");
    quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        quitMenuItemActionPerformed(evt);
      }
    });
    fileMenu.add(quitMenuItem);

    mainMenuBar.add(fileMenu);

    preEnvelopingMenu.setBorder(null);
    preEnvelopingMenu.setMnemonic('R');
    preEnvelopingMenu.setText("Pre-Enveloping");
    preEnvelopingMenu.setToolTipText("<html>\n<b>Create a file with samples</b><br />\nYou select the fields of which you want unique samples <br />\nand the maximum number of samples<br /> \n(leaving the textfield blank results in getting the<br />\n maximum samples returned by the query)");
    preEnvelopingMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        preEnvelopingMenuActionPerformed(evt);
      }
    });

    sortByEltaFlagMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
    sortByEltaFlagMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/flag.png"))); // NOI18N
    sortByEltaFlagMenuItem.setMnemonic('n');
    sortByEltaFlagMenuItem.setText("Sort by Elta Flag");
    sortByEltaFlagMenuItem.setToolTipText("<html>\nSorts the file and adds the elta flag: <br>\n0 : Abroad <br>\n1: Cyprus <br>\n2: Uknown TK <br>\n3:  Not standard Envelope <br>\n4: Attica <br>\n5: North Greece <br>\n6: Rest of Greece <br>");
    sortByEltaFlagMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        sortByEltaFlagMenuItemActionPerformed(evt);
      }
    });
    preEnvelopingMenu.add(sortByEltaFlagMenuItem);

    sortByNumOfTkMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/numOfTK.png"))); // NOI18N
    sortByNumOfTkMenuItem.setText("Sort by Num of Tk");
    sortByNumOfTkMenuItem.setToolTipText("<html>\nSorts the file by the number of Tk");
    sortByNumOfTkMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        sortByNumOfTkMenuItemActionPerformed(evt);
      }
    });
    preEnvelopingMenu.add(sortByNumOfTkMenuItem);

    addFlags_menuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/flag.png"))); // NOI18N
    addFlags_menuItem.setText("Do not Sort");
    addFlags_menuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addFlags_menuItemActionPerformed(evt);
      }
    });
    preEnvelopingMenu.add(addFlags_menuItem);

    fixedFileActionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
    fixedFileActionMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/fixed.gif"))); // NOI18N
    fixedFileActionMenuItem.setMnemonic('f');
    fixedFileActionMenuItem.setText("Make Fixed File");
    fixedFileActionMenuItem.setToolTipText("Make a delimeted file fixed");
    fixedFileActionMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fixedFileActionMenuItemActionPerformed(evt);
      }
    });
    preEnvelopingMenu.add(fixedFileActionMenuItem);

    formatTkActionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
    formatTkActionMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/formatTK.gif"))); // NOI18N
    formatTkActionMenuItem.setMnemonic('f');
    formatTkActionMenuItem.setText("Format Zip Code");
    formatTkActionMenuItem.setToolTipText("<html>\nFormats the TK fields to <b>@XXXXX</b>");
    formatTkActionMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        formatTkActionMenuItemActionPerformed(evt);
      }
    });
    preEnvelopingMenu.add(formatTkActionMenuItem);

    combineTkCityMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
    combineTkCityMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/combineTkCity.gif"))); // NOI18N
    combineTkCityMenuItem.setMnemonic('b');
    combineTkCityMenuItem.setText("Combine Zip Code & City");
    combineTkCityMenuItem.setToolTipText("<html>\nCombines the TK and the City field");
    combineTkCityMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        combineTkCityMenuItemActionPerformed(evt);
      }
    });
    preEnvelopingMenu.add(combineTkCityMenuItem);

    zipCodesFlagActionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
    zipCodesFlagActionMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/flag.gif"))); // NOI18N
    zipCodesFlagActionMenuItem.setMnemonic('z');
    zipCodesFlagActionMenuItem.setText("Zip Flag");
    zipCodesFlagActionMenuItem.setToolTipText("<html>\n<b>Not used anymore</b><br>\nAdds a zip codes flag");
    zipCodesFlagActionMenuItem.setEnabled(false);
    zipCodesFlagActionMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        zipCodesFlagActionMenuItemActionPerformed(evt);
      }
    });
    preEnvelopingMenu.add(zipCodesFlagActionMenuItem);

    addCounterActionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
    addCounterActionMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/addCounter.gif"))); // NOI18N
    addCounterActionMenuItem.setMnemonic('c');
    addCounterActionMenuItem.setText("Add counter");
    addCounterActionMenuItem.setToolTipText("<html>\nAdds a counter to the file<br>\nAsks if should add nothing, \"1\" or \"11\" at the end of the 6digit counter\n");
    addCounterActionMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addCounterActionMenuItemActionPerformed(evt);
      }
    });
    preEnvelopingMenu.add(addCounterActionMenuItem);

    shiftFieldsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/shiftFields.gif"))); // NOI18N
    shiftFieldsMenuItem.setText("Shift Fields");
    shiftFieldsMenuItem.setToolTipText("<html>\nShifts the selected fields to the right so all empty columns should be on the left");
    shiftFieldsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        shiftFieldsMenuItemActionPerformed(evt);
      }
    });
    preEnvelopingMenu.add(shiftFieldsMenuItem);

    changeDelimeterMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
    changeDelimeterMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/changeDelimeter.png"))); // NOI18N
    changeDelimeterMenuItem.setMnemonic('h');
    changeDelimeterMenuItem.setText("Change Delimeter");
    changeDelimeterMenuItem.setToolTipText("<html>\nChanges the delimeter from <br>\nTAB to #<br>\n# to ;<br>\n; to TAB<br>");
    changeDelimeterMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        changeDelimeterMenuItemActionPerformed(evt);
      }
    });
    preEnvelopingMenu.add(changeDelimeterMenuItem);

    envelopeTestMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/testEnvelope.png"))); // NOI18N
    envelopeTestMenuItem.setText("Envelope Test");
    envelopeTestMenuItem.setToolTipText("<html>\nSearches the <b>final</b> pcadd file and gets the 5 id's with the most label information. The id's are displayed in the output textarea");
    envelopeTestMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        envelopeTestMenuItemActionPerformed(evt);
      }
    });
    preEnvelopingMenu.add(envelopeTestMenuItem);

    jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/samples.png"))); // NOI18N
    jMenuItem2.setText("Create Samples");
    jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem2ActionPerformed(evt);
      }
    });
    preEnvelopingMenu.add(jMenuItem2);
    preEnvelopingMenu.add(jSeparator5);

    addressFileMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/addressFile.gif"))); // NOI18N
    addressFileMenuItem.setText("Address File");
    addressFileMenuItem.setToolTipText("<html>\n<b>Not implemented yet</b><br>\nRun all the address file commands\n");
    addressFileMenuItem.setEnabled(false);
    addressFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addressFileMenuItemActionPerformed(evt);
      }
    });
    preEnvelopingMenu.add(addressFileMenuItem);

    mainMenuBar.add(preEnvelopingMenu);

    postEnvelopingMenu.setMnemonic('O');
    postEnvelopingMenu.setText("Post-Enveloping");

    notEnvelopedActionsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
    notEnvelopedActionsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/notEnv.gif"))); // NOI18N
    notEnvelopedActionsMenuItem.setMnemonic('e');
    notEnvelopedActionsMenuItem.setText("Not Enveloped");
    notEnvelopedActionsMenuItem.setToolTipText("Makes a not enveloped file");
    notEnvelopedActionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        notEnvelopedActionsMenuItemActionPerformed(evt);
      }
    });
    postEnvelopingMenu.add(notEnvelopedActionsMenuItem);

    notEnvelopedIDsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
    notEnvelopedIDsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/notEnvId.gif"))); // NOI18N
    notEnvelopedIDsMenuItem.setMnemonic('i');
    notEnvelopedIDsMenuItem.setText("Not Enveloped IDs");
    notEnvelopedIDsMenuItem.setToolTipText("Not enveloped ids");
    notEnvelopedIDsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        notEnvelopedIDsMenuItemActionPerformed(evt);
      }
    });
    postEnvelopingMenu.add(notEnvelopedIDsMenuItem);

    reprintFileActionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
    reprintFileActionMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/reprint.gif"))); // NOI18N
    reprintFileActionMenuItem.setMnemonic('r');
    reprintFileActionMenuItem.setText("Reprint File");
    reprintFileActionMenuItem.setToolTipText("makes a reprint file");
    reprintFileActionMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        reprintFileActionMenuItemActionPerformed(evt);
      }
    });
    postEnvelopingMenu.add(reprintFileActionMenuItem);

    mergeReportsActionMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/mergeReports.gif"))); // NOI18N
    mergeReportsActionMenu.setText("Merge Reports");
    mergeReportsActionMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        mergeReportsActionMenuActionPerformed(evt);
      }
    });
    postEnvelopingMenu.add(mergeReportsActionMenu);

    mainMenuBar.add(postEnvelopingMenu);

    zipCodeMenu.setText("Zip Code");
    zipCodeMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        zipCodeMenuActionPerformed(evt);
      }
    });

    tkReportMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
    tkReportMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/tkReport.gif"))); // NOI18N
    tkReportMenuItem.setMnemonic('t');
    tkReportMenuItem.setText("Zip Code Report");
    tkReportMenuItem.setToolTipText("Generates a report for the TK Bundles");
    tkReportMenuItem.setEnabled(false);
    tkReportMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        tkReportMenuItemActionPerformed(evt);
      }
    });
    zipCodeMenu.add(tkReportMenuItem);
    tkReportMenuItem.setVisible(false);

    tkLabelsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/tkLabels.gif"))); // NOI18N
    tkLabelsMenuItem.setText("Zip Code Labels");
    tkLabelsMenuItem.setToolTipText("Prints Zip Code labels to PDF");
    tkLabelsMenuItem.setEnabled(false);
    tkLabelsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        tkLabelsMenuItemActionPerformed(evt);
      }
    });
    zipCodeMenu.add(tkLabelsMenuItem);
    tkLabelsMenuItem.setVisible(false);

    tkLabelsFromPCADDMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/tkLabelsPcadd.gif"))); // NOI18N
    tkLabelsFromPCADDMenuItem.setText("Zip Code Labels From PCADD");
    tkLabelsFromPCADDMenuItem.setToolTipText("Create a Labels PDF from a PCADD File");
    tkLabelsFromPCADDMenuItem.setEnabled(false);
    tkLabelsFromPCADDMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        tkLabelsFromPCADDMenuItemActionPerformed(evt);
      }
    });
    zipCodeMenu.add(tkLabelsFromPCADDMenuItem);
    tkLabelsFromPCADDMenuItem.setVisible(false);

    mainMenuBar.add(zipCodeMenu);
    zipCodeMenu.setVisible(false);

    correctMenu.setText("Correcting");

    findMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/find.png"))); // NOI18N
    findMenu.setText("Find");

    findTKMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/findTK.gif"))); // NOI18N
    findTKMenuItem.setText("Find Zip Code");
    findTKMenuItem.setToolTipText("Finds Zip Codes");
    findTKMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        findTKMenuItemActionPerformed(evt);
      }
    });
    findMenu.add(findTKMenuItem);

    tkFindCity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/findCity.gif"))); // NOI18N
    tkFindCity.setText("Find City");
    tkFindCity.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        tkFindCityActionPerformed(evt);
      }
    });
    findMenu.add(tkFindCity);

    findCountyMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/findCounty.png"))); // NOI18N
    findCountyMenuItem.setText("Find County");
    findCountyMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        findCountyMenuItemActionPerformed(evt);
      }
    });
    findMenu.add(findCountyMenuItem);

    correctMenu.add(findMenu);

    correctmenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/correct.png"))); // NOI18N
    correctmenu.setText("Correct");

    correctAddressCity.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/correctCityAddress.png"))); // NOI18N
    correctAddressCity.setText("Correct Address/City");
    correctAddressCity.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        correctAddressCityActionPerformed(evt);
      }
    });
    correctmenu.add(correctAddressCity);

    menuItem_correcrFirstnames.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/editUser.gif"))); // NOI18N
    menuItem_correcrFirstnames.setText("Correct Firstnames");
    menuItem_correcrFirstnames.setToolTipText("Corrects the firstnames");
    menuItem_correcrFirstnames.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuItem_correcrFirstnamesActionPerformed(evt);
      }
    });
    correctmenu.add(menuItem_correcrFirstnames);

    correctMenu.add(correctmenu);

    fillEmptyAddresses.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/fillAddresses.png"))); // NOI18N
    fillEmptyAddresses.setText("Fill Empty Addresses");
    fillEmptyAddresses.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fillEmptyAddressesActionPerformed(evt);
      }
    });
    correctMenu.add(fillEmptyAddresses);

    extractZipCode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/splitNames.png"))); // NOI18N
    extractZipCode.setText("Extract Zipcode");
    extractZipCode.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        extractZipCodeActionPerformed(evt);
      }
    });
    correctMenu.add(extractZipCode);

    mainMenuBar.add(correctMenu);

    addressingMenu.setText("Addressing");

    menuSplitNames.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/splitNames.png"))); // NOI18N
    menuSplitNames.setText("Split Firstname/Surname");
    menuSplitNames.setToolTipText("<html>\nSplits the fullname in first name(s) and surname(s) creating 2 new fields.<br />\nIf it's is a company all the fullname is copied to the surname field");
    menuSplitNames.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuSplitNamesActionPerformed(evt);
      }
    });
    addressingMenu.add(menuSplitNames);

    getGenderMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
    getGenderMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/getSex.gif"))); // NOI18N
    getGenderMenuItem.setMnemonic('g');
    getGenderMenuItem.setText("GetGender");
    getGenderMenuItem.setToolTipText("Gets the Gender of each record");
    getGenderMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        getGenderMenuItemActionPerformed(evt);
      }
    });
    addressingMenu.add(getGenderMenuItem);

    menuFirstname.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/addressingVoc.gif"))); // NOI18N
    menuFirstname.setText("Firstname");

    firstNameVocativeAddressingMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/addressing.gif"))); // NOI18N
    firstNameVocativeAddressingMenuItem.setText("Vocative Addressing");
    firstNameVocativeAddressingMenuItem.setToolTipText("Κώστα, Δημήτρη κλπ");
    firstNameVocativeAddressingMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        firstNameVocativeAddressingMenuItemActionPerformed(evt);
      }
    });
    menuFirstname.add(firstNameVocativeAddressingMenuItem);

    addressingMenu.add(menuFirstname);
    menuFirstname.setVisible(false);

    menuLastname.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/addressingVoc.gif"))); // NOI18N
    menuLastname.setText("Create Addressing");

    lastNameVocativeAddressingMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
    lastNameVocativeAddressingMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/addressing.gif"))); // NOI18N
    lastNameVocativeAddressingMenuItem.setMnemonic('A');
    lastNameVocativeAddressingMenuItem.setText("Vocative Addressing");
    lastNameVocativeAddressingMenuItem.setToolTipText("<html> Vocative Addressing (eg \"Αγαπητέ κύριε Παπαδόπουλε,\")");
    lastNameVocativeAddressingMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lastNameVocativeAddressingMenuItemActionPerformed(evt);
      }
    });
    menuLastname.add(lastNameVocativeAddressingMenuItem);

    lastnameGenitiveAddressingMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/addressingVoc.gif"))); // NOI18N
    lastnameGenitiveAddressingMenuItem.setText("Genitive Addressing");
    lastnameGenitiveAddressingMenuItem.setToolTipText("<html>Genative Addressing (πχ του κύριου Παπαδόπουλου Γιάννη)");
    lastnameGenitiveAddressingMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lastnameGenitiveAddressingMenuItemActionPerformed(evt);
      }
    });
    menuLastname.add(lastnameGenitiveAddressingMenuItem);

    lastNameAccusativeAddressingMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/addressing.gif"))); // NOI18N
    lastNameAccusativeAddressingMenuItem.setText("Accusative Addressing");
    lastNameAccusativeAddressingMenuItem.setToolTipText("<html>Accusative Addressing (πχ τον κύριο Παπαδόπουλο Γιάννη )");
    lastNameAccusativeAddressingMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        lastNameAccusativeAddressingMenuItemActionPerformed(evt);
      }
    });
    menuLastname.add(lastNameAccusativeAddressingMenuItem);

    addressingMenu.add(menuLastname);

    addNamesMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/namesEdit.gif"))); // NOI18N
    addNamesMenuItem.setText("Open names for editing");
    addNamesMenuItem.setToolTipText("<html>Opens a surnames; file for adding accents");
    addNamesMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addNamesMenuItemActionPerformed(evt);
      }
    });
    addressingMenu.add(addNamesMenuItem);

    mainMenuBar.add(addressingMenu);

    checkFileMenu.setMnemonic('c');
    checkFileMenu.setText("Check file");
    checkFileMenu.setToolTipText("Checks a file");
    checkFileMenu.setActionCommand("Check File");

    checkTKMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
    checkTKMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/checkTK.gif"))); // NOI18N
    checkTKMenuItem.setText("Check TK");
    checkTKMenuItem.setToolTipText("<html>\nChecks if the TK is in the TK database and adds the elta flag: <br>\n0 : Abroad <br>\n1: Cyprus <br>\n2: Uknown TK <br>\n3:  Not standard Envelope <br>\n4: Attica <br>\n5: North Greece <br>\n6: Rest of Greece <br>");
    checkTKMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkTKMenuItemActionPerformed(evt);
      }
    });
    checkFileMenu.add(checkTKMenuItem);

    checkFieldsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
    checkFieldsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/checkFields.gif"))); // NOI18N
    checkFieldsMenuItem.setMnemonic('f');
    checkFieldsMenuItem.setText("Check Fields");
    checkFieldsMenuItem.setToolTipText("Checks the number of the fields");
    checkFieldsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkFieldsMenuItemActionPerformed(evt);
      }
    });
    checkFileMenu.add(checkFieldsMenuItem);

    checkLengthMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
    checkLengthMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/checkLength.gif"))); // NOI18N
    checkLengthMenuItem.setMnemonic('l');
    checkLengthMenuItem.setText("Check Length");
    checkLengthMenuItem.setToolTipText("Checks the length of each line");
    checkLengthMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkLengthMenuItemActionPerformed(evt);
      }
    });
    checkFileMenu.add(checkLengthMenuItem);

    checkControlChrMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
    checkControlChrMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/checkChars.gif"))); // NOI18N
    checkControlChrMenuItem.setMnemonic('c');
    checkControlChrMenuItem.setText("Check Ctrl Chars");
    checkControlChrMenuItem.setToolTipText("Checks a file for control characters");
    checkControlChrMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkControlChrMenuItemActionPerformed(evt);
      }
    });
    checkFileMenu.add(checkControlChrMenuItem);

    checkAddressMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/checkAddress.gif"))); // NOI18N
    checkAddressMenuItem.setText("Check Address");
    checkAddressMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkAddressMenuItemActionPerformed(evt);
      }
    });
    checkFileMenu.add(checkAddressMenuItem);

    checkList13MenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/list13.gif"))); // NOI18N
    checkList13MenuItem.setText("Check List 13");
    checkList13MenuItem.setToolTipText("<html>Checks the file for persons in List 13 and removes them from the file<br />\nIt checks the Surname, the first 3 letters of the firstname and /or TK and City");
    checkList13MenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkList13MenuItemActionPerformed(evt);
      }
    });
    checkFileMenu.add(checkList13MenuItem);

    mainMenuBar.add(checkFileMenu);

    toolsMenu.setMnemonic('t');
    toolsMenu.setText("Tools");

    fieldToolsMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/fieldMenu.gif"))); // NOI18N
    fieldToolsMenu.setText("Field Tools");

    combineFieldsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/combineFields.png"))); // NOI18N
    combineFieldsMenuItem.setText("Combine Fields");
    combineFieldsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        combineFieldsMenuItemActionPerformed(evt);
      }
    });
    fieldToolsMenu.add(combineFieldsMenuItem);

    addFieldMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/addField.gif"))); // NOI18N
    addFieldMenuItem.setText("Add Field");
    addFieldMenuItem.setToolTipText("Adds a field in each record");
    addFieldMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addFieldMenuItemActionPerformed(evt);
      }
    });
    fieldToolsMenu.add(addFieldMenuItem);

    deleteFieldsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/deleteFields.png"))); // NOI18N
    deleteFieldsMenuItem.setText("Delete Fields");
    deleteFieldsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteFieldsMenuItemActionPerformed(evt);
      }
    });
    fieldToolsMenu.add(deleteFieldsMenuItem);

    multisortingMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/multisort.png"))); // NOI18N
    multisortingMenuItem.setText("Multisorting");
    multisortingMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        multisortingMenuItemActionPerformed(evt);
      }
    });
    fieldToolsMenu.add(multisortingMenuItem);

    toolsMenu.add(fieldToolsMenu);

    fileToolsMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/fileToolsMenu.gif"))); // NOI18N
    fileToolsMenu.setText("File Tools");

    splitFileMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
    splitFileMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/splitFile.gif"))); // NOI18N
    splitFileMenuItem.setText("Split File");
    splitFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        splitFileMenuItemActionPerformed(evt);
      }
    });
    fileToolsMenu.add(splitFileMenuItem);

    jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/mergeFiles.png"))); // NOI18N
    jMenuItem3.setText("Merge Files");
    jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem3ActionPerformed(evt);
      }
    });
    fileToolsMenu.add(jMenuItem3);

    toolsMenu.add(fileToolsMenu);

    productionToolsMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/productionTools.gif"))); // NOI18N
    productionToolsMenu.setText("Production Tools");

    duplexMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/duplex.gif"))); // NOI18N
    duplexMenuItem.setText("Make Duplex");
    duplexMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        duplexMenuItemActionPerformed(evt);
      }
    });
    productionToolsMenu.add(duplexMenuItem);

    doubleProductionMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/doubleProduction.gif"))); // NOI18N
    doubleProductionMenuItem.setText("Double Production");
    doubleProductionMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        doubleProductionMenuItemActionPerformed(evt);
      }
    });
    productionToolsMenu.add(doubleProductionMenuItem);

    multiplePrintingMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/multiplePrinting.gif"))); // NOI18N
    multiplePrintingMenuItem.setText("Multiple Printing");
    multiplePrintingMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        multiplePrintingMenuItemActionPerformed(evt);
      }
    });
    productionToolsMenu.add(multiplePrintingMenuItem);

    doubleSplittedMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/splitDouble.png"))); // NOI18N
    doubleSplittedMenuItem.setText("Double Splitted");
    doubleSplittedMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        doubleSplittedMenuItemActionPerformed(evt);
      }
    });
    productionToolsMenu.add(doubleSplittedMenuItem);

    multiplePagesMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/mergeFiles.png"))); // NOI18N
    multiplePagesMenuItem.setText("Multiple Pages");
    multiplePagesMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        multiplePagesMenuItemActionPerformed(evt);
      }
    });
    productionToolsMenu.add(multiplePagesMenuItem);

    toolsMenu.add(productionToolsMenu);

    linesToolsMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/lineTools.gif"))); // NOI18N
    linesToolsMenu.setText("Line Tools");

    multiplyLinesMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/multiplyLines.gif"))); // NOI18N
    multiplyLinesMenuItem.setText("Multiply Lines");
    multiplyLinesMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        multiplyLinesMenuItemActionPerformed(evt);
      }
    });
    linesToolsMenu.add(multiplyLinesMenuItem);

    deleteDuplicatesMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/deleteDupl.gif"))); // NOI18N
    deleteDuplicatesMenuItem.setText("Delete Duplicates");
    deleteDuplicatesMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteDuplicatesMenuItemActionPerformed(evt);
      }
    });
    linesToolsMenu.add(deleteDuplicatesMenuItem);

    deleteFromFileMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/deleteFromList.png"))); // NOI18N
    deleteFromFileMenuItem.setText("Delete from List");
    deleteFromFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        deleteFromFileMenuItemActionPerformed(evt);
      }
    });
    linesToolsMenu.add(deleteFromFileMenuItem);

    toolsMenu.add(linesToolsMenu);

    convertMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/convert.png"))); // NOI18N
    convertMenu.setText("Convert");

    capitilizeMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/capitilize.gif"))); // NOI18N
    capitilizeMenuItem.setText("Capitilize");
    capitilizeMenuItem.setToolTipText("Capitalizes all data");
    capitilizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        capitilizeMenuItemActionPerformed(evt);
      }
    });
    convertMenu.add(capitilizeMenuItem);

    convertGreekMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/convertGreek.png"))); // NOI18N
    convertGreekMenuItem.setText("Convert to Greek");
    convertGreekMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        convertGreekMenuItemActionPerformed(evt);
      }
    });
    convertMenu.add(convertGreekMenuItem);

    convertAmountMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/amountConvert.gif"))); // NOI18N
    convertAmountMenuItem.setText("Convert Amount");
    convertAmountMenuItem.setToolTipText("Converts a numeric ammount to textual ");
    convertAmountMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        convertAmountMenuItemActionPerformed(evt);
      }
    });
    convertMenu.add(convertAmountMenuItem);

    trimFieldsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/trimFields.png"))); // NOI18N
    trimFieldsMenuItem.setText("Trim Fileds");
    trimFieldsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        trimFieldsMenuItemActionPerformed(evt);
      }
    });
    convertMenu.add(trimFieldsMenuItem);

    toolsMenu.add(convertMenu);

    jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/misc.png"))); // NOI18N
    jMenu2.setText("Misc");

    codelineMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/codeline.gif"))); // NOI18N
    codelineMenu.setText("Codeline");
    codelineMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        codelineMenuActionPerformed(evt);
      }
    });
    jMenu2.add(codelineMenu);

    menuItemCheckDigit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/checkDigit.gif"))); // NOI18N
    menuItemCheckDigit.setText("Check Digit");
    menuItemCheckDigit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuItemCheckDigitActionPerformed(evt);
      }
    });
    jMenu2.add(menuItemCheckDigit);

    menuItemHolidays.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/priest.png"))); // NOI18N
    menuItemHolidays.setText("Holidays");
    menuItemHolidays.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuItemHolidaysActionPerformed(evt);
      }
    });
    jMenu2.add(menuItemHolidays);

    jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/barcode.png"))); // NOI18N
    jMenuItem4.setText("Registered Code");
    jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem4ActionPerformed(evt);
      }
    });
    jMenu2.add(jMenuItem4);

    jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/random.png"))); // NOI18N
    jMenuItem5.setText("Random Code");
    jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem5ActionPerformed(evt);
      }
    });
    jMenu2.add(jMenuItem5);

    toolsMenu.add(jMenu2);

    jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/update.png"))); // NOI18N
    jMenu3.setText("Update");

    exportList13MenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/list13.gif"))); // NOI18N
    exportList13MenuItem.setText("List 13");
    exportList13MenuItem.setToolTipText("<html>\n<b>Exports list13 into the dafermos table in directmail database</b><br>\nFirst open the list13.csv file (Better delete the header before the import).<br>\nThen choose export-List13 . Only the records that are not already in the databse will be inserted");
    exportList13MenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        exportList13MenuItemActionPerformed(evt);
      }
    });
    jMenu3.add(exportList13MenuItem);

    menuItem_updateFnamesDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/editUser.gif"))); // NOI18N
    menuItem_updateFnamesDB.setText("FirstNames");
    menuItem_updateFnamesDB.setToolTipText("Update the plain column of fnames with the new names that have been inserted in the DB");
    menuItem_updateFnamesDB.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuItem_updateFnamesDBActionPerformed(evt);
      }
    });
    jMenu3.add(menuItem_updateFnamesDB);

    jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/erp.png"))); // NOI18N
    jMenuItem1.setText("Customers");
    jMenuItem1.setToolTipText("<html>Updates the MySQL PAvendor table with data from the ERP database");
    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jMenuItem1ActionPerformed(evt);
      }
    });
    jMenu3.add(jMenuItem1);

    toolsMenu.add(jMenu3);
    toolsMenu.add(jSeparator3);

    optionsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
    optionsMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/options.gif"))); // NOI18N
    optionsMenuItem.setMnemonic('o');
    optionsMenuItem.setText("Options");
    optionsMenuItem.setToolTipText("Application Options");
    optionsMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        optionsMenuItemActionPerformed(evt);
      }
    });
    toolsMenu.add(optionsMenuItem);

    mainMenuBar.add(toolsMenu);

    usersMenu.setText("Users");

    addUserMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/addUser.gif"))); // NOI18N
    addUserMenuItem.setText("Add user");
    addUserMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addUserMenuItemActionPerformed(evt);
      }
    });
    usersMenu.add(addUserMenuItem);

    editUserMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/editUser.gif"))); // NOI18N
    editUserMenuItem.setText("Edit User");
    editUserMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        editUserMenuItemActionPerformed(evt);
      }
    });
    usersMenu.add(editUserMenuItem);

    mainMenuBar.add(usersMenu);

    helpMenu.setMnemonic('h');
    helpMenu.setText("Help");
    helpMenu.setToolTipText("Help");

    helpHelpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
    helpHelpMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/help.gif"))); // NOI18N
    helpHelpMenuItem.setMnemonic('H');
    helpHelpMenuItem.setText("Help");
    helpHelpMenuItem.setToolTipText("Help about the program");
    helpHelpMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        helpHelpMenuItemActionPerformed(evt);
      }
    });
    helpMenu.add(helpHelpMenuItem);

    logsHelpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_J, java.awt.event.InputEvent.CTRL_MASK));
    logsHelpMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/warning.gif"))); // NOI18N
    logsHelpMenuItem.setMnemonic('j');
    logsHelpMenuItem.setText("Log");
    logsHelpMenuItem.setToolTipText("<html>\nOpens the default internet browser and displays the log file");
    logsHelpMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        logsHelpMenuItemActionPerformed(evt);
      }
    });
    helpMenu.add(logsHelpMenuItem);

    aboutHelpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
    aboutHelpMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/about.gif"))); // NOI18N
    aboutHelpMenuItem.setText("About");
    aboutHelpMenuItem.setToolTipText("About the program");
    aboutHelpMenuItem.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        aboutHelpMenuItemActionPerformed(evt);
      }
    });
    helpMenu.add(aboutHelpMenuItem);

    menuItem_update.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/update.png"))); // NOI18N
    menuItem_update.setText("Update");
    menuItem_update.setEnabled(checkForUpdates()!=null);
    menuItem_update.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        menuItem_updateActionPerformed(evt);
      }
    });
    helpMenu.add(menuItem_update);

    mainMenuBar.add(helpMenu);

    setJMenuBar(mainMenuBar);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void popUpBreakFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpBreakFieldActionPerformed
    action.breakField(this);

  }//GEN-LAST:event_popUpBreakFieldActionPerformed

  private void markFieldAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markFieldAddressActionPerformed
    setHeader("ADDRESS");
    setAddressField(selectedColumn);
  }//GEN-LAST:event_markFieldAddressActionPerformed

  private void markFieldSurnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markFieldSurnameActionPerformed
    setHeader("SURNAME");
    setSurnameField(selectedColumn);
  }//GEN-LAST:event_markFieldSurnameActionPerformed

  private void markFieldFirstnameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markFieldFirstnameActionPerformed
    setHeader("FIRST NAME");
    setFirstnameField(selectedColumn);
  }//GEN-LAST:event_markFieldFirstnameActionPerformed

  private void markFieldCityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markFieldCityActionPerformed
    setHeader("CITY");
    setCityField(selectedColumn);
  }//GEN-LAST:event_markFieldCityActionPerformed

  private void markFieldUnmarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markFieldUnmarkActionPerformed
    setHeader("" + selectedColumn);
    rearrangeHeaders();
  }//GEN-LAST:event_markFieldUnmarkActionPerformed

  private void markFieldGenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markFieldGenderActionPerformed
    setHeader("GENDER");
    setGenderField(selectedColumn);
  }//GEN-LAST:event_markFieldGenderActionPerformed

  private void markFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markFieldActionPerformed
  }//GEN-LAST:event_markFieldActionPerformed

  private void markFieldFullNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markFieldFullNameActionPerformed
    //setTkField(selectedColumn);
    setFullnameField(selectedColumn);
    setHeader("FULLNAME");
  }//GEN-LAST:event_markFieldFullNameActionPerformed

  private void markFieldTKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markFieldTKActionPerformed
    if (isLabeling()) {
      SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          SelectLabelQuantities sl = new SelectLabelQuantities(MainForm.this);

        }
      });
    }
    setTkField(selectedColumn);
    setHeader("ZIP CODE");
  }//GEN-LAST:event_markFieldTKActionPerformed

  private void popupPadFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popupPadFieldActionPerformed
    action.padField(this);
}//GEN-LAST:event_popupPadFieldActionPerformed

  private void popUpDeleteColActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpDeleteColActionPerformed
    action.deleteColumn(this);
  }//GEN-LAST:event_popUpDeleteColActionPerformed

  private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
    action.formWindowOpen(this);
  }//GEN-LAST:event_formWindowOpened

  private void createTemplateMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createTemplateMenuItemActionPerformed
    action.createTemplate(this);
  }//GEN-LAST:event_createTemplateMenuItemActionPerformed

  private void applyTemplateMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyTemplateMenuItemActionPerformed
    action.applyTemplate(this);
}//GEN-LAST:event_applyTemplateMenuItemActionPerformed

  private void saveTemplateMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveTemplateMenuItemActionPerformed
    action.saveTemplate(this);
  }//GEN-LAST:event_saveTemplateMenuItemActionPerformed

  private void multiplyLinesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiplyLinesMenuItemActionPerformed
    action.multiplyLines(this);
  }//GEN-LAST:event_multiplyLinesMenuItemActionPerformed

  private void fixedToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedToolbarActionPerformed
    fixedFileActionMenuItemActionPerformed(evt);
  }//GEN-LAST:event_fixedToolbarActionPerformed

  private void addCounterToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCounterToolbarActionPerformed
    addCounterActionMenuItemActionPerformed(evt);
  }//GEN-LAST:event_addCounterToolbarActionPerformed

  private void setHeader(String title) {

    if (selectedColumn > -1) {
      for (int i = 0; i < getHeaderTitles().size(); i++) {
        TableColumn column = null;
        column = getColModel().getColumn(i);
        //column.setHeaderValue("Field " + i);
        if (column.getModelIndex() == selectedColumn) {
          column.setHeaderValue(title);
          getHeaderTitles().setElementAt(title, i);
        } else if (column.getHeaderValue().equals(title)) {
          column.setHeaderValue("" + i);
          getHeaderTitles().setElementAt("" + i, i);
        }
      }
      sampleTable.getTableHeader().resizeAndRepaint();
      Common.renderSampleTableHeaders(this);
      Common.unMarkFields(this);
      rearrangeHeaders();
    }

  }

  private void addFieldMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFieldMenuItemActionPerformed
    action.addField(this);
  }//GEN-LAST:event_addFieldMenuItemActionPerformed

  private void undoToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoToolbarActionPerformed
    undoMenuItemActionPerformed(evt);
  }//GEN-LAST:event_undoToolbarActionPerformed

  private void undoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoMenuItemActionPerformed
    action.undo(this);
  }//GEN-LAST:event_undoMenuItemActionPerformed

  private void buttonClearOutput1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClearOutput1ActionPerformed
  }//GEN-LAST:event_buttonClearOutput1ActionPerformed

  private void textArea1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_textArea1MouseDragged
  }//GEN-LAST:event_textArea1MouseDragged

  private void findTKMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findTKMenuItemActionPerformed
    action.findTk(this);
  }//GEN-LAST:event_findTKMenuItemActionPerformed

  private void FileToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileToolbarActionPerformed
    try {
      Common.closeProgram(this);
    } catch (IOException ex) {
      myLog.log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_FileToolbarActionPerformed

  private void revertToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertToolbarActionPerformed
    revertFileMenuItemActionPerformed(evt);
  }//GEN-LAST:event_revertToolbarActionPerformed

  private void saveToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveToolbarActionPerformed
    saveFileMenuItemActionPerformed(evt);
  }//GEN-LAST:event_saveToolbarActionPerformed

  private void openToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openToolbarActionPerformed
    openFileMenuItemActionPerformed(evt);
  }//GEN-LAST:event_openToolbarActionPerformed

  private void combineFieldsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combineFieldsMenuItemActionPerformed
    action.combineFields(this);
  }//GEN-LAST:event_combineFieldsMenuItemActionPerformed

  private void checkAddressMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAddressMenuItemActionPerformed
    action.checkAddress(this);
  }//GEN-LAST:event_checkAddressMenuItemActionPerformed

  private void revertFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertFileMenuItemActionPerformed
    action.revert(this);
  }//GEN-LAST:event_revertFileMenuItemActionPerformed

  private void doubleProductionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doubleProductionMenuItemActionPerformed
    action.doubleProduction(this);
  }//GEN-LAST:event_doubleProductionMenuItemActionPerformed

  private void tkLabelsFromPCADDMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tkLabelsFromPCADDMenuItemActionPerformed
    action.tkLabelsFromPcadd(this);
  }//GEN-LAST:event_tkLabelsFromPCADDMenuItemActionPerformed

  private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    try {
      Common.closeProgram(this);
    } catch (IOException ex) {
      myLog.log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_formWindowClosing

  private void tkLabelsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tkLabelsMenuItemActionPerformed
    action.tkLabelsFromPcadd(this);
  }//GEN-LAST:event_tkLabelsMenuItemActionPerformed

  private void multiplePrintingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiplePrintingMenuItemActionPerformed
    action.multiplePrinting(this);
  }//GEN-LAST:event_multiplePrintingMenuItemActionPerformed

  private void zipCodeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zipCodeMenuActionPerformed
  }//GEN-LAST:event_zipCodeMenuActionPerformed

  private void lastnameGenitiveAddressingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastnameGenitiveAddressingMenuItemActionPerformed
    try {
      action.lastNameGenitiveAddressing(this);
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_lastnameGenitiveAddressingMenuItemActionPerformed

  private void addNamesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNamesMenuItemActionPerformed
    action.addNames(this);
  }//GEN-LAST:event_addNamesMenuItemActionPerformed
  //TODO Double production exo meinei

  private void duplexMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_duplexMenuItemActionPerformed
    action.duplex(this);
  }//GEN-LAST:event_duplexMenuItemActionPerformed

  private void combineTkCityMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combineTkCityMenuItemActionPerformed
    action.combineTkCity(this);
  }//GEN-LAST:event_combineTkCityMenuItemActionPerformed

  private void formatTkActionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formatTkActionMenuItemActionPerformed
    action.formatTk(this);
  }//GEN-LAST:event_formatTkActionMenuItemActionPerformed

  private void splitFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_splitFileMenuItemActionPerformed
    action.splitFile(this);
  }//GEN-LAST:event_splitFileMenuItemActionPerformed

  private void checkTKMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkTKMenuItemActionPerformed
    action.checkTk(this);

  }//GEN-LAST:event_checkTKMenuItemActionPerformed

  private void checkControlChrMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkControlChrMenuItemActionPerformed
    action.checkControlChars(this);
  }//GEN-LAST:event_checkControlChrMenuItemActionPerformed

  private void logsHelpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logsHelpMenuItemActionPerformed
    action.logs(this);
}//GEN-LAST:event_logsHelpMenuItemActionPerformed

  private void checkLengthMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkLengthMenuItemActionPerformed
    action.checkLength(this);
  }//GEN-LAST:event_checkLengthMenuItemActionPerformed

  private void checkFieldsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkFieldsMenuItemActionPerformed
    action.checkFields(this);
  }//GEN-LAST:event_checkFieldsMenuItemActionPerformed

  private void lastNameVocativeAddressingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastNameVocativeAddressingMenuItemActionPerformed
    try {
      action.lastNameVocativeAddressing(this);
    } catch (SQLException ex) {
      myLog.log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_lastNameVocativeAddressingMenuItemActionPerformed

  private void tkReportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tkReportMenuItemActionPerformed
    action.tkReport(this);
  }//GEN-LAST:event_tkReportMenuItemActionPerformed

  private void aboutHelpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutHelpMenuItemActionPerformed
    action.about(this);
  }//GEN-LAST:event_aboutHelpMenuItemActionPerformed

  private void helpHelpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpHelpMenuItemActionPerformed
    action.help(this);
  }//GEN-LAST:event_helpHelpMenuItemActionPerformed

  private void getGenderMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getGenderMenuItemActionPerformed
    //action.getGender(this);
    action.getGenderNew(this);
  }//GEN-LAST:event_getGenderMenuItemActionPerformed

  private void changeDelimeterMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeDelimeterMenuItemActionPerformed
    action.changeDelimeter(this);
  }//GEN-LAST:event_changeDelimeterMenuItemActionPerformed

  private void optionsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionsMenuItemActionPerformed
    action.options(this);
  }//GEN-LAST:event_optionsMenuItemActionPerformed

  public void reApplyStyle() {
    action.reApplyStyle(this);
  }

  private void addCounterActionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCounterActionMenuItemActionPerformed
    action.addCounter(this);
  }//GEN-LAST:event_addCounterActionMenuItemActionPerformed

  private void sortByEltaFlagMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortByEltaFlagMenuItemActionPerformed
    action.sortByEltaFlag(this);
  }//GEN-LAST:event_sortByEltaFlagMenuItemActionPerformed

  private void zipCodesFlagActionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zipCodesFlagActionMenuItemActionPerformed
    action.zipCodeFlag(this);
}//GEN-LAST:event_zipCodesFlagActionMenuItemActionPerformed

  private void notEnvelopedIDsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notEnvelopedIDsMenuItemActionPerformed
    action.notEnvelopedIds(this);
  }//GEN-LAST:event_notEnvelopedIDsMenuItemActionPerformed

  private void appendFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appendFileMenuItemActionPerformed
  }//GEN-LAST:event_appendFileMenuItemActionPerformed

  private void preEnvelopingMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preEnvelopingMenuActionPerformed
  }//GEN-LAST:event_preEnvelopingMenuActionPerformed

  private void reprintFileActionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reprintFileActionMenuItemActionPerformed
    action.reprintFile(this);
  }//GEN-LAST:event_reprintFileActionMenuItemActionPerformed

  private void notEnvelopedActionsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notEnvelopedActionsMenuItemActionPerformed
    action.notEnveloped(this);
  }//GEN-LAST:event_notEnvelopedActionsMenuItemActionPerformed

  private void fixedFileActionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedFileActionMenuItemActionPerformed
    action.fixedFile(this);
  }//GEN-LAST:event_fixedFileActionMenuItemActionPerformed

  private void openFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileMenuItemActionPerformed
    action.openFile(this, evt);
  }//GEN-LAST:event_openFileMenuItemActionPerformed

  private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuItemActionPerformed
    try {
      Common.closeProgram(this);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_quitMenuItemActionPerformed

  private void saveFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFileMenuItemActionPerformed
    action.saveFile(this);
  }//GEN-LAST:event_saveFileMenuItemActionPerformed

  private void popUpDeleteFirstLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpDeleteFirstLineActionPerformed
    action.deleteFirstLine(this);
  }//GEN-LAST:event_popUpDeleteFirstLineActionPerformed

  private void addressFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addressFileMenuItemActionPerformed
}//GEN-LAST:event_addressFileMenuItemActionPerformed

  private void addUserMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addUserMenuItemActionPerformed
    action.addUser();
  }//GEN-LAST:event_addUserMenuItemActionPerformed

  private void editUserMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editUserMenuItemActionPerformed
    action.editUser();
}//GEN-LAST:event_editUserMenuItemActionPerformed

  private void importExcellMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importExcellMenuItemActionPerformed
    action.importExcel(this);
}//GEN-LAST:event_importExcellMenuItemActionPerformed

  private void importDbMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importDbMenuItemActionPerformed
    action.importDB(this);
  }//GEN-LAST:event_importDbMenuItemActionPerformed

  private void deleteDuplicatesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDuplicatesMenuItemActionPerformed
    action.deleteDuplicates(this);
}//GEN-LAST:event_deleteDuplicatesMenuItemActionPerformed

  private void convertAmountMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_convertAmountMenuItemActionPerformed
    action.convertAmount(this);
  }//GEN-LAST:event_convertAmountMenuItemActionPerformed

  private void markFieldAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markFieldAmountActionPerformed
    setAmountField(selectedColumn);
    setHeader("AMOUNT");
  }//GEN-LAST:event_markFieldAmountActionPerformed

  private void closeCurrentFile(Users users, java.awt.event.ActionEvent evt) {
    Common.closeCurrentFile(this, users, evt);
  }

  private void closeFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeFileMenuItemActionPerformed
    action.closeFile(this);
  }//GEN-LAST:event_closeFileMenuItemActionPerformed

  private void formWindowIconified(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowIconified
    setVisible(false);
  }//GEN-LAST:event_formWindowIconified

  private void popUpSortAscActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpSortAscActionPerformed
    action.popUpSort(this, selectedColumn, true);
  }//GEN-LAST:event_popUpSortAscActionPerformed

  private void lastNameAccusativeAddressingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastNameAccusativeAddressingMenuItemActionPerformed
    try {
      action.lastNameAccusativeAddressing(this);
    } catch (SQLException ex) {
      myLog.log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_lastNameAccusativeAddressingMenuItemActionPerformed

private void menuItemCheckDigitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCheckDigitActionPerformed
  action.checkDigit(this);
}//GEN-LAST:event_menuItemCheckDigitActionPerformed

private void popUpSearchReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpSearchReplaceActionPerformed
  action.searchReplace(this, selectedColumn);
}//GEN-LAST:event_popUpSearchReplaceActionPerformed

private void shiftFieldsToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shiftFieldsToolbarActionPerformed
  shiftFieldsMenuItemActionPerformed(evt);
}//GEN-LAST:event_shiftFieldsToolbarActionPerformed

private void shiftFieldsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shiftFieldsMenuItemActionPerformed
  action.shiftFields(this);
}//GEN-LAST:event_shiftFieldsMenuItemActionPerformed

private void FormatTKToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FormatTKToolbarActionPerformed
  formatTkActionMenuItemActionPerformed(evt);
}//GEN-LAST:event_FormatTKToolbarActionPerformed

private void CombineTKCityToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CombineTKCityToolbarActionPerformed
  combineTkCityMenuItemActionPerformed(evt);
}//GEN-LAST:event_CombineTKCityToolbarActionPerformed

private void markFieldCountryFlagActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markFieldCountryFlagActionPerformed
  setAbroadField(selectedColumn);
  setHeader("COUNTRY");
}//GEN-LAST:event_markFieldCountryFlagActionPerformed

private void popUpConvertGreekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpConvertGreekActionPerformed
  action.convertGreek(this, selectedColumn);
}//GEN-LAST:event_popUpConvertGreekActionPerformed

private void tkFindCityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tkFindCityActionPerformed
  action.findCity(this);
}//GEN-LAST:event_tkFindCityActionPerformed

private void checkList13MenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkList13MenuItemActionPerformed
  action.checkList13(this);
}//GEN-LAST:event_checkList13MenuItemActionPerformed

private void firstNameVocativeAddressingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstNameVocativeAddressingMenuItemActionPerformed
  //action.firstNameVocativeAddressing(this);
}//GEN-LAST:event_firstNameVocativeAddressingMenuItemActionPerformed

private void capitilizeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_capitilizeMenuItemActionPerformed
  action.capitalize(this, -1);
}//GEN-LAST:event_capitilizeMenuItemActionPerformed

private void popUpCapitalizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpCapitalizeActionPerformed
  action.capitalize(this, selectedColumn);
}//GEN-LAST:event_popUpCapitalizeActionPerformed

private void popUpAddColBeforeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpAddColBeforeActionPerformed
  action.addColumn(this, selectedColumn - 1);
}//GEN-LAST:event_popUpAddColBeforeActionPerformed

private void popUpAddColAfterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpAddColAfterActionPerformed
  action.addColumn(this, selectedColumn);
}//GEN-LAST:event_popUpAddColAfterActionPerformed

private void popUpTrimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpTrimActionPerformed
  action.trim(this);
}//GEN-LAST:event_popUpTrimActionPerformed

private void popUpDuplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpDuplicateActionPerformed
  action.duplicate(this);
}//GEN-LAST:event_popUpDuplicateActionPerformed

private void codelineMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codelineMenuActionPerformed
  action.codeline(this);
}//GEN-LAST:event_codelineMenuActionPerformed

private void mergeReportsActionMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mergeReportsActionMenuActionPerformed
  action.mergeReports(this);
}//GEN-LAST:event_mergeReportsActionMenuActionPerformed

private void correctAddressCityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_correctAddressCityActionPerformed
  action.correctAddresCity(this);
}//GEN-LAST:event_correctAddressCityActionPerformed

private void fillEmptyAddressesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillEmptyAddressesActionPerformed
  action.fillEmptyAddresses(this);
}//GEN-LAST:event_fillEmptyAddressesActionPerformed

private void convertGreekMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_convertGreekMenuItemActionPerformed
  action.convertGreek(this, -1);
}//GEN-LAST:event_convertGreekMenuItemActionPerformed

private void deleteFromFileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteFromFileMenuItemActionPerformed
  action.deleteFromFile(this);
}//GEN-LAST:event_deleteFromFileMenuItemActionPerformed

private void exportDBMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportDBMenuItemActionPerformed
  action.exportToDB(this);
}//GEN-LAST:event_exportDBMenuItemActionPerformed

private void extraTabButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extraTabButtonActionPerformed
  tabPanel.remove(2);
}//GEN-LAST:event_extraTabButtonActionPerformed

private void popUpFillBlanksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpFillBlanksActionPerformed
  action.fillBlankFields(this);
}//GEN-LAST:event_popUpFillBlanksActionPerformed

private void combineFieldsToolbar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combineFieldsToolbar1ActionPerformed
  combineFieldsMenuItemActionPerformed(evt);
}//GEN-LAST:event_combineFieldsToolbar1ActionPerformed

private void getGenderToolbar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getGenderToolbar1ActionPerformed
  getGenderMenuItemActionPerformed(evt);
}//GEN-LAST:event_getGenderToolbar1ActionPerformed

private void lastNameVocativeAddressingToolbar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastNameVocativeAddressingToolbar1ActionPerformed
  lastNameVocativeAddressingMenuItemActionPerformed(evt);
}//GEN-LAST:event_lastNameVocativeAddressingToolbar1ActionPerformed

private void exportList13MenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportList13MenuItemActionPerformed
  action.exportToList13(this);
}//GEN-LAST:event_exportList13MenuItemActionPerformed

private void popUpKeepFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpKeepFirstNameActionPerformed
  action.keepFirstWord(this);
}//GEN-LAST:event_popUpKeepFirstNameActionPerformed

private void doubleSplittedMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doubleSplittedMenuItemActionPerformed
  action.doubleSplitted(this);
}//GEN-LAST:event_doubleSplittedMenuItemActionPerformed

private void menuItemHolidaysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemHolidaysActionPerformed
  action.holidays(this);
}//GEN-LAST:event_menuItemHolidaysActionPerformed

private void menuItem_updateFnamesDBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_updateFnamesDBActionPerformed
  action.updateFirstNamesDB(this);
}//GEN-LAST:event_menuItem_updateFnamesDBActionPerformed

private void menuItem_correcrFirstnamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_correcrFirstnamesActionPerformed
  action.correctFirstName(this);
}//GEN-LAST:event_menuItem_correcrFirstnamesActionPerformed

private void testEnvelopeToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testEnvelopeToolbarActionPerformed
  action.testEnvelopes(this);
}//GEN-LAST:event_testEnvelopeToolbarActionPerformed

private void envelopeTestMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_envelopeTestMenuItemActionPerformed
  action.testEnvelopes(this);
}//GEN-LAST:event_envelopeTestMenuItemActionPerformed

private void findCountyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findCountyMenuItemActionPerformed
  action.findCounty(this);
}//GEN-LAST:event_findCountyMenuItemActionPerformed

private void menuSplitNamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSplitNamesActionPerformed
  action.splitNames(this);
}//GEN-LAST:event_menuSplitNamesActionPerformed

private void popUpSortDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpSortDescActionPerformed
  action.popUpSort(this, selectedColumn, false);
}//GEN-LAST:event_popUpSortDescActionPerformed

private void uniqueMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uniqueMenuItemActionPerformed
  action.unique(this);
}//GEN-LAST:event_uniqueMenuItemActionPerformed

private void sortByNumOfTkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortByNumOfTkMenuItemActionPerformed
  action.sortByNumOfTk(this);
}//GEN-LAST:event_sortByNumOfTkMenuItemActionPerformed

private void numOfTKToolbarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_numOfTKToolbarMouseReleased
  int x = 0, y = 0;
  if (!popUp_dropDownShowing) {
    if (numOfTKToolbar.getX() > numOfTKToolbar.getY()) {
      x = numOfTKToolbar.getX() - numOfTKToolbar.getWidth() - 10 - (6 * 27);
      y = numOfTKToolbar.getY() + numOfTKToolbar.getHeight() - 4;
    } else {
      x = numOfTKToolbar.getX() - numOfTKToolbar.getWidth() + 4;
      y = numOfTKToolbar.getY() + numOfTKToolbar.getHeight() - 10 - (7 * 27);
    }
    popUp_dropDown.show(numOfTKToolbar, x, y);
    popUp_dropDownShowing = true;
  } else {
    popUp_dropDown.setVisible(false);
    popUp_dropDownShowing = false;
  }
}//GEN-LAST:event_numOfTKToolbarMouseReleased

private void popUpSortByEltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpSortByEltaActionPerformed
  popUp_dropDown.setVisible(false);
  popUp_dropDownShowing = false;
  sortByEltaFlagMenuItemActionPerformed(evt);
}//GEN-LAST:event_popUpSortByEltaActionPerformed

private void popUpSortByNumOfTkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpSortByNumOfTkActionPerformed
  popUp_dropDown.setVisible(false);
  popUp_dropDownShowing = false;
  action.sortByNumOfTk(this);
}//GEN-LAST:event_popUpSortByNumOfTkActionPerformed

private void deleteFieldsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteFieldsMenuItemActionPerformed
  action.deleteFields(this);
}//GEN-LAST:event_deleteFieldsMenuItemActionPerformed

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
  action.updateErp(this);
}//GEN-LAST:event_jMenuItem1ActionPerformed

private void combo_delimeterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_delimeterActionPerformed
  if (action.isOpenFile(this, false)) {
    int d = combo_delimeter.getSelectedIndex();
    char del = Delimeters.toCharArray()[d];
    if (del != delimeter) {
      action.changeDelimeter(this, del);
    }
  }
}//GEN-LAST:event_combo_delimeterActionPerformed

private void samplesToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_samplesToolbarActionPerformed
  if (action.isOpenFile(this, false)) {
    action.createSamples(this);
  }
}//GEN-LAST:event_samplesToolbarActionPerformed

private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
  if (action.isOpenFile(this, false)) {
    action.createSamples(this);
  }
}//GEN-LAST:event_jMenuItem2ActionPerformed

private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
  action.mergeFiles(this, null);
}//GEN-LAST:event_jMenuItem3ActionPerformed

private void filterMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterMenuItemActionPerformed
  action.filterColumn(this, selectedColumn);
}//GEN-LAST:event_filterMenuItemActionPerformed

private void dataActionsMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_dataActionsMenuSelected
  markFilters();
}//GEN-LAST:event_dataActionsMenuSelected

private void removeFiltersMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFiltersMenuItemActionPerformed
  filters.clear();
  action.filterColumn(this, -1);
}//GEN-LAST:event_removeFiltersMenuItemActionPerformed

private void saveFilteredResultsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFilteredResultsMenuItemActionPerformed
  try {
    action.saveFilteredResults(this);
  } catch (IOException ex) {
    MainForm.myLog.log(Level.SEVERE, null, ex);
  }
}//GEN-LAST:event_saveFilteredResultsMenuItemActionPerformed

private void popUpPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_popUpPopupMenuWillBecomeVisible
  markFilters();
  if (filters.size() > 0) {
    dataActions.setEnabled(false);
    tableEdit.setEnabled(false);
    columnActions.setEnabled(false);
  } else {
    dataActions.setEnabled(true);
    tableEdit.setEnabled(true);
    columnActions.setEnabled(true);
  }
}//GEN-LAST:event_popUpPopupMenuWillBecomeVisible

private void filtersMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_filtersMenuMenuSelected
  markFilters();
}//GEN-LAST:event_filtersMenuMenuSelected

private void popUpReplaceFromDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpReplaceFromDbActionPerformed
  action.replaceFromDb(this);
}//GEN-LAST:event_popUpReplaceFromDbActionPerformed

private void multisortingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multisortingMenuItemActionPerformed
  action.multiSortingForm(this);
}//GEN-LAST:event_multisortingMenuItemActionPerformed

private void popUpFormatDecimalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpFormatDecimalActionPerformed
  action.decimalFormat(this);
}//GEN-LAST:event_popUpFormatDecimalActionPerformed

private void trimFieldsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trimFieldsMenuItemActionPerformed
  action.trimFields(this);
}//GEN-LAST:event_trimFieldsMenuItemActionPerformed

private void popUpAddFlagsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpAddFlagsActionPerformed
  action.addFlags(this);
}//GEN-LAST:event_popUpAddFlagsActionPerformed

private void addFlags_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFlags_menuItemActionPerformed
  action.addFlags(this);
}//GEN-LAST:event_addFlags_menuItemActionPerformed

private void menuItem_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem_updateActionPerformed
  System.out.println("Updating...");
  File destPath = new File(DmOptions._JAR_DIR_);
  File orPath = new File(updatesPath + "/DirectMail " + updatedVersion);
  try {
    System.out.println("Running updater");
    System.out.println("" + destPath + " " + orPath);
    String command = "java -jar \"" + DmOptions._JAR_DIR_
        + "lib\\DmUpdater.jar\" \"" + destPath + "\" \"" + orPath + "\"";
    System.out.println(command);
    Process p = Runtime.getRuntime().exec(command);
    // BufferedReader errStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    //String line = "";
    //while((line = errStream.readLine()) != null){
    //  System.out.println(line);
    //}
  } catch (IOException ex) {
    myLog.log(Level.SEVERE, null, ex);
  }
  System.exit(0);
}//GEN-LAST:event_menuItem_updateActionPerformed

private void multiplePagesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiplePagesMenuItemActionPerformed
  action.multiplePages(this);
}//GEN-LAST:event_multiplePagesMenuItemActionPerformed

private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
  action.addRegisteredCode(this);
}//GEN-LAST:event_jMenuItem4ActionPerformed

private void removeFiltersToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFiltersToolbarActionPerformed
  filters.clear();
  action.filterColumn(this, -1);
}//GEN-LAST:event_removeFiltersToolbarActionPerformed

private void buttonClearOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClearOutputActionPerformed
  textAreaOutput.setText("");
  progressBar.setValue(0);
  progressBar.setString("0%");
}//GEN-LAST:event_buttonClearOutputActionPerformed

private void checkbox_dontBotherMeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkbox_dontBotherMeActionPerformed
  // TODO add your handling code here:
}//GEN-LAST:event_checkbox_dontBotherMeActionPerformed

private void checkbox_showOutPutProggressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkbox_showOutPutProggressActionPerformed
  String sel = checkbox_showOutPutProggress.isSelected() ? "true" : "false";
  options.setOption(DmOptions.SHOW_OUTPUT_PROGRESS, sel);
  options.save();
}//GEN-LAST:event_checkbox_showOutPutProggressActionPerformed

private void comboBox_sampleFontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBox_sampleFontSizeActionPerformed
  float size = Float.parseFloat(String.valueOf(comboBox_sampleFontSize.getSelectedItem()));
  sampleTable.setFont(sampleTable.getFont().deriveFont(size));
  sampleTable.repaint();
  sampleTable.revalidate();
}//GEN-LAST:event_comboBox_sampleFontSizeActionPerformed

private void textField_maxLengthKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textField_maxLengthKeyReleased
  try {
    Integer.parseInt(textField_maxLength.getText());
    options.setOption(DmOptions.MAX_FIELD_LENGTH, textField_maxLength.getText());
    options.save();
    if (evt.getKeyCode() == Event.ENTER) {
      textField_maxLength.transferFocus();
    }
  } catch (NumberFormatException ex) {
    textField_maxLength.setText(options.toString(DmOptions.MAX_FIELD_LENGTH));
  }
}//GEN-LAST:event_textField_maxLengthKeyReleased

private void textField_maxLengthFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textField_maxLengthFocusGained
  if (textField_maxLength.getText().equals(String.valueOf(options.toInt(DmOptions.MAX_FIELD_LENGTH)))) {
    textField_maxLength.setText("");
    textField_maxLength.setForeground(Color.BLACK);
  }
}//GEN-LAST:event_textField_maxLengthFocusGained

private void combobox_databaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_databaseActionPerformed
  action.changeDB(this);
}//GEN-LAST:event_combobox_databaseActionPerformed

private void combobox_checkOnSavingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_checkOnSavingActionPerformed
  String checkOnSave = (String) combobox_checkOnSaving.getSelectedItem();
  options.setOption(DmOptions.CHECK_ON_SAVING, checkOnSave);
  options.save();
}//GEN-LAST:event_combobox_checkOnSavingActionPerformed

private void textField_updateRowsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textField_updateRowsKeyReleased
  if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
    action.updateSampleRows(this);
  }
}//GEN-LAST:event_textField_updateRowsKeyReleased

private void textField_updateRowsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textField_updateRowsFocusLost
  try {
    Integer.parseInt(textField_updateRows.getText().replaceAll("-", ""));
  } catch (NumberFormatException ex) {
    textField_updateRows.setText("Sample Rows");
    textField_updateRows.setForeground(Color.LIGHT_GRAY);
  }
}//GEN-LAST:event_textField_updateRowsFocusLost

private void textField_updateRowsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textField_updateRowsFocusGained

  // if (textField_updateRows.getText().equals("Sample Rows")) {
  textField_updateRows.setText("");
  textField_updateRows.setForeground(Color.BLACK);
  //}
}//GEN-LAST:event_textField_updateRowsFocusGained

private void sampleTableMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sampleTableMouseMoved
  int colNumber = getColModel().getColumnIndexAtX(evt.getX());
  Point p = evt.getPoint();
  int rowNumber = sampleTable.rowAtPoint(p) + 1;
  labelRow.setText("Row: " + rowNumber);
  labelCol.setText("Field: " + colNumber);
}//GEN-LAST:event_sampleTableMouseMoved

private void sampleTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sampleTableMouseReleased

  final int ind = getColModel().getColumnIndexAtX(evt.getX());
  TableColumn c;
  TableCellRenderer r;
  Component hr;
  Printable print;

  // Get the column

  if (evt.getButton() == MouseEvent.BUTTON1) {
    Point p = evt.getPoint();
    int rowNumber = sampleTable.rowAtPoint(p);
  } else {

    popUp.show(evt.getComponent(), evt.getX(), evt.getY());
    selectedColumn = ind;
  }
}//GEN-LAST:event_sampleTableMouseReleased

private void markFieldCustCodeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_markFieldCustCodeFieldActionPerformed
  setCustomerCodeField(selectedColumn);
  setHeader("CUSTOMER CODE");
}//GEN-LAST:event_markFieldCustCodeFieldActionPerformed

private void extractZipCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractZipCodeActionPerformed
  action.extractZipCode(this);
}//GEN-LAST:event_extractZipCodeActionPerformed

private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
  action.createRandomCode(this);
}//GEN-LAST:event_jMenuItem5ActionPerformed

  public void markFilters() {
    if (isColumnFiltered(selectedColumn) == null) {
      ImageIcon icon = new ImageIcon(getClass().getResource("/DirectMail/Images/popup/filter.png"));
      filterMenuItem.setIcon(icon);
      filterMenuItem.setText("Filter Field");
    } else {
      ImageIcon icon = new ImageIcon(getClass().getResource("/DirectMail/Images/popup/removeFilter.png"));
      filterMenuItem.setIcon(icon);
      filterMenuItem.setText("Remove Filter");
    }
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  public javax.swing.JButton CombineTKCityToolbar;
  public javax.swing.JPanel ExtraTab;
  public javax.swing.JButton FileToolbar;
  public javax.swing.JButton FormatTKToolbar;
  public javax.swing.JMenuItem aboutHelpMenuItem;
  private static javax.swing.JMenuItem addCounterActionMenuItem;
  public javax.swing.JButton addCounterToolbar;
  public javax.swing.JMenuItem addFieldMenuItem;
  public javax.swing.JMenuItem addFlags_menuItem;
  public javax.swing.JMenuItem addNamesMenuItem;
  public javax.swing.JMenuItem addUserMenuItem;
  public javax.swing.JMenuItem addressFileMenuItem;
  public javax.swing.JMenu addressingMenu;
  public javax.swing.JMenuItem appendFileMenuItem;
  public javax.swing.JMenuItem applyTemplateMenuItem;
  public javax.swing.JButton buttonClearOutput;
  public javax.swing.JMenuItem capitilizeMenuItem;
  public javax.swing.JMenuItem changeDelimeterMenuItem;
  public javax.swing.JMenuItem checkAddressMenuItem;
  public javax.swing.JMenuItem checkControlChrMenuItem;
  public javax.swing.JMenuItem checkFieldsMenuItem;
  public javax.swing.JMenu checkFileMenu;
  public javax.swing.JMenuItem checkLengthMenuItem;
  public javax.swing.JMenuItem checkList13MenuItem;
  public javax.swing.JMenuItem checkTKMenuItem;
  public javax.swing.JCheckBox checkbox_dontBotherMe;
  public javax.swing.JCheckBox checkbox_showOutPutProggress;
  public javax.swing.JMenuItem closeFileMenuItem;
  public javax.swing.JMenuItem codelineMenu;
  public javax.swing.JMenu columnActions;
  public javax.swing.JMenuItem combineFieldsMenuItem;
  public javax.swing.JButton combineFieldsToolbar1;
  public static javax.swing.JMenuItem combineTkCityMenuItem;
  public javax.swing.JComboBox comboBox_sampleFontSize;
  public javax.swing.JComboBox combo_delimeter;
  public javax.swing.JComboBox combobox_checkOnSaving;
  public javax.swing.JComboBox combobox_database;
  public javax.swing.JMenuItem convertAmountMenuItem;
  public javax.swing.JMenuItem convertGreekMenuItem;
  public javax.swing.JMenu convertMenu;
  public javax.swing.JMenuItem correctAddressCity;
  public javax.swing.JMenu correctMenu;
  public javax.swing.JMenu correctmenu;
  public javax.swing.JMenuItem createTemplateMenuItem;
  public javax.swing.JMenu dataActions;
  public javax.swing.JMenuItem deleteDuplicatesMenuItem;
  public javax.swing.JMenuItem deleteFieldsMenuItem;
  public javax.swing.JMenuItem deleteFromFileMenuItem;
  public javax.swing.JMenuItem doubleProductionMenuItem;
  public javax.swing.JMenuItem doubleSplittedMenuItem;
  public javax.swing.JMenuItem duplexMenuItem;
  public javax.swing.JMenuItem editUserMenuItem;
  public javax.swing.JMenuItem envelopeTestMenuItem;
  public static javax.swing.JButton erpIcon;
  public javax.swing.JMenuItem exportDBMenuItem;
  public javax.swing.JMenuItem exportList13MenuItem;
  public javax.swing.JPanel extraPanel;
  public javax.swing.JButton extraTabButton;
  public javax.swing.JLabel extraTabLabel;
  public javax.swing.JPanel extraTabPanel;
  public javax.swing.JMenuItem extractZipCode;
  public javax.swing.JMenu fieldToolsMenu;
  public javax.swing.JMenu fileMenu;
  public javax.swing.JMenu fileToolsMenu;
  public javax.swing.JToolBar filetoolbar;
  public javax.swing.JMenuItem fillEmptyAddresses;
  public javax.swing.JMenuItem filterMenuItem;
  public javax.swing.JMenu filtersMenu;
  public javax.swing.JMenuItem findCountyMenuItem;
  public javax.swing.JMenu findMenu;
  public static javax.swing.JMenuItem findTKMenuItem;
  public javax.swing.JMenuItem firstNameVocativeAddressingMenuItem;
  public javax.swing.JMenuItem fixedFileActionMenuItem;
  public javax.swing.JButton fixedToolbar;
  public static javax.swing.JMenuItem formatTkActionMenuItem;
  public javax.swing.JPanel genOptionsPanel;
  public javax.swing.JMenuItem getGenderMenuItem;
  public javax.swing.JButton getGenderToolbar1;
  public javax.swing.JMenuItem helpHelpMenuItem;
  public javax.swing.JMenu helpMenu;
  public javax.swing.JMenuItem importDbMenuItem;
  public javax.swing.JMenuItem importExcellMenuItem;
  public javax.swing.JMenu importMenuItem;
  public javax.swing.JLabel jLabel1;
  public javax.swing.JLabel jLabel10;
  public javax.swing.JLabel jLabel11;
  public javax.swing.JLabel jLabel12;
  public javax.swing.JLabel jLabel13;
  public javax.swing.JLabel jLabel14;
  public javax.swing.JLabel jLabel2;
  public javax.swing.JLabel jLabel3;
  public javax.swing.JLabel jLabel4;
  public javax.swing.JLabel jLabel5;
  public javax.swing.JLabel jLabel6;
  public javax.swing.JLabel jLabel7;
  public javax.swing.JLabel jLabel8;
  public javax.swing.JLabel jLabel9;
  public javax.swing.JMenu jMenu1;
  public javax.swing.JMenu jMenu2;
  public javax.swing.JMenu jMenu3;
  public javax.swing.JMenuItem jMenuItem1;
  public javax.swing.JMenuItem jMenuItem2;
  public javax.swing.JMenuItem jMenuItem3;
  public javax.swing.JMenuItem jMenuItem4;
  public javax.swing.JMenuItem jMenuItem5;
  public javax.swing.JPanel jPanel1;
  public javax.swing.JScrollPane jScrollPane1;
  public javax.swing.JScrollPane jScrollPane3;
  public javax.swing.JSeparator jSeparator1;
  public javax.swing.JSeparator jSeparator3;
  public javax.swing.JSeparator jSeparator4;
  public javax.swing.JSeparator jSeparator5;
  public javax.swing.JLabel labelCol;
  public javax.swing.JLabel labelCustomers;
  public javax.swing.JLabel labelDelimeter;
  public javax.swing.JLabel labelEncodingIn;
  public javax.swing.JLabel labelEncodingOut;
  public javax.swing.JLabel labelFields;
  public javax.swing.JLabel labelRow;
  public javax.swing.JLabel labelTmpFilename;
  public javax.swing.JMenuItem lastNameAccusativeAddressingMenuItem;
  public javax.swing.JMenuItem lastNameVocativeAddressingMenuItem;
  public javax.swing.JButton lastNameVocativeAddressingToolbar1;
  public javax.swing.JMenuItem lastnameGenitiveAddressingMenuItem;
  public static javax.swing.JLayeredPane layeredPane;
  public javax.swing.JMenu linesToolsMenu;
  public javax.swing.JMenuItem logsHelpMenuItem;
  public static javax.swing.JButton mainHostIcon;
  public javax.swing.JMenuBar mainMenuBar;
  public javax.swing.JMenu markField;
  public javax.swing.JMenuItem markFieldAddress;
  public javax.swing.JMenuItem markFieldAmount;
  public javax.swing.JMenuItem markFieldCity;
  public javax.swing.JMenuItem markFieldCountryFlag;
  public javax.swing.JMenuItem markFieldCustCodeField;
  public javax.swing.JMenuItem markFieldFirstname;
  public javax.swing.JMenuItem markFieldFullName;
  public javax.swing.JMenuItem markFieldGender;
  public javax.swing.JMenuItem markFieldSurname;
  public javax.swing.JMenuItem markFieldTK;
  public javax.swing.JMenuItem markFieldUnmark;
  public javax.swing.JPanel memoryPanel;
  public javax.swing.JMenu menuFirstname;
  public javax.swing.JMenuItem menuItemCheckDigit;
  public javax.swing.JMenuItem menuItemHolidays;
  public javax.swing.JMenuItem menuItem_correcrFirstnames;
  public javax.swing.JMenuItem menuItem_update;
  public javax.swing.JMenuItem menuItem_updateFnamesDB;
  public javax.swing.JMenu menuLastname;
  public javax.swing.JMenuItem menuSplitNames;
  public javax.swing.JMenuItem mergeReportsActionMenu;
  public javax.swing.JMenuItem multiplePagesMenuItem;
  public javax.swing.JMenuItem multiplePrintingMenuItem;
  public javax.swing.JMenuItem multiplyLinesMenuItem;
  public javax.swing.JMenuItem multisortingMenuItem;
  public javax.swing.JMenuItem notEnvelopedActionsMenuItem;
  public javax.swing.JMenuItem notEnvelopedIDsMenuItem;
  public javax.swing.JButton numOfTKToolbar;
  public javax.swing.JMenuItem openFileMenuItem;
  public javax.swing.JButton openToolbar;
  public javax.swing.JMenuItem optionsMenuItem;
  public javax.swing.JPanel outputPanel;
  public javax.swing.JPanel panel_output;
  public javax.swing.JPanel panel_quickOptions;
  public javax.swing.JPopupMenu popUp;
  public javax.swing.JMenuItem popUpAddColAfter;
  public javax.swing.JMenuItem popUpAddColBefore;
  public javax.swing.JMenuItem popUpAddFlags;
  public javax.swing.JMenuItem popUpBreakField;
  public javax.swing.JMenuItem popUpCapitalize;
  public javax.swing.JMenuItem popUpConvertGreek;
  public javax.swing.JMenuItem popUpDeleteCol;
  public javax.swing.JMenuItem popUpDeleteFirstLine;
  public javax.swing.JMenuItem popUpDuplicate;
  public javax.swing.JMenuItem popUpFillBlanks;
  public javax.swing.JMenuItem popUpFormatDecimal;
  public javax.swing.JMenuItem popUpKeepFirstName;
  public javax.swing.JMenuItem popUpReplaceFromDb;
  public javax.swing.JMenuItem popUpSearchReplace;
  public javax.swing.JMenuItem popUpSortAsc;
  public javax.swing.JMenuItem popUpSortByElta;
  public javax.swing.JMenuItem popUpSortByNumOfTk;
  public javax.swing.JMenuItem popUpSortDesc;
  public javax.swing.JMenuItem popUpTrim;
  public javax.swing.JPopupMenu popUp_dropDown;
  public javax.swing.JMenuItem popupPadField;
  public javax.swing.JMenu postEnvelopingMenu;
  public javax.swing.JMenu preEnvelopingMenu;
  public javax.swing.JMenu productionToolsMenu;
  public javax.swing.JProgressBar progressBar;
  public javax.swing.JMenuItem quitMenuItem;
  private static javax.swing.JLabel remainingTimeLabel;
  public javax.swing.JMenuItem removeFiltersMenuItem;
  public javax.swing.JButton removeFiltersToolbar;
  public javax.swing.JMenuItem reprintFileActionMenuItem;
  public javax.swing.JMenuItem revertFileMenuItem;
  public javax.swing.JButton revertToolbar;
  public javax.swing.JPanel samplePanel;
  public javax.swing.JTable sampleTable;
  public javax.swing.JScrollPane sampleTablePane;
  public javax.swing.JButton samplesToolbar;
  public javax.swing.JMenuItem saveFileMenuItem;
  public javax.swing.JMenuItem saveFilteredResultsMenuItem;
  public javax.swing.JMenuItem saveTemplateMenuItem;
  public javax.swing.JButton saveToolbar;
  public javax.swing.JMenuItem shiftFieldsMenuItem;
  public javax.swing.JButton shiftFieldsToolbar;
  public javax.swing.JMenuItem sortByEltaFlagMenuItem;
  public javax.swing.JMenuItem sortByNumOfTkMenuItem;
  public javax.swing.JMenuItem splitFileMenuItem;
  public javax.swing.JSplitPane splitPane_main;
  public javax.swing.JTabbedPane tabPanel;
  public javax.swing.JMenu tableEdit;
  public static javax.swing.JMenu templatesMenu;
  public javax.swing.JButton testEnvelopeToolbar;
  public javax.swing.JTextArea textAreaOutput;
  public javax.swing.JTextArea textArea_extra;
  private static javax.swing.JTextField textBoxCustomers;
  private static javax.swing.JTextField textBoxDelimeter;
  public javax.swing.JTextField textBoxEncodingIn;
  public javax.swing.JTextField textBoxEncodingOut;
  private static javax.swing.JTextField textBoxFields;
  private static javax.swing.JTextField textBoxOpenedFile;
  private static javax.swing.JTextField textBoxTmpFilename;
  public javax.swing.JTextField textField_maxLength;
  public javax.swing.JTextField textField_updateRows;
  public static javax.swing.JTextField tf_maxMemory;
  public static javax.swing.JTextField tf_totalMemory;
  public static javax.swing.JTextField tf_usedMemory;
  public javax.swing.JMenuItem tkFindCity;
  public static javax.swing.JMenuItem tkLabelsFromPCADDMenuItem;
  public static javax.swing.JMenuItem tkLabelsMenuItem;
  public static javax.swing.JMenuItem tkReportMenuItem;
  public javax.swing.JMenu toolsMenu;
  public javax.swing.JMenuItem trimFieldsMenuItem;
  private static javax.swing.JMenuItem undoMenuItem;
  private static javax.swing.JButton undoToolbar;
  public javax.swing.JMenuItem uniqueMenuItem;
  public javax.swing.JMenu usersMenu;
  public static javax.swing.JMenu zipCodeMenu;
  public javax.swing.JMenuItem zipCodesFlagActionMenuItem;
  // End of variables declaration//GEN-END:variables

  public void setDelimeter(char d) {
    String delShown;
    char oldDel = delimeter;
    delimeter = d;
    // update the label
    delShown = (d == '\t') ? "TAB" : "" + d;
    textBoxDelimeter.setText(delShown);
    if (oldDel != delimeter) {
      combo_delimeter.setSelectedItem(String.valueOf(d));
    }
  }

  public char getDelimeter() {
    return delimeter;
  }

  public void setCustomers(int c) {
    customers = c;
    String cString = "" + c;
    textBoxCustomers.setText(cString);

  }

  public int getCustomers() {
    return customers;
  }

  public void setFirstLine(String str) {
    firstLine = str;
  }

  public String getFirstLine() {
    return firstLine;
  }

  public void setFields(int f) {
    fields = f;
    textBoxFields.setText("" + f);
  }

  public int getFields() {
    return fields;
  }

  public void setTkField(int t) {
    tkField = t;

  }

  public int getTkField() {
    return tkField;
  }

  public void setReprints(int r) {
    reprints = r;
    //textBoxEncoding.setText("" + r);
  }

  public int getReprints() {
    return reprints;
  }

  public void setReprintIDs(String str) {
    reprintIDs = str;
  }

  public String getReprintIDs() {
    return reprintIDs;
  }

  public void setOrigFile(File f) {
    origFile = f;
  }

  public File getOrigFile() {
    return origFile;
  }

  public void setTmpFile(File f) {
    tmpFile = f;
  }

  public File getTmpFile() {
    return tmpFile;
  }

  public void setOrigFileName(String f) {
    origFileName = f;
    textBoxTmpFilename.setText(f);
  }

  public String getOrigFileName() {
    return origFileName;
  }

  public void setTmpFileName(String f) {
    tmpFileName = f;
  }

  public String getTmpFileName() {
    return tmpFileName;
  }

  public void setTextAreaText(String text) {
    Common.setTextAreaText(this, text);
  }

  public String getTextAreaText() {
    return null;
  }

  public void appendToSampleArea(String string) {
    Common.appendToSampleArea(this, string);
  }

  public void setNotEnveloped(int n) {
    notEnveloped = n;
  }

  public int getNotEnveloped() {
    return notEnveloped;
  }

  public void setLineLength() {
    lineLength = firstLine.length();
  }

  public int getLineLength() {
    return lineLength;
  }

  public void setOpenedFile(String f) {
    openedFile = f;
    textBoxOpenedFile.setText(f);
  }

  public String getOpenedFile() {
    return openedFile;
  }

  private void labelMouseOver(java.awt.event.MouseEvent evt) {
    evt.getComponent().setCursor(new Cursor(Cursor.HAND_CURSOR));
    evt.getComponent().setForeground(Color.RED);
  }

  private void labelMouseOut(java.awt.event.MouseEvent evt) {
    evt.getComponent().setForeground(Color.BLACK);
  }

  /**
   *  INITIALIZES THE PARAMETERS
   *
   * @param preserveTkField
   */
  public void init(boolean preserveTkField) {
    String line;
    BufferedReader dis;
    FileReader in;
    File tmp;
    BufferedReader br = null;
    PrintWriter out = null;

    // get the first line//create the bufferred reader
    br = DmFunctions.createBufferedReader(MainForm.this);
    try {
      setFirstLine(br.readLine());
      br.close();
    } catch (Exception ex) {
      myLog.log(Level.WARNING, null, ex);
      return;
    }
    // find the del
    if (delimeter == '\u0000') {
      char d = DmFunctions.findDelimeter(this, getFirstLine());
      setDelimeter(d);
    }

    // Set empty fields to a space // empty fields in the line
    while (getFirstLine().indexOf("" + getDelimeter() + "" + getDelimeter()) > -1) {
      firstLine = getFirstLine().replaceAll("" + getDelimeter() + "" + getDelimeter(), "" + getDelimeter() + " " + "" + getDelimeter());
    }
    // empty fields at the end of the line
    if (getFirstLine().lastIndexOf(getDelimeter()) == getFirstLine().length() - 1) {
      firstLine = getFirstLine() + " ";
    }
    // find num of fields
    int nof = getFirstLine().split("" + getDelimeter(), -1).length;
    setFields(nof);

    // set the tk field
    if (!preserveTkField) {
      setTkField(-1);
    }

    model.setColumnCount(nof);

    //create the table
    model.setRowCount(0);
    Iterator<String[]> it = getSampleText().iterator();
    while (it.hasNext()) {
      String[] rowData = it.next();
      model.addRow(rowData);
    }

    setLineLength();

    if (!preserveTkField) {
      runNumOfTk = false;
    }

    // Set progress to 0
    progressBar.setValue(0);
    progressBar.setString("0%");

    // Set remaining time to 0
    remainingTimeLabel.setText("E.T.A. : 00:00:00 (T.P. : 00:00:00)");

    //create a back up file used for the undo option
    tmp = new File("undo_" + getUndoId());
    setUndoId(getUndoId() + 1);

    if (isUndoStatus()) {
      setUndoLevel(getUndoLevel() - 1);
      setUndoStatus(false);
    } else {
      setUndoLevel(getUndoId() - 1);
      setUndoStatus(false);
    }

    if (getUndoLevel() > 0) {
      undoMenuItem.setEnabled(true);
      undoToolbar.setEnabled(true);
    } else {
      undoMenuItem.setEnabled(false);
      undoToolbar.setEnabled(false);
    }

    try {
      out = new PrintWriter(new BufferedWriter(new FileWriter(DmOptions._JAR_DIR_ + "tmp/" + tmp)));
      in = new FileReader(DmOptions._JAR_DIR_ + "tmp/" + getOrigFileName());
      dis = new BufferedReader(in);
      while ((line = dis.readLine()) != null) {
        out.println(line);
      }

      in.close();
      out.close();
    } catch (IOException ex) {
      myLog.log(Level.SEVERE, "Could not write tmp file", ex);
    }

    // Create the header's vector the first time
    if (getHeaderTitles().isEmpty()) {
      for (int i = 0; i < getFields(); i++) {
        // name cols
        getColModel().getColumn(i).setHeaderValue("" + i);
        getHeaderTitles().addElement("" + i);
      }
      // Force the header to resize and repaint itself

      sampleTable.getTableHeader().resizeAndRepaint();
    } else {
      rearrangeHeaders();
    }

    // check the combined TK and add 1 (for undo)
    if (isCombinedTK()) {
      setCombinedTKCount(getCombinedTKCount() + 1);
      //System.out.println("c_init:" + combinedTKCount);
    }

    Common.adjustSampleTable(this, false);
    Common.renderSampleTableHeaders(this);
  }

  public synchronized void rearrangeHeaders() {
    Common.rearrangeHeaders(this);
  }

  public void setOutput(String message) {
    Common.setOutput(this, message);
  }

  public void appendOutput(String message) {
    Common.appendOutput(this, message);
  }

  public void appendToCurrentOutput(String string) {
    Common.appendToCurrentOutput(this, string);
  }

  public void updateProgress(int val) {
    progressBar.setValue(val);
    progressBar.setString(val + "%");
  }

  public void IndeterminateProgress(boolean b) {
    progressBar.setIndeterminate(b);
  }

  public void setProgressText(String s) {
    progressBar.setString(s);
  }

  public void setRemainingTime(String r) {
    remainingTimeLabel.setText(r);
  }

  private void loadDatabasesNames() {
    databases[0] = options.toString(DmOptions.DATABASE);
  }

  public String getCurrentDirectory() {
    return currentDirectory;
  }

  public void setCurrentDirectory(String currentDirectory) {
    this.currentDirectory = currentDirectory;
  }

  public boolean isCombinedTK() {
    return combinedTK;
  }

  public void setCombinedTK(boolean combinedTK) {
    this.combinedTK = combinedTK;
  }

  public int getCombinedTKCount() {
    return combinedTKCount;
  }

  public void setCombinedTKCount(int combinedTKCount) {
    this.combinedTKCount = combinedTKCount;
  }

  public boolean isFormatedTK() {
    return formatedTK;
  }

  public void setFormatedTK(boolean formatedTK) {
    this.formatedTK = formatedTK;
  }

  public boolean isLabeling() {
    return labeling;
  }

  public void setLabeling(boolean labeling) {
    this.labeling = labeling;
  }

  public int getUndoId() {
    return undoId;
  }

  public void setUndoId(int undoId) {
    this.undoId = undoId;
  }

  public int getUndoLevel() {
    return undoLevel;
  }

  public void setUndoLevel(int undoLevel) {
    this.undoLevel = undoLevel;
  }

  public boolean isUndoStatus() {
    return undoStatus;
  }

  public void setUndoStatus(boolean undoStatus) {
    this.undoStatus = undoStatus;
  }

  public boolean isMainConnectionToDB() {
    return mainConnectionToDB;
  }

  public void setMainConnectionToDB(boolean mainConnectionToDB) {
    this.mainConnectionToDB = mainConnectionToDB;
  }

  public int getGenderField() {
    return genderField;
  }

  public void setGenderField(int genderField) {
    this.genderField = genderField;
  }

  public int getFullnameField() {
    return fullnameField;
  }

  public void setFullnameField(int fullnameField) {
    this.fullnameField = fullnameField;
  }

  public int getAmountField() {
    return amountField;
  }

  public void setAmountField(int amountField) {
    this.amountField = amountField;
  }

  public int getFirstnameField() {
    return firstnameField;
  }

  public void setFirstnameField(int firstnameField) {
    this.firstnameField = firstnameField;
  }

  public int getSurnameField() {
    return surnameField;
  }

  public void setSurnameField(int surnameField) {
    this.surnameField = surnameField;
  }

  public int getCityField() {
    return cityField;
  }

  public void setCityField(int cityField) {
    this.cityField = cityField;
  }

  public int getAddressField() {
    return addressField;
  }

  public void setAddressField(int addressField) {
    this.addressField = addressField;
  }

  public int getAbroadField() {
    return abroadField;
  }

  public void setAbroadField(int abroadField) {
    this.abroadField = abroadField;
  }

  public void setCounterField(int counterField) {
    this.counterField = counterField;
  }

  public int getCounterField() {
    return counterField;
  }

  public int getIsTKRightField() {
    return isTKRightField;
  }

  public void setIsTKRightField(int isTKRightField) {
    this.isTKRightField = isTKRightField;
  }

  public String getEncodingIn() {
    return encodingIn;
  }

  public void setEncodingIn(String encodingIn) {
    this.encodingIn = encodingIn;
  }

  public String getEncodingOut() {
    return encodingOut;
  }

  public void setEncodingOut(String encodingOut) {
    this.encodingOut = encodingOut;
  }

  public String getCharacterSet() {
    return characterSet;
  }

  public void setCharacterSet(String characterSet) {
    this.characterSet = characterSet;
  }

  public String getCollation() {
    return collation;
  }

  public void setCollation(String collation) {
    this.collation = collation;
  }

  public TableColumnModel getColModel() {
    return colModel;
  }

  public void setColModel(TableColumnModel colModel) {
    this.colModel = colModel;
  }

  public PFile getPFile() {
    return pFile;
  }

  public void setPFile(PFile p) {
    this.pFile = p;
  }

  public String getFullVersion() {
    return version + build.substring(0, 1) + "." + build.substring(1);
  }

  public void setFullVersion(String fullVersion) {
    this.fullVersion = fullVersion;
  }

  public String[] getDatabases() {
    return databases;
  }

  public Vector<String> getHeaderTitles() {
    return headerTitles;
  }

  public void setHeaderTitles(Vector<String> headerTitles) {
    this.headerTitles = headerTitles;
  }

  public Vector<String[]> getSampleText() {
    return sampleText;
  }

  public void setSampleText(Vector<String[]> sampleText) {
    this.sampleText = sampleText;
  }

  public ArrayList<Integer> getMoveColumnFrom() {
    return moveColumnFrom;
  }

  public void setMoveColumnFrom(ArrayList<Integer> moveColumnFrom) {
    this.moveColumnFrom = moveColumnFrom;
  }

  public ArrayList<Integer> getMoveColumnTo() {
    return moveColumnTo;
  }

  public void setMoveColumnTo(ArrayList<Integer> moveColumnTo) {
    this.moveColumnTo = moveColumnTo;
  }

  public static Filter isColumnFiltered(int selectedColumn) {
    for (int i = 0; i < MainForm.filters.size(); i++) {
      Filter f = MainForm.filters.get(i);
      if (f.getColumn() == selectedColumn) {
        return f;
      }
    }
    return null;
  }

  /**
   * @return the currentUser
   */
  public Users getCurrentUser() {
    return currentUser;
  }

  /**
   * @param currentUser the currentUser to set
   */
  public void setCurrentUser(Users currentUser) {
    this.currentUser = currentUser;
  }

  /**
   * @return the customerCodeField
   */
  public int getCustomerCodeField() {
    return customerCodeField;
  }

  /**
   * @param customerCodeField the customerCodeField to set
   */
  public void setCustomerCodeField(int customerCodeField) {
    this.customerCodeField = customerCodeField;
  }
}
