/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.impservices.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import com.tcs.dg.constant.UserConfigConstant;
import com.tcs.dg.impservices.commonservices.FileCopyScheduler;

/**
 * The Class Utility.
 */
public class Utility {
	
	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(Utility.class);

	/**
	 * Mk dir.
	 *
	 * @param name the name
	 * @param endTimeDate the end time date
	 * @param userConfigConstant the user config constant
	 * @return true, if successful
	 */
	public static synchronized boolean mkDir(String name, Date endTimeDate,UserConfigConstant userConfigConstant){
		File file = new File(userConfigConstant.getIntermediate()+File.separator+name);
		if (!file.exists()) {
			if (file.mkdir()){ 
				new FileCopyScheduler(endTimeDate,name,userConfigConstant);
				return true;
			}
		}else{
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the updated subnetwork.
	 *
	 * @param subnetwork the subnetwork
	 * @param i the i
	 * @return the updated subnetwork
	 */
	public static String getUpdatedSubnetwork(String subnetwork, int i){
		if(i>1){
			subnetwork = subnetwork.substring(0,subnetwork.lastIndexOf("_"))+"_"+i;
		}else{
			subnetwork = subnetwork+"_"+i;
		}
		return subnetwork;
	}
	
	/**
	 * Convert xm lto gzip file.
	 *
	 * @param sourceFilepath the source filepath
	 * @param destinatonZipFilepath the destinaton zip filepath
	 */
	public static void convertXMLtoGzipFile(String sourceFilepath, String destinatonZipFilepath) {

		byte[] buffer = new byte[1024];
		int bytesRead;
		try (
				FileOutputStream fileOutputStream = new FileOutputStream(destinatonZipFilepath);
				GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fileOutputStream);
				FileInputStream fileInput = new FileInputStream(sourceFilepath);
				)
				{
			while ((bytesRead = fileInput.read(buffer)) > 0) {
				gzipOuputStream.write(buffer, 0, bytesRead);
			}
			fileInput.close();
			gzipOuputStream.finish();
			gzipOuputStream.close();

				} catch (IOException ex) {
					logger.error("Error in converting XML to Gzip file");
				}
	}

	
}
