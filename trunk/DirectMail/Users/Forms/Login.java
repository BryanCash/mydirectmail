/*
 * Login.java
 *
 * Created on 4 ���������� 2008, 9:37 ��
 */
package DirectMail.Users.Forms;

import DirectMail.Help.Components.Errors;
import DirectMail.Main.MainForm;
import DirectMail.Help.Components.Users;
import DirectMail.Options.DmOptions;
import com.ice.jni.registry.NoSuchValueException;
import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;
import com.ice.jni.registry.RegistryValue;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.UnsupportedLookAndFeelException;
import soldatos.functions.EncodingFunctions;
import soldatos.functions.FileFunctions;

/**
 *
 * @author  ssoldatos
 */
public class Login extends javax.swing.JFrame {

  private static File fileToOpen;
  private Vector<Users> users = new Vector<Users>();
  private int tries = 2;
  private Users currentUser;
  private MouseEvent pressed;
  private Point location;
  private boolean dev = true;
  private FileReader in;
  private PrintWriter out;
  public MainForm m;
  public static boolean firstTime;

  /**
   * 
   * @throws java.lang.ClassNotFoundException
   * @throws java.lang.InstantiationException
   * @throws java.lang.IllegalAccessException
   * @throws javax.swing.UnsupportedLookAndFeelException
   * @throws java.io.IOException
   * @throws java.awt.AWTException
   */
  public Login() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException, AWTException {
    firstTime = true;
    java.net.URL imgURL = getClass().getResource("/DirectMail/Images/directMail.png");
    setIconImage(new ImageIcon(imgURL).getImage());
    MainForm.myLog = soldatos.messages.myLogger.createHtmlLogger("log", FileFunctions.getJarDir(this) + "/" + "direct_mail", 250000, true, 1);
    if (!dev) {
      initComponents();
      setLocationRelativeTo(null);
      loadUsers();
    } else {
      MainForm main = new MainForm(new Users("Spyros", "spyros", "admin"), fileToOpen, null);
    }
  }

  private void writeUser() throws IOException {

    out = new PrintWriter(new BufferedWriter(new FileWriter("users.dat")));
    out.println("admin" + "\t" + EncodingFunctions.base64Encode("admin") + "\t"
        + EncodingFunctions.base64Encode("adminadmin"));
    out.close();

  }

  private void loadUsers() {
//      users.addElement(new Users("Spyros", "spyros", "admin"));
//      users.addElement(new Users("GeorgeN", "georgen", "admin"));
//      users.addElement(new Users("GeorgeV", "georgev", "admin"));
//      users.addElement(new Users("GeorgeM", "georgem", "enveloper"));
//      users.addElement(new Users("Vasilis", "vasilis", "printer"));
//      users.addElement(new Users("admin", "admin", "admin"));
  }

  private boolean checkUser(String un, String pass) throws IOException {
    String line;
    String[] lineArr;

    in = new FileReader("users.dat");
    BufferedReader dis = new BufferedReader(in);
    while ((line = dis.readLine()) != null) {
      lineArr = line.split("\t", -1);
      if (un.equals(lineArr[0]) && EncodingFunctions.base64Encode(pass).equals(lineArr[1])) {
        if (EncodingFunctions.base64Encode("admin" + un).equals(lineArr[2])) {
          currentUser = new Users(un, pass, "admin");
        } else if (EncodingFunctions.base64Encode("enveloper" + un).equals(lineArr[2])) {
          currentUser = new Users(un, pass, "enveloper");
        } else if (EncodingFunctions.base64Encode("printer" + un).equals(lineArr[2])) {
          currentUser = new Users(un, pass, "printer");
        }
        return true;
      }
    }
    in.close();
    return false;
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    buttonLogin = new javax.swing.JButton();
    tf_username = new javax.swing.JTextField();
    buttonClose = new javax.swing.JButton();
    tf_password = new javax.swing.JPasswordField();
    jLabel1 = new javax.swing.JLabel();
    labelUsername = new javax.swing.JLabel();
    labelPassword = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("Direct Mail Log In");
    setUndecorated(true);
    addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        formMousePressed(evt);
      }
    });
    addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(java.awt.event.MouseEvent evt) {
        formMouseDragged(evt);
      }
    });

    jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

    buttonLogin.setText("Login");
    buttonLogin.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonLoginActionPerformed(evt);
      }
    });

    buttonClose.setText("Close");
    buttonClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        buttonCloseActionPerformed(evt);
      }
    });

    tf_password.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        tf_passwordKeyReleased(evt);
      }
    });

    jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14));
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText("Direct Mail Login");

    labelUsername.setLabelFor(tf_username);
    labelUsername.setText("Username :");

    labelPassword.setLabelFor(tf_password);
    labelPassword.setText("Password :");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(buttonLogin)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(buttonClose))
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
          .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
              .addComponent(labelPassword)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(tf_password))
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
              .addComponent(labelUsername)
              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(tf_username, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addGap(21, 21, 21)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelUsername)
          .addComponent(tf_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(labelPassword)
          .addComponent(tf_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(buttonLogin)
          .addComponent(buttonClose))
        .addContainerGap())
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
  private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloseActionPerformed
    System.exit(0);
  }//GEN-LAST:event_buttonCloseActionPerformed

  private void buttonLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLoginActionPerformed
    try {
      dispose();
      login();
    } catch (ClassNotFoundException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (UnsupportedLookAndFeelException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    } catch (AWTException ex) {
      MainForm.myLog.log(Level.SEVERE, null, ex);
    }



  }//GEN-LAST:event_buttonLoginActionPerformed

  private void tf_passwordKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_passwordKeyReleased
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
      try {
        dispose();
        login();
      } catch (Exception ex) {
        MainForm.myLog.log(Level.WARNING, null, ex);
      }
    }
  }//GEN-LAST:event_tf_passwordKeyReleased

  private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
    location = evt.getComponent().getLocation(location);
    int x = location.x - pressed.getX() + evt.getX();
    int y = location.y - pressed.getY() + evt.getY();

    evt.getComponent().setLocation(x, y);
  }//GEN-LAST:event_formMouseDragged

  private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
    pressed = evt;
  }//GEN-LAST:event_formMousePressed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
     String log = "Running main with " + args.length + "\r\n";
    if (args.length > 0) {
      if (new File(args[0]).isFile()) {
        fileToOpen = new File(args[0]);
      } else {
        log += "Running main with " + args[0] + "\r\n";
        File upd = new File(args[0] + "/lib/DmUpdater.jar");
        File dest = new File("./lib/DmUpdater.jar");
        try {
          log += "copy updater\n";
          FileFunctions.copyBinary(upd, dest);
        } catch (IOException ex) {
          log += "Could not copy updater\r\n";
        }
        log += "copied updater\r\n";
      }
    }
    java.awt.EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        try {
          new Login();
        } catch (Exception ex) {
          MainForm.myLog.log(Level.SEVERE, null, ex);
        }

      }
    });
  }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton buttonClose;
  private javax.swing.JButton buttonLogin;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JLabel labelPassword;
  private javax.swing.JLabel labelUsername;
  private javax.swing.JPasswordField tf_password;
  private javax.swing.JTextField tf_username;
  // End of variables declaration//GEN-END:variables

  private void login() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException, AWTException {

    String un = tf_username.getText().trim();
    char[] p = tf_password.getPassword();

    String pass = "";
    for (int i = 0; i < p.length; i++) {
      pass += p[i];
      p[i] = '\u0000';
    }
    pass = pass.trim();
    File f = new File("users.dat");
    if (!f.exists()) {
      writeUser();
    }
    if (checkUser(un, pass)) {

      m = new MainForm(currentUser, fileToOpen, null);
      dispose();

    } else {
//      if (tries > 0) {
//	Errors.customError("Wrong login information", "Username or password is invalid\nYou have " + tries + " more tries");
//	tf_username.setText("");
//	tf_password.setText("");
//	tries--;
//      } else {
      Errors.customError("Wrong login information", "Username or password is invalid\nExiting...");
      System.exit(0);
//      }
    }

  }
}
