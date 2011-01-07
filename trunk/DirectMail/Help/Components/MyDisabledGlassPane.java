/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DirectMail.Help.Components;

import DirectMail.Main.MainForm;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ssoldatos
 */
public class MyDisabledGlassPane extends soldatos.lookaandfeel.DisabledGlassPane implements KeyListener {
  MainForm m;
  public MyDisabledGlassPane(Color color_5, int i) {
    super(color_5, i);
  }

  public void setMainForm(MainForm m){
    this.m = m;
  }


  @Override
  public void keyPressed(KeyEvent e) {
    e.consume();
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyReleased(KeyEvent e) {
   
  }
}
