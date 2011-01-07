/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Help.Components;

import DirectMail.Main.MainForm;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

/**
 *
 * @author ssoldatos
 */
public class InfoThread implements Runnable {

  private double freeMemory;
  private double totalMemory;
  private double maxMemory;
  JLabel totalLabel = new JLabel();
  JLabel usedLabel = new JLabel();
  private boolean isMemoryLowShown = false;

  public InfoThread() {
    
  }

  @Override
  public void run() {
    Timer t = new Timer(1000, new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        updateIndicators();
      }
    });

    t.start();

  }

  private void updateIndicators() {
    maxMemory = (double) (Runtime.getRuntime().maxMemory() / (1024 * 1024));
    totalMemory = (double) (Runtime.getRuntime().totalMemory() / (1024 * 1024));
    freeMemory = (double) (Runtime.getRuntime().freeMemory() / (1024 * 1024));


    MainForm.tf_maxMemory.setText(String.valueOf(maxMemory) + " MB");
    MainForm.tf_totalMemory.setText(String.valueOf(totalMemory) + " MB");
    MainForm.tf_usedMemory.setText(String.valueOf(totalMemory - freeMemory) + " MB");

    int total = (int) (totalMemory * 100 / maxMemory);
    int used = (int) ((totalMemory - freeMemory) * 100 / maxMemory);

    //int total = (int) totalMemory;
    //int used = (int) ((totalMemory - freeMemory) * 100 / totalMemory);

    MainForm.layeredPane.removeAll();
    totalLabel.setSize(total, 20);
    totalLabel.setBackground(Color.YELLOW);
    totalLabel.setOpaque(true);
    totalLabel.setBorder(new LineBorder(Color.black));
    usedLabel.setSize(used, 20);
    usedLabel.setBackground(Color.RED);
    usedLabel.setOpaque(true);
    usedLabel.setBorder(new LineBorder(Color.black));
    //m.layeredPane.setLayout(new OverlayLayout(m.layeredPane));
    MainForm.layeredPane.add(totalLabel, new Integer(1), 1);
    MainForm.layeredPane.add(usedLabel, new Integer(2), 1);
    MainForm.layeredPane.validate();

    if ((maxMemory == totalMemory) && !isMemoryLowShown) {
      System.out.println("aa");
      MainForm.trayIcon.showWarningMessage("Warning!!!",
          "Out of memory!!!!\nIncrease JVM Heap Space");
      isMemoryLowShown = true;
    }
  }
}
