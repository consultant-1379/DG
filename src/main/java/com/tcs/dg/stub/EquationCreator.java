package com.tcs.dg.stub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.tcs.dg.helper.NamespaceFilter;
import com.tcs.dg.jaxb.radio.MeasCollecFile;

public class EquationCreator {

	private static Set<String> counters;

	public static void main(String arg[]) {
		couterFetcher(arg[0]);
		WriteRules();
	}

	public static void couterFetcher(String location) {
		String[] files = new File(location).list();
		counters = new HashSet<>();
		for (String name : files) {
			try {
				/*// Updated by mahi
				SAXParserFactory spf = SAXParserFactory.newInstance();
				spf.setFeature(
						"http://apache.org/xml/features/nonvalidating/load-external-dtd",
						false);
				spf.setFeature("http://xml.org/sax/features/validation", false);
				spf.setNamespaceAware(true); // Binding attributes
				EntityResolver entityResolver = new EntityResolver() {
					@Override
					public InputSource resolveEntity(String publicId,
							String systemId) throws SAXException, IOException {
						if (systemId.contains("MeasDataCollection.dtd")
								|| systemId.contains("MeasDataCollection.xsl")) {
							return new InputSource(new StringReader(""));
						} else {
							return null;
						}
					}
				};
				XMLReader xmlReader = spf.newSAXParser().getXMLReader();
				xmlReader.setEntityResolver(entityResolver);
				SAXSource source = new SAXSource(xmlReader, new InputSource(
						new FileInputStream(location + File.separator + name)));
				JAXBContext jaxbContext = JAXBContext.newInstance(MeasCollecFile.class);
				Unmarshaller um = jaxbContext.createUnmarshaller();
				MeasCollecFile root = (MeasCollecFile) um.unmarshal(source);
*/
				
				JAXBContext jc = JAXBContext.newInstance(MeasCollecFile.class);
				Unmarshaller u = jc.createUnmarshaller();
				String filename = location + File.separator + name;
				InputSource inputSource;
				XMLReader reader = XMLReaderFactory.createXMLReader();
				NamespaceFilter inFilter = new NamespaceFilter("http://www.3gpp.org/ftp/specs/archive/32_series/32.435#measCollec", true);
				inFilter.setParent(reader);
				
					inputSource = new InputSource(new FileInputStream(filename));
				
				SAXSource source = new SAXSource(inFilter, inputSource);
				MeasCollecFile root = (MeasCollecFile) u.unmarshal(source);
				for (int i = 0; i < root.getMeasData().size(); i++) {
					for (int a = 0; a <  root.getMeasData().get(i).getMeasInfo().size(); a++) {
						for (int j = 0; j < root.getMeasData().get(i).getMeasInfo().get(a).getMeasType().size(); j++) {
							counters.add(root.getMeasData().get(i).getMeasInfo().get(a).getMeasType().get(j).getValue());
						}
					}
				}

			} catch (Exception ex) {
				System.out.println("Exception occured while parsing.");
			}
		}

	}

	/**
	 * @return
	 */
	public static Set<String> getCounters() {
		return counters;
	}

	/**
	 * @param counters
	 */
	public static void setCounters(Set<String> counters) {
		EquationCreator.counters = counters;
	}

	public static void WriteRules()
	{
		try{
			FileWriter fw = new FileWriter("S:\\xsishai\\input_radio\\counter.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			for(String counter : counters){
				bw.write("<Rule>"+counter+"="+counter+"*2"+"</Rule>");
				bw.write("\n");
			}
			bw.close();
		}catch(Exception ex){
			
		}
			
		}
		
}
