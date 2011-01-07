/*
 * SelectBreakFields.java
 *
 * Created on 28 Ιούλιος 2007, 8:57 μμ
 */

package DirectMail.Tools.Row.Forms;

import DirectMail.Help.Components.Errors;
import DirectMail.*;
import DirectMail.Main.MainForm;
import DirectMail.Tools.Row.MultiplyLines;

/**
 * Select fields for deleting
 * @author ssoldatos
 */
public class SelectMultiplyLines extends javax.swing.JFrame {
  /**
   * A boolean array holding a boolean value for every field
   */
  int multiplier = 1;
  private MainForm m;
  
  /**
   * Constructor:
   * Inits the panel and adds a label for every field.
   * Clicking on a fields marks/umarks it for deleting.
   * @param m 
   */
  public SelectMultiplyLines(MainForm m){
    this.m = m;
    setVisible(true);
    initComponents();
    setLocationRelativeTo(null);
    
  }
  
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    labelTitle = new javax.swing.JLabel();
    okButton = new javax.swing.JButton();
    cancelButton = new javax.swing.JButton();
    multiplierTextfield = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Multiply Lines");
    setResizable(false);

    labelTitle.setFont(new java.awt.Font("Tahoma", 1, 12));
    labelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    labelTitle.setText("Multiply Lines");

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

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText("How many times?");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(labelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
          .addGroup(layout.createSequentialGroup()
            .addGap(24, 24, 24)
            .addComponent(jLabel2)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(multiplierTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(okButton)
            .addGap(19, 19, 19)
            .addComponent(cancelButton)))
        .addContainerGap())
    );

    layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(labelTitle)
        .addGap(22, 22, 22)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(multiplierTextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(cancelButton)
          .addComponent(okButton))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  
  /**
   * Cancels the breaking
   * @param evt Clicking th ecancel button event
   */
  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    dispose();
  }//GEN-LAST:event_cancelButtonActionPerformed
  
  /**
   * Commits the breaking
   * @param evt Clicking the OK button event
   */
  private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    
    try {
      int lineMultiplier = Integer.parseInt(multiplierTextfield.getText());
      MultiplyLines ml = new MultiplyLines(lineMultiplier, m);
      Thread t = new Thread(ml);
      t.start();
      dispose();
    } catch (NumberFormatException ex) {
      setVisible(false);
      Errors.customError("Not an Integer","The text you entered is not a valid integer");
      multiplierTextfield.setText("");
      setVisible(true);
    }
  }//GEN-LAST:event_okButtonActionPerformed
  
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton cancelButton;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel labelTitle;
  private javax.swing.JTextField multiplierTextfield;
  private javax.swing.JButton okButton;
  // End of variables declaration//GEN-END:variables
  
}
