/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Help.Components;

import DirectMail.Main.MainForm;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author ssoldatos
 */
public class MyTableHeaderRenderer implements TableCellRenderer {

  private MainForm m;
  private String title;
  private int field;
  private HeaderJPanel panel;
  

  public MyTableHeaderRenderer(MainForm m, String title, int field) {
    this.m = m;
    this.title = title;
    this.field = field;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table,
      Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    panel = new HeaderJPanel(m,title,field);
    
    
    return panel;
  }
}
