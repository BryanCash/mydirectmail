/*
 * SelectCD.java
 *
 * Created on 28 ������� 2007, 8:57 ��
 */
package DirectMail.Tools.Row.Forms;

import DirectMail.Main.MainForm;
import DirectMail.*;
import DirectMail.Help.Components.FieldsDropDown;
import DirectMail.Help.Components.MyDraggable;
import DirectMail.Tools.Column.CreateCD;
import DirectMail.Tools.Row.DeleteFromFile;
import java.awt.Color;
import java.util.ArrayList;
import soldatos.functions.ArrayFunctions;

/**
 * Select fields for deleting
 * @author ssoldatos
 */
public class DeleteFromListPanel extends MyDraggable {

    int fieldToMatch = -1;
    private MainForm m;
    String[] list;
    /**
     * Constructor:
     * Inits the panel and adds a label for every field.
     * Clicking on a fields marks/umarks it for deleting.
     * @param m 
     */
    public DeleteFromListPanel(MainForm m) {
        MainForm.glassPane.activate(null);
        this.m = m;
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    labelTitle = new javax.swing.JLabel();
    okButton = new javax.swing.JButton();
    cancelButton = new javax.swing.JButton();
    jLabel1 = new javax.swing.JLabel();
    comboFields = new FieldsDropDown(m);
    jLabel2 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    textArea_list = new javax.swing.JTextArea();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Select fields to delete");
    setResizable(false);

    labelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    labelTitle.setText("Delete from List");

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

    jLabel1.setText("Field to match :");

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText("List to match:");

    jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    textArea_list.setColumns(20);
    textArea_list.setRows(5);
    jScrollPane1.setViewportView(textArea_list);

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(labelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
            .addComponent(okButton)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(cancelButton))
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel2)
              .addComponent(jLabel1))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
              .addComponent(comboFields, 0, 187, Short.MAX_VALUE))))
        .addContainerGap())
    );

    layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(labelTitle)
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(comboFields, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel1))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jLabel2)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(okButton)
          .addComponent(cancelButton))
        .addContainerGap())
    );

    pack();
    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    java.awt.Dimension dialogSize = getSize();
    setLocation((screenSize.width-dialogSize.width)/2,(screenSize.height-dialogSize.height)/2);
  }// </editor-fold>//GEN-END:initComponents
  
  /**
   * Cancels the breaking
   * @param evt Clicking th ecancel button event
   */
  private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    dispose();
    MainForm.glassPane.deactivate();
  }//GEN-LAST:event_cancelButtonActionPerformed
  
  /**
   * Commits the breaking
   * @param evt Clicking the OK button event
   */
  private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    dispose();
    fieldToMatch = comboFields.getSelectedIndex();
    String strList = textArea_list.getText();
    list = strList.split("\n", -1);
    DeleteFromFile dff = new DeleteFromFile(m, fieldToMatch, list);
    Thread t = new Thread(dff);
    t.start();
  }//GEN-LAST:event_okButtonActionPerformed
  
  
  
  /**
   * Changes the label's color to black
   * @param evt The mouse out event
   */
  private static void labelMouseOut(java.awt.event.MouseEvent evt) {
    //Font small = new Font("Monospace",Font.BOLD,12);
    //evt.getComponent().setCursor(new Cursor(Cursor.HAND_CURSOR));
    //evt.getComponent().setFont(small);
    evt.getComponent().setForeground(Color.BLACK);
  }
  
  
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton cancelButton;
  private javax.swing.JComboBox comboFields;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JLabel labelTitle;
  private javax.swing.JButton okButton;
  private javax.swing.JTextArea textArea_list;
  // End of variables declaration//GEN-END:variables
  
}
