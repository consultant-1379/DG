/*
 * Data Generator
 * Author: Shaifali Singh
 */
package com.tcs.dg.impservices.radioservices;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.tcs.dg.constant.ConstantHolder;
import com.tcs.dg.constant.UserConfigConstant;
import com.tcs.dg.impservices.parser.FileDetailParser;
import com.tcs.dg.impservices.utility.DateTimeActivities;
import com.tcs.dg.impservices.utility.Utility;
import com.tcs.dg.iservices.ImpFileParser;
import com.tcs.dg.jaxb.appconfig.AppConfiguration;
import com.tcs.dg.jaxb.radio.MeasCollecFile;

/**
 * The Class FileUpdateAndWriteForUTC.
 */
public class RadioFileUpdateAndWriteForUTC {
	
	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(RadioFileUpdateAndWriteForUTC.class);
    
    private FileDetailParser fileDetailParser;
	
	/**
	 *  This method will update time inside XML object and called write method with required parameters.
	 * @param start the start
	 * @param end the end
	 * @param root the root
	 * @param output the output
	 * @param fileName the file name
	 * @param datasource the data source
	 * @param different the different
	 * @param rop the rope
	 */
	public synchronized void updateXML(String start, String end, MeasCollecFile root, String fileName, long different, Map<String,Integer> fdnRep, int mod,UserConfigConstant userConfigConstant,String offset) {
        //this.timeZone = timeZone;
        start = new DateTimeActivities().radioParseDate(start,userConfigConstant,offset);
        root.getFileHeader().getMeasCollec().setBeginTime(start );
        end = new DateTimeActivities().radioParseDate(end,userConfigConstant,offset);
        if(UserConfigConstant.isOffset()){
        	root.getFileFooter().setEndTime(end);
        }else{
        	root.getFileFooter().getMeasCollec().setEndTime(end);
        }
        UserConfigConstant.setOffset(false) ;
        for (int i = 0; i < root.getMeasData().size(); i++) {
			for(int j=0; j < root.getMeasData().get(i).getMeasInfo().size(); j++){
				root.getMeasData().get(i).getMeasInfo().get(j).getGranPeriod().setEndTime(end);
			}
		}
      
        for(int i = 1; i <= fdnRep.get(fileName); i++){
            if(i == 1){
                if(fdnRep.get(fileName) != 0){
                	/*if(null != userConfigConstant.getRules() && !userConfigConstant.getRules().isEmpty()){
                        new RadioApplyingRules().changeValues(root, userConfigConstant);
                    }*/
                    writeXML(root,fileName,userConfigConstant.getDtsource(),different,userConfigConstant.getRoptime(),fdnRep,i,userConfigConstant);
                }
            } else{
                if(fdnRep.get(fileName) != 0){
                   writeXML(root,fileName,userConfigConstant.getDtsource(),different,userConfigConstant.getRoptime(),fdnRep,i,userConfigConstant);
                }
            }

        }
    }

	/**
	 * This method is used to update the XML object and also write the file with expected count(Node extrapolation) after updating file name.
	 * @param root the root
	 * @param name the name
	 * @param datasource the data source
	 * @param diff the different
	 * @param rop the rope
	 */
	public synchronized void writeXML(MeasCollecFile root, String name, String datasource, long diff, int rop, Map<String,Integer> fdnRep, int i,UserConfigConstant userConfigConstant) {
		try {
			patternMatcher(name,datasource,diff,rop,userConfigConstant);
			StringBuilder startAppend = new StringBuilder(fileDetailParser.getTimeStart());
			startAppend.insert(8, '.');
			String directoryName = fileDetailParser.getStopTime();
			if(Utility.mkDir(directoryName,fileDetailParser.getEndTimeDate(),userConfigConstant)){
				String fName = null;
				if(fdnRep.get(name)==1)
					fName = name.charAt(0)+startAppend.toString()+"-"+fileDetailParser.getStopTime().substring(8)+"_"+fileDetailParser.getFdn();
				else if(fdnRep.get(name)>1){
					/** The index. */
				    int index;
					if (fileDetailParser.getFdn().contains("_osscounterfile"))
					{
					index = fileDetailParser.getFdn().trim().indexOf("_osscounterfile");
					}
					else
					{
					index = fileDetailParser.getFdn().trim().indexOf("_statsfile");
					}
					String fd = fileDetailParser.getFdn().substring(0, index)+"_"+i+fileDetailParser.getFdn().substring(index);
					fName = name.charAt(0)+startAppend.toString()+"-"+fileDetailParser.getStopTime().substring(8)+"_"+fd;
				}
				
				if(fdnRep.get(name)> 1 && root.getFileHeader().getDnPrefix()!=null){
					root.getFileHeader().setDnPrefix(Utility.getUpdatedSubnetwork(root.getFileHeader().getDnPrefix(),i));
					for (int a = 0; a < root.getMeasData().size(); a++) {
						for(int j = 0; j < root.getMeasData().get(a).getMeasInfo().size(); j++)
						{
							for(int k = 0; k < root.getMeasData().get(a).getMeasInfo().get(j).getMeasValue().size(); k++)
							{
								if(root.getMeasData().get(a).getMeasInfo().get(j).getMeasValue().get(k).getMeasObjLdn().contains("ManagedElement"))
								{
									String nodeName=root.getMeasData().get(a).getMeasInfo().get(j).getMeasValue().get(k).getMeasObjLdn();
									int index = nodeName.indexOf(",");
									String nodeNameUpdated = Utility.getUpdatedSubnetwork(nodeName.substring(0,index) , i) + nodeName.substring(index);
									root.getMeasData().get(a).getMeasInfo().get(j).getMeasValue().get(k).setMeasObjLdn(nodeNameUpdated);
								}
							}

						}
					}
				}
				File file = new File(userConfigConstant.getIntermediate()+File.separator+directoryName+File.separator+fName);
				JAXBContext jaxbContext = JAXBContext.newInstance(MeasCollecFile.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				jaxbMarshaller.marshal(root, file);
				
			}
		} catch (Exception e) {
			System.out.println("Writing File Exception" );
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will check whether the input file  matches the pattern or not.
	 * @param name the name
	 * @param datasourceName the data source name
	 * @param different the different
	 * @param ropTime the rope time
	 * @return the first file name details
	 */
	public void patternMatcher(String name, String datasourceName, long different, int ropTime,UserConfigConstant userConfigConstant) {
			String configFileLoc = ConstantHolder.getAPPPATH()+File.separator+ConstantHolder.getAppconfig();
			AppConfiguration appConfig = ImpFileParser.parseAppConfigInput(configFileLoc);
			String pattern = appConfig.getDataSource().get(UserConfigConstant.getDataPosition().get(userConfigConstant.getDtsource())).getRelease().getUTCPattern();
			Pattern value = Pattern.compile(pattern);
			Matcher match =  value.matcher(name);
			if(match.find()){
			 fileDetailParser=new FileDetailParser().getFirstFileNameDetailsUTC(match,different,ropTime,userConfigConstant);
		}
	}
}
