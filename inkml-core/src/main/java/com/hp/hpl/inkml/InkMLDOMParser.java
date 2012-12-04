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
 * $Revision: 274 $
 * $Author: selvarmu $
 * $LastChangedDate: 2008-07-07 21:24:00 +0530 (Mon, 07 Jul 2008) $
 ************************************************************************************/
package com.hp.hpl.inkml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import java.util.logging.Logger;

/**
 * This class provides DOM implementation of the InkMLParser interface.
 * It uses the Xerces parser bundled with JDK 1.5 or higher versions.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 */
public class InkMLDOMParser implements InkMLParser, ErrorHandler{
	private Document inkmlDOMDocument;
	private InkMLProcessor inkMLProcessor;
	protected static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";
	protected static final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";
	protected static final String DEFAULT_PARSER_NAME = "dom.wrappers.Xerces";

	private static Logger logger = Logger.getLogger(
    					  InkMLDOMParser.class.getName());

	public InkMLDOMParser(InkMLProcessor processor){
		this.inkMLProcessor = processor;
	}

	 /**
	  * bind data from parsed xml to the inkml data objects
	  * it passes the element to coresponding getXXX method based on their type(tag name) and performs the data binding
	  * @param ink the ink document data object to be populated with parsing an inkml file
	  */
	protected void bindData(Ink ink) throws InkMLException{
		logger.info("\nTo bind the parsed data to InkML data objects.\n");
		if(null ==  inkmlDOMDocument){
			logger.warning("No parsed data available for data binding.");
			return;
		}
		Element rootElmnt = inkmlDOMDocument.getDocumentElement();
		if ("ink".equalsIgnoreCase(rootElmnt.getLocalName())) {
			ink.setDocID(rootElmnt.getAttribute("documentID"));
			NodeList list = rootElmnt.getChildNodes();
			for(int i=0;i<list.getLength();i++) {
				Node node = list.item(i);
				if (!(node instanceof Element))
					continue;
				Element element = (Element) node;
				addToInkElementList(element, ink);
			}
    	} else if ("inkMLFragment".equalsIgnoreCase(rootElmnt.getLocalName())) {
    		NodeList list = rootElmnt.getChildNodes();
			for(int i=0;i<list.getLength();i++) {
				Node node = list.item(i);
				if (!(node instanceof Element))
					continue;
				Element element = (Element) node;
				addToInkElementList(element, ink);
			}
        }
	}

	/**
	 * Method to bind the direct children of Ink data object
	 * @param element child element of Ink
	 * @param ink ink data object
	 * @throws InkMLException
	 */
	protected void addToInkElementList(Element element, Ink ink) throws InkMLException{

		String tagName = element.getLocalName();

		// Content of <ink> is ( definitions | context | trace | traceGroup |
		//                          traceView | annotation | annotationXML )
		if (tagName.equals("definitions")) {
			Definitions definitions = ink.getDefinitions();
			NodeList childElements = element.getChildNodes();
			addToDefinitions(childElements, definitions );
		} else if (tagName.equals("context")) {
			// a <context> as direct child to <ink> updates the curentContext.
			Definitions defs=ink.getDefinitions();
			Context context = getContext(element, defs);


			//replace 'implicit contextRef' to the 'id' of the current context
			if("".equals(context.getContextRef())){
				context.setContextRef("#"+ink.getCurrentContext().getId());
			}

			context.deriveContextualChildrenData(ink.getDefinitions(), ink.getCurrentContext());

			ArrayList<Ink.contextChangeStatus> ctxChanges = ink.getContextChanges(context);
			if(0 == ctxChanges.size()){
				// no context change occured. Ignore this context element
				return;
			}

			//assign ID and place it in definitions
			if("".equals(context.getId())){
				String ctxId = InkMLIDGenerator.getNextIDForContext();
				while(defs.contains(ctxId)){
					ctxId = InkMLIDGenerator.getNextIDForContext();
				}
				context.setId(ctxId);
			}
			defs.addToDirectChildrenMap(context);
			inkMLProcessor.notifyContextChanged(context, ctxChanges);
			ink.setCurrentContext(context);
		} else if (tagName.equals("trace")) {
			Trace trace = getTrace(element);
			ink.addTrace(trace);
			inkMLProcessor.notifyTraceReceived(trace);
			if(! "".equals(trace.getId()))
				ink.getDefinitions().addToIndirectChildrenMap(trace);
		} else if (tagName.equals("traceGroup")) {
			TraceGroup traceGroup = getTraceGroup(element);
			traceGroup.resolveAssociatedContext(ink.getCurrentContext());
			ink.addTraceGroup(traceGroup);
			inkMLProcessor.notifyTraceGroupReceived(traceGroup);

			//store traceData having 'id' attibute to IndirectChildren
			if(! "".equals(traceGroup.getId()))
				ink.getDefinitions().addToIndirectChildrenMap(traceGroup);
			ArrayList<TraceDataElement> children = traceGroup.getTraceDataList();
			if(null != children){
				for(int i=0; i< children.size(); i++){
					TraceDataElement child = children.get(i);
					if(! "".equals(child.getId()))
						ink.getDefinitions().addToIndirectChildrenMap(child);
				}
			}
		} else if (tagName.equals("traceView")) {
			TraceView traceView = getTraceView(element, ink.getDefinitions());
			traceView.setAssociatedContext(ink.getCurrentContext());
			ink.addTraceView(traceView);
			inkMLProcessor.notifyTraceViewReceived(traceView);
			if(! "".equals(traceView.getId()))
				ink.getDefinitions().addToDirectChildrenMap(traceView);
		} else if (tagName.equals("annotation")) {
			Annotation annotation = getAnnotation(element);
			ink.addAnnotation(annotation);
		} else if (tagName.equals("annotationXML")) {
			AnnotationXML aXml = getAnnotationXML(element);
			ink.addAnnotationXML(aXml);
		} else {
			throw new InkMLException("The Element "+tagName+" is not a valid Child Element for InkML <Ink> Element");
		}
	}

	/**
	 * Method to bind AnnotationXML element
	 * @param element the AnnotationXML element
	 * @return AnnotationXML data object
	 * @throws InkMLException
	 */
	protected AnnotationXML getAnnotationXML(Element element) throws InkMLException {
			AnnotationXML aXml = new AnnotationXML();
			NamedNodeMap attributesMap = element.getAttributes();
			int length = attributesMap.getLength();
			for(int index=0; index < length; index++) {
				Attr attribute = (Attr) attributesMap.item(index);
				String attributeName = attribute.getName();
				if ("type".equals(attributeName)) {
					aXml.setType(attribute.getValue());
				} else if ("encoding".equals(attributeName)) {
					aXml.setEncoding(attribute.getValue());
				} else if ("href".equals(attributeName)) {
					String hRef=attribute.getValue();
					try {
						aXml.setHref(new URI(hRef));
						if(! "".equals(hRef))
							aXml.addAllToPropertyMap(hRef);
					} catch (InkMLException e) {
						throw new InkMLException(
						"Problem in binding 'href' attribute of "+
						"AnnotationXML data.\nReason: "+e.getMessage());
					}
				} else {
					aXml.addToOtherAttributesMap(attributeName, attribute.getValue());
				}
			}
			logger.finest("annotationXML received: "+element.toString());
			NodeList list = element.getChildNodes();
			int nChildren = list.getLength();
			if(nChildren > 0) {
				for(int i=0; i<nChildren; i++) {
					Node node = list.item(i);
					if (!(node instanceof Element))
						continue;
					Element childElement = (Element) node;
					//get the tagName to use as Key in the valueMap
					String tagName = childElement.getLocalName();
					//String key = this.parentXPath+"/"+tagName;
					String value = childElement.getFirstChild().getNodeValue();
					//propertyElementsMap.put(key, childElement);
					//propertyElementsMap.put(key, value);
					aXml.addToPropertyElementsMap(tagName, value);
					logger.finer("The property with name = "+ tagName + " is added to the propertyElementsMap.");
				}
			}
			return aXml;
		}

	/**
	 * Method to bind Annotation element
	 * @param element the Annotation element
	 * @return Annotation data object
	 * @throws InkMLException
	 */
	protected Annotation getAnnotation(Element element) throws InkMLException {
		Annotation annotation = new Annotation();

		NamedNodeMap attributesMap = element.getAttributes();
		int length = attributesMap.getLength();
		for(int index=0; index < length; index++) {
			Attr attribute = (Attr) attributesMap.item(index);
			String attributeName = attribute.getName();
			if ("type".equals(attributeName)) {
				annotation.setType(attribute.getValue());
			} else if ("encoding".equals(attributeName)) {
				annotation.setEncoding(attribute.getValue());
			} else {
				annotation.addToOtherAttributesMap(attributeName, attribute.getValue());
			}
		}
		Node valueNode = element.getFirstChild();
		if(null != valueNode)
			annotation.setAnnotationTextValue(valueNode.getNodeValue());
		return annotation;
	}

	/**
	 * Method to bind TraceView element
	 * @param element the TraceView element
	 * @param definitions the definitions data object to resolve the reference attributes
	 * @return TraceView data object
	 * @throws InkMLException
	 */
	protected TraceView getTraceView(Element element, Definitions definitions)
		throws InkMLException {
		TraceView traceView = new TraceView();
		String id  = element.getAttribute("id");
		if(! "".equals(id))
			traceView.setId(id);
		String contextRef = element.getAttribute("contextRef");
		if(! "".equals(contextRef))
			traceView.setContextRef(contextRef);
		traceView.resolveContext(definitions);
		String traceDataRef = element.getAttribute("traceDataRef");
		if(! "".equals(traceDataRef)) {
			traceView.setTraceDataRef(traceDataRef);
			traceView.setFromAttribute(element.getAttribute("from"));
			traceView.setToAttribute(element.getAttribute("to"));
			traceView.setSelectedTree(definitions);
		} else {
			// <traceView> does not have 'traceDataRef' attribute; then it is used to -
			//      group child <traceView> elements.
			NodeList list = element.getElementsByTagName("traceView");
			int nData = list.getLength();
			if(0 != nData){
				traceView.initChildViewList();
				for(int i=0; i<nData; i++) {
					Element traceViewElement = (Element)list.item(i);
					TraceView childTV = getTraceView(traceViewElement, definitions);
					traceView.addToChildTraceViewList(childTV);
				}
				// add child element of type annotation and annotationXML
				traceView.processChildren(definitions);
			}
		}
		return traceView;
	}

	/**
	 * Method to bind TraceGroup element
	 * @param element the TraceGroup element
	 * @return TraceGroup data object
	 * @throws InkMLException
	 */
	protected TraceGroup getTraceGroup(Element element) throws InkMLException {
		TraceGroup traceGroup = new TraceGroup();

		// bind attribute
		String id = element.getAttribute("id");
		String contextref = element.getAttribute("contextRef");
		String brushRef = element.getAttribute("brushRef");
		if(!"".equals(id))
			traceGroup.setId(id);
		if(!"".equals(contextref))
			traceGroup.setContextRef(contextref);
		if(!"".equals(brushRef))
			traceGroup.setBrushRef(brushRef);

		//bind child element
		NodeList list = element.getChildNodes();
		for(int i=0;i<list.getLength();i++) {
			Node node = list.item(i);
			if (!(node instanceof Element))
				continue;
			Element child = (Element) node;
			String tagName = child.getLocalName();
			if("trace".equals(tagName)){
				Trace trace = getTrace(child);
				trace.setParentTraceGroup(traceGroup);
				traceGroup.addToTraceData(trace);
			} else if("traceGroup".equals(tagName)){
				TraceGroup childTG = getTraceGroup(child);
				childTG.setParentTraceGroup(traceGroup);
				traceGroup.addToTraceData(childTG);
			}
		}
		return traceGroup;
	}

	/**
	 * Method to bind Trace element
	 * @param element the Trace element
	 * @return Trace data object
	 * @throws InkMLException
	 */
	protected Trace getTrace(Element element) throws InkMLException{
		Trace trace = new Trace();
		// set value of the object from the value of the DOM element
		// Extract and set Attribute values
		NamedNodeMap attrMap = element.getAttributes();
		int length = attrMap.getLength();
		for(int i=0; i<length; i++){
			Node attr = attrMap.item(i);
			trace.setAttribute(attr.getLocalName(), attr.getNodeValue());
		}
		//get trace data
		String traceText = "";
		NodeList nl = element.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
				traceText += nl.item(i).getNodeValue();
			}
		}
		Ink ink = inkMLProcessor.getInk();
		Context currCtx = ink.getCurrentContext();
		Definitions defs = ink.getDefinitions();
		trace.resolveAssociatedContext(currCtx, defs);
		trace.processTraceElement(traceText, currCtx, defs);

		return trace;
	}

	/**
	 * Method to bind the children of Definitions element
	 * @param elements the Definitions child element
	 * @param definitions the definitions data object to resolve the reference attributes
	 * @throws InkMLException
	 */
	protected void addToDefinitions(NodeList elements, Definitions definitions) throws InkMLException{
		for(int i=0;i<elements.getLength();i++) {
			Node node = elements.item(i);
			if (!(node instanceof Element))
				continue;
			Element element = (Element) node;
			String tagName = element.getLocalName();
			String id = element.getAttribute("id");
			if("".equals(id)){
				throw new InkMLException("Elements within a <definitions> block must have an 'id' attribute. A "+tagName+" element do not have 'id' attribute.");
			}
			InkElement inkElement=null;
			// when the object of child elements created, the elements having a non empty id are added to definitions.
			//   This is done in the constructors of all the child elements of Definitions
			if("brush".equalsIgnoreCase(tagName)) {
				inkElement = getBrush(element);
			} else if("traceFormat".equalsIgnoreCase(tagName)) {
				inkElement = getTraceFormat(element, definitions);
			} else if("context".equalsIgnoreCase(tagName)) {
				Context context = getContext(element, definitions);
				// resolve implicit references for contextual elements to their counterpart in the "DefaultContext"
				context.resolveImplicitReferenceWithDefaultContext();
				inkElement = context;
			} else if("inkSource".equalsIgnoreCase(tagName)) {
				inkElement = getInkSource(element, definitions);
			} else if("trace".equalsIgnoreCase(tagName)) {
				inkElement = getTrace(element);
			} else if("traceGroup".equalsIgnoreCase(tagName)) {
				inkElement = getTraceGroup(element);
			} else if("traceView".equalsIgnoreCase(tagName)) {
				inkElement = getTraceView(element, definitions);
			} else if("canvas".equalsIgnoreCase(tagName)) {
				inkElement = getCanvas(element, definitions);
			} else if("canvasTransform".equalsIgnoreCase(tagName)) {
				inkElement = getCanvasTransform(element);
			} else if("timestamp".equalsIgnoreCase(tagName)) {
				inkElement = getTimestamp(element);
			} else if("mapping".equalsIgnoreCase(tagName)) {
				inkElement = getMapping(element);
			} else {
				throw new InkMLException("Parse Error: The element "+ tagName +
						"should not be a child to <definitions> element.\n");
			}
			definitions.addToDirectChildrenMap(inkElement);
		}
	}

	/**
	 * Method to bind Mapping element
	 * @param element the Mapping element
	 * @return Mapping data object
	 * @throws InkMLException
	 */
	protected Mapping getMapping(Element element) throws InkMLException{
		Mapping mapping = new Mapping();
		// Extract and set Attribute values
		String type = element.getAttribute("type");
		logger.info("mapping type="+type);
		if((!"identity".equalsIgnoreCase(type))
				&& (!"unknown".equalsIgnoreCase(type))){
			throw new InkMLException(
					"Feature not implemented: 'Mapping' with 'type' attribute other than identity and unknown");
		}
		mapping.setType(type);
		String id = element.getAttribute("id");
		if(!id.equals(""))
			mapping.setId(id);
		return mapping;
	}

	/**
	 * Method to bind Timestamp element
	 * @param element the Timestamp element
	 * @return Timestamp data object
	 * @throws InkMLException
	 */
	protected Timestamp getTimestamp(Element element) throws InkMLException{
		throw new InkMLException(
		"Feature not implemented. Mapping of type other than identity or unknown");
	}

	/**
	 * Method to bind CanvasTransform element
	 * @param element the CanvasTransform element
	 * @return CanvasTransform data object
	 * @throws InkMLException
	 */
	protected CanvasTransform getCanvasTransform(Element element) throws InkMLException {
		CanvasTransform canvasTransform = new CanvasTransform();
		// Extract and set Attribute values
		NamedNodeMap attrMap = element.getAttributes();
		int length = attrMap.getLength();
		for(int i=0; i<length; i++){
			Node attr = attrMap.item(i);
			canvasTransform.setAttribute(attr.getLocalName(), attr.getNodeValue());
		}

		NodeList list = element.getElementsByTagName("mapping");
		int nMappingChildren = list.getLength();
		if(nMappingChildren==2) {
			canvasTransform.setForwardMapping(getMapping((Element) list.item(0)));
			canvasTransform.setReverseMapping(getMapping((Element) list.item(1)));
		} else if(nMappingChildren==1) {
			canvasTransform.setForwardMapping(getMapping((Element) list.item(0)));
		}
		return canvasTransform;
	}

	/**
	 * Method to bind Canvas element
	 * @param element the Canvas element
	 * @param definitions the definitions data object to resolve the reference attributes
	 * @return Canvas data object
	 * @throws InkMLException
	 */
	protected Canvas getCanvas(Element element, Definitions definitions) throws InkMLException {
		Canvas canvas = new Canvas();
		// Extract and set Attribute values
		NamedNodeMap attrMap = element.getAttributes();
		int length = attrMap.getLength();
		for(int i=0; i<length; i++){
			Node attr = attrMap.item(i);
			canvas.setAttribute(attr.getLocalName(), attr.getNodeValue());
		}

		NodeList list = element.getElementsByTagName("traceFormat");
		if(list.getLength() != 0) {
			canvas.setTraceFormat(getTraceFormat((Element) list.item(0), definitions));
		}

		return canvas;
	}

	/**
	 * Method to bind InkSource element
	 * @param element the InkSource element
	 * @param definitions the definitions data object to resolve the reference attributes
	 * @return InkSource data object
	 * @throws InkMLException
	 */
	protected InkSource getInkSource(Element element, Definitions definitions) throws InkMLException {
		InkSource inkSrc = new InkSource();
		// Extract and set Attribute values
		NamedNodeMap attrMap = element.getAttributes();
		int length = attrMap.getLength();
		for(int i=0; i<length; i++){
			Node attr = attrMap.item(i);
			inkSrc.setAttribute(attr.getLocalName(), attr.getNodeValue());
		}

		NodeList list = element.getElementsByTagName("traceFormat");
		if(list.getLength() != 0) {
			inkSrc.setTraceFormat(getTraceFormat((Element) list.item(0), definitions));
		}

		list = element.getElementsByTagName("sampleRate");
		if(list.getLength() != 0) {
			inkSrc.setSampleRate(getSampleRate((Element) list.item(0), inkSrc));
		}
		list = element.getElementsByTagName("latency");
		if(list.getLength() != 0) {
			inkSrc.setLatency(getLatency((Element) list.item(0), inkSrc));
		}
		list = element.getElementsByTagName("activeArea");
		if(list.getLength() != 0) {
			inkSrc.setActiveArea(getActiveArea((Element) list.item(0), inkSrc));
		}
		list = element.getElementsByTagName("srcProperty");
		for(int i=0; i<list.getLength(); i++){
			inkSrc.addSourceProperty(getSourceProperty((Element) list.item(i), inkSrc));
		}
		list = element.getElementsByTagName("channelProperties");
		if(list.getLength() != 0) {
			inkSrc.setChannelProperties(getChannelProperties((Element) list.item(0), inkSrc));
		}
		return inkSrc;
	}

	/**
	 * Method to bind InkSource.Latency element
	 * @param element the InkSource.Latency element
	 * @param inkSrc the enclosing InkSource data object
	 * @return InkSource.Latency data object
	 * @throws InkMLException
	 */
	protected InkSource.Latency getLatency(Element element, InkSource inkSrc) throws InkMLException {
		String value = element.getAttribute("value");
		if("".equals(value)){
			return inkSrc.new Latency(new Double(value).doubleValue());
		}
		return null;
	}

	/**
	 * Method to bind InkSource.ActiveArea element
	 * @param element the InkSource.ActiveArea element
	 * @param inkSrc the enclosing InkSource data object
	 * @return InkSource.ActiveArea data object
	 * @throws InkMLException
	 */
	protected InkSource.ActiveArea getActiveArea(Element element, InkSource inkSrc) throws InkMLException {
		InkSource.ActiveArea activeArea = inkSrc.new ActiveArea();
		NamedNodeMap attrMap = element.getAttributes();
		int length = attrMap.getLength();
		for(int i=0; i<length; i++){
			Node attr = attrMap.item(i);
			String attrName = attr.getLocalName();
			if("size".equals(attrName))
				activeArea.setSize(attr.getNodeValue());
			if("height".equals(attrName))
				activeArea.setHegiht(new Double(attr.getNodeValue()).doubleValue());
			if("width".equals(attrName))
				activeArea.setWidth(new Double(attr.getNodeValue()).doubleValue());
			if("units".equals(attrName))
				activeArea.setUnits(attr.getNodeValue());
		}
		return activeArea;
	}

	/**
	 * Method to bind InkSource.SampleRate element
	 * @param element the InkSource.SampleRate element
	 * @param inkSrc the enclosing InkSource data object
	 * @return InkSource.SampleRate data object
	 * @throws InkMLException
	 */
	protected InkSource.SampleRate getSampleRate(Element element, InkSource inkSrc)
		throws InkMLException {
		String isUniform = element.getAttribute("uniform");
		String value = element.getAttribute("value");
		InkSource.SampleRate sampleRate;
		if("".equals(isUniform)){
			sampleRate = inkSrc.new SampleRate(new Double(value).doubleValue());
		} else {
			sampleRate = inkSrc.new SampleRate(new Double(value).doubleValue(),
										new Boolean(isUniform).booleanValue());
		}
		return sampleRate;
	}

	/**
	 * Method to bind InkSource.ChannelProperties element
	 * @param element the InkSource.ChannelProperties element
	 * @param inkSrc the enclosing InkSource data object
	 * @return InkSource.ChannelProperties data object
	 * @throws InkMLException
	 */
	protected InkSource.ChannelProperties getChannelProperties(Element element,
			InkSource inkSrc) throws InkMLException {
		NodeList list = element.getElementsByTagName("channelProperty");
		int nChnProperty = list.getLength();
		if(0 != nChnProperty){
			InkSource.ChannelProperties chnProps = inkSrc.new ChannelProperties();
			InkSource.ChannelProperties.ChannelProperty chnProp;
			for(int i=0; i<nChnProperty; i++){
				chnProp = getChannelProperty((Element)list.item(i), chnProps);
				chnProps.addChannelProperty(chnProp);
			}
			return chnProps;
		}
		return null;
	}

	/**
	 * Method to bind InkSource.ChannelProperties.ChannelProperty element
	 * @param element the InkSource.ChannelProperties.ChannelProperty element
	 * @param chnProps the enclosing InkSource.ChannelProperties data object
	 * @return InkSource.ChannelProperties.ChannelProperty data object
	 * @throws InkMLException
	 */
	protected InkSource.ChannelProperties.ChannelProperty getChannelProperty(Element element,
			InkSource.ChannelProperties chnProps) throws InkMLException {
		InkSource.ChannelProperties.ChannelProperty chnProp = null;
		String channel = element.getAttribute("channel");
		String name = element.getAttribute("name");
		String value = element.getAttribute("value");
		String units = element.getAttribute("units");
		if("".equals(units)){
			chnProp = chnProps.new ChannelProperty(channel, name, new Double(value).doubleValue());
		} else {
			chnProp = chnProps.new ChannelProperty(channel, name, new Double(value).doubleValue(), units);
		}
		return chnProp;
	}

	/**
	 * Method to bind InkSource.SourceProperty element
	 * @param element the InkSource.SourceProperty element
	 * @param inkSrc the enclosing InkSource data object
	 * @return InkSource.SourceProperty data object
	 * @throws InkMLException
	 */
	protected InkSource.SourceProperty getSourceProperty(Element element, InkSource inkSrc)
														throws InkMLException {
		String name = element.getAttribute("name");
		String value = element.getAttribute("value");
		String units = element.getAttribute("units");
		InkSource.SourceProperty srcProperty;
		if("".equals(units)){
			srcProperty = inkSrc.new SourceProperty(name, new Double(value).doubleValue());
		} else {
			srcProperty = inkSrc.new SourceProperty(name, new Double(value).doubleValue(), units);
		}
		return srcProperty;
	}

	/**
	 * Method to bind Context element
	 * @param element the Context element
	 * @param definitions the definitions data object to resolve the reference attributes
	 * @return Context data object
	 * @throws InkMLException
	 */
	protected Context getContext(Element element, Definitions definitions) throws InkMLException {
		Context context = new Context();
		// set value of the object from the value of the DOM element
		// Extract and set Attribute values
		NamedNodeMap attrMap = element.getAttributes();
		int length = attrMap.getLength();
		for(int i=0; i<length; i++){
			Node attr = attrMap.item(i);
			context.setAttribute(attr.getLocalName(), attr.getNodeValue());
		}
		NodeList list = element.getChildNodes();
		for(int i=0;i<list.getLength();i++) {
			Node node = list.item(i);
			if (!(node instanceof Element))
				continue;
			Element child = (Element) node;
			addToContextChildrenList(child, context, definitions);
		}
		return context;
	}

	/**
	 * Method to bind the children of context element
	 * @param ctxChild child element of context element
	 * @param context context data object
	 * @param definitions the definitions data object to resolve the reference attributes
	 * @throws InkMLException
	 */
	protected void addToContextChildrenList(Element ctxChild, Context context, Definitions definitions) throws InkMLException {
		String tagName = ctxChild.getLocalName();

		// Content of <context> is ( brush | traceFormat |  )
		if (tagName.equals("brush")) {
			Brush brush = getBrush(ctxChild);
			context.addToContextElementList(brush);
		} else if (tagName.equals("traceFormat")) {
			TraceFormat tf = getTraceFormat(ctxChild, definitions);
			context.addToContextElementList(tf);
		} else if (tagName.equals("canvas")) {
			Canvas canvas = getCanvas(ctxChild, definitions);
			context.addToContextElementList(canvas);
		} else if (tagName.equals("canvasTransform")) {
			CanvasTransform canvasTransform = getCanvasTransform(ctxChild);
			context.addToContextElementList(canvasTransform);
		} else if (tagName.equals("inkSource")) {
			InkSource inkSrc = getInkSource(ctxChild, definitions);
			context.addToContextElementList(inkSrc);
		} else if (tagName.equals("timeStamp")) {
			Timestamp timeStamp = getTimestamp(ctxChild);
			context.addToContextElementList(timeStamp);
		}
	}

	/**
	 * Method to bind brush element
	 * @param element brush element
	 * @return brush data object
	 * @throws InkMLException
	 */
	protected Brush getBrush(Element element) throws InkMLException {
		String id = element.getAttribute("id");
		Brush brush = new Brush(id);
		String brushRef = element.getAttribute("brushRef");
		if(!"".equals(brushRef))
			brush.setBrushRef(brushRef);

		//Process the child <annotation>, <annotationXML> elements
		NodeList list = element.getChildNodes();
		for(int i=0;i<list.getLength();i++) {
			Node node = list.item(i);
			if (!(node instanceof Element))
				continue;
			Element childElement = (Element) node;
			String tagName = childElement.getLocalName();
			if("annotationXML".equalsIgnoreCase(tagName)) {
				AnnotationXML aXml = getAnnotationXML(childElement);
				brush.setAnnotationXML(aXml);
			} else if("annotation".equalsIgnoreCase(tagName)) {
				Annotation annotation = getAnnotation(childElement);
				brush.setAnnotation(annotation);
			}
		}
		return brush;
	}

	/**
	 * Method to bind TraceFormat
	 * @param element TraceFormat element
	 * @param definitions the definitions data object to resolve the reference attributes
	 * @return TraceFormat data object
	 * @throws InkMLException
	 */
	protected TraceFormat getTraceFormat(Element element, Definitions definitions) throws InkMLException {
		TraceFormat traceFormat = new TraceFormat();
		String id = element.getAttribute("id");
		if(!"".equals(id)){
			traceFormat.setId(element.getAttribute("id"));
			definitions.addToIndirectChildrenMap(traceFormat);
		}
		String href= element.getAttribute("href");
		if(!"".equals(href)){
			TraceFormat refferedTF = definitions.getTraceFormatRefElement(href);
			traceFormat.setHref(href);
			traceFormat.setChannelList(refferedTF.getChannelList());
		}
		NodeList list = element.getElementsByTagName("channel");
		int nChannel = list.getLength();

		/*
		 if(0 == nChannel) {
			// if no channel available in the traceFormat, copy the default channels
			if(traceFormat.channelMap.size()==0){
					// add the channel for default TraceFormat
				Channel xChannel = new Channel("X",Channel.ChannelType.DECIMAL);
				Channel yChannel = new Channel("Y",Channel.ChannelType.DECIMAL);
				traceFormat.addChannel(xChannel);
				traceFormat.addChannel(yChannel);
			}
		}
		*/
		for(int i=0; i<nChannel; i++) {
			Element channelElmnt = (Element)list.item(i);
			Channel ch = getChannel(channelElmnt);
			traceFormat.addChannel(ch);
		}
		return traceFormat;
	}

	/**
	 * Method to bind Channel element
	 * @param channelElement Channel element
	 * @return Channel data object
	 * @throws InkMLException
	 */
	protected Channel getChannel(Element channelElement) throws InkMLException{
		Channel channel = null;
		String chnName = channelElement.getAttribute("name");
		if("".equals(chnName)){
			throw new InkMLException("Channel element must have value for 'name' attribute");
		}else
			channel = new Channel(chnName);
		// checking for intermittend channel
		if("intermittentChannels".equalsIgnoreCase(channelElement.getParentNode().getLocalName()))
			channel.setIntermittent(true);

		// Extract and set Attribute values
		NamedNodeMap attrMap = channelElement.getAttributes();
		int length = attrMap.getLength();
		for(int i=0; i<length; i++){
			Node attr = attrMap.item(i);
			channel.setAttribute(attr.getLocalName(), attr.getNodeValue());
		}
		return channel;
	}

	/**
	 * Method to parse the InkML file identified by the inkmlFilename given
	 * in the parameter and creates data objects. It performs validation with schema. 
         * It must have "ink" as root element with InkML name space specified with 
	 * xmlns="http://www.w3.org/2003/InkML".
	 * The schema location may be specified. If it is not specified or if relative path of 	  
	 * the InkML.xsd file is specified, then the InkML.xsd file path must be added to 		 * CLASSPATH for the parser to locate it. An example of a typical header is,
	 * <ink xmlns="http://www.w3.org/2003/InkML" 
	 * 	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	 *      xsi:schemaLocation="http://www.w3.org/2003/InkML C:\project\schema\inkml.xsd">
	 * @param inkmlFileName
	 * @throws InkMLException
	 */
	public void parseInkMLFile(String inkmlFileName) throws InkMLException {
		
		// Get the DOM document object by parsing the InkML input file
		InputSource input=null;
		try {
			input = new InputSource(new FileInputStream(inkmlFileName));
		} catch (FileNotFoundException e1) {
			throw new InkMLException("The input inkml file ("+inkmlFileName+") not found.");
		}

		DOMParser parser = new DOMParser();
		parser.setErrorHandler(this);
		
		// check the JVM parameter java -DschemaValidation={"true"|"false"}
		boolean isSchemaValidationEnabled=true;
		String validationStatus = System.getProperty("com.hp.hpl.inkml.SchemaValidation");
		if(null != validationStatus){
			validationStatus=validationStatus.trim().toLowerCase();
			if("true".equals(validationStatus)) {
				isSchemaValidationEnabled=true;	
			}
			else if("false".equals(validationStatus)) {
				isSchemaValidationEnabled=false;
			}	
			else {
				logger.warning("Invalid value to the JVM parameter '-Dcom.hp.hpl.inkml.SchemaValidation'.");
				isSchemaValidationEnabled=false;
			}
		}

		if(isSchemaValidationEnabled) {
			logger.info("Validation using schema is enabled.");
			try {
				java.net.URL schemaFile = getClass().getClassLoader().getResource("inkml.xsd");
				if(null != schemaFile){
					parser.setProperty("http://apache.org/xml/properties/schema/" +
							"external-schemaLocation","http://www.w3.org/2003/InkML "+schemaFile.getPath());
				} else {
					logger.severe("Failed to load Schema (inkml.xsd) file");
				}
			}catch (Exception e){
				logger.info("Exception in loading schema file for validation. Reason - "+e.getMessage());
			}
			try {
				try {
					parser.setFeature(VALIDATION_FEATURE_ID, true);
					parser.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);					
				} catch (org.xml.sax.SAXNotRecognizedException e) {
					logger.severe("Unrecognized feature: "+ SCHEMA_VALIDATION_FEATURE_ID);
				} catch (org.xml.sax.SAXNotSupportedException e) {
					logger.severe("Unsupported feature: " + SCHEMA_VALIDATION_FEATURE_ID);
				}
			}catch (Exception e) {
				logger.warning("Parser does not support feature ("+SCHEMA_VALIDATION_FEATURE_ID+")");
			}
		}
		else {
			logger.info("Validation using schema is disabled.");
		}
		
		try {
			parser.parse(input);
		} catch (SAXException e) {
			throw new InkMLException("Error in parsing Input InkML XML file.\n Message: "+e.getMessage());
		} catch (IOException e) {
			throw new InkMLException("I/O error while parsing Input InkML XML file.\n Message: "+e.getMessage());
		}

		logger.info("\nInput InkML XML file parsing is completed.\n");

		//get the DOM document object of the input InkML XML file.
		inkmlDOMDocument =  parser.getDocument();
		inkMLProcessor.beginInkSession();
		bindData(inkMLProcessor.getInk());
	}

	/**
	 * Method to parse the InkML string markup data identified by the inkmlStr given
	 * @param inkmlStr String markup data
	 * @throws InkMLException
	 */
	public void parseInkMLString(String inkmlStr) throws InkMLException {
		if(inkmlStr.indexOf("ink") == -1){
			// the given welformed inkmlStr does not contain complete <ink> document as string.
			// wrap it with a false root element, <inkMLFramgment>. It is called as fragment.
			inkmlStr = "<inkMLFramgment>"+ inkmlStr +" </inkMLFramgment>";
		}
		logger.fine(inkmlStr);
		DOMParser parser = new DOMParser();
		parser.setErrorHandler(this);		
		logger.info("Validation using schema is disabled.");

		try {
    		parser.parse( new InputSource(new StringReader(inkmlStr)) );
    	}catch(SAXException saxExp){
    		logger.severe("InkProcessor::parseInk method. Error in parsing "+
    						"Ink String data.\n"+saxExp.getMessage());
    	}catch(IOException ioExp){
    		logger.severe("InkProcessor::parseInk method. IO Error while "+
    						"parsing Ink String data.\n"+ioExp.getMessage());
    	}
    	inkmlDOMDocument = parser.getDocument();
    	bindData(inkMLProcessor.getInk());
	}

    /** Prints the error message. */
    private void printError(String messageType, SAXParseException exception) {

        System.err.print("[");
        System.err.print(messageType);
        System.err.print("] ");
        String systemId = exception.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            System.err.print(systemId);
        }
        System.err.print(':');
        System.err.print(exception.getLineNumber());
        System.err.print(':');
        System.err.print(exception.getColumnNumber());
        System.err.print(": ");
        System.err.print(exception.getMessage());
        System.err.println();
        System.err.flush();

    }

    // printError(String,SAXParseException) functions
    /**
     * Method to display XML Parse and schema validation error messages
     */
    public void error(SAXParseException exception) throws SAXException {
		printError("Error", exception);
		System.exit(-1);
	}

    /**
     * Method to display XML Parse and schema validation fatalError messages
     */
    public void fatalError(SAXParseException exception) throws SAXException {
		printError("FatalError", exception);
		System.exit(-1);
	}

    /**
     * Method to display XML Parse and schema validation warning messages
     */
	public void warning(SAXParseException exception) throws SAXException {
		printError("Warning", exception);
	}
}