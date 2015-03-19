package jcondor;

import java.io.*;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainGui {

    private JTabs jtab; 
    private  JFrame frame;
    private  JMenuBar menuBar;
    private  HTable h1,h2,h3;
    private String wwwfile = ""; 
    private String wwwfileUsers = ""; 
    private String cgifile = null; 
    static public String updateTime="";
    private JLabel message;
    private DateFormat formatter = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy");
    private  javax.swing.Timer displayTimer;
  
    /**
     * Build a frame
     * @param cgiFile cgi file to trigger update of readFile. Set to null for localfile 
     * @param readFile file to be read after cgi update (condor_status -l)
     * @param readFileUsers file to be read after cgi update (condor_status -submitters -l)
     */
 public MainGui(String cgiFile, String readFile, String readFileUsers) {

  this.cgifile = cgiFile;
  this.wwwfile = readFile;
  this.wwwfileUsers = readFileUsers;

  Date date = new Date();
  updateTime=formatter.format(date);

  updateFiles();  // initial update

  ReadClassAds r=new ReadClassAds(wwwfile);
  ArrayList<Map> array= r.getSlots();
  VHolder vh1 = new VHolder(array);
  h1=new HTable(vh1,true, this);

  SortedMap<String,Map<String,String>> comp= r.getComputers();
  VHolder vh2 = new VHolder(comp,r.getMaster());
  h2=new HTable(vh2,false,this);


  ReadClassAdsUsers rusers=new ReadClassAdsUsers(wwwfileUsers);
  SortedMap<String,Map<String,String>> usersC= rusers.getUsers();
  VHolder vh3 = new VHolder(usersC,true);
  h3=new HTable(vh3,false,this);

  
        frame = new JFrame("jCondor");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);}
        });

        
     // Creates a menubar for a JFrame
        menuBar = new JMenuBar();
     // Add the menubar to the frame
        frame.setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        JMenu aboutMenu = new JMenu("Help");
        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);
        
        JMenuItem item00 = new JMenuItem(new ExitAction());
        fileMenu.add(item00 );
        
        JMenuItem item11 = new JMenuItem(new ShowAboutAction());
        aboutMenu.add(item11);

        JMenuItem aboutAction = new JMenuItem(new ShowAboutAction());
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setTitle("Condor status");
        jtab=new JTabs();
        jtab.addPanel(h2, "Computers");
        Info info = new Info();
        jtab.addPanel(h1, "Cores");
        jtab.addPanel(h3, "Users");
             
        JPanel jbut = new JPanel();
        message= new JLabel();
        jbut.add(message,BorderLayout.WEST);
        setMessage("Last updated: "+updateTime);
        frame.getContentPane().add(jbut, BorderLayout.SOUTH); 
        frame.getContentPane().add(jtab, BorderLayout.CENTER);
        frame.setSize(700, 600);
        frame.setVisible(true);


 // update every 2 min
 EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ActionListener actionListener = new ActionListener() {

                    public void actionPerformed(ActionEvent actionEvent) {
                        updateData();
                        System.gc();
                    }
                };
                displayTimer  = new javax.swing.Timer(1000*60*2, actionListener);
                displayTimer.start();
            }
        });  
 
 
 }


  public void updateFiles(){

     if (cgifile != null) { 
	// System.out.println(mess);
      boolean wasSent = false;
      try {
              URL destURL = new URL(cgifile);
              InputStream in = destURL.openStream();
              BufferedReader br = new BufferedReader(new InputStreamReader(in));
              String aLine;
              while ((aLine = br.readLine()) != null) {
                      // System.out.println("reading:"+aLine);
                      String s = aLine;
              }
              wasSent = true;
      } catch (Exception e) {
              e.printStackTrace();
      }
      
      
      // sleep 2 seconds
      try {
		Thread.sleep(2000L);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 

       } // this is Applet or Web-based staff



       if (cgifile == null) {        

          
          String cmd=jcondor.Main.DirPath+jcondor.Main.fSep+"condor.sh"; 
          ArrayList<String> command = new ArrayList<String>();
          command.add(cmd); 
          ProcessBuilder builder = new ProcessBuilder(command);
          Process process;
                try {
                        process = builder.start();
                        InputStream is = process.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);
                        String line;
                        while ((line = br.readLine()) != null) {
                                System.out.println(line);
                        }
                        // System.out.println("Program terminated!");


                //Wait to get exit value
        try {
            int exitValue = process.waitFor();
            // System.out.println("\n\nExit Value is " + exitValue);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }


         try {
                Thread.sleep(3000L); // 5-sec wait 
        } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }



          }


 } 


  public void setMessage(String mess){
    message.setText(mess);
  }
 
 
 /**
  * Reload the table with new data
  */
  public void updateData() {
 
          updateFiles();
    
          Date date = new Date();
          updateTime=formatter.format(date);
         
	  ReadClassAds r=new ReadClassAds(wwwfile);
	  ArrayList<Map> array= r.getSlots();
	  VHolder vh1 = new VHolder(array);
	  h1.updateTable(vh1,true);

	   
	  SortedMap<String,Map<String,String>> comp= r.getComputers(); 
	  VHolder vh2 = new VHolder(comp,r.getMaster());
	  h2.updateTable(vh2,false); 
	  
	  ReadClassAdsUsers rusers=new ReadClassAdsUsers(wwwfileUsers);
	  SortedMap<String,Map<String,String>> cusers= rusers.getUsers(); 
	  VHolder vh3 = new VHolder(cusers,true);
	  h3.updateTable(vh3,false); 

          setMessage("Last updated: "+updateTime);
	  
	    
	}



private class ShowAboutAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    ShowAboutAction() {
            super("About");
    }

    public void actionPerformed(ActionEvent e) {
    	new AboutDialog(frame);
    }
}


private class ExitAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    ExitAction() {
            super("Exit");
    }

    public void actionPerformed(ActionEvent e) {
        displayTimer.stop(); 
    	frame.setVisible(false);
    	frame.dispose();

    }
}













}

