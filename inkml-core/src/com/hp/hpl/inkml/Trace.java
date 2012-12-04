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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;

/**
 * This class models the {@code <trace>} InkML element.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 * Creation date : 6-May-2007
 */

public class Trace implements TraceDataElement {
	private HashMap<String, String> attributesMap;
	private LinkedHashMap<String, ArrayList> traceData;

	/**
	 * This is used to qulaify if the trace is a penDown which means created using the pen in contact of the writing surface.
	 * pen up trace - trace created while the pen is up for example capturing the hovering over the wrting surface and 
	 * the last possible value is indeterminate which means the information not available on if it is a pen down trace or pen up trace.
	 * The default value is penDown. 
	 *
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
	 * To indicate the Continuation type of the trace, if it is a continution fragment trace of a huge trace. 
	 * Possible values - begin mens that it is the first trace, end means that it is the last trace, 
	 * middle means that it an intermediate trace and none means that the trace is stand alone and 
	 * not part of a Continuation trace.
	 * Default value - Continuation.NONE
	 *
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
	private Continuation continuation = Continuation.NONE;

	/**
	 * Trace prefix are used to encode trace data using the single or double difference notations 
	 * in order to reduce the size of mark data only by stroing the differences of subsequent sample ponits data. 
	 *
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
	private Trace priorReferredTrace = null;

	//private String contextRef="";
	private Context associatedContext;
	//private String brushRef="";
	private TraceGroup parentTraceGroup;


	// Create logger instance for logging
	private static Logger logger =
		 Logger.getLogger(Trace.class.getName());


	/**
	 * No argument constructor. It is being used to construct the 'trace' object that comes
	 *    as the result of a 'traceView' selection operation.
	 */
	public Trace() {
		this.attributesMap = new HashMap<String, String>();
		//this.associatedContext = Context.getDefaultContext();
	}

	// initialize data structures required for processing the trace data text value
	private void initTraceDataStructure(){
		if(null != this.associatedContext){
			TraceFormat traceFormat = this.associatedContext.getTraceFormat();
			ArrayList<Channel> channelList = traceFormat.getChannelList();
			int nChannel = channelList.size();
			this.traceData = new LinkedHashMap<String, ArrayList>();
			for(int i=0; i< nChannel; i++) {
				Channel chn = channelList.get(i);
				String channelName = chn.getName();
				this.traceData.put(channelName, new ArrayList());
			}
		}
	}
	/**
	 * To get the template (blue-print) of the trace with current context state,
	 * for loading the trace data arrived in sub selection of trace in traceView select command.
	 * @return new trace object with all context properties of the current trace but with empty data.
	 */
	public Trace getTraceTemplate() {
		TraceFormat traceFormat = getAssociatedContext().getTraceFormat();
		ArrayList<Channel> channelList = traceFormat.getChannelList();
		int nChannel = channelList.size();
		LinkedHashMap<String, ArrayList> newTraceData = new LinkedHashMap<String, ArrayList>();
		for(int i=0; i< nChannel; i++) {
			Channel chn = channelList.get(i);
			String channelName = chn.getName();
			newTraceData.put(channelName, new ArrayList());
		}
		Trace traceObject = new Trace();
		//To Do: add contextRef and brushRef
		traceObject.setAssociatedContext(this.associatedContext);
		traceObject.setTraceData(newTraceData);
		return traceObject;
	}

	/**
	 * Constructor to create Trace InkML object from the trace DOM element
	 * @param element Trace DOM element
	 * @param context The current context object that associated with the containing Ink document
	 * @param definitions The global definition state associated with the containing Ink document
	 * @throws InkMLException
	 * @throws InkMLException
	 */
	void resolveAssociatedContext(Context currentContext, Definitions defs) throws InkMLException {
		String contextRefAttr = getContextRef();
		if(!"".equals(contextRefAttr)){
			associatedContext = defs.getContextRefElement(contextRefAttr);
		}
		else {
			if(null != this.parentTraceGroup) {
				associatedContext = this.parentTraceGroup.getAssociatedContext();
			}
			associatedContext = (null != associatedContext)? associatedContext:
				(null != currentContext)? currentContext:Context.getDefaultContext();
			setContextRef("#"+associatedContext.getId());
		}

		String brushRefAttr = getBrushRef();
		if(!"".equals(brushRefAttr)){
			associatedContext.setBrush(defs.getBrushRefElement(brushRefAttr));
			//associatedContext.getBrush().override(
			//		defs.getBrushRefElement(brushRefAttr));
		}
		initTraceDataStructure();
	}

	private void setContextRef(String contextRef) {
		if (! "".equals(contextRef))
			this.attributesMap.put("contextRef", contextRef);;
	}

	// used in processing the single, double difference trace prefix notations
	private TracePrefix getLastPrefixOf(String channelName) {
		return lastPrefixMap.get(channelName);
	}


	/*
	 * This method process the Trace Sample data to compute the absolute value
	 *  after processing any prefix if uesd.
	 */
	private void processAndStoreChannelValue(String channelName,
											String channelValue,
											Channel.ChannelType channelType,
											int traceIndex,
											HashMap<String, Object> variableValueMap,
											Object previousDoubleDiffVal)
											throws InkMLException {
		int mCharPos;
		Object chnVal;
		if((mCharPos = channelValue.indexOf('!')) >=0){
			if(channelType == Channel.ChannelType.BOOLEAN) {
				//logger.error("The prefix <!> is NOT applicable to BOOLEAN channel.");
				throw new InkMLException("The prefix <!> is NOT applicable to BOOLEAN channel.");
			}
			chnVal = getChannelValueObject(channelValue.substring(mCharPos + 1), channelType);
			//traceData.get(channelName).add(traceIndex, chnVal);
		}
		else if((mCharPos = channelValue.indexOf('\'')) >=0){
			if(channelType == Channel.ChannelType.BOOLEAN) {
				//logger.error("The prefix <'> is applicable to numerical channels only. The prefix is ignored.");
				throw new InkMLException("The prefix <'> is NOT applicable to BOOLEAN channel.");
				//Object chnVal = getChannelValueObject(channelValue.substring(mCharPos + 1), channelType);
				//traceData.get(channelName).add(traceIndex, chnVal);
				//return;
			}

			// To implement the condition that the prefix <'> must be preceded -
			// by an explicit encoding. If the traceSample value index is 0 -
			// means that it is not preceded by an explicit encoding.
			if(lastPrefixMap.get(channelName) != TracePrefix.NONE || this.continuation == Continuation.begin
					|| 0 == traceIndex) {
				throw new InkMLException(
						"The condition that the the prefix <'> must be preceded "+
						"with an explicit value is violated.\nProblem is in the channelValue "+
						channelValue+" (channel name = "+channelName+").");
			}

			// processing Single difference denoted by the prefix '
			lastPrefixMap.put(channelName, TracePrefix.velocity);
			Object previousValue = traceData.get(channelName).get(traceIndex - 1);
			Object singleDiffVal = getChannelValueObject(channelValue.substring(mCharPos + 1), channelType);
			variableValueMap.put(channelName, singleDiffVal);
			chnVal = null;
			if(channelType == Channel.ChannelType.DECIMAL)
				chnVal =  (Float) previousValue + (Float) singleDiffVal;
			else if(channelType == Channel.ChannelType.INTEGER)
				chnVal =  (Integer) previousValue + (Integer) singleDiffVal;
			//traceData.get(channelName).add(traceIndex, chnVal);
		}
		else if((mCharPos = channelValue.indexOf('\"')) >=0){
			if(channelType == Channel.ChannelType.BOOLEAN) {
				//logger.error("The prefix <\"> is applicable to numerical channels only. The prefix is ignored.");
				throw new InkMLException("The prefix <\"> is NOT applicable to BOOLEAN channel.");
				//Object chnVal = getChannelValueObject(channelValue.substring(mCharPos + 1), channelType);
				//traceData.get(channelName).add(traceIndex, chnVal);
				//return;
			}

			// processing Double difference denoted by the prefix "

			// To implement the condition that the prefix <"> must be preceded -
			// with an 'single difference' or 'velocity' (the prefix <'>).
			// If the last prefix is velocity means that it is not preceded by -
			// a single difference.
			if( lastPrefixMap.get(channelName) != TracePrefix.velocity || this.continuation == Continuation.begin) {
				throw new InkMLException(
						"The condition that the the prefix <\"> must be preceded "+
						"with a single difference is violated.\nProblem is in the channelValue "+
						channelValue+" (channel name = "+channelName+").");
			}
			lastPrefixMap.put(channelName, TracePrefix.acceleration);
			Object doubleDiffVal = getChannelValueObject(channelValue.substring(mCharPos + 1), channelType);
			Object currentVariableValue = variableValueMap.get(channelName);
			Object newVariableValue = null;
			if(channelType == Channel.ChannelType.DECIMAL)
				newVariableValue =  (Float) doubleDiffVal + (Float) currentVariableValue;
			else if(channelType == Channel.ChannelType.INTEGER)
				newVariableValue =  (Integer) doubleDiffVal + (Integer) currentVariableValue;
			variableValueMap.put(channelName, newVariableValue);
			Object previousValue = traceData.get(channelName).get(traceIndex - 1);
			chnVal = null;
			if(channelType == Channel.ChannelType.DECIMAL)
				chnVal =  (Float) previousValue + (Float) newVariableValue;
			else if(channelType == Channel.ChannelType.INTEGER)
				chnVal =  (Integer) previousValue + (Integer) newVariableValue;
			//traceData.get(channelName).add(traceIndex, chnVal);
			previousDoubleDiffVal = doubleDiffVal;
		} else if((mCharPos = channelValue.indexOf('?')) >=0){
			traceData.get(channelName).add(null);
			return;
		} else if((mCharPos = channelValue.indexOf('*')) >=0){
			Channel channel = getAssociatedContext().getTraceFormat().getChannel(channelName);
			if(channel.isIntermittent()){
				Object previousValue = traceData.get(channelName).get(traceIndex - 1);
				int i = 2; // to start with last but one index data w.r.t current traceIndex,
				           // i.e. to start with traceIndex - i where i=2,3, .... until -
						   // we hit data at traceIndex=0 which must be != null.
				while(null == previousValue){
					previousValue = traceData.get(channelName).get(traceIndex - i);
					i++;
				}
				if(null == previousValue){
					previousValue = channel.getDefaultValue();
					if(channelType == Channel.ChannelType.DECIMAL)
						previousValue =  new Float((String)previousValue);
					else if(channelType == Channel.ChannelType.INTEGER)
						previousValue =  new Integer((String)previousValue);
					else if(channelType == Channel.ChannelType.BOOLEAN) {
						if("F".equalsIgnoreCase((String)previousValue))
							previousValue =  new Boolean("false");
						else if("T".equalsIgnoreCase((String)previousValue))
							previousValue =  new Boolean("true");
					}
				}
				chnVal = previousValue;
				validateRange(chnVal.toString(), channelName);
				traceData.get(channelName).add(traceIndex, chnVal);
				return;
			}
			if(channelType == Channel.ChannelType.BOOLEAN) {
				Object previousValue = traceData.get(channelName).get(traceIndex - 1);
				chnVal = previousValue;
				//traceData.get(channelName).add(traceIndex, previousValue);
				//return;
			}
			// processing the prefix * which retains the previous value or -
			//   previous difference prefix notion
			if(lastPrefixMap.get(channelName) == TracePrefix.velocity) {
				Object singleDiffVal = variableValueMap.get(channelName);
				Object previousValue = traceData.get(channelName).get(traceIndex - 1);
				chnVal = null;
				if(channelType == Channel.ChannelType.DECIMAL)
					chnVal =  (Float) previousValue + (Float) singleDiffVal;
				else if(channelType == Channel.ChannelType.INTEGER)
					chnVal =  (Integer) previousValue + (Integer) singleDiffVal;
				//traceData.get(channelName).add(traceIndex, chnVal);
			} else if(lastPrefixMap.get(channelName) == TracePrefix.acceleration) {
				Object doubleDiffVal = previousDoubleDiffVal;
				Object currentVariableValue = variableValueMap.get(channelName);
				Object newVariableValue = null;
				if(channelType == Channel.ChannelType.DECIMAL)
					newVariableValue =  (Float) doubleDiffVal + (Float) currentVariableValue;
				else if(channelType == Channel.ChannelType.INTEGER)
					newVariableValue =  (Integer) doubleDiffVal + (Integer) currentVariableValue;
				variableValueMap.put(channelName, newVariableValue);
				Object previousValue = traceData.get(channelName).get(traceIndex - 1);
				chnVal = null;
				if(channelType == Channel.ChannelType.DECIMAL)
					chnVal =  (Float) previousValue + (Float) newVariableValue;
				else if(channelType == Channel.ChannelType.INTEGER)
					chnVal =  (Integer) previousValue + (Integer) newVariableValue;
				//traceData.get(channelName).add(traceIndex, chnVal);
			} else {
				Object previousValue = traceData.get(channelName).get(traceIndex - 1);
				chnVal = previousValue;
				//traceData.get(channelName).add(traceIndex, previousValue);
			}
		} else {
			// processing data without any prefix

			// for boolean channel
			if(channelType == Channel.ChannelType.BOOLEAN) {
				chnVal=null;
				if("F".equalsIgnoreCase(channelValue))
					chnVal =  new Boolean("false");
				else if("T".equalsIgnoreCase(channelValue))
					chnVal =  new Boolean("true");
				traceData.get(channelName).add(traceIndex, chnVal);
				return;
			}

			// for numerical channel
			// check if the current prefix is single difference a.k.a velocity
			if(lastPrefixMap.get(channelName) == TracePrefix.velocity) {
				Object previousValue = traceData.get(channelName).get(traceIndex - 1);
				Object singleDiffVal = null;
				if(channelType == Channel.ChannelType.DECIMAL)
					singleDiffVal =  new Float(channelValue);
				else if(channelType == Channel.ChannelType.INTEGER)
					singleDiffVal =  new Integer(channelValue);
				variableValueMap.put(channelName, singleDiffVal);
				chnVal = null;
				if(channelType == Channel.ChannelType.DECIMAL)
					chnVal =  (Float) previousValue + (Float) singleDiffVal;
				else if(channelType == Channel.ChannelType.INTEGER)
					chnVal =  (Integer) previousValue + (Integer) singleDiffVal;
				//traceData.get(channelName).add(traceIndex, chnVal);
			} else if(lastPrefixMap.get(channelName) == TracePrefix.acceleration) {
				// check if the current prefix is double difference a.k.a acceleration
				Object doubleDiffVal = null;
				if(channelType == Channel.ChannelType.DECIMAL)
					doubleDiffVal =  new Float(channelValue);
				else if(channelType == Channel.ChannelType.INTEGER)
					doubleDiffVal =  new Integer(channelValue);
				Object currentVariableValue = variableValueMap.get(channelName);
				Object newVariableValue = null;
				if(channelType == Channel.ChannelType.DECIMAL)
					newVariableValue =  (Float) doubleDiffVal + (Float) currentVariableValue;
				else if(channelType == Channel.ChannelType.INTEGER)
					newVariableValue =  (Integer) doubleDiffVal + (Integer) currentVariableValue;
				variableValueMap.put(channelName, newVariableValue);
				Object previousValue = traceData.get(channelName).get(traceIndex - 1);
				chnVal = null;
				if(channelType == Channel.ChannelType.DECIMAL)
					chnVal =  (Float) previousValue + (Float) newVariableValue;
				else if(channelType == Channel.ChannelType.INTEGER)
					chnVal =  (Integer) previousValue + (Integer) newVariableValue;
				//traceData.get(channelName).add(traceIndex, chnVal);
				previousDoubleDiffVal = doubleDiffVal;
			}
			else {
				// It is an absolute value, meaning there is no 'prefix' used earlier
				chnVal = null;
				if(channelType == Channel.ChannelType.DECIMAL)
					chnVal =  new Float(channelValue);
				else if(channelType == Channel.ChannelType.INTEGER)
					chnVal =  new Integer(channelValue);
				//traceData.get(channelName).add(traceIndex, chnVal);
			}
		}
		validateRange(chnVal.toString(), channelName);
		traceData.get(channelName).add(chnVal);
	}

	// To great channel value object according to the type of the Channel.
	private Object getChannelValueObject(String channelValue, Channel.ChannelType channelType) {
		Object chnVal = null;
		if(channelType == Channel.ChannelType.DECIMAL)
			chnVal = new Float(channelValue);
		else if(channelType == Channel.ChannelType.INTEGER)
			chnVal = new Integer(channelValue);
		else if(channelType == Channel.ChannelType.BOOLEAN)
			chnVal = new Boolean(channelValue);
		return chnVal;
	}

	/**
	 * This method gives a list containg value belongs to all trace sample point -
	 *  of Channel with name given in the parameter channelName
	 * @param channalName The name of the Channel
	 * @return List of Channel values
	 * @throws InkMLException
	 */
	public ArrayList getChannelValueList(String channalName) throws InkMLException {
		if (! traceData.containsKey(channalName)) {
			throw new InkMLException("Invalid Channel Name (" + channalName + ").");
		}
		return traceData.get(channalName);
	}

	/**
	 * This method gives the "type" attribute value of <trace> element.
	 * @return TraceType value
	 */
	public TraceType getType() {
		String typeStr = this.attributesMap.get("type");
		if(null == typeStr)
			return TraceType.penDown; // default value
		return TraceType.valueOf(typeStr);
	}
	public void setType(TraceType type) {
		String typeStr = String.valueOf(type);
		this.attributesMap.put("type", typeStr);
	}

	/**
	 * This method gives the "continuation" attribute value of <trace> element.
	 * @return Continuation value
	 */
	public Continuation getContinuation() {
		String continuStr = this.attributesMap.get("continuation");
		if(null == continuStr)
			return Continuation.NONE;
		return Continuation.valueOf(continuStr);

	}

	public void setContinuation(Continuation continuation) {
		String continuStr = String.valueOf(continuation);
		this.attributesMap.put("continuation", continuStr);
	}

	/**
	 * This method gives the "contextRef" attribute value of <trace> element.
	 * @return contextRef String value
	 */
	public String getContextRef() {
		String contextRef = this.attributesMap.get("contextRef");
		if(null == contextRef)
			return "";
		return contextRef;
	}

	/**
	 * This method gives the "brushRef" attribute value of <trace> element.
	 * @return brushRef String value
	 */
	public String getBrushRef() {
		String brushRef = this.attributesMap.get("brushRef");
		if(null == brushRef)
			return "";
		return brushRef;
	}

	/**
	 * This method gives the "duration" attribute value of <trace> element.
	 * The duration of this trace, in milliseconds.
	 * @return duration integer value
	 */
	public long getDuration() {
		String durationStr = this.attributesMap.get("duration");
		if(null == durationStr)
			return 0;
		long duration = Long.parseLong(durationStr);
		return duration;
	}

	/**
	 * This method gives the "id" attribute value of <trace> element.
	 * @return id String
	 */
	public String getId() {
		String id = this.attributesMap.get("id");
		if(null == id)
			return "";
		return id;
	}

	/**
	 * This method gives the "priorRef" attribute value of <trace> element.
	 * @return previous Trace object to which this object is a continuation
	 */
	public String getPriorRef() {
		String prioRef = this.attributesMap.get("priorRef");
		if(null == prioRef)
			return "";
		return prioRef;
	}

	/**
	 * This method gives the "timeOffset" attribute value of <trace> element.
	 * The relative timestamp or time-of-day for the start of this trace, in
	 * milliseconds.
	 * @return timeOffset decimal value
	 */
	public float getTimeOffset() {
		String tOffsetStr = this.attributesMap.get("timeOffset");
		if(null==tOffsetStr)
			return 0;
		float timeOffset = Float.parseFloat(tOffsetStr);
		return timeOffset;
	}

	/**
	 * This method gives the Brush object associated with this Trace object
	 * @return associated Brush Object
	 */
	public Brush getAssociatedBrush() {
		return this.associatedContext.getBrush();
	}

	/**
	 * This method set the Brush object associated with this Trace object
	 * @param brush associated Brush Object
	 */
	public void setAssociatedBrush(Brush brush) {
		this.associatedContext.setBrush(brush);
	}


	/**
	 * This method gives the TraceFormat object associated with this Trace Object
	 * @return The associated TraceFormat object
	 */
	public TraceFormat getAssociatedTraceFormat() {
		return this.associatedContext.getTraceFormat();
	}

	/**
	 * method returns the trace data as string value,
	 * with each line having only 75 characters to produce good display of the data.
	 * @return traceData as String
	 */
	public String[] getTraceDataAsString() {
		StringBuffer traceDataBuffer = new StringBuffer();
		final int lineSize=75;
		Set channelNames = this.traceData.keySet();
		int nChannels = channelNames.size();
		ArrayList[] channelValues = new ArrayList[nChannels];
		Iterator itr =channelNames.iterator();
		int index = 0;
		while(itr.hasNext()) {
			String channelName = (String) itr.next();
			channelValues[index++] = this.traceData.get(channelName);
		}
		int sampleSize = channelValues[0].size();
		for(int sampleIndex = 0; sampleIndex<sampleSize; sampleIndex++){
			for(index = 0; index<nChannels; index++){
				String value = String.valueOf(channelValues[index].get(sampleIndex));
				if("null".equals(value))
					value="?";
				else if("false".equalsIgnoreCase(value))
					value="F";
				else if("true".equalsIgnoreCase(value))
					value="T";
				traceDataBuffer.append(" "+value);
			}
			if((sampleIndex+1) < sampleSize)
				traceDataBuffer.append(',');
		}
		int dataLength = traceDataBuffer.length();
		int q = dataLength/lineSize;

		String[] traceDataStr = new String[q+1];
		for(int i=0, start=0;i< q+1; i++){
			if(start+lineSize > dataLength-1) // last Line
				traceDataStr[i] = traceDataBuffer.substring(start);
			else {
				int commaIndex = traceDataBuffer.substring(start+lineSize,dataLength-1).indexOf(',');
				traceDataStr[i] = traceDataBuffer.substring(start, start+lineSize+commaIndex+1);
				start=start+lineSize+commaIndex+1;
			}
		}
		return traceDataStr;
	}

	/**
	 * This method gives the type of this Ink element object which is the class name of this object.
	 * @return the class name of this object as the Ink element type
	 */
	public String getInkElementType() {
		return "Trace";
	}

	/**
	 * This method gives the parent Trace Group if any of this Trace object
	 * @return TraceGroup object
	 */
	public TraceGroup getParentTraceGroup() {
		return parentTraceGroup;
	}

	/**
	 * This method assigns the parentTraceGroup data member of this object with -
	 *  the object given in the parameter
	 * @param parentTraceGroup the object of the Parent TraceGroup of this Trace object
	 */
	public void setParentTraceGroup(TraceGroup parentTraceGroup) {
		this.parentTraceGroup = parentTraceGroup;
	}

	/**
	 * This method gives the TraceData object that results by the TraceView selection -
	 *  that has this object as TraceData Reference and a range is provided for selecting data.
	 * @param from the starting index of the selection range
	 * @param to the end index of the the selection range
	 */
	public TraceDataElement getSelectedTraceDataByRange(String from, String to) throws InkMLException {
		Trace traceDataElement = null;
		   int fromIndex, toIndex;
		   if("".equals(from) && "".equals(to)) {
			   traceDataElement = this;
		   } else {
			   traceDataElement = getTraceTemplate();
			   /*if(null != this.associatedContext)
				   traceDataElement.setAssociatedContext(associatedContext);
			   */
			   String aChannelName = getAssociatedTraceFormat().getChannelList().get(0).getName();
			   int nSamples = traceData.get(aChannelName).size();
			   if("".equals(to)) {
				   fromIndex = Integer.parseInt(from);
				   if(! isRangeDataValid(fromIndex) ) {
					   throw new InkMLException("The given 'from' RangeString, "+from
							                                         +" is not valid");
				   }

				   // Select the data in the range defined by the traceView and -
				   //   add to the selection resul Trace object.
				   LinkedHashMap<String, ArrayList> newTraceData = new LinkedHashMap<String, ArrayList>();
				   Iterator iterator = traceData.entrySet().iterator();
				   while (iterator.hasNext()) {
					   Map.Entry entry = (Map.Entry)iterator.next();
					   String key = (String) entry.getKey();
					   ArrayList channelValueList = (ArrayList) entry.getValue();
					   ArrayList newChannelValueList = new ArrayList();
					   for(int i=(fromIndex-1); i< nSamples; i++) {
						   newChannelValueList.add(channelValueList.get(i));
					   }
					   newTraceData.put(key, newChannelValueList);
				   }
				   traceDataElement.setTraceData(newTraceData);
			   } else if("".equals(from)) {
				   toIndex = Integer.parseInt(to);
				   if(! isRangeDataValid(toIndex) ) {
					   throw new InkMLException("The given 'to' RangeString, "+to+" is not valid");
				   }

				   // Select the data in the range defined by the traceView and -
				   //  add to the new Trace object which is under construction to become -
				   //  the target of the traceView
				   LinkedHashMap<String, ArrayList> newTraceData = new LinkedHashMap<String, ArrayList>();
				   Iterator iterator = traceData.entrySet().iterator();
				   while (iterator.hasNext()) {
					   Map.Entry entry = (Map.Entry)iterator.next();
					   String key = (String) entry.getKey();
					   ArrayList channelValueList = (ArrayList) entry.getValue();
					   ArrayList newChannelValueList = new ArrayList();
					   for(int i=0; i< toIndex; i++) {
						   newChannelValueList.add(channelValueList.get(i));
					   }
					   newTraceData.put(key, newChannelValueList);
				   }
				   traceDataElement.setTraceData(newTraceData);
			   } else { // both 'from' and 'to' rangeString are having non empty values.
				   // get elements and add them in order to the subtree under selection
				   // check range value validity
				   fromIndex = Integer.parseInt(from);
				   if(! isRangeDataValid(fromIndex) ) {
					   throw new InkMLException("The given 'from' RangeString, "+from
							                                         +" is not valid");
				   }
				   toIndex = Integer.parseInt(to);
				   if(! isRangeDataValid(toIndex) ) {
					   throw new InkMLException("The given 'to' RangeString, "+to+" is not valid");
				   }

				   // Select the data in the range defined by the traceView and -
				   //   add to the selection resul Trace object.
				   LinkedHashMap<String, ArrayList> newTraceData = new LinkedHashMap<String, ArrayList>();
				   Iterator iterator = traceData.entrySet().iterator();
				   while (iterator.hasNext()) {
					   Map.Entry entry = (Map.Entry)iterator.next();
					   String key = (String) entry.getKey();
					   ArrayList channelValueList = (ArrayList) entry.getValue();
					   ArrayList newChannelValueList = new ArrayList();
					   for(int i=(fromIndex-1); i< toIndex; i++) {
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
	 * @return the associated context object
	 */
	public Context getAssociatedContext() {
		return this.associatedContext;
	}

	/**
	 * /**
	 * This method is used to set the Trace data of the newly constructed
	 * Trace data with the Trace data results in selection by the range values.
	 * @param newTraceData
	 */
	public void setTraceData(LinkedHashMap<String, ArrayList> newTraceData) {
		this.traceData = newTraceData;
	}
	/**
	 * method to set trace data for given channelName with given array of data
	 * @param channelName
	 * @param dataList
	 */
	public void setTraceData(String channelName, ArrayList dataList) {
		this.traceData.put(channelName, dataList);
	}
	/**
	 * method to set trace data for given channelName with given array of data
	 * @param channelName
	 * @param data
	 */
	public void setTraceData(String channelName, long[] data) {
		ArrayList<Long> dataList = new ArrayList<Long>(data.length);
		for(int i=0; i<data.length; i++)
			dataList.add(new Long(data[i]));
		this.traceData.put(channelName, dataList);
	}
	/**
	 * method to set trace data for given channelName with given array of data
	 * @param channelName
	 * @param data
	 */
	public void setTraceData(String channelName, int[] data) {
		ArrayList<Integer> dataList = new ArrayList<Integer>(data.length);
		for(int i=0; i<data.length; i++)
			dataList.add(new Integer(data[i]));
		this.traceData.put(channelName, dataList);
    }
	/**
	 * method to set trace data for given channelName with given array of data
	 * @param channelName
	 * @param data
	 */
    public void setTraceData(String channelName, float[] data) {
		ArrayList<Float> dataList = new ArrayList<Float>(data.length);
		for(int i=0; i<data.length; i++)
			dataList.add(new Float(data[i]));
		this.traceData.put(channelName, dataList);
    }
    /**
     * method to set trace data for given channelName with given array of data
     * @param channelName
     * @param data
     */
    public void setTraceData(String channelName, boolean[] data) {
		ArrayList<Boolean> dataList = new ArrayList<Boolean>(data.length);
		for(int i=0; i<data.length; i++)
			dataList.add(new Boolean(data[i]));
		this.traceData.put(channelName, dataList);
    }

    /**
     * method to trace data from the given map of trace data in the parameter
     * @param newTraceData
     */
    public void addToTraceData(LinkedHashMap<String, ArrayList> newTraceData) {
		this.traceData.putAll(newTraceData);
	}
    /**
     * method to add trace data for given channelName with given ArrayList of data
     * @param channelName
     * @param dataList
     */
	public void addToTraceData(String channelName, ArrayList dataList) {
		this.traceData.put(channelName, dataList);
	}
	/**
	 * method to add trace data for given channelName with given array of data
	 * @param channelName
	 * @param data
	 */
	public void addToTraceData(String channelName, long[] data) {
		ArrayList<Long> dataList = new ArrayList<Long>(data.length);
		for(int i=0; i<data.length; i++)
			dataList.add(new Long(data[i]));
		this.traceData.get(channelName).addAll(dataList);
	}

	/**
	 * method to add trace data for given channelName with given array of data
	 * @param channelName
	 * @param data
	 */
	public void addToTraceData(String channelName, int[] data) {
		ArrayList<Integer> dataList = new ArrayList<Integer>(data.length);
		for(int i=0; i<data.length; i++)
			dataList.add(new Integer(data[i]));
		this.traceData.get(channelName).addAll(dataList);
    }
	/**
	 * method to add trace data for given channelName with given array of data
	 * @param channelName
	 * @param data
	 */
    public void addToTraceData(String channelName, float[] data) {
		ArrayList<Float> dataList = new ArrayList<Float>(data.length);
		for(int i=0; i<data.length; i++)
			dataList.add(new Float(data[i]));
		this.traceData.get(channelName).addAll(dataList);
    }
    /**
     * method to add trace data for given channelName with given array of data
     * @param channelName
     * @param data
     */
    public void addToTraceData(String channelName, boolean[] data) {
		ArrayList<Boolean> dataList = new ArrayList<Boolean>(data.length);
		for(int i=0; i<data.length; i++)
			dataList.add(new Boolean(data[i]));
		this.traceData.get(channelName).addAll(dataList);
    }

    /**
     * method to remove trace sample-point data for the given channel at the given index
     * @param channelName
     * @param index
     */
    public void removeTraceDataAt(String channelName, int index) {
    	this.traceData.get(channelName).remove(index);
    }

    /**
     * method to remove trace sample-point data for the given channel at the first index i.e. at index = 0
     * @param channelName
     * @param data
     */
    public void removeTraceDataAtFirst(String channelName, Object data) {
    	int firstIndex = this.traceData.get(channelName).indexOf(data);
    	this.traceData.get(channelName).remove(firstIndex);
    }

    /**
     * method to remove trace sample-point data for the given channel at the last index
     * @param channelName
     * @param data
     */
    public void removeTraceDataAtLast(String channelName, Object data) {
    	int lastIndex = this.traceData.get(channelName).lastIndexOf(data);
    	this.traceData.get(channelName).remove(lastIndex);
    }
    /**
     * method to set tracedata for the channel identified by channelName,
     * with the data from the array identified by data prameter.
     * Note: This method should be used for the channels whose 'type' attribute is 'integer'
     * @param channelName
     * @param data
     */
    public void setChanneldataLong(String channelName, long[] data) {
		ArrayList<Long> dataList = new ArrayList<Long>(data.length);
		for(int i=0; i<data.length; i++)
			dataList.add(new Long(data[i]));
		this.traceData.put(channelName, dataList);
    }
    /**
     * method to set tracedata for the channel identified by channelName,
     * with the data from the array identified by data prameter.
     * Note: This method should be used for the channels whose 'type' attribute is 'integer'
     * @param channelName
     * @param data
     */
    public void setChanneldataInt(String channelName, int[] data) {
		ArrayList<Integer> dataList = new ArrayList<Integer>(data.length);
		for(int i=0; i<data.length; i++)
			dataList.add(new Integer(data[i]));
		this.traceData.put(channelName, dataList);

    }
    /**
     * method to set tracedata for the channel identified by channelName,
     * with the data from the array identified by data prameter.
     * Note: This method should be used for the channels whose 'type' attribute is 'decimal'
     * @param channelName
     * @param data
     */
    public void setChanneldataFloat(String channelName, float[] data) {
		ArrayList<Float> dataList = new ArrayList<Float>(data.length);
		for(int i=0; i<data.length; i++)
			dataList.add(new Float(data[i]));
		this.traceData.put(channelName, dataList);
    }

    /**
     * method to set tracedata for the channel identified by channelName,
     * with the data from the array identified by data prameter.
     * Note: This method should be used for the channels whose 'type' attribute is 'decimal'
     * @param channelName
     * @param data
     */
    public void setChanneldataDouble(String channelName, double[] data) {
		ArrayList<Double> dataList = new ArrayList<Double>(data.length);
		for(int i=0; i<data.length; i++)
			dataList.add(new Double(data[i]));
		this.traceData.put(channelName, dataList);
    }

	/**
	 * This method is used to validate the value given in from/to range parameters
	 *  in a TraceView object selecting data from the TraceData object
	 *  @param rangeIndex
	 */
	private boolean isRangeDataValid(int rangeIndex) throws InkMLException {
		// Check if the rangeIndex value is within the size of the traceSampleDataList
		String aChannelName = getAssociatedTraceFormat().getChannelList().get(0).getName();
		int nSamples = traceData.get(aChannelName).size();
		if((rangeIndex < 1 || rangeIndex > nSamples)) {
			return false;
		}
		return true;
	}

	/**
	 * This method assigns the associated context to the Trace Object.
	 * @param associatedContext the Context object to be associated with the Trace object
	 */
	public void setAssociatedContext(Context associatedContext) {
		if(null == associatedContext){
			logger.fine("Trace::setAssociatedContext, the given parameter context is null");
			return;
		}
		this.associatedContext = new Context(associatedContext);
		initTraceDataStructure();
	}
	/**
	 * method to get channelType for the given channelName in the parameter
	 * @param channelName
	 * @return ChannelType ("integer"|"decimal"|"boolean")
	 * @throws InkMLException
	 */
	public Channel.ChannelType getChannelType(String channelName) throws InkMLException{
		return getAssociatedTraceFormat().getChannel(channelName).getChannelType();
	}
	/*
	 * This utility method looks for the paternString in the given inputString
	 *  and replace that pattern match with the replacementString
	 */
	private String replaceString(String inputStr, String patternStr, String replacementStr) {
        // Compile regular expression
        Pattern pattern = Pattern.compile(patternStr);

        // Replace all occurrences of pattern in input
        Matcher matcher = pattern.matcher(inputStr);
        String output = matcher.replaceAll(replacementStr);
        return output;
    }

	/**
	 * Overriding the toString method to give the value of Trace Object
	 * @return simple view of trace data as string
	 */
	public String toString() {
		String id = getId();
		if(id==null)
			id="";
		String biodata = "\n\tTrace - objectid = "+this.hashCode()+
						 "\n\tId = "+ id +
						 "\n\tbrushRef = "+getBrushRef()+
						 "\n\tcontextRef = "+getContextRef();
		Brush brush = this.associatedContext.getBrush();
		if(null != brush)
			biodata = biodata + "\n\t associatedBrush = "+ brush.getId()+" - "+brush;
		if(null != this.associatedContext)
			biodata = biodata + "\n\t associatedContext = "+ this.associatedContext.getId()+" - "+this.associatedContext;
		biodata = biodata + "\n";
		return biodata;
	}

	/**
	 * method to write markup data of the trace as string
	 * @return the trace InkML markup data as string
	 */
	public String toInkML()
    {
    	String strInkML = "<trace";
    	String id = getId();
    	if(! "".equals(id))
    		strInkML+=" id=\""+id+"\"";
    	float timeOffset = getTimeOffset();
    	if(-1 != timeOffset)
    		strInkML+=" timeOffset=\""+timeOffset+"\"";
    	strInkML+=">";

    	Object keys[] = this.traceData.keySet().toArray();
    	ArrayList channels[] = new ArrayList[keys.length];
    	for(int i=0; i<keys.length; i++)
    	{
    		channels[i] = this.traceData.get(keys[i].toString());
    	}
    	int dataLen = channels[0].size();
    	for(int i=0; i<dataLen; i++)
    	{
    		for(int j=0; j<keys.length; j++)
    		{
    			ArrayList al = channels[j];
    			strInkML += " " + al.get(i);
    		}
    		if(i<dataLen-1)
    			strInkML += ",";//No comma after last point
    	}

    	strInkML += "</trace>";
    	return strInkML;
    }
	/**
	 * method to apply the transform, upon call, it get the simple multiplication factor
	 *  from the associated 'canvasTransform' and apply it on each sample point.
	 * @param channelName
	 * @param factor
	 */
	public void applyTransform(String channelName, double factor) {
		ArrayList channelValues = this.traceData.get(channelName);
		for(int i=0; i<channelValues.size(); i++) {
			double oldValue = ((Number) channelValues.get(i)).doubleValue();
			channelValues.set(i, new Double(oldValue*factor));
		}
	}
	/**
	 * method to get the trace data as Map ( 'Channel Name' as the key and an 'ArrayList' holding the sample point values.
	 * @return the traceData map
	 */
	public LinkedHashMap<String, ArrayList> getTraceData() {
		return traceData;
	}
	/**
	 * method to set 'timeOffset' attribute
	 * @param timeOffset
	 */
	public void setTimeOffset(long timeOffset) {
		this.attributesMap.put("timeOffset", String.valueOf(timeOffset));
	}
	/**
	 * method to set 'id' attribute
	 * @param id
	 */
	public void setId(String id) {
		this.attributesMap.put("id", id);
	}
	/**
	 * method to set an attribute
	 * @param attrName
	 * @param attrValue
	 */
	public void setAttribute(String attrName, String attrValue) {
		this.attributesMap.put(attrName, attrValue);
	}

	/**
	 * The core logic of processing the trace data
	 * @param traceDataStr
	 * @param currentContext
	 * @param definitions
	 * @throws InkMLException
	 */
	void processTraceElement(String traceDataStr, Context currentContext,
				Definitions definitions) throws InkMLException{
		if(null == this.associatedContext)
			resolveAssociatedContext(currentContext, definitions);

		//expand the implicit reference to context by setting
		// proper value to 'contextRef' attribute
		String ctxRef = attributesMap.get("contextRef");
		if(null == ctxRef){
			if(null != parentTraceGroup){
				ctxRef = parentTraceGroup.getContextRef();
			} else {
				ctxRef = "#"+currentContext.getId();
			}
			attributesMap.put("contextRef", ctxRef);
		}

		// co-occurence contrains evaluation
		String continution="";
		Continuation continuationAttr = Continuation.NONE;
		try {
			continution= attributesMap.get("continution");
			if(null != continution)
				continuationAttr = Continuation.valueOf(continution);
		} catch(IllegalArgumentException illArgExp) {
			logger.severe("The value = "+continution+ "is illegeal for the 'continution' attribute of trace element.");
		}

		String priorRefStr = attributesMap.get("priorRef");
		if(null == priorRefStr) {
			if((Continuation.middle == continuationAttr) ||
					(Continuation.end == continuationAttr)) {
				throw new InkMLException("Problem in the definition of trace:"+
						toString()+"The continuation attribute is either 'middle' "+
						" or 'end', but no value is given for 'priorRef' attribute.");
			}
		} else {
			if(Continuation.begin == continuationAttr) {
				logger.severe("Problem in the definition of tarce:"+
						toString()+"The continuation attribute is either 'begin' "+
						", but a value is given for 'priorRef' attribute. The priorRef value is ignored.");
			}
		}

		HashMap<String, Object> variableValueMap = new HashMap<String, Object> ();

		if("".equals(traceDataStr)) // trace with empty traceData
			return;
		logger.fine("Trace data read from XML Doc: "+ traceDataStr);
		traceDataStr = traceDataStr.trim();
		traceDataStr = traceDataStr.replace('\n', ' ');
		traceDataStr = replaceString(traceDataStr, "\'-", " \'-");
		traceDataStr = replaceString(traceDataStr, "\"-", " \"-");
		// replce 3-5 with 3<space>-5
		traceDataStr = replaceString(traceDataStr, "-", " -");
		traceDataStr = replaceString(traceDataStr, "\' -", "\'-");
		traceDataStr = replaceString(traceDataStr, "\" -", "\"-");
		// replce '3'5 with <space>'3<space>'5
		traceDataStr = replaceString(traceDataStr, "\'", " \'");
		traceDataStr = replaceString(traceDataStr, "\"", " \"");
		traceDataStr = replaceString(traceDataStr, "\\*", " \\* ");
		traceDataStr = replaceString(traceDataStr, "!", " !");
		logger.fine("Trace data after inserting required spaces for clarity: "+ traceDataStr);

		StringTokenizer st = new StringTokenizer(traceDataStr, ",");
		int nTraceSamples = st.countTokens();
		if(0 == nTraceSamples) // trace with empty traceData
			return;

		TraceFormat tf = this.associatedContext.getTraceFormat();
		ArrayList<Channel> channelList = tf.getChannelList();
		int nChannel = channelList.size();
		String[] channelNames = new String[nChannel];
		Channel.ChannelType[] channelTypes = new Channel.ChannelType[nChannel];
		lastPrefixMap = new HashMap<String, TracePrefix>();
		for(int i=0; i< nChannel; i++) {
			Channel chn = channelList.get(i);
			String channelName = chn.getName();
			channelNames[i] = channelName;
			channelTypes[i] = chn.getChannelType();
			if(null != this.priorReferredTrace)
				lastPrefixMap.put(channelName, this.priorReferredTrace.getLastPrefixOf(channelName));
			else
				lastPrefixMap.put(channelName, TracePrefix.NONE);

			// set the intial 'Vchn' (eg. vx and vy) values to 0 by checking the Channel list
			// 'Vchn' value is the value used in computing the effective diference value -
			//   to be added to the channel value when single/double difference prefix are used
			if(chn.getChannelType() == Channel.ChannelType.DECIMAL)
				variableValueMap.put(channelName, new Float(0));
			if(chn.getChannelType() == Channel.ChannelType.INTEGER)
				variableValueMap.put(channelName, new Integer(0));
		}

		int index = 0;
		while (st.hasMoreTokens()) {
			String traceSampleData = st.nextToken().trim();

			// extract and process the traceSample data
			StringTokenizer token = new StringTokenizer(traceSampleData, " ");
			//int valueIndex = 0;
			//while (token.hasMoreTokens()) {
			for(int valueIndex = 0; valueIndex<nChannel; valueIndex++){
				String channelValue=null;
				if(!(token.hasMoreTokens())){
					// value for channel is not given
					Channel chn = channelList.get(valueIndex);
					if(chn.isIntermittent()){
						//unreported intermittent channel values should be -
						// treated as if the prefix '*' (get previous value) given.
						if(0 == index)
							channelValue = chn.getDefaultValue();
						else
							channelValue = "*";
					} else {
						throw new InkMLException(
						"Error: Value must be given to all regular Channels.\n" +
						"Location: Value is not given for Channel "+ chn.getName()+
						" in sample point index "+ index +" [range starts from 0].");
					}
				} else {
					channelValue = token.nextToken().trim();
				}
				// check ' and " prefix constraints

				Object previousDoubleDiffVal = 0;
				processAndStoreChannelValue(channelNames[valueIndex%nChannel],
						channelValue,channelTypes[valueIndex%nChannel], index,
						variableValueMap, previousDoubleDiffVal);
				//valueIndex++;
			}
			index++;
		}

		// store this element in definitions as it may be reffered later -
		//  by its ID attribute
		if(! "".equals(getId())){
			definitions.addToIndirectChildrenMap(this);
		}
	}
	/**
	 * Method that validates the range of sample data for the defined range through 'min' and 'max' attributes of channels
	 * @param channelValue
	 * @param channelName
	 * @throws InkMLException
	 */
	private void validateRange(String channelValue, String channelName) throws InkMLException {
		Channel channel = this.associatedContext.getTraceFormat().getChannel(channelName);
		if(channel.getChannelType() == Channel.ChannelType.BOOLEAN)
			return;
		// min, max range check is applicale only to integer or deciaml channels
		String min = channel.getMin();
		String max = channel.getMax();
		try{
			double iValue, iMin, iMax;
			iValue = Double.parseDouble(channelValue);
			if(!"".equals(min)){
				iMin = Double.parseDouble(min);
				if(!"".equals(max)){
					iMax = Double.parseDouble(max);
					if((iValue<iMin) || (iValue>iMax)){
						throw new InkMLException(
								channelName+" Channel data value = "+channelValue+" is out of the format range (min="+
								min+", max="+max+")."
								);
					}
				} else {
					if(iValue<iMin){
						throw new InkMLException(
								channelName+" Channel data value = "+channelValue+" is under the format min range (min="+
								min+")."
								);
					}
				}

			} else if(!"".equals(max)){
				iMax = Double.parseDouble(max);
				if(iValue>iMax){
					throw new InkMLException(
							channelName+" Channel data value = "+channelValue+" is over the format max range (max="+
							max+")."
							);
				}
			}
		} catch(NumberFormatException e){
			logger.severe("Invalid value to min/max attribute of channel "+channelName);
		}
	}

	/**
	 * method to get map of all defined attributes (attributeName as 'key' and attributeValue as 'value')
	 * @return attributes map
	 */
	public HashMap<String, String> getAttributesMap() {
		return this.attributesMap;
	}
	/**
	 * Method to write inkml markup in to file or other stream
	 */
	public void writeXML(InkMLWriter writer) {
		writer.writeStartTag("trace", attributesMap);
		writer.incrementTagLevel();
		String[] traceDataStr = getTraceDataAsString();
		for(int i=0; i<traceDataStr.length; i++){
			writer.writeCharacters(traceDataStr[i]);
		}
		writer.decrementTagLevel();
		writer.writeEndTag("trace");
	}

	/**
	 * Method to log trace data in console window
	 *
	 */
	public void printTrace(){
		logger.fine("<trace>");
		String[] arr = getTraceDataAsString();
		for(int i=0;i<arr.length;i++){
			logger.fine(" "+arr[i]);
		}
		logger.fine("</trace>");
	}
}