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

/** This interface describes a filtering algorithm.
 *
 * @author Lilian Chamontin, VLSolutions
 */
public interface VLJTableFilter {

  /**  Sets the filter according to the input of the associated filter zone
   *
   */
  public void setFilter(Object value);
  
  /** Returns true if the cell respects the filtering (and should be displayed).
   *
   * @param cellContent  can be of any type (generally a string) depending on the column class
   */
  public boolean accept(Object cellContent);
  
  
  /** Preprocess the filter to optimize performance.
   *<p> 
   *  This method is called before a filtering pass is done on the table model.
   *<p> 
   * For example, if the algorithm depends on regular expressions, this compile() 
   * method would compile the filter pattern.
   *<p>
   * Some filtering algorithm don't need to be compiled (like those relying on a 
   * simple String.indexOf()). In that case, leave the method empty.
   */
  public void compile();
  
  /** Returns true if the filter has got a value that should trigger filtering.
   * <p>
   * This method is called after compile(), and is used to avoid unnecessary 
   * filtering (for example, when the filter zone is empty).
   * <p>
   * When all filters are not filtering (isFiltering returns false for every column), 
   * the table access will be optimized to skip the filtering process.
   */
  public boolean isFiltering();
  
  
  
}
