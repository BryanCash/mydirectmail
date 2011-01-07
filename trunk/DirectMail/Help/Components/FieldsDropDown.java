/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FieldsDropDown.java
 *
 * Created on 27 ��� 2009, 9:01:46 ��
 */
package DirectMail.Help.Components;

import DirectMail.Main.MainForm;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 *
 * @author ssoldatos
 */
public class FieldsDropDown extends JComboBox{
  private static final long serialVersionUID = 34525643L;
  private String[] fields;
  private ComboBoxModel model;

  public FieldsDropDown() {
    initComponents();
  }

  /** Creates new form FieldsDropDown
   * @param m
   */
  public FieldsDropDown(MainForm m) {
    super();
    fields = new String[m.getFields()];
    for (int i = 0; i < m.getFields(); i++) {
      fields[i] = String.valueOf(m.getColModel().getColumn(i).getHeaderValue());
    }
    model = new DefaultComboBoxModel(fields);
    initComponents();
  }

  FieldsDropDown(MainForm m, int field) {
    super();
    fields = new String[m.getFields()];
    for (int i = 0; i < m.getFields(); i++) {
      fields[i] = String.valueOf(m.getColModel().getColumn(i).getHeaderValue());
    }
    model = new DefaultComboBoxModel(fields);
    initComponents();
    setSelectedIndex(field);
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    setModel(model);
  }// </editor-fold>//GEN-END:initComponents
  // Variables declaration - do not modify//GEN-BEGIN:variables
  // End of variables declaration//GEN-END:variables
}