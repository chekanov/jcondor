package jcondor;

import java.util.Properties;

import javax.swing.JApplet;


public class MainApplet extends JApplet {

	
	
	private MainGui maingui;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Main apllet
	 */
    public MainApplet() {
            
    }

    public void init() {
    	
    	final String cgiFile = getParameter("cgiFile");
    	final String readFile = getParameter("readFile");
    	final String readFileUsers = getParameter("readFileUsers");
    	
    	maingui=new MainGui(cgiFile,readFile,readFileUsers);
    }
    
    
    /**
     * Start
     */
    public void start() {
            
    }

    /**
     * Stop
     */
    public void stop() {

        	maingui=null;
            destroy();
    }


}
