/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.constant;

import java.io.File;


/**
 * The Class ConstantHolder.
 */
public class ConstantHolder {

    /** The Constant CONFIGURATION. */
    private final static String CONFIGURATION = "config.properties";

    /** The apppath. */
    private static String APPPATH = "";

    /** The configloc. */
    private static String CONFIGLOC = "config" + File.separator + "appConfigXML";

    /** The Constant APPCONFIG. */
    private final static String APPCONFIG = CONFIGLOC + File.separator + "AppConfiguration.xml";

    /** The Constant USRCONFIG. */
    private final static String USRCONFIG = CONFIGLOC + File.separator + "UserInputSpecification.xml";

    /** The Constant RULE. */
    private final static String RULE = CONFIGLOC + File.separator + "Rules.xml";
    
    /** The Constant NODE_MULTI. */
    private final static int NODE_MULTI = 1;
    
    /** The Constant String appended after file copy */
    public static final String APPEND_AFTER_COPY="_PROCESSED";
    
    /** The Constant RULE_UPDATION_REQUIRED. */
    private final static String RULE_UPDATION_REQUIRED = "isRulesUpdationRequired";

	/**
	 * @return the aPPPATH
	 */
	public static String getAPPPATH() {
		return APPPATH;
	}

	/**
	 * @param aPPPATH the aPPPATH to set
	 */
	public static void setAPPPATH(String aPPPATH) {
		APPPATH = aPPPATH;
	}

	/**
	 * @return the cONFIGLOC
	 */
	public static String getCONFIGLOC() {
		return CONFIGLOC;
	}

	
	/**
	 * @return the configuration
	 */
	public static String getConfiguration() {
		return CONFIGURATION;
	}

	/**
	 * @return the appconfig
	 */
	public static String getAppconfig() {
		return APPCONFIG;
	}

	/**
	 * @return the usrconfig
	 */
	public static String getUsrconfig() {
		return USRCONFIG;
	}

	/**
	 * @return the rule
	 */
	public static String getRule() {
		return RULE;
	}

	/**
	 * @return the nodeMulti
	 */
	public static int getNodeMulti() {
		return NODE_MULTI;
	}

	/**
	 * @return the ruleUpdationRequired
	 */
	public static String getRuleUpdationRequired() {
		return RULE_UPDATION_REQUIRED;
	}
}
