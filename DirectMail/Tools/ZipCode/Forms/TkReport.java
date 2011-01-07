/*
 * TkReport.java
 *
 * Created on 1 ��������� 2007, 2:24 ��
 */
package DirectMail.Tools.ZipCode.Forms;

import DirectMail.Help.Components.Errors;
import DirectMail.Help.Functions.DmFunctions;
import DirectMail.Main.MainForm;
import DirectMail.Options.DmOptions;
import DirectMail.Tools.File.PFile;
import soldatos.functions.StringFunctions;
import soldatos.connection.MyDBCConnection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 *
 * @author  ssoldatos
 */
public class TkReport extends javax.swing.JFrame implements Runnable {

  int env[] = new int[6];
  /**
   * The {@link BufferedReader} that reads the {@link File}
   */
  BufferedReader in;
  /**
   * The {@link PrintWriter} that writes to the output {@link File}
   */
  PrintWriter output;
  /**
   * The {@link Connection} to the MySQL Database
   */
  private MyDBCConnection mdbc;
  /**
   * The {@link Statement} used for the queries
   */
  private Statement stmt;
  /**
   * The number of fields in each line
   */
  private int fields;
  /**
   * The Batch used for sending all the insert queries at once
   */
  private boolean batch;
  /**
   * The reports filename
   */
  private String filename;
  long start, end;
  private MainForm m;

  /** Creates new form TkReport
   * @param m
   * @throws SQLException
   */
  public TkReport(MainForm m) throws SQLException {
    this.m = m;
    //create the bufferred reader
    in = DmFunctions.createBufferedReader(m);
    //m.setTextAreaText(null);


   MyDBCConnection.connect(MainForm.options.toString(DmOptions.HOST),
        MainForm.options.toString(DmOptions.DATABASE),
        MainForm.options.toString(DmOptions.DB_USER),
        MainForm.options.toString(DmOptions.DB_PASSWORD));
    stmt = MyDBCConnection.myConnection.createStatement();

  }

  @Override
  public void run() {
    try {
      report();
    } catch (Exception ex) {
      MainForm.myLog.log(Level.WARNING, null, ex);
    }
  }

  public void report() throws SQLException, IOException {
    for (int i = 0; i < env.length; i++) {
      env[i] = 0;
    }


    start = System.currentTimeMillis();

    m.setOutput("Creating the table");
    //Drop the table if exists
    dropTmpTables();
    //Create the new empty table
    createTmpTables();
    //Read the file and insert the rows
    fillTmpTable();
    m.appendOutput("\nDB Updated!!!");
    //Fill numoftk table
    m.appendOutput("\nRunning Report Query!!!");
    runQuery();
    m.appendOutput("\nQuery Run!!!");
    saveReport();
    m.appendOutput("\nReport generated and saved");
    m.init(true);
    //dropTmpTables();

    end = System.currentTimeMillis();
    m.appendOutput("\nExecution time : " + DmFunctions.execTime(start, end));
    initComponents();
    setVisible(true);
    MainForm.trayIcon.showInfoMessage("TK Report",
        "TK Report is saved\n" +
        "Execution time : " + DmFunctions.execTime(start, end));
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    jButton1 = new javax.swing.JButton();
    jPanel2 = new javax.swing.JPanel();
    jLabel2 = new javax.swing.JLabel();
    jLabel3 = new javax.swing.JLabel();
    jLabel4 = new javax.swing.JLabel();
    jLabel5 = new javax.swing.JLabel();
    jLabel6 = new javax.swing.JLabel();
    jLabel7 = new javax.swing.JLabel();
    jLabel9 = new javax.swing.JLabel();
    jPanel3 = new javax.swing.JPanel();
    jTextField1 = new javax.swing.JTextField();
    jTextField2 = new javax.swing.JTextField();
    jTextField3 = new javax.swing.JTextField();
    jTextField4 = new javax.swing.JTextField();
    jTextField5 = new javax.swing.JTextField();
    jTextField11 = new javax.swing.JTextField();
    jTextField13 = new javax.swing.JTextField();
    jPanel4 = new javax.swing.JPanel();
    jTextField12 = new javax.swing.JTextField();
    jTextField10 = new javax.swing.JTextField();
    jTextField9 = new javax.swing.JTextField();
    jTextField8 = new javax.swing.JTextField();
    jTextField7 = new javax.swing.JTextField();
    jTextField6 = new javax.swing.JTextField();
    jTextField14 = new javax.swing.JTextField();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14));
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText("TK Report");

    jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    jButton1.setText("Close");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Packs", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel2.setText("Packs with 1 envelope :");

    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel3.setText("Packs with 2 envelopes :");

    jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel4.setText("Packs with 3 envelopes :");

    jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel5.setText("Packs with 4 envelopes :");

    jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel6.setText("Packs with 5 envelopes :");

    jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel7.setText(" Packs with less than 4 envelopes :");

    jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    jLabel9.setText("Total Packs with less than 5 envelopes :");

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jLabel7)
          .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
              .addGap(73, 73, 73)
              .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addComponent(jLabel6)
                .addComponent(jLabel5)
                .addComponent(jLabel4)
                .addComponent(jLabel3)
                .addComponent(jLabel2)))
            .addComponent(jLabel9)))
        .addContainerGap(13, Short.MAX_VALUE))
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel2Layout.createSequentialGroup()
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel3)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel4)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel5)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel6)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
        .addComponent(jLabel7)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel9)
        .addContainerGap())
    );

    jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Quantity", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

    jTextField1.setEditable(false);
    jTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField1.setText(soldatos.functions.StringFunctions.padLeft(""+env[1],12," "));
    jTextField1.setBorder(null);
    jTextField1.setOpaque(false);

    jTextField2.setEditable(false);
    jTextField2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField2.setText(""+env[2]);
    jTextField2.setBorder(null);
    jTextField2.setOpaque(false);

    jTextField3.setEditable(false);
    jTextField3.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField3.setText(""+env[3]);
    jTextField3.setBorder(null);
    jTextField3.setOpaque(false);

    jTextField4.setEditable(false);
    jTextField4.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField4.setText(""+env[4]);
    jTextField4.setBorder(null);
    jTextField4.setOpaque(false);

    jTextField5.setEditable(false);
    jTextField5.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField5.setText(""+env[5]);
    jTextField5.setBorder(null);
    jTextField5.setOpaque(false);

    jTextField11.setEditable(false);
    jTextField11.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField11.setText(""+(env[1]+env[2]+env[3]));
    jTextField11.setBorder(null);
    jTextField11.setMinimumSize(new java.awt.Dimension(72, 20));
    jTextField11.setOpaque(false);

    jTextField13.setEditable(false);
    jTextField13.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField13.setText(""+(env[1]+env[2]+env[3]+env[4]));
    jTextField13.setBorder(null);
    jTextField13.setMinimumSize(new java.awt.Dimension(72, 20));
    jTextField13.setOpaque(false);

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jTextField13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jTextField11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jTextField5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField1, jTextField2, jTextField3, jTextField4, jTextField5});

    jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField11, jTextField13});

    jPanel3Layout.setVerticalGroup(
      jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel3Layout.createSequentialGroup()
        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
        .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Perc %", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

    jTextField12.setEditable(false);
    jTextField12.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField12.setText(""+(computePerc(1)+computePerc(2)+computePerc(3)));
    jTextField12.setBorder(null);
    jTextField12.setMinimumSize(new java.awt.Dimension(72, 20));
    jTextField12.setOpaque(false);

    jTextField10.setEditable(false);
    jTextField10.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField10.setText(""+computePerc(5));
    jTextField10.setBorder(null);
    jTextField10.setOpaque(false);

    jTextField9.setEditable(false);
    jTextField9.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField9.setText(""+computePerc(4));
    jTextField9.setBorder(null);
    jTextField9.setOpaque(false);

    jTextField8.setEditable(false);
    jTextField8.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField8.setText(""+computePerc(3));
    jTextField8.setBorder(null);
    jTextField8.setOpaque(false);

    jTextField7.setEditable(false);
    jTextField7.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField7.setText(""+computePerc(2));
    jTextField7.setBorder(null);
    jTextField7.setOpaque(false);

    jTextField6.setEditable(false);
    jTextField6.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField6.setText(soldatos.functions.StringFunctions.padLeft(""+computePerc(1),15," "));
    jTextField6.setBorder(null);
    jTextField6.setOpaque(false);

    jTextField14.setEditable(false);
    jTextField14.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    jTextField14.setText(""+(computePerc(1)+computePerc(2)+computePerc(3)+computePerc(4)));
    jTextField14.setBorder(null);
    jTextField14.setMinimumSize(new java.awt.Dimension(72, 20));
    jTextField14.setOpaque(false);

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jTextField14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jTextField12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jTextField10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jTextField9, javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jTextField8, javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jTextField7, javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(jTextField6, javax.swing.GroupLayout.Alignment.TRAILING))
        .addContainerGap())
    );

    jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField12, jTextField14});

    jPanel4Layout.setVerticalGroup(
      jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel4Layout.createSequentialGroup()
        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
              .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
              .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
          .addGroup(layout.createSequentialGroup()
            .addGap(245, 245, 245)
            .addComponent(jButton1)))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jLabel8)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButton1)
        .addGap(31, 31, 31))
    );

    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((screenSize.width-570)/2, (screenSize.height-396)/2, 570, 396);
  }// </editor-fold>//GEN-END:initComponents

  private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    dispose();
  }//GEN-LAST:event_jButton1ActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton jButton1;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JTextField jTextField1;
  private javax.swing.JTextField jTextField10;
  private javax.swing.JTextField jTextField11;
  private javax.swing.JTextField jTextField12;
  private javax.swing.JTextField jTextField13;
  private javax.swing.JTextField jTextField14;
  private javax.swing.JTextField jTextField2;
  private javax.swing.JTextField jTextField3;
  private javax.swing.JTextField jTextField4;
  private javax.swing.JTextField jTextField5;
  private javax.swing.JTextField jTextField6;
  private javax.swing.JTextField jTextField7;
  private javax.swing.JTextField jTextField8;
  private javax.swing.JTextField jTextField9;
  // End of variables declaration//GEN-END:variables

  private double computePerc(int id) {
    double perc = 0.0;

    perc = env[id] * 100.0 / m.getCustomers();
    //System.out.println(perc);
    // this is to keep only 2 dec digits
    perc = (int) (perc * 100) / 100.0;
    return perc;
  }

  private void dropTmpTables() {
    String sql = "DROP TABLE IF EXISTS  "+ DmOptions.TMP_DB +"."
        + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmptkreport";
    //System.out.println(sql);
    try {
      stmt.executeUpdate(sql);
    } catch (SQLException ex) {
      Errors.SQLError(ex.getMessage());
      ex.printStackTrace();
    }
  }

  private void createTmpTables() {
    String sql = "";
    //create numoftk table
    sql = "CREATE TABLE IF NOT EXISTS  "+ DmOptions.TMP_DB +"." +
        MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmptkreport(zip VARCHAR(10))";
    try {
      stmt.executeUpdate(sql);
    } catch (SQLException ex) {
      Errors.SQLError(ex.getMessage());
      ex.printStackTrace();
    }
  }

  private void fillTmpTable() {
    String str;
    int lines = 0;
    String strArr[];
    String sql, curTK = "";
    try {
      while ((str = in.readLine()) != null) {
        lines++;

        m.appendToCurrentOutput("Lines read: " + lines);
        m.updateProgress(lines * 100 / m.getCustomers());
        if (lines % 50 == 0) {
          DmFunctions.calcRemainingTime(m, start, System.currentTimeMillis(), lines, m.getCustomers());
        }
        strArr = str.split("" + m.getDelimeter(), -1);
        for (int i = 0; i < m.getFields(); i++) {
          if (i == m.getTkField()) {
            curTK = DmFunctions.prepareTK(strArr[i]);
          }
        }
        sql = "INSERT INTO  "+ DmOptions.TMP_DB +"."
            + MainForm.options.toString(DmOptions.DB_PREFIX) + "_tmptkreport VALUES ('" + curTK + "')";

        stmt.addBatch(sql);

      }
      in.close();
      m.appendOutput("\nDB is being updating");
      stmt.executeBatch();
    } catch (IOException ex) {
      Errors.IOError(ex.getMessage());
      ex.printStackTrace();
    } catch (SQLException ex) {
      Errors.SQLError(ex.getMessage());
      ex.printStackTrace();
    }



  }

  private void runQuery() throws SQLException {
    String sql = "";
    ResultSet rs;

    sql = "SELECT COUNT(zip) as numoftk FROM  " + DmOptions.TMP_DB +"."
        + MainForm.options.toString(DmOptions.DB_PREFIX) +
        "_tmptkreport GROUP BY zip HAVING COUNT(zip) < 6";

    rs = stmt.executeQuery(sql);

    while (rs.next()) {
      //System.out.println(rs.getInt(1));
      env[rs.getInt(1)] += rs.getInt(1);
    }

  }

  private void saveReport() throws IOException {
    PrintWriter out;
    String curDir, openedFile;

    curDir = m.getCurrentDirectory();
    openedFile = m.getOpenedFile();

    filename = curDir + "//TK_Report_" + openedFile.substring(0, openedFile.length() - 4) + ".txt";


    out = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
    out.println("������� �� ��� ������ " + PFile.OpenedFileName);
    out.println("");
    for (int i = 0; i < env.length; i++) {
      if (i == 0) {
      } else if (i == 1) {
        out.println("������ �� 1 �������         : " + StringFunctions.padLeft("" + env[1], 6, " ") + StringFunctions.padLeft("" + computePerc(1), 7, " ") + "%");
      } else if (i == 2) {
        out.println("������ �� 2 ���������       : " + StringFunctions.padLeft("" + env[2], 6, " ") + StringFunctions.padLeft("" + computePerc(2), 7, " ") + "%");
      } else if (i == 3) {
        out.println("������ �� 3 ���������       : " + StringFunctions.padLeft("" + env[3], 6, " ") + StringFunctions.padLeft("" + computePerc(3), 7, " ") + "%");
      } else if (i == 4) {
        out.println("������ �� 4 ���������       : " + StringFunctions.padLeft("" + env[4], 6, " ") + StringFunctions.padLeft("" + computePerc(4), 7, " ") + "%");
      } else if (i == 5) {
        out.println("������ �� 5 ���������       : " + StringFunctions.padLeft("" + env[5], 6, " ") + StringFunctions.padLeft("" + computePerc(5), 7, " ") + "%");
      }
    }
    out.println("");
    out.println("");
    out.println("������ ������� �� ���������� ��� 4 ��������� : " + StringFunctions.padLeft("" + (env[1] + env[2] + env[3]), 6, " ") + StringFunctions.padLeft("" + (computePerc(1) + computePerc(2) + computePerc(3)), 7, " ") + "%");
    out.println("������ ������� �� ���������� ��� 5 ��������� : " + StringFunctions.padLeft("" + (env[1] + env[2] + env[3] + env[4]), 6, " ") + StringFunctions.padLeft("" + (computePerc(1) + computePerc(2) + computePerc(3) + computePerc(4)), 7, " ") + "%");

    out.close();


  }
}

