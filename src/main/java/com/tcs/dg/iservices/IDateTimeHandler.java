/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.iservices;

import java.text.ParseException;
import java.util.Date;

import com.tcs.dg.constant.UserConfigConstant;

// TODO: Auto-generated Javadoc
/**
 * The Interface IDateTimeHandler.
 */
public interface IDateTimeHandler {

	/**
	 * Gets the time difference.
	 *
	 * @param date the date
	 * @return the time difference
	 * @throws ParseException the parse exception
	 */
	public long getTimeDifference(long date)throws ParseException;
	
	/**
	 * Convert string to date with time zone.
	 *
	 * @param dateString the date string
	 * @return the date
	 */
	public Date convertStringToDateWithTimeZone(String dateString,UserConfigConstant userConfigConstant);
	
	/**
	 * Convert rop string to date.
	 *
	 * @param dateString the date string
	 * @return the date
	 */
	 public Date convertRopStringToDate(String dateString);
	 
	 /**
 	 * Convert to round figure updated.
 	 *
 	 * @param dateInMillis the date in millis
 	 * @param ropInterval the rop interval
 	 * @return the long
 	 */
 	public long convertToRoundFigureUpdated(long dateInMillis, int ropInterval);
 	
 	/**
	  * Gets the time zone offset.
	  *
	  * @return the time zone offset
	  */
 	public String getTimeZoneOffset();
 	
 	/**
	  * Convert string to date with utc.
	  *
	  * @param dateString the date string
	  * @return the date
	  */
 	public Date convertStringToDateWithUTC(String dateString,UserConfigConstant userConfigConstant);
 	
 	/**
	  * Gets the time difference utc.
	  *
	  * @param fileDate the file date
	  * @return the time difference utc
	  * @throws ParseException the parse exception
	  */
 	public long getTimeDifferenceUTC(long fileDate) throws ParseException;
 	
 	/**
 	 * @param firstfileDate
 	 * @param econdfileDate
 	 * @return
 	 * @throws ParseException
 	 */
 	public long getTwoFileTimeDifference(long firstfileDate, long econdfileDate) throws ParseException;
	
}
