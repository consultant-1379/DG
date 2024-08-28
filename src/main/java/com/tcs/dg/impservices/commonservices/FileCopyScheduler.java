/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.impservices.commonservices;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.io.FileUtils;

import com.tcs.dg.constant.ConstantHolder;
import com.tcs.dg.constant.UserConfigConstant;

/**
 * The Class FileCopyScheduler.
 */
public class FileCopyScheduler {

	/** The timer. */
	private Timer timer;

	/** The folder name. */
	private String folderName;
	
	/** The folder userConfigConstant. */
	private UserConfigConstant userConfigConstant;

	/**
	 * This is used to instantiate JAVA scheduler as per ROP time. 
	 * @param end
	 * @param name
	 * @param userConfigConstant
	 */
	public FileCopyScheduler(Date end, String name,
			UserConfigConstant userConfigConstant) {
		this.folderName = name;
		this.userConfigConstant = userConfigConstant;
		timer = new Timer();
		timer.schedule(new CopyTask(), end);
	}

	/**
	 * The Class CopyTask.
	 */
	class CopyTask extends TimerTask {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		public void run() {
			String[] files = new File(userConfigConstant.getIntermediate()
					+ File.separator + folderName).list();
			if (null != files) {
				File source = new File(userConfigConstant.getIntermediate()
						+ File.separator + folderName);
				File destination = new File(userConfigConstant.getOutput());
				try {
					FileUtils.copyDirectory(source, destination);
					//Folder renaming logic
			        //Directory with new name
			        File newName = new File(userConfigConstant.getIntermediate()
							+ File.separator + folderName+ ConstantHolder.APPEND_AFTER_COPY);
			        source.renameTo(newName);
			        
			        //Folder renaming logic ends
					System.out.println("\nFiles copied Sucessfully");
				} catch (IOException e) {
					System.out.println("\nFiles Copying Exception");
				}
				timer.cancel();
			}
		}
	}
}
