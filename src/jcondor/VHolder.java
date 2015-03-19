package jcondor; 

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;



/**
 * 
 * @author S.Chekanov
 * 
 */
public class VHolder {

	private String title;

	private Vector<String> names;

        private DecimalFormat dfb  = new DecimalFormat( "#,###,###,##0.00" );

	private Vector<Object> data;

	private String  master;
	
	private DateFormat formatter = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy");
	
	public void setMaster(String s){
		master=s;
	}

	
	/**
	 * Get list with cores
	 * @param arrayList
	 */
	public VHolder(ArrayList<Map>  arrayList) {
		
		float loadsum=0;
		float totCPU=0;
		
		DecimalFormat dfb2  = new DecimalFormat( "###,##0.0" );
		data = new Vector<Object>();
		names = new Vector<String>(Arrays.asList((new String[] { "Nr", "Name", "State", "Activity",
				"LoadAv %", "ActivityTime" })));

                int j=0;
                for (Map xmap : arrayList) {
                        int size = xmap.size();
                        j=j+1;
                        String v0=Integer.toString(j);
                        String v1=(String)xmap.get("Name");
                        String v2=(String)xmap.get("Activity");
                        String v3=(String)xmap.get("TotalLoadAvg");
                        String v4=(String)xmap.get("State");
                        String v5=(String)xmap.get("EnteredCurrentActivity");
                        String v6= (String)xmap.get("MyCurrentTime");
                        String v7= (String)xmap.get("TotalCpus");
                        Long l1=convertToLong(v5);
                        Long l2=convertToLong(v6);
                        
                        String tmp="0";
                        if (v4.equals("Claimed")) {
                        	
                        	Long LL=l2-l1; // convert to D,H,M
                        	int mins = (int)(LL/60) ;  // minutes
                        	int _days = mins / 60 / 24 ;
                        	int _hours = (mins - _days * 60 * 24) / 60 ;
                        	int _mins = mins - _days * 60 * 24 - _hours * 60 ;
                        	if (_days == 0 && _hours==0){
                        		tmp= String.valueOf(_mins)+"min";
                        	}  else if (_days == 0   && _hours>0){
                        		tmp= String.valueOf(_hours)+"hours: "+String.valueOf(_mins)+"min";
                        	} else if (_days >0  && _hours>0){
                            	tmp= String.valueOf(_days)+"days: "+String.valueOf(_hours)+"hours: "+String.valueOf(_mins)+"min";
                        	}
                        	
                        }
                        
                        
                        float a1=convertToFloat(v3);
                        float a2=convertToFloat(v7);
                        totCPU=totCPU+a2;
                        float aa=100*(a1/a2);
                        loadsum=loadsum+aa;
                        String ss=dfb2.format(aa);
                        
                        
                        
			Vector<Object> row = new Vector<Object>();
                        row.add(v0);
			row.add(v1);
			row.add(v4);
			row.add(v2);
			row.add(ss);
                        row.add(tmp);
			data.add(row);
		}

                float averageLoad = loadsum /arrayList.size();
                String sss=dfb2.format(averageLoad);
                
                title = "Nr of cores="+Integer.toString(arrayList.size())+"   &nbsp; &nbsp;  Average load="+sss+"% &nbsp; &nbsp;";        
                
                
	}


	
	/**
	 * Get vector with computers
	 * @param computers
	 */

	public VHolder(SortedMap<String, Map<String,String>> computers, String master) {

                int j=0;
		this.master=master;
		data = new Vector<Object>();
		names = new Vector<String>(Arrays.asList((new String[] { "Nr", "Name", "System",
				"Disk (GB)", "TotalDisk (GB)",  "CPU", "Memory (MB)", "EnteredCurrentState", "CurrentTime",  "Host" })));
		title = "Active computers: "+Integer.toString(computers.size())+" &nbsp; &nbsp; <i>Collector: "+master+"</i>  &nbsp; &nbsp;";
		Set s=computers.entrySet();
		
		Iterator it=s.iterator();

        while(it.hasNext())
        {

            j=j+1;
            String v0=Integer.toString(j);

            // key=value separator this by Map.Entry to get key and value
            Map.Entry m =(Map.Entry)it.next();

            // getKey is used to get key of Map
            String key=(String)m.getKey();

            // getValue is used to get value of key in Map
            Map<String,String> value=(Map<String,String>)m.getValue();
            
            
            String s1=(String)value.get("OpSys");
            
            String s2=(String)value.get("Disk");
            float a2=convertToFloat(s2)/1000000;
            
            String s3=(String)value.get("TotalDisk");
            float a3=convertToFloat(s3)/1000000;
            
            String s4= (String)value.get("TotalCpus");
            
            String s5=  (String)value.get("TotalMemory");
            	    
            
            String s6= (String)value.get("EnteredCurrentState");
            String s6a= (String)value.get("MyCurrentTime");
            
            Long l1=convertToLong(s6); //  Date d1 = new Date(l1);
            Long l2=convertToLong(s6a); //  Date d2 = new Date(l2);
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(l1*1000);
            String ss1=formatter.format(calendar.getTime());
            
            
            calendar.setTimeInMillis(l2*1000);
            String ss2=formatter.format(calendar.getTime());
            
            
            
            
            
            String s7= (String)value.get("PublicNetworkIpAddr");
           


            
           
            Vector<Object> row = new Vector<Object>();
                        row.add(v0);
			row.add(key);
			row.add(s1);
			row.add( dfb.format(a2) );
			row.add( dfb.format(a3)  );
			row.add(  s4 );
			row.add(  s5  );
			row.add(  ss1 );
			row.add(  ss2 );
			row.add(  s7  );
			data.add(row);
            
            
         //   System.out.println("Key :"+key+"  Value :"+value);
        }
	}
	
		
        /**
    	 * Get current users
    	 * @param computers
    	 * @param users - is just a dummy
    	 */

    	public VHolder(SortedMap<String, Map<String,String>> users, boolean user) {

                int j=0;
    		data = new Vector<Object>();
    		names = new Vector<String>(Arrays.asList((new String[] { "Nr", "Name", "Running Jobs", "Idle Jobs",
    				"Held Jobs", "Flocked Jobs",  "Schedd Name",  "RunTime"})));
    		
    		title = "Users";
    		Set s=users.entrySet();
    		
    		Iterator it=s.iterator();

            while(it.hasNext())
            {

            j=j+1;
           String v0=Integer.toString(j);


                // key=value separator this by Map.Entry to get key and value
                Map.Entry m =(Map.Entry)it.next();

                // getKey is used to get key of Map
                String key=(String)m.getKey();

                // getValue is used to get value of key in Map
                Map<String,String> value=(Map<String,String>)m.getValue();
                
                
                String s11=(String)value.get("RunningJobs");
                String s12=(String)value.get("IdleJobs");
                String s13=(String)value.get("HeldJobs");
                String s14=(String)value.get("FlockedJobs");
                String s15=(String)value.get("ScheddName");
                
                String s6a= (String)value.get("MyCurrentTime");
                
                String s6= (String)value.get("JobQueueBirthdate");

                
                String s7= (String)value.get("DaemonStartTime");
              
                
                String s8= (String)value.get("UpdateSequenceNumber");
                
                Long l1=convertToLong(s6a);  //  Date d1 = new Date(l1);
                Long l2=convertToLong(s6);   //  Date d2 = new Date(l2);
                Long l3=convertToLong(s7);   //  Date d2 = new Date(l2);
                Long l4=convertToLong(s8)*10; //  Do not know what to put!! 10 seems OK
                
                String tmp="0";
  
                
                if (s11.length()>0)	 {
               
                	Long LL=l1-l2; // convert to D,H,M
                	int mins = (int)(LL/60) ;  // minutes
                	int _days = mins / 60 / 24 ;
                	int _hours = (mins - _days * 60 * 24) / 60 ;
                	int _mins = mins - _days * 60 * 24 - _hours * 60 ;
                	if (_days == 0 && _hours==0){
                		tmp= String.valueOf(_mins)+"min";
                	}  else if (_days == 0   && _hours>0){
                		tmp= String.valueOf(_hours)+"hours: "+String.valueOf(_mins)+"min";
                	} else if (_days >0  && _hours>0){
                    	tmp= String.valueOf(_days)+"days: "+String.valueOf(_hours)+"hours: "+String.valueOf(_mins)+"min";
                	}
                	
                }
                
                String tmp1="0";
               
                if (s11.length()>0)	 {
               
                	Long LL=l4;            // convert to D,H,M
                	int mins = (int)(LL/60) ;  // minutes
                	int _days = mins / 60 / 24 ;
                	int _hours = (mins - _days * 60 * 24) / 60 ;
                	int _mins = mins - _days * 60 * 24 - _hours * 60 ;
                	if (_days == 0 && _hours==0){
                		tmp1= String.valueOf(_mins)+"min";
                	}  else if (_days == 0   && _hours>0){
                		tmp1= String.valueOf(_hours)+"hours: "+String.valueOf(_mins)+"min";
                	} else if (_days >0  && _hours>0){
                    	tmp1= String.valueOf(_days)+"days: "+String.valueOf(_hours)+"hours: "+String.valueOf(_mins)+"min";
                	}
                	
                }
               
                

              
               
                Vector<Object> row = new Vector<Object>();
                        row.add(v0);
    			row.add(key);
    			row.add(s11);
    			row.add(s12);
    			row.add(s13);
    			row.add(s14);
    			row.add(s15);
    			// row.add(tmp);
    			row.add(tmp1);
    			data.add(row);
                
                
             //   System.out.println("Key :"+key+"  Value :"+value);
            }

    		
        
        
        
        
        
        
        
		
		
		

	}

	
	
	
	
	
	
	
	
	
	
	
	


    private  float convertToFloat(String s){
     float f=0;
     try
    {
      f = Float.valueOf(s.trim()).floatValue();
      // System.out.println("float f = " + f);
    }
    catch (NumberFormatException nfe)
    {
      System.out.println("NumberFormatException: " + nfe.getMessage());
    }

    return f;
    }



    /**
     * Convert to long
     * @param s
     * @return
     */
    
    private  Long convertToLong(String s){
     Long f=0L;
     try
    {
      f = Long.valueOf(s.trim()).longValue();
      // System.out.println("float f = " + f);
    }
    catch (NumberFormatException nfe)
    {
      System.out.println("NumberFormatException: " + nfe.getMessage());
    }

    return f;
    }
	/**
	 * Get title of the container
	 * 
	 * @return Title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Return Vector with the names
	 * 
	 * @return Names of the columns
	 */

	public Vector<String> getNames() {
		return names;
	}

	/**
	 * Return a Vector with the data (rows of the dimension obtained by calling
	 * dimen()).
	 * 
	 * @return Names of the columns
	 */

	public Vector getData() {
		return data;
	}

	/**
	 * Return the dimension of the data
	 * 
	 * @return Dimension
	 */

	public int dimen() {
		return names.size();
	}

	/**
	 * Return the size of the data, i.e. the number of rows
	 * 
	 * @return The number of rows
	 */

	public int size() {
		return data.size();
	}

	

	
}
