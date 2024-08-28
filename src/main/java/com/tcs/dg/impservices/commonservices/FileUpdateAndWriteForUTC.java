/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.impservices.commonservices;

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
import com.tcs.dg.impservices.utility.Utility;
import com.tcs.dg.iservices.ImpFileParser;
import com.tcs.dg.jaxb.appconfig.AppConfiguration;
import com.tcs.dg.jaxb.produces.Mdc;

/**
 * The Class FileUpdateAndWriteForUTC.
 */
public class FileUpdateAndWriteForUTC  {

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(FileUpdateAndWriteForUTC.class);

	/** The node. */
	private String node;

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
	public synchronized void updateXML(String start, String end, Mdc root, String fileName, long different, Map<String,Integer> fdnRep, int mod,UserConfigConstant userConfigConstant) {
		root.getMfh().setCbt(start);
		root.getMff().setTs(end);
		for (int i = 0; i < root.getMd().size(); i++) {
			for(int j=0; j < root.getMd().get(i).getMi().size(); j++){
				root.getMd().get(i).getMi().get(j).setMts(end);
			}
		}

		for(int i=1; i<=fdnRep.get(fileName); i++){
			if(i == 1){
				if(fdnRep.get(fileName) != 0){
					/*if(null != userConfigConstant.getEnrichment() && !userConfigConstant.getEnrichment().isEmpty()){
						new ApplyingRules().changeValues(root,userConfigConstant);
					}*/
					writeXML(root,fileName,userConfigConstant.getDtsource(),different,userConfigConstant.getRoptime(),fdnRep, i,userConfigConstant);
				}
			}else{
				if(fdnRep.get(fileName) != 0){
					writeXML(root,fileName,userConfigConstant.getDtsource(),different,userConfigConstant.getRoptime(), fdnRep, i,userConfigConstant);
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
	public synchronized void writeXML(Mdc root, String name, String datasource, long diff, int rop, Map<String,Integer> fdnRep, int nodeExtCount, UserConfigConstant userConfigConstant) {
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
					int index;
					if(fileDetailParser.getFdn().contains("_osscounterfile"))
					{	
						index = fileDetailParser.getFdn().trim().indexOf("_osscounterfile");
						int lastindex = fileDetailParser.getFdn().trim().lastIndexOf("=");
						node=fileDetailParser.getFdn().substring(lastindex,index);
					}
					else
					{
						index = fileDetailParser.getFdn().trim().lastIndexOf("_");
						node=fileDetailParser.getFdn().substring(0,index);
					}
					String updatedfdn = fileDetailParser.getFdn().substring(0, index)+"_"+nodeExtCount+fileDetailParser.getFdn().substring(index);
					fName = name.charAt(0)+startAppend.toString()+"-"+fileDetailParser.getStopTime().substring(8)+"_"+updatedfdn;
				}
				if(fdnRep.get(name)> 1){
					if(!root.getMfh().getSn().isEmpty())
					{
						root.getMfh().setSn(Utility.getUpdatedSubnetwork(root.getMfh().getSn() , nodeExtCount));
					}
					for (int j = 0; j < root.getMd().size(); j++) {
						if(!root.getMd().get(j).getNeid().getNeun().isEmpty())
						{
							root.getMd().get(j).getNeid().setNeun(Utility.getUpdatedSubnetwork(root.getMd().get(j).getNeid().getNeun(),nodeExtCount));
						}
						if(!root.getMd().get(j).getNeid().getNedn().isEmpty())
						{
							root.getMd().get(j).getNeid().setNedn(Utility.getUpdatedSubnetwork(root.getMd().get(j).getNeid().getNedn() , nodeExtCount));
						}
						if(!root.getMd().get(j).getMi().isEmpty())
						{
							for(int k = 0; k < root.getMd().get(j).getMi().size(); k++){
								if(!root.getMd().get(j).getMi().get(k).getMv().isEmpty())
								{

									for(int q = 0; q < root.getMd().get(j).getMi().get(k).getMv().size(); q++){
										if(root.getMd().get(j).getMi().get(k).getMv().get(q).getMoid().contains(node)){

											String[] main = root.getMd().get(j).getMi().get(k).getMv().get(q).getMoid().split("-");
											root.getMd().get(j).getMi().get(k).getMv().get(q).setMoid(Utility.getUpdatedSubnetwork(main[0], nodeExtCount) + "-" + main[1]);
										}
									}
								}
							}
						}
					}
				}
				if(fdnRep.get(name) != 0){
					File file = new File(userConfigConstant.getIntermediate()+File.separator+directoryName+File.separator+fName);
					JAXBContext jaxbContext = JAXBContext.newInstance(Mdc.class);
					Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
					jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					jaxbMarshaller.marshal(root, file);
				}
			}
		} catch (Exception e) {
			logger.error("Writing File Exception" );
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
