/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.impservices.mainthreads;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.tcs.dg.constant.ConstantHolder;
import com.tcs.dg.constant.DateAndStringConstants;
import com.tcs.dg.constant.UserConfigConstant;
import com.tcs.dg.controller.DeletionScheduler;
import com.tcs.dg.helper.PropertiesReader;
import com.tcs.dg.impservices.utility.DateTimeActivities;
import com.tcs.dg.iservices.IConstantHelper;
import com.tcs.dg.iservices.IDateTimeHandler;
import com.tcs.dg.iservices.ImpFileParser;
import com.tcs.dg.iservices.threads.IThreadPoolService;
import com.tcs.dg.jaxb.appconfig.AppConfiguration;

/**
 * The Class ThreadPoolService.
 */
public class ThreadPoolService implements IThreadPoolService,IConstantHelper {

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(ThreadPoolService.class);

	/** The expected mapper. */
	private Map<String, Integer> expectedMapper;

	/** The counter node. */
	private Map<Long, List<String>> counterNode;

	/** The mod. */
	private int mod;

	/**The fileTime non-UTC Global */
	private String fileTimeNonUTC=null;


	/**The fileTime UTC Global */
	private String fileTimeUTC=null;


	/**boolean variable for calling scheduler only once */
	private volatile static boolean calledScheduler=false;


	/** The offSet. */
	public static String offSet = new DateTimeActivities().getTimeZoneOffset();

	private static String deletionRequired ;


	/* 
	 * This method is used to take first file name from the folder and parse that name to get time. This method is also responsible for calling node extrapolation 
	 * as well as repetition method and also find time different from current system time.
	 * This method also called thread executor for each file present inside the folder for further processing.
	 */
	@Override
	public void poolCreator(String[] inputs,UserConfigConstant userConfigConstant,Map<String, UserConfigConstant> userInputDetails) throws ParseException{
		logger.info("\nFile Processing Started for "+userConfigConstant.getDtsource() );
		expectedMapper = new LinkedHashMap<>();
		counterNode = new LinkedHashMap<>();
		long timedifferent;
		long time;
		mapperCreator(inputs,userConfigConstant);
		Properties pro = PropertiesReader.getProperties(ConstantHolder.getConfiguration());
		ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(pro.getProperty("ThreadPoolSize")));
		int leadtimefactor =Integer.parseInt(pro.getProperty("LeadTimeFactor"));
		int requiredRoptime=(leadtimefactor*60)/userConfigConstant.getRoptime();
		boolean utcCheck;
		IDateTimeHandler dateConvert = new DateTimeActivities();
		RopFileCreator ropCreatorObj=new RopFileCreator();
		ropCreatorObj.inputMaker(userConfigConstant,inputs);
		if(!userConfigConstant.getTimezone().equalsIgnoreCase("UTC")){
			timedifferent = new DateTimeActivities().getTimeDifference(getFirstFileNameDetails(inputs[0],userConfigConstant));
			timedifferent = timedifferent + userConfigConstant.getRoptime() * 60 * 1000;
			utcCheck = true;
			time = new DateTimeActivities().getTwoFileTimeDifference(getFirstFileNameDetails(inputs[0],userConfigConstant),
					getFirstFileNameDetails(inputs[inputs.length - 1],userConfigConstant));
			try {
				DateTimeActivities.convertFileTimeToMillis(timedifferent,fileTimeNonUTC,userConfigConstant,userInputDetails);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} else{
			timedifferent = new DateTimeActivities().getTimeDifferenceUTC(getFirstFileNameDetailsUTC(inputs[0],userConfigConstant));
			timedifferent = timedifferent + userConfigConstant.getRoptime() * 60 * 1000;
			utcCheck = false;
			time = new DateTimeActivities().getTwoFileTimeDifference(getFirstFileNameDetailsUTC(inputs[0],userConfigConstant),
					getFirstFileNameDetailsUTC(inputs[inputs.length - 1],userConfigConstant));
			try {
				DateTimeActivities.convertFileTimeToMillis(timedifferent,fileTimeUTC,userConfigConstant,userInputDetails);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		int ropCount=0;
		for(int RopFactor:userConfigConstant.getRopToFileMap().keySet()){
			long duration = timedifferent;
			if(RopFactor > 0){
				duration = duration + (time * RopFactor) + ((userConfigConstant.getRoptime() * 60 * 1000) * RopFactor);
			}

			for(Long startTime:userConfigConstant.getRopToFileMap().get(RopFactor).keySet()){
				for(String fileName:userConfigConstant.getRopToFileMap().get(RopFactor).get(startTime)){
					if(expectedMapper.get(fileName) != 0){
						executor.execute(new WorkerThread(fileName, duration, dateConvert, offSet, utcCheck, expectedMapper, mod,userConfigConstant));
					}
				}
				ropCount++;
				logger.info("incremented"+ropCount);
				if ("YES".equalsIgnoreCase(deletionRequired) && ropCount>=requiredRoptime) 
				{
					while(true){	
						try {
							Thread.sleep(10000);
							if (userConfigConstant.isDeletionstatus()) {
								logger.info("Awake from Thread Sleep for\t"+userConfigConstant.getDtsource());
								int deletionfactor = userConfigConstant.getRopDeletionNumber();
								ropCount = ropCount-deletionfactor;
								userConfigConstant.setDeletionstatus(false);
								logger.info("Deletion status set to false for\t"+userConfigConstant.getDtsource());
								break;
							}
						} catch (InterruptedException e) {
							logger.error("Interruption in thread sleep");
						}
					}
				}
			}
		}
		logger.info("\nFile Processing Completed for "+userConfigConstant.getDtsource() );
		logger.info("\nCopy Scheduler Initiated for " +userConfigConstant.getDtsource());
		executor.shutdown();
	}


	/* (non-Java doc)
	 * @see com.tcs.dg.iservices.threads.IThreadPoolService#getFirstFileNameDetails(java.lang.String)
	 * This method finds out date and time through file name in string format.
	 * And call convertStringToDateWithTimeZone() method to change string date in date format.
	 * It returns deviation in Epoc time.
	 */
	@Override
	public long getFirstFileNameDetails(String name,UserConfigConstant userConfigConstant){
		String fileTime=null;
		String pattern;
		String configFileLoc = ConstantHolder.getAPPPATH() + File.separator + ConstantHolder.getAppconfig();
		AppConfiguration appConfig = ImpFileParser.parseAppConfigInput(configFileLoc);
		Matcher match;
		pattern = appConfig.getDataSource().get(UserConfigConstant.getDataPosition().get(userConfigConstant.getDtsource())).getRelease().getFileNamePattern();
		Pattern value = Pattern.compile(pattern);
		match = value.matcher(name);
		if(match.find()){

			if(name.contains("+")) 
			{
				if(userConfigConstant.getDtsource().equals("LTE-Event-Statistics"))
				{
					fileTime = name.substring(1,name.indexOf("."))+" "+name.substring(name.indexOf(".")+1,name.indexOf("-"))+"00000";
				}else{
					fileTime = name.substring(1,name.indexOf("."))+" "+name.substring(name.indexOf(".")+1,name.indexOf("+"))+"00000";
				}
			}else{
				fileTime = name.substring(1,name.indexOf("."))+" "+name.substring(name.indexOf(".")+1,name.indexOf("-"))+"00000";
			}
		}
		if(fileTimeNonUTC==null){
			fileTimeNonUTC=fileTime;
		}
		return new DateTimeActivities().convertStringToDateWithTimeZone(fileTime,userConfigConstant).getTime();

	}

	/* (non-Java doc)
	 * @see com.tcs.dg.iservices.threads.IThreadPoolService#getFirstFileNameDetails(java.lang.String)
	 * This method finds out date and time through file name in string format.
	 * And call convertStringToDateWithTimeZone() method to change string date in date format.
	 * It returns deviation in Epoc time.
	 */
	@Override
	public long getFirstFileNameDetailsUTC(String name,UserConfigConstant userConfigConstant){
		String configFileLoc = ConstantHolder.getAPPPATH() + File.separator + ConstantHolder.getAppconfig();
		AppConfiguration appConfig = ImpFileParser.parseAppConfigInput(configFileLoc);
		String pattern = appConfig.getDataSource().get(0).getRelease().getUTCPattern();
		Pattern value = Pattern.compile(pattern);
		Matcher match = value.matcher(name);
		if(match.find()){
			String fileTime = match.group(DateAndStringConstants.getDate()) + " " + match.group(DateAndStringConstants.getStarttime())
					+ DateAndStringConstants.getSecondDefault() + DateAndStringConstants.getMillisecondDefault();
			if(fileTimeUTC==null){
				fileTimeUTC=fileTime;
			}
			return new DateTimeActivities().convertStringToDateWithUTC(fileTime,userConfigConstant).getTime();
		}
		return 0;
	}

	/**
	 * This method is used to create map according to the rope time. In that map long time is a key and list of FDN is value.
	 * @param inputs
	 * @param userConfigConstant
	 */
	public void countNode(String[] inputs,UserConfigConstant userConfigConstant){
		for(int i = 0; i < inputs.length; i++){
			long count;
			if(!userConfigConstant.getTimezone().equalsIgnoreCase("UTC")){
				count = getFirstFileNameDetails(inputs[i],userConfigConstant);
			} else{
				count = getFirstFileNameDetailsUTC(inputs[i],userConfigConstant);
			}
			if(!counterNode.containsKey(count)){
				List<String> list = new LinkedList<>();
				list.add(inputs[i]);
				counterNode.put(count,list);
			} else{
				counterNode.get(count).add(inputs[i]);
			}
		}
	}

	/**
	 * This method contain main logic for node extrapolation, it will take expected node value from user input and make map with expected node count.
	 * If in user input value of expected node count is 0 than it will only process the numbers of file present inside input location.
	 * @param inputs
	 * @param userConfigConstant
	 */
	public void mapperCreator(String[] inputs,UserConfigConstant userConfigConstant){
		if(userConfigConstant.getExpectedcount() > 0){
			boolean flag = false;
			countNode(inputs,userConfigConstant);
			List<String> str = new LinkedList<>(counterNode.values()).getFirst();
			int value = userConfigConstant.getExpectedcount();
			int div = value / str.size();
			mod = value % str.size();
			for(int j = 0; j < inputs.length; j++){
				expectedMapper.put(inputs[j],div);
			}
			for(long rope : counterNode.keySet()){
				for(int j = 0; j < mod; j++){
					for(int k = 0; k < counterNode.get(rope).size(); k++){
						if(mod == k){
							flag = true;
							break;
						}
						int val = expectedMapper.get(counterNode.get(rope).get(k));
						expectedMapper.put(counterNode.get(rope).get(k),++val);
					}
					if(flag){
						break;
					}
				}
			}
		}else{
			for(int j = 0; j < inputs.length; j++){
				expectedMapper.put(inputs[j],ConstantHolder.getNodeMulti());
			}
		}
	}

	public static void addTimeDifference(long millis, long timeDifference, UserConfigConstant userConfigConstant, Map<String, UserConfigConstant> userInputDetails) throws InterruptedException, ParseException {
		long result;
		result = millis+timeDifference;
		result=new DateTimeActivities().convertToRoundFigureUpdated(result, userConfigConstant.getRoptime());
		String dateRequired=getDate(result,userConfigConstant,userInputDetails);
		if(!calledScheduler)
		{
			DeletionSchedulerCaller(dateRequired,userConfigConstant,userInputDetails);
			calledScheduler=true;
		}

	}

	/**
	 * Gets the date in "yyyyMMddHHmm" format
	 *
	 * @param result the result
	 * @param dateFormat the date format
	 * @param userConfigConstant the user config constant
	 * @param userInputDetails the user input details
	 * @return the date
	 */
	private static String getDate(long result,UserConfigConstant userConfigConstant, Map<String, UserConfigConstant> userInputDetails) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(result);
		return formatter.format(calendar.getTime());
	}

	/**
	 * Deletion scheduler caller.
	 * Function invokes the DeletionScheduler
	 * @param dateRequired the date required
	 * @param userConfigConstant the user config constant
	 * @param userInputDetails the user input details
	 */
	public static void DeletionSchedulerCaller(String dateRequired, UserConfigConstant userConfigConstant, Map<String, UserConfigConstant> userInputDetails)
	{
		Properties deletionProperty = PropertiesReader
				.getProperties(ConstantHolder.getConfiguration());
		deletionRequired= deletionProperty
				.getProperty("isDeletionRequired");

		if ("YES".equalsIgnoreCase(deletionRequired)) 
		{
			DeletionScheduler deletion = new DeletionScheduler();
			try {
				deletion.DeletionSchedulerFunction(dateRequired,userConfigConstant,userInputDetails);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			logger.info("deletionRequired is set to NO");
		}
	}
}
