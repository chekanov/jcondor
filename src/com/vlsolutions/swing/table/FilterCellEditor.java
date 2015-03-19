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
import javax.swing.event.ChangeListener;

/** Interface implemented by JComponents that are used as editors for table filter headers.
 *
 * @author Lilian Chamontin, VLSolutions
 */
public interface FilterCellEditor {
  
  /** Updates the value displayed in the editor */
  public void setValue(Object value);
  
  /** Returns the value currently edited */
  public Object getValue();
  
  /** Returns the name of the property bound to a change in the filter content.
   *<p>
   * The VLJTable needs this information to add a PropertyChangeListener and listen
   * to the changes (dynamically, not just on focus lost) to reflect them in 
   * the filtered data.
   *
   * @see #addPropertyChangeListener
   * @see #removePropertyChangeListener
   */
  public String getFilterChangePropertyName();
  
  /** Registers a listener to track changes of the content of the filter, must use 
   * the getFilterChangePropertyName() as the name of the property.
   *<p>
   * This method is already part of the JComponent signature, and there is no need to
   * override it.
   *
   * @see #removePropertyChangeListener
   * @see #getFilterChangePropertyName
   */
  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
  
  /** Removes a property change listener.
   * @see #addPropertyChangeListener
   * @see #getFilterChangePropertyName
   */
  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
  
  
  
}
