/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Help.Components;

import DirectMail.Main.MainForm;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

/**
 *
 * @author ssoldatos
 */
public class myTableColumnListener implements TableColumnModelListener {

  private JTable table;
  private MainForm m;

  public myTableColumnListener(MainForm m, JTable table) {
    this.table = table;
    this.m = m;
  }

  @Override
  public void columnAdded(TableColumnModelEvent e) {
  }

  @Override
  public void columnRemoved(TableColumnModelEvent e) {
  }

  @Override
  public void columnMoved(TableColumnModelEvent e) {
    int fromIndex = e.getFromIndex();
    int toIndex = e.getToIndex();
    m.getMoveColumnFrom().add(fromIndex);
    m.getMoveColumnTo().add(toIndex);
  }

  @Override
  public void columnMarginChanged(ChangeEvent e) {
  }

  @Override
  public void columnSelectionChanged(ListSelectionEvent e) {
  }
}
