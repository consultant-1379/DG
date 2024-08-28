/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.constant;


/**
 * The Class DataAndStringConstants.
 */
public class DateAndStringConstants {

	/** The Constant MILLISECOND_DEFAULT. */
	private final static String MILLISECOND_DEFAULT = "000";

	/** The Constant SECOND_DEFAULT. */
	private final static String SECOND_DEFAULT = "00";

	/** The Constant SECOND_DEFAULT_OFFSET. */
	private final static String SECOND_DEFAULT_OFFSET = "00Z";

	/** The one minute millisecs. */
	private final static int ONE_MINUTE_MILLISECS = 60000;

	/** The one second millisecs. */
	private final static int ONE_SECOND_MILLISECS = 1000;


	/** The Constant DATE. */
	private final static int DATE = 1;

	/** The Constant STARTTIME. */
	private final static int STARTTIME = 2;

	/** The Constant ENDTIME. */
	private final static int ENDTIME = 4;

	/** The Constant FDN. */
	private final static int FDN = 6;

	/** The Constant UTCENDTIME. */
	private final static int UTCENDTIME = 3;

	/** The Constant UTCFDN. */
	private final static int UTCFDN = 4;

	private static String TIME_OFFSET = " ";

	/** The Constant SGSN_ENDTIME. */
	private final static int SGSN_ENDTIME = 5;

	/** The Constant SGSN_FDN. */
	private final static int SGSN_FDN = 7;

	/** The Constant SGSN_FDN. */
	private final static int LTE_FDN = 5;

	/** The Constant SGSN_FDN. */
	private final static int LTE_ENDTIME = 3;

	/**
	 * @return the tIME_OFFSET
	 */
	public static String getTIME_OFFSET() {
		return TIME_OFFSET;
	}

	/**
	 * @param tIME_OFFSET the tIME_OFFSET to set
	 */
	public static void setTIME_OFFSET(String tIME_OFFSET) {
		TIME_OFFSET = tIME_OFFSET;
	}

	/**
	 * @return the millisecondDefault
	 */
	public static String getMillisecondDefault() {
		return MILLISECOND_DEFAULT;
	}

	/**
	 * @return the secondDefault
	 */
	public static String getSecondDefault() {
		return SECOND_DEFAULT;
	}

	/**
	 * @return the secondDefaultOffset
	 */
	public static String getSecondDefaultOffset() {
		return SECOND_DEFAULT_OFFSET;
	}

	/**
	 * @return the date
	 */
	public static int getDate() {
		return DATE;
	}

	/**
	 * @return the starttime
	 */
	public static int getStarttime() {
		return STARTTIME;
	}

	/**
	 * @return the endtime
	 */
	public static int getEndtime() {
		return ENDTIME;
	}

	/**
	 * @return the fdn
	 */
	public static int getFdn() {
		return FDN;
	}

	/**
	 * @return the utcendtime
	 */
	public static int getUtcendtime() {
		return UTCENDTIME;
	}

	/**
	 * @return the utcfdn
	 */
	public static int getUtcfdn() {
		return UTCFDN;
	}

	/**
	 * @return the sgsnEndtime
	 */
	public static int getSgsnEndtime() {
		return SGSN_ENDTIME;
	}

	/**
	 * @return the sgsnFdn
	 */
	public static int getSgsnFdn() {
		return SGSN_FDN;
	}

	/**
	 * @return the lteFdn
	 */
	public static int getLteFdn() {
		return LTE_FDN;
	}

	/**
	 * @return the lteEndtime
	 */
	public static int getLteEndtime() {
		return LTE_ENDTIME;
	}

	/**
	 * @return the oneMinuteMillisecs
	 */
	public static int getOneMinuteMillisecs() {
		return ONE_MINUTE_MILLISECS;
	}

	/**
	 * @return the oneSecondMillisecs
	 */
	public static int getOneSecondMillisecs() {
		return ONE_SECOND_MILLISECS;
	}

}
