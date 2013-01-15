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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

/**
 * This class models the <ink> element (root element) of InkML document.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0 Creation date : 7th May, 2007
 */

public final class Ink {

    public static final String INKML_NAMESPACE = "http://www.w3.org/2003/InkML";

    public static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema-datatypes";

    /**
     * This indicate what kind of context change happened. The change in context happens wheneither or many of the following happens, brush, traceFormat,
     * inkSource, canvas, canvasTransform, timestamp. Default value none which is used to indicate no change in context.
     */
    public enum ContextChangeStatus {
        /**
         * Brush is changed
         */
        isBrushChanged,
        /**
         * traceFormat is changed
         */
        isTraceFormatChanged,
        /**
         * inkSource is changed
         */
        isInkSourceChanged,
        /**
         * canvas is changed
         */
        isCanvasChanged,
        /**
         * canvasTransform is changed
         */
        isCanvasTransformChanged,
        /**
         * timestamp is changed
         */
        isTimestampChanged,
        /**
         * no context change
         */
        NONE
    };

    private LinkedList<TraceDataElement> traceDataList;
    private final Definitions definitions;
    private Context currentContext;
    private Annotation annotation;
    private AnnotationXML annotationXML;
    // Create logger instance for logging
    private static Logger logger = Logger.getLogger(Ink.class.getName());

    /** ID du document */
    private String docID = "";

    /**
     * No argument Constructor creates a 'blank' InkML Ink object.
     */
    public Ink() {
        this.definitions = new Definitions();
        this.currentContext = Context.getDefaultContext();
        this.traceDataList = new LinkedList<TraceDataElement>();
    }

    /**
     * Compare the Context object in the parameter with the current context of this Ink Object and returns the status.
     * 
     * @param context Context object to be compared with.
     * @throws InkMLException
     */
    public List<ContextChangeStatus> getContextChanges(final Context context) throws InkMLException {
        final ArrayList<ContextChangeStatus> ctxStatus = new ArrayList<ContextChangeStatus>();
        if (null == context) {
            return ctxStatus;
        }

        // check and populate the status of all the contextual elements
        final Brush brush = context.getBrush();
        final TraceFormat traceFormat = context.getTraceFormat();
        final InkSource inkSource = context.getInkSource();
        final Canvas canvas = context.getCanvas();
        final CanvasTransform canvasTransform = context.getCanvasTransform();
        final Timestamp timestamp = context.getTimestamp();

        if (brush != null && !this.currentContext.getBrush().equals(brush)) {
            ctxStatus.add(ContextChangeStatus.isBrushChanged);
        }
        if (traceFormat != null && !this.currentContext.getTraceFormat().equals(traceFormat)) {
            ctxStatus.add(ContextChangeStatus.isTraceFormatChanged);
        }
        if (inkSource != null && !this.currentContext.getInkSource().equals(inkSource)) {
            ctxStatus.add(ContextChangeStatus.isInkSourceChanged);
        }
        if (canvas != null && !this.currentContext.getCanvas().equals(canvas)) {
            ctxStatus.add(ContextChangeStatus.isCanvasChanged);
        }
        if (canvasTransform != null && !this.currentContext.getCanvasTransform().equals(canvasTransform)) {
            ctxStatus.add(ContextChangeStatus.isCanvasTransformChanged);
        }
        if (timestamp != null && !this.currentContext.getTimestamp().equals(timestamp)) {
            ctxStatus.add(ContextChangeStatus.isTimestampChanged);
        }
        return ctxStatus;
    }

    /**
     * Method to add a trace or traceGroup or a traceView to Ink document data object
     * 
     * @param traceData
     */
    public void addToTraceDataList(final TraceDataElement traceData) {
        this.traceDataList.add(traceData);
    }

    /**
     * This method gives an Iterator for the List of Trace objects that - the Ink object contains. It includes the Trace object contained in Trace collections
     * such as TraceGroup and TraceView.
     * 
     * @return The Itearator object that can be used to navigate the list of - Trace objects that contained within the current Ink document.
     * @throws InkMLException
     */
    public Iterator<Trace> getTraceIterator() throws InkMLException {
        final ArrayList<Trace> traceList = new ArrayList<Trace>();
        if (this.traceDataList != null) {
            final Iterator<TraceDataElement> inkChildrenListIterator = this.traceDataList.iterator();
            while (inkChildrenListIterator.hasNext()) {
                final InkElement object = inkChildrenListIterator.next();
                final String inkElmntType = object.getInkElementType();
                if ("Trace".equals(inkElmntType)) {
                    traceList.add((Trace) object);
                }
                if ("TraceGroup".equals(inkElmntType)) {
                    traceList.addAll(((TraceGroup) object).getTraceList());
                }
                if ("TraceGroup".equals(inkElmntType)) {
                    traceList.addAll(((TraceView) object).getTraceList());
                }
            }
        }
        return traceList.iterator();
    }

    /**
     * This method gives the global definition state that is associated with - the current <ink> document.
     * 
     * @return the Definition object.
     */
    public Definitions getDefinitions() {
        return this.definitions;
    }

    /**
     * This method gives the 'ID' of the current Ink Document's root <ink> element ID.
     * 
     * @return the ID string of the <ink> element.
     */
    public String getDocID() {
        return this.docID;
    }

    /**
     * Method to add a Trace
     * 
     * @param trace
     */
    public void addTrace(final Trace trace) {
        this.addToTraceDataList(trace);
    }

    /**
     * Method to add a TraceGroup
     * 
     * @param traceGroup
     */
    public void addTraceGroup(final TraceGroup traceGroup) {
        this.addToTraceDataList(traceGroup);
    }

    /**
     * Method to add a TraceView
     * 
     * @param traceView
     */
    public void addTraceView(final TraceView traceView) {
        this.addToTraceDataList(traceView);
    }

    /**
     * Method to empty the trace list
     */
    public void clearTraceDataList() {
        this.traceDataList.clear();
    }

    /**
     * @param traceData
     * @return boolean status
     */
    public boolean containsInTraceDataList(final TraceDataElement traceData) {
        return this.traceDataList.contains(traceData);
    }

    /**
     * @param traceData collection
     * @return boolean status
     */
    public boolean containsAllInTraceDataList(final Collection<TraceDataElement> traceData) {
        return this.traceDataList.containsAll(traceData);
    }

    /**
     * Method to check if traceData list of Ink is empty
     * 
     * @return boolean status
     */
    public boolean isTraceDataListEmpty() {
        return this.traceDataList.isEmpty();
    }

    /**
     * Method to remove traceData at givem index parameter
     * 
     * @param index
     * @return InkElement
     */
    public InkElement removeFromTraceDataList(final int index) {
        return this.traceDataList.remove(index);
    }

    /**
     * Method to remove given traceData in the parameter
     * 
     * @param traceData to be removed
     * @return status
     * @see java.util.ArrayList#remove(java.lang.Object)
     */
    public boolean removeFromTraceDataList(final TraceDataElement traceData) {
        return this.traceDataList.remove(traceData);
    }

    /**
     * Method to remove all traceData instances given in the parameter
     * 
     * @param traceDataList
     * @return boolean
     * @see java.util.AbstractCollection#removeAll(java.util.Collection)
     */
    public boolean removeAllTraceData(final Collection<TraceDataElement> traceDataList) {
        return traceDataList.removeAll(traceDataList);
    }

    /**
     * Method to remove other traceData but retain them given in the parameter
     * 
     * @param traceDataList collection
     * @return boolean status
     * @see java.util.AbstractCollection#retainAll(java.util.Collection)
     */
    public boolean retainAllTraceData(final Collection<TraceDataElement> traceDataList) {
        return traceDataList.retainAll(traceDataList);
    }

    /**
     * Method to get the global 'current context' in the scope of the ink document data object
     * 
     * @return Context
     */

    public Context getCurrentContext() {
        return this.currentContext;
    }

    /**
     * Method to set the documentID
     * 
     * @param String the documentID URI as string
     */
    void setDocID(final String id) {
        this.docID = id;
    }

    /**
     * Method to add annotation to the ink document, to add semantic label to it. It is an optional element and users can utilize this to tag the ink document
     * with some meta data information.
     * 
     * @param annotation data it corresponds to a textual description wrapped by {@code <annotation>} element in the markup.
     */
    public void addAnnotation(final Annotation annotation) {
        this.annotation = annotation;
    }

    /**
     * Method to add annotationXML data to the ink document, to add semantic label to it. It is an optional element and users can utilize this to tag the ink
     * document with some meta data information in XML format.
     * 
     * @param aXml AnnotationXML data, it corresponds to an XML tree wrapped by {@code <annotationXML>} element in the markup.
     * @see com.hp.hpl.inkml.AnnotationXML
     */
    public void addAnnotationXML(final AnnotationXML aXml) {
        this.annotationXML = aXml;
    }

    /**
     * Method to get the list of trace, traceGroup and traceView data that belongs to the ink data object
     * 
     * @return LinkedList<TraceDataElement>
     */
    public LinkedList<TraceDataElement> getTraceDataList() {
        return this.traceDataList;
    }

    /**
     * Method to get the annotation/label of the ink document data object
     * 
     * @return Annotation
     */
    public Annotation getAnnotation() {
        return this.annotation;
    }

    /**
     * Method to assign an annotation, which is also called as semantic label.
     * 
     * @param annotation the annotation to set
     */
    public void setAnnotation(final Annotation annotation) {
        this.annotation = annotation;
    }

    /**
     * Method to get the annotationXML/label XML data of the ink document data object
     * 
     * @return the annotationXML
     */
    public AnnotationXML getAnnotationXML() {
        return this.annotationXML;
    }

    /**
     * Method to assign an annotationXML, which is also called as semantic label XML data.
     * 
     * @param annotationXML the annotationXML to set
     */
    public void setAnnotationXML(final AnnotationXML annotationXML) {
        this.annotationXML = annotationXML;
    }

    /**
     * Method to assign a list of trace data to the ink document. Any existing trace data is overwritten by the new list.
     * 
     * @param traceDataList the traceDataList to set
     */
    public void setTraceDataList(final LinkedList<TraceDataElement> traceDataList) {
        this.traceDataList = traceDataList;
    }

    /**
     * Method to set the associated current context object.
     * 
     * @param context
     */
    public void setCurrentContext(final Context context) {
        this.currentContext = context;
    }

    /**
     * Method used by the Archiver component (InkMLWriter) to save the markup data of the Ink data object to file or other data stream
     * 
     * @param writer
     */

    public void writeXML(final InkMLWriter writer) throws IOException, InkMLException {
        if (writer == null) {
            Ink.logger.severe("Ink:writeXML, InkMLWriter object not available (null)!!!");
            throw new InkMLException("Ink:writeXML, InkMLWriter object not available (null)!!!");
        }
        final LinkedHashMap<String, String> attrMap = new LinkedHashMap<String, String>();
        if (StringUtils.isNotEmpty(this.docID)) {
            attrMap.put("documentID", this.docID);
        }
        attrMap.put("xmlns", Ink.INKML_NAMESPACE);
        writer.writeStartTag("ink", attrMap);
        writer.incrementTagLevel();
        // write definitions
        this.definitions.writeXML(writer);

        // write Trace data list
        final Iterator<TraceDataElement> iterator = this.traceDataList.iterator();
        while (iterator.hasNext()) {
            final TraceDataElement data = iterator.next();
            data.writeXML(writer);
        }
        writer.decrementTagLevel();
        writer.writeEndTag("ink");
    }

    /**
     * Method to give the markup string data of the Ink data object
     * 
     * @return String markup string
     */
    public String toInkML() {
        final StringBuffer xml = new StringBuffer();
        final LinkedHashMap<String, String> attrMap = new LinkedHashMap<String, String>();
        if (StringUtils.isNotEmpty(this.docID)) {
            attrMap.put("documentID", this.docID);
        }
        xml.append("<ink xmlns=\"" + Ink.INKML_NAMESPACE + "\">");
        // write definitions
        xml.append(this.definitions.toInkML());

        // write Trace data list
        final Iterator<TraceDataElement> iterator = this.traceDataList.iterator();
        while (iterator.hasNext()) {
            final TraceDataElement data = iterator.next();
            xml.append(data.toInkML());
        }
        xml.append("</ink>");
        return xml.toString();
    }
}