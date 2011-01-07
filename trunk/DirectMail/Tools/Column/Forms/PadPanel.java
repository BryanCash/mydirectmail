/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PadPanel.java
 *
 * Created on 25 ��� 2009, 3:23:28 ��
 */
package DirectMail.Tools.Column.Forms;

import javax.swing.SpinnerNumberModel;
import soldatos.constants.Padding;

/**
 *
 * @author ssoldatos
 */
public class PadPanel extends javax.swing.JDialog {

  public int length;
  public String str;
  public int padding;
  private SpinnerNumberModel model;
  public boolean cancel = false;
  private int min;

  /**
   * 
   * @param longestField
   */
  public PadPanel(int longestField) {
    this.min = longestField;
    model = new SpinnerNumberModel(longestField, longestField > 0 ? 1 : 0, 200, 1);
    initComponents();
    setLocationRelativeTo(null);
    setModal(true);
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

    buttonGroup1 = new javax.swing.ButtonGroup();
    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    spinnerLength = new javax.swing.JSpinner();
    textfield_string = new javax.swing.JTextField();
    button_ok = new javax.swing.JButton();
    button_cancel = new javax.swing.JButton();
    radio_right = new javax.swing.JRadioButton();
    radio_left = new javax.swing.JRadioButton();
    checkBox_reduce = new javax.swing.JCheckBox();

    jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD, jLabel1.getFont().getSize()+2));
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText("Pad Field");

    jLabel2.setText("Field new Length :");

    jLabel3.setText("Pad Char:");

    spinnerLength.setModel(model);

    textfield_string.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        textfield_stringKeyReleased(evt);
      }
    });

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

    buttonGroup1.add(radio_right);
    radio_right.setSelected(true);
    radio_right.setText("Right padding");
    radio_right.setOpaque(false);

    buttonGroup1.add(radio_left);
    radio_left.setText("Left padding");
    radio_left.setOpaque(false);

    checkBox_reduce.setText("Reduce chars");
    checkBox_reduce.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        checkBox_reduceActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(jLabel3)
              .addComponent(jLabel2))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(spinnerLength, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
              .addComponent(radio_right)
              .addComponent(radio_left)
              .addComponent(textfield_string, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(checkBox_reduce))
          .addGroup(layout.createSequentialGroup()
            .addComponent(button_ok)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(button_cancel)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(spinnerLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(checkBox_reduce))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(textfield_string, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(radio_right)
        .addGap(3, 3, 3)
        .addComponent(radio_left)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(button_ok)
          .addComponent(button_cancel))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
      this.cancel = true;
      dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_okActionPerformed
      length = Integer.parseInt(String.valueOf(spinnerLength.getValue()));
      str = textfield_string.getText();
      padding = radio_right.isSelected() ? Padding.RIGHT_PAD : Padding.LEFT_PAD;
      dispose();
    }//GEN-LAST:event_button_okActionPerformed

    private void textfield_stringKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textfield_stringKeyReleased
      if (textfield_string.getText().length() > 1) {
        textfield_string.setText(textfield_string.getText().substring(0, 1));
      }
    }//GEN-LAST:event_textfield_stringKeyReleased

    private void checkBox_reduceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBox_reduceActionPerformed
//      if(checkBox_reduce.isSelected()){
//      model.setMinimum(1);
//      } else {
//        model.setMinimum(min);
//        model.setValue(min);
//      }
    }//GEN-LAST:event_checkBox_reduceActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.ButtonGroup buttonGroup1;
  private javax.swing.JButton button_cancel;
  private javax.swing.JButton button_ok;
  public javax.swing.JCheckBox checkBox_reduce;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JRadioButton radio_left;
  private javax.swing.JRadioButton radio_right;
  private javax.swing.JSpinner spinnerLength;
  private javax.swing.JTextField textfield_string;
  // End of variables declaration//GEN-END:variables
}
