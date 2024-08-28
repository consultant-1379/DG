/*
 * Data Generator
 * Author: Shaifali Singh
 */
package com.tcs.dg.constant;


import java.util.List;
import java.util.Map;


/**
 * The Class RuleReader.
 */
public class RuleReaderConstant {
	
	/** The rule rhs. */
	private String ruleLHS;
	
	/** The rule lhs. */
	private Map<String, Boolean> ruleRHS;
	
	/** The oprand. */
	private List<String> oprand;

	/**
	 * @return the ruleLHS
	 */
	public String getRuleLHS() {
		return ruleLHS;
	}

	/**
	 * @param ruleLHS the ruleLHS to set
	 */
	public void setRuleLHS(String ruleLHS) {
		this.ruleLHS = ruleLHS;
	}

	/**
	 * @return the oprand
	 */
	public List<String> getOprand() {
		return oprand;
	}

	/**
	 * @param oprand the oprand to set
	 */
	public void setOprand(List<String> oprand) {
		this.oprand = oprand;
	}

	/**
	 * @return the ruleRHS
	 */
	public Map<String, Boolean> getRuleRHS() {
		return ruleRHS;
	}

	/**
	 * @param ruleRHS the ruleRHS to set
	 */
	public void setRuleRHS(Map<String, Boolean> ruleRHS) {
		this.ruleRHS = ruleRHS;
	}

	
}
