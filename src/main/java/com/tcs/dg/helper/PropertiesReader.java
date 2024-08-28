/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * The Class PropertiesReader.
 * Load a properties file from the file system and retrieved the property value.
 */
public class PropertiesReader {

	/** The props. */
	private static Properties props = null;
	
	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(PropertiesReader.class);
	
	/**
	 * Gets the properties and save them in key and value pair.
	 *
	 * @param resource the resource
	 * @return the properties
	 */
	public static Properties getProperties(String resourceName){
		try{
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			props = new Properties();
			InputStream input = loader.getResourceAsStream(resourceName);
			props.load(input);
		}catch (IOException e) {
			logger.error("Error in loading file.." + e);
		} 
		return props;
	}

}
