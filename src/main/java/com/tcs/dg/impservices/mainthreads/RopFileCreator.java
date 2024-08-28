/*
 * Data Generator
 * Author: Shaifali Singh
 */
package com.tcs.dg.impservices.mainthreads;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tcs.dg.constant.ConstantHolder;
import com.tcs.dg.constant.DateAndStringConstants;
import com.tcs.dg.constant.UserConfigConstant;
import com.tcs.dg.impservices.utility.DateTimeActivities;
import com.tcs.dg.iservices.ImpFileParser;
import com.tcs.dg.jaxb.appconfig.AppConfiguration;

/**
 * The Class InputFileHolder.
 */
public class RopFileCreator {

	/**
	 * This method is used to make data as per repetition factor that present inside UserSpecification.xml
	 * If in user input value of repetition factor is 0 than it will only process according to number of ROP inside input location.
	 * @param rep
	 * @param input
	 */
	public void inputMaker(UserConfigConstant userConfigConstant, String[] input){
		int rep=userConfigConstant.getRepetition();
		Map<Integer, String[]> tempRopMapper=new LinkedHashMap<>();
		if(rep>0){
			for(int i = 0; i <rep; i++){
				tempRopMapper.put(i,input);
			}
		}
		else{
			tempRopMapper.put(rep,input);
		}

		SortedRopCreator(userConfigConstant,tempRopMapper);
	}

	public void SortedRopCreator(UserConfigConstant userConfigConstant,Map<Integer, String[]> TempRopMapper)
	{   
	Map<Integer,Map<Long, List<String>>> finalRopMapper=new LinkedHashMap<>();
	Map<Long, List<String>> ropToFileMap=new LinkedHashMap<>();
	for(int ropFactor:TempRopMapper.keySet()){
		if(finalRopMapper.isEmpty()){
			List<String> ropfileList=new ArrayList<>();
			String configFileLoc = ConstantHolder.getAPPPATH() + File.separator + ConstantHolder.getAppconfig();
			AppConfiguration appConfig = ImpFileParser.parseAppConfigInput(configFileLoc);
			String pattern ;
			for(String filename: TempRopMapper.get(ropFactor)){
				if(userConfigConstant.getTimezone().equalsIgnoreCase("NONUTC")){
					pattern =appConfig.getDataSource().get(UserConfigConstant.getDataPosition().get(userConfigConstant.getDtsource())).getRelease()
							.getFileNamePattern();
				}else
				{
					pattern = appConfig.getDataSource().get(UserConfigConstant.getDataPosition().get(userConfigConstant.getDtsource())).getRelease().getUTCPattern();
				}
				Pattern value = Pattern.compile(pattern);
				Matcher match = value.matcher(filename);
				if(match.find()){
					long startTime = new DateTimeActivities().convertStringToDateWithTimeZone(
							match.group(DateAndStringConstants.getDate()) + " " + match.group(DateAndStringConstants.getStarttime())
							+ DateAndStringConstants.getSecondDefault() + DateAndStringConstants.getMillisecondDefault(),userConfigConstant).getTime();
					if(!ropToFileMap.keySet().contains(startTime))
					{
						ropfileList=new ArrayList<>();
						ropfileList.add(filename);
					}
					else
					{
						ropfileList.add(filename);
					}
					ropToFileMap.put(startTime, ropfileList);
				}
			}
			finalRopMapper.put(ropFactor, ropToFileMap);
		}
		else
		{
			finalRopMapper.put(ropFactor, ropToFileMap);
		}
	}
	userConfigConstant.setRopToFileMap(finalRopMapper);
	}
}
