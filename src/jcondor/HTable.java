// * This code is licensed under:
// * JHPlot License, Version 1.0
// * - for license details see http://hepforge.cedar.ac.uk/jhepwork/ 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
package jcondor;

import com.vlsolutions.swing.table.VLJTable;
import com.vlsolutions.swing.table.filters.RegExpFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.*;

/**
 * 
 * Build a table in a frame to display data in various containers (P1D, H1D,
 * F1D). One can sort and filter data in this table, but not modify the data For
 * more advance manipulations, use SPsheet class
 * 
 * @author S.Chekanov
 * 
 */

public class HTable extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private VLJTable table;

    private boolean renderColor=false;
	
	private JButton filter;

	private JPanel control;
	
	private DefaultTableModel model;
	
	private MainGui maingui;
	
	 private JButton updateButton;

	/**
	 * Main class to build a table from Vectors.
	 * 
	 * @param explanation
	 *            Title of the table or some explanation
	 * 
	 * @param colNames
	 *            Vector with column names
	 * 
	 * @param fillnames
	 *            Vector with some data: First you should fill a vector with the
	 *            row (should have the same size as colNames). Then add this row
	 *            as an element to the global vector
	 * 
	 */

	public HTable(String explanation, Vector<String> colNames, Vector fillnames) {

		// build table
		buildTable(explanation, colNames, fillnames);

	}

/**
	 * Build a table frame to display data. Default constructor.
	 * 
	 * 
	 */
	public HTable() {

	}


 
 /**
  * Fill the table
  * @param vh
  * @param renderColor false if no color is used 
  */
 
        public HTable(VHolder vh, boolean renderColor, MainGui maingui) {
	            this.renderColor=renderColor;
	            this.maingui=maingui;
                buildTable(vh.getTitle(), vh.getNames(), vh.getData());

        }


       
        /**
         * Update table with new data
         * @param vh
         * @param renderColor
         */
        public void updateTable(VHolder vh, boolean renderColor) {
            this.renderColor=renderColor;
            fillTable(vh.getTitle(), vh.getNames(), vh.getData());

    }

        
        
        
        /**
         * Fill table
         * @param explanation
         * @param colNames
         * @param fillnames
         */
        
       public void fillTable(String explanation, Vector<String> colNames,
    			Vector fillnames) {
        	
    	   
        	 model = new DefaultTableModel(fillnames, colNames);
        	 table.setModel(model);
        	 
        }
        
        
        
        
        
        
        
        

	/**
	 * Main class to build the table.
	 * 
	 * @param explanation
	 *            Title of the table or some explanation
	 * 
	 * @param colNames
	 *            Vector with column names
	 * 
	 * @param fillnames
	 *            Vector with some data: First you should fill a vector with the
	 *            row (should have the same size as colNames). Then add this row
	 *            as an element to the global vector
	 * 
	 */

	private void buildTable(String explanation, Vector<String> colNames,
			Vector fillnames) {

		

		
		
		setLayout(new BorderLayout());

		JPanel top = new JPanel(new BorderLayout());
		JPanel center = new JPanel(new BorderLayout());  
        add(center, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

		

		table = new VLJTable() {
			public static final long serialVersionUID = 125;

			public Component prepareRenderer(TableCellRenderer renderer,
					int rowIndex, int vColIndex) {

				Component c = super.prepareRenderer(renderer, rowIndex,
						vColIndex);
				Color colcell = new Color(240, 255, 255);
				
				
				
				
				
				
				if (rowIndex % 2 == 0 && !isCellSelected(rowIndex, vColIndex)) {
					c.setBackground(colcell);
				} else {
					// If not shaded, match the table's background
					c.setBackground(getBackground());
				}

				// colom
				c.setForeground(Color.black);
				if (isCellSelected(rowIndex, vColIndex)) {
					c.setBackground(Color.blue);
					c.setForeground(Color.white);
				}
				
				
				// show in color
				if (renderColor) {
					String s =  table.getModel().getValueAt(rowIndex, vColIndex ).toString();
				
				 if(s.equalsIgnoreCase("Claimed")) 
			     {
			         c.setForeground(Color.black);
			         c.setBackground(Color.green);
			     }
				}
				
				
				return c;
			}
			
			
			
			
			
		};

		// declare and init the VLJTable and its table model.
		 model = new DefaultTableModel(fillnames, colNames) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};

		table.setModel(model);
		table.setFilteringEnabled(true);
		table.setPopUpSelectorEnabled(true);
		table.getPopUpSelector().setCaseSensitive(false);

                 for (int i=0; i<colNames.size(); i++) {
                        table.installFilter(i, new RegExpFilter(true));
                }

 

		/*
		 * TableColumn column = table.getColumnModel().getColumn(0);
		 * column.setPreferredWidth(100); column.setMinWidth(100);
		 * column.setMaxWidth(100);
		 */
		// do disable filtering on a column :
		// table.getFilterColumnModel().setFilterCellEditor(2, null);
		// declare a filter button
		filter = new JButton(new ImageIcon(getClass().getResource(
				"/com/vlsolutions/swing/table/filter16.png")));
		filter.setMargin(new Insets(2, 2, 2, 2));
		filter.setRolloverEnabled(true);
		filter.setToolTipText("Filter");
		filter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.setFilterHeaderVisible(!table.isFilterHeaderVisible());
			}
		});

		
		

		String explanations = "<html><body>" + explanation 
				+ "</body></html>";
		top.add(new JLabel(explanations),BorderLayout.CENTER);
		

		control = new JPanel(new BorderLayout());
		control.setPreferredSize(new Dimension(24, 60));
		control.setMaximumSize(new Dimension(24, 60));
		control.setMinimumSize(new Dimension(24, 60));
		control.setLayout(new GridLayout(2, 0, 0, 24));
		
		
		
		 updateButton = new JButton(new ImageIcon(getClass().getResource(
        "/com/images/ReloadSmall.png")));
         updateButton.setToolTipText("Refresh status");
         updateButton.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {

        	  maingui.updateData();
          }
          });

         
         updateButton.setMargin(new Insets(2, 2, 2, 2));
         updateButton.setRolloverEnabled(true);


		/*
		 * filter.setPreferredSize(new Dimension(30, 22));
		 * closeButton.setPreferredSize(new Dimension(30, 22));
		 * filter.setMaximumSize(new Dimension(30, 22));
		 * closeButton.setMaximumSize(new Dimension(30, 22));
		 */
		control.add(updateButton);
		control.add(filter);
		top.add(control,BorderLayout.WEST);
		center.add(new JScrollPane(table));

		table.setFilterHeaderVisible(true);

	        //TableColumn col = table.getColumnModel().getColumn(0);
                //int width = 60;
                //col.setPreferredWidth(width);	

	}

	
	
	
	
}
