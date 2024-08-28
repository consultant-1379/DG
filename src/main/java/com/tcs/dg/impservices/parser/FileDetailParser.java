/*
 * Data Generator
 * Author: Shaifali singh
 */
package com.tcs.dg.impservices.parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import com.tcs.dg.constant.DateAndStringConstants;
import com.tcs.dg.constant.UserConfigConstant;
import com.tcs.dg.impservices.utility.DateTimeActivities;

public class FileDetailParser {
	/** The FDN. */
	private  String fdn;

	/** The end. */
	private  Date endTimeDate;

	/** The time start. */
	private  String timeStart;

	/** The stop time. */
	private  String stopTime;

	private FileDetailParser fileDetailParser=null;


	/**
	 * @return the fdn
	 */
	public String getFdn() {
		return fdn;
	}

	/**
	 * @param fdn the fdn to set
	 */
	public void setFdn(String fdn) {
		this.fdn = fdn;
	}

	/**
	 * @return the endTimeDate
	 */
	public Date getEndTimeDate() {
		return endTimeDate;
	}

	/**
	 * @param endTimeDate the endTimeDate to set
	 */
	public void setEndTimeDate(Date endTimeDate) {
		this.endTimeDate = endTimeDate;
	}

	/**
	 * @return the timeStart
	 */
	public String getTimeStart() {
		return timeStart;
	}

	/**
	 * @param timeStart the timeStart to set
	 */
	public void setTimeStart(String timeStart) {
		this.timeStart = timeStart;
	}

	/**
	 * @return the stopTime
	 */
	public String getStopTime() {
		return stopTime;
	}

	/**
	 * @param stopTime the stopTime to set
	 */
	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}

	/**
	 * This method will take file name and find attributes from that name.
	 *
	 * @param match the match
	 * @param different the different
	 * @param ropTime the rop time
	 * @param userConfigConstant the user config constant
	 * @param endtimeIndex the endtime index
	 * @param fdnIndex the fdn index
	 * @param offset the offset
	 * @return the first file name details non utc
	 */
	public FileDetailParser getFirstFileNameDetailsNonUTC(Matcher match,long different,int ropTime,UserConfigConstant userConfigConstant,int endtimeIndex,int fdnIndex,String offset)
	{   fileDetailParser=new FileDetailParser();
	fileDetailParser.setFdn(match.group(fdnIndex));
	long startTime = new DateTimeActivities().convertStringToDateWithTimeZone(
			match.group(DateAndStringConstants.getDate()) + " " + match.group(DateAndStringConstants.getStarttime())
			+ DateAndStringConstants.getSecondDefault() + DateAndStringConstants.getMillisecondDefault() + offset,userConfigConstant).getTime();
	long endTime = new DateTimeActivities().convertStringToDateWithTimeZone(
			match.group(DateAndStringConstants.getDate()) + " " + match.group(endtimeIndex)
			+ DateAndStringConstants.getSecondDefault() + DateAndStringConstants.getMillisecondDefault() + offset,userConfigConstant).getTime();
	Date roundupStart = new Date(new DateTimeActivities().convertToRoundFigureUpdated(startTime + different,ropTime));
	Date roundupEnd = new Date(new DateTimeActivities().convertToRoundFigureUpdated(endTime + different,ropTime));
	fileDetailParser.setEndTimeDate(roundupEnd);
	DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
	fileDetailParser.setTimeStart(df.format(roundupStart));
	fileDetailParser.setStopTime(df.format(roundupEnd));
	return fileDetailParser;
	}

	/**
	 * This method will take file name and find attributes from that name.
	 *
	 * @param match the match
	 * @param different the different
	 * @param ropTime the rop time
	 * @param userConfigConstant the user config constant
	 * @return the first file name details utc
	 */
	public FileDetailParser getFirstFileNameDetailsUTC(Matcher match,long different,int ropTime,UserConfigConstant userConfigConstant)
	{   fileDetailParser=new FileDetailParser();
	fileDetailParser.setFdn(match.group(DateAndStringConstants.getUtcfdn()));
	long startTime = new DateTimeActivities().convertStringToDateWithUTC(match.group(DateAndStringConstants.getDate())+" "+match.group(DateAndStringConstants.getStarttime())+DateAndStringConstants.getSecondDefault()+DateAndStringConstants.getMillisecondDefault(),userConfigConstant).getTime();
	long endTime = new DateTimeActivities().convertStringToDateWithUTC(match.group(DateAndStringConstants.getDate())+" "+match.group(DateAndStringConstants.getUtcendtime())+DateAndStringConstants.getSecondDefault()+DateAndStringConstants.getMillisecondDefault(),userConfigConstant).getTime();
	Date roundupStart = new Date(new DateTimeActivities().convertToRoundFigureUpdated(startTime+different, ropTime));
	Date roundupEnd =  new Date(new DateTimeActivities().convertToRoundFigureUpdated(endTime+different, ropTime));
	fileDetailParser.setEndTimeDate(roundupEnd);
	DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
	fileDetailParser.setTimeStart(df.format(roundupStart));
	fileDetailParser.setStopTime(df.format(roundupEnd));
	return fileDetailParser;
	}
}
