/*
 * Data Generator
 * Author: Sumedh singh
 */
package com.tcs.dg.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.tcs.dg.constant.DateAndStringConstants;
import com.tcs.dg.constant.UserConfigConstant;

class DeletionSchedulerTask extends TimerTask {
	final static Logger logger = Logger.getLogger(DeletionSchedulerTask.class);
	
	/** The user input details. */
	private Map<String, UserConfigConstant> userInputDetails;
	
	/** The timer. */
	private Timer timer;

	private UserConfigConstant userConfigObj;
	
	
	
	public DeletionSchedulerTask(Map<String, UserConfigConstant> userInputDetails, UserConfigConstant userConfigObj, Timer timer) {
		this.userInputDetails = userInputDetails;
		this.timer = timer;
		this.userConfigObj = userConfigObj;
	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 * Schedules the DeletionTask to be called at a fixed interval according to the deletionFactor specified in
	 * UserInputSpecification.xml
	 */
	public void run() {
		int interval = UserConfigConstant.getDeletionFactor();
		logger.info("Deletion will commence after "+interval+ "Hrs");
		Timer time = new Timer();
		DeletionTask delete = new DeletionTask(userInputDetails,time,timer);
		time.schedule(delete,(interval * 60 * DateAndStringConstants.getOneMinuteMillisecs()) + userConfigObj.getRoptime()   , interval * 60 * DateAndStringConstants.getOneMinuteMillisecs());
	}
}


/**
 * The Class DeletionScheduler.
 */
public class DeletionScheduler {
	
	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(DeletionScheduler.class);
	/**
	 * Deletion scheduler function.
	 *
	 * @param dateRequired the date required
	 * @param userConfigObj the user config obj
	 * @param userInputDetails the user input details
	 * @throws ParseException the parse exception
	 * Calls the DeletionScheduler at the start time of the first ROP
	 */
	public void DeletionSchedulerFunction(String dateRequired,UserConfigConstant userConfigObj, Map<String, UserConfigConstant> userInputDetails) throws ParseException {
		DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmm"); 
		Date date = dateFormatter.parse(dateRequired);
		logger.info("Deletion Timer started for\t" + date);
		Timer timer = new Timer();
		timer.schedule(new DeletionSchedulerTask(userInputDetails,userConfigObj,timer), date);
	}

}
