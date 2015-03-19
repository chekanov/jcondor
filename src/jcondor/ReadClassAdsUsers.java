package jcondor;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Date;
/**
 * This file parses the user output from condor_status -submitter -l
 * 
 * @author sergei
 *
 */
public class ReadClassAdsUsers {

	private ArrayList<Map> arrayList;
	private Map<String, String> envVars;
	private SortedMap<String, Map<String, String>> serVars;
	private String masterVar = "";
	private boolean first;
	private URL URLfile;
 
	
	/**
	 * Read URL file and parse the input
	 * @param FileName
	 */
	public ReadClassAdsUsers(String FileName) {



		//int Ntot = 0;
		first = true;
		arrayList = new ArrayList<Map>();

		envVars = new HashMap<String, String>();
		serVars = new TreeMap<String, Map<String, String>>();

		
		if (FileName.indexOf("http")>-1 ) {
		try {
			URLfile = new URL(FileName);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
		
		
		String key, value;

		try {
			BufferedReader br;
			
			if (FileName.indexOf("http")>-1 ) {
			   br = new BufferedReader(new InputStreamReader(
					URLfile.openStream()));
			   
			} else {
		            FileInputStream fstream = new FileInputStream(FileName);
			    // Get the object of DataInputStream
			    DataInputStream in = new DataInputStream(fstream);
			     br = new BufferedReader(new InputStreamReader(in));
			}

                        first=true;
			String line;
			while ((line = br.readLine()) != null) {
                                line = line.trim();
				int idx = line.indexOf('=');
                                int inx2 = line.indexOf("Machine");

                                 // System.out.println(line);
                                 if (idx > -1) {
					key = line.substring(0, idx);
					value = line.substring(idx + 1,line.length());
					key = key.trim();
					value = value.trim();
					value = value.replace("\"", "");
					// System.out.println( key + " = " + value );
					// envVars.setProperty( key, value );
					// String p = envVars.getProperty("TargetType");
					envVars.put(key, value);
                                 }

				if (line.length()< 2) { // trigger new user 
					arrayList.add(envVars);
					envVars = new HashMap<String, String>();
					first = false;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

                // System.out.println(arrayList.size());


		// remove last
		int xlast = arrayList.size();
		// arrayList.remove(xlast - 1);

		for (Map<String, String> xmap : arrayList) {

                        
			value = (String) xmap.get("Name");
                        if (value.indexOf("DedicatedScheduler@")>-1) {
                           continue; 
                        } 
                        //System.out.println(value);
			Map<String, String> attr = new HashMap<String, String>();
			attr.put("RunningJobs", (String) xmap.get("RunningJobs"));
			attr.put("IdleJobs", (String) xmap.get("IdleJobs"));
			attr.put("HeldJobs", (String) xmap.get("HeldJobs"));
			attr.put("FlockedJobs", (String) xmap.get("FlockedJobs"));
			attr.put("ScheddName", (String) xmap.get("ScheddName"));
			attr.put("JobQueueBirthdate", (String) xmap.get("JobQueueBirthdate"));
			attr.put("MyCurrentTime", (String) xmap.get("MyCurrentTime"));
			attr.put("DaemonStartTime", (String) xmap.get("DaemonStartTime"));
			attr.put("UpdateSequenceNumber", (String) xmap.get("UpdateSequenceNumber"));
			serVars.put((String) value, attr);

			Set skeys = xmap.entrySet();
			Iterator it = skeys.iterator();
			while (it.hasNext()) {
				// key=value separator this by Map.Entry to get key and value
				Map.Entry m = (Map.Entry) it.next();
				// getKey is used to get key of Map
				key = (String) m.getKey();
				// getValue is used to get value of key in Map
				value = (String) m.getValue();
				// System.out.println("Key :"+key+"  Value :"+value);
			}

		}

		

	}

	

	public ArrayList<Map> getSlots() {

		return arrayList;
	}

	/**
	 * Get map with users
	 * @return
	 */
	public SortedMap<String, Map<String, String>> getUsers() {

		return serVars;
	}
}
