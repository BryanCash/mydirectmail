/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SelectExportDB2.java
 *
 * Created on 7 ���� 2009, 12:44:57 ��
 */
package DirectMail.Tools.File.Export.Forms;

import DirectMail.Help.Action;
import DirectMail.Help.Components.MyDraggable;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import soldatos.connection.MyDBCConnection;

/**
 *
 * @author ssoldatos
 */
public class SelectExportDB extends MyDraggable {

  public boolean export;
  private MainForm m;
  public String username;
  public String database;
  public String password;
  public String tablename;
  public boolean createColumnNames;
  private ComboBoxModel databases;
  private ComboBoxModel tables;
  public boolean append = true;

  /** Creates new form SelectExportDB2
   * @param m
   */
  public SelectExportDB(MainForm m) {
    try {
      MainForm.glassPane.activate(null);
      this.m = m;
      getDbs();
      initComponents();
      setLocationRelativeTo(null);
      setVisible(true);
    } catch (SQLException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }

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
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    jLabel4 = new javax.swing.JLabel();
    textField_username = new javax.swing.JTextField();
    textfield_password = new javax.swing.JTextField();
    jLabel5 = new javax.swing.JLabel();
    textfield_tablename = new javax.swing.JTextField();
    checkbox_columnNames = new javax.swing.JCheckBox();
    button_OK = new javax.swing.JButton();
    buttonCancel = new javax.swing.JButton();
    combo_databases = new javax.swing.JComboBox();
    jLabel6 = new javax.swing.JLabel();
    combo_tables = new javax.swing.JComboBox();
    checkbox_append = new javax.swing.JCheckBox();
    jLabel7 = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

    jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

    jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD, jLabel1.getFont().getSize()+2));
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText("Select Export to Database properties");

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText("Database :");

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText("Username :");

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText("Password :");

    textField_username.setText(m.options.toString(DmOptions.DB_USER));

    textfield_password.setText(m.options.toString(DmOptions.DB_PASSWORD));

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText("New :");

    textfield_tablename.setText(getTableName());

    checkbox_columnNames.setText("Create column names from first line");

    button_OK.setText("OK");
    button_OK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        button_OKActionPerformed(evt);
      }
    });

    buttonCancel.setText("Cancel");
    buttonCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonCancelActionPerformed(evt);
      }
    });

    combo_databases.setModel(databases);
    combo_databases.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        combo_databasesActionPerformed(evt);
      }
    });

    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel6.setText("Existing :");

    combo_tables.setModel(tables);
    combo_tables.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        combo_tablesActionPerformed(evt);
      }
    });

    checkbox_append.setSelected(true);
    checkbox_append.setText("Append");
    checkbox_append.setEnabled(false);

    jLabel7.setFont(jLabel7.getFont().deriveFont(jLabel7.getFont().getStyle() | java.awt.Font.BOLD, jLabel7.getFont().getSize()+2));
    jLabel7.setText("Table");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addGap(10, 10, 10)
        .addComponent(button_OK)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(buttonCancel)
        .addContainerGap(192, Short.MAX_VALUE))
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
        .addContainerGap(142, Short.MAX_VALUE)
        .addComponent(jLabel7)
        .addGap(144, 144, 144))
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
              .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
              .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(checkbox_append, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
              .addComponent(checkbox_columnNames, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
              .addComponent(textfield_tablename, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
              .addComponent(combo_tables, 0, 195, Short.MAX_VALUE)
              .addComponent(textfield_password, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
              .addComponent(textField_username, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
              .addComponent(combo_databases, 0, 195, Short.MAX_VALUE))))
        .addGap(20, 20, 20))
    );

    jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {combo_databases, combo_tables, textField_username, textfield_password, textfield_tablename});

    jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {checkbox_append, checkbox_columnNames});

    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
            .addGap(12, 12, 12)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
            .addGap(12, 12, 12)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(combo_databases, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
            .addGap(6, 6, 6)
            .addComponent(textField_username, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
            .addGap(6, 6, 6)
            .addComponent(textfield_password, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)))
        .addGap(18, 18, 18)
        .addComponent(jLabel7)
        .addGap(8, 8, 8)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
          .addComponent(jLabel6)
          .addComponent(combo_tables, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(3, 3, 3)
        .addComponent(checkbox_append)
        .addGap(2, 2, 2)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
          .addComponent(textfield_tablename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel5))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(checkbox_columnNames)
        .addGap(12, 12, 12)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(button_OK)
          .addComponent(buttonCancel))
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
      .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private void button_OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_OKActionPerformed
      export = true;
      database = String.valueOf(combo_databases.getSelectedItem()).trim();
      username = textField_username.getText().trim();
      password = textfield_password.getText().trim();
      if(combo_tables.getSelectedIndex() == 0){
        tablename = textfield_tablename.getText().trim();
      } else {
        tablename = (String) combo_tables.getSelectedItem();
      }
      createColumnNames = checkbox_columnNames.isSelected();
      append = checkbox_append.isSelected();
      dispose();
}//GEN-LAST:event_button_OKActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
      dispose();
      MainForm.glassPane.deactivate();
}//GEN-LAST:event_buttonCancelActionPerformed

    private void combo_databasesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_databasesActionPerformed
      try {
        getTables((String) combo_databases.getSelectedItem(), true);
      } catch (SQLException ex) {
        MainForm.myLog.log(Level.SEVERE, null, ex);
      }
    }//GEN-LAST:event_combo_databasesActionPerformed

    private void combo_tablesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_tablesActionPerformed
      textfield_tablename.setEnabled(combo_tables.getSelectedIndex() == 0);
      checkbox_append.setEnabled(combo_tables.getSelectedIndex() != 0);
    }//GEN-LAST:event_combo_tablesActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonCancel;
  private javax.swing.JButton button_OK;
  private javax.swing.JCheckBox checkbox_append;
  private javax.swing.JCheckBox checkbox_columnNames;
  private javax.swing.JComboBox combo_databases;
  public javax.swing.JComboBox combo_tables;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JTextField textField_username;
  private javax.swing.JTextField textfield_password;
  private javax.swing.JTextField textfield_tablename;
  // End of variables declaration//GEN-END:variables

  private void getDbs() throws SQLException {
    String sql = "SHOW DATABASES";
    MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
        MainForm.options.toString(DmOptions.DATABASE),
        MainForm.options.toString(DmOptions.DB_USER),
        MainForm.options.toString(DmOptions.DB_PASSWORD));
    Statement stmt = MyDBCConnection.myConnection.createStatement();
    ResultSet rs = stmt.executeQuery(sql);
    Vector<String> dbs = new Vector<String>();
    while (rs.next()) {
      String db = rs.getString(1);
      if (!db.equals("information_schema") && !db.equals("mysql")) {
        dbs.add(db);
      }
    }
    databases = new DefaultComboBoxModel(dbs);
    getTables(dbs.get(0), false);
  }

  private void getTables(String database, boolean updateCombo) throws SQLException {
    String sql = "SHOW TABLES FROM " + database;
    MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
        MainForm.options.toString(DmOptions.DATABASE),
        MainForm.options.toString(DmOptions.DB_USER),
        MainForm.options.toString(DmOptions.DB_PASSWORD));
    Statement stmt = MyDBCConnection.myConnection.createStatement();
    ResultSet rs = stmt.executeQuery(sql);
    Vector<String> tbls = new Vector<String>();
    tbls.add(" (New table) ");
    while (rs.next()) {
      String table = rs.getString(1);
      tbls.add(table);
    }
    tables = new DefaultComboBoxModel(tbls);
    if (updateCombo) {
      combo_tables.setModel(tables);
    }
  }

  private String getTableName(){
    return m.getOpenedFile().toLowerCase().replaceAll("[\\. -]", "_");
  }
}
