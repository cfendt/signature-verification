/*****************************************************************************************
* Copyright (c) 2008 Hewlett-Packard Development Company, L.P.
* Permission is hereby granted, free of charge, to any person obtaining a copy of
* this software and associated documentation files (the "Software"), to deal in
* the Software without restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
* Software, and to permit persons to whom the Software is furnished to do so,
* subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
* PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
* CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
* OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*****************************************************************************************/
/****************************************************************************************
 * SVN MACROS
 *
 * $Revision: 259 $
 * $Author: selvarmu $
 * $LastChangedDate: 2008-07-06 14:36:54 +0530 (Sun, 06 Jul 2008) $
 ************************************************************************************/
package com.hp.hpl.inkml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class models the {@code <annotationXML>} element in InkML.
 * It is a child element to {@code <ink>}, {@code <traceGroup>} and {@code <traceView>} InkML elements.
 * It maintains a HashMap with entry for each child element of {@code <annotationXML>} element.
 * An entry in the map has the tagName of the child element of {@code <annotationXML>}
 * as the key and the text node value as the Value.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 * Creation date : 4 May 2007
 */

public class AnnotationXML implements AnnotationElement{
	private String type="";
	private String encoding="";
	//private URI href; // not implemented
	private HashMap<String, String> otherAttributesMap;
	private HashMap<String, String> propertyElementsMap;

	// Create logger instance for logging
	private static Logger logger =
		 Logger.getLogger(AnnotationXML.class.getName());



	/**
	 * Constructor to give an Empty annotation Object.
	 * Then the attributes and content XML element would be created using using appropriate setter methods.
	 */
	AnnotationXML () {
		otherAttributesMap = new HashMap<String, String>();
		propertyElementsMap = new HashMap<String, String>();
	}


	private HashMap<String, String> getPropertyElementsMapFromHrefElement
	(String href) throws InkMLException{
		throw new InkMLException("Feature (AnnotationXML:getPropertyElementsMapFromHrefElement) is not implimented.");
		//HashMap<String, String> valueMap = null;

		// Implementation of this method will be provided later.
		//Element hrefElement = Util.getAnnotationXMLRefElement(href);
			// process the referred element to populate the value Map

		//return valueMap;
	}

	/**
	 * This method returns the value of the attribute of the property defined in the {@code <annotationXML>}
	 * by means of a child element with tage Name as the value in the parameter 'property'.
	 * **Note: This method is not yet implemented.
	 * @param property	the name of the Property element given as child to annotationXML.
	 * @param attribute the attribute name of the property element identified by the parameter 'property'.
	 * @return the String value of the attribute of the property.
	 * @throws InkMLException
	 */
	public String getValue(String property, String attribute) throws InkMLException {
		throw new InkMLException("Feature not yet implemented.");
	}

	/**
	 * This method returns the value of the property defined in the {@code <annotationXML>}
	 * by means of a child element with tage Name as the value in the parameter 'property'.
	 * @param property	the name of the Property element given as child to annotationXML.
	 * @return the value of the property. Returns 'null' for property that does not defined in the {@code <annotationXML>}.
	 * @throws InkMLException
	 */
	public String getProperty(String property) {
		return  propertyElementsMap.get(property);
	}

	/**
	 * This method set the value of the property defined in the {@code <annotationXML>}
	 * by means of a child element with tag Name same as the value in the 'property' parameter.
	 * @param property	the name of the property element given as child to annotationXML.
	 * @param value	of the property element given as child to annotationXML.
	 */
	public void setProperty(String property, String value) {
		this.propertyElementsMap.put(property, value);
	}

	/**
	 * This method returns the type attribute value of the {@code <annotationXML>} element.
	 * @return the type attribute value String.
	 */
	public String getType() {
		return type;
	}

	/**
	 * This method returns the encoding attribute value of the {@code <annotationXML>} element.
	 * @return the encoding attribute value String.
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Method to know the type of the InkML Element which is nothing but the class name of the data object (AnnotationXML)
	 * @return the class name
	 */
	public String getInkElementType() {
		return "AnnotationXML";
	}

	/**
	 * Equals method overriden to compare AnnotationXML objects
	 * @param annotationXML
	 * @return status of comparision
	 */
	public boolean equals(AnnotationXML annotationXML) {
		boolean isEqual = true;
		if(otherAttributesMap.size() != annotationXML.otherAttributesMap.size())
			return false;
		if(!otherAttributesMap.keySet().containsAll(annotationXML.otherAttributesMap.keySet()))
			return false;
		if(propertyElementsMap.size() != annotationXML.propertyElementsMap.size())
			return false;
		Set<String> propertyKeys = propertyElementsMap.keySet();
		if(!propertyKeys.containsAll(annotationXML.propertyElementsMap.keySet())) {
			return false;
		} else {
			Iterator iterator = propertyKeys.iterator();
			while(iterator.hasNext()) {
				String key = (String)iterator.next();
				String propertyValue1 =null, propertyValue2=null;
				propertyValue1 = getProperty(key);
				propertyValue2 =annotationXML.getProperty(key);
				if(! propertyValue1.equalsIgnoreCase(propertyValue2))
					return false;
			}
		}
		return isEqual;
	}

	/**
	 * Method to serialize the markup data of this annotationXML object as a String
	 */
	public String toInkML() {
		StringBuffer annotXMLData = new StringBuffer("<annotationXML>");
		if(propertyElementsMap.size()!=0) {
			Set propertyKeys = propertyElementsMap.keySet();
			Iterator iterator = propertyKeys.iterator();
			while(iterator.hasNext()) {
				String key = (String)iterator.next();
				String value =(String)propertyElementsMap.get(key);
				annotXMLData.append("<"+key+">"+value+"</"+key+">");
			}
		}
		annotXMLData.append("</annotationXML>");
		return annotXMLData.toString();
	}

	/**
	 * Gives an array of Propertynames available in AnnotationXML content XML block
	 * @return array of user defined property names
	 */
	public String[] getPropertyNames() {
		int nProperties = propertyElementsMap.size();
		String[] propertyNames = new String[nProperties];
		Set<String> keys = propertyElementsMap.keySet();
		Iterator<String> itr = keys.iterator();
		int index=0;
		while(itr.hasNext()) {
			propertyNames[index++]=itr.next();
		}
		return propertyNames;
	}

	/**
	 * method to check if a property available
	 * @param propertyName
	 * @return status if the property exist
	 */
	public boolean isPropertyExist(String propertyName) {
		return propertyElementsMap.containsKey(propertyName);
	}

	/**
	 * method to copy the values of the AnnotationXML object to the current AnnotationXML object
	 * @param referredAxmlObject
	 */
	public void override(AnnotationXML referredAxmlObject){
		if(null == referredAxmlObject)
			return;
		String[] propertyNames = referredAxmlObject.getPropertyNames();
		for(int i=0; i<propertyNames.length; i++) {
				this.propertyElementsMap.put(propertyNames[i],
						referredAxmlObject.getProperty(propertyNames[i]));
		}
	}

	/**
	 * method to set the 'type' attribute
	 * @param value
	 */
	public void setType(String value) {
		this.type=value;
	}

	/**
	 * method to set the 'encoding' attribute
	 * @param value
	 */
	public void setEncoding(String value) {
		this.encoding=value;
	}

	/**
	 * method to set the 'href' attribute.
	 * Note: This feature is not yet implemented.
	 */
	public void setHref(URI value) throws InkMLException{
		throw new InkMLException("Feature (AnnotationXML:setHref) is not implimented.");
	}

	/**
	 * method to get the 'href' attribute.
	 * Note: This feature is not yet imple
	 * @return the URI from where user defined properties are loaded from
	 * @throws InkMLException
	 */
	public URI getHref() throws InkMLException{
		throw new InkMLException("Feature (AnnotationXML:getHref) is not implimented.");
	}

	/**
	 * method to copy the definitions from the another XML object, perhaps a tree of xml data which is identified by the parameter string as URI
	 * Note: this feature is not yet implemented
	 * @param ref
	 */
	public void addAllToPropertyMap(String ref) {
		try{
			this.propertyElementsMap.putAll(getPropertyElementsMapFromHrefElement(ref));
		} catch (InkMLException e) {
			logger.severe(
			"Problem in binding 'href' attribute of "+
			"AnnotationXML data.\nReason: "+e.getMessage());
		}
	}

	/**
	 * method to add 'user-defined' attributes other than the standard attributes.
	 * @param attributeName
	 * @param value
	 */
	public void addToOtherAttributesMap(String attributeName, String value) {
		this.otherAttributesMap.put(attributeName, value);
	}

	/**
	 * method to add an user defined property as a {name, value} pair which are identified by the parameters
	 * @param name
	 * @param value
	 */
	public void addToPropertyElementsMap(String name, String value) {
		this.propertyElementsMap.put(name, value);
	}

	/**
	 * Method used by the Archiver component (InkMLWriter) to save the markup data of the AnotationXML data object to file or other data stream
	 * @param writer
	 */
	public void writeXML(InkMLWriter writer) {
		if(null != writer)
			writer.writeXML(toInkML());
	}
}