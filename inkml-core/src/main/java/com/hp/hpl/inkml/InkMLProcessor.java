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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * In summary, it provides operations for an ink application to get/set/modify/remove and save(archive) the ink data. The processing operation involves the
 * following sub tasks, 1. Parse the InkML XML data using an XML Parser. 2. Bind the XML data to InkML data objects. 3. Interpret/process the data as per the
 * InkML specification. In the process it prepares the data to a state that it is ready for the Ink application to consume it.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0 Create Date: 27-July-2007
 */
public class InkMLProcessor {
    // Create the listener list
    private final ArrayList<InkMLEventListener> listenerList = new ArrayList<InkMLEventListener>();
    private Ink ink;
    // private InkMLProcessorConfig config; -- will be used while implementing Factory for Parser creation etc.
    // private String defaultParser="com.hp.hpl.inkml.InkMLDOMParser"; -- will be used while implementing Factory for Parser creation etc.

    // Create logger instance for logging. Using JDK >1.4 logger
    private static final Logger LOG = Logger.getLogger(InkMLProcessor.class.getName());

    /**
     * This methods allows observer classes to register to listen for InkML events.
     * 
     * @param listener
     */
    public void addInkMLEventListener(final InkMLEventListener listener) {
        this.listenerList.add(listener);
    }

    /**
     * This methods allows observer classes to unregister from listening to InkML events
     * 
     * @param listener
     */
    public void removeInkMLEventListener(final InkMLEventListener listener) {
        this.listenerList.remove(listener);
    }

    // Parsing Functions
    /**
     * Method to parse an InkMLFile.
     * 
     * @param inkmlFileName
     * @throws InkMLException
     */
    public void parseInkMLFile(final String inkmlFileName) throws InkMLException {
        this.ink = new Ink();
        final InkMLDOMParser parser = new InkMLDOMParser(this);
        parser.parseInkMLFile(inkmlFileName);
    }

    /**
     * Method to parse an InkMLFile.
     * 
     * @param inkmlFile
     * @throws InkMLException
     */
    public void parseInkMLFile(final InputStream inkmlFile) throws InkMLException {
        this.ink = new Ink();
        final InkMLDOMParser parser = new InkMLDOMParser(this);
        parser.parseInkMLFile(inkmlFile);
    }

    /**
     * This method parses and bind the InkML data given as String. It is useful in streaming scenario based applications. A complete InkML document that has
     * {@code <ink>} as root element can be given as input. And InkML fragments : a collection of well-formed child elements of InkML:Ink element, without
     * providing the root element {@code (<ink>)} can be given as input. e.g.:
     * {@code <definitions> <brush>...</brush> </definitions> <trace> ... </trace> <traceGroup> ... </traceGroup>}
     * 
     * @param inkmlString string
     * @throws InkMLException
     */
    public void parseInkMLString(final String inkmlString) throws InkMLException {
        if (this.ink == null) {
            throw new InkMLException("ParseInk operation terminated. Reason: No active Ink session available.");
        }
        final InkMLDOMParser parser = new InkMLDOMParser(this);
        parser.parseInkMLString(inkmlString);
    }

    /**
     * Method to notify the application components that listen for InkML event chanages which is Context Change in this method.
     * 
     * @param context the new context object
     * @param ctxChanges the context change status to indicate which contextual element changed.
     * @see #addInkMLEventListener(InkMLEventListener)
     * @see Ink.contextChangeStatus
     */
    public void notifyContextChanged(final Context context, final ArrayList<Ink.contextChangeStatus> ctxChanges) {
        InkMLProcessor.LOG.finer("To notify - context changed");
        if (0 == this.listenerList.size()) {
            return;
        }
        if (0 != ctxChanges.size()) { // some context change occured
            for (int i = 0; i < ctxChanges.size(); i++) {
                if (ctxChanges.get(i) == Ink.contextChangeStatus.isBrushChanged) {
                    this.notifyBrushChangedEvent(context.getBrush());
                } else if (ctxChanges.get(i) == Ink.contextChangeStatus.isCanvasChanged) {
                    this.notifyCanvasChangedEvent(context.getCanvas());
                } else if (ctxChanges.get(i) == Ink.contextChangeStatus.isCanvasTransformChanged) {
                    this.notifyCanvasTransformChangedEvent(context.getCanvasTransform());
                } else if (ctxChanges.get(i) == Ink.contextChangeStatus.isInkSourceChanged) {
                    this.notifyInkSourceChangedEvent(context.getInkSource());
                } else if (ctxChanges.get(i) == Ink.contextChangeStatus.isTimestampChanged) {
                    this.notifyTimestampChangedEvent(context.getTimestamp());
                } else if (ctxChanges.get(i) == Ink.contextChangeStatus.isTraceFormatChanged) {
                    this.notifyTraceFormatChangedEvent(context.getTraceFormat());
                }
            }
        }
    }

    /**
     * Method to notify the application components that listen for InkML event chanages - Trace received notification.
     * 
     * @param trace trace data object that received
     * @see #addInkMLEventListener(InkMLEventListener)
     */
    public void notifyTraceReceived(final Trace trace) {
        InkMLProcessor.LOG.finer("To notify - trace received");
        if (0 == this.listenerList.size()) {
            return;
        }
        final InkMLEventListener[] listeners = this.listenerList.toArray(new InkMLEventListener[this.listenerList.size()]);
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].traceReceivedEvent(trace);
        }
    }

    /**
     * Method to notify the application components that listen for InkML event chanages - Trace View received notification.
     * 
     * @param traceView the traceView data object that received
     * @see #addInkMLEventListener(InkMLEventListener)
     */
    public void notifyTraceViewReceived(final TraceView traceView) {
        if (0 == this.listenerList.size()) {
            return;
        }
        final InkMLEventListener[] listeners = this.listenerList.toArray(new InkMLEventListener[this.listenerList.size()]);
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].traceViewReceivedEvent(traceView);
        }
    }

    /**
     * Method to notify the application components that listen for InkML event chanages - Trace Group received notification.
     * 
     * @param traceGroup the TraceGroup data object that received
     * @see #addInkMLEventListener(InkMLEventListener)
     */
    public void notifyTraceGroupReceived(final TraceGroup traceGroup) {
        if (0 == this.listenerList.size()) {
            return;
        }
        final InkMLEventListener[] listeners = this.listenerList.toArray(new InkMLEventListener[this.listenerList.size()]);
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].traceGroupReceivedEvent(traceGroup);
        }
    }

    /**
     * Method to notify the application components that listen for InkML event chanages - Brush changed notification.
     * 
     * @param brush the Brush data object that received
     * @see #addInkMLEventListener(InkMLEventListener)
     */
    public void notifyBrushChangedEvent(final Brush brush) {
        InkMLProcessor.LOG.finer("To notify - brush changed");
        final InkMLEventListener[] listeners = this.listenerList.toArray(new InkMLEventListener[this.listenerList.size()]);
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].brushChangedEvent(brush);
        }
    }

    /**
     * Method to notify the application components that listen for InkML event chanages - InkSource changed notification.
     * 
     * @param inkSource the InkSource data object that received
     * @see #addInkMLEventListener(InkMLEventListener)
     */
    public void notifyInkSourceChangedEvent(final InkSource inkSource) {
        final InkMLEventListener[] listeners = this.listenerList.toArray(new InkMLEventListener[this.listenerList.size()]);
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].inkSourceChangedEvent(inkSource);
        }
    }

    /**
     * Method to notify the application components that listen for InkML event chanages - TraceFormat changed notification.
     * 
     * @param traceFormat the TraceFormat data object that received
     * @see #addInkMLEventListener(InkMLEventListener)
     */
    public void notifyTraceFormatChangedEvent(final TraceFormat traceFormat) {
        final InkMLEventListener[] listeners = this.listenerList.toArray(new InkMLEventListener[this.listenerList.size()]);
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].traceFormatChangedEvent(traceFormat);
        }
    }

    /**
     * Method to notify the application components that listen for InkML event chanages - Canvas changed notification.
     * 
     * @param canvas the Canvas data object that received
     * @see #addInkMLEventListener(InkMLEventListener)
     */
    public void notifyCanvasChangedEvent(final Canvas canvas) {
        final InkMLEventListener[] listeners = this.listenerList.toArray(new InkMLEventListener[this.listenerList.size()]);
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].canvasChangedEvent(canvas);
        }
    }

    /**
     * Method to notify the application components that listen for InkML event chanages - CanvasTransform changed notification.
     * 
     * @param canvasTransform the CanvasTransform data object that received
     * @see #addInkMLEventListener(InkMLEventListener)
     */
    public void notifyCanvasTransformChangedEvent(final CanvasTransform canvasTransform) {
        final InkMLEventListener[] listeners = this.listenerList.toArray(new InkMLEventListener[this.listenerList.size()]);
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].canvasTransformChangedEvent(canvasTransform);
        }
    }

    /**
     * Method to notify the application components that listen for InkML event chanages - Timestamp changed notification.
     * 
     * @param timestamp the Timestamp data object that received
     * @see #addInkMLEventListener(InkMLEventListener)
     */
    public void notifyTimestampChangedEvent(final Timestamp timestamp) {

        final InkMLEventListener[] listeners = this.listenerList.toArray(new InkMLEventListener[this.listenerList.size()]);
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].timestampChangedEvent(timestamp);
        }
    }

    /**
     * Method gives the status of the Ink data object being handled by the InkML Processor.
     * 
     * @return status if the Ink data object is null or not
     */
    public boolean isInkNull() {
        return this.ink == null ? true : false;
    }

    /**
     * Method to get the Ink data object of the current ink session or in other words Ink data object of that of the InkML document under processing either in
     * full or as fragment by fragment.
     * 
     * @return the ink object of the current ink session
     */
    public Ink getInk() {
        return this.ink;
    }

    /**
     * Method to set an ink data object that has to be used by the processor to build upon with the data binding from the upcoming parse functions of the given
     * InkML XML data. It is an alternate way to the 'beginInkSession()' method which assign a blank ink data object.
     * 
     * @param ink
     * @see #beginInkSession()
     * @see #resetInkSession()
     * @see #endInkSession()
     * @see #saveInkSession(String)
     */
    public void loadInkSession(final Ink ink) {
        this.ink = ink;
    }

    /**
     * Method to set a blank ink data object that has to be used by the processor to build upon with the data binding from the upcoming parse functions of the
     * given InkML XML data. It is an alternate way to the 'loadInkSession(Ink)' method which may assign a non-blank ink data object.
     * 
     * @see #loadInkSession(Ink)
     * @see #resetInkSession()
     * @see #endInkSession()
     * @see #saveInkSession(String)
     */
    public void beginInkSession() {
        this.ink = new Ink();
    }

    /**
     * Method to set a null ink data object to indicate the closing of an Ink session to the processor.
     * 
     * @see #loadInkSession(Ink)
     * @see #beginInkSession()
     * @see #resetInkSession()
     * @see #saveInkSession(String)
     */
    public void endInkSession() {
        this.ink = null;
    }

    /**
     * Method to replace a fresh blank ink data object instead of the current Ink data object being used by the processor to build upon with the data binding
     * from the upcoming parse functions of the given InkML XML data. It is an alternate way to the 'loadInkSession(Ink)' method which may assign a non-blank
     * ink data object.
     * 
     * @see #loadInkSession(Ink)
     * @see #beginInkSession()
     * @see #endInkSession()
     * @see #saveInkSession(String)
     */
    public void resetInkSession() {
        this.ink = new Ink();
    }

    /**
     * Method to serialize (archive) the current active Ink data object being used by the processor to the file which file path that identified by the
     * 'fileName' parameter. The default encoding is UTF8.
     * 
     * @param fileName the InkML file for saving the InkML ink data.
     * @throws IOException
     * @throws InkMLException
     */
    public void saveInkSession(final String fileName) throws FileNotFoundException, UnsupportedEncodingException, IOException, InkMLException {
        if (this.ink == null) {
            throw new InkMLException("SaveInkSession operation terminated. Reason: No active Ink session available.");
        }
        final InkMLWriter writer = new InkMLWriter(fileName);
        writer.writeProcessingInstruction();
        this.ink.writeXML(writer);
        writer.close();
    }

    /**
     * Method to serialize (archive) the current active Ink data object being used by the processor to the file which file path that identified by the
     * 'fileName' parameter.
     * 
     * @param fileName
     * @param encoding
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws InkMLException
     */
    public void saveInkSession(final String fileName, final String encoding) throws FileNotFoundException, UnsupportedEncodingException, IOException, InkMLException {
        if (this.ink == null) {
            throw new InkMLException("SaveInkSession operation terminated. Reason: No active Ink session available.");
        }
        final InkMLWriter writer = new InkMLWriter(fileName, encoding);
        writer.writeProcessingInstruction();
        this.ink.writeXML(writer);
        writer.close();
    }

    /**
     * Method to serialize (archive) the current active Ink data object being used by the processor to the writer object.
     * 
     * @param writer
     * @throws IOException
     * @throws InkMLException
     */
    public void saveInkSession(final java.io.Writer writer) throws IOException, InkMLException {
        if (this.ink == null) {
            throw new InkMLException("SaveInkSession operation terminated. Reason: No active Ink session available.");
        }
        final InkMLWriter inkMLWriter = new InkMLWriter(writer);
        inkMLWriter.writeProcessingInstruction();
        this.ink.writeXML(inkMLWriter);
        inkMLWriter.close();
    }

    /**
     * Method to serialize (archive) the current active Ink data object being used by the processor to the stream object and the given encoding is used.
     * 
     * @param stream
     * @param encoding
     * @throws UnsupportedEncodingException
     * @throws InkMLException
     * @throws IOException
     */
    public void saveInkSession(final OutputStream stream, final String encoding) throws UnsupportedEncodingException, InkMLException, IOException {
        if (this.ink == null) {
            throw new InkMLException("SaveInkSession operation terminated. Reason: No active Ink session available.");
        }
        final InkMLWriter inkMLWriter = new InkMLWriter(stream, encoding);
        inkMLWriter.writeProcessingInstruction();
        this.ink.writeXML(inkMLWriter);
        inkMLWriter.close();
    }

    /**
     * Method to serialize (archive) the current active Ink data object being used by the processor to the stream. The default encoding is UTF8.
     * 
     * @param stream
     * @throws UnsupportedEncodingException
     * @throws InkMLException
     * @throws IOException
     */
    public void saveInkSession(final OutputStream stream) throws UnsupportedEncodingException, InkMLException, IOException {
        if (this.ink == null) {
            throw new InkMLException("SaveInkSession operation terminated. Reason: No active Ink session available.");
        }
        final InkMLWriter inkMLWriter = new InkMLWriter(stream);
        inkMLWriter.writeProcessingInstruction();
        this.ink.writeXML(inkMLWriter);
        inkMLWriter.close();
    }

}
