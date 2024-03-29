/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GetFlagPosition.java
 *
 * Created on 10 ��� 2009, 11:48:08 ��
 */

package DirectMail.Post.Forms;

/**
 *
 * @author ssoldatos
 */
public class GetFlagPosition extends javax.swing.JDialog {
  public int position = -1;


  /** Creates new form GetFlagPosition
   * @param parent
   * @param modal
   * @param text
   */
    public GetFlagPosition(java.awt.Frame parent, boolean modal, String text) {
        super(parent, modal);
        initComponents();
        textArea_string.setText(text);
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

    jLabel1 = new javax.swing.JLabel();
    jScrollPane1 = new javax.swing.JScrollPane();
    textArea_string = new javax.swing.JTextArea();
    button_ok = new javax.swing.JButton();
    button_cancel = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD, jLabel1.getFont().getSize()+2));
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText("Mark the enveloping flag");

    jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

    textArea_string.setColumns(20);
    textArea_string.setFont(new java.awt.Font("Courier New", 1, 16));
    textArea_string.setRows(5);
    jScrollPane1.setViewportView(textArea_string);

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

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 661, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 661, Short.MAX_VALUE)
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
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(button_cancel)
          .addComponent(button_ok))
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void button_cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_cancelActionPerformed
      dispose();
    }//GEN-LAST:event_button_cancelActionPerformed

    private void button_okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_okActionPerformed
      position = textArea_string.getSelectionStart();
      dispose();
    }//GEN-LAST:event_button_okActionPerformed

   

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton button_cancel;
  private javax.swing.JButton button_ok;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTextArea textArea_string;
  // End of variables declaration//GEN-END:variables

}
