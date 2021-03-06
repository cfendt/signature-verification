/*****************************************************************************************
 * Copyright (c) 2008 Hewlett-Packard Development Company, L.P. Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *****************************************************************************************/
/****************************************************************************************
 * SVN MACROS $Revision: 274 $ $Author: selvarmu $ $LastChangedDate: 2008-07-07 21:24:00 +0530 (Mon, 07 Jul 2008) $
 ************************************************************************************/
package com.hp.hpl.inkml;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * This class models the component that write the InkML data object's markup data to file or other data output stream.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 */
public final class InkMLWriter {

    // setting tab space size to 4 spaces
    private String tabSpace = "    ";
    private int tabSize = 4;
    // logger
    private static Logger logger = Logger.getLogger(InkMLWriter.class.getName());

    /** Print writer. */
    protected PrintWriter out;

    /** tagLevel for deciding intentation of XML output */
    protected int tagLevel = 0;
    protected String encoding;

    /**
     * InkMLWriter sets "UTF8" as the default encoding method and write in to the output stream which path given in the parameter.
     * 
     * @param stream
     * @throws UnsupportedEncodingException
     */
    public InkMLWriter(final OutputStream stream) throws UnsupportedEncodingException {
        this(stream, "UTF8");
    }

    /**
     * Constructor creating InkMLWriter the OutputStream object given in the parameter.
     * 
     * @param stream
     * @param encoding
     */
    public InkMLWriter(final OutputStream stream, final String encoding) throws UnsupportedEncodingException {

        if (encoding == null) {
            this.encoding = "UTF8";
        } else {
            this.encoding = encoding;
        }

        final java.io.Writer writer = new OutputStreamWriter(stream, encoding);
        this.out = new PrintWriter(writer);
    }

    /**
     * InkMLWriter sets the encoding method as per the 'encoding' parameter value and write in to the file which path given in the parameter.
     * 
     * @param fileName
     * @param encoding
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public InkMLWriter(final String fileName, final String encoding) throws FileNotFoundException, UnsupportedEncodingException {
        FileOutputStream fos = null;
        fos = new FileOutputStream(fileName);
        final OutputStream bout = new BufferedOutputStream(fos);

        if (encoding == null) {
            this.encoding = "UTF8";
        } else {
            this.encoding = encoding;
        }

        final java.io.Writer writer = new OutputStreamWriter(bout, encoding);
        this.out = new PrintWriter(writer);
    }

    /**
     * InkMLWriter sets "UTF8" as the default encoding method and write in to the file which path given in the parameter.
     * 
     * @param filePath
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public InkMLWriter(final String filePath) throws FileNotFoundException, UnsupportedEncodingException {
        this(filePath, "UTF8");
    }

    /**
     * Constructor creating InkMLWriter using the Writer object in the parameter.
     * 
     * @param writer
     */
    public InkMLWriter(final java.io.Writer writer) {
        this.out = writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter(writer);
    }

    /**
     * Method to write the XML Processing instruction. e.g.: {@code <?xml version=1.0 encoding="ISO-8859-1"?>}
     */
    public void writeProcessingInstruction() {
        this.out.println("<?xml version=\"1.0\" encoding=\"" + this.getEncoding() + "\"?>");
    }

    /**
     * Method to write the start tag eg: {@code <trace id="t1">}
     * 
     * @param tagName name the tag
     * @param attrMap the map of attributeName ==> attributeValue
     */
    public void writeStartTag(final String tagName, final Map<String, String> attrMap) {
        // print space for intentation
        for (int i = 0; i < this.tagLevel; i++) {
            this.out.write(this.tabSpace);
        }
        this.writeBaseStartTag(tagName, attrMap, this.out);
        this.out.print(">");
        this.out.println();
    }

    /**
     * Method to write the end tag eg: {@code </trace>}
     * 
     * @param tagName name the tag
     */
    public void writeEndTag(final String tagName) {
        InkMLWriter.logger.finer("end tag for " + tagName + ". tagLevel: " + this.tagLevel);

        // print space for intentation
        for (int i = 0; i < this.tagLevel; i++) {
            this.out.print(this.tabSpace);
        }
        this.out.print("</" + tagName + ">");
        this.out.println();
        this.out.flush();
    }

    /**
     * Write the given characterData as String in the paramater at the current tag level
     * 
     * @param characterData e.g: The trace data string, "10 15, '1 '5, "2 "5, 5 6, 3 4"
     */
    public void writeCharacters(final String characterData) {
        // print space for intentation
        for (int i = 0; i < this.tagLevel; i++) {
            this.out.print(this.tabSpace);
        }
        this.writeCharacters(characterData, this.out);
        this.out.println();
        this.out.flush();
    }

    /**
     * Method to write the given xmlString in the parameter at the current tag level
     * 
     * @param xmlString
     */
    public void writeXML(final String xmlString) {
        // print space(s) for intentation
        for (int i = 0; i < this.tagLevel; i++) {
            this.out.print(this.tabSpace);
        }
        this.out.write(xmlString);
        this.out.println();
        this.out.flush();
    }

    /**
     * Method to write the empty start tag eg: {@code <channel name="X" type="interger" />}
     * 
     * @param tagName name the tag
     * @param attributesMap the map of attributeName ==> attributeValue
     */
    public void writeEmptyStartTag(final String tagName, final Map<String, String> attributesMap) {
        // print space for intentation
        for (int i = 0; i < this.tagLevel; i++) {
            this.out.write(this.tabSpace);
        }
        this.writeBaseStartTag(tagName, attributesMap, this.out);
        this.out.print("/>");
        this.out.println();
    }

    /**
     * Method creates an Empty start tag XML but not write in to the fileStream of the writer. Thus accumulated XML from calling this function many time can be
     * written in to the file stream using writeXML method.
     * 
     * @param tagName
     * @param attributesMap
     * @return the EmtpyStartTag XML data
     */
    public String getEmptyStartTagXML(final String tagName, final Map<String, String> attributesMap) {

        final StringWriter stringWriter = new StringWriter();
        // print space for intentation
        for (int i = 0; i < this.tagLevel; i++) {
            stringWriter.write(this.tabSpace);
        }
        this.writeBaseStartTag(tagName, attributesMap, stringWriter);
        stringWriter.write("/>\r\n");
        return stringWriter.toString();
    }

    /**
     * Method to set increment the level of the tag in the DOM tree, it is used for alignment of the tags.
     */
    public void incrementTagLevel() {
        this.tagLevel++;
    }

    /**
     * Method to set decrement the level of the tag in the DOM tree, it is used for alignment of the tags.
     */
    public void decrementTagLevel() {
        this.tagLevel--;
    }

    /**
     * Method to give the current tag level number.
     * 
     * @return current tag level number
     */
    public int getTagLevel() {
        return this.tagLevel;
    }

    /**
     * Method to close the Writer object. It close the output stream.
     */
    public void close() {
        this.out.close();
    }

    // private methods

    // change the special chracters that are not allowed in XML content to proper encoded literal strings.
    private void writeCharacter(final char c, final Writer out) throws IOException {
        switch (c) {
        case '<': {
            out.write("&lt;");
            break;
        }
        case '>': {
            out.write("&gt;");
            break;
        }
        case '&': {
            out.write("&amp;");
            break;
        }
        case '"': {
            out.write("&quot;");
            break;
        }
        case '\r': {
            out.write("&#xD;");
            break;
        }
        default: {
            out.write(c);
        }
        }
    }

    private void writeBaseStartTag(final String tagName, final Map<String, String> attrs, final Writer out) {
        try {
            if (null == out) {
                InkMLWriter.logger.severe("No writer assigned. Can not write InkML data");
                return;
            }
            out.write('<');
            out.write(tagName);
            if (attrs != null) {
                // sort by attribute name
                final Map<String, String> sortedAttrs = new TreeMap<String, String>(attrs);

                final java.util.Set<String> keys = sortedAttrs.keySet();
                final java.util.Iterator<String> itr = keys.iterator();
                while (itr.hasNext()) {
                    out.write(' ');
                    final String key = itr.next();
                    out.write(key);
                    out.write("=\"");
                    this.writeCharacters(attrs.get(key), out);
                    out.write('"');
                }
            }
        } catch (final IOException e) {
            InkMLWriter.logger.severe("Error while writing StartTag,  " + tagName + ".\nMessage: " + e.getMessage());
        }
    }

    private void writeCharacters(final String charcaterData, final Writer out) {
        if (null != charcaterData) {
            for (int i = 0; i < charcaterData.length(); i++) {
                final char c = charcaterData.charAt(i);
                try {
                    this.writeCharacter(c, out);
                } catch (final IOException e) {
                    InkMLWriter.logger.severe("Error while encoding character data." + e.getMessage());
                }
            }
        }

    }

    // method to map Java Encoding and XML encoding string litteral values
    private String getEncoding() {
        String encodingStr = null;
        if (this.encoding.equalsIgnoreCase("UTF8")) {
            encodingStr = "UTF-8";
        } else if (this.encoding.equalsIgnoreCase("8859_1")) {
            encodingStr = "ISO-8859-1";
        }
        return encodingStr;
    }

    public String getTabSpace() {
        return this.tabSpace;
    }

    public int getTabSize() {
        return this.tabSize;
    }

    public void setTabSize(final int tabSize) {
        this.tabSize = tabSize;
        if (tabSize == 0) {
            this.tabSpace = "";
        }
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < tabSize; i++) {
            buffer.append(' ');
        }
        this.tabSpace = buffer.toString();
    }
}
