/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.impservices.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.tcs.dg.constant.DateAndStringConstants;
import com.tcs.dg.constant.UserConfigConstant;
import com.tcs.dg.impservices.mainthreads.ThreadPoolService;
import com.tcs.dg.iservices.IDateTimeHandler;

/**
 * The Class DateTimeActivities.
 */
public class DateTimeActivities implements IDateTimeHandler{

	
	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(DateTimeActivities.class);
	
	/* (non-Javadoc)
	 * @see com.tcs.dg.iservices.IDateTimeHandler#getTimeDifference(long)
	 */
	@Override
	public long getTimeDifference(long fileDate) throws ParseException {
		 DateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmssSSSZ");
		 Date now = new Date();
		 String strDate = formatter.format(now);
		 return formatter.parse(strDate).getTime() - fileDate;
	}
	
	
	/* (non-Javadoc)
	 * @see com.tcs.dg.iservices.IDateTimeHandler#getTimeDifference(long)
	 */
	@Override
	public long getTimeDifferenceUTC(long fileDate) throws ParseException {
		 DateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmssSSS");
		 Date now = new Date();
		 String strDate = formatter.format(now);
		 return formatter.parse(strDate).getTime() - fileDate;
	}
	
	
	/* (non-Javadoc)
	 * @see com.tcs.dg.iservices.IDateTimeHandler#getTwoFileTimeDifference(long, long)
	 */
	@Override
	public long getTwoFileTimeDifference(long firstfileDate, long secondfileDate) throws ParseException {
		return secondfileDate - firstfileDate;
	}
	
	
	/* (non-Javadoc)
	 * @see com.tcs.dg.iservices.IDateTimeHandler#convertStringToDateWithTimeZone(java.lang.String)
	 * This method is used to parse string date to date object.
	 */
	@Override
    public Date convertStringToDateWithTimeZone(String dateString,UserConfigConstant userConfigConstant){
        Date selectedDate;
        DateFormat formatter;
        if(dateString.contains("Z")){
        formatter = new SimpleDateFormat("yyyyMMdd HHmmssSSSZ");
        }else{
        formatter = new SimpleDateFormat("yyyyMMdd HHmmssSSS");
        }
        try{
            selectedDate = formatter.parse(dateString);
        } catch(ParseException e){
            logger.info("Could not parse string date :" + dateString + " to date object.");
            return null;
        }
        return selectedDate;
    }
	
	/* (non-Javadoc)
	 * @see com.tcs.dg.iservices.IDateTimeHandler#convertStringToDateWithTimeZone(java.lang.String)
	 */
	@Override
	 public Date convertStringToDateWithUTC(String dateString,UserConfigConstant userConfigConstant){
	        Date selectedDate;
	        DateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmssSSS");
	        try{
	            selectedDate = formatter.parse(dateString);
	        } catch(ParseException e){
	            System.out.println("Could not parse string date :" + dateString + " to date object.");
	            return null;
	        }
	        return selectedDate;
	    }
	 /* (non-Javadoc)
 	 * @see com.tcs.dg.iservices.IDateTimeHandler#convertRopStringToDate(java.lang.String)
 	 */
	@Override
    public Date convertRopStringToDate(String dateString){
        Date selectedDate;
        DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        try{
            selectedDate = formatter.parse(dateString);
        } catch(ParseException e){
        	logger.info("Could not parse string date :" + dateString + " to date object.");
            return null;
        }
        return selectedDate;
    }

    /* (non-Javadoc)
     * @see com.tcs.dg.iservices.IDateTimeHandler#getTimeZoneOffsetInLong()
     */
	@Override
    public String getTimeZoneOffset(){
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmssSSSZ");
		String offset = formatter.format(new Date());
		char [] off = new char[5];
		for(int i=1; i<=off.length;i++){
			off[i-1] = offset.charAt(offset.length()-i);
		}
		StringBuilder a = new StringBuilder(new String(off));
        return a.reverse().toString();
    }


    /* (non-Javadoc)
     * @see com.tcs.dg.iservices.IDateTimeHandler#convertToRoundFigureUpdated(long, int)
     */
	@Override
    public long convertToRoundFigureUpdated(long dateInMillis, int ropInterval){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateInMillis);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        int remainMin = minute % ropInterval;
        int minutesToAdd = ropInterval - remainMin - 1;
        int secondsToAdd = 60 - second;
        dateInMillis = dateInMillis + minutesToAdd * DateAndStringConstants.getOneMinuteMillisecs() + secondsToAdd * DateAndStringConstants.getOneSecondMillisecs()- ropInterval * DateAndStringConstants.getOneMinuteMillisecs();
        return dateInMillis;
    }
	
	/**
	 * Parses the date.
	 *
	 * @param date the date
	 * @return the string
	 */
	public String radioParseDate(String date,UserConfigConstant userConfigConstant,String offset) {
		// 201606011245
		String mainDate = date.substring(0, 8);
		String offsetSplited=null;
		try {
			String year = mainDate.substring(0,4) ;
			String month = mainDate.substring(4,6) ;
			String dat = mainDate.substring(6,8) ;
			// output date format
			//SimpleDateFormat dFormatFinal = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
			mainDate =  year+"-"+month+"-"+dat +"T" ;
			String hrs = date.substring(8,10) ;
			String min = date.substring(10,12) ;
			String sec = "00" ;
			//SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
			String time = hrs+":"+min+":"+sec ;
			//	Date date = dFormat.parse(strDate);
			//2016-02-23T10:00:00+01:00
			if(UserConfigConstant.isOffset()){
				return mainDate+time+"Z";
			}
			if (!userConfigConstant.getTimezone().equalsIgnoreCase("UTC")) {
			offsetSplited=offset.substring(0,3);
			offsetSplited = offsetSplited+":" +offset.substring(3);
			}
			if (!userConfigConstant.getTimezone().equalsIgnoreCase("UTC")) {
			return mainDate+time+offsetSplited;
			}
			else
			{
			return mainDate+time+"+00:00";
			}
			
		} catch (Exception e) {
			logger.error("Error in parsing date for Radio file");

		}
		return "";
	}
	public static void convertFileTimeToMillis(long timeDifference, String fileTime, UserConfigConstant userConfigConstant, Map<String, UserConfigConstant> userInputDetails) throws ParseException, InterruptedException {
    	DateFormat format = new SimpleDateFormat("yyyyMMdd HHmmssSSS");
    	Date date = format.parse(fileTime);
    	long millis = date.getTime();
    	ThreadPoolService.addTimeDifference(timeDifference,millis,userConfigConstant,userInputDetails);
		
	}

}
