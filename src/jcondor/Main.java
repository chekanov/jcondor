package jcondor;

import java.util.jar.*;
import java.io.*;
import java.net.*;
import java.security.*;

public class Main
 {


  public static String DirPath="";
  public static final String fSep = System.getProperty("file.separator");
  public static String cgifile=null;
  public static String wwwfile = null;
  public static String wwwfileUsers = null;
 
 /**
  * For local testing
  * @param argv
  */

 public static void main(String argv[]) {


   try {
                                DirPath=getJarContainingFolder(jcondor.Main.class);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }

  // System.out.println("Local testing:");
  cgifile = null; 
  wwwfile = DirPath+fSep+"current"+fSep+"status.txt";
  wwwfileUsers = DirPath+fSep+"current"+fSep+"users.txt";
  System.out.println("  Status file="+wwwfile);
  System.out.println("  User file="+wwwfileUsers);

  File file = new File(DirPath+fSep+"condor.sh");
        if (file.exists()) {
            boolean bval = file.setExecutable(true);
        } else {
            System.out.println("Not found: "+DirPath+fSep+"condor.sh");
            System.exit(0); 
        }

  file = new File(DirPath+fSep+"current");
        if (file.exists()) {
            boolean bval = file.setWritable(true);
        } else {
            System.out.println("Not found: "+DirPath+fSep+"current");
            System.exit(0);
        }

   
  new MainGui(cgifile,wwwfile,wwwfileUsers);
  
	 

 
}

 /**
         * Get foulder of the jar file
         * @param aclass
         * @return
         * @throws Exception
         */
        private static String getJarContainingFolder(Class aclass) throws Exception {
                  CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();

                  File jarFile;

                  if (codeSource.getLocation() != null) {
                    jarFile = new File(codeSource.getLocation().toURI());
                  }
                  else {
                    String path = aclass.getResource(aclass.getSimpleName() + ".class").getPath();
                    String jarFilePath = path.substring(path.indexOf(":") + 1, path.indexOf("!"));
                    jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
                    jarFile = new File(jarFilePath);
                  }
                  return jarFile.getParentFile().getAbsolutePath();
                }


}



