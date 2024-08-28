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
import com.tcs.dg.constant.DateAndStringConstants;
import com.tcs.dg.constant.UserConfigConstant;
import com.tcs.dg.impservices.parser.FileDetailParser;
import com.tcs.dg.impservices.utility.DateTimeActivities;
import com.tcs.dg.impservices.utility.Utility;
import com.tcs.dg.iservices.IConstantHelper;
import com.tcs.dg.iservices.ImpFileParser;
import com.tcs.dg.jaxb.appconfig.AppConfiguration;
import com.tcs.dg.jaxb.radio.MeasCollecFile;





/**
 * The Class FileUpdateAndWriter.
 */
public class RadioFileUpdateAndWriter implements IConstantHelper {

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(RadioFileUpdateAndWriter.class);

	/** The offset. */
	private String offset;

	private FileDetailParser fileDetailParser;

	/**
	 *  This method will update time inside XML object and called write method with required parameters.
	 * @param start the start
	 * @param end the end
	 * @param root the root
	 * @param offset the offset
	 * @param fileName the file name
	 * @param different the different
	 * @param fdnRep the fileDetailParser.getFdn() rep
	 * @param userConfigConstant the user config constant
	 */
	public void updateXML(String start, String end, MeasCollecFile root, String offset, String fileName, long different, Map<String, Integer> fdnRep,UserConfigConstant userConfigConstant){
		this.offset = offset;
		start = new DateTimeActivities().radioParseDate(start,userConfigConstant,offset);
		root.getFileHeader().getMeasCollec().setBeginTime(start );
		if(root.getFileHeader().getMeasCollec().getBeginTime().contains("Z")){
			UserConfigConstant.setOffset(true);
		}
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

					if(null != userConfigConstant.getRules() && !userConfigConstant.getRules().isEmpty()){
						new RadioApplyingRules().changeValues(root, userConfigConstant);
					}
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
	 * @param datasource the datasource
	 * @param diff the diff
	 * @param rop the rop
	 * @param fdnRep the fileDetailParser.getFdn() rep
	 * @param i the i
	 * @param userConfigConstant the user config constant
	 */
	public void writeXML(MeasCollecFile root, String name, String datasource, long diff, int rop, Map<String, Integer> fdnRep, int i,UserConfigConstant userConfigConstant){
		try{
			patternMatcher(name,datasource,offset,diff,rop,userConfigConstant);
			StringBuilder startAppend = new StringBuilder(fileDetailParser.getTimeStart());
			startAppend.insert(8,'.');
			String directoryName = fileDetailParser.getStopTime();
			if(Utility.mkDir(directoryName,fileDetailParser.getEndTimeDate(),userConfigConstant)){

				String fName = null;
				if(fdnRep.get(name) == 1){


					if(userConfigConstant.getDtsource().equals(SGSNMME))
					{
						fName = name.charAt(0) + startAppend.toString() + offset+"-" + startAppend.toString().substring(0,9) + fileDetailParser.getStopTime().substring(8)+offset + "_" + fileDetailParser.getFdn();
					}
					else
					{

						fName = name.charAt(0) + startAppend.toString()+offset + "-" + fileDetailParser.getStopTime().substring(8) +offset+ "_" + fileDetailParser.getFdn();
					}

				} else if(fdnRep.get(name) > 1){
					int index = fileDetailParser.getFdn().trim().indexOf("_statsfile");
					String fd = fileDetailParser.getFdn().substring(0,index) + "_" + i + fileDetailParser.getFdn().substring(index);
					if(userConfigConstant.getDtsource().equals(SGSNMME))
					{
						fName = name.charAt(0) + startAppend.toString()+offset  + "-" + startAppend.toString().substring(0,9) + fileDetailParser.getStopTime().substring(8)+offset + "_" + fd;
					}
					else
					{
						fName = name.charAt(0) + startAppend.toString()+offset  + "-" + fileDetailParser.getStopTime().substring(8)+offset + "_" + fd;
					}
				}
				if(fdnRep.get(name) > 1){
					for (int a = 0; a < root.getMeasData().size(); a++) {
						if(userConfigConstant.getDtsource().equals(SGSNMME))
						{
							if(!root.getMeasData().get(a).getManagedElement().getLocalDn().isEmpty())
							{
								root.getMeasData().get(a).getManagedElement().setLocalDn(Utility.getUpdatedSubnetwork(root.getMeasData().get(a).getManagedElement().getLocalDn(),i));
							}
						}
						else if(userConfigConstant.getDtsource().equals(RADIO))
						{
							if(root.getFileHeader().getDnPrefix()!=null)
							{
								root.getFileHeader().setDnPrefix(Utility.getUpdatedSubnetwork(root.getFileHeader().getDnPrefix(),i));
							}
							if(root.getMeasData().get(a).getManagedElement().getUserLabel()!=null)
							{
								root.getMeasData().get(a).getManagedElement().setUserLabel(Utility.getUpdatedSubnetwork(root.getMeasData().get(a).getManagedElement().getUserLabel(),i));
							}

							if(root.getFileHeader().getFileSender().getLocalDn()!=null && root.getFileHeader().getFileSender().getLocalDn().contains("MeContext"))
							{
								// String nodeName=root.getFileHeader().getFileSender().getLocalDn();	 
								root.getFileHeader().getFileSender().setLocalDn(Utility.getUpdatedSubnetwork(root.getFileHeader().getFileSender().getLocalDn(),i));
							}

						}
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
				if(fdnRep.get(name) != 0){
					boolean  isGz = false;
					if(fName.contains(".gz")){
						fName = fName .replace(".gz", "");
						isGz =true ;
					}
					File file = new File(userConfigConstant.getIntermediate() + File.separator + directoryName + File.separator + fName);
					JAXBContext jaxbContext = JAXBContext.newInstance(MeasCollecFile.class);
					Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
					jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
					//jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "<?xml-stylesheet type="text/xsl" href="MeasDataCollection.xsl"?>");
					jaxbMarshaller.marshal(root,file);
					if(isGz){
						Utility.convertXMLtoGzipFile(file.getAbsolutePath(),file+".gz");
						file.delete();
					}
				}
			}
		}catch(Exception e){
			logger.error("Writting File Exception"+" "+ name);
			e.printStackTrace();
		}
	}

	/**
	 * This method will check whether the input file  matches the pattern or not.
	 * @param name the name
	 * @param datasourceName the datasource name
	 * @param offset the offset
	 * @param different the different
	 * @param ropTime the rop time
	 * @param userConfigConstant the user config constant
	 * @return the first file name details
	 */
	public void patternMatcher(String name, String datasourceName, String offset, long different, int ropTime,UserConfigConstant userConfigConstant){
		String configFileLoc = ConstantHolder.getAPPPATH() + File.separator + ConstantHolder.getAppconfig();
		AppConfiguration appConfig = ImpFileParser.parseAppConfigInput(configFileLoc);
		Matcher match;
		String pattern;
		pattern = appConfig.getDataSource().get(UserConfigConstant.getDataPosition().get(userConfigConstant.getDtsource())).getRelease().getFileNamePattern();
		Pattern value = Pattern.compile(pattern);
		match = value.matcher(name);
		if(match.find()){
			int endtime_index;
			int fdn_index;
			if(userConfigConstant.getDtsource().equals(RADIO))
			{
				endtime_index=DateAndStringConstants.getEndtime();
				fdn_index=DateAndStringConstants.getFdn();
				fileDetailParser=new FileDetailParser().getFirstFileNameDetailsNonUTC(match,different,ropTime,userConfigConstant,endtime_index,fdn_index,offset);
			}
			else if(userConfigConstant.getDtsource().equals(SGSNMME))
			{
				endtime_index=DateAndStringConstants.getSgsnEndtime();
				fdn_index=DateAndStringConstants.getSgsnFdn();
				fileDetailParser=new FileDetailParser().getFirstFileNameDetailsNonUTC(match,different,ropTime,userConfigConstant,endtime_index,fdn_index,offset);
			}

		}
	}
	
}
