/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SelectFilterField.java
 *
 * Created on 14 ��� 2009, 9:09:29 ��
 */
package DirectMail.Tools.Column.Forms;

import DirectMail.Help.Components.MyDraggable;
import DirectMail.Main.MainForm;
import DirectMail.Tools.Column.FilterColumn;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import org.omg.CORBA.INTERNAL;

/**
 *
 * @author ssoldatos
 */
public class SelectFilterField extends MyDraggable {

  public static final int _STARTS_WITH_ = 0;
  public static final int _EQUALS_ = 1;
  public static final int _DOES_NOT_EQUAL_ = 2;
  public static final int _INCLUDES_ = 3;
  public static final int _DOES_NOT_INCLUDE_ = 4;
  public static final int _ENDS_WITH_ = 5;
  public static final int _NUMERIC_SMALLER = 10;
  public static final int _NUMERIC_EQUAL = 11;
  public static final int _NUMERIC_BIGGER = 12;
  public int rule;
  public String text;
  public boolean caseSensitive;
  private ComboBoxModel stringModel = new DefaultComboBoxModel();
  private ComboBoxModel numericModel = new DefaultComboBoxModel();

  public SelectFilterField() {
    MainForm.glassPane.activate(null);
    initComponents();
    createStringModel();
    createNumericModel();
    setModel();
    setLocationRelativeTo(null);
    setVisible(true);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    combobox_rule = new javax.swing.JComboBox();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    textfield_text = new javax.swing.JTextField();
    button_ok = new javax.swing.JButton();
    button_cancel = new javax.swing.JButton();
    checkbox_sensitive = new javax.swing.JCheckBox();
    checkBox_numeric = new javax.swing.JCheckBox();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

    jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

    jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD, jLabel1.getFont().getSize()+2));
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText("Filter field");

    combobox_rule.setModel(stringModel);

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText("Filtering rule :");

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText("Text :");

    button_ok.setText("OK");
    button_ok.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        button_okActionPerformed(evt);
      }
    });

    button_cancel.setText("Cancel");
    button_cancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        button_cancelActionPerformed(evt);
      }
    });

    checkbox_sensitive.setText("Case Sensitive");
    checkbox_sensitive.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    checkbox_sensitive.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    checkbox_sensitive.setMargin(new java.awt.Insets(2, 0, 2, 2));

    checkBox_numeric.setText("Numeric");
    checkBox_numeric.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkBox_numericActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(combobox_rule, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBox_numeric))
              .addComponent(textfield_text, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(checkbox_sensitive))
            .addGap(11, 11, 11))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(button_ok)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(button_cancel)))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(combobox_rule, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(checkBox_numeric))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(textfield_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkbox_sensitive)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(button_ok)
          .addComponent(button_cancel))
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
      .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
    MainForm.glassPane.deactivate();
    rule = -1;
    dispose();
  }//GEN-LAST:event_button_cancelActionPerformed

  private void button_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_okActionPerformed
    if (!checkBox_numeric.isSelected()) {
      rule = combobox_rule.getSelectedIndex();
    } else {
      rule = combobox_rule.getSelectedIndex() + 10;
    }
    text = textfield_text.getText();
    caseSensitive = checkbox_sensitive.isSelected();
    dispose();
  }//GEN-LAST:event_button_okActionPerformed

  private void checkBox_numericActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBox_numericActionPerformed
    setModel();
  }//GEN-LAST:event_checkBox_numericActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton button_cancel;
  private javax.swing.JButton button_ok;
  private javax.swing.JCheckBox checkBox_numeric;
  private javax.swing.JCheckBox checkbox_sensitive;
  private javax.swing.JComboBox combobox_rule;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JTextField textfield_text;
  // End of variables declaration//GEN-END:variables

  private void setModel() {
    if (checkBox_numeric.isSelected()) {
      combobox_rule.setModel(numericModel);
    } else {
      combobox_rule.setModel(stringModel);
    }
  }

  private void createStringModel() {
    Vector<String> strValues = new Vector<String>();
    strValues.add("Starts With");
    strValues.add("Equals");
    strValues.add("Does not equal");
    strValues.add("Includes");
    strValues.add("Does not include");
    strValues.add("Ends with");
    stringModel = new DefaultComboBoxModel(strValues);
  }

  private void createNumericModel() {
    Vector<String> numValues = new Vector<String>();
    numValues.add("Smaller than");
    numValues.add("Equals");
    numValues.add("Bigger than");
    numericModel = new DefaultComboBoxModel(numValues);
  }
}
