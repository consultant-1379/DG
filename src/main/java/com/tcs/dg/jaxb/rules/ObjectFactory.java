//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.23 at 11:54:16 AM CEST 
//


package com.tcs.dg.jaxb.rules;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.tcs.dg.jaxb.rules package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.tcs.dg.jaxb.rules
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Rules }
     * 
     */
    public Rules createRules() {
        return new Rules();
    }

    /**
     * Create an instance of {@link Rules.DataSources }
     * 
     */
    public Rules.DataSources createRulesDataSources() {
        return new Rules.DataSources();
    }

    /**
     * Create an instance of {@link Rules.DataSources.DataSource }
     * 
     */
    public Rules.DataSources.DataSource createRulesDataSourcesDataSource() {
        return new Rules.DataSources.DataSource();
    }

}
