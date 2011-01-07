/*
 * SelectBreakFields.java
 *
 * Created on 28 ������� 2007, 8:57 ��
 */
package DirectMail.Tools.Column.Forms;

import DirectMail.Main.MainForm;
import DirectMail.*;
import DirectMail.Help.Components.MyDraggable;
import DirectMail.Tools.Column.DeleteTheFields;
import soldatos.functions.SwingFunctions;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.JLabel;
import soldatos.functions.StringFunctions;

/**
 * Select fields for deleting
 * @author ssoldatos
 */
public class SelectDeleteFields extends MyDraggable {

  /**
   * A boolean array holding a boolean value for every field
   * True : break the field
   * False : Do not break it
   */
  boolean fieldsToDelete[];
  private MainForm m;

  /**
   * Constructor:
   * Inits the panel and adds a label for every field.
   * Clicking on a fields marks/umarks it for deleting.
   * @param m 
   */
  public SelectDeleteFields(MainForm m) {
    this.m = m;
    fieldsToDelete = new boolean[m.getFields()];
    initComponents();
     addDeleteFields();
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

    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    deleteFieldsPanel = new javax.swing.JPanel();
    labelTitle = new javax.swing.JLabel();
    okButton = new javax.swing.JButton();
    cancelButton = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle("Select fields to delete");
    setResizable(false);

    jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

    deleteFieldsPanel.setLayout(new javax.swing.BoxLayout(deleteFieldsPanel, javax.swing.BoxLayout.Y_AXIS));
    jScrollPane1.setViewportView(deleteFieldsPanel);

    labelTitle.setFont(labelTitle.getFont().deriveFont(labelTitle.getFont().getStyle() | java.awt.Font.BOLD, labelTitle.getFont().getSize()+2));
    labelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    labelTitle.setText("Select fields to delete");

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

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 417, Short.MAX_VALUE)
          .addComponent(labelTitle, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
            .addComponent(okButton)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
        .addGap(18, 18, 18)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(cancelButton)
          .addComponent(okButton))
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
      .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
    fieldsToDelete = null;
    dispose();
  }//GEN-LAST:event_cancelButtonActionPerformed

  /**
   * Commits the breaking
   * @param evt Clicking the OK button event
   */
  private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    dispose();
    DeleteTheFields df = new DeleteTheFields(fieldsToDelete, m);
    Thread t = new Thread(df);
    t.start();
  }//GEN-LAST:event_okButtonActionPerformed

  /**
   * Adds the labels for all the fields on the panel
   */
  private void addDeleteFields() {
    String flArr[] = new String[m.getFields()];
    flArr = m.getFirstLine().split("" + m.getDelimeter(), -1);
    final Font selected = new Font("Monospaced", Font.BOLD, 14);
    final Font notSelected = new Font("Monospaced", Font.PLAIN, 14);

    for (int i = 0; i < m.getFields(); i++) {
      fieldsToDelete[i] = false;
      JLabel l = new JLabel();
       l.setText(StringFunctions.padRight(m.getHeaderTitles().get(i), 12, " ") + ": " + StringFunctions.cutString(flArr[i], 20));
      l.setName("" + i);
      l.setFont(notSelected);

      // mouse over
      l.addMouseListener(new java.awt.event.MouseAdapter() {

        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
          labelMouseOver(evt);
        }
      });
      //mouseout
      l.addMouseListener(new java.awt.event.MouseAdapter() {

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
          labelMouseOut(evt);
        }
      });

      // Click
      l.addMouseListener(new java.awt.event.MouseAdapter() {

        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
          if (evt.getComponent().getFont() == selected) {
            evt.getComponent().setFont(notSelected);
          } else {
            evt.getComponent().setFont(selected);
          }
          int id = Integer.parseInt(evt.getComponent().getName());
          fieldsToDelete[id] = !fieldsToDelete[id];
        }
      });
      deleteFieldsPanel.add(l);
    }

  }

  /**
   * Sets the cursor to hand
   * Changes the label's color to red
   * @param evt The mouse over event
   */
  private static void labelMouseOver(java.awt.event.MouseEvent evt) {
    // Font big = new Font("Monospace",Font.BOLD,14);
    evt.getComponent().setCursor(new Cursor(Cursor.HAND_CURSOR));
    //evt.getComponent().setFont(big);
    evt.getComponent().setForeground(Color.RED);
  }

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
  private javax.swing.JPanel deleteFieldsPanel;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JLabel labelTitle;
  private javax.swing.JButton okButton;
  // End of variables declaration//GEN-END:variables
}
