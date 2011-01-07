/*
 * SelectSplitFile.java
 *
 * Created on 7 ��������� 2007, 4:20 ��
 */
package DirectMail.Tools.File.Forms;

import DirectMail.Help.Components.FieldsDropDown;
import DirectMail.Help.Components.MyDraggable;
import DirectMail.Help.Components.Errors;
import DirectMail.Main.MainForm;
import DirectMail.Tools.File.SplitTheFile;

/**
 *
 * @author  ssoldatos
 */
public class SelectSplitFile extends MyDraggable {

  private MainForm m;

  /**
   * Creates new form SelectSplitFile
   * @param m 
   */
  public SelectSplitFile(MainForm m) {
    MainForm.glassPane.activate(null);
    this.m = m;
    initComponents();
    setLocationRelativeTo(m);
    setVisible(true);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    buttonGroup1 = new javax.swing.ButtonGroup();
    jPanel1 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    jRadioButton1 = new javax.swing.JRadioButton();
    jRadioButton2 = new javax.swing.JRadioButton();
    jRadioButton3 = new javax.swing.JRadioButton();
    jRadioButton4 = new javax.swing.JRadioButton();
    textBoxStartTo = new javax.swing.JTextField();
    textBoxFromToEnd = new javax.swing.JTextField();
    jLabel2 = new javax.swing.JLabel();
    textBoxFrom = new javax.swing.JTextField();
    jLabel3 = new javax.swing.JLabel();
    textBoxTo = new javax.swing.JTextField();
    textBoxEvery = new javax.swing.JTextField();
    jLabel4 = new javax.swing.JLabel();
    okButton = new javax.swing.JButton();
    cancelButton = new javax.swing.JButton();
    jRadioButton5 = new javax.swing.JRadioButton();
    combo_fields = new FieldsDropDown(m);

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Split File");
    setResizable(false);

    jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

    jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD, jLabel1.getFont().getSize()+2));
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText("Select how to split the file");

    buttonGroup1.add(jRadioButton1);
    jRadioButton1.setText("From the start  to");
    jRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    jRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
    jRadioButton1.setOpaque(false);

    buttonGroup1.add(jRadioButton2);
    jRadioButton2.setText("From");
    jRadioButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    jRadioButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));
    jRadioButton2.setOpaque(false);

    buttonGroup1.add(jRadioButton3);
    jRadioButton3.setText("From");
    jRadioButton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    jRadioButton3.setMargin(new java.awt.Insets(0, 0, 0, 0));
    jRadioButton3.setOpaque(false);

    buttonGroup1.add(jRadioButton4);
    jRadioButton4.setText("Every");
    jRadioButton4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    jRadioButton4.setMargin(new java.awt.Insets(0, 0, 0, 0));
    jRadioButton4.setOpaque(false);

    textBoxStartTo.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        textBoxStartToFocusGained(evt);
      }
    });

    textBoxFromToEnd.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        textBoxFromToEndFocusGained(evt);
      }
    });

    jLabel2.setText("to the end");

    textBoxFrom.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        textBoxFromFocusGained(evt);
      }
    });

    jLabel3.setText("to");

    textBoxTo.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        textBoxToFocusGained(evt);
      }
    });

    textBoxEvery.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        textBoxEveryFocusGained(evt);
      }
    });

    jLabel4.setText("lines");

    okButton.setText("OK");
    okButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
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

    buttonGroup1.add(jRadioButton5);
    jRadioButton5.setText("By Field");
    jRadioButton5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
    jRadioButton5.setMargin(new java.awt.Insets(0, 0, 0, 0));
    jRadioButton5.setOpaque(false);

    combo_fields.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        combo_fieldsFocusGained(evt);
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
            .addGap(35, 35, 35)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textBoxStartTo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                  .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                    .addComponent(jRadioButton5)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(combo_fields, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                  .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                    .addComponent(jRadioButton2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(textBoxFromToEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                      .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jRadioButton4)
                        .addGap(2, 2, 2)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                      .addComponent(textBoxFrom)
                      .addComponent(textBoxEvery, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(jLabel4)
                  .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(jLabel3)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(textBoxTo, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                  .addComponent(jLabel2))))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(cancelButton)))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jRadioButton1)
          .addComponent(textBoxStartTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jRadioButton2)
          .addComponent(textBoxFromToEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel2))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jRadioButton3)
          .addComponent(textBoxFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel3)
          .addComponent(textBoxTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(6, 6, 6)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jRadioButton4)
          .addComponent(textBoxEvery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel4))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jRadioButton5)
          .addComponent(combo_fields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(okButton)
          .addComponent(cancelButton))
        .addContainerGap())
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    pack();
    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    java.awt.Dimension dialogSize = getSize();
    setLocation((screenSize.width-dialogSize.width)/2,(screenSize.height-dialogSize.height)/2);
  }// </editor-fold>//GEN-END:initComponents
  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    dispose();
    MainForm.glassPane.deactivate();
  }//GEN-LAST:event_cancelButtonActionPerformed

  private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    int num1 = 0, num2 = 0;
    boolean num2OK = false, num1OK = false, submit = false;
    String action = "";
    // Start To NUM
    if (jRadioButton1.isSelected()) {
      if (textBoxStartTo.getText().isEmpty()) {
        Errors.customError("Error", "Please give a valid number for splitting the file");
        return;
      } else {
        try {
          num1 = Integer.parseInt(textBoxStartTo.getText());
          action = "StartToNum";
          submit = true;
        } catch (NumberFormatException ex) {
          Errors.customError("Error", "Please give a valid number for splitting the file");
          textBoxStartTo.setText("");
          textBoxStartTo.requestFocus();
          return;
        }
      }
    } // NUM To End selected
    else if (jRadioButton2.isSelected()) {
      if (textBoxFromToEnd.getText().isEmpty()) {
        Errors.customError("Error", "Please give a valid number for splitting the file");
        return;
      } else {
        try {
          num1 = Integer.parseInt(textBoxFromToEnd.getText());
          action = "NumToEnd";
          submit = true;
        } catch (NumberFormatException ex) {
          Errors.customError("Error", "Please give a valid number for splitting the file");
          textBoxFromToEnd.setText("");
          textBoxFromToEnd.requestFocus();
          return;
        }
      }
    } // FROM NUM1 TO NUM 2 SELECTED
    else if (jRadioButton3.isSelected()) {
      if (textBoxFrom.getText().isEmpty() || textBoxTo.getText().isEmpty()) {
        Errors.customError("Error", "Please give a valid number for splitting the file");
        return;
      } else {
        try {
          num1 = Integer.parseInt(textBoxFrom.getText());
          num1OK = true;
        } catch (NumberFormatException ex) {
          Errors.customError("Error", "Please give a valid number for splitting the file");
          textBoxFrom.setText("");
          textBoxFrom.requestFocus();
          return;
        }
        try {
          num2 = Integer.parseInt(textBoxTo.getText());
          num2OK = true;
        } catch (NumberFormatException ex) {
          Errors.customError("Error", "Please give a valid number for splitting the file");
          textBoxTo.setText("");
          textBoxTo.requestFocus();
          return;
        }
        if (num1OK && num2OK) {
          if (num2 < num1) {
            Errors.customError("Wrong Numbers", "Start number cannot be greater then the end number");
            return;
          } else {
            // Submit
            submit = true;
          }
        }
      }
    } // EVERY NUM SELECTED
    else if (jRadioButton4.isSelected()) {
      if (textBoxEvery.getText().isEmpty()) {
        Errors.customError("Error", "Please give a valid number for splitting the file");
        return;
      } else {
        try {
          num1 = Integer.parseInt(textBoxEvery.getText());
          action = "EveryNum";
          submit = true;
        } catch (NumberFormatException ex) {
          Errors.customError("Error", "Please give a valid number for splitting the file");
          textBoxEvery.setText("");
          textBoxEvery.requestFocus();
          return;
        }
      }
    } else if (jRadioButton5.isSelected()) {
      num1 = combo_fields.getSelectedIndex();
      action = "Field";
      submit = true;
    } else {
      Errors.customError("Error", "Please give a valid number for splitting the file");
      return;
    }

    if (submit) {
      dispose();
      if (!action.isEmpty()) {
          SplitTheFile sf = new SplitTheFile(num1, action, m);
          Thread t = new Thread(sf);
          t.start();
      } else {
        SplitTheFile sf = new SplitTheFile(num1, num2, m);
        Thread t = new Thread(sf);
        t.start();
      }
    }
  }//GEN-LAST:event_okButtonActionPerformed

  private void textBoxEveryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textBoxEveryFocusGained
    jRadioButton4.setSelected(true);
  }//GEN-LAST:event_textBoxEveryFocusGained

  private void textBoxToFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textBoxToFocusGained
    jRadioButton3.setSelected(true);
  }//GEN-LAST:event_textBoxToFocusGained

  private void textBoxFromFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textBoxFromFocusGained
    jRadioButton3.setSelected(true);
  }//GEN-LAST:event_textBoxFromFocusGained

  private void textBoxFromToEndFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textBoxFromToEndFocusGained
    jRadioButton2.setSelected(true);
  }//GEN-LAST:event_textBoxFromToEndFocusGained

  private void textBoxStartToFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textBoxStartToFocusGained
    jRadioButton1.setSelected(true);
  }//GEN-LAST:event_textBoxStartToFocusGained

  private void combo_fieldsFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_combo_fieldsFocusGained
    jRadioButton5.setSelected(true);
  }//GEN-LAST:event_combo_fieldsFocusGained
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.ButtonGroup buttonGroup1;
  private javax.swing.JButton cancelButton;
  private javax.swing.JComboBox combo_fields;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JRadioButton jRadioButton1;
  private javax.swing.JRadioButton jRadioButton2;
  private javax.swing.JRadioButton jRadioButton3;
  private javax.swing.JRadioButton jRadioButton4;
  private javax.swing.JRadioButton jRadioButton5;
  private javax.swing.JButton okButton;
  private javax.swing.JTextField textBoxEvery;
  private javax.swing.JTextField textBoxFrom;
  private javax.swing.JTextField textBoxFromToEnd;
  private javax.swing.JTextField textBoxStartTo;
  private javax.swing.JTextField textBoxTo;
  // End of variables declaration//GEN-END:variables
}