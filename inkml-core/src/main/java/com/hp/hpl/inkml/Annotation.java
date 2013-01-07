/*****************************************************************************************
 * Copyright (c) 2008 Hewlett-Packard Development Company, L.P. Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *****************************************************************************************/
/************************************************************************
 * SVN MACROS $Revision: 259 $ $Author: selvarmu $ $LastChangedDate: 2008-07-06 14:36:54 +0530 (Sun, 06 Jul 2008) $
 ************************************************************************************/
package com.hp.hpl.inkml;

import java.util.HashMap;

/**
 * This class models the {@code <annotation>} element of InkML specification. See http://www.w3.org/TR/InkML/#annotationElement for a description and purpose of
 * this element.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0 Creation date : 23-May-2007
 */
public class Annotation implements AnnotationElement {
    /**
     * @Attribute
     */
    private String type = ""; // 'type' attribute value of {@code <annotation>} element
    private String encoding = ""; // 'encoding' attribute value of {@code <annotation>} element
    // Collection of the attributes other than 'type' and 'encoding'.
    private HashMap<String, String> otherAttributesMap;
    private String annotationTextValue = ""; // annotation text value

    /**
     * Constructor to give an Empty annotation Object that represents {@code <annotation/>}. Then the attributes and/or annotation text can be added using
     * appropriate setter methods.
     */
    Annotation() {
        otherAttributesMap = new HashMap<String, String>();
    }

    /**
     * This method gives the value of the attribute identified by the 'attributeName' parameter
     * 
     * @param attributeName the name of the {@code <annotation>} element attribute
     * @return the value of the {@code <annotation>} element attribute. Returns null when the attributeName does not exist.
     */
    public String getAttributeValue(String attributeName) {
        if (attributeName == null)
            return null;
        String value = null;
        if ("type".equals(attributeName)) {
            value = this.type;
        } else if ("encoding".equals(attributeName)) {
            value = this.encoding;
        } else {
            value = otherAttributesMap.get(attributeName);
        }
        return value;
    }

    /**
     * This method gives the 'encoding' attribute value.
     * 
     * @return the 'encoding' attribute value of the {@code <annotation>} element.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * This method gives the 'type' attribute value.
     * 
     * @return the 'type' attribute value of the {@code <annotation>} element.
     */
    public String getType() {
        return type;
    }

    /**
     * This method gives the AnnotationText of the {@code <annotation>} element.
     * 
     * @return AnnotationText String
     */
    public String getAnnotationTextValue() {
        return annotationTextValue;
    }

    /**
     * This method gives the type (Element name) of the InkML element
     * 
     * @return InkML Element name String
     */
    public String getInkElementType() {
        return "Annotation";
    }

    /**
     * This method gives the markup text of the current object state. It is used by the InkMLWriter object for saving inkml to file.
     * 
     * @return markup text String
     */
    public String toInkML() {
        String xml = "<annotation";
        if (!"".equals(this.type))
            xml += " type='" + this.type + "'";
        if (!"".equals(this.encoding))
            xml += " encoding='" + this.encoding + "'";
        if (0 != this.otherAttributesMap.size()) {
            for (Object attr : this.otherAttributesMap.keySet()) {
                Object value = this.otherAttributesMap.get(attr);
                xml += " " + attr + "='" + value + "'";
            }
        }
        if ("".equals(this.annotationTextValue))
            xml += "/>";
        else
            xml += "> " + this.annotationTextValue + " </annotation>";
        return xml;
    }

    /**
     * This method writes the markup text of the current object state in to inkml file using InkMLWriter object in the parameter.
     * 
     * @param writer the InkMLWriter object
     */
    public void writeXML(InkMLWriter writer) {
        writer.writeXML(toInkML());
    }

    /**
     * This method sets value of the 'type' attribute of {@code <annotation>} element.
     * 
     * @param value type attribute value String
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * This method sets value of the 'encoding' attribute of {@code <annotation>} element.
     * 
     * @param value encoding attribute value String
     */
    public void setEncoding(String value) {
        this.encoding = value;
    }

    /**
     * This method add an user defined attribute of {@code <annotation>} element.
     * 
     * @param encoding attribute value String
     */
    /**
     * This method add an user defined attribute of {@code <annotation>} element.
     * 
     * @param attributeName name of the attribute
     * @param value value of the attribute
     */
    public void addToOtherAttributesMap(String attributeName, String value) {
        this.otherAttributesMap.put(attributeName, value);
    }

    /**
     * This method sets value annotation text of {@code <annotation>} element.
     * 
     * @param annotationText
     */
    public void setAnnotationTextValue(String annotationText) {
        this.annotationTextValue = annotationText;
    }
}
