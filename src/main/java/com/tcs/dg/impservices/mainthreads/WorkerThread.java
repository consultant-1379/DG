/*
 * Data Generator
 * Author: Mahipal Jain
 */
package com.tcs.dg.impservices.mainthreads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.log4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.tcs.dg.constant.DateAndStringConstants;
import com.tcs.dg.constant.UserConfigConstant;
import com.tcs.dg.helper.NamespaceFilter;
import com.tcs.dg.impservices.commonservices.FileUpdateAndWriteForUTC;
import com.tcs.dg.impservices.commonservices.FileUpdateAndWriter;
import com.tcs.dg.impservices.radioservices.RadioFileUpdateAndWriteForUTC;
import com.tcs.dg.impservices.radioservices.RadioFileUpdateAndWriter;
import com.tcs.dg.iservices.IConstantHelper;
import com.tcs.dg.iservices.IDateTimeHandler;
import com.tcs.dg.jaxb.produces.Mdc;
import com.tcs.dg.jaxb.radio.MeasCollecFile;

import java.util.zip.GZIPInputStream;

/**
 * The Class WorkerThread.
 * 
 */
public class WorkerThread implements Runnable, IConstantHelper {

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(WorkerThread.class);

	/** The different. */
	private long different;

	/** The file name. */
	private String fileName;

	/** The date convert. */
	private IDateTimeHandler dateConvert;

	/** The offset. */
	private String offset;

	/** The time zone. */
	private boolean timeZone;

	/** The expectedMapper. */
	private Map<String, Integer> expectedMapper;

	/** The mod. */
	private int mod;

	/** The userConfigConstant. */
	private UserConfigConstant userConfigConstant;

	/**
	 * This is used to initializing all the instance variables.
	 * 
	 * @param name
	 *            the name
	 * @param different
	 *            the different
	 * @param url
	 *            the url
	 * @param dateConvert
	 *            the date convert
	 * @param offset
	 *            the offset
	 * @param ropTime
	 *            the rop time
	 * @param out
	 *            the out
	 * @param datasource
	 *            the datasource
	 * @param timeZone
	 *            the time zone
	 */
	public WorkerThread(String name, long different, IDateTimeHandler dateConvert, String offset, boolean timeZone,
			Map<String, Integer> fdnRep, int mod, UserConfigConstant userConfigConstant) {
		this.fileName = name;
		this.different = different;
		this.dateConvert = dateConvert;
		this.offset = offset;
		this.timeZone = timeZone;
		this.expectedMapper = fdnRep;
		this.mod = mod;
		this.userConfigConstant = userConfigConstant;
	}

	/*
	 * This method is used to excuteDataSource method.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			executeDataSource();
		} catch (JAXBException e) {
			logger.error("JAXBException in worker Thread");
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException in worker Thread");
		}
	}

	/**
	 * This method will process files that contain offset in file name. This
	 * method is responsible for reading the file from location and create JAXB
	 * object for that file. This method also called updateXml method and pass
	 * required parameters to that method(i.e start and end time, XML object)
	 * 
	 * @param appender
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	private synchronized void runForNonUTC(String appender) throws JAXBException, FileNotFoundException {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			spf.setFeature("http://xml.org/sax/features/validation", false);
			spf.setNamespaceAware(true); // Binding attributes
			EntityResolver entityResolver = new EntityResolver() {
				@Override
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					if (systemId.contains("MeasDataCollection.dtd") || systemId.contains("MeasDataCollection.xsl")) {
						return new InputSource(new StringReader(""));
					} else {
						return null;
					}
				}
			};

			String filename = userConfigConstant.getInputs() + File.separator + fileName;
			SAXSource source;
			XMLReader xmlReader = spf.newSAXParser().getXMLReader();
			xmlReader.setEntityResolver(entityResolver);
			if (!userConfigConstant.getFiletype().equalsIgnoreCase("gz")) {
				source = new SAXSource(xmlReader, new InputSource(new FileInputStream(filename)));
			} else {
				source = new SAXSource(xmlReader, new InputSource(new GZIPInputStream(new FileInputStream(filename))));
			}
			JAXBContext jaxbContext = JAXBContext.newInstance(Mdc.class);
			Unmarshaller um = jaxbContext.createUnmarshaller();
			Mdc root = (Mdc) um.unmarshal(source);
			long startTime = dateConvert.convertStringToDateWithTimeZone(
					root.getMfh().getCbt().substring(0, 8) + " " + root.getMfh().getCbt().substring(8, 13)
							+ DateAndStringConstants.getMillisecondDefault() + offset, userConfigConstant).getTime();
			long endTime = dateConvert.convertStringToDateWithTimeZone(
					root.getMff().getTs().substring(0, 8) + " " + root.getMff().getTs().substring(8, 13)
							+ DateAndStringConstants.getMillisecondDefault() + offset, userConfigConstant).getTime();
			Date roundupStart = new Date(dateConvert.convertToRoundFigureUpdated(startTime + different, userConfigConstant.getRoptime()));
			Date roundupEnd = new Date(dateConvert.convertToRoundFigureUpdated(endTime + different, userConfigConstant.getRoptime()));
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
			new FileUpdateAndWriter().updateXML(df.format(roundupStart) + appender, df.format(roundupEnd) + appender, root, offset,
					fileName, different, expectedMapper, userConfigConstant);
		} catch (JAXBException e) {
			logger.error("JAXBException in worker Thread runForNonUTC()");
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException in worker Thread runForNonUTC()");
		} catch (SAXException e) {
			logger.error("SAXException in worker Thread runForNonUTC()");
		} catch (Exception ec) {
			logger.error("Exception in worker Thread runForNonUTC()");
			ec.printStackTrace();
		}
	}

	/**
	 * This method will process files that not contain offset in file name. This
	 * method is responsible for reading the file from location and create JAXB
	 * object for that file. This method also called updateXml method and pass
	 * required parameters to that method(i.e start and end time, XML object)
	 * 
	 * @param appender
	 * @throws CloneNotSupportedException
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public synchronized void runForUTC(String appender) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			spf.setFeature("http://xml.org/sax/features/validation", false);
			spf.setNamespaceAware(true); // Binding attributes
			EntityResolver entityResolver = new EntityResolver() {
				@Override
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					if (systemId.contains("MeasDataCollection.dtd") || systemId.contains("MeasDataCollection.xsl")) {
						return new InputSource(new StringReader(""));
					} else {
						return null;
					}
				}
			};
			XMLReader xmlReader = spf.newSAXParser().getXMLReader();
			xmlReader.setEntityResolver(entityResolver);
			SAXSource source = new SAXSource(xmlReader, new InputSource(new FileInputStream(userConfigConstant.getInputs() + File.separator
					+ fileName)));

			JAXBContext jaxbContext = JAXBContext.newInstance(Mdc.class);
			Unmarshaller um = jaxbContext.createUnmarshaller();
			Mdc root = (Mdc) um.unmarshal(source);
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
			long startTime = dateConvert.convertStringToDateWithUTC(
					root.getMfh().getCbt().substring(0, 8) + " " + root.getMfh().getCbt().substring(8, 13)
							+ DateAndStringConstants.getMillisecondDefault(), userConfigConstant).getTime();
			long endTime = dateConvert.convertStringToDateWithUTC(
					root.getMff().getTs().substring(0, 8) + " " + root.getMff().getTs().substring(8, 13)
							+ DateAndStringConstants.getMillisecondDefault(), userConfigConstant).getTime();
			Date roundupStart = new Date(dateConvert.convertToRoundFigureUpdated(startTime + different, userConfigConstant.getRoptime()));
			Date roundupEnd = new Date(dateConvert.convertToRoundFigureUpdated(endTime + different, userConfigConstant.getRoptime()));
			new FileUpdateAndWriteForUTC().updateXML(df.format(roundupStart) + appender, df.format(roundupEnd) + appender, root, fileName,
					different, expectedMapper, mod, userConfigConstant);
		} catch (JAXBException e) {
			logger.error("JAXBException in worker Thread runForUTC()");
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException in worker Thread runForUTC()");
		} catch (SAXException e) {
			logger.error("SAXException in worker Thread runForUTC()");
		} catch (Exception ec) {
			logger.error("Exception in worker Thread runForUTC()");
		}
	}

	/**
	 * This method will process files that not contain offset in file name. This
	 * method is responsible for reading the file from location and create JAXB
	 * object for that file. This method also called updateXml method and pass
	 * required parameters to that method(i.e start and end time, XML object)
	 * 
	 * @param appender
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public synchronized void runForUTCRadio() {
		try {
			JAXBContext jc = JAXBContext.newInstance(MeasCollecFile.class);
			Unmarshaller u = jc.createUnmarshaller();
			XMLReader reader = XMLReaderFactory.createXMLReader();
			NamespaceFilter inFilter = new NamespaceFilter("http://www.3gpp.org/ftp/specs/archive/32_series/32.435#measCollec", true);
			inFilter.setParent(reader);
			InputSource is = new InputSource(new FileInputStream(userConfigConstant.getInputs() + File.separator + fileName));
			SAXSource source = new SAXSource(inFilter, is);
			MeasCollecFile root = (MeasCollecFile) u.unmarshal(source);
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
			long startTime = dateConvert.convertStringToDateWithUTC(
					root.getFileHeader().getMeasCollec().getBeginTime().substring(0, 10).replaceAll("-", "") + " "
							+ root.getFileHeader().getMeasCollec().getBeginTime().substring(11, 19).replaceAll(":", "")
							+ DateAndStringConstants.getMillisecondDefault(), userConfigConstant).getTime();

			long endTime;
			if (root.getFileHeader().getMeasCollec().getBeginTime().contains("Z")) {
				UserConfigConstant.setOffset(true);
				endTime = dateConvert.convertStringToDateWithUTC(
						root.getFileFooter().getEndTime().substring(0, 10).replaceAll("-", "") + " "
								+ root.getFileFooter().getEndTime().substring(11, 19).replaceAll(":", "")
								+ DateAndStringConstants.getMillisecondDefault(), userConfigConstant).getTime();
			} else {
				endTime = dateConvert.convertStringToDateWithUTC(
						root.getFileFooter().getMeasCollec().getEndTime().substring(0, 10).replaceAll("-", "") + " "
								+ root.getFileFooter().getMeasCollec().getEndTime().substring(11, 19).replaceAll(":", "")
								+ DateAndStringConstants.getMillisecondDefault(), userConfigConstant).getTime();
			}

			Date roundupStart = new Date(dateConvert.convertToRoundFigureUpdated(startTime + different, userConfigConstant.getRoptime()));
			Date roundupEnd = new Date(dateConvert.convertToRoundFigureUpdated(endTime + different, userConfigConstant.getRoptime()));
			new RadioFileUpdateAndWriteForUTC().updateXML(df.format(roundupStart), df.format(roundupEnd), root, fileName, different,
					expectedMapper, mod, userConfigConstant, offset);
		} catch (JAXBException e) {
			logger.error("JAXBException in worker Thread runForUTCRadio()");
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException in worker Thread runForUTCRadio()");
		} catch (SAXException e) {
			logger.error("SAXException in worker Thread runForUTCRadio()");
		} catch (Exception ec) {
			logger.error("Exception in worker Thread runForUTCRadio()");
		}
	}

	/**
	 * This method will process files that contain offset in file name. This
	 * method is responsible for reading the file from location and create JAXB
	 * object for that file. This method also called updateXml method and pass
	 * required parameters to that method(i.e start and end time, XML object)
	 * 
	 * @param appender
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public synchronized void runForNonUTCRadio() {
		try {

			JAXBContext jc = JAXBContext.newInstance(MeasCollecFile.class);
			Unmarshaller u = jc.createUnmarshaller();
			String filename = userConfigConstant.getInputs() + File.separator + fileName;
			InputSource inputSource;
			XMLReader reader = XMLReaderFactory.createXMLReader();
			NamespaceFilter inFilter = new NamespaceFilter("http://www.3gpp.org/ftp/specs/archive/32_series/32.435#measCollec", true);
			inFilter.setParent(reader);
			if (!userConfigConstant.getFiletype().equalsIgnoreCase("gz")) {
				inputSource = new InputSource(new FileInputStream(filename));
			} else {
				inputSource = new InputSource(new GZIPInputStream(new FileInputStream(filename)));
			}
			SAXSource source = new SAXSource(inFilter, inputSource);
			MeasCollecFile root = (MeasCollecFile) u.unmarshal(source);
			long startTime = dateConvert.convertStringToDateWithTimeZone(
					root.getFileHeader().getMeasCollec().getBeginTime().substring(0, 10).replaceAll("-", "") + " "
							+ root.getFileHeader().getMeasCollec().getBeginTime().substring(11, 19).replaceAll(":", "")
							+ DateAndStringConstants.getMillisecondDefault() + offset, userConfigConstant).getTime();

			long endTime;
			if (root.getFileHeader().getMeasCollec().getBeginTime().contains("Z")) {
				UserConfigConstant.setOffset(true);
				endTime = dateConvert.convertStringToDateWithTimeZone(
						root.getFileFooter().getEndTime().substring(0, 10).replaceAll("-", "") + " "
								+ root.getFileFooter().getEndTime().substring(11, 19).replaceAll(":", "")
								+ DateAndStringConstants.getMillisecondDefault() + offset, userConfigConstant).getTime();
			} else {
				endTime = dateConvert.convertStringToDateWithTimeZone(
						root.getFileFooter().getMeasCollec().getEndTime().substring(0, 10).replaceAll("-", "") + " "
								+ root.getFileFooter().getMeasCollec().getEndTime().substring(11, 19).replaceAll(":", "")
								+ DateAndStringConstants.getMillisecondDefault() + offset, userConfigConstant).getTime();
			}
			Date roundupStart = new Date(dateConvert.convertToRoundFigureUpdated(startTime + different, userConfigConstant.getRoptime()));
			Date roundupEnd = new Date(dateConvert.convertToRoundFigureUpdated(endTime + different, userConfigConstant.getRoptime()));
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
			new RadioFileUpdateAndWriter().updateXML(df.format(roundupStart), df.format(roundupEnd), root, offset, fileName, different,
					expectedMapper, userConfigConstant);

		} catch (JAXBException e) {
			logger.error("JAXBException in worker Thread runForNonUTCRadio()");
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException in worker Thread runForNonUTCRadio()");
		} catch (SAXException e) {
			logger.error("SAXException in worker Thread runForNonUTCRadio()");
		} catch (Exception ec) {
			logger.error("Exception in worker Thread runForNonUTCRadio()");
		}
	}

	/**
	 * This method is used to process specific data source files as per user
	 * inputs.
	 * 
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @throws CloneNotSupportedException
	 */
	private void executeDataSource() throws JAXBException, FileNotFoundException {

		switch (userConfigConstant.getDtsource()) {

		case WRANLTE:
			if (timeZone) {
				runForNonUTC(DateAndStringConstants.getSecondDefaultOffset());
			} else {
				runForUTC(DateAndStringConstants.getSecondDefaultOffset());
			}

			break;
		case LTEEvent:

			if (timeZone) {
				runForNonUTC(DateAndStringConstants.getSecondDefaultOffset());
			} else {
				runForUTC(DateAndStringConstants.getSecondDefaultOffset());
			}

			break;

		case SGSNMME:
			if (timeZone) {
				runForNonUTCRadio();
			} else {
				runForUTCRadio();
			}
			break;

		case SGSN_MME:
			if (timeZone) {
				runForNonUTC(DateAndStringConstants.getSecondDefault() + ThreadPoolService.offSet);
			} else {
				runForUTC(DateAndStringConstants.getSecondDefault() + ThreadPoolService.offSet);
			}

			break;

		case IPWORKS:
			if (timeZone) {
				runForNonUTC(DateAndStringConstants.getSecondDefault() + ThreadPoolService.offSet);
			} else {
				runForUTC(DateAndStringConstants.getSecondDefault() + ThreadPoolService.offSet);
			}

			break;

		case RADIO:
			if (timeZone) {
				runForNonUTCRadio();
			} else {
				runForUTCRadio();
			}

			break;
		default:
			break;
		}
	}

}
