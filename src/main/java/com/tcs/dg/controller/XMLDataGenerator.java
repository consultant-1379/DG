/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

import com.tcs.dg.constant.UserConfigConstant;
import com.tcs.dg.impservices.mainthreads.ThreadPoolService;

/**
 * The Class DataGenerator.
 * This gives the implementation of run() method of the thread.
 */
public class XMLDataGenerator implements Runnable {

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(XMLDataGenerator.class);

	private UserConfigConstant userConfigConstant;

	private Map<String, UserConfigConstant> userInputDetails;

	public XMLDataGenerator(UserConfigConstant userConfigConstant, Map<String, UserConfigConstant> userInputDetails) {
		this.userConfigConstant = userConfigConstant;
		this.userInputDetails=userInputDetails;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * Thread run() method calls ThreadPoolService().poolCreator() for further Files Processing.
	 */
	public synchronized void run() {
		File dir = new File(userConfigConstant.getInputs());
		String[] fileList = dir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
                    return name.endsWith(".xml") || name.endsWith(".gz");
				}
		});
		Arrays.sort(fileList);
		try {
			new ThreadPoolService().poolCreator(fileList, userConfigConstant,userInputDetails);
		} catch (Exception e) {
			logger.error("......Main method Exception....");
		}
	}
}
