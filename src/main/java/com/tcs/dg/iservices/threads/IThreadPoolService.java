/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.iservices.threads;

import java.text.ParseException;
import java.util.Map;

import com.tcs.dg.constant.UserConfigConstant;


// TODO: Auto-generated Javadoc
/**
 * The Interface IThreadPoolService.
 */
public interface IThreadPoolService {

	/**
	 * Pool creator.
	 *
	 * @param inputs the inputs
	 * @param datasourceName the datasource name
	 * @param url the url
	 * @param out the out
	 * @param ropTime the rop time
	 * @param timeZone the time zone
	 * @throws ParseException the parse exception
	 */
	public void poolCreator(String [] inputs,UserConfigConstant userConfigConstant,Map<String, UserConfigConstant> userInputDetails) throws ParseException;
	
	/**
	 * Gets the first file name details.
	 *
	 * @param name the name
	 * @param datasourceName the datasource name
	 * @return the first file name details
	 */
	public long getFirstFileNameDetails(String name,UserConfigConstant userConfigConstant);
	
	/**
	 * Gets the first file name details utc.
	 *
	 * @param name the name
	 * @param datasourceName the datasource name
	 * @return the first file name details utc
	 */
	public long getFirstFileNameDetailsUTC(String name,UserConfigConstant userConfigConstant);
}
