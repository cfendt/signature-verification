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

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * This class models the {@code <canvas>} element in InkML. The {@code <canvas>} element provides the virtual coordinate system, which uniquely identifies a
 * shared virtual space for cooperation of ink applications. It contains the traceFormat of the virtaul space. Please refer,
 * http://www.w3.org/TR/InkML/#canvasElement for more details.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0 Creation date : 11-May-2007
 */
public final class Canvas implements InkElement {
    private HashMap<String, String> attributesMap;
    private String id = "";
    private String traceFormatRef = "";
    private TraceFormat traceFormat;

    // Create logger instance for logging
    private static Logger logger = Logger.getLogger(Canvas.class.getName());

    public Canvas() {
        super();
        this.traceFormat = TraceFormat.getDefaultTraceFormat();
    }

    public Canvas(final TraceFormat traceFormat) throws InkMLException {
        this("", traceFormat);
    }

    public Canvas(final String id, final TraceFormat traceFormat) throws InkMLException {
        super();
        if (null != id) {
            this.id = id;
        }
        if (traceFormat == null) {
            throw new InkMLException("Can not create Canvas object with null traceformat");
        }
        this.traceFormat = traceFormat;
    }

    /*
     * (non-Javadoc)
     * @see com.hp.hpl.inkml.InkElement#getId()
     */
    @Override
    public String getId() {
        return this.id;
    }

    /*
     * (non-Javadoc)
     * @see com.hp.hpl.inkml.InkElement#getInkElementType()
     */
    @Override
    public String getInkElementType() {
        return "Canvas";
    }

    public static Canvas getDefaultCanvas() {
        Canvas defaultCanvas = null;
        try {
            defaultCanvas = new Canvas("DefaultCanvas", TraceFormat.getDefaultTraceFormat());
        } catch (final InkMLException e) {
            Canvas.logger.severe("Default TraceFormat is null.");
        }
        return defaultCanvas;
    }

    public TraceFormat getTraceFormat() {
        return this.traceFormat;
    }

    public void setAttribute(final String attrName, final String attrValue) {
        if (this.attributesMap == null) {
            this.attributesMap = new HashMap<String, String>();
        }
        this.attributesMap.put(attrName, attrValue);
    }

    public void setTraceFormat(final TraceFormat traceFormat) {
        this.traceFormat = traceFormat;
    }

    public boolean equals(final Canvas canvas) {
        if (canvas == null) {
            return false;
        }
        return this.traceFormat.equals(canvas.traceFormat);
    }

    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public String toInkML() {
        String canvasElement = "<canvas ";
        if (!"".equals(this.id)) {
            canvasElement += "id='" + this.id + "' ";
        }
        String traceFormatChild = null;
        if (!"".equals(this.traceFormatRef)) {
            canvasElement += "traceFormatRef='" + this.id + "' ";
        } else {
            traceFormatChild = this.traceFormat.toInkML();
        }
        canvasElement += ">";
        if (null != traceFormatChild) {
            canvasElement += traceFormatChild;
        }
        canvasElement += "</canvas>";
        return canvasElement;
    }

    public String getTraceFormatRef() {
        return this.traceFormatRef;
    }

    public void setTraceFormatRef(final String traceFormatRef) {
        this.traceFormatRef = traceFormatRef;
    }

    @Override
    public void writeXML(final InkMLWriter writer) {
        HashMap<String, String> attrs;
        if (!"".equals(this.id) || !"".equals(this.traceFormatRef)) {
            attrs = new HashMap<String, String>();
            if (!"".equals(this.id)) {
                attrs.put("id", this.id);
            }
            if (!"".equals(this.traceFormatRef)) {
                attrs.put("traceFormatRef", this.traceFormatRef);
            }

        } else {
            attrs = null;
        }
        if (null != this.traceFormat) {
            writer.writeStartTag("canvas", attrs);
            writer.incrementTagLevel();
            this.traceFormat.writeXML(writer);
            writer.decrementTagLevel();
            writer.writeEndTag("canvas");
        } else {
            writer.writeEmptyStartTag("canvas", attrs);
        }
    }
}
