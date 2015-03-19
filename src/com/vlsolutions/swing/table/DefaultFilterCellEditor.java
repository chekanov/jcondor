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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/** Default implementation of a Filter cell editor : works only with String content, as it uses
 * a JTextField as input.
 * 
 *
 * @author Lilian Chamontin, VLSolutions
 */
public class DefaultFilterCellEditor extends JTextField implements FilterCellEditor, DocumentListener {
  
  public DefaultFilterCellEditor() {
    getDocument().addDocumentListener(this);    
  }
  public void insertUpdate(DocumentEvent e){
    changed(e);
  }
  public void changedUpdate(DocumentEvent e){
    changed(e);
  }
  public void removeUpdate(DocumentEvent e){
    changed(e);
  }
  private void changed(DocumentEvent e){
     firePropertyChange(getFilterChangePropertyName(), null, getText());
  }
  
  public Object getValue(){
    return getText();
  }
  
  public void setValue(Object value){
     if (value == null){
       setText("");
     } else {
       setText(value.toString());
     }
  }

  public String getFilterChangePropertyName() {
    return "FilterContent";
  }

  
}