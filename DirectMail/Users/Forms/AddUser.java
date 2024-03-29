/*
 * addUser.java
 *
 * Created on 7 ���������� 2008, 9:36 ��
 */
package DirectMail.Users.Forms;

import DirectMail.Main.MainForm;
import soldatos.functions.SwingFunctions;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import soldatos.functions.EncodingFunctions;

/**
 *
 * @author  ssoldatos
 */
public class AddUser extends javax.swing.JFrame implements Runnable {

  /** Creates new form addUser */
  public AddUser() {
    initComponents();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    jLabel4 = new javax.swing.JLabel();
    tf_username = new javax.swing.JTextField();
    tf_password = new javax.swing.JTextField();
    comboType = new javax.swing.JComboBox();
    buttonOK = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Direct Mail :: Add User");

    jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14));
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText("User Administration");

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText("Username :");

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText("Password :");

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText("Usertype :");

    comboType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Enveloper", "Printer", "Administrator" }));

    buttonOK.setText("OK");
    buttonOK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonOKActionPerformed(evt);
      }
    });

    buttonCancel.setText("Cancel");
    buttonCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonCancelActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
              .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(comboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(tf_username, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
              .addComponent(tf_password)))
          .addGroup(layout.createSequentialGroup()
            .addComponent(buttonOK)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonCancel)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel2)
          .addComponent(tf_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel3)
          .addComponent(tf_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(jLabel4)
          .addComponent(comboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonOK)
          .addComponent(buttonCancel))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents
  private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
    dispose();
  }//GEN-LAST:event_buttonCancelActionPerformed

  private void buttonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOKActionPerformed
    try {
      FileWriter append = null;
      String type = "";
      String un = tf_username.getText().trim();
      String pass = tf_password.getText().trim();
      String cType = (String) comboType.getSelectedItem();
      if (cType.equals("Administrator")) {
        type = "admin";
      } else if (cType.equals("Enveloper")) {
        type = "enveloper";
      } else if (cType.equals("Printer")) {
        type = "printer";
      }
      append = new FileWriter("users.dat", true);
      String user = un + "\t" + EncodingFunctions.base64Encode(pass) + "\t"
          + EncodingFunctions.base64Encode(type + un);
      append.write(user + "\n");
      dispose();
    } catch (IOException ex) {
        MainForm.myLog.log(Level.WARNING, null, ex);
    }

  }//GEN-LAST:event_buttonOKActionPerformed

  @Override
  public void run() {
    setVisible(true);
    setLocationRelativeTo(null);
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton buttonOK;
  private javax.swing.JComboBox comboType;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JTextField tf_password;
  private javax.swing.JTextField tf_username;
  // End of variables declaration//GEN-END:variables
}
