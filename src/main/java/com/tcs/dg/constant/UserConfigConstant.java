/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class UserConfigConstant.
 */
public class UserConfigConstant {
	
	/** The offset. */
	private static boolean offset = false ;
	
	/** The inputs. */
	private  String inputs="";
	
	/** The output. */
	private String output="";
	
	/** The intermediate. */
	private String intermediate="";
	
	/** The repetition. */
	private int repetition;
	
	/** The timezone. */
	private String timezone="";
	
	/** The roptime. */
	private int roptime;
	
	/** The expectedcount. */
	private int expectedcount;
	
	/** The filetype. */
	private String filetype="";
	
	/** The dtsource. */
	private String dtsource;
	
	/** The rules. */
	private List<String> rules;
	
	/** The rules details. */
	private List<RuleReaderConstant> rulesDetails;
	
	/** The data position. */
	private final static Map<String, Integer> dataPosition=new HashMap<>();
	
	/** The deletion factor. */
	private static int deletionFactor;
	
	/** The deletion factor. */
	private int ropDeletionNumber;
	
	private boolean deletionstatus=false;
	
	private Map<Integer,Map<Long, List<String>>> ropToFileMap;
	
	/**
	 * @return the offset
	 */
	public static boolean isOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public static void setOffset(boolean offset) {
		UserConfigConstant.offset = offset;
	}

	public String getInputs() {
		return inputs;
	}

	public void setInputs(String inputs) {
		this.inputs = inputs;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getIntermediate() {
		return intermediate;
	}

	public void setIntermediate(String intermediate) {
		this.intermediate = intermediate;
	}

	public int getRepetition() {
		return repetition;
	}

	public void setRepetition(int repetition) {
		this.repetition = repetition;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public int getRoptime() {
		return roptime;
	}

	public void setRoptime(int roptime) {
		this.roptime = roptime;
	}

	public int getExpectedcount() {
		return expectedcount;
	}

	public void setExpectedcount(int expectedcount) {
		this.expectedcount = expectedcount;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getDtsource() {
		return dtsource;
	}

	public void setDtsource(String dtsource) {
		this.dtsource = dtsource;
	}

	public List<String> getRules() {
		return rules;
	}

	public void setRules(List<String> rules) {
		this.rules = rules;
	}

	/**
	 * @return the rulesDetails
	 */
	public List<RuleReaderConstant> getRulesDetails() {
		return rulesDetails;
	}

	/**
	 * @param rulesDetails the rulesDetails to set
	 */
	public void setRulesDetails(List<RuleReaderConstant> rulesDetails) {
		this.rulesDetails = rulesDetails;
	}

	/**
	 * @return the dataPosition
	 */
	public static Map<String, Integer> getDataPosition() {
		dataPosition.put("WRAN-LTE", 0);
		dataPosition.put("RADIO", 1);
		dataPosition.put("SGSNMME(COM/ESIM)", 2);
		dataPosition.put("SGSN-MME", 2);
		dataPosition.put("LTE-Event-Statistics",3);
		dataPosition.put("IPWORKS", 4);
		return dataPosition;
	}

	/**
	 * @return the ropToFileMap
	 */
	public Map<Integer, Map<Long, List<String>>> getRopToFileMap() {
		return ropToFileMap;
	}

	/**
	 * @param ropToFileMap the ropToFileMap to set
	 */
	public void setRopToFileMap(Map<Integer, Map<Long, List<String>>> ropToFileMap) {
		this.ropToFileMap = ropToFileMap;
	}


	public boolean isDeletionstatus() {
		return deletionstatus;
	}

	public void setDeletionstatus(boolean deletionstatus) {
		this.deletionstatus = deletionstatus;
	}

	/**
	 * @return the deletionFactor
	 */
	public static int getDeletionFactor() {
		return deletionFactor;
	}

	/**
	 * @param deletionFactor the deletionFactor to set
	 */
	public static void setDeletionFactor(int deletionFactor) {
		UserConfigConstant.deletionFactor = deletionFactor;
	}

	/**
	 * @return the ropDeletionNumber
	 */
	public int getRopDeletionNumber() {
		return ropDeletionNumber;
	}

	/**
	 * @param ropDeletionNumber the ropDeletionNumber to set
	 */
	public void setRopDeletionNumber(int ropDeletionNumber) {
		this.ropDeletionNumber = ropDeletionNumber;
	}

	
}
