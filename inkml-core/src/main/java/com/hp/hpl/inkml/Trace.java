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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class models the {@code <trace>} InkML element.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0 Creation date : 6-May-2007
 */

public class Trace implements TraceDataElement {
    private final HashMap<String, String> attributesMap;
    private LinkedHashMap<String, List<Object>> traceData;

    /**
     * This is used to qulaify if the trace is a penDown which means created using the pen in contact of the writing surface. pen up trace - trace created while
     * the pen is up for example capturing the hovering over the wrting surface and the last possible value is indeterminate which means the information not
     * available on if it is a pen down trace or pen up trace. The default value is penDown.
     */
    public enum TraceType {
        /**
         * penDown means that the trace created using the pen in contact of the writing surface.
         */
        penDown,
        /**
         * trace created while the pen is up for example capturing the hovering over the writing surface.
         */
        penUp,
        /**
         * the information not available on if it is a pen down trace or pen up trace.
         */
        indeterminate
    }

    /**
     * To indicate the Continuation type of the trace, if it is a continution fragment trace of a huge trace. Possible values - begin mens that it is the first
     * trace, end means that it is the last trace, middle means that it an intermediate trace and none means that the trace is stand alone and not part of a
     * Continuation trace. Default value - Continuation.NONE
     */
    public enum Continuation {
        /**
         * Not a Continuation trace
         */
        NONE,
        /**
         * The first trace in the series.
         */
        begin,
        /**
         * The last trace.
         */
        end,
        /**
         * An intermediate trace.
         */
        middle
    }

    // continution type
    private final Continuation continuation = Continuation.NONE;

    /**
     * Trace prefix are used to encode trace data using the single or double difference notations in order to reduce the size of mark data only by stroing the
     * differences of subsequent sample ponits data.
     */
    public enum TracePrefix {
        /**
         * None - the default value
         */
        NONE,
        /**
         * single difference; it is denoted by ' as the prefix
         */
        velocity,
        /**
         * double difference; it is denoted by " as the prefix
         */
        acceleration,
        /**
         * explicit data value; it is denoted by ! as the prefix
         */
        explicit
    }

    private HashMap<String, TracePrefix> lastPrefixMap;

    // used to specify the trace to which the current trace is a continution
    private final Trace priorReferredTrace = null;

    // private String contextRef="";
    private Context associatedContext;
    // private String brushRef="";
    private TraceGroup parentTraceGroup;

    // Create logger instance for logging
    private static Logger logger = Logger.getLogger(Trace.class.getName());

    /**
     * No argument constructor. It is being used to construct the 'trace' object that comes as the result of a 'traceView' selection operation.
     */
    public Trace() {
        this.attributesMap = new HashMap<String, String>();
        // this.associatedContext = Context.getDefaultContext();
    }

    // initialize data structures required for processing the trace data text value
    private void initTraceDataStructure() {
        if (null != this.associatedContext) {
            final TraceFormat traceFormat = this.associatedContext.getTraceFormat();
            final ArrayList<Channel> channelList = traceFormat.getChannelList();
            final int nChannel = channelList.size();
            this.traceData = new LinkedHashMap<String, List<Object>>();
            for (int i = 0; i < nChannel; i++) {
                final Channel chn = channelList.get(i);
                final String channelName = chn.getName();
                this.traceData.put(channelName, new ArrayList<Object>());
            }
        }
    }

    /**
     * To get the template (blue-print) of the trace with current context state, for loading the trace data arrived in sub selection of trace in traceView
     * select command.
     * 
     * @return new trace object with all context properties of the current trace but with empty data.
     */
    public Trace getTraceTemplate() {
        final TraceFormat traceFormat = this.getAssociatedContext().getTraceFormat();
        final ArrayList<Channel> channelList = traceFormat.getChannelList();
        final int nChannel = channelList.size();
        final LinkedHashMap<String, List<Object>> newTraceData = new LinkedHashMap<String, List<Object>>();
        for (int i = 0; i < nChannel; i++) {
            final Channel chn = channelList.get(i);
            final String channelName = chn.getName();
            newTraceData.put(channelName, new ArrayList<Object>());
        }
        final Trace traceObject = new Trace();
        // To Do: add contextRef and brushRef
        traceObject.setAssociatedContext(this.associatedContext);
        traceObject.setTraceData(newTraceData);
        return traceObject;
    }

    /**
     * Constructor to create Trace InkML object from the trace DOM element
     * 
     * @param element Trace DOM element
     * @param context The current context object that associated with the containing Ink document
     * @param definitions The global definition state associated with the containing Ink document
     * @throws InkMLException
     * @throws InkMLException
     */
    void resolveAssociatedContext(final Context currentContext, final Definitions defs) throws InkMLException {
        final String contextRefAttr = this.getContextRef();
        if (!"".equals(contextRefAttr)) {
            this.associatedContext = defs.getContextRefElement(contextRefAttr);
        } else {
            if (null != this.parentTraceGroup) {
                this.associatedContext = this.parentTraceGroup.getAssociatedContext();
            }
            this.associatedContext = null != this.associatedContext ? this.associatedContext : null != currentContext ? currentContext : Context.getDefaultContext();
            this.setContextRef("#" + this.associatedContext.getId());
        }

        final String brushRefAttr = this.getBrushRef();
        if (!"".equals(brushRefAttr)) {
            this.associatedContext.setBrush(defs.getBrushRefElement(brushRefAttr));
            // associatedContext.getBrush().override(
            // defs.getBrushRefElement(brushRefAttr));
        }
        this.initTraceDataStructure();
    }

    private void setContextRef(final String contextRef) {
        if (!"".equals(contextRef)) {
            this.attributesMap.put("contextRef", contextRef);
        }
        ;
    }

    // used in processing the single, double difference trace prefix notations
    private TracePrefix getLastPrefixOf(final String channelName) {
        return this.lastPrefixMap.get(channelName);
    }

    /*
     * This method process the Trace Sample data to compute the absolute value after processing any prefix if uesd.
     */
    private void processAndStoreChannelValue(final String channelName, final String channelValue, final Channel.ChannelType channelType, final int traceIndex,
            final HashMap<String, Object> variableValueMap, Object previousDoubleDiffVal) throws InkMLException {
        int mCharPos;
        Object chnVal;
        if ((mCharPos = channelValue.indexOf('!')) >= 0) {
            if (channelType == Channel.ChannelType.BOOLEAN) {
                // logger.error("The prefix <!> is NOT applicable to BOOLEAN channel.");
                throw new InkMLException("The prefix <!> is NOT applicable to BOOLEAN channel.");
            }
            chnVal = this.getChannelValueObject(channelValue.substring(mCharPos + 1), channelType);
            // traceData.get(channelName).add(traceIndex, chnVal);
        } else if ((mCharPos = channelValue.indexOf('\'')) >= 0) {
            if (channelType == Channel.ChannelType.BOOLEAN) {
                // logger.error("The prefix <'> is applicable to numerical channels only. The prefix is ignored.");
                throw new InkMLException("The prefix <'> is NOT applicable to BOOLEAN channel.");
                // Object chnVal = getChannelValueObject(channelValue.substring(mCharPos + 1), channelType);
                // traceData.get(channelName).add(traceIndex, chnVal);
                // return;
            }

            // To implement the condition that the prefix <'> must be preceded -
            // by an explicit encoding. If the traceSample value index is 0 -
            // means that it is not preceded by an explicit encoding.
            if (this.lastPrefixMap.get(channelName) != TracePrefix.NONE || this.continuation == Continuation.begin || 0 == traceIndex) {
                throw new InkMLException("The condition that the the prefix <'> must be preceded " + "with an explicit value is violated.\nProblem is in the channelValue " + channelValue
                        + " (channel name = " + channelName + ").");
            }

            // processing Single difference denoted by the prefix '
            this.lastPrefixMap.put(channelName, TracePrefix.velocity);
            final Object previousValue = this.traceData.get(channelName).get(traceIndex - 1);
            final Object singleDiffVal = this.getChannelValueObject(channelValue.substring(mCharPos + 1), channelType);
            variableValueMap.put(channelName, singleDiffVal);
            chnVal = null;
            if (channelType == Channel.ChannelType.DECIMAL) {
                chnVal = (Float) previousValue + (Float) singleDiffVal;
            } else if (channelType == Channel.ChannelType.INTEGER) {
                chnVal = (Integer) previousValue + (Integer) singleDiffVal;
                // traceData.get(channelName).add(traceIndex, chnVal);
            }
        } else if ((mCharPos = channelValue.indexOf('\"')) >= 0) {
            if (channelType == Channel.ChannelType.BOOLEAN) {
                // logger.error("The prefix <\"> is applicable to numerical channels only. The prefix is ignored.");
                throw new InkMLException("The prefix <\"> is NOT applicable to BOOLEAN channel.");
                // Object chnVal = getChannelValueObject(channelValue.substring(mCharPos + 1), channelType);
                // traceData.get(channelName).add(traceIndex, chnVal);
                // return;
            }

            // processing Double difference denoted by the prefix "

            // To implement the condition that the prefix <"> must be preceded -
            // with an 'single difference' or 'velocity' (the prefix <'>).
            // If the last prefix is velocity means that it is not preceded by -
            // a single difference.
            if (this.lastPrefixMap.get(channelName) != TracePrefix.velocity || this.continuation == Continuation.begin) {
                throw new InkMLException("The condition that the the prefix <\"> must be preceded " + "with a single difference is violated.\nProblem is in the channelValue " + channelValue
                        + " (channel name = " + channelName + ").");
            }
            this.lastPrefixMap.put(channelName, TracePrefix.acceleration);
            final Object doubleDiffVal = this.getChannelValueObject(channelValue.substring(mCharPos + 1), channelType);
            final Object currentVariableValue = variableValueMap.get(channelName);
            Object newVariableValue = null;
            if (channelType == Channel.ChannelType.DECIMAL) {
                newVariableValue = (Float) doubleDiffVal + (Float) currentVariableValue;
            } else if (channelType == Channel.ChannelType.INTEGER) {
                newVariableValue = (Integer) doubleDiffVal + (Integer) currentVariableValue;
            }
            variableValueMap.put(channelName, newVariableValue);
            final Object previousValue = this.traceData.get(channelName).get(traceIndex - 1);
            chnVal = null;
            if (channelType == Channel.ChannelType.DECIMAL) {
                chnVal = (Float) previousValue + (Float) newVariableValue;
            } else if (channelType == Channel.ChannelType.INTEGER) {
                chnVal = (Integer) previousValue + (Integer) newVariableValue;
            }
            // traceData.get(channelName).add(traceIndex, chnVal);
            previousDoubleDiffVal = doubleDiffVal;
        } else if ((mCharPos = channelValue.indexOf('?')) >= 0) {
            this.traceData.get(channelName).add(null);
            return;
        } else if ((mCharPos = channelValue.indexOf('*')) >= 0) {
            final Channel channel = this.getAssociatedContext().getTraceFormat().getChannel(channelName);
            if (channel.isIntermittent()) {
                Object previousValue = this.traceData.get(channelName).get(traceIndex - 1);
                int i = 2; // to start with last but one index data w.r.t current traceIndex,
                           // i.e. to start with traceIndex - i where i=2,3, .... until -
                           // we hit data at traceIndex=0 which must be != null.
                while (null == previousValue) {
                    previousValue = this.traceData.get(channelName).get(traceIndex - i);
                    i++;
                }
                if (null == previousValue) {
                    previousValue = channel.getDefaultValue();
                    if (channelType == Channel.ChannelType.DECIMAL) {
                        previousValue = new Float((String) previousValue);
                    } else if (channelType == Channel.ChannelType.INTEGER) {
                        previousValue = new Integer((String) previousValue);
                    } else if (channelType == Channel.ChannelType.BOOLEAN) {
                        if ("F".equalsIgnoreCase((String) previousValue)) {
                            previousValue = new Boolean("false");
                        } else if ("T".equalsIgnoreCase((String) previousValue)) {
                            previousValue = new Boolean("true");
                        }
                    }
                }
                chnVal = previousValue;
                this.validateRange(chnVal.toString(), channelName);
                this.traceData.get(channelName).add(traceIndex, chnVal);
                return;
            }
            if (channelType == Channel.ChannelType.BOOLEAN) {
                final Object previousValue = this.traceData.get(channelName).get(traceIndex - 1);
                chnVal = previousValue;
                // traceData.get(channelName).add(traceIndex, previousValue);
                // return;
            }
            // processing the prefix * which retains the previous value or -
            // previous difference prefix notion
            if (this.lastPrefixMap.get(channelName) == TracePrefix.velocity) {
                final Object singleDiffVal = variableValueMap.get(channelName);
                final Object previousValue = this.traceData.get(channelName).get(traceIndex - 1);
                chnVal = null;
                if (channelType == Channel.ChannelType.DECIMAL) {
                    chnVal = (Float) previousValue + (Float) singleDiffVal;
                } else if (channelType == Channel.ChannelType.INTEGER) {
                    chnVal = (Integer) previousValue + (Integer) singleDiffVal;
                    // traceData.get(channelName).add(traceIndex, chnVal);
                }
            } else if (this.lastPrefixMap.get(channelName) == TracePrefix.acceleration) {
                final Object doubleDiffVal = previousDoubleDiffVal;
                final Object currentVariableValue = variableValueMap.get(channelName);
                Object newVariableValue = null;
                if (channelType == Channel.ChannelType.DECIMAL) {
                    newVariableValue = (Float) doubleDiffVal + (Float) currentVariableValue;
                } else if (channelType == Channel.ChannelType.INTEGER) {
                    newVariableValue = (Integer) doubleDiffVal + (Integer) currentVariableValue;
                }
                variableValueMap.put(channelName, newVariableValue);
                final Object previousValue = this.traceData.get(channelName).get(traceIndex - 1);
                chnVal = null;
                if (channelType == Channel.ChannelType.DECIMAL) {
                    chnVal = (Float) previousValue + (Float) newVariableValue;
                } else if (channelType == Channel.ChannelType.INTEGER) {
                    chnVal = (Integer) previousValue + (Integer) newVariableValue;
                    // traceData.get(channelName).add(traceIndex, chnVal);
                }
            } else {
                final Object previousValue = this.traceData.get(channelName).get(traceIndex - 1);
                chnVal = previousValue;
                // traceData.get(channelName).add(traceIndex, previousValue);
            }
        } else {
            // processing data without any prefix

            // for boolean channel
            if (channelType == Channel.ChannelType.BOOLEAN) {
                chnVal = null;
                if ("F".equalsIgnoreCase(channelValue)) {
                    chnVal = new Boolean("false");
                } else if ("T".equalsIgnoreCase(channelValue)) {
                    chnVal = new Boolean("true");
                }
                this.traceData.get(channelName).add(traceIndex, chnVal);
                return;
            }

            // for numerical channel
            // check if the current prefix is single difference a.k.a velocity
            if (this.lastPrefixMap.get(channelName) == TracePrefix.velocity) {
                final Object previousValue = this.traceData.get(channelName).get(traceIndex - 1);
                Object singleDiffVal = null;
                if (channelType == Channel.ChannelType.DECIMAL) {
                    singleDiffVal = new Float(channelValue);
                } else if (channelType == Channel.ChannelType.INTEGER) {
                    singleDiffVal = new Integer(channelValue);
                }
                variableValueMap.put(channelName, singleDiffVal);
                chnVal = null;
                if (channelType == Channel.ChannelType.DECIMAL) {
                    chnVal = (Float) previousValue + (Float) singleDiffVal;
                } else if (channelType == Channel.ChannelType.INTEGER) {
                    chnVal = (Integer) previousValue + (Integer) singleDiffVal;
                    // traceData.get(channelName).add(traceIndex, chnVal);
                }
            } else if (this.lastPrefixMap.get(channelName) == TracePrefix.acceleration) {
                // check if the current prefix is double difference a.k.a acceleration
                Object doubleDiffVal = null;
                if (channelType == Channel.ChannelType.DECIMAL) {
                    doubleDiffVal = new Float(channelValue);
                } else if (channelType == Channel.ChannelType.INTEGER) {
                    doubleDiffVal = new Integer(channelValue);
                }
                final Object currentVariableValue = variableValueMap.get(channelName);
                Object newVariableValue = null;
                if (channelType == Channel.ChannelType.DECIMAL) {
                    newVariableValue = (Float) doubleDiffVal + (Float) currentVariableValue;
                } else if (channelType == Channel.ChannelType.INTEGER) {
                    newVariableValue = (Integer) doubleDiffVal + (Integer) currentVariableValue;
                }
                variableValueMap.put(channelName, newVariableValue);
                final Object previousValue = this.traceData.get(channelName).get(traceIndex - 1);
                chnVal = null;
                if (channelType == Channel.ChannelType.DECIMAL) {
                    chnVal = (Float) previousValue + (Float) newVariableValue;
                } else if (channelType == Channel.ChannelType.INTEGER) {
                    chnVal = (Integer) previousValue + (Integer) newVariableValue;
                }
                // traceData.get(channelName).add(traceIndex, chnVal);
                previousDoubleDiffVal = doubleDiffVal;
            } else {
                // It is an absolute value, meaning there is no 'prefix' used earlier
                chnVal = null;
                if (channelType == Channel.ChannelType.DECIMAL) {
                    chnVal = new Float(channelValue);
                } else if (channelType == Channel.ChannelType.INTEGER) {
                    chnVal = new Integer(channelValue);
                    // traceData.get(channelName).add(traceIndex, chnVal);
                }
            }
        }
        this.validateRange(chnVal.toString(), channelName);
        this.traceData.get(channelName).add(chnVal);
    }

    // To great channel value object according to the type of the Channel.
    private Object getChannelValueObject(final String channelValue, final Channel.ChannelType channelType) {
        Object chnVal = null;
        if (channelType == Channel.ChannelType.DECIMAL) {
            chnVal = new Float(channelValue);
        } else if (channelType == Channel.ChannelType.INTEGER) {
            chnVal = new Integer(channelValue);
        } else if (channelType == Channel.ChannelType.BOOLEAN) {
            chnVal = new Boolean(channelValue);
        }
        return chnVal;
    }

    /**
     * This method gives a list containg value belongs to all trace sample point - of Channel with name given in the parameter channelName
     * 
     * @param channalName The name of the Channel
     * @return List of Channel values
     * @throws InkMLException
     */
    public List<Object> getChannelValueList(final String channalName) throws InkMLException {
        if (!this.traceData.containsKey(channalName)) {
            throw new InkMLException("Invalid Channel Name (" + channalName + ").");
        }
        return this.traceData.get(channalName);
    }

    /**
     * This method gives the "type" attribute value of <trace> element.
     * 
     * @return TraceType value
     */
    public TraceType getType() {
        final String typeStr = this.attributesMap.get("type");
        if (null == typeStr) {
            return TraceType.penDown; // default value
        }
        return TraceType.valueOf(typeStr);
    }

    public void setType(final TraceType type) {
        final String typeStr = String.valueOf(type);
        this.attributesMap.put("type", typeStr);
    }

    /**
     * This method gives the "continuation" attribute value of <trace> element.
     * 
     * @return Continuation value
     */
    public Continuation getContinuation() {
        final String continuStr = this.attributesMap.get("continuation");
        if (null == continuStr) {
            return Continuation.NONE;
        }
        return Continuation.valueOf(continuStr);

    }

    public void setContinuation(final Continuation continuation) {
        final String continuStr = String.valueOf(continuation);
        this.attributesMap.put("continuation", continuStr);
    }

    /**
     * This method gives the "contextRef" attribute value of <trace> element.
     * 
     * @return contextRef String value
     */
    public String getContextRef() {
        final String contextRef = this.attributesMap.get("contextRef");
        if (null == contextRef) {
            return "";
        }
        return contextRef;
    }

    /**
     * This method gives the "brushRef" attribute value of <trace> element.
     * 
     * @return brushRef String value
     */
    public String getBrushRef() {
        final String brushRef = this.attributesMap.get("brushRef");
        if (null == brushRef) {
            return "";
        }
        return brushRef;
    }

    /**
     * This method gives the "duration" attribute value of <trace> element. The duration of this trace, in milliseconds.
     * 
     * @return duration integer value
     */
    public long getDuration() {
        final String durationStr = this.attributesMap.get("duration");
        if (null == durationStr) {
            return 0;
        }
        final long duration = Long.parseLong(durationStr);
        return duration;
    }

    /**
     * This method gives the "id" attribute value of <trace> element.
     * 
     * @return id String
     */
    @Override
    public String getId() {
        final String id = this.attributesMap.get("id");
        if (null == id) {
            return "";
        }
        return id;
    }

    /**
     * This method gives the "priorRef" attribute value of <trace> element.
     * 
     * @return previous Trace object to which this object is a continuation
     */
    public String getPriorRef() {
        final String prioRef = this.attributesMap.get("priorRef");
        if (null == prioRef) {
            return "";
        }
        return prioRef;
    }

    /**
     * This method gives the "timeOffset" attribute value of <trace> element. The relative timestamp or time-of-day for the start of this trace, in
     * milliseconds.
     * 
     * @return timeOffset decimal value
     */
    public float getTimeOffset() {
        final String tOffsetStr = this.attributesMap.get("timeOffset");
        if (null == tOffsetStr) {
            return 0;
        }
        final float timeOffset = Float.parseFloat(tOffsetStr);
        return timeOffset;
    }

    /**
     * This method gives the Brush object associated with this Trace object
     * 
     * @return associated Brush Object
     */
    public Brush getAssociatedBrush() {
        return this.associatedContext.getBrush();
    }

    /**
     * This method set the Brush object associated with this Trace object
     * 
     * @param brush associated Brush Object
     */
    public void setAssociatedBrush(final Brush brush) {
        this.associatedContext.setBrush(brush);
    }

    /**
     * This method gives the TraceFormat object associated with this Trace Object
     * 
     * @return The associated TraceFormat object
     */
    public TraceFormat getAssociatedTraceFormat() {
        return this.associatedContext.getTraceFormat();
    }

    /**
     * method returns the trace data as string value, with each line having only 75 characters to produce good display of the data.
     * 
     * @return traceData as String
     */
    public String[] getTraceDataAsString() {
        final StringBuffer traceDataBuffer = new StringBuffer();
        final int lineSize = 75;
        final Set<String> channelNames = this.traceData.keySet();
        final int nChannels = channelNames.size();
        final List<Object>[] channelValues = new List[nChannels];
        final Iterator<String> itr = channelNames.iterator();
        int index = 0;
        while (itr.hasNext()) {
            final String channelName = itr.next();
            channelValues[index++] = this.traceData.get(channelName);
        }
        final int sampleSize = channelValues[0].size();
        for (int sampleIndex = 0; sampleIndex < sampleSize; sampleIndex++) {
            for (index = 0; index < nChannels; index++) {
                String value = String.valueOf(channelValues[index].get(sampleIndex));
                if ("null".equals(value)) {
                    value = "?";
                } else if ("false".equalsIgnoreCase(value)) {
                    value = "F";
                } else if ("true".equalsIgnoreCase(value)) {
                    value = "T";
                }
                traceDataBuffer.append(" " + value);
            }
            if (sampleIndex + 1 < sampleSize) {
                traceDataBuffer.append(',');
            }
        }
        final int dataLength = traceDataBuffer.length();
        final int q = dataLength / lineSize;

        final String[] traceDataStr = new String[q + 1];
        for (int i = 0, start = 0; i < q + 1; i++) {
            if (start + lineSize > dataLength - 1) {
                traceDataStr[i] = traceDataBuffer.substring(start);
            } else {
                final int commaIndex = traceDataBuffer.substring(start + lineSize, dataLength - 1).indexOf(',');
                traceDataStr[i] = traceDataBuffer.substring(start, start + lineSize + commaIndex + 1);
                start = start + lineSize + commaIndex + 1;
            }
        }
        return traceDataStr;
    }

    /**
     * This method gives the type of this Ink element object which is the class name of this object.
     * 
     * @return the class name of this object as the Ink element type
     */
    @Override
    public String getInkElementType() {
        return "Trace";
    }

    /**
     * This method gives the parent Trace Group if any of this Trace object
     * 
     * @return TraceGroup object
     */
    public TraceGroup getParentTraceGroup() {
        return this.parentTraceGroup;
    }

    /**
     * This method assigns the parentTraceGroup data member of this object with - the object given in the parameter
     * 
     * @param parentTraceGroup the object of the Parent TraceGroup of this Trace object
     */
    public void setParentTraceGroup(final TraceGroup parentTraceGroup) {
        this.parentTraceGroup = parentTraceGroup;
    }

    /**
     * This method gives the TraceData object that results by the TraceView selection - that has this object as TraceData Reference and a range is provided for
     * selecting data.
     * 
     * @param from the starting index of the selection range
     * @param to the end index of the the selection range
     */
    @Override
    public TraceDataElement getSelectedTraceDataByRange(final String from, final String to) throws InkMLException {
        Trace traceDataElement = null;
        int fromIndex, toIndex;
        if ("".equals(from) && "".equals(to)) {
            traceDataElement = this;
        } else {
            traceDataElement = this.getTraceTemplate();
            /*
             * if(null != this.associatedContext) traceDataElement.setAssociatedContext(associatedContext);
             */
            final String aChannelName = this.getAssociatedTraceFormat().getChannelList().get(0).getName();
            final int nSamples = this.traceData.get(aChannelName).size();
            if ("".equals(to)) {
                fromIndex = Integer.parseInt(from);
                if (!this.isRangeDataValid(fromIndex)) {
                    throw new InkMLException("The given 'from' RangeString, " + from + " is not valid");
                }

                // Select the data in the range defined by the traceView and -
                // add to the selection resul Trace object.
                final LinkedHashMap<String, List<Object>> newTraceData = new LinkedHashMap<String, List<Object>>();
                final Iterator<Map.Entry<String, List<Object>>> iterator = this.traceData.entrySet().iterator();
                while (iterator.hasNext()) {
                    final Map.Entry<String, List<Object>> entry = iterator.next();
                    final String key = entry.getKey();
                    final List<Object> channelValueList = entry.getValue();
                    final List<Object> newChannelValueList = new ArrayList<Object>();
                    for (int i = fromIndex - 1; i < nSamples; i++) {
                        newChannelValueList.add(channelValueList.get(i));
                    }
                    newTraceData.put(key, newChannelValueList);
                }
                traceDataElement.setTraceData(newTraceData);
            } else if ("".equals(from)) {
                toIndex = Integer.parseInt(to);
                if (!this.isRangeDataValid(toIndex)) {
                    throw new InkMLException("The given 'to' RangeString, " + to + " is not valid");
                }

                // Select the data in the range defined by the traceView and -
                // add to the new Trace object which is under construction to become -
                // the target of the traceView
                final LinkedHashMap<String, List<Object>> newTraceData = new LinkedHashMap<String, List<Object>>();
                final Iterator<Map.Entry<String, List<Object>>> iterator = this.traceData.entrySet().iterator();
                while (iterator.hasNext()) {
                    final Map.Entry<String, List<Object>> entry = iterator.next();
                    final String key = entry.getKey();
                    final List<Object> channelValueList = entry.getValue();
                    final List<Object> newChannelValueList = new ArrayList<Object>();
                    for (int i = 0; i < toIndex; i++) {
                        newChannelValueList.add(channelValueList.get(i));
                    }
                    newTraceData.put(key, newChannelValueList);
                }
                traceDataElement.setTraceData(newTraceData);
            } else { // both 'from' and 'to' rangeString are having non empty values.
                // get elements and add them in order to the subtree under selection
                // check range value validity
                fromIndex = Integer.parseInt(from);
                if (!this.isRangeDataValid(fromIndex)) {
                    throw new InkMLException("The given 'from' RangeString, " + from + " is not valid");
                }
                toIndex = Integer.parseInt(to);
                if (!this.isRangeDataValid(toIndex)) {
                    throw new InkMLException("The given 'to' RangeString, " + to + " is not valid");
                }

                // Select the data in the range defined by the traceView and -
                // add to the selection resul Trace object.
                final LinkedHashMap<String, List<Object>> newTraceData = new LinkedHashMap<String, List<Object>>();
                final Iterator<Map.Entry<String, List<Object>>> iterator = this.traceData.entrySet().iterator();
                while (iterator.hasNext()) {
                    final Map.Entry<String, List<Object>> entry = iterator.next();
                    final String key = entry.getKey();
                    final List<Object> channelValueList = entry.getValue();
                    final List<Object> newChannelValueList = new ArrayList<Object>();
                    for (int i = fromIndex - 1; i < toIndex; i++) {
                        newChannelValueList.add(channelValueList.get(i));
                    }
                    newTraceData.put(key, newChannelValueList);
                }
                traceDataElement.setTraceData(newTraceData);
            }
        }
        return traceDataElement;
    }

    /**
     * This method gives the Context object that associated with this Trace Object
     * 
     * @return the associated context object
     */
    public Context getAssociatedContext() {
        return this.associatedContext;
    }

    /**
     * /** This method is used to set the Trace data of the newly constructed Trace data with the Trace data results in selection by the range values.
     * 
     * @param newTraceData
     */
    public void setTraceData(final LinkedHashMap<String, List<Object>> newTraceData) {
        this.traceData = newTraceData;
    }

    /**
     * method to set trace data for given channelName with given array of data
     * 
     * @param channelName
     * @param dataList
     */
    public void setTraceData(final String channelName, final List<Object> dataList) {
        this.traceData.put(channelName, dataList);
    }

    /**
     * method to set trace data for given channelName with given array of data
     * 
     * @param channelName
     * @param data
     */
    public void setTraceData(final String channelName, final long[] data) {
        final List<Object> dataList = new ArrayList<Object>(data.length);
        for (int i = 0; i < data.length; i++) {
            dataList.add(new Long(data[i]));
        }
        this.traceData.put(channelName, dataList);
    }

    /**
     * method to set trace data for given channelName with given array of data
     * 
     * @param channelName
     * @param data
     */
    public void setTraceData(final String channelName, final int[] data) {
        final List<Object> dataList = new ArrayList<Object>(data.length);
        for (int i = 0; i < data.length; i++) {
            dataList.add(new Integer(data[i]));
        }
        this.traceData.put(channelName, dataList);
    }

    /**
     * method to set trace data for given channelName with given array of data
     * 
     * @param channelName
     * @param data
     */
    public void setTraceData(final String channelName, final float[] data) {
        final List<Object> dataList = new ArrayList<Object>(data.length);
        for (int i = 0; i < data.length; i++) {
            dataList.add(new Float(data[i]));
        }
        this.traceData.put(channelName, dataList);
    }

    /**
     * method to set trace data for given channelName with given array of data
     * 
     * @param channelName
     * @param data
     */
    public void setTraceData(final String channelName, final boolean[] data) {
        final List<Object> dataList = new ArrayList<Object>(data.length);
        for (int i = 0; i < data.length; i++) {
            dataList.add(new Boolean(data[i]));
        }
        this.traceData.put(channelName, dataList);
    }

    /**
     * method to trace data from the given map of trace data in the parameter
     * 
     * @param newTraceData
     */
    public void addToTraceData(final LinkedHashMap<String, List<Object>> newTraceData) {
        this.traceData.putAll(newTraceData);
    }

    /**
     * method to add trace data for given channelName with given ArrayList of data
     * 
     * @param channelName
     * @param dataList
     */
    public void addToTraceData(final String channelName, final List<Object> dataList) {
        this.traceData.put(channelName, dataList);
    }

    /**
     * method to add trace data for given channelName with given array of data
     * 
     * @param channelName
     * @param data
     */
    public void addToTraceData(final String channelName, final long[] data) {
        final List<Object> dataList = new ArrayList<Object>(data.length);
        for (int i = 0; i < data.length; i++) {
            dataList.add(new Long(data[i]));
        }
        this.traceData.get(channelName).addAll(dataList);
    }

    /**
     * method to add trace data for given channelName with given array of data
     * 
     * @param channelName
     * @param data
     */
    public void addToTraceData(final String channelName, final int[] data) {
        final List<Object> dataList = new ArrayList<Object>(data.length);
        for (int i = 0; i < data.length; i++) {
            dataList.add(new Integer(data[i]));
        }
        this.traceData.get(channelName).addAll(dataList);
    }

    /**
     * method to add trace data for given channelName with given array of data
     * 
     * @param channelName
     * @param data
     */
    public void addToTraceData(final String channelName, final float[] data) {
        final List<Object> dataList = new ArrayList<Object>(data.length);
        for (int i = 0; i < data.length; i++) {
            dataList.add(new Float(data[i]));
        }
        this.traceData.get(channelName).addAll(dataList);
    }

    /**
     * method to add trace data for given channelName with given array of data
     * 
     * @param channelName
     * @param data
     */
    public void addToTraceData(final String channelName, final boolean[] data) {
        final List<Object> dataList = new ArrayList<Object>(data.length);
        for (int i = 0; i < data.length; i++) {
            dataList.add(new Boolean(data[i]));
        }
        this.traceData.get(channelName).addAll(dataList);
    }

    /**
     * method to remove trace sample-point data for the given channel at the given index
     * 
     * @param channelName
     * @param index
     */
    public void removeTraceDataAt(final String channelName, final int index) {
        this.traceData.get(channelName).remove(index);
    }

    /**
     * method to remove trace sample-point data for the given channel at the first index i.e. at index = 0
     * 
     * @param channelName
     * @param data
     */
    public void removeTraceDataAtFirst(final String channelName, final Object data) {
        final int firstIndex = this.traceData.get(channelName).indexOf(data);
        this.traceData.get(channelName).remove(firstIndex);
    }

    /**
     * method to remove trace sample-point data for the given channel at the last index
     * 
     * @param channelName
     * @param data
     */
    public void removeTraceDataAtLast(final String channelName, final Object data) {
        final int lastIndex = this.traceData.get(channelName).lastIndexOf(data);
        this.traceData.get(channelName).remove(lastIndex);
    }

    /**
     * method to set tracedata for the channel identified by channelName, with the data from the array identified by data prameter. Note: This method should be
     * used for the channels whose 'type' attribute is 'integer'
     * 
     * @param channelName
     * @param data
     */
    public void setChanneldataLong(final String channelName, final long[] data) {
        final List<Object> dataList = new ArrayList<Object>(data.length);
        for (int i = 0; i < data.length; i++) {
            dataList.add(new Long(data[i]));
        }
        this.traceData.put(channelName, dataList);
    }

    /**
     * method to set tracedata for the channel identified by channelName, with the data from the array identified by data prameter. Note: This method should be
     * used for the channels whose 'type' attribute is 'integer'
     * 
     * @param channelName
     * @param data
     */
    public void setChanneldataInt(final String channelName, final int[] data) {
        final List<Object> dataList = new ArrayList<Object>(data.length);
        for (int i = 0; i < data.length; i++) {
            dataList.add(new Integer(data[i]));
        }
        this.traceData.put(channelName, dataList);

    }

    /**
     * method to set tracedata for the channel identified by channelName, with the data from the array identified by data prameter. Note: This method should be
     * used for the channels whose 'type' attribute is 'decimal'
     * 
     * @param channelName
     * @param data
     */
    public void setChanneldataFloat(final String channelName, final float[] data) {
        final List<Object> dataList = new ArrayList<Object>(data.length);
        for (int i = 0; i < data.length; i++) {
            dataList.add(new Float(data[i]));
        }
        this.traceData.put(channelName, dataList);
    }

    /**
     * method to set tracedata for the channel identified by channelName, with the data from the array identified by data prameter. Note: This method should be
     * used for the channels whose 'type' attribute is 'decimal'
     * 
     * @param channelName
     * @param data
     */
    public void setChanneldataDouble(final String channelName, final double[] data) {
        final List<Object> dataList = new ArrayList<Object>(data.length);
        for (int i = 0; i < data.length; i++) {
            dataList.add(new Double(data[i]));
        }
        this.traceData.put(channelName, dataList);
    }

    /**
     * This method is used to validate the value given in from/to range parameters in a TraceView object selecting data from the TraceData object
     * 
     * @param rangeIndex
     */
    private boolean isRangeDataValid(final int rangeIndex) throws InkMLException {
        // Check if the rangeIndex value is within the size of the traceSampleDataList
        final String aChannelName = this.getAssociatedTraceFormat().getChannelList().get(0).getName();
        final int nSamples = this.traceData.get(aChannelName).size();
        if (rangeIndex < 1 || rangeIndex > nSamples) {
            return false;
        }
        return true;
    }

    /**
     * This method assigns the associated context to the Trace Object.
     * 
     * @param associatedContext the Context object to be associated with the Trace object
     */
    @Override
    public void setAssociatedContext(final Context associatedContext) {
        if (null == associatedContext) {
            Trace.logger.fine("Trace::setAssociatedContext, the given parameter context is null");
            return;
        }
        this.associatedContext = new Context(associatedContext);
        this.initTraceDataStructure();
    }

    /**
     * method to get channelType for the given channelName in the parameter
     * 
     * @param channelName
     * @return ChannelType ("integer"|"decimal"|"boolean")
     * @throws InkMLException
     */
    public Channel.ChannelType getChannelType(final String channelName) throws InkMLException {
        return this.getAssociatedTraceFormat().getChannel(channelName).getChannelType();
    }

    /*
     * This utility method looks for the paternString in the given inputString and replace that pattern match with the replacementString
     */
    private String replaceString(final String inputStr, final String patternStr, final String replacementStr) {
        // Compile regular expression
        final Pattern pattern = Pattern.compile(patternStr);

        // Replace all occurrences of pattern in input
        final Matcher matcher = pattern.matcher(inputStr);
        final String output = matcher.replaceAll(replacementStr);
        return output;
    }

    /**
     * Overriding the toString method to give the value of Trace Object
     * 
     * @return simple view of trace data as string
     */
    @Override
    public String toString() {
        String id = this.getId();
        if (id == null) {
            id = "";
        }
        String biodata = "\n\tTrace - objectid = " + this.hashCode() + "\n\tId = " + id + "\n\tbrushRef = " + this.getBrushRef() + "\n\tcontextRef = " + this.getContextRef();
        final Brush brush = this.associatedContext.getBrush();
        if (null != brush) {
            biodata = biodata + "\n\t associatedBrush = " + brush.getId() + " - " + brush;
        }
        if (null != this.associatedContext) {
            biodata = biodata + "\n\t associatedContext = " + this.associatedContext.getId() + " - " + this.associatedContext;
        }
        biodata = biodata + "\n";
        return biodata;
    }

    /**
     * method to write markup data of the trace as string
     * 
     * @return the trace InkML markup data as string
     */
    @Override
    public String toInkML() {
        String strInkML = "<trace";
        final String id = this.getId();
        if (!"".equals(id)) {
            strInkML += " id=\"" + id + "\"";
        }
        final float timeOffset = this.getTimeOffset();
        if (-1 != timeOffset) {
            strInkML += " timeOffset=\"" + timeOffset + "\"";
        }
        strInkML += ">";

        final Object keys[] = this.traceData.keySet().toArray();
        final List<Object> channels[] = new List[keys.length];
        for (int i = 0; i < keys.length; i++) {
            channels[i] = this.traceData.get(keys[i].toString());
        }
        final int dataLen = channels[0].size();
        for (int i = 0; i < dataLen; i++) {
            for (int j = 0; j < keys.length; j++) {
                final List<Object> al = channels[j];
                strInkML += " " + al.get(i);
            }
            if (i < dataLen - 1) {
                strInkML += ",";// No comma after last point
            }
        }

        strInkML += "</trace>";
        return strInkML;
    }

    /**
     * method to apply the transform, upon call, it get the simple multiplication factor from the associated 'canvasTransform' and apply it on each sample
     * point.
     * 
     * @param channelName
     * @param factor
     */
    public void applyTransform(final String channelName, final double factor) {
        final List<Object> channelValues = this.traceData.get(channelName);
        for (int i = 0; i < channelValues.size(); i++) {
            final double oldValue = ((Number) channelValues.get(i)).doubleValue();
            channelValues.set(i, new Double(oldValue * factor));
        }
    }

    /**
     * method to get the trace data as Map ( 'Channel Name' as the key and an 'ArrayList' holding the sample point values.
     * 
     * @return the traceData map
     */
    public LinkedHashMap<String, List<Object>> getTraceData() {
        return this.traceData;
    }

    /**
     * method to set 'timeOffset' attribute
     * 
     * @param timeOffset
     */
    public void setTimeOffset(final long timeOffset) {
        this.attributesMap.put("timeOffset", String.valueOf(timeOffset));
    }

    /**
     * method to set 'id' attribute
     * 
     * @param id
     */
    public void setId(final String id) {
        this.attributesMap.put("id", id);
    }

    /**
     * method to set an attribute
     * 
     * @param attrName
     * @param attrValue
     */
    public void setAttribute(final String attrName, final String attrValue) {
        this.attributesMap.put(attrName, attrValue);
    }

    /**
     * The core logic of processing the trace data
     * 
     * @param traceDataStr
     * @param currentContext
     * @param definitions
     * @throws InkMLException
     */
    void processTraceElement(String traceDataStr, final Context currentContext, final Definitions definitions) throws InkMLException {
        if (null == this.associatedContext) {
            this.resolveAssociatedContext(currentContext, definitions);
        }

        // expand the implicit reference to context by setting
        // proper value to 'contextRef' attribute
        String ctxRef = this.attributesMap.get("contextRef");
        if (null == ctxRef) {
            if (null != this.parentTraceGroup) {
                ctxRef = this.parentTraceGroup.getContextRef();
            } else {
                ctxRef = "#" + currentContext.getId();
            }
            this.attributesMap.put("contextRef", ctxRef);
        }

        // co-occurence contrains evaluation
        String continution = "";
        Continuation continuationAttr = Continuation.NONE;
        try {
            continution = this.attributesMap.get("continution");
            if (null != continution) {
                continuationAttr = Continuation.valueOf(continution);
            }
        } catch (final IllegalArgumentException illArgExp) {
            Trace.logger.severe("The value = " + continution + "is illegeal for the 'continution' attribute of trace element.");
        }

        final String priorRefStr = this.attributesMap.get("priorRef");
        if (null == priorRefStr) {
            if (Continuation.middle == continuationAttr || Continuation.end == continuationAttr) {
                throw new InkMLException("Problem in the definition of trace:" + this.toString() + "The continuation attribute is either 'middle' "
                        + " or 'end', but no value is given for 'priorRef' attribute.");
            }
        } else {
            if (Continuation.begin == continuationAttr) {
                Trace.logger.severe("Problem in the definition of tarce:" + this.toString() + "The continuation attribute is either 'begin' "
                        + ", but a value is given for 'priorRef' attribute. The priorRef value is ignored.");
            }
        }

        final HashMap<String, Object> variableValueMap = new HashMap<String, Object>();

        if ("".equals(traceDataStr)) {
            return;
        }
        Trace.logger.fine("Trace data read from XML Doc: " + traceDataStr);
        traceDataStr = traceDataStr.trim();
        traceDataStr = traceDataStr.replace('\n', ' ');
        traceDataStr = this.replaceString(traceDataStr, "\'-", " \'-");
        traceDataStr = this.replaceString(traceDataStr, "\"-", " \"-");
        // replce 3-5 with 3<space>-5
        traceDataStr = this.replaceString(traceDataStr, "-", " -");
        traceDataStr = this.replaceString(traceDataStr, "\' -", "\'-");
        traceDataStr = this.replaceString(traceDataStr, "\" -", "\"-");
        // replce '3'5 with <space>'3<space>'5
        traceDataStr = this.replaceString(traceDataStr, "\'", " \'");
        traceDataStr = this.replaceString(traceDataStr, "\"", " \"");
        traceDataStr = this.replaceString(traceDataStr, "\\*", " \\* ");
        traceDataStr = this.replaceString(traceDataStr, "!", " !");
        Trace.logger.fine("Trace data after inserting required spaces for clarity: " + traceDataStr);

        final StringTokenizer st = new StringTokenizer(traceDataStr, ",");
        final int nTraceSamples = st.countTokens();
        if (0 == nTraceSamples) {
            return;
        }

        final TraceFormat tf = this.associatedContext.getTraceFormat();
        final ArrayList<Channel> channelList = tf.getChannelList();
        final int nChannel = channelList.size();
        final String[] channelNames = new String[nChannel];
        final Channel.ChannelType[] channelTypes = new Channel.ChannelType[nChannel];
        this.lastPrefixMap = new HashMap<String, TracePrefix>();
        for (int i = 0; i < nChannel; i++) {
            final Channel chn = channelList.get(i);
            final String channelName = chn.getName();
            channelNames[i] = channelName;
            channelTypes[i] = chn.getChannelType();
            if (null != this.priorReferredTrace) {
                this.lastPrefixMap.put(channelName, this.priorReferredTrace.getLastPrefixOf(channelName));
            } else {
                this.lastPrefixMap.put(channelName, TracePrefix.NONE);
            }

            // set the intial 'Vchn' (eg. vx and vy) values to 0 by checking the Channel list
            // 'Vchn' value is the value used in computing the effective diference value -
            // to be added to the channel value when single/double difference prefix are used
            if (chn.getChannelType() == Channel.ChannelType.DECIMAL) {
                variableValueMap.put(channelName, new Float(0));
            }
            if (chn.getChannelType() == Channel.ChannelType.INTEGER) {
                variableValueMap.put(channelName, new Integer(0));
            }
        }

        int index = 0;
        while (st.hasMoreTokens()) {
            final String traceSampleData = st.nextToken().trim();

            // extract and process the traceSample data
            final StringTokenizer token = new StringTokenizer(traceSampleData, " ");
            // int valueIndex = 0;
            // while (token.hasMoreTokens()) {
            for (int valueIndex = 0; valueIndex < nChannel; valueIndex++) {
                String channelValue = null;
                if (!token.hasMoreTokens()) {
                    // value for channel is not given
                    final Channel chn = channelList.get(valueIndex);
                    if (chn.isIntermittent()) {
                        // unreported intermittent channel values should be -
                        // treated as if the prefix '*' (get previous value) given.
                        if (0 == index) {
                            channelValue = chn.getDefaultValue();
                        } else {
                            channelValue = "*";
                        }
                    } else {
                        throw new InkMLException("Error: Value must be given to all regular Channels.\n" + "Location: Value is not given for Channel " + chn.getName() + " in sample point index "
                                + index + " [range starts from 0].");
                    }
                } else {
                    channelValue = token.nextToken().trim();
                }
                // check ' and " prefix constraints

                final Object previousDoubleDiffVal = 0;
                this.processAndStoreChannelValue(channelNames[valueIndex % nChannel], channelValue, channelTypes[valueIndex % nChannel], index, variableValueMap, previousDoubleDiffVal);
                // valueIndex++;
            }
            index++;
        }

        // store this element in definitions as it may be reffered later -
        // by its ID attribute
        if (!"".equals(this.getId())) {
            definitions.addToIndirectChildrenMap(this);
        }
    }

    /**
     * Method that validates the range of sample data for the defined range through 'min' and 'max' attributes of channels
     * 
     * @param channelValue
     * @param channelName
     * @throws InkMLException
     */
    private void validateRange(final String channelValue, final String channelName) throws InkMLException {
        final Channel channel = this.associatedContext.getTraceFormat().getChannel(channelName);
        if (channel.getChannelType() == Channel.ChannelType.BOOLEAN) {
            return;
        }
        // min, max range check is applicale only to integer or deciaml channels
        final String min = channel.getMin();
        final String max = channel.getMax();
        try {
            double iValue, iMin, iMax;
            iValue = Double.parseDouble(channelValue);
            if (!"".equals(min)) {
                iMin = Double.parseDouble(min);
                if (!"".equals(max)) {
                    iMax = Double.parseDouble(max);
                    if (iValue < iMin || iValue > iMax) {
                        throw new InkMLException(channelName + " Channel data value = " + channelValue + " is out of the format range (min=" + min + ", max=" + max + ").");
                    }
                } else {
                    if (iValue < iMin) {
                        throw new InkMLException(channelName + " Channel data value = " + channelValue + " is under the format min range (min=" + min + ").");
                    }
                }

            } else if (!"".equals(max)) {
                iMax = Double.parseDouble(max);
                if (iValue > iMax) {
                    throw new InkMLException(channelName + " Channel data value = " + channelValue + " is over the format max range (max=" + max + ").");
                }
            }
        } catch (final NumberFormatException e) {
            Trace.logger.severe("Invalid value to min/max attribute of channel " + channelName);
        }
    }

    /**
     * method to get map of all defined attributes (attributeName as 'key' and attributeValue as 'value')
     * 
     * @return attributes map
     */
    public HashMap<String, String> getAttributesMap() {
        return this.attributesMap;
    }

    /**
     * Method to write inkml markup in to file or other stream
     */
    @Override
    public void writeXML(final InkMLWriter writer) {
        writer.writeStartTag("trace", this.attributesMap);
        writer.incrementTagLevel();
        final String[] traceDataStr = this.getTraceDataAsString();
        for (int i = 0; i < traceDataStr.length; i++) {
            writer.writeCharacters(traceDataStr[i]);
        }
        writer.decrementTagLevel();
        writer.writeEndTag("trace");
    }

    /**
     * Method to log trace data in console window
     */
    public void printTrace() {
        Trace.logger.fine("<trace>");
        final String[] arr = this.getTraceDataAsString();
        for (int i = 0; i < arr.length; i++) {
            Trace.logger.fine(" " + arr[i]);
        }
        Trace.logger.fine("</trace>");
    }
}