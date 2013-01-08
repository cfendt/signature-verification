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
 * SVN MACROS $Revision: 259 $ $Author: selvarmu $ $LastChangedDate: 2008-07-06 14:36:54 +0530 (Sun, 06 Jul 2008) $
 ************************************************************************************/
package com.hp.hpl.inkml;

import java.util.logging.Logger;

/**
 * This class models the <timestamp> InkML Element. ** Note **: Implementation is not complete.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0 Creation date : 19-May-2007
 */

public class Timestamp implements ContextElement {
    // @@@ Note: Implementation of this class will be provided later @@@
    private String id = "";
    private static Logger logger = Logger.getLogger(Timestamp.class.getName());

    public Timestamp() {
        super();
    }

    /**
     * This method gives the "id" attribute value of <timestamp> element.
     * 
     * @return id String
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * method to set 'id' attribute
     * 
     * @param id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * This method gives the type of this Ink element object which is the class name of this object.
     * 
     * @return the class name of this object as the Ink element type
     */
    @Override
    public String getInkElementType() {
        return "Timestamp";
    }

    public static Timestamp getDefaultTimestamp() {
        final Timestamp timestamp = new Timestamp();
        timestamp.setId("DefaultTimestamp");
        return timestamp;
    }

    /**
     * method to give inkml markup of the Timestamp data object as string *** Note ***: The method not implemented.
     * 
     * @return timestamp markup as string
     */
    @Override
    public String toInkML() {
        Timestamp.logger.warning("The timestamp.toInkML method not implemented.");
        return "";
    }

    /**
     * Method to archive timestamp XML data to file and other output stream *** Note ***: The method not implemented.
     * 
     * @param writer
     */
    @Override
    public void writeXML(final InkMLWriter writer) {
        Timestamp.logger.warning("The timestamp.writeXML method not implemented.");
        return;
    }

    /**
     * Method to override timestamp value from the value of the timestamp in the parameter *** Note ***: The method not implemented.
     * 
     * @param source
     */
    public void override(final Timestamp source) {
        Timestamp.logger.warning("The timestamp.override method not implemented.");
        return;
    }
}
