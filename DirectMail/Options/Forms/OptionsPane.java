/*
 * OptionsPanel.java
 *
 * Created on 29 Ιούλιος 2007, 7:46 μμ
 */
package DirectMail.Options.Forms;

import DirectMail.Help.Functions.CheckConnectionToDB;
import DirectMail.Help.Components.Errors;
import DirectMail.*;
import DirectMail.Help.Components.MyDraggable;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import com.ice.jni.registry.NoSuchKeyException;
import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;
import com.ice.jni.registry.RegistryValue;
import java.util.Enumeration;
import java.util.logging.Logger;
import soldatos.lookaandfeel.ComboBoxRenderer;
import soldatos.connection.MyDBCConnection;
import soldatos.messages.Messages;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import soldatos.functions.ArrayFunctions;
import soldatos.functions.FileFunctions;

/**
 *
 * @author  ssoldatos
 */
public class OptionsPane extends MyDraggable {

  private MainForm m;
  private ComboBoxModel model_fonts;
  private ComboBoxModel model_lafs;
  private GraphicsEnvironment env;
  private Color origColor;
  private String origFont;
  private int origFontSize;
  private ArrayList hosts = new ArrayList();
  private DefaultComboBoxModel hostsModel;
  private DmOptions options;
  private String origLaf;

  /** Creates new form OptionsPanel
   * @param m
   */
  public OptionsPane(MainForm m) {
    setModalityType(ModalityType.DOCUMENT_MODAL);
    this.m = m;
    this.options = MainForm.options;
    this.origColor = options.toColor(DmOptions.SKIN_COLOR);
    this.origFont = options.toString(DmOptions.FONT);
    this.origFontSize = options.toInt(DmOptions.FONT_SIZE);
    this.origLaf = options.toString(DmOptions.LAF);
    hostsModel = new DefaultComboBoxModel(getHostsKeys(options));
    getAllHosts();
    initComponents();
    setLocationRelativeTo(null);
    setVisible(true);

  }

  /** Creates new form OptionsPanel and sets the selected tab
   * @param m
   * @param selectedTab
   */
  public OptionsPane(MainForm m, int selectedTab) {
    this(m);
    optionsTabbedPanel.setSelectedIndex(selectedTab);
  }

  private void getAllHosts() {
    String[] hostsArr = options.toArray(DmOptions.HOSTS_LIST);
    for (int i = 0; i < hostsArr.length; i++) {
      String host = hostsArr[i];
      if (!host.equals("")) {
        hosts.add(host);
      }
    }
  }

  public static String[] getHostsKeys(DmOptions op) {
    String[] hostsArr = op.toArray(DmOptions.HOSTS_LIST);
    String[] keys = new String[hostsArr.length];

    for (int i = 0; i < hostsArr.length; i++) {
      String host = hostsArr[i];
      String[] hostArr = host.split(",");
      if (!hostArr[0].trim().equals("")) {
        keys[i] = hostArr[0];
      }
    }
    return keys;
  }

  private void createModelFonts() {
    env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    model_fonts = new DefaultComboBoxModel(env.getAvailableFontFamilyNames());
  }

  private void createModelLafs() {
    LookAndFeelInfo lafs[] = UIManager.getInstalledLookAndFeels();
    String[] lafNames = new String[lafs.length];
    for (int i = 0; i < lafs.length; i++) {
      lafNames[i] = lafs[i].getName();
    }
    model_lafs = new DefaultComboBoxModel(lafNames);
  }

  private void getOptionsComponents() {
    for (int i = 0; i < optionsTabbedPanel.getTabCount(); i++) {
      parse((JPanel) optionsTabbedPanel.getComponentAt(i));
    }
  }

  /**
   * Parsing a jpanel and gets components that are options<br />
   * Then sets the option with the name of the component to the value of the componont
   * @param panel
   */
  private void parse(JPanel panel) {
    Component[] c = panel.getComponents();
    for (int i = 0; i < c.length; i++) {
      if (c[i].getName() != null) {
        if (options.isOption(c[i].getName())) {
          options.setOption(c[i].getName(), String.valueOf(getValue(c[i])));
        }
      }
      if (c[i] instanceof JPanel) {
        parse((JPanel) c[i]);
      }
    }
  }

  /**
   * Getting a value of a component <br />
   * Components are JSpinner, JCheckBox, JComboBox
   * @param c The component
   * @return the value of the component
   */
  private Object getValue(Component c) {
    //TODO Low combobox get value
    boolean getTheValue = false;

    if (c instanceof JSpinner) {
      JSpinner spin = (JSpinner) c;
      return spin.getValue();
    }
    if (c instanceof JCheckBox) {
      JCheckBox check = (JCheckBox) c;
      return check.isSelected();
    }
    if (c instanceof JTextField) {
      JTextField text = (JTextField) c;
      return text.getText();
    }
    if (c instanceof JComboBox) {
      JComboBox combo = (JComboBox) c;
      String name = combo.getName();
      return String.valueOf(combo.getSelectedItem());

    }
    // Get color buttons
    if (c instanceof JLabel) {
      JLabel label = (JLabel) c;
      return label.getBackground().getRed() + ", "
          + label.getBackground().getGreen() + ", "
          + label.getBackground().getBlue();
    }
    return "";
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    optionsPanel = new javax.swing.JPanel();
    okButton = new javax.swing.JButton();
    cancelButton = new javax.swing.JButton();
    optionsTabbedPanel = new javax.swing.JTabbedPane();
    generalPanel = new javax.swing.JPanel();
    labelLinesInSample = new javax.swing.JLabel();
    labelLengthOfBrokenFields = new javax.swing.JLabel();
    textBoxOptions_0 = new javax.swing.JTextField();
    textBoxOptions_1 = new javax.swing.JTextField();
    labelNumOfTKOrder = new javax.swing.JLabel();
    comboBoxOptions_11 = new javax.swing.JComboBox();
    labelShowOutput = new javax.swing.JLabel();
    checkBoxOptions_12 = new javax.swing.JCheckBox();
    jLabel3 = new javax.swing.JLabel();
    textBoxOptions_18 = new javax.swing.JTextField();
    jLabel4 = new javax.swing.JLabel();
    jLabel5 = new javax.swing.JLabel();
    textBoxOptions_19 = new javax.swing.JTextField();
    jLabel6 = new javax.swing.JLabel();
    buttonOptions_23 = new javax.swing.JButton();
    labelOptions_23 = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    comboBox_font = new javax.swing.JComboBox();
    comboBox_fontSize = new javax.swing.JComboBox();
    jLabel1 = new javax.swing.JLabel();
    label_FontPreview = new javax.swing.JLabel();
    jLabel11 = new javax.swing.JLabel();
    comboBox_laf = new javax.swing.JComboBox();
    jLabel16 = new javax.swing.JLabel();
    checkbox_integrate = new javax.swing.JCheckBox();
    filePanel = new javax.swing.JPanel();
    labelAdd1 = new javax.swing.JLabel();
    checkBoxOptions_13 = new javax.swing.JCheckBox();
    jLabel7 = new javax.swing.JLabel();
    jTextField1 = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    checkBox_loadlastOpenedFile = new javax.swing.JCheckBox();
    jLabel9 = new javax.swing.JLabel();
    combobox_checkOnSaving = new javax.swing.JComboBox();
    addressingPanel = new javax.swing.JPanel();
    labelMale = new javax.swing.JLabel();
    labelFemale = new javax.swing.JLabel();
    labelCompanies = new javax.swing.JLabel();
    labelUknown = new javax.swing.JLabel();
    textBoxOptions_2 = new javax.swing.JTextField();
    textBoxOptions_3 = new javax.swing.JTextField();
    textBoxOptions_4 = new javax.swing.JTextField();
    textBoxOptions_5 = new javax.swing.JTextField();
    checkBoxOptions_16 = new javax.swing.JCheckBox();
    checkBoxOptions_17 = new javax.swing.JCheckBox();
    checkBoxOptions_20 = new javax.swing.JCheckBox();
    checkBoxOptions_24 = new javax.swing.JCheckBox();
    checkBoxOptions_25 = new javax.swing.JCheckBox();
    combobox_setAddressing = new javax.swing.JComboBox();
    databasePanel = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();
    buttonTestConnection = new javax.swing.JButton();
    labelHost = new javax.swing.JLabel();
    textBoxOptions_8 = new javax.swing.JTextField();
    labelPassword = new javax.swing.JLabel();
    textBoxOptions_7 = new javax.swing.JTextField();
    textBoxOptions_10 = new javax.swing.JPasswordField();
    labelDatabase = new javax.swing.JLabel();
    textBoxOptions_9 = new javax.swing.JTextField();
    labelTmpTable = new javax.swing.JLabel();
    labelUserName = new javax.swing.JLabel();
    comboBoxOptions_6 = new javax.swing.JComboBox();
    jPanel3 = new javax.swing.JPanel();
    jLabel12 = new javax.swing.JLabel();
    jLabel13 = new javax.swing.JLabel();
    jLabel14 = new javax.swing.JLabel();
    jLabel15 = new javax.swing.JLabel();
    jTextField2 = new javax.swing.JTextField();
    jTextField3 = new javax.swing.JTextField();
    jTextField4 = new javax.swing.JTextField();
    jTextField5 = new javax.swing.JTextField();
    jLabel10 = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle("Direct Mail Application - Options");
    setIconImage(new javax.swing.ImageIcon("D:\\JavaProjects\\DirectMail\\src\\DirectMail\\Images\\options.gif").getImage());

    jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
    jPanel1.setMinimumSize(new java.awt.Dimension(713, 434));

    optionsPanel.setMinimumSize(new java.awt.Dimension(674, 381));

    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        okButtonActionPerformed(evt);
      }
    });

    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelButtonActionPerformed(evt);
      }
    });

    labelLinesInSample.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelLinesInSample.setText("Lines displayed in the sample:");

    labelLengthOfBrokenFields.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelLengthOfBrokenFields.setText("Maximun length of broken fields :");

    textBoxOptions_0.setText(String.valueOf(options.toString(DmOptions.MAX_SAMPLE_LINES)));
    textBoxOptions_0.setName(DirectMail.Options.DmOptions.MAX_SAMPLE_LINES);

    textBoxOptions_1.setText(options.toString(DmOptions.MAX_FIELD_LENGTH));
    textBoxOptions_1.setName(DirectMail.Options.DmOptions.MAX_FIELD_LENGTH);

    labelNumOfTKOrder.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelNumOfTKOrder.setText("Elta Flag Order:");

    comboBoxOptions_11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ASC", "DESC" }));
    comboBoxOptions_11.setSelectedIndex(options.toString(DmOptions.ELTA_SORT_ORDER).equals("ASC") ? 0 : 1);
    comboBoxOptions_11.setName(DirectMail.Options.DmOptions.ELTA_SORT_ORDER);

    labelShowOutput.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelShowOutput.setText("Show progress in output :");

    checkBoxOptions_12.setSelected(options.toBoolean(DmOptions.SHOW_OUTPUT_PROGRESS));
    checkBoxOptions_12.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkBoxOptions_12.setMargin(new java.awt.Insets(0, 0, 0, 0));
    checkBoxOptions_12.setName(DirectMail.Options.DmOptions.SHOW_OUTPUT_PROGRESS);
    checkBoxOptions_12.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkBoxOptions_12ActionPerformed(evt);
      }
    });

    jLabel3.setText("Maximum number of unsorted envelopes :");

    textBoxOptions_18.setText(options.toString(DmOptions.MAX_NUMBER_OF_UNSORTED_ENVELOPES));
    textBoxOptions_18.setName(DirectMail.Options.DmOptions.MAX_NUMBER_OF_UNSORTED_ENVELOPES);

    jLabel4.setText("(Slows down processing - Only for debugging)");

    jLabel5.setText("Maximum number of pages in an envelope:");

    textBoxOptions_19.setText(options.toString(DmOptions.MAX_NUMBER_OF_PAGES_IN_ENVELOPE));
    textBoxOptions_19.setName(DirectMail.Options.DmOptions.MAX_NUMBER_OF_PAGES_IN_ENVELOPE);

    jLabel6.setText("Skin Color:");

    buttonOptions_23.setText("Change");
    buttonOptions_23.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonOptions_23ActionPerformed(evt);
      }
    });

    labelOptions_23.setBackground(options.toColor(DmOptions.SKIN_COLOR));
    labelOptions_23.setName(DirectMail.Options.DmOptions.SKIN_COLOR);
    labelOptions_23.setOpaque(true);

    jLabel8.setText("Font :");

    createModelFonts();
    comboBox_font.setEditable(true);
    comboBox_font.setModel(model_fonts);
    comboBox_font.setSelectedItem(options.toString(DmOptions.FONT));
    comboBox_font.setName(DirectMail.Options.DmOptions.FONT);
    comboBox_font.setRenderer(new ComboBoxRenderer());
    comboBox_font.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        comboBox_fontActionPerformed(evt);
      }
    });

    comboBox_fontSize.setEditable(true);
    comboBox_fontSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10", "11", "12", "14", "17", "20", "26" }));
    comboBox_fontSize.setSelectedItem(options.toString(DmOptions.FONT_SIZE));
    comboBox_fontSize.setName(DmOptions.FONT_SIZE);
    comboBox_fontSize.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        comboBox_fontSizeActionPerformed(evt);
      }
    });

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText("Font size :");

    label_FontPreview.setText("ABCDabcd 123 ΑΒΓαβγ 123");

    jLabel11.setText("Look & Feel:");

    createModelLafs();
    comboBox_laf.setModel(model_lafs);
    comboBox_laf.setSelectedItem(options.toString(DmOptions.LAF));
    comboBox_laf.setName(DmOptions.LAF);

    jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel16.setText("Integrate into shell:");

    checkbox_integrate.setSelected(options.toBoolean(DmOptions.INTEGRATE));
    checkbox_integrate.setText("(Windows Only)");
    checkbox_integrate.setName(DmOptions.INTEGRATE);

    javax.swing.GroupLayout generalPanelLayout = new javax.swing.GroupLayout(generalPanel);
    generalPanel.setLayout(generalPanelLayout);
    generalPanelLayout.setHorizontalGroup(
      generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(generalPanelLayout.createSequentialGroup()
        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addGroup(generalPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jLabel11))
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, generalPanelLayout.createSequentialGroup()
            .addGap(36, 36, 36)
            .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(labelShowOutput)
              .addComponent(labelNumOfTKOrder)
              .addComponent(labelLinesInSample, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(labelLengthOfBrokenFields, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel3)
              .addComponent(jLabel5)
              .addComponent(jLabel6)
              .addComponent(jLabel8)))
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, generalPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
              .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE))))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(generalPanelLayout.createSequentialGroup()
              .addComponent(comboBox_fontSize, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addContainerGap())
            .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(generalPanelLayout.createSequentialGroup()
                .addComponent(checkBoxOptions_12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addContainerGap())
              .addGroup(generalPanelLayout.createSequentialGroup()
                .addComponent(textBoxOptions_0, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addContainerGap())
              .addGroup(generalPanelLayout.createSequentialGroup()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                  .addComponent(comboBoxOptions_11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(textBoxOptions_18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                  .addComponent(textBoxOptions_19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                  .addComponent(textBoxOptions_1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                  .addGroup(generalPanelLayout.createSequentialGroup()
                    .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                      .addComponent(comboBox_laf, javax.swing.GroupLayout.Alignment.LEADING, 0, 173, Short.MAX_VALUE)
                      .addComponent(buttonOptions_23, javax.swing.GroupLayout.Alignment.LEADING)
                      .addComponent(comboBox_font, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(label_FontPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(labelOptions_23, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(268, 268, 268))))
          .addGroup(generalPanelLayout.createSequentialGroup()
            .addComponent(checkbox_integrate)
            .addContainerGap())))
    );

    generalPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {textBoxOptions_0, textBoxOptions_1, textBoxOptions_18, textBoxOptions_19});

    generalPanelLayout.setVerticalGroup(
      generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, generalPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelLinesInSample, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
          .addComponent(textBoxOptions_0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelNumOfTKOrder, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
          .addComponent(comboBoxOptions_11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelLengthOfBrokenFields, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
          .addComponent(textBoxOptions_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(textBoxOptions_18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel5)
          .addComponent(textBoxOptions_19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, generalPanelLayout.createSequentialGroup()
            .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(checkBoxOptions_12)
              .addComponent(jLabel4))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(generalPanelLayout.createSequentialGroup()
                .addComponent(labelOptions_23, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addGap(20, 20, 20))
              .addGroup(generalPanelLayout.createSequentialGroup()
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                  .addComponent(jLabel6)
                  .addComponent(buttonOptions_23))
                .addGap(13, 13, 13)
                .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                  .addComponent(jLabel8)
                  .addComponent(comboBox_font, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addComponent(label_FontPreview)))))
          .addComponent(labelShowOutput))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel1)
          .addComponent(comboBox_fontSize, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel11)
          .addComponent(comboBox_laf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(generalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel16)
          .addComponent(checkbox_integrate))
        .addGap(19, 19, 19))
    );

    generalPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {checkBoxOptions_12, comboBoxOptions_11, textBoxOptions_0, textBoxOptions_1, textBoxOptions_18, textBoxOptions_19});

    optionsTabbedPanel.addTab("General", generalPanel);

    labelAdd1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelAdd1.setText("Ask for file encoding on file opening :");

    checkBoxOptions_13.setSelected(options.toBoolean(DmOptions.ASK_FOR_FILE_ENCODING));
    checkBoxOptions_13.setAlignmentY(0.0F);
    checkBoxOptions_13.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkBoxOptions_13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    checkBoxOptions_13.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    checkBoxOptions_13.setMargin(new java.awt.Insets(0, 0, 0, 0));
    checkBoxOptions_13.setName(DirectMail.Options.DmOptions.ASK_FOR_FILE_ENCODING);

    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel7.setText("Default Directory :");

    jTextField1.setText(options.toString(DmOptions.DEFAULT_DIR));
    jTextField1.setName(DirectMail.Options.DmOptions.DEFAULT_DIR);

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText("Load last opened file:");

    checkBox_loadlastOpenedFile.setSelected(options.toBoolean(DmOptions.LOAD_LAST_OPENED_FILE));
    checkBox_loadlastOpenedFile.setAlignmentY(0.0F);
    checkBox_loadlastOpenedFile.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
    checkBox_loadlastOpenedFile.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    checkBox_loadlastOpenedFile.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    checkBox_loadlastOpenedFile.setMargin(new java.awt.Insets(0, 0, 0, 0));
    checkBox_loadlastOpenedFile.setName(DirectMail.Options.DmOptions.LOAD_LAST_OPENED_FILE);

    jLabel9.setText("Check on file saving:");

    combobox_checkOnSaving.setModel(new DefaultComboBoxModel(new Object[] {DirectMail.Options.DmOptions._NOTHING_,DirectMail.Options.DmOptions._LENGTH_OF_LINES_,DirectMail.Options.DmOptions._NUMBER_OF_FIELDS_,DirectMail.Options.DmOptions._BOTH_}));
    combobox_checkOnSaving.setSelectedItem(options.toString(DmOptions.CHECK_ON_SAVING));
    combobox_checkOnSaving.setName(DirectMail.Options.DmOptions.CHECK_ON_SAVING);

    javax.swing.GroupLayout filePanelLayout = new javax.swing.GroupLayout(filePanel);
    filePanel.setLayout(filePanelLayout);
    filePanelLayout.setHorizontalGroup(
      filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(filePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jLabel7)
          .addComponent(labelAdd1)
          .addComponent(jLabel2)
          .addComponent(jLabel9))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(checkBox_loadlastOpenedFile, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
          .addComponent(checkBoxOptions_13)
          .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(combobox_checkOnSaving, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(62, 62, 62))
    );
    filePanelLayout.setVerticalGroup(
      filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(filePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(labelAdd1)
          .addComponent(checkBoxOptions_13, 0, 25, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jLabel2)
          .addComponent(checkBox_loadlastOpenedFile))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel7)
          .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(filePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel9)
          .addComponent(combobox_checkOnSaving, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(214, Short.MAX_VALUE))
    );

    optionsTabbedPanel.addTab("File", filePanel);

    labelMale.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelMale.setText("Male :");

    labelFemale.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelFemale.setText("Female :");

    labelCompanies.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelCompanies.setText("Companies :");

    labelUknown.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelUknown.setText("Uknown :");

    textBoxOptions_2.setText(options.toString(DmOptions.ADDRESSING_MALE));
    textBoxOptions_2.setName(DirectMail.Options.DmOptions.ADDRESSING_MALE);

    textBoxOptions_3.setText(options.toString(DmOptions.ADDRESSING_FEMALE));
    textBoxOptions_3.setName(DirectMail.Options.DmOptions.ADDRESSING_FEMALE);

    textBoxOptions_4.setText(options.toString(DmOptions.ADDRESSING_COMPANIES));
    textBoxOptions_4.setName(DirectMail.Options.DmOptions.ADDRESSING_COMPANIES);

    textBoxOptions_5.setText(options.toString(DmOptions.ADDRESSING_UNKNOWN));
    textBoxOptions_5.setName(DirectMail.Options.DmOptions.ADDRESSING_UNKNOWN);

    checkBoxOptions_16.setSelected(options.toBoolean(DmOptions.USE_NOT_ACCENTED_SURNAME));
    checkBoxOptions_16.setText("Use not accented surname if not found in DB");
    checkBoxOptions_16.setToolTipText("If the surname is not found in the database use it without accent");
    checkBoxOptions_16.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkBoxOptions_16.setMargin(new java.awt.Insets(0, 0, 0, 0));
    checkBoxOptions_16.setName(DirectMail.Options.DmOptions.USE_NOT_ACCENTED_SURNAME);

    checkBoxOptions_17.setSelected(options.toBoolean(DmOptions.USE_SURNAME_TO_GET_GENDER));
    checkBoxOptions_17.setText("If name not in database use surname for finding gender");
    checkBoxOptions_17.setToolTipText("If the name is not found in the database use the last letter of the surname to find out the gender");
    checkBoxOptions_17.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkBoxOptions_17.setMargin(new java.awt.Insets(0, 0, 0, 0));
    checkBoxOptions_17.setName(DirectMail.Options.DmOptions.USE_SURNAME_TO_GET_GENDER);

    checkBoxOptions_20.setSelected(options.toBoolean(DmOptions.ADD_ADDRESSING_TO_UNKNOWN_GENDER));
    checkBoxOptions_20.setText("Add addressing and to unknown gender");
    checkBoxOptions_20.setToolTipText("If gender is uknown add the name after the addressing");
    checkBoxOptions_20.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkBoxOptions_20.setMargin(new java.awt.Insets(0, 0, 0, 0));
    checkBoxOptions_20.setName(DirectMail.Options.DmOptions.ADD_ADDRESSING_TO_UNKNOWN_GENDER);

    checkBoxOptions_24.setSelected(options.toBoolean(DmOptions.CAPITAL_ADDRESSING));
    checkBoxOptions_24.setText("CAPITILIZE Addressing");
    checkBoxOptions_24.setToolTipText("Addressing in capitals");
    checkBoxOptions_24.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkBoxOptions_24.setMargin(new java.awt.Insets(0, 0, 0, 0));
    checkBoxOptions_24.setName(DirectMail.Options.DmOptions.CAPITAL_ADDRESSING);

    checkBoxOptions_25.setSelected(options.toBoolean(DmOptions.NOT_ACCENTED_ADDRESSING));
    checkBoxOptions_25.setText("Addressing without accent");
    checkBoxOptions_25.setToolTipText("Add addressing without accent");
    checkBoxOptions_25.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    checkBoxOptions_25.setMargin(new java.awt.Insets(0, 0, 0, 0));
    checkBoxOptions_25.setName(DirectMail.Options.DmOptions.NOT_ACCENTED_ADDRESSING);

    combobox_setAddressing.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "-", "Αγαπητέ", "Αξιότιμε" }));
    combobox_setAddressing.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        combobox_setAddressingActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout addressingPanelLayout = new javax.swing.GroupLayout(addressingPanel);
    addressingPanel.setLayout(addressingPanelLayout);
    addressingPanelLayout.setHorizontalGroup(
      addressingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(addressingPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(addressingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addGroup(addressingPanelLayout.createSequentialGroup()
            .addComponent(labelFemale)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textBoxOptions_3))
          .addGroup(addressingPanelLayout.createSequentialGroup()
            .addComponent(labelCompanies)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textBoxOptions_4))
          .addGroup(addressingPanelLayout.createSequentialGroup()
            .addComponent(labelMale, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(textBoxOptions_2))
          .addGroup(addressingPanelLayout.createSequentialGroup()
            .addComponent(labelUknown)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(addressingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(checkBoxOptions_16)
              .addComponent(checkBoxOptions_17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(checkBoxOptions_20)
              .addComponent(textBoxOptions_5)
              .addComponent(checkBoxOptions_24)
              .addComponent(checkBoxOptions_25))))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(combobox_setAddressing, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(162, Short.MAX_VALUE))
    );

    addressingPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {labelCompanies, labelFemale, labelMale, labelUknown});

    addressingPanelLayout.setVerticalGroup(
      addressingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(addressingPanelLayout.createSequentialGroup()
        .addGap(30, 30, 30)
        .addGroup(addressingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelMale)
          .addComponent(textBoxOptions_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(combobox_setAddressing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(addressingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelFemale)
          .addComponent(textBoxOptions_3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(addressingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelCompanies)
          .addComponent(textBoxOptions_4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(addressingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelUknown)
          .addComponent(textBoxOptions_5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(22, 22, 22)
        .addComponent(checkBoxOptions_20)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkBoxOptions_17)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkBoxOptions_16)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkBoxOptions_24)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkBoxOptions_25)
        .addGap(79, 79, 79))
    );

    optionsTabbedPanel.addTab("Addressing", addressingPanel);

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "MySQL Connection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

    buttonTestConnection.setText("Add Host");
    buttonTestConnection.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonTestConnectionActionPerformed(evt);
      }
    });

    labelHost.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelHost.setText("Host :");

    textBoxOptions_8.setText(options.toString(DmOptions.DB_PREFIX));
    textBoxOptions_8.setName(DirectMail.Options.DmOptions.DB_PREFIX);

    labelPassword.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelPassword.setText("Password :");

    textBoxOptions_7.setText(options.toString(DmOptions.DATABASE));
    textBoxOptions_7.setName(DirectMail.Options.DmOptions.DATABASE);

    textBoxOptions_10.setText(options.toString(DmOptions.DB_PASSWORD));
    textBoxOptions_10.setName(DirectMail.Options.DmOptions.DB_PASSWORD);

    labelDatabase.setText("Database :");

    textBoxOptions_9.setText(options.toString(DmOptions.DB_USER));
    textBoxOptions_9.setName(DirectMail.Options.DmOptions.DB_USER);

    labelTmpTable.setText("Table prefix :");

    labelUserName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    labelUserName.setText("Username :");

    comboBoxOptions_6.setEditable(true);
    comboBoxOptions_6.setModel(hostsModel);
    comboBoxOptions_6.setSelectedItem(options.toString(DmOptions.HOST));
    comboBoxOptions_6.setName(DirectMail.Options.DmOptions.HOST);
    comboBoxOptions_6.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        comboBoxOptions_6ActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(labelDatabase)
          .addComponent(labelHost)
          .addComponent(labelTmpTable))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addComponent(textBoxOptions_7, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
          .addComponent(textBoxOptions_8)
          .addComponent(comboBoxOptions_6, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(66, 66, 66)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(labelUserName)
          .addComponent(labelPassword))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(buttonTestConnection)
          .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
            .addComponent(textBoxOptions_10, javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(textBoxOptions_9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)))
        .addContainerGap())
    );

    jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {labelHost, labelPassword, labelUserName});

    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(labelHost)
              .addComponent(comboBoxOptions_6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(labelDatabase)
              .addComponent(textBoxOptions_7))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(labelTmpTable)
              .addComponent(textBoxOptions_8))
            .addGap(3, 3, 3))
          .addGroup(jPanel2Layout.createSequentialGroup()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(labelUserName)
              .addComponent(textBoxOptions_9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(labelPassword)
              .addComponent(textBoxOptions_10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonTestConnection)))
        .addContainerGap())
    );

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "MsSQL Connection", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

    jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel12.setText("Host :");

    jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel13.setText("password :");

    jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel14.setText("username :");

    jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel15.setText("Database :");

    jTextField2.setText(options.toString(DmOptions.SQL_HOST));
    jTextField2.setName(DirectMail.Options.DmOptions.SQL_HOST);

    jTextField3.setText(options.toString(DmOptions.SQL_DB));
    jTextField3.setName(DirectMail.Options.DmOptions.SQL_DB);

    jTextField4.setText(options.toString(DmOptions.SQL_USERNAME));
    jTextField4.setName(DirectMail.Options.DmOptions.SQL_USERNAME);

    jTextField5.setText(options.toString(DmOptions.SQL_PASSWORD));
    jTextField5.setName(DirectMail.Options.DmOptions.SQL_PASSWORD);

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(jPanel3Layout.createSequentialGroup()
            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(46, Short.MAX_VALUE))
    );
    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addGap(11, 11, 11)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel12)
          .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(6, 6, 6)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel15)
          .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(6, 6, 6)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel14)
          .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel13)
          .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout databasePanelLayout = new javax.swing.GroupLayout(databasePanel);
    databasePanel.setLayout(databasePanelLayout);
    databasePanelLayout.setHorizontalGroup(
      databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(databasePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 637, Short.MAX_VALUE))
        .addContainerGap())
    );
    databasePanelLayout.setVerticalGroup(
      databasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(databasePanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addGap(75, 75, 75))
    );

    optionsTabbedPanel.addTab("Database", databasePanel);

    javax.swing.GroupLayout optionsPanelLayout = new javax.swing.GroupLayout(optionsPanel);
    optionsPanel.setLayout(optionsPanelLayout);
    optionsPanelLayout.setHorizontalGroup(
      optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(optionsPanelLayout.createSequentialGroup()
        .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(optionsPanelLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(okButton)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(cancelButton))
          .addComponent(optionsTabbedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE))
        .addContainerGap())
    );

    optionsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

    optionsPanelLayout.setVerticalGroup(
      optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, optionsPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(optionsTabbedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(okButton)
          .addComponent(cancelButton))
        .addContainerGap())
    );

    optionsTabbedPanel.getAccessibleContext().setAccessibleName("tab");

    jLabel10.setFont(jLabel10.getFont().deriveFont(jLabel10.getFont().getStyle() | java.awt.Font.BOLD, jLabel10.getFont().getSize()+2));
    jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel10.setText("Direct Mail Options");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(optionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 672, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel10)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(optionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addContainerGap())
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void saveHosts() {
    options.setOption(DmOptions.HOSTS_LIST, ArrayFunctions.join(hosts, String.valueOf(MainForm.options.arraySplitter)));
  }

  /**
   * Saves the options
   * @throws java.io.IOException
   */
  private void saveOptions() throws IOException, ParseException, Exception {
    getOptionsComponents();
    //Save the hosts
    saveHosts();
    options.save();
    options.loadOptions();
    dispose();
  }

  private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    try {
      saveOptions();
      // close window
      updateQuickOptions();
      dispose();
      // check connections
      CheckConnectionToDB c = new CheckConnectionToDB(m.getDatabases(), m);
      Thread t = new Thread(c);
      t.start();
      //m.applyNewColor();
      //MainForm.labelCurrentHost.setText(Options.toString(Options.HOST));
      Color newColor = options.toColor(DmOptions.SKIN_COLOR);
      String newFont = options.toString(DmOptions.FONT);
      String newLaf = options.toString(DmOptions.LAF);
      int newFontSize = options.toInt(DmOptions.FONT_SIZE);
      boolean integrate = checkbox_integrate.isSelected();
      integrateInShell(integrate);
      if ((!newColor.equals(origColor)
          || !newFont.equals(origFont)
          || !newLaf.equals(origLaf)
          || newFontSize != origFontSize)
          && (Messages.confirm("Restart Application?", "Skin color, font ,font size or Look & Feel changed\n"
          + "Restart the application?") == JOptionPane.YES_OPTION)) {
        m.reApplyStyle();
      }

    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (ParseException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (Exception ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }
  }//GEN-LAST:event_okButtonActionPerformed

  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    dispose();
  }//GEN-LAST:event_cancelButtonActionPerformed

private void comboBoxOptions_6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxOptions_6ActionPerformed
  try {
    String host = (String) comboBoxOptions_6.getSelectedItem();
    if (host.trim().equals("")) {
      textBoxOptions_9.setText("");
      textBoxOptions_10.setText("");
      return;
    }
    String[] allHosts = options.toArray(DmOptions.HOSTS_LIST);
    for (int i = 0; i < allHosts.length; i++) {
      String curHost = allHosts[i];
      String[] hostArr = curHost.split(",", -1);
      String key = hostArr[0];
      if (key.equals(host)) {
        String username = hostArr[1];
        String password = hostArr[2];
        textBoxOptions_9.setText(username);
        textBoxOptions_10.setText(password);
        return;
      }
    }
    textBoxOptions_9.setText("");
    textBoxOptions_10.setText("");
  } catch (NullPointerException ex) {
    textBoxOptions_9.setText("");
    textBoxOptions_10.setText("");
  }
}//GEN-LAST:event_comboBoxOptions_6ActionPerformed

private void buttonTestConnectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTestConnectionActionPerformed
  String host = (String) comboBoxOptions_6.getSelectedItem();
  String db = textBoxOptions_7.getText();
  String un = textBoxOptions_9.getText();
  String pass = "";
  char pw[] = textBoxOptions_10.getPassword();
  for (char c : pw) {
    pass += c;
  }
  if (hosts.contains(host + "," + un + "," + pass)) {
    Messages.customError("Host exists", "This host already exists in the list");
    return;
  }

  MyDBCConnection.connect(host, db, un, pass);
  if (MyDBCConnection.isConnected) {
    hosts.add(host + "," + un + "," + pass);
    Messages.customMessage("Host Added", "Host Added");
  } else {
    Errors.customError("Error", "Can't connect to database " + db);
  }
}//GEN-LAST:event_buttonTestConnectionActionPerformed

private void buttonOptions_23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOptions_23ActionPerformed
  ColorPanel c = new ColorPanel(options.toColor(DmOptions.SKIN_COLOR));
}//GEN-LAST:event_buttonOptions_23ActionPerformed

private void comboBox_fontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBox_fontActionPerformed
  updateFontPreview();
}//GEN-LAST:event_comboBox_fontActionPerformed

private void comboBox_fontSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBox_fontSizeActionPerformed
  updateFontPreview();
}//GEN-LAST:event_comboBox_fontSizeActionPerformed

private void combobox_setAddressingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_setAddressingActionPerformed
  int s = combobox_setAddressing.getSelectedIndex();
  switch (s) {
    case 1:
      textBoxOptions_2.setText("Αγαπητέ κύριε");
      textBoxOptions_3.setText("Αγαπητή κυρία");
      textBoxOptions_4.setText("Αγαπητοί κύριοι");
      textBoxOptions_5.setText("Αγαπητέ/τή κύριε/κυρία");
      break;
    case 2:
      textBoxOptions_2.setText("Αξιότιμε κύριε");
      textBoxOptions_3.setText("Αξιότιμη κυρία");
      textBoxOptions_4.setText("Αξιότιμοι κύριοι");
      textBoxOptions_5.setText("Αξιότιμε/μη κύριε/κυρία");
      break;

  }
}//GEN-LAST:event_combobox_setAddressingActionPerformed

private void checkBoxOptions_12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxOptions_12ActionPerformed
  m.checkbox_showOutPutProggress.setSelected(checkBoxOptions_12.isSelected());
}//GEN-LAST:event_checkBoxOptions_12ActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private static javax.swing.JPanel addressingPanel;
  private javax.swing.JButton buttonOptions_23;
  private javax.swing.JButton buttonTestConnection;
  private javax.swing.JButton cancelButton;
  private javax.swing.JCheckBox checkBoxOptions_12;
  private javax.swing.JCheckBox checkBoxOptions_13;
  private javax.swing.JCheckBox checkBoxOptions_16;
  private javax.swing.JCheckBox checkBoxOptions_17;
  private javax.swing.JCheckBox checkBoxOptions_20;
  private javax.swing.JCheckBox checkBoxOptions_24;
  private javax.swing.JCheckBox checkBoxOptions_25;
  private javax.swing.JCheckBox checkBox_loadlastOpenedFile;
  private javax.swing.JCheckBox checkbox_integrate;
  private javax.swing.JComboBox comboBoxOptions_11;
  private javax.swing.JComboBox comboBoxOptions_6;
  private javax.swing.JComboBox comboBox_font;
  private javax.swing.JComboBox comboBox_fontSize;
  private javax.swing.JComboBox comboBox_laf;
  private javax.swing.JComboBox combobox_checkOnSaving;
  private javax.swing.JComboBox combobox_setAddressing;
  private javax.swing.JPanel databasePanel;
  private javax.swing.JPanel filePanel;
  public static javax.swing.JPanel generalPanel;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel10;
  private javax.swing.JLabel jLabel11;
  private javax.swing.JLabel jLabel12;
  private javax.swing.JLabel jLabel13;
  private javax.swing.JLabel jLabel14;
  private javax.swing.JLabel jLabel15;
  private javax.swing.JLabel jLabel16;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JTextField jTextField1;
  private javax.swing.JTextField jTextField2;
  private javax.swing.JTextField jTextField3;
  private javax.swing.JTextField jTextField4;
  private javax.swing.JTextField jTextField5;
  private javax.swing.JLabel labelAdd1;
  private javax.swing.JLabel labelCompanies;
  private javax.swing.JLabel labelDatabase;
  private javax.swing.JLabel labelFemale;
  private javax.swing.JLabel labelHost;
  private javax.swing.JLabel labelLengthOfBrokenFields;
  private javax.swing.JLabel labelLinesInSample;
  private javax.swing.JLabel labelMale;
  private javax.swing.JLabel labelNumOfTKOrder;
  public static javax.swing.JLabel labelOptions_23;
  private javax.swing.JLabel labelPassword;
  private javax.swing.JLabel labelShowOutput;
  private javax.swing.JLabel labelTmpTable;
  private javax.swing.JLabel labelUknown;
  private javax.swing.JLabel labelUserName;
  private javax.swing.JLabel label_FontPreview;
  private javax.swing.JButton okButton;
  private javax.swing.JPanel optionsPanel;
  public static javax.swing.JTabbedPane optionsTabbedPanel;
  private static javax.swing.JTextField textBoxOptions_0;
  private static javax.swing.JTextField textBoxOptions_1;
  private javax.swing.JPasswordField textBoxOptions_10;
  private javax.swing.JTextField textBoxOptions_18;
  private javax.swing.JTextField textBoxOptions_19;
  private static javax.swing.JTextField textBoxOptions_2;
  private static javax.swing.JTextField textBoxOptions_3;
  private static javax.swing.JTextField textBoxOptions_4;
  private static javax.swing.JTextField textBoxOptions_5;
  private javax.swing.JTextField textBoxOptions_7;
  private javax.swing.JTextField textBoxOptions_8;
  private javax.swing.JTextField textBoxOptions_9;
  // End of variables declaration//GEN-END:variables

  private void updateFontPreview() {
    String font = (String) comboBox_font.getSelectedItem();
    if (ArrayFunctions.isInArray(font, env.getAvailableFontFamilyNames())) {
      int size;
      try {
        size = Integer.parseInt((String) comboBox_fontSize.getSelectedItem());
      } catch (NumberFormatException ex) {
        comboBox_fontSize.setSelectedIndex(0);
        size = 10;
      }
      Font f = new Font(font, Font.PLAIN, size);
      label_FontPreview.setFont(f);
    } else {
      comboBox_font.setSelectedItem(options.toString(DmOptions.FONT));
    }
  }

  private void updateQuickOptions() {
    m.combobox_checkOnSaving.setSelectedItem(options.toString(DmOptions.CHECK_ON_SAVING));
    m.combobox_database.setSelectedItem(options.toString(DmOptions.HOST));
    m.textField_maxLength.setText(options.toString(DmOptions.MAX_FIELD_LENGTH));
  }

  private void integrateInShell(boolean integrate) {
    String os = System.getProperty("os.name");
    if(os.indexOf("Windows") == -1){
      return ;
    }
    try {
      String dllPath = FileFunctions.getJarDir(Class.forName("DirectMail.Main.MainForm")) +
          "lib\\ICE_JNIRegistry.dll";
      System.load(dllPath);
    } catch (ClassNotFoundException ex) {
      
    }
    RegistryKey regkey = Registry.HKEY_CLASSES_ROOT;
    Registry registry = new Registry();
    RegistryKey command = Registry.openSubkey(regkey, "*\\shell\\Open in Direct Mail\\command", RegistryKey.ACCESS_ALL);
    if (integrate) {
      if (command == null) {
        try {
          command = regkey.createSubKey("*\\shell\\Open in Direct Mail\\command", "", RegistryKey.ACCESS_ALL);
          RegStringValue commandVal = new RegStringValue(command, "", RegistryValue.REG_SZ);
          String reg = "javaw.exe -jar -Xmx512m \"" + FileFunctions.getJarDir(Class.forName("DirectMail.Main.MainForm")) + "DirectMail.jar\" \"%1\"";
          commandVal.setData(reg);
          command.setValue(commandVal);
        } catch (ClassNotFoundException ex) {
          MainForm.myLog.log(Level.SEVERE, null, ex);
        } catch (RegistryException ex) {
          MainForm.myLog.log(Level.SEVERE, null, ex);
        }
      }
    } else {
      command = Registry.openSubkey(regkey, "*", RegistryKey.ACCESS_ALL);
      RegistryKey test = Registry.openSubkey(regkey, "*\\shell\\Open in Direct Mail\\command", RegistryKey.ACCESS_ALL);
      if (test != null) {
        try {
          command.deleteSubKey("shell\\Open in Direct Mail\\command");
          command.deleteSubKey("shell\\Open in Direct Mail");
          command.deleteSubKey("shell");
        } catch (NoSuchKeyException ex) {
          MainForm.myLog.log(Level.SEVERE, null, ex);
        } catch (RegistryException ex) {
          MainForm.myLog.log(Level.SEVERE, null, ex);
        }
      }
    }
  }
}
