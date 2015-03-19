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

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

/** This class is responsible for the management of filters (editing mainly)
 * on behalf of the VLTable.
 *
 * @author Lilian Chamontin, VLSolutions
 */
public class FilterColumnModel {
  
  protected int columns;  
  protected FilterCellEditor [] editors;
  protected TableModel model;
  
  public FilterColumnModel(TableModel model) {
    this.model = model;
    this.columns = model.getColumnCount();
    installDefaultFilters();
  }
  
  private void installDefaultFilters(){
    int cols = model.getColumnCount();
    editors = new FilterCellEditor[cols];
    for (int i=0; i < cols; i++){
      editors[i] = new DefaultFilterCellEditor();
    }
  }
  
  public void setFilterCellEditor(int column, FilterCellEditor editor){
    editors[column] = editor;
  }
  
  public FilterCellEditor getFilterCellEditor(int column){
    return editors[column];
  }
  
}
