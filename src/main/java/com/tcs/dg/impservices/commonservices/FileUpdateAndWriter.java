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
import com.tcs.dg.constant.DateAndStringConstants;
import com.tcs.dg.constant.UserConfigConstant;
import com.tcs.dg.impservices.parser.FileDetailParser;
import com.tcs.dg.impservices.utility.Utility;
import com.tcs.dg.iservices.IConstantHelper;
import com.tcs.dg.iservices.ImpFileParser;
import com.tcs.dg.jaxb.appconfig.AppConfiguration;
import com.tcs.dg.jaxb.produces.Mdc;

/**
 * The Class FileUpdateAndWriter.
 */
public class FileUpdateAndWriter implements IConstantHelper {

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(FileUpdateAndWriter.class);

	/** The offset. */
	private String offset;

	/** The node. */
	private String node;

	/** The file detail parser. */
	private FileDetailParser fileDetailParser;

	/**
	 * This method will update time inside XML object and called write method
	 * with required parameters.
	 * 
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @param root
	 *            the root
	 * @param offset
	 *            the offset
	 * @param output
	 *            the output
	 * @param fileName
	 *            the file name
	 * @param datasource
	 *            the data source
	 * @param different
	 *            the different
	 * @param rop
	 *            the rope
	 */
	public void updateXML(String start, String end, Mdc root, String offset, String fileName, long different, Map<String, Integer> fdnRep,
			UserConfigConstant userConfigConstant) {
		this.offset = offset;
		root.getMfh().setCbt(start);
		root.getMff().setTs(end);
		for (int i = 0; i < root.getMd().size(); i++) {
			for (int j = 0; j < root.getMd().get(i).getMi().size(); j++) {
				root.getMd().get(i).getMi().get(j).setMts(end);
			}
		}

		for (int i = 1; i <= fdnRep.get(fileName); i++) {
			if (i == 1) {
				if (fdnRep.get(fileName) != 0) {
					if (null != userConfigConstant.getRules() && !userConfigConstant.getRules().isEmpty()) {
						new ApplyingRules().changeValues(root, userConfigConstant);
					}
					writeXML(root, fileName, userConfigConstant.getDtsource(), different, userConfigConstant.getRoptime(), fdnRep, i,
							userConfigConstant);
				}
			} else {
				if (fdnRep.get(fileName) != 0) {
					writeXML(root, fileName, userConfigConstant.getDtsource(), different, userConfigConstant.getRoptime(), fdnRep, i,
							userConfigConstant);
				}
			}

		}
	}

	/**
	 * This method is used to update the XML object and also write the file with
	 * expected count(Node extrapolation) after updating file name.
	 * 
	 * @param root
	 *            the root
	 * @param output
	 *            the output
	 * @param name
	 *            the name
	 * @param datasource
	 *            the data source
	 * @param offset
	 *            the offset
	 * @param different
	 *            the different
	 * @param rop
	 * @param fdnRep
	 * @param i
	 * @param userConfigConstant
	 */
	public void writeXML(Mdc root, String name, String datasource, long diff, int rop, Map<String, Integer> fdnRep, int i,
			UserConfigConstant userConfigConstant) {
		try {
			patternMatcher(name, datasource, offset, diff, rop, userConfigConstant);
			StringBuilder startAppend = new StringBuilder(fileDetailParser.getTimeStart());
			startAppend.insert(8, '.');
			String directoryName = fileDetailParser.getStopTime();
			if (Utility.mkDir(directoryName, fileDetailParser.getEndTimeDate(), userConfigConstant)) {

				String fName = null;
				if (fdnRep.get(name) == 1) {

					if (userConfigConstant.getDtsource().equals(SGSN_MME)) {
						fName = name.charAt(0) + startAppend.toString() + offset + "-" + startAppend.toString().substring(0, 9)
								+ fileDetailParser.getStopTime().substring(8) + offset + "_" + fileDetailParser.getFdn();
					} else if (userConfigConstant.getDtsource().equals("WRAN-LTE")) {
						fName = name.charAt(0) + startAppend.toString() + offset + "-" + fileDetailParser.getStopTime().substring(8)
								+ offset + "_" + fileDetailParser.getFdn();
					} else {
						fName = name.charAt(0) + startAppend.toString() + "-" + fileDetailParser.getStopTime().substring(8) + offset + "_"
								+ fileDetailParser.getFdn();
					}
				} else if (fdnRep.get(name) > 1) {
				   int index;
					if (userConfigConstant.getDtsource().equals(SGSN_MME)) {
						index = fileDetailParser.getFdn().trim().indexOf(".xm");
						int nodeindex = fileDetailParser.getFdn().trim().lastIndexOf("_");
						node = fileDetailParser.getFdn().substring(nodeindex + 1, index);
						String fd = fileDetailParser.getFdn().substring(0, index) + "_" + i + fileDetailParser.getFdn().substring(index);
						fName = name.charAt(0) + startAppend.toString() + offset + "-" + startAppend.toString().substring(0, 9)
								+ fileDetailParser.getStopTime().substring(8) + offset + "_" + fd;
					} else {
						if (!userConfigConstant.getDtsource().equals("WRAN-LTE")) {
							index = fileDetailParser.getFdn().trim().indexOf("_osscounterfile");
						} else {
							index = fileDetailParser.getFdn().trim().indexOf("_statsfile");
						}
						int MeContextindex = fileDetailParser.getFdn().trim().lastIndexOf("MeContext");
						node = fileDetailParser.getFdn().substring(MeContextindex + 10, index);
						String fd = fileDetailParser.getFdn().substring(0, index) + "_" + i + fileDetailParser.getFdn().substring(index);
						if (!userConfigConstant.getDtsource().equals("WRAN-LTE")) {
							fName = name.charAt(0) + startAppend.toString() + "-" + fileDetailParser.getStopTime().substring(8) + offset
									+ "_" + fd;
						} else {
							fName = name.charAt(0) + startAppend.toString() + offset + "-" + fileDetailParser.getStopTime().substring(8)
									+ offset + "_" + fd;
						}

					}
				}
				if (fdnRep.get(name) > 1) {
					if (!root.getMfh().getSn().isEmpty()) {
						root.getMfh().setSn(Utility.getUpdatedSubnetwork(root.getMfh().getSn(), i));
					}
					for (int j = 0; j < root.getMd().size(); j++) {
						if (!root.getMd().get(j).getNeid().getNeun().isEmpty()) {
							root.getMd().get(j).getNeid().setNeun(Utility.getUpdatedSubnetwork(root.getMd().get(j).getNeid().getNeun(), i));
						}
						if (!root.getMd().get(j).getNeid().getNedn().isEmpty()) {
							root.getMd().get(j).getNeid().setNedn(Utility.getUpdatedSubnetwork(root.getMd().get(j).getNeid().getNedn(), i));
						}
						if (!root.getMd().get(j).getMi().isEmpty()) {
							for (int k = 0; k < root.getMd().get(j).getMi().size(); k++) {
								if (!root.getMd().get(j).getMi().get(k).getMv().isEmpty()) {

									for (int q = 0; q < root.getMd().get(j).getMi().get(k).getMv().size(); q++) {
										if (root.getMd().get(j).getMi().get(k).getMv().get(q).getMoid().contains(node)) {

											String[] main = root.getMd().get(j).getMi().get(k).getMv().get(q).getMoid().split("-");
											root.getMd().get(j).getMi().get(k).getMv().get(q)
													.setMoid(Utility.getUpdatedSubnetwork(main[0], i) + "-" + main[1]);
										}
									}
								}
							}
						}
					}
				}
				if (fdnRep.get(name) != 0) {
					boolean isGz = false;
					if (fName.contains(".gz")) {
						fName = fName.replace(".gz", "");
						isGz = true;
					}
					File file = new File(userConfigConstant.getIntermediate() + File.separator + directoryName + File.separator + fName);
					JAXBContext jaxbContext = JAXBContext.newInstance(Mdc.class);
					Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
					jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
					jaxbMarshaller.marshal(root, file);
					if (isGz) {
						Utility.convertXMLtoGzipFile(file.getAbsolutePath(), file + ".gz");
						file.delete();
					}
				}
			}
		} catch (Exception e) {
			logger.error("Writting File Exception");
		}
	}

	/**
	 * This method will take file name and find attributes from that name.
	 * 
	 * @param name
	 *            the name
	 * @param datasourceName
	 *            the data source name
	 * @param offset
	 *            the offset
	 * @param different
	 *            the different
	 * @param ropTime
	 *            the rope time
	 * @return the first file name details
	 */
	public void patternMatcher(String name, String datasourceName, String offset, long different, int ropTime,
			UserConfigConstant userConfigConstant) {
		String configFileLoc = ConstantHolder.getAPPPATH() + File.separator + ConstantHolder.getAppconfig();
		AppConfiguration appConfig = ImpFileParser.parseAppConfigInput(configFileLoc);
		Matcher match;
		String pattern;
		pattern = appConfig.getDataSource().get(UserConfigConstant.getDataPosition().get(userConfigConstant.getDtsource())).getRelease()
				.getFileNamePattern();
		Pattern value = Pattern.compile(pattern);
		match = value.matcher(name);
		if (match.find()) {
			int endtime_index;
			int fdn_index;
			if (userConfigConstant.getDtsource().equals(SGSN_MME)) {
				endtime_index = DateAndStringConstants.getSgsnEndtime();
				fdn_index = DateAndStringConstants.getSgsnFdn();
				fileDetailParser = new FileDetailParser().getFirstFileNameDetailsNonUTC(match, different, ropTime, userConfigConstant,
						endtime_index, fdn_index, offset);
			} else if (userConfigConstant.getDtsource().equals(LTEEvent)) {
				endtime_index = DateAndStringConstants.getLteEndtime();
				fdn_index = DateAndStringConstants.getLteFdn();
				fileDetailParser = new FileDetailParser().getFirstFileNameDetailsNonUTC(match, different, ropTime, userConfigConstant,
						endtime_index, fdn_index, offset);
			} else {
				endtime_index = DateAndStringConstants.getEndtime();
				fdn_index = DateAndStringConstants.getFdn();
				fileDetailParser = new FileDetailParser().getFirstFileNameDetailsNonUTC(match, different, ropTime, userConfigConstant,
						endtime_index, fdn_index, offset);
			}
		}
	}
}
