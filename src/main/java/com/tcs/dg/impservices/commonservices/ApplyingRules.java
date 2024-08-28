/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.impservices.commonservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.tcs.dg.constant.RuleReaderConstant;
import com.tcs.dg.constant.UserConfigConstant;
import com.tcs.dg.jaxb.produces.Mdc;

/**
 * The Class ApplyingRules.
 */
public class ApplyingRules {

	/** The position. */
	private Map<Integer, Map<Integer, List<Integer>>> counterPositionMap;
	
	/** The left pos. */
	private Map<Integer, Map<Integer, Integer>> leftPos;
	
	/**
	 * Change values.
	 *
	 * @param root the root
	 * @param userConfigConstant the user config constant
	 * @return the mdc
	 */
	public Mdc changeValues(Mdc root, UserConfigConstant userConfigConstant) {
		return applyRules(root, userConfigConstant);
	}

	/**
	 * Apply rules.
	 * 
	 * @param root
	 *            the root
	 * @return the mdc
	 */
	public Mdc applyRules(Mdc root, UserConfigConstant userConfigConstant) { 
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
	public Mdc find(Map<String, Boolean>  main, Mdc root, String name, List<String> oprand) {
		leftPos = new HashMap<>();
		counterPositionMap = new HashMap<>();

		for (int mdIndex = 0; mdIndex < root.getMd().size(); mdIndex++) {
			Map<Integer, List<Integer>> miPoistionMap = new HashMap<>();
			for (int miIndex = 0; miIndex < root.getMd().get(mdIndex).getMi().size(); miIndex++) {
				List<Integer> mtPositionList =  new ArrayList<>();
				for (String tokenRHS : main.keySet()) {
					if (root.getMd().get(mdIndex).getMi().get(miIndex).getMt().contains(tokenRHS)) {
						mtPositionList.add(root.getMd().get(mdIndex).getMi().get(miIndex).getMt().indexOf(tokenRHS));
					}
				}
				if (root.getMd().get(mdIndex).getMi().get(miIndex).getMt().contains(name.trim())) {
					//LHS 
					Map<Integer, Integer> leftposition = new HashMap<>();
					leftposition.put(miIndex, root.getMd().get(mdIndex).getMi().get(miIndex).getMt().indexOf(name));
					leftPos.put(mdIndex, leftposition);
				}
				if(!mtPositionList.isEmpty()){
					miPoistionMap.put(miIndex, mtPositionList);
				}
			}
			if(!miPoistionMap.isEmpty()){
				counterPositionMap.put(mdIndex, miPoistionMap);
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
	 * @param oprand
	 *            the oprand
	 * @return the mdc
	 * @throws ScriptException
	 *             the script exception
	 */
	public Mdc updateRules(Map<String, Boolean> main, Mdc root, List<String> oprand) throws ScriptException {

		for (Integer mdIndex : leftPos.keySet()) {
			int indexCount = 0;
			Map<Integer, Integer> leftmiposMap = leftPos.get(mdIndex);
			Map<Integer, List<Integer>> rightMiPosMap = counterPositionMap.get(mdIndex);
			for (Integer miIndex : leftmiposMap.keySet()) {

				int rpos = leftmiposMap.get(miIndex);

				List<Integer> mtList = rightMiPosMap.get(miIndex);
				for (int mvIndex = 0; mvIndex < root.getMd().get(mdIndex).getMi().get(miIndex).getMv().size(); mvIndex++) {
					String newExprssion = "";

					for (int mtIndex : mtList) {

						for (String mapRHS : main.keySet()) {
							if (root.getMd().get(mdIndex).getMi().get(miIndex).getMt().get(mtIndex).trim().equalsIgnoreCase(mapRHS)){
								String countervalue=String.valueOf(root.getMd().get(mdIndex).getMi().get(miIndex).getMv().get(mvIndex).getR().get(mtIndex)).trim();
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
						root.getMd().get(mdIndex).getMi().get(miIndex).getMv().get(mvIndex).getR().set(rpos, newFinalEq);
					} else {
						value = engine.eval(newExprssion).toString();
						Double val = Double.parseDouble(value);

						root.getMd().get(mdIndex).getMi().get(miIndex).getMv().get(mvIndex).getR().set(rpos, String.valueOf(val.intValue()));
					}

				}


			}
		}

		return root;
	}

}


