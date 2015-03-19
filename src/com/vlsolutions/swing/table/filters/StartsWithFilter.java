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

package com.vlsolutions.swing.table.filters;

import com.vlsolutions.swing.table.*;

/** A filter that accepts only string with the same beginning.
 *
 * @author Lilian Chamontin, VLSolutions
 */
public class StartsWithFilter implements VLJTableFilter {
  protected String filter = null;
  protected boolean ignoreCase = false;
  protected String lowerCaseFilter;
  
  public StartsWithFilter() {
  }

  public StartsWithFilter(String filter) {
    this.filter = filter;
  }

  public StartsWithFilter(boolean ignoreCase) {
    this.ignoreCase = ignoreCase;
  }

  public StartsWithFilter(boolean ignoreCase, String filter) {
    this.ignoreCase = ignoreCase;
    setFilter(filter);
  }
  
  public void setFilter(String filter){
    this.filter = filter;
  }
  public String getFilter(){
    return filter;
  }
  public void setIgnoreCase(boolean ignoreCase){
    this.ignoreCase = ignoreCase;
  }
  public boolean isIgnoreCase(){
    return ignoreCase;
  }
  

  public boolean accept(Object filterPattern) {
    if (filter == null) {
      return true;
    } else if (ignoreCase){
      return ((String)filterPattern).toLowerCase().startsWith(lowerCaseFilter);
    } else {   
      return ((String)filterPattern).startsWith(filter);
    }
  }
  
 /** preprocess the filter to optimize performance */
  public void compile(){
    if (ignoreCase && filter != null){
      lowerCaseFilter = filter.toLowerCase();
    }
  }
  
 /**  Sets the filter according to the input the filter zone
   *
   */
  public void setFilter(Object value){
    setFilter((String) value);
  }
  
  public boolean isFiltering() {
    return filter != null && !filter.equals("");
  }
  
  
}
