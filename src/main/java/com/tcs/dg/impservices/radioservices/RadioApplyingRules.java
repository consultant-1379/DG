/*
 * Data Generator
 * Author: Shaifali Singh
 */
package com.tcs.dg.impservices.radioservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.tcs.dg.constant.RuleReaderConstant;
import com.tcs.dg.constant.UserConfigConstant;
import com.tcs.dg.jaxb.radio.MeasCollecFile;


/**
 * The Class ApplyingRules.
 */
public class RadioApplyingRules {

	/** The position. */
	private Map<Integer, Map<Integer, List<Integer>>> counterPositionMap;
	private Map<Integer, Map<Integer, Integer>> leftPos;

	/**
	 * Change values.
	 * 
	 * @param root
	 *            the root
	 * @return the mdc
	 */
	public MeasCollecFile changeValues(MeasCollecFile root,UserConfigConstant userConfigConstant) {
		return applyRules(root, userConfigConstant);

	}

	/**
	 * Apply rules.
	 * 
	 * @param root
	 *            the root
	 * @return the mdc
	 */
	public MeasCollecFile applyRules(MeasCollecFile root, UserConfigConstant userConfigConstant) {

		for (RuleReaderConstant rulDetails : userConfigConstant.getRulesDetails()) {
			List<String> oprand;
			Map<String, Boolean> ruleRHS;
			ruleRHS=rulDetails.getRuleRHS();
			String ruleLHS=rulDetails.getRuleLHS();
			oprand=rulDetails.getOprand();

			find(ruleRHS, root, ruleLHS, oprand);
		}
		return root;
	}
	/**
	 * Find.
	 * 
	 * @param main
	 *            the main
	 * @param root
	 *            the root
	 * @param name
	 *            the name
	 * @param oprand
	 *            the oprand
	 * @return the mdc
	 */
	public MeasCollecFile find(Map<String, Boolean> main, MeasCollecFile root, String name, List<String> oprand) {
		leftPos = new HashMap<>();
		counterPositionMap = new HashMap<>();
		for (int measDataIndex = 0; measDataIndex < root.getMeasData().size(); measDataIndex++) {
			Map<Integer, List<Integer>> miPoistionMap = new HashMap<>();
			Map<Integer, Integer> leftposition = new HashMap<>();
			for (int measInfoIndex = 0; measInfoIndex < root.getMeasData().get(measDataIndex).getMeasInfo().size(); measInfoIndex++) {
				List<Integer> mtPositionList =  new ArrayList<>();
				for (int measTypeIndex = 0; measTypeIndex < root.getMeasData().get(measDataIndex).getMeasInfo().get(measInfoIndex).getMeasType().size(); measTypeIndex++){
				for (String tokenRHS : main.keySet()) {
					
					if (root.getMeasData().get(measDataIndex).getMeasInfo().get(measInfoIndex).getMeasType().get(measTypeIndex).getValue().trim().equals(tokenRHS)) {	
						mtPositionList.add(measTypeIndex);
					}
				}
				if (root.getMeasData().get(measDataIndex).getMeasInfo().get(measInfoIndex).getMeasType().get(measTypeIndex).getValue().trim().equalsIgnoreCase(name.trim())) {
					leftposition.put(measInfoIndex, measTypeIndex);
				}
				}
				if (!mtPositionList.isEmpty()) {
					miPoistionMap.put(measInfoIndex, mtPositionList);
				}
			}
			if(!miPoistionMap.isEmpty()){
				counterPositionMap.put(measDataIndex, miPoistionMap);
			}
			if (!leftposition.isEmpty()) {
				leftPos.put(measDataIndex, leftposition);
			}
		}
		try {
			if (!counterPositionMap.isEmpty()) {
				return updateRules(main, root, oprand);
			}
		} catch (Exception ex) {
			System.out.println("Script Exception" + ex);
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Update rules.
	 * 
	 * @param main
	 *            the main
	 * @param root
	 *            the root
	 * @param left
	 *            the left
	 * @param oprand
	 *            the oprand
	 * @return the mdc
	 * @throws ScriptException
	 *             the script exception
	 */
	public MeasCollecFile updateRules(Map<String, Boolean> main, MeasCollecFile root, List<String> oprand) throws ScriptException {

		for (Integer measDataIndex : leftPos.keySet()) {
			Map<Integer, Integer> leftmiposMap = leftPos.get(measDataIndex);
			Map<Integer, List<Integer>> rightMiPosMap = counterPositionMap.get(measDataIndex);
			for (Integer measInfoIndex : leftmiposMap.keySet()) {
				int rpos = leftmiposMap.get(measInfoIndex);

				List<Integer> mtList = rightMiPosMap.get(measInfoIndex);
				for (int measValIn = 0; measValIn < root.getMeasData().get(measDataIndex).getMeasInfo().get(measInfoIndex).getMeasValue().size(); measValIn++) {
					String newExprssion = "";
					int indexCount = 0;

					for (int measTypeIndex : mtList) {

						for (String mapRHS : main.keySet()) {
							if (root.getMeasData().get(measDataIndex).getMeasInfo().get(measInfoIndex).getMeasType().get(measTypeIndex).getValue().equalsIgnoreCase(mapRHS)){
								String countervalue=String.valueOf(root.getMeasData().get(measDataIndex).getMeasInfo().get(measInfoIndex).getMeasValue().get(measValIn).getR().get(measTypeIndex).getValue()).trim();
								if(indexCount >=main.size()-1){
									if(countervalue.contains(","))
									{
										newExprssion = (newExprssion.trim() + "(" +countervalue+ ")").trim();
										indexCount++;
									}
									else
									{
										newExprssion = newExprssion.trim() + countervalue;
										indexCount++;
									}
								}
								else
								{
									if(countervalue.contains(","))
									{
										newExprssion = newExprssion.trim() + "("+ countervalue+ ")" + oprand.get(indexCount).trim();
										indexCount++;
									}
									else
									{
										newExprssion = newExprssion.trim() + countervalue+ oprand.get(indexCount).trim();
										indexCount++;
									}
								}	
							}
							else if(main.get(mapRHS).equals(true)){
								if(indexCount >= main.size()-1){
									newExprssion = newExprssion.trim() + mapRHS.trim() ;
									indexCount++;
								}
								else
								{
									newExprssion = newExprssion.trim() + mapRHS.trim() + oprand.get(indexCount).trim();
									indexCount++;
								}
							}

						}
					}
					String newFinalEq = "";

					String newEq = newExprssion;
					if(newEq.contains(","))
					{
						newEq = newExprssion.substring(newEq.indexOf("(")+1,newExprssion.lastIndexOf(")"));
						String finalEq [] = newEq.split(",");
						for(int index=0; index < finalEq.length; index++)
						{
							char opp = newExprssion.charAt(newExprssion.lastIndexOf(")")+1);
							int factor = Integer.parseInt(newExprssion.substring(newExprssion.lastIndexOf(")")+2));
							int result = Integer.parseInt(finalEq[index].trim()); 
							switch(opp)
							{
							case '*':
								newFinalEq = newFinalEq +(result*factor)+",";

								break;
							case '+':
								newFinalEq = newFinalEq+(result+factor)+",";
								break;
							case '-':
								newFinalEq = newFinalEq+(result-factor)+",";
								break;
							case '/':
								newFinalEq = newFinalEq+(result/factor)+",";
								break;
							}	
						}
						newFinalEq = newFinalEq.substring(0, newFinalEq.length() - 1);
					}


					ScriptEngineManager mgr = new ScriptEngineManager();
					ScriptEngine engine = mgr.getEngineByName("JavaScript");
					String value;

					if (!newFinalEq.equalsIgnoreCase("")) {
						//For Array Logic
						root.getMeasData().get(measDataIndex).getMeasInfo().get(measInfoIndex).getMeasValue().get(measValIn).getR().get(rpos).setValue(newFinalEq);
					} else {
						value = engine.eval(newExprssion).toString();
						Double val = Double.parseDouble(value);

						root.getMeasData().get(measDataIndex).getMeasInfo().get(measInfoIndex).getMeasValue().get(measValIn).getR().get(rpos).setValue(String.valueOf(val.intValue()));
					}

				}

			}
		}

		return root;
	}

}


