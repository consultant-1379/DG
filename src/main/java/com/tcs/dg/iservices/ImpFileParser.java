/*
 * Data Generator
 * Author: Shaifali Singh
 */
package com.tcs.dg.iservices;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.tcs.dg.jaxb.appconfig.AppConfiguration;
import com.tcs.dg.jaxb.rules.Rules;
import com.tcs.dg.jaxb.userconfig.UserInputSpecification;

/**
 * The Class ImpFileParser.
 */
public class ImpFileParser {

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(ImpFileParser.class);

	/**
	 * Parses the app config input.
	 *
	 * @param fileName the file name
	 * @return the app configuration
	 */
	public static AppConfiguration parseAppConfigInput(String fileName) {
		File file = new File(fileName);
		if (isExists(file)) {
			JAXBContext jaxbContext;
			AppConfiguration appConfiguration;
			try {
				jaxbContext = JAXBContext.newInstance(AppConfiguration.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				appConfiguration = (AppConfiguration) jaxbUnmarshaller.unmarshal(file);

			} catch (JAXBException e) {
				String message = "Parsing failed for Application Configuration Input file:"
						+ fileName + ".";
				logger.info(message);
				return null;
			}
			return appConfiguration;
		} else {
			return null;
		}
	}

	/**
	 * Parses the user config input.
	 *
	 * @param fileName the file name
	 * @return the user input specification
	 */
	public static UserInputSpecification parseUserConfigInput(String fileName) {
		File file = new File(fileName);
		if (isExists(file)) {
			JAXBContext jaxbContext;
			UserInputSpecification userConfiguration = null;
			try {
				jaxbContext = JAXBContext.newInstance(UserInputSpecification.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				userConfiguration = (UserInputSpecification) jaxbUnmarshaller.unmarshal(file);
			} catch (JAXBException e) {
				logger.info(e.getMessage());
			}
			return userConfiguration;
		} else {
			return null;
		}
	}
	
	public static Rules parseRules(String name){
		File file = new File(name);
		if (isExists(file)) {
			JAXBContext jaxbContext;
			Rules rul = null;
			try {
				jaxbContext = JAXBContext.newInstance(Rules.class);
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				rul = (Rules) jaxbUnmarshaller.unmarshal(file);
			} catch (JAXBException e) {
				logger.info(e.getMessage());
			}
			return rul;
		} else {
			return null;
		}
	}

	/**
	 * Checks if is exists.
	 *
	 * @param file the file
	 * @return true, if is exists
	 */
	public static boolean isExists(File file) {
		 return file.exists();
	}
}
