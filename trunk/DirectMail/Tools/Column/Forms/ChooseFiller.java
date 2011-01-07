/*
 * SelectBreakFields.java
 *
 * Created on 28 Ιούλιος 2007, 8:57 μμ
 */
package DirectMail.Tools.Column.Forms;

import DirectMail.Main.MainForm;
import DirectMail.*;
import DirectMail.Help.Components.FieldsDropDown;
import DirectMail.Help.Components.MyDraggable;
import DirectMail.Pre.ShiftFields;
import DirectMail.Tools.Column.FillField;
import javax.swing.DefaultComboBoxModel;

/**
 * Select fields for deleting
 * @author ssoldatos
 */
public class ChooseFiller extends MyDraggable {

  /**
   * A boolean array holding a boolean value for every field
   */
  boolean fieldBefore[];
  int fromField, toField;
  String fields[];
  String text;
  private int selectedColumn;
  private MainForm m;

  /**
   * Constructor:
   * Inits the panel and adds a label for every field.
   * Clicking on a fields marks/umarks it for deleting.
   * @param selectedColumn
   * @param m
   */
  public ChooseFiller(int selectedColumn, MainForm m) {
    this.selectedColumn = selectedColumn;
    this.m = m;
    initComponents();
    MainForm.glassPane.activate(null);
    setLocationRelativeTo(m);
    setVisible(true);
  }

  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    labelTitle = new javax.swing.JLabel();
    okButton = new javax.swing.JButton();
    cancelButton = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();
    combo_filler = new FieldsDropDown(m);

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Select fields to shift");
    setMinimumSize(new java.awt.Dimension(400, 130));

    jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
    jPanel1.setMinimumSize(new java.awt.Dimension(416, 118));

    labelTitle.setFont(new java.awt.Font("Tahoma", 1, 12));
    labelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    labelTitle.setText("Fill  Field");

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

    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel1.setText("Select field to fill from: ");

    combo_filler.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        combo_fillerActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(combo_filler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(okButton)
            .addGap(5, 5, 5)
            .addComponent(cancelButton))
          .addComponent(labelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
        .addContainerGap())
    );

    jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(labelTitle)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(combo_filler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(cancelButton)
          .addComponent(okButton)))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  private void combo_fillerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_fillerActionPerformed
    fromField = combo_filler.getSelectedIndex();
}//GEN-LAST:event_combo_fillerActionPerformed

  /**
   * Cancels the shifting
   * @param evt Clicking th ecancel button event
   */
  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    dispose();
    MainForm.glassPane.deactivate();
  }//GEN-LAST:event_cancelButtonActionPerformed

  /**
   * Commits the shifting
   * @param evt Clicking the OK button event
   */
  private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    if (combo_filler.getSelectedIndex() == selectedColumn) {
      soldatos.messages.Messages.customError("Wrong Filler", "You can't fill a field by itself!!!!");
    } else {
      FillField sf = new FillField(m,combo_filler.getSelectedIndex(), selectedColumn);
      Thread t = new Thread(sf);
      t.start();
      dispose();
    }
  }//GEN-LAST:event_okButtonActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton cancelButton;
  private javax.swing.JComboBox combo_filler;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JLabel labelTitle;
  private javax.swing.JButton okButton;
  // End of variables declaration//GEN-END:variables
}
