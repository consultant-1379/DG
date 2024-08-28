package com.tcs.dg.controller;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.tcs.dg.constant.ConstantHolder;
import com.tcs.dg.constant.UserConfigConstant;

/**
 * The Class DeletionTask.
 */
public class DeletionTask extends TimerTask {
	final static Logger logger = Logger.getLogger(DeletionTask.class);



	/** The user input details. */
	private Map<String, UserConfigConstant> userInputDetails;

	/** The time. */
	private Timer time;

	/** The timer. */
	private Timer timer;

	/** The no of files. */
	private int noOfFiles=0;
	
	
	private UserConfigConstant value;


	/**
	 * Instantiates a new deletion task.
	 *
	 * @param userConfigObj the user config obj
	 * @param userInputDetails the user input details
	 * @param time the time
	 * @param timer the timer
	 */
	public DeletionTask(Map<String, UserConfigConstant> userInputDetails, Timer time, Timer timer) {
		this.userInputDetails=userInputDetails;
		this.time=time;
		this.timer=timer;
	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 * Checks if there are any files in the intermediate folders and starts deleting them according to
	 * the specified deletion factor
	 */
	public void run() {
		noOfFiles=0;
			for (String name: userInputDetails.keySet()){
				value = userInputDetails.get(name);
				logger.info(name +" -> "+value);
				File[] fileList = null;
				while(true){
					File dir = new File(value.getIntermediate()); 
					fileList = dir.listFiles(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							return name.endsWith(ConstantHolder.APPEND_AFTER_COPY);
						}
					});
					if(fileList.length == value.getRopDeletionNumber())
					{
						value.setDeletionstatus(true);
						logger.info("Deletion status set to true for\t"+value.getDtsource());
						break;
					}
				}
				

				for(File file : fileList) {
					try{
						Date now = new Date();
						FileUtils.deleteDirectory(file);
						logger.info(file+" " +"was deleted on"+ " "+now);
					}
					catch(Exception e){
						logger.info("Delete operation failed");
						e.printStackTrace(); 
					}
				}
			}
			int filesRemaining=CalculateNoOfFilesRemaining(userInputDetails);
			if(filesRemaining==0)
			{
				CancelTimers();
			}
	}

	/**
	 * Calculate no of files remaining.
	 *
	 * @param userInputDetails the user input details
	 * @return the int
	 */
	private int CalculateNoOfFilesRemaining(Map<String, UserConfigConstant> userInputDetails) {
		for (String name: userInputDetails.keySet()){
			UserConfigConstant value = userInputDetails.get(name); 
			File folder=new File(value.getIntermediate());
			noOfFiles+=folder.listFiles().length;

		}
		logger.info("Total no of files in intermediate folders "+noOfFiles);
		return(noOfFiles);

	}
	private void CancelTimers(){
	logger.info("Deletion completed");
	time.cancel();
	time.purge();
	timer.cancel();
	timer.purge();
	}


}

