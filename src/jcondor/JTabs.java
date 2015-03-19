package jcondor;

import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;

import java.awt.*;
import java.awt.event.*;




public class JTabs extends JTabbedPane {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public JTabs() {
       
        
    
    }

	
	/**
	 * Add one panel
	 * @param jp
	 * @param name
	 */
	public void addPanel(JPanel jp, String name) {
		 addTab(name,null, jp, name);
         setSelectedIndex(0);
		
		
	}
	
}


