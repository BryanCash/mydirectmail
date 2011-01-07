/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Help.Components;

import DirectMail.Help.Common;
import DirectMail.Main.MainForm;
import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author ssoldatos
 */
public class MyTrayIcon {

  private MainForm m;
  private TrayIcon myTrayIcon;
  private MenuItem exitItem;
  private MenuItem maximizeItem;
  private MenuItem minimizeItem;
  private SystemTray tray;
  private String trayIconMessage;
  private CheckboxMenuItem alwaysOnTopItem;

  public MyTrayIcon(MainForm m) throws AWTException {
    this.m = m;
    init();
    show();
  }

  public void showInfoMessage(String title, String message) {
    myTrayIcon.displayMessage(title,
        message,
        TrayIcon.MessageType.INFO);
  }

  public void showWarningMessage(String title, String message) {
    myTrayIcon.displayMessage(title,
        message,
        TrayIcon.MessageType.WARNING);
  }

  public void showErrorMessage(String title, String message) {
    myTrayIcon.displayMessage(title,
        message,
        TrayIcon.MessageType.ERROR);
  }

  public void showMessage(String title, String message) {
    myTrayIcon.displayMessage(title,
        message,
        TrayIcon.MessageType.NONE);
  }

  public void hide() {
    tray.remove(myTrayIcon);
  }

  public void show() {
    if (SystemTray.isSupported()) {

      MouseListener mouseListener = new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent e) {
          if (e.getButton() == MouseEvent.BUTTON1) {
//	    if (m.t.isAlive()) {
//	     trayIconMessage = "DirectMail:" +  m.progressBar.getString();
//	    } else {
//	      trayIconMessage = "DirectMail: Application is idle";
//	    }
          } else {

            if (!m.isVisible()) {
              maximizeItem.setEnabled(true);
              minimizeItem.setEnabled(false);
            } else {
              maximizeItem.setEnabled(false);
              minimizeItem.setEnabled(true);
            }
          }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
      };

      ActionListener actionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          m.setVisible(true);
          m.setExtendedState(JFrame.NORMAL);
        }
      };

      myTrayIcon.addActionListener(actionListener);
      myTrayIcon.addMouseListener(mouseListener);


    }
  }

  private void init() throws AWTException {
    tray = SystemTray.getSystemTray();
    ImageIcon image = new javax.swing.ImageIcon(getClass().getResource("/DirectMail/Images/directMail.png"));
    Image icon = image.getImage();

    ActionListener exitListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          Common.closeProgram(m);
        } catch (IOException ex) {
          MainForm.myLog.log(Level.WARNING, null, ex);
        }
      }
    };
    ActionListener maximizeListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        m.setVisible(true);
        m.setExtendedState(JFrame.NORMAL);
      }
    };
    ActionListener minimizeListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        m.setVisible(false);
        m.setExtendedState(JFrame.ICONIFIED);
      }
    };

    ItemListener alwaysOnTopListener = new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        if (alwaysOnTopItem.getState()) {
          m.setAlwaysOnTop(true);
        } else {
          m.setAlwaysOnTop(false);
        }
      }

      public void actionPerformed(ActionEvent e) {
      }
    };

    PopupMenu popup = new PopupMenu();
    exitItem = new MenuItem("Exit");
    exitItem.addActionListener(exitListener);
    maximizeItem = new MenuItem("Maximize");
    maximizeItem.addActionListener(maximizeListener);
    minimizeItem = new MenuItem("Minimize");
    minimizeItem.addActionListener(minimizeListener);
    alwaysOnTopItem = new CheckboxMenuItem("AlwaysOnTop", false);
    alwaysOnTopItem.addItemListener(alwaysOnTopListener);

    popup.add(alwaysOnTopItem);
    popup.add(maximizeItem);
    popup.add(minimizeItem);
    popup.add(exitItem);
    myTrayIcon = new TrayIcon(icon, "Direct Mail", popup);
    myTrayIcon.setImageAutoSize(true);
    tray.add(myTrayIcon);
  }
}
