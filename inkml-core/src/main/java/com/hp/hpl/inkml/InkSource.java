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
import java.util.logging.Logger;
/**
 * This class models the {@code <inkSource>} InkML Element.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 * Creation date : 4 May 2007
 */

public class InkSource implements ContextElement{
	private HashMap<String, String> attributesMap;
	private TraceFormat traceFormat;
	private SampleRate sampleRate;
	private ActiveArea activeArea;
	private ArrayList<SourceProperty> sourcePropertyList;
	private ChannelProperties channelProperties;
	private Latency latency;

	// logger
    private static Logger logger =
    	Logger.getLogger(InkSource.class.getName());

	/**
	 * Constructor to create an InkSource with traceFormat initiliazed with default traceformat.
	 *
	 */
	public InkSource() {
		this.attributesMap= new HashMap<String, String>();
		this.traceFormat=TraceFormat.getDefaultTraceFormat();
	}

	/**
	 * Constructor to create an InkSource with traceFormat initiliazed with given traceformat in the parameter.
	 *
	 */
	public InkSource(TraceFormat tf){
		this.traceFormat=tf;
	}

	/**
	 * Method that returns the defaultInkSource with 'id' attribute set to "DefaultInkSource"
	 * @return default InkSource data object
	 */
	public static InkSource getDefaultInkSource() {
		InkSource defaultInkSource = new InkSource();
		defaultInkSource.setId("DefaultInkSource");
		return defaultInkSource;
	}

	/**
	 * Method used by the Data Binding code of the parser to bind the attribute of inkSource data object.
	 * @param attrName attribute name
	 * @param attrValue attribute value
	 */
	public void setAttribute(String attrName, String attrValue) {
		this.attributesMap.put(attrName, attrValue);
	}

	/**
	 * Method to se 'id' attribute
	 * @param id
	 */
	public void setId(String id) {
		this.attributesMap.put("id", id);
	}
	/**
	 * Method to get 'id' attribute
	 */
	public String getId() {
		return this.attributesMap.get("id");
	}

	/**
	 * Method that returns the type of the InkML element which is "InkSource"
	 */
	public String getInkElementType() {
		return "InkSource";
	}

	/**
	 * Method to remove the inkSource property identified by the propertyName in the parameter
	 * @param propertyName
	 */
	void removeSourceProperty(String propertyName){
		if(null != sourcePropertyList){
			sourcePropertyList = new ArrayList<SourceProperty>();
			Iterator<SourceProperty> itr = sourcePropertyList.iterator();
			while(itr.hasNext()){
				SourceProperty sp = itr.next();
				if(propertyName.equalsIgnoreCase(sp.getName())){
					itr.remove();
				}
			}
		}
	}


	/**
	 * Method to assign value to a inkSource property identified by the propertyName
	 * in the parameter with value identified by the propertyValue
	 * @param propertyName
	 * @param propertyValue
	 */
	void setSourceProperty(String propertyName, double propertyValue){
		if(null != sourcePropertyList){
			sourcePropertyList = new ArrayList<SourceProperty>();
			Iterator<SourceProperty> itr = sourcePropertyList.iterator();
			boolean done=false;
			while(itr.hasNext()){
				SourceProperty sp = itr.next();
				if( propertyName.equalsIgnoreCase(sp.getName())){
					sp.setValue(propertyValue);
					done=true;
				}
			}
			if(!done)
				sourcePropertyList.add(new SourceProperty(propertyName, propertyValue));
		}
	}


/**
 * Overriden equlas ethod to compare InkSource objects.
 * !!!!!!! NOTE !!!!! - Implementation is not provided.
 * @param inkSource
 * @return status of if compared objects are equal or not
 */
	public boolean equals(InkSource inkSource) {
		if(inkSource == null)
			return false;
		logger.warning("The equals method implementtaion is not provided.\n"+
				"It returns 'true' for any two not null objects.");
		return true;
//		if(inkSource == null)
//			return false;
//		boolean isEqual = true;
//
//		return isEqual;
	}

	// Inner Classes
	/**
	 * It models the {@code <sampleRate>} child element of inkSource.
	 */
	public class SampleRate {
		private boolean isUniform=true;
		private double value;

		/**
		 * Constructor set the sampleRate value with given value in parameter (samples/second)
		 * @param value
		 */
		public SampleRate(double value){
			this.value=value;
		}

		public SampleRate(double value, boolean isUniform){
			this.value=value;
			this.isUniform=isUniform;
		}
		/**
		 * status if the 'uniform' attribute
		 * @return the isUniform
		 */
		public boolean isUniform() {
			return isUniform;
		}
		/**
		 * assign value for 'uniform' attribute
		 * @param isUniform the isUniform to set
		 */
		public void setUniform(boolean isUniform) {
			this.isUniform = isUniform;
		}
		/**
		 * get the sample rate value in samples/second
		 * @return the value
		 */
		public double getValue() {
			return value;
		}
		/**
		 * method to set the sample rate value
		 * @param value the value to set
		 */
		public void setValue(double value) {
			this.value = value;
		}

		/**
		 * mwthod to archive the sampleRate data object markup data to outputStream
		 * @param writer
		 */
		public void writeXML(InkMLWriter writer) {
			HashMap<String, String> attr= new HashMap<String, String>();
			attr.put("uniform", String.valueOf(this.isUniform));
			attr.put("value", String.valueOf(this.value));
			writer.writeEmptyStartTag("sampleRate", attr);
		}
	}

	/**
	 * This class models the {@code <latency>} child element of inkSource.
	 * @author Muthuselvam Selvaraj
	 *
	 */
	public class Latency {
		private double value;

		/**
		 * Constructor set value of latency
		 * @param value
		 */
		public Latency(double value){
			this.value = value;
		}
		/**
		 * method to get latency value
		 * @return the value
		 */
		public double getValue() {
			return value;
		}
		/**
		 * method to set value for Latency property
		 * @param value
		 */
		public void setValue(double value) {
			this.value = value;
		}
		/**
		 * method to archive Latency property of inkSource
		 * @param writer
		 */
		public void writeXML(InkMLWriter writer) {
			HashMap<String, String> attr= new HashMap<String, String>();
			attr.put("value", String.valueOf(this.value));
			writer.writeEmptyStartTag("latency", attr);
		}
	}

	/**
	 * class model the ActiveArea property of inkSource
	 * @author Muthuselvam Selvaraj
	 *
	 */
	public class ActiveArea {
		private String size="unknown";
		private double hegiht=-1;
		private double width=-1;
		private String units="unknown";


		/**
		 * method to get the height of ActiveArea
		 * @return the hegiht
		 */
		public double getHegiht() {
			return hegiht;
		}
		/**
		 * method to set the height of ActiveArea
		 * @param hegiht the hegiht to set
		 */
		public void setHegiht(double hegiht) {
			this.hegiht = hegiht;
		}
		/**
		 * method to get the size of ActiveArea
		 * @return the size
		 */
		public String getSize() {
			return size;
		}
		/**
		 * method to set the size of ActiveArea
		 * @param size the size to set
		 */
		public void setSize(String size) {
			this.size = size;
		}
		/**
		 * method to get the units of ActiveArea dimension
		 * @return the units
		 */
		public String getUnits() {
			return units;
		}
		/**
		 * method to set the units of ActiveArea dimension
		 * @param units the units to set
		 */
		public void setUnits(String units) {
			this.units = units;
		}
		/**
		 * method to get the width of ActiveArea
		 * @return the width
		 */
		public double getWidth() {
			return width;
		}
		/**
		 * method to set the width of ActiveArea
		 * @param width the width to set
		 */
		public void setWidth(double width) {
			this.width = width;
		}

		/**
		 *  method to archive ActiveArea property of inkSource
		 * @param writer
		 */
		public void writeXML(InkMLWriter writer) {
			HashMap<String, String> attributes = new HashMap<String, String>();
			if(!size.equals("unknown"))
				attributes.put("size", this.size);
			if(-1 != hegiht)
				attributes.put("hegiht", String.valueOf(hegiht));
			if(-1 != width)
				attributes.put("width", String.valueOf(width));
			if(!units.equals("unknown"))
				attributes.put("units", this.units);
			writer.writeEmptyStartTag("activeArea", attributes);
		}
	}

	/**
	 * class model the SourceProperty of inkSource
	 * @author Muthuselvam Selvaraj
	 *
	 */

	class SourceProperty {
		private String name; // required attribute
		private double value; // required attribute
		private String 	units="";

		/**
		 * Constructor to create a SourceProperty of inkSource
		 * @param name
		 * @param value
		 * @param units
		 */
		public SourceProperty(String name, double value, String units) {
			this.name=name;
			this.value=value;
			this.units=units;
		}
		/**
		 * Constructor to create a SourceProperty of inkSource
		 * @param name
		 * @param value
		 */
		public SourceProperty(String name, double value) {
			this(name, value, "unknown");
		}

		/**
		 * method to get the name of SourceProperty
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * method to set the name of SourceProperty
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * method to get the units of SourceProperty
		 * @return the units
		 */
		public String getUnits() {
			return units;
		}
		/**
		 * method to set the units of SourceProperty
		 * @param units the units to set
		 */
		public void setUnits(String units) {
			this.units = units;
		}
		/**
		 * method to get the value of SourceProperty element
		 * @return the value
		 */
		public double getValue() {
			return value;
		}
		/**
		 * method to set the value of SourceProperty element
		 * @param value the value to set
		 */
		public void setValue(double value) {
			this.value = value;
		}
		/**
		 * method to archive SourceProperty of inkSource
		 * @param writer
		 */
		public void writeXML(InkMLWriter writer) {
			HashMap<String, String> attr= new HashMap<String, String>();
			attr.put("name", this.name);
			attr.put("value", String.valueOf(this.value));
			if(!"".equals(units))
				attr.put("units", this.units);
			writer.writeEmptyStartTag("sourceProperty", attr);
		}
	}

	/**
	 * class model the ChannelProperties of inkSource
	 * @author Muthuselvam Selvaraj
	 *
	 */
	public class ChannelProperties {
		private ArrayList<ChannelProperty> ChannelPropertyList;

		/**
		 * Constructor for ChannelProperties child element of inkSource
		 *
		 */
		public ChannelProperties() {
			ChannelPropertyList = new ArrayList<ChannelProperty>();
		}

		/**
		 * method to add a ChannelProperty to the ChannelProperties collection
		 * @param property
		 */
		public void addChannelProperty(ChannelProperty property){
			if(null == ChannelPropertyList)
				ChannelPropertyList = new ArrayList<ChannelProperty>();
			ChannelPropertyList.add(property);
		}

		/**
		 * method to remove a ChannelProperty from the ChannelProperties collection
		 * @param channelName
		 * @param propertyName
		 */
		public void removeChannelProperty(String channelName, String propertyName){
			if(null != ChannelPropertyList){
				ChannelPropertyList = new ArrayList<ChannelProperty>();
				Iterator<ChannelProperty> itr = ChannelPropertyList.iterator();
				while(itr.hasNext()){
					ChannelProperty cp = itr.next();
					if( (channelName.equalsIgnoreCase(cp.getChannel())) &&
							(propertyName.equalsIgnoreCase(cp.getName()))){
						itr.remove();
					}
				}
			}
		}

		/**
		 * method to assign a ChannelProperty to the ChannelProperties collection
		 * @param channelName
		 * @param propertyName
		 * @param propertyValue
		 */
		public void setChannelProperty(String channelName, String propertyName,
				double propertyValue){
			if(null != ChannelPropertyList){
				ChannelPropertyList = new ArrayList<ChannelProperty>();
				Iterator<ChannelProperty> itr = ChannelPropertyList.iterator();
				while(itr.hasNext()){
					ChannelProperty cp = itr.next();
					if( (channelName.equalsIgnoreCase(cp.getChannel())) &&
							(propertyName.equalsIgnoreCase(cp.getName()))){
						cp.setValue(propertyValue);
					}
				}
			}
		}

		/**
		 * class model the ChannelProperty of inkSource
		 * @author Muthuselvam Selvaraj
		 *
		 */
		public class ChannelProperty {
			private String channel;
			private String name;
			private double value;
			private String units;

			/**
			 * Constructor to create ChannelProperty child element for ChannelProperties
			 * @param channelName
			 * @param propertyName
			 * @param value
			 */
			public ChannelProperty(String channelName, String propertyName,
					double value){
				this(channelName, propertyName, value, "unknown");
			}

			/**
			 * Constructor to create ChannelProperty child element for ChannelProperties
			 * @param channelName
			 * @param propertyName
			 * @param value
			 * @param units
			 */
			public ChannelProperty(String channelName, String propertyName,
					double value, String units){
				this.channel=channelName;
				this.name=propertyName;
				this.value=value;
				this.units=units;
			}

			/**
			 * method to get the channel name related to the ChannelProperty
			 * @return the channel
			 */
			public String getChannel() {
				return channel;
			}
			/**
			 * method to set the channel name related to the ChannelProperty
			 * @param channel the channel to set
			 */
			public void setChannel(String channel) {
				this.channel = channel;
			}
			/**
			 * method to get the property name
			 * @return the name
			 */
			public String getName() {
				return name;
			}
			/**
			 * method to set the property name
			 * @param name the name to set
			 */
			public void setName(String name) {
				this.name = name;
			}
			/**
			 * method to get the units
			 * @return the units
			 */
			public String getUnits() {
				return units;
			}
			/**
			 * method to set the units
			 * @param units the units to set
			 */
			public void setUnits(String units) {
				this.units = units;
			}
			/**
			 * method to get the value
			 * @return the value
			 */
			public double getValue() {
				return value;
			}
			/**
			 * method to set the value
			 * @param value the value to set
			 */
			public void setValue(double value) {
				this.value = value;
			}
			/**
			 *  method to archive channelProperty of inkSource
			 * @param writer
			 */
			public void writeXML(InkMLWriter writer) {
				HashMap<String, String> attr= new HashMap<String, String>();
				attr.put("channel", this.channel);
				attr.put("name", this.name);
				attr.put("value", String.valueOf(this.value));
				if(!"".equals(units))
					attr.put("units", this.units);
				writer.writeEmptyStartTag("channelProperty", attr);
			}
		}
		/**
		 * method to archive channelProperties of inkSource
		 * @param writer
		 */
		public void writeXML(InkMLWriter writer) {
			if(0 != ChannelPropertyList.size()){
				writer.writeStartTag("channelProperties", null);
				writer.incrementTagLevel();
				Iterator<ChannelProperty> itr = ChannelPropertyList.iterator();
				while(itr.hasNext()){
					ChannelProperty cp = itr.next();
					cp.writeXML(writer);
				}
				writer.decrementTagLevel();
				writer.writeEndTag("channelProperties");
			}
		}
	}

	/**
	 *  method to get markup string of the inkSource data object
	 */
	public String toInkML() {

		 	String inkSourceResult="<inkSource ";
			String id = getId();
		 	if(!id.equals("")){
				inkSourceResult+= "id='" + id+"' ";
			}
		 	String manufacturer = getManufacturer();
			if(!manufacturer.equals("")){
				inkSourceResult+= "manufacturer='" + manufacturer+"' ";
			}
			String model = getModel();
			if(!model.equals("")){
				inkSourceResult+= "model='" + model+"' ";
			}
			String serialNo = getSerialNo();
			if(!serialNo.equals("")){
				inkSourceResult+= "serialNo='" + serialNo+"' ";
			}
			String specificationRef = getSpecificationRef().getURIString();
			if(!specificationRef.equals("")){
				inkSourceResult+= "specificationRef='" + specificationRef+"' ";
			}
			String description = getDescription();
			if(!description.equals("")){
				inkSourceResult+= "description='" + description+"' ";
			}
			inkSourceResult+=">";
			if(traceFormat!=null){
				inkSourceResult+= traceFormat.toInkML();
			}
			inkSourceResult+="</inkSource>";
			return inkSourceResult;
	}
		public TraceFormat getTraceFormat() {
			return traceFormat;
		}
		public void setTraceFormat(TraceFormat traceFormat) {
			this.traceFormat = traceFormat;
		}
		public ActiveArea getActiveArea() {
			return activeArea;
		}
		public void setActiveArea(ActiveArea activeArea) {
			this.activeArea = activeArea;
		}
		public String getDescription() {
			return this.attributesMap.get("description");
		}
		public void setDescription(String description) {
			this.attributesMap.put("description", description);
		}
		public String getManufacturer() {
			return this.attributesMap.get("manufacturer");
		}
		public void setManufacturer(String manufacturer) {
			this.attributesMap.put("manufacturer", manufacturer);
		}

		/**
		 * method get the model number attribute of the inkSource device
		 * @return model number
		 */
		public String getModel() {
			return this.attributesMap.get("model");
		}

		/**
		 * method set the model number attribute of the inkSource device
		 * @param model number
		 */
		public void setModel(String model) {
			this.attributesMap.put("model", model);
		}

		/**
		 * method get the sampleRate attribute of the inkSource device
		 * @return sampleRate
		 */
		public SampleRate getSampleRate() {
			return sampleRate;
		}

		/**
		 * method set the sampleRate attribute of the inkSource device
		 * @param sampleRate
		 */
		public void setSampleRate(SampleRate sampleRate) {
			this.sampleRate = sampleRate;
		}

		/**
		 * method get the serial number attribute of the inkSource device
		 * @return serialNo
		 */
		public String getSerialNo() {
			return this.attributesMap.get("serialNo");
		}

		/**
		 * method set the serial number attribute of the inkSource device
		 * @param serialNo
		 */
		public void setSerialNo(String serialNo) {
			this.attributesMap.put("serialNo", serialNo);
		}
		/**
		 * method to get he inkSource specification URL
		 * @return specification URL
		 */
		public URI getSpecificationRef() {
			return new URI(this.attributesMap.get("specificationRef"));
		}

		/**
		 * method to set the inkSource specification URL (e.g.: manufacturer data sheet html page URL!)
		 * @param specificationRef
		 */
		public void setSpecificationRef(URI specificationRef) {
			this.attributesMap.put("specificationRef", specificationRef.toString());
		}

		/**
		 * method to set channel property child data object to inksource
		 * @param chnProbs
		 */
		void setChannelProperties(ChannelProperties chnProbs){
			this.channelProperties = chnProbs;
		}

		/**
		 * method to add channel property to inksource
		 * @param chnProperty
		 */
		void addToChannelProperties(ChannelProperties.ChannelProperty chnProperty){
			this.channelProperties.addChannelProperty(chnProperty);
		}

		/**
		 * Method to archive the inkSource data object
		 */
		public void writeXML(InkMLWriter writer) {
				writer.writeStartTag("inkSource", attributesMap);
				writer.incrementTagLevel();
				if(null != this.traceFormat)
					this.traceFormat.writeXML(writer);
				if(null != this.sampleRate)
					this.sampleRate.writeXML(writer);
				if(null != this.activeArea)
					this.activeArea.writeXML(writer);
				if(null != this.sourcePropertyList){
					for (SourceProperty sp : sourcePropertyList) {
						if(null != sp)
							sp.writeXML(writer);
					}
				}
				if(null != this.channelProperties){
					channelProperties.writeXML(writer);
				}
				writer.decrementTagLevel();
				writer.writeEndTag("inkSource");
		}

		/**
		 * Method to add inkSource property identified by sourceProperty
		 * @param sourceProperty
		 */
		public void addSourceProperty(SourceProperty sourceProperty) {
			if(null == sourcePropertyList)
				sourcePropertyList = new ArrayList<SourceProperty>();
			sourcePropertyList.add(sourceProperty);
		}

		/**
		 * Method set the latency data object for the inkSoource data object
		 * @param latency
		 */
		public void setLatency(Latency latency) {
			this.latency = latency;
		}

		/**
		 * method gives the latency child element data object of the inkSource
		 * @return latency data object
		 */
		public Latency getLatency() {
			return latency;
		}
}
