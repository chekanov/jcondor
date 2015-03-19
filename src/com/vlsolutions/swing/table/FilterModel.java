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

import java.util.ArrayList;
import java.util.Date;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/** A filtering table model, to be used with VLJTable.
 *
 * The Sorting related content is based on the Java tutorial.
 *
 * @author Lilian Chamontin, VLSolutions
 */
public class FilterModel extends AbstractTableModel implements TableModelListener {

  // our sorting constants (3 modes : unsorted, sorted asc, sorted desc)
  /** sort mode : unsorted  */
  public static final int SORT_NONE = 0;
  /** ascending sort */
  public static final int SORT_ASCENDING = 1;
  /** descenging sort */
  public static final int SORT_DESCENDING = 2;
  
  
  protected int[] filterIndex = new int[0];
  protected VLJTableFilter [] filters ;
  protected int [] sortModes;
  protected int [] indexes;
  
  protected TableModel model;
  protected VLJTable table;
      
  /** Creates a filter model for a table, and with a given base model 
   */
  public FilterModel(VLJTable table, TableModel model){ 
    this.table = table;
    this.model = model;
    filters = new VLJTableFilter[model.getColumnCount()];
    sortModes = new int[model.getColumnCount()];
    rebuildIndex();
    model.addTableModelListener(this);
  }
  
  /**  returns the base (source) model used by this table model*/
  public TableModel getModel(){
    return model;
  }
  
  
  /** Removes all filters from the model (all data contained in the base model will be shown) */
  public void clearFilters(){
    int cols = model.getColumnCount();
    
    for (int i=0; i < cols; i++){
      if (filters[i] != null){
        filters[i].setFilter(null);
      }
    }
    rebuildIndex();
  }
  
  /** Returns the corresponding row in the base model */
  public int getSourceRow(int row){
    return filterIndex[indexes[row]];
  }
  
  /** Sets a filter value for a given column */
  public void setFilter(int col, Object value) {
    filters[col].setFilter(value);
    rebuildIndex();
  }
  
  /** install a new filtering implementation on a column */
  public void installFilter(int col, VLJTableFilter filter) {
    filters[col] = filter;
    rebuildIndex();
  }
  
  
  /** Returns the filtering implementation for a given column */
  public VLJTableFilter getFilter(int col) {
    return filters[col];
  }
  
  /** {@inheritDoc} */
  public int getColumnCount() {
    return model.getColumnCount();
  }
  /** {@inheritDoc} */
  public String getColumnName(int col){
    return model.getColumnName(col);
  }
  public Class getColumnClass(int col){
  /** {@inheritDoc} */
    return model.getColumnClass(col);
  }
  /** {@inheritDoc} */
  public int getRowCount(){
    return filterIndex.length;
  }
  /** {@inheritDoc} */
  public Object getValueAt(int row, int col){
    return model.getValueAt(filterIndex[indexes[row]], col);
  }
  /** {@inheritDoc} */
  public boolean isCellEditable(int row, int col){
    return model.isCellEditable(filterIndex[indexes[row]], col);
  }
  /** {@inheritDoc} */
  public void setValueAt(Object o, int row, int col){
    model.setValueAt(o, filterIndex[indexes[row]], col);
  }
  /** {@inheritDoc} */
  public void tableChanged(TableModelEvent e){

    boolean sortOrFilter = isSortedOrFiltered();
    synchronized(this){
      if (e.getFirstRow() == TableModelEvent.HEADER_ROW) { 
        // this is how we recognize a structure change
        filters = new VLJTableFilter[model.getColumnCount()];
        sortModes = new int[model.getColumnCount()];
        rebuildIndex();
//        if (table.isFilterHeaderVisible()){ 
//          table.installFilterHeader();
//        } else {
//          table.installHeader();
//        }
        fireTableStructureChanged();
      } else if (sortOrFilter){
        rebuildIndex();
      } else if (e.getLastRow() > indexes.length){
        indexes = new int[model.getRowCount()];
        filterIndex = new int[model.getRowCount()];
        for (int i = 0; i < indexes.length; i++) {
          indexes[i] = filterIndex[i] = i;
        }
        fireTableChanged(new TableModelEvent(this, e.getFirstRow(),
            e.getLastRow(), e.getColumn(), e.getType()));
      }
    }
  }
  
  //--------- contents below are borrowed from the java tutorial, and reworked when possible
  
  /** Modify visible indexes based on filtering */
  public void rebuildIndex(){
    boolean existFilter = false;
    for (int i = 0; i < model.getColumnCount(); i++) {
      if (filters[i] != null) {
        existFilter = true;
      }
    }
    
    if (existFilter){ // some filtering
      int rowCount = model.getRowCount();
      int colCount = model.getColumnCount();
      
      for (int j = 0; j < colCount; j++) {
        if (filters[j] != null) {
          filters[j].compile();
        }
      }
      
      ArrayList dataIndex = new ArrayList(model.getRowCount() / 2);
      for (int i = 0; i < rowCount; i++) {
        boolean reject = false;
        for (int j = 0; j < colCount && !reject; j++) {
          VLJTableFilter filter = filters[j];
          if (filter != null) {
            Object o = model.getValueAt(i, j);
            
            if (!filter.accept(model.getValueAt(i, j))) {
              reject = true;
            }
          }
        }
        if (!reject) {
          // keep the row as all filters have passed the test
          dataIndex.add(new Integer(i));
        }
      }
      // dataIndex contains filtered lines
      this.filterIndex = new int[dataIndex.size()];
      for (int i = 0; i < dataIndex.size(); i++) {
        filterIndex[i] = ( (Integer) dataIndex.get(i)).intValue();
      }
    } else { // no filters
      this.filterIndex = new int[model.getRowCount()];
      for (int i = 0; i < filterIndex.length; i++) {
        filterIndex[i] = i;
      }
    }
    
    reallocateIndexes();
    
    // now we can sort
    sort();
    fireTableDataChanged();
  }
  
  protected boolean isSortedOrFiltered(){
    if (!table.isSortEnabled() && !table.isFilteringEnabled()) return false;
    boolean rep = false;
    for (int i = 0; i < sortModes.length && !rep; i++) {
      rep = sortModes[i] != SORT_NONE;
    }
    for (int i = 0; i < filters.length && !rep; i++) {
      rep = filters[i] != null;
    }
    return rep;
  }
  
  /**     Return the sort mode for a given column.
   *  (between  SORT_NONE, SORT_ASCENDING, SORT_DESCENDING)
   */
  protected int getSortMode(int col) {
    return sortModes[col];
  }
  
  /**     Sets the sort mode for a given column.
   *  (between  SORT_NONE, SORT_ASCENDING, SORT_DESCENDING)
   */
  protected void setSortMode(int col, int mode) {
    sortModes[col] = mode;
    rebuildIndex();
  }

  private int compareRowsByColumn(int row1, int row2, int column) {
    TableModel data = model;
    Class type = getColumnClass(column);
    
    // Check for nulls.
    Object o1 = data.getValueAt(row1, column);
    Object o2 = data.getValueAt(row2, column);
    
    // If both values are null, return 0.
    if (o1 == null && o2 == null) {
      return 0;
    }  else if (o1 == null) {
      // Define null less than everything.
      return -1;
    }  else if (o2 == null) {
      return 1;
    }
    
    if (type.getSuperclass() == java.lang.Number.class) {
      Number n1 = (Number) o1;
      double d1 = n1.doubleValue();
      Number n2 = (Number)o2;
      double d2 = n2.doubleValue();
      
      if (d1 < d2) {
        return -1;
      } else if (d1 > d2) {
        return 1;
      } else {
        return 0;
      }
    }  else if (type == java.util.Date.class) {
      Date d1 = (Date)o1;
      long n1 = d1.getTime();
      Date d2 = (Date)o2;
      long n2 = d2.getTime();
      
      if (n1 < n2) {
        return -1;
      } else if (n1 > n2) {
        return 1;
      } else {
        return 0;
      }
    } else if (type == String.class) {
      String s1 = (String)o1;
      String s2    = (String)o2;
      int result = s1.compareTo(s2);
      
      if (result < 0) {
        return -1;
      } else if (result > 0) {
        return 1;
      } else {
        return 0;
      }
    } else if (type == Boolean.class) {
      Boolean bool1 = (Boolean)o1;
      boolean b1 = bool1.booleanValue();
      Boolean bool2 = (Boolean)o2;
      boolean b2 = bool2.booleanValue();
      if (b1 == b2) {
        return 0;
      } else if (b1) {
        // Define false < true.
        return 1;
      } else {
        return -1;
      }
    } else {
      String s1 = o1.toString();
      String s2 = o2.toString();
      int result = s1.compareTo(s2);
      if (result < 0) {
        return -1;
      } else if (result > 0) {
        return 1;
      } else {
        return 0;
      }
    }
  }
  
  private int compare(int row1, int row2) {
    for (int i = 0; i < sortModes.length; i++) {
      int sortMode = sortModes[i];
      if (sortMode != 0){
        int result = compareRowsByColumn(filterIndex[row1], filterIndex[row2], i);
        if (result != 0) {
          return sortMode == SORT_ASCENDING ? result : -result;
        }
      }
    }
    return 0;
  }
  
  /** Setup a new array of indexes with the right number of elements
   *  for the new data model.
   */
  private void reallocateIndexes() {
    int rowCount = filterIndex.length;
    indexes = new int[rowCount];
    
    // Initialise with the identity mapping.
    for (int row = 0; row < rowCount; row++) {
      indexes[row] = row;
    }
  }
  
  
  private void checkModel() {
    if (indexes.length != filterIndex.length) {
      throw new RuntimeException("Model changed");
    }
  }
  
  private void sort() {
    checkModel();
    shuttleSort((int[])indexes.clone(), indexes, 0, indexes.length);
  }
  
  private void n2sort() {
    for (int i = 0; i < getRowCount(); i++) {
      for (int j = i+1; j < getRowCount(); j++) {
        if (compare(indexes[i], indexes[j]) == -1) {
          swap(i, j);
        }
      }
    }
  }
  
  private void shuttleSort(int from[], int to[], int low, int high) {
    if (high - low < 2) {
      return;
    }
    int middle = (low + high)/2;
    shuttleSort(to, from, low, middle);
    shuttleSort(to, from, middle, high);
    
    int p = low;
    int q = middle;
    
    if (high - low >= 4 && compare(from[middle-1], from[middle]) <= 0) {
      for (int i = low; i < high; i++) {
        to[i] = from[i];
      }
      return;
    }
    
    // A normal merge.
    for (int i = low; i < high; i++) {
      if (q >= high || (p < middle && compare(from[p], from[q]) <= 0)) {
        to[i] = from[p++];
      } else {
        to[i] = from[q++];
      }
    }
  }
  
  private void swap(int i, int j) {
    int tmp = indexes[i];
    indexes[i] = indexes[j];
    indexes[j] = tmp;
  }
  
  
}
