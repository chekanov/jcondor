package jcondor;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ReadClassAds {

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
	public ReadClassAds(String FileName) {

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

			String line;
			while ((line = br.readLine()) != null) {
				int idx = line.indexOf('=');

				if (idx > -1 && first == true) {
					key = line.substring(0, idx);
					value = line.substring(idx + 1);
					key = key.trim();
					value = value.trim();
					value = value.replace("\"", "");
					// System.out.println( key + " = " + value );
					// envVars.setProperty( key, value );
					// String p = envVars.getProperty("TargetType");
					envVars.put(key, value);

				}

				if (idx > -1 && first == false) {
					key = line.substring(0, idx);
					value = line.substring(idx + 1);
					key = key.trim();
					value = value.trim();
					value = value.replace("\"", "");
					// System.out.println( key + " = " + value );
					envVars.put(key.trim(), value.trim());
				}

				if (idx < 0) {
					arrayList.add(envVars);
					envVars = new HashMap<String, String>();
					first = false;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// remove last
		int xlast = arrayList.size();
		// arrayList.remove(xlast - 1);

		for (Map<String, String> xmap : arrayList) {

			value = (String) xmap.get("Machine");

			Map<String, String> attr = new HashMap<String, String>();

			masterVar = (String) xmap.get("COLLECTOR_HOST_STRING");

			attr.put("OpSys", (String) xmap.get("OpSys") + " "
					+ (String) xmap.get("Arch"));
			attr.put("Disk", (String) xmap.get("Disk"));
			attr.put("TotalDisk", (String) xmap.get("TotalDisk"));
			attr.put("TotalMemory", (String) xmap.get("TotalMemory"));
			attr.put("TotalCpus", (String) xmap.get("TotalCpus"));
			attr.put("KFlops", (String) xmap.get("KFlops"));
			attr.put("Mips", (String) xmap.get("Mips"));
			attr.put("EnteredCurrentState", (String) xmap.get("EnteredCurrentState"));
			attr.put("TimeToLive", (String) xmap.get("TimeToLive"));
			attr.put("MyCurrentTime", (String) xmap.get("MyCurrentTime"));
			attr.put("PublicNetworkIpAddr", (String) xmap
					.get("PublicNetworkIpAddr"));
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

		/*
		 * System.out.println(arrayList.size());
		 * 
		 * for (Map xmap : arrayList) { int size = xmap.size(); Set
		 * skeys=xmap.entrySet(); // System.out.println(size);
		 * value=(String)xmap.get("MyType"); System.out.println(value); Iterator
		 * it=skeys.iterator(); while(it.hasNext()) { // key=value separator
		 * this by Map.Entry to get key and value Map.Entry m
		 * =(Map.Entry)it.next(); // getKey is used to get key of Map
		 * key=(String)m.getKey(); // getValue is used to get value of key in
		 * Map value=(String)m.getValue();
		 * System.out.println("Key :"+key+"  Value :"+value); }
		 * 
		 * } // end loop over list
		 */

	}

	public String getMaster() {

		return masterVar;
	}

	public ArrayList<Map> getSlots() {

		return arrayList;
	}

	public SortedMap<String, Map<String, String>> getComputers() {

		return serVars;
	}
}
