package org.lc.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

public class Lib {

	static Logger logger = Logger.getLogger(Lib.class);
	
	public static URLClassLoader getSysLoader() {
		return (java.net.URLClassLoader)ClassLoader.getSystemClassLoader();
	}
	public static synchronized void loadLibrary(java.io.File jar)
	{        
	    try {                    
	        /*We are using reflection here to circumvent encapsulation; addURL is not public*/
	        java.net.URLClassLoader loader = getSysLoader();                        
	        java.net.URL url = jar.toURI().toURL();
	        /*Disallow if already loaded*/
	        for (java.net.URL it : java.util.Arrays.asList(loader.getURLs())){
	            if (it.equals(url)){
	                logger.debug("library " + jar.toString() + " is already loaded");
	            }                
	        }                 
	        java.lang.reflect.Method method = java.net.URLClassLoader.class.getDeclaredMethod(
	            "addURL", 
	            new Class[]{java.net.URL.class}
	        );
	        method.setAccessible(true); /*promote the method to public access*/
	        method.invoke(loader, new Object[]{url});
	    } catch (final NoSuchMethodException | 
	        java.lang.IllegalAccessException | 
	        java.net.MalformedURLException | 
	        java.lang.reflect.InvocationTargetException e){
	        throw new RuntimeException(e.getMessage());
	    }        
	}
	
	public static synchronized void loadLibraries(String dir) {
		File file = new File(dir);
		logger.debug(String.format("Trying to loading jar files under %1$s", dir));
		File[] jars = file.listFiles(new FilenameFilter(){
			public boolean accept(File f, String s) {
				return s!=null && (s.endsWith(".jar") || s.endsWith(".zip"));
			}
		});
		if (jars==null) {
			logger.debug(String.format("No jar files under %1$s", dir));
			return;
		}
		String xcludes = System.getProperty("test.xcludeJars");
		Set<String> sxcludes = new HashSet<String>();
		if (xcludes!=null) {
			String[] xjars = xcludes.split(File.pathSeparator);
			for (String xjar : xjars) sxcludes.add(xjar);
		}
		for (File jar : jars) {
			try {
				if (sxcludes.contains(jar.getName())) continue;
				logger.debug(String.format("Loading jar file %1$s", jar.getName()));
				loadLibrary(jar);
			} catch(Exception e) {
				logger.error(String.format("Cannot load jar file %1$s", jar.getName()));
			}
		}
	}
	public static void main(String[] args) {
		try {
			Lib.loadLibraries("C:\\projects\\pyfia\\target\\com.uxl.sites.pyfia-1.0.1\\WEB-INF\\lib");
			Lib.getSysLoader().loadClass("org.springframework.aop.Advisor");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
