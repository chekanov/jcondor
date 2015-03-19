/* 
VLSolutions VLJTable : an enhanced JTable for Swing Applications
Copyright (C) 2005 VLSolutions http://www.vlsolutions.com

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License version 2.1 as published by the Free Software Foundation.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/ 

package com.vlsolutions.swing.table;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/** A simple pop-up added on the table, and triggered by key input.
 *
 *<p> Allows fast positionning on the table.
 *<p> Can be enabled or disabled from the VLJTable (with the popUpSelectorActive property)
 *
 *
 * @author Lilian Chamontin, VLSolutions
 */
public class PopUpSelector extends JPopupMenu implements KeyListener, DocumentListener , FocusListener, ActionListener {
  
  /** The input field */
  protected JTextField textField = new JTextField(10);
  
  /** the searched column */
  protected int col;
  
  /** the label for the column */
  protected JLabel columnLabel = new JLabel();
  
  /** the target jtable */
  protected VLJTable table;
  
  /** search can be case sensitive or not */
  protected boolean isCaseSensitive = false;
  
  /** the container  */
  protected JPanel panel = new JPanel(new FlowLayout());
  
  /** Creates a new pop up menu for a given table */
  public PopUpSelector(VLJTable table){
    this.col = col;
    this.table = table;
    textField.setOpaque(false);

    textField.addKeyListener(this);
    textField.addFocusListener(this);
    textField.addActionListener(this);
    textField.getDocument().addDocumentListener(this);

    panel.add(columnLabel);
    panel.add(textField);
    panel.setBackground(getBackground().brighter()); // make it more visible
    add(panel);
    setBorderPainted(true);
    setInvoker(table);
    pack();
    
  }

  
  /** Creates a new pop up menu for a given table and column */
  public PopUpSelector(VLJTable table, int col){
    this(table);
    setCol(col);    
  }

  /** sets the column this pop up is for */
  public void setCol(int col){
    this.col = col;
    columnLabel.setText(table.getModel().getColumnName(col));
  }

  /** process key event */
  public void keyPressed(KeyEvent e){
    if (e.getKeyCode() == KeyEvent.VK_TAB 
        || e.getKeyCode() == KeyEvent.VK_ESCAPE){
      returnToTable();
    }
  }
  
  /** process textfield action */
  public void actionPerformed(ActionEvent e){
    returnToTable();
  }

  /** close this popup */
  public void returnToTable(){
    setVisible(false);
    table.requestFocus(); 
  }

  /** implementation side effect : does nothing */
  public void keyTyped(KeyEvent e){
  }

  /** process key released to close the pop up*/
  public void keyReleased(KeyEvent e){
    if (e.getKeyCode() == KeyEvent.VK_UP
        || e.getKeyCode() == KeyEvent.VK_DOWN
        || e.getKeyCode() == KeyEvent.VK_ENTER){
      returnToTable();
    }
  }

  /** implementation side effect : does nothing */
  public void focusGained(FocusEvent e){
  }

  /** closes the pop up on focus lost */
  public void focusLost(FocusEvent e){
    setVisible(false);
  }

  /** process text field document update : triggers a table positionning */
  public void insertUpdate(DocumentEvent e){
    updateSelection(e.getDocument());
  }
  /** process text field document update : triggers a table positionning */
  public void removeUpdate(DocumentEvent e){
    updateSelection(e.getDocument());
  }
  /** process text field document update : triggers a table positionning */
  public void changedUpdate(DocumentEvent e){
    updateSelection(e.getDocument());
  }
  
  private void updateSelection(Document d){
    try {
      table.selectFirstRowLike(col, d.getText(0,d.getLength()), isCaseSensitive);
    } catch (Exception ignore) {
    }
  }
  
  /** update the case-sensitive property */
  public void setCaseSensitive(boolean caseSensitive){
    this.isCaseSensitive = caseSensitive;
  }
  
  /** returns true if search is case sensitive */
  public boolean isCaseSensitive(){
    return this.isCaseSensitive;
  }

  /** set the content of the text field*/
  public void setText(String text){
    textField.setText(text);
  }
  
  /** returns the content of the text field*/
  public String getText(){
    return textField.getText();
  }
  
  /** shows the popup on top of the table */
  public void popUp(){
    Insets i = getInsets();
    int h = panel.getPreferredSize().height + i.top + i.bottom;
    // we get the height from the contained panel as getHeight() returns 0 until the
    // pop up is displayed
    if (table.getParent() instanceof JViewport) {      
    // for JViewport usage (standard), we have to be located relatively to the viewport 
      show(table.getParent(), 0, -h); 
    } else {
      show(table, 0, -h); 
    }
    textField.requestFocus();    
  }

}

