/*
 * Data Generator
 * Author: Mahipal Jain
 * 
 */
package com.tcs.dg.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NumberUtils;
import org.apache.log4j.Logger;

import com.tcs.dg.constant.ConstantHolder;
import com.tcs.dg.constant.RuleReaderConstant;
import com.tcs.dg.constant.UserConfigConstant;
import com.tcs.dg.helper.PropertiesReader;
import com.tcs.dg.iservices.ImpFileParser;
import com.tcs.dg.jaxb.rules.Rules;
import com.tcs.dg.jaxb.userconfig.UserInputSpecification;
import com.tcs.dg.jaxb.userconfig.UserInputSpecification.DataSources.DataSource.GeneralInfo;

/**
 * The Class JobDistributor.
 * This class is the entry point of this project
 * This is responsible for creating userInput object by parsing userInputSpecification.xml 
 * This will handle list of data source present inside userInputSpecification.xml through multi threading.
 */
@SuppressWarnings("deprecation")
public class JobDistributor {

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(JobDistributor.class);
	/**
	 * The main method.
	 * This method is the entry point of this project
	 * This is responsible for creating userInput object by parsing userInputSpecification.xml
	 * This will handle list of data source by creating a thread pool and assign a single thread for each datasource. 
	 * This will call the runnable task  XMLDataGenerator
	 * @param args the arguments
	 * @throws IOException 
	 */
	public static void main(String args[]) throws IOException{
		Properties pro = PropertiesReader.getProperties(ConstantHolder.getConfiguration());
		String isRulesUpdationRequired =pro.getProperty(ConstantHolder.getRuleUpdationRequired());
		ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(pro.getProperty("ThreadPoolSize")));

		if(null != args[0]){
			ConstantHolder.setAPPPATH(args[0]);
		} else{
			logger.info("......Please provide configuration file location....");
			System.exit(0);
		}
		String userConfig = ConstantHolder.getAPPPATH() + File.separator + ConstantHolder.getUsrconfig();
		UserInputSpecification userInputSpecification = ImpFileParser.parseUserConfigInput(userConfig);
		Map<String, UserConfigConstant> userInputDetails = new HashMap<>();
		for(int i = 0; i < userInputSpecification.getDataSources().getDataSource().size(); i++)
		{
			Boolean flag=false;
			UserConfigConstant userConfigObj=new UserConfigConstant();
			GeneralInfo generalInfo= userInputSpecification.getDataSources().getDataSource().get(i).getGeneralInfo();
			String dataSourceName = userInputSpecification.getDataSources().getDataSource().get(i).getName();
			userConfigObj.setDtsource(dataSourceName);
			userConfigObj.setInputs(generalInfo.getInputFileLocation());
			userConfigObj.setOutput(generalInfo.getOutputFileLocation());
			userConfigObj.setIntermediate(generalInfo.getIntermediateLoc());
			if(!flag)
			{
				if(userConfigObj.getIntermediate()!=null)
				{
					flag=true;
					File dir = new File(userConfigObj.getIntermediate());
					if(dir.exists() && dir.isDirectory()) {
						FileUtils.cleanDirectory(dir);
					}
				}
			}
			userConfigObj.setRepetition(generalInfo.getRepetition());
			userConfigObj.setRoptime(generalInfo.getROPTime());
			userConfigObj.setTimezone(generalInfo.getTimeZone());
			userConfigObj.setFiletype(generalInfo.getFileType());
			userConfigObj.setExpectedcount(generalInfo.getNumberOfNode());
			UserConfigConstant.setDeletionFactor(userInputSpecification.getDeletionFactor());
			int ropDeletionNumber=((UserConfigConstant.getDeletionFactor()*60)/userConfigObj.getRoptime());
			System.out.println("job distributor"+ropDeletionNumber);
			userConfigObj.setRopDeletionNumber(ropDeletionNumber);
			userInputDetails.put(dataSourceName, userConfigObj);
			if(isRulesUpdationRequired.equalsIgnoreCase("YES"))
			{
				ruleSetter(userConfigObj);
			}
			logger.info("\nCompleted User Inputs Initialization for XML processing for "+userConfigObj.getDtsource());
			executor.execute(new XMLDataGenerator(userConfigObj,userInputDetails));
		}
		logger.info("\nJob thread Completed.");
		executor.shutdown();
	}

	/**
	 * Rule setter.
	 *This method read the rules.xml file and sets its value to  UserConfigConstant object.
	 * @param userConfigObj the user config obj
	 */

	public static void ruleSetter(UserConfigConstant userConfigObj) {
		String ruleConfig = ConstantHolder.getAPPPATH() + File.separator + ConstantHolder.getRule();
		Rules ruleObj = ImpFileParser.parseRules(ruleConfig);
		for(int j = 0; j < ruleObj.getDataSources().getDataSource().size(); j++){
			if(null != ruleObj.getDataSources().getDataSource().get(j).getRule()&& ruleObj.getDataSources().getDataSource().get(j).getName().equals(userConfigObj.getDtsource()) ){
				userConfigObj.setRules(ruleObj.getDataSources().getDataSource().get(j).getRule());
				List<RuleReaderConstant> rulesDetails  = new ArrayList<>();
				for (String str : userConfigObj.getRules()) {
					RuleReaderConstant ruleReaderObj= new RuleReaderConstant();
					Map<String, Boolean> rhsoperatorMap = new LinkedHashMap<>();
					List<String> operandList = new ArrayList<>();
					String[] token = str.split("=");
					StringTokenizer splitRHS = new StringTokenizer(token[1], "+-*/", true);
					while (splitRHS.hasMoreTokens()) {
						String rightToken = splitRHS.nextToken().trim();
						if ("+-/*".contains(rightToken)) {
							operandList.add(rightToken);
						} else {
							rhsoperatorMap.put(rightToken,NumberUtils.isNumber(rightToken));
						}
					}
					ruleReaderObj.setOprand(operandList);
					ruleReaderObj.setRuleRHS(rhsoperatorMap);
					ruleReaderObj.setRuleLHS(token[0]);
					rulesDetails.add(ruleReaderObj);	
				}
				userConfigObj.setRulesDetails(rulesDetails);
				logger.info("\nSetting Rules for "+userConfigObj.getDtsource());
			}
		}
	}
}
