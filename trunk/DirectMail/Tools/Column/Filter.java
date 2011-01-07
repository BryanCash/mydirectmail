/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DirectMail.Tools.Column;

/**
 *
 * @author ssoldatos
 */
public class Filter {

    private int column;
    private int type;
    private String text;
    private boolean caseSensitive;

   public Filter(int column, int type, String text, boolean caseSensitive) {
      this.column = column;
      this.type = type;
      this.text = text;
      this.caseSensitive = caseSensitive;
    }

  /**
   * @return the column
   */
  public int getColumn() {
    return column;
  }

  /**
   * @param column the column to set
   */
  public void setColumn(int column) {
    this.column = column;
  }

  /**
   * @return the type
   */
  public int getType() {
    return type;
  }

  /**
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * @return the caseSensitive
   */
  public boolean isCaseSensitive() {
    return caseSensitive;
  }
  }