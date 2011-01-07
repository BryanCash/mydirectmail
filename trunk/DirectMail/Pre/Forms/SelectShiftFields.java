/*
 * SelectBreakFields.java
 *
 * Created on 28 Ιούλιος 2007, 8:57 μμ
 */
package DirectMail.Pre.Forms;

import DirectMail.Main.MainForm;
import DirectMail.*;
import DirectMail.Help.Components.MyDraggable;
import DirectMail.Pre.ShiftFields;
import javax.swing.DefaultComboBoxModel;

/**
 * Select fields for deleting
 * @author ssoldatos
 */
public class SelectShiftFields extends MyDraggable {

  /**
   * A boolean array holding a boolean value for every field
   */
  boolean fieldBefore[];
  int fromField, toField;
  String fields[];
  String text;
  private MainForm m;

  /**
   * Constructor:
   * Inits the panel and adds a label for every field.
   * Clicking on a fields marks/umarks it for deleting.
   * @param m 
   */
  public SelectShiftFields(MainForm m) {
    this.m = m;
    initComponents();
    MainForm.glassPane.activate(null);
    fields = new String[m.getFields()];
    for (int i = 0; i < m.getFields(); i++) {
      fields[i] = String.valueOf(m.getColModel().getColumn(i).getHeaderValue());
    }
    fieldFromCombo.setModel(new DefaultComboBoxModel(fields));
    fieldToCombo.setModel(new DefaultComboBoxModel(fields));
    if (m.getCustomerCodeField() > -1) {
      if (m.getCustomerCodeField() == 6) {
        fieldFromCombo.setSelectedIndex(7);
      } else {
        soldatos.messages.Messages.customError("Shift Fields", "You may want to move the customer code field at the 6th position");
        dispose();
        MainForm.glassPane.deactivate();
        return;
      }
    } else {
      fieldFromCombo.setSelectedIndex(6);
    }
    int to = m.getAbroadField() > -1 ? m.getAbroadField() : (m.getTkField() > -1 ? m.getTkField() : 0);
    fieldToCombo.setSelectedIndex(to);
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
    fieldFromCombo = new javax.swing.JComboBox();
    jLabel2 = new javax.swing.JLabel();
    fieldToCombo = new javax.swing.JComboBox();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Select fields to shift");
    setMinimumSize(new java.awt.Dimension(400, 130));

    jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

    labelTitle.setFont(new java.awt.Font("Tahoma", 1, 12));
    labelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    labelTitle.setText("Shift Fields");

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
    jLabel1.setText("Shift Fields From Field:");

    fieldFromCombo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fieldFromComboActionPerformed(evt);
      }
    });

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText("Shift Fields To Field:");

    fieldToCombo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fieldToComboActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(labelTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(10, 10, 10)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGap(40, 40, 40)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(fieldToCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(fieldFromCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(okButton)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(cancelButton)))
        .addContainerGap())
    );

    jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(labelTitle)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel1)
          .addComponent(fieldFromCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(fieldToCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(okButton)
          .addComponent(cancelButton))
        .addContainerGap(20, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  private void fieldFromComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldFromComboActionPerformed
    fromField = fieldFromCombo.getSelectedIndex();
}//GEN-LAST:event_fieldFromComboActionPerformed

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
    if (fromField >= toField) {
      soldatos.messages.Messages.customError("Wrong Selection", "The selection you made was wrong.\n\"From field\" must be before \"to field\"");
    } else {
      ShiftFields sf = new ShiftFields(m, fromField, toField);
      Thread t = new Thread(sf);
      t.start();
      dispose();
    }
  }//GEN-LAST:event_okButtonActionPerformed

private void fieldToComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldToComboActionPerformed
  toField = fieldToCombo.getSelectedIndex();
}//GEN-LAST:event_fieldToComboActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton cancelButton;
  private javax.swing.JComboBox fieldFromCombo;
  private javax.swing.JComboBox fieldToCombo;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JLabel labelTitle;
  private javax.swing.JButton okButton;
  // End of variables declaration//GEN-END:variables
}
