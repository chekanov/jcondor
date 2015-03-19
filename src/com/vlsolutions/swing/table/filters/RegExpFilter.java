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
import com.vlsolutions.swing.table.VLJTableFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** An extended column filter that allows regular expressions .
 *
 * @author Lilian Chamontin, VLSolutions
 */
public class RegExpFilter implements VLJTableFilter  {
  
  String expression;
  boolean ignoreCase = false;
  Pattern pattern;
  boolean hasPattern; // true when somthing entered in the filter

  public RegExpFilter() {
  }

  public RegExpFilter(boolean ignoreCase) {
    setIgnoreCase(ignoreCase);
  }
  
  public void setFilter(String filter){
    this.expression = filter;
  }
  public String getFilter(){
    return expression;
  }

  public void setIgnoreCase(boolean ignoreCase){
    this.ignoreCase = ignoreCase;
  }
  public boolean isIgnoreCase(){
    return ignoreCase;
  }
  
  
  public boolean accept(Object filterPattern) {    
    if (!hasPattern){
      // no pattern, so accept
      return true;
    } else if (ignoreCase){
      Matcher m = pattern.matcher(((String)filterPattern).toLowerCase());
      return m.matches();
    } else {
      Matcher m = pattern.matcher(((String)filterPattern));
      return m.matches();
    }
  }
  
  
  /** preprocess the filter to optimize performance */
  public void compile(){
    if (expression == null || expression.equals("")){
      hasPattern = false;
      return;
    } else {      
      hasPattern = true;
      String filter = expression;
      if (ignoreCase){
        filter = filter.toLowerCase();
      }

      StringBuffer sb = new StringBuffer();
      filter = filter.replace('?', '.');// regexp syntax
      boolean foundRegExp = false;
      for (int i = 0; i < filter.length(); i++) {
        char c = filter.charAt(i);
        if (c == '*'){
          sb.append(".*");
          foundRegExp = true;
        } else if (c == '?'){
          sb.append('.');
          foundRegExp = true;
        } else {          
          sb.append(c);
        }
      }
      
      if (! foundRegExp){
        // no regexp, swith to "startswith" mode by adding a .* at the end
        sb.append(".*");
      }
      
      try {
        this.pattern = Pattern.compile(sb.toString());
      } catch (Exception ex) {
        // ignore
      }
    }
  }

  /**  Sets the filter according to the input the filter zone
   *
   */
  public void setFilter(Object value){
    setFilter((String) value);
  }

  public boolean isFiltering() {
    return expression != null && !expression.equals("");
  }
  
  
}
