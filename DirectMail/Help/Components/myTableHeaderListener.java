/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DirectMail.Help.Components;

import DirectMail.Help.Forms.Tooltip;
import DirectMail.Help.Components.Errors;
import DirectMail.Help.Functions.ComputeMaxLength;
import DirectMail.Help.Common;
import DirectMail.Main.MainForm;
import DirectMail.Tools.Column.DeleteTheFields;
import java.awt.Cursor;
import soldatos.functions.SwingFunctions;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import soldatos.functions.ArrayFunctions;

/**
 *
 * @author ssoldatos
 */
public class myTableHeaderListener extends MouseAdapter {

  private JTable table;
  private MainForm m;
  private JTableHeader header;
  private Tooltip ttip;

  public myTableHeaderListener(MainForm m, JTable table) {
    this.m = m;
    this.table = table;
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    super.mouseDragged(e);
    System.out.println("dragg");
  }

  @Override
  public void mouseClicked(MouseEvent evt) {
    if (evt.getClickCount() == 2) {
      Common.adjustSampleTable(m, true);
    } else if (evt.getClickCount() == 1) {
      TableColumnModel colModel = table.getColumnModel();
      final int index = colModel.getColumnIndexAtX(evt.getX());
      int colPosition = getColumnPosition(table, index);
      if (evt.getX() - colPosition < 31 && evt.getX() - colPosition > 11) {
        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            DeleteTheFields dtf = new DeleteTheFields(index, m);
            Thread t = new Thread(dtf);
            t.start();

          }
        });
        return;
      }

      header = table.getTableHeader();
      if (ttip != null) {
        ttip.dispose();
      }
      ttip = new Tooltip();
      ttip.setLocation(evt.getXOnScreen() - 20, evt.getYOnScreen() - 20);
      ttip.labelMaxLength.setText("Computing max length...");
      //header.setToolTipText("Computing max length...");
      ComputeMaxLength cml = new ComputeMaxLength(m, index, header, ttip);
      Thread n = new Thread(cml);
      n.start();
    }
  }

  @Override
  public void mouseExited(MouseEvent evt) {
    m.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    header = table.getTableHeader();
    // header.setToolTipText(null);
    if (ttip != null) {
      ttip.dispose();
    }
  }

  @Override
  public void mouseReleased(MouseEvent evt) {
    m.setMoveColumnFrom(ArrayFunctions.removeDuplicate(m.getMoveColumnFrom()));
    m.setMoveColumnTo(ArrayFunctions.removeDuplicate(m.getMoveColumnTo()));
    if (m.getMoveColumnFrom().size() > 0) {
      int from = Integer.parseInt(String.valueOf(m.getMoveColumnFrom().get(0)));
      int to = Integer.parseInt(String.valueOf(m.getMoveColumnTo().get(m.getMoveColumnTo().size() - 1)));
      if (from != to) {
        if (MainForm.filters.size() > 0) {
          Errors.FiltersError();
          m.sampleTable.moveColumn(to, from);
        } else {
          m.MoveTableColumn(from, to);
        }
      }
    }
    m.getMoveColumnFrom().clear();
    m.getMoveColumnTo().clear();
  }

  private int getColumnPosition(JTable table, int index) {
    int position = 0;
    int tablePositionX = table.getX() + table.getInsets().left;
    position = tablePositionX;
    for (int i = 0; i < index; i++) {
      position += table.getColumnModel().getColumn(i).getWidth();
    }

    return position;
  }
}
