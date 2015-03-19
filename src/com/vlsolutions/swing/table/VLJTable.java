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
import java.beans.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.UIResource;
import javax.swing.table.*;


/** This class extends the JTable component, adding filtering, sorting and key based - selection capabilities.
 *
 *<p> Activating the filter is external to the table (for example with a button)
 *<p> Sorting is enabled with a click on a column header.
 *<p> Key based selection is triggered by key input on a cell (when the column is not editable).
 *
 * @author Lilian Chamontin, VLSolutions
 * @update 2005/08/18 Lilian Chamontin : update configureEnclosingScrollPane to support display of the table 
 * without filtering enabled.
 */

public class VLJTable extends JTable {
  
  
  // default Image set
  private static Image defaultAscImage, defaultDescImage;
  static {
    try {
      defaultAscImage = new ImageIcon(VLJTable.class.getResource("sortAsc16.png")).getImage();
      defaultDescImage = new ImageIcon(VLJTable.class.getResource("sortDsc16.png")).getImage();
    } catch (Exception ignore){
    }
  }
  
  // local images (arrows used to render the ascending and descending sorting)
  private Image ascendingSortImage, descendingSortImage;
  
  private boolean isFilterHeaderDisplayed = false;
  private boolean isFilteringEnabled = false;
  private boolean isSortEnabled = true;
  
  /** A pop-up triggered by key input, allows fast positionning on a sorted table. */
  private PopUpSelector popupSelector;
  /** the associated key trigering listener */
  private KeyListener popupSelectorKeyListener;
  
  /** The table header replacement */
  private JPanel tableHeaderReplacement;
  
  private FilterColumnModel filterColumnModel;
  
  private boolean isFilterRequestingFocus = true;

  /** The panel containing a row of input fields corresponding to the filters
   * (i.e. the lower part of the table header when filtering is enabled and displayed)
   */
  private JPanel filterSubHeader;
  
  public VLJTable() {
    setAscendingSortImage(defaultAscImage);
    setDescendingSortImage(defaultDescImage);
    
    // don't allow column reordering because we're not treating this case for the moment
    // (maybe in a future version)
    //getTableHeader().setReorderingAllowed(false);
  }
  
  
  public void setFilterColumnModel(FilterColumnModel fcmodel){
    this.filterColumnModel = fcmodel;
    if (isFilterHeaderDisplayed){ // hide it, as it's not the right one anymore
      setFilterHeaderVisible(false);
    }
  }
  
  /** Returns the filter column model used by this table. 
   * <p>
   * The FilterColumnModel is responsible for providing JComponents used as filters
   * for the table column headers.
   */
  public FilterColumnModel getFilterColumnModel(){
    return filterColumnModel;
  }
  
  /** Replaces the default ascending sort image by a new one */
  public void setAscendingSortImage(Image image){
    this.ascendingSortImage = image;
  }
  
  /** Replaces the default descending sort image by a new one */
  public void setDescendingSortImage(Image image){
    this.descendingSortImage = image;
  }
  
  /** Returns the ascending sort image used by this table */
  public Image getAscendingSortImage(){
    return ascendingSortImage;
  }
  
  /** Returns the descending sort image used by this table */
  public Image getDescendingSortImage(){
    return descendingSortImage;
  }
  
  /** Enables filtering with a custom table header.
   *<p>
   * This doesn't show the filter zones, but simply allows it to pop-up on right mouse click
   *
   * */
  public void setFilteringEnabled(boolean enable){
    this.isFilteringEnabled = enable;
    FilterModel fm = ((FilterModel)getModel());
    if (fm != null){
      fm.rebuildIndex();
    }
    JTableHeader th = getTableHeader();
    if (th != null){
      th.repaint();
    }
  }
  
  /** Returns a boolean indicating wether the filtering is enabled or not on this table */
  public boolean isFilteringEnabled(){
    return isFilteringEnabled;
  }
  
  /** Enable or disable sorting on this table */
  public void setSortEnabled(boolean enable){
    this.isSortEnabled = enable;
    FilterModel fm = ((FilterModel)getModel());
    if (fm != null){
      fm.rebuildIndex();
    }
    // must repaint as sorting images are not bound properties
    JTableHeader th = getTableHeader();
    if (th != null){
      th.repaint(); 
    }
  }
  
  /** Returns a boolean indicating if sorting is enabled on this table */
  public boolean isSortEnabled(){
    return isSortEnabled;
  }
  
  /**
   * Updates the visibility of the filters on this table.
   * <p> 
   * When enabled, a row of editable JComponents is added under the table header.
   * <p>
   * This method is a shortcut to setFilterHeaderVisible(enable, true);
   * 
   */
  public void setFilterHeaderVisible(boolean visible){
    setFilterHeaderVisible(visible, true);
  }
  
  /** Updates the visibility of the filters on this table.
   * <p> 
   * When enabled, a row of editable JComponents is added under the table header.
   * <p>
   * @param visible        installs the filters if true, removes them if false
   * @param requestFocus  position the focus on the first editable filter when the filter header is shown
   */
  public void setFilterHeaderVisible(boolean visible, boolean requestFocus){
    if (visible){
      isFilterRequestingFocus = requestFocus;
      installFilterHeader();
    } else {
      uninstallFilterHeader();
    }
  }
  
  /** Returns a boolean indicating if the filter header is visible
   */
  public boolean isFilterHeaderVisible(){
    return isFilterHeaderDisplayed;
  }
  
  /** returns true is the focus should be put on the filter zone when it's displayed */
  public boolean isFilterRequestingFocus(){
    return isFilterRequestingFocus;
  }
  
  
  /** enable the popup key selector (fast selection of a row when typing the first chars)
   */
  public void setPopUpSelectorEnabled(boolean enabled){
    if (popupSelectorKeyListener != null) {
      removeKeyListener(popupSelectorKeyListener);
    }
    if (enabled){
      popupSelectorKeyListener = new KeyAdapter(){
        public void keyTyped(KeyEvent e){
          processTableKeyEvent(e);
        }
      };
      addKeyListener(popupSelectorKeyListener);
    }
  }
  
  /** Returns the PopUpSelector used */
  public PopUpSelector getPopUpSelector(){
    if (popupSelector == null){
      popupSelector = createPopUpSelector();
    }
    return popupSelector;
  }
  

  /** overriden to add sorting */
  public void addNotify(){
    super.addNotify();
    
//    installHeader();
    
    /* the standard table header is installed during the addNotify() process. Wo we have to 
     * wait addNotify to add our mouse listener.
     */
    getTableHeader().addMouseListener(new MouseAdapter(){
      public void mouseClicked(MouseEvent e){
        if (SwingUtilities.isLeftMouseButton(e)){
          if (isSortEnabled){
            // rotate the sorting (unsorted => ascending => descending )
            TableColumnModel columnModel = getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = convertColumnIndexToModel(viewColumn);
            int currentSort = getSortMode(column);
            if (currentSort == FilterModel.SORT_NONE) {
              setSortMode(column, FilterModel.SORT_ASCENDING);
            } else if (currentSort == FilterModel.SORT_ASCENDING) {
              setSortMode(column, FilterModel.SORT_DESCENDING);
            } else {
              setSortMode(column, FilterModel.SORT_NONE);
            }
          }
        }
      }
    });
    

  }
  
  /** overriden to follow table structure changes */
  public void tableChanged(TableModelEvent e){
    
    super.tableChanged(e);

    if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
      // it's a structure change
      FilterModel fModel = (FilterModel) getModel();
      TableModel model = fModel.getModel();
      for (int i = 0; i < model.getColumnCount(); i++) {
        getColumnModel().getColumn(i).setHeaderRenderer(new VLTableCellRenderer(VLJTable.this, i));
      }
      
      // install a default FilterColumnModel
      boolean isFilterDisplayed = isFilterHeaderDisplayed;
      
      setFilterColumnModel(new FilterColumnModel(fModel));
      
      if (isFilterDisplayed){
        // show again the filter zones as we were already displaying them
        installFilterHeader();
      } else {
        installHeader();
      }
      
    }
    
  }
  
  /** Installs the table model.
   *<p>
   * Warning : this method replaces the model by an internal (filtering-enabled) one, so
   * getModel() won't return the original model, but the FilterModel.
   *<p>
   * To access the original model, use getBaseModel() instead of getModel().
   *
   */
  public void setModel(TableModel model){
    
    
    FilterModel fModel = new FilterModel(this, model);

    super.setModel(fModel);
    
    for (int i = 0; i < model.getColumnCount(); i++) {
      getColumnModel().getColumn(i).setHeaderRenderer(new VLTableCellRenderer(this, i));
    }
    
    // install a default FilterColumnModel
    
    setFilterColumnModel(new FilterColumnModel(fModel));
    
    
    if (isFilterHeaderDisplayed){
      // show again the filter zones as we were already displaying them
      installFilterHeader();
    }

    getColumnModel().addColumnModelListener(new TableColumnModelListener(){
      public void columnAdded(TableColumnModelEvent e){
      }
      
      public void columnRemoved(TableColumnModelEvent e){
      }
      
      public void columnMoved(TableColumnModelEvent e){
        if (e.getFromIndex() != e.getToIndex() && isFilterHeaderDisplayed){
          reorderFilterHeader();
        }
      }
      
      public void columnMarginChanged(ChangeEvent e){
        adjustFilterSizes();
      }
      
      public void columnSelectionChanged(ListSelectionEvent e){
      }
    });

  }
  
  /** Returns the base model used by the table (when no filtering or sorting is done).
   *<p>
   * Warning : setModel(model) installs a new FilterModel(model) as the table model, 
   * so getModel() doesn't return the same table model, and this is why you have to use this 
   * getBaseModel() method to keep an access on it.
   *<p>
   *
   */
  public TableModel getBaseModel() {
    return ((FilterModel)getModel()).getModel();
  }

  
  /** Returns the base row index of a given visible row.
   * <p>
   * This method is used to retrieve the original position (in the "base" model) of a row, when 
   * no filtering/sorting is applied.
   *  */
  public int getBaseRow(int row){
    return ((FilterModel)getModel()).getSourceRow(row);
  }
  
  /** Sets a filter on a filter column header.
   */
  public void setFilterValue(int col, Object value){
    if (filterColumnModel != null && isFilterHeaderDisplayed){
      filterColumnModel.getFilterCellEditor(col).setValue(value);
    } else {
      ( (FilterModel) getModel()).setFilter(col, value);
      Rectangle r = getCellRect(0, 0, false);
      scrollRectToVisible(r);
    }
  }

  /** Returns the filter used for a column */
  public VLJTableFilter getFilter(int col){
    return ((FilterModel)getModel()).getFilter(col);
  }
  
  /** Activates sorting on a column
   * @param col  the colunm
   * @param mode a sorting mode from FilterModel.SORT_NONE, _ASCENDING or _DESCENDING
   *
   *@see FilterModel
   */
  public void setSortMode(int col,int mode){
    isSortEnabled = true;
    ((FilterModel)getModel()).setSortMode(col,mode);
    getTableHeader().repaint();
  }
  
  /** Returns the sort mode of the given column */
  public int getSortMode(int col){
    return ((FilterModel)getModel()).getSortMode(col);
  }
  
  /** Installs a filter on a column. 
   *
   */
  public void installFilter(int col, VLJTableFilter filter){
    ((FilterModel)getModel()).installFilter(col  , filter);
  }
  
  /** Select the first row of the table matching the given text.
   * <p>
   * If text is longer than the first column, a matching is tried with subsequent columns
   */
  public void selectFirstRowLike(int col, String text, boolean isCaseSensitive) {
    int idx = -1;
    for (int i = 0; i < getRowCount(); i++) {
      StringBuffer sb = new StringBuffer();
      sb.append(getValueAt(i, col).toString());
      int colAppend = col+1;
      int len = text.length();
      while (sb.length() < len && colAppend < getModel().getColumnCount()){
        sb.append(getValueAt(i, colAppend++).toString());
      }
      String str = sb.toString();
      int compare = isCaseSensitive ? str.compareTo(text) : str.compareToIgnoreCase(text);
      if (compare >= 0) {
        idx = i;
        break;
      }
    }
    if (idx != -1) { // found
      setRowSelectionInterval(idx, idx);
      scrollRectToVisible(getCellRect(idx, col, true));
    } else {
      clearSelection();
    }
  }

  
  /** Creates a popUpSelector (protected access to allow specific (local) implementations.
   * <p>
   * The popup selector appears when the user press a key on a non-editable cell, and is used 
   * to select the first row beginning by the typed text.
   */
  protected PopUpSelector createPopUpSelector(){
    return new PopUpSelector(this);
  }
  
  protected void reorderFilterHeader(){
    filterSubHeader.removeAll();
    for (int i = 0; i < getModel().getColumnCount(); i++) {
      int col = getColumnModel().getColumn(i).getModelIndex();
      FilterCellEditor fcEditor = filterColumnModel.getFilterCellEditor(col);
      JComponent tfComp =  fcEditor != null ?(JComponent) fcEditor : new JLabel();
      filterSubHeader.add(tfComp);
    }
    adjustFilterSizes();
    
  }
  
  protected void adjustFilterSizes(){
    for (int i = 0; i < getModel().getColumnCount(); i++) {
      Component comp = filterSubHeader.getComponent(i);
      int w = getColumnModel().getColumn(i).getWidth();
      Dimension d = new Dimension(w, comp.getPreferredSize().height);
      comp.setPreferredSize(d);       
    }
    filterSubHeader.revalidate();
  }
    
  
  /** installs a filter header under the standard table header */
  protected void installFilterHeader(){ 
    Container parent = getParent();
    if (parent instanceof JViewport) {
      JPanel newHeader = new JPanel(new BorderLayout());
      newHeader.add(getTableHeader(), BorderLayout.CENTER);
      filterSubHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,0));
      // add a nice border to isolate 
      filterSubHeader.setBorder(
          BorderFactory.createCompoundBorder(
          BorderFactory.createMatteBorder(0,0,1,0, Color.black),
          BorderFactory.createEmptyBorder(1,0,0,0)
          ));
      
      
      FilterCellEditor firstFilter = null;
      
      // install the filter for every column (except when it has a null filter)
      for (int i = 0; i < getModel().getColumnCount(); i++) {
        final FilterCellEditor fcEditor = filterColumnModel.getFilterCellEditor(i);
        if (fcEditor != null){
          // when null (no filter available), we replace it by a simple label
          fcEditor.setValue(null); // empty at first show
        }
        final JComponent tfComp =  fcEditor != null ?(JComponent) fcEditor : new JLabel();
        if (firstFilter == null){
          firstFilter = fcEditor;
        }
        final int col = i;
        int w = getColumnModel().getColumn(col).getWidth(); 
        Dimension d = new Dimension(w, tfComp.getPreferredSize().height);
        tfComp.setPreferredSize(d);
        
        filterSubHeader.add(tfComp);
        if (fcEditor != null){
          fcEditor.addPropertyChangeListener(fcEditor.getFilterChangePropertyName(),
              new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent e){              
                  ( (FilterModel) getModel()).setFilter(
                  col , e.getNewValue());
                  Rectangle r = getCellRect(0, 0, false);
                  scrollRectToVisible(r);
            }
          });
        }        
      }
      
      newHeader.add(filterSubHeader, BorderLayout.SOUTH);
      tableHeaderReplacement = newHeader;
      installHeader();
      if (firstFilter != null && isFilterRequestingFocus){
        final JComponent filter = (JComponent)firstFilter;
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            filter.requestFocus();
          }
        });
      }
    }
    isFilterHeaderDisplayed = true;

    
  }
  
  
  
  /** overriden to bypass the scrollpane configuration */
  protected void configureEnclosingScrollPane(){
    // well, this is a copy of JTable source code, where the tableheader installation 
    // has been removed as it created problems with installHeader method.
    if (isFilterHeaderDisplayed){ //2005/08/18
      Container p = getParent();
      if (p instanceof JViewport) {
        Container gp = p.getParent();
        if (gp instanceof JScrollPane) {
          JScrollPane scrollPane = (JScrollPane)gp;
          Border border = scrollPane.getBorder();
          if (border == null || border instanceof UIResource) {
            scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
          }
        }
      }
    } else {
      super.configureEnclosingScrollPane();
    }
    
  }

  /** installs an alternative header or the original one, depending on the filtering state */
  protected void installHeader(){
    Container parent = getParent();
    if (parent instanceof JViewport) {
      JScrollPane enclosingScrollPane = (JScrollPane) parent.getParent();
      if (tableHeaderReplacement == null){
        // get back to standard header
        enclosingScrollPane.setColumnHeaderView(getTableHeader());
      } else { // tableHeaderReplacement != null
        enclosingScrollPane.setColumnHeaderView(tableHeaderReplacement);
      }      
    } // else parent isn't a viewport, and we cannot install a header
  }

  /** processing of key input on the table : triggers the popup selector if enabled*/
  private void processTableKeyEvent(KeyEvent e) {
    int col = getSelectedColumn();
    int row = getSelectedRow();
    if (col == -1) {
      col = 0;
    }
    if (row == -1) {
      row = 0;
    }
    if (getModel().isCellEditable(row, col)) return; 
    
    if (Character.isLetterOrDigit(e.getKeyChar())) { 
      // avoid intercepting control keys (F10, ESCAPE...)
      if (popupSelector == null) {
        popupSelector = createPopUpSelector();
        popupSelector.setCol(col);
      } else {
        popupSelector.setCol(col);
      }
      popupSelector.setText(String.valueOf(e.getKeyChar()));
      popupSelector.popUp();
    }
  }
  
  
  /** removes the filter header (back to standard table header)*/
  private void uninstallFilterHeader(){
   tableHeaderReplacement = null;
   installHeader();
    ((FilterModel)getModel()).clearFilters();
    isFilterHeaderDisplayed = false;
  }
  
  
  
  
  
  
  
  
}
