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

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/** The table cell renderer used to display sortable column headers.
 *
 * @author Lilian Chamontin, VLSolutions
 */
public class VLTableCellRenderer extends DefaultTableCellRenderer {
  protected VLJTable table;
  protected int col;
      
  public VLTableCellRenderer() {
  }
  
  VLTableCellRenderer(VLJTable table, int col){
    this.col = col;
    this.table = table;
  }
  
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    if (table != null) {
      JTableHeader header = table.getTableHeader();
      if (header != null) {
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());
        setHorizontalAlignment(JLabel.CENTER);
      }
    }
    
    setText( (value == null) ? "" : value.toString());
    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    return this;
  }
  
  public void paintComponent(Graphics g){
    super.paintComponent(g);
        
    int sort = table.getSortMode(col);
    if ( sort == FilterModel.SORT_NONE){
    } else{
      g.setColor(getBackground().darker());
      if (sort == FilterModel.SORT_ASCENDING) {
        g.drawImage(table.getAscendingSortImage(), getWidth()-16, getHeight()/2-8, null);
      } else {
        g.drawImage(table.getDescendingSortImage(), getWidth()-16, getHeight()/2-8, null);
      }
    }
  }
}
