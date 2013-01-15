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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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

/**
 * This class provides DOM implementation of the InkMLParser interface. It uses the Xerces parser bundled with JDK 1.5 or higher versions.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 */
public class InkMLDOMParser implements InkMLParser, ErrorHandler {
    private Document inkmlDOMDocument;
    private final InkMLProcessor inkMLProcessor;

    private static Logger LOG = Logger.getLogger(InkMLDOMParser.class.getName());

    /**
     * Constructor
     * 
     * @param processor
     */
    public InkMLDOMParser(final InkMLProcessor processor) {
        super();
        this.inkMLProcessor = processor;
    }

    /**
     * bind data from parsed xml to the inkml data objects it passes the element to coresponding getXXX method based on their type(tag name) and performs the
     * data binding
     * 
     * @param ink the ink document data object to be populated with parsing an inkml file
     */
    protected void bindData(final Ink ink) throws InkMLException {
        InkMLDOMParser.LOG.info("\nTo bind the parsed data to InkML data objects.\n");
        if (null == this.inkmlDOMDocument) {
            InkMLDOMParser.LOG.warning("No parsed data available for data binding.");
            return;
        }
        final Element rootElmnt = this.inkmlDOMDocument.getDocumentElement();
        if ("ink".equalsIgnoreCase(rootElmnt.getLocalName())) {
            ink.setDocID(rootElmnt.getAttribute("documentID"));
            final NodeList list = rootElmnt.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                final Node node = list.item(i);
                if (!(node instanceof Element)) {
                    continue;
                }
                final Element element = (Element) node;
                this.addToInkElementList(element, ink);
            }
        } else if ("inkMLFragment".equalsIgnoreCase(rootElmnt.getLocalName())) {
            final NodeList list = rootElmnt.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                final Node node = list.item(i);
                if (!(node instanceof Element)) {
                    continue;
                }
                final Element element = (Element) node;
                this.addToInkElementList(element, ink);
            }
        }
    }

    /**
     * Method to bind the direct children of Ink data object
     * 
     * @param element child element of Ink
     * @param ink ink data object
     * @throws InkMLException
     */
    protected void addToInkElementList(final Element element, final Ink ink) throws InkMLException {

        final String tagName = element.getLocalName();

        // Content of <ink> is ( definitions | context | trace | traceGroup |
        // traceView | annotation | annotationXML )
        if (tagName.equals("definitions")) {
            final Definitions definitions = ink.getDefinitions();
            final NodeList childElements = element.getChildNodes();
            this.addToDefinitions(childElements, definitions);
        } else if (tagName.equals("context")) {
            // a <context> as direct child to <ink> updates the curentContext.
            final Definitions defs = ink.getDefinitions();
            final Context context = this.getContext(element, defs);

            // replace 'implicit contextRef' to the 'id' of the current context
            if ("".equals(context.getContextRef())) {
                context.setContextRef("#" + ink.getCurrentContext().getId());
            }

            context.deriveContextualChildrenData(ink.getDefinitions(), ink.getCurrentContext());

            final ArrayList<Ink.contextChangeStatus> ctxChanges = ink.getContextChanges(context);
            if (0 == ctxChanges.size()) {
                // no context change occured. Ignore this context element
                return;
            }

            // assign ID and place it in definitions
            if ("".equals(context.getId())) {
                String ctxId = InkMLIDGenerator.getNextIDForContext();
                while (defs.contains(ctxId)) {
                    ctxId = InkMLIDGenerator.getNextIDForContext();
                }
                context.setId(ctxId);
            }
            defs.addToDirectChildrenMap(context);
            this.inkMLProcessor.notifyContextChanged(context, ctxChanges);
            ink.setCurrentContext(context);
        } else if (tagName.equals("trace")) {
            final Trace trace = this.getTrace(element);
            ink.addTrace(trace);
            this.inkMLProcessor.notifyTraceReceived(trace);
            if (!"".equals(trace.getId())) {
                ink.getDefinitions().addToIndirectChildrenMap(trace);
            }
        } else if (tagName.equals("traceGroup")) {
            final TraceGroup traceGroup = this.getTraceGroup(element);
            traceGroup.resolveAssociatedContext(ink.getCurrentContext());
            ink.addTraceGroup(traceGroup);
            this.inkMLProcessor.notifyTraceGroupReceived(traceGroup);

            // store traceData having 'id' attibute to IndirectChildren
            if (!"".equals(traceGroup.getId())) {
                ink.getDefinitions().addToIndirectChildrenMap(traceGroup);
            }
            final ArrayList<TraceDataElement> children = traceGroup.getTraceDataList();
            if (null != children) {
                for (int i = 0; i < children.size(); i++) {
                    final TraceDataElement child = children.get(i);
                    if (!"".equals(child.getId())) {
                        ink.getDefinitions().addToIndirectChildrenMap(child);
                    }
                }
            }
        } else if (tagName.equals("traceView")) {
            final TraceView traceView = this.getTraceView(element, ink.getDefinitions());
            traceView.setAssociatedContext(ink.getCurrentContext());
            ink.addTraceView(traceView);
            this.inkMLProcessor.notifyTraceViewReceived(traceView);
            if (!"".equals(traceView.getId())) {
                ink.getDefinitions().addToDirectChildrenMap(traceView);
            }
        } else if (tagName.equals("annotation")) {
            final Annotation annotation = this.getAnnotation(element);
            ink.addAnnotation(annotation);
        } else if (tagName.equals("annotationXML")) {
            final AnnotationXML aXml = this.getAnnotationXML(element);
            ink.addAnnotationXML(aXml);
        } else {
            throw new InkMLException("The Element " + tagName + " is not a valid Child Element for InkML <Ink> Element");
        }
    }

    /**
     * Method to bind AnnotationXML element
     * 
     * @param element the AnnotationXML element
     * @return AnnotationXML data object
     * @throws InkMLException
     */
    protected AnnotationXML getAnnotationXML(final Element element) throws InkMLException {
        final AnnotationXML aXml = new AnnotationXML();
        final NamedNodeMap attributesMap = element.getAttributes();
        final int length = attributesMap.getLength();
        for (int index = 0; index < length; index++) {
            final Attr attribute = (Attr) attributesMap.item(index);
            final String attributeName = attribute.getName();
            if ("type".equals(attributeName)) {
                aXml.setType(attribute.getValue());
            } else if ("encoding".equals(attributeName)) {
                aXml.setEncoding(attribute.getValue());
            } else {
                aXml.addToOtherAttributesMap(attributeName, attribute.getValue());
            }
        }
        InkMLDOMParser.LOG.finest("annotationXML received: " + element.toString());
        final NodeList list = element.getChildNodes();
        final int nChildren = list.getLength();
        if (nChildren > 0) {
            for (int i = 0; i < nChildren; i++) {
                final Node node = list.item(i);
                if (!(node instanceof Element)) {
                    continue;
                }
                final Element childElement = (Element) node;
                // get the tagName to use as Key in the valueMap
                final String tagName = childElement.getLocalName();
                // String key = this.parentXPath+"/"+tagName;
                final String value = childElement.getFirstChild().getNodeValue();
                // propertyElementsMap.put(key, childElement);
                // propertyElementsMap.put(key, value);
                aXml.addToPropertyElementsMap(tagName, value);
                InkMLDOMParser.LOG.finer("The property with name = " + tagName + " is added to the propertyElementsMap.");
            }
        }
        return aXml;
    }

    /**
     * Method to bind Annotation element
     * 
     * @param element the Annotation element
     * @return Annotation data object
     * @throws InkMLException
     */
    protected Annotation getAnnotation(final Element element) throws InkMLException {
        final Annotation annotation = new Annotation();

        final NamedNodeMap attributesMap = element.getAttributes();
        final int length = attributesMap.getLength();
        for (int index = 0; index < length; index++) {
            final Attr attribute = (Attr) attributesMap.item(index);
            final String attributeName = attribute.getName();
            if ("type".equals(attributeName)) {
                annotation.setType(attribute.getValue());
            } else if ("encoding".equals(attributeName)) {
                annotation.setEncoding(attribute.getValue());
            } else {
                annotation.addToOtherAttributesMap(attributeName, attribute.getValue());
            }
        }
        final Node valueNode = element.getFirstChild();
        if (null != valueNode) {
            annotation.setAnnotationTextValue(valueNode.getNodeValue());
        }
        return annotation;
    }

    /**
     * Method to bind TraceView element
     * 
     * @param element the TraceView element
     * @param definitions the definitions data object to resolve the reference attributes
     * @return TraceView data object
     * @throws InkMLException
     */
    protected TraceView getTraceView(final Element element, final Definitions definitions) throws InkMLException {
        final TraceView traceView = new TraceView();
        final String id = element.getAttribute("id");
        if (!"".equals(id)) {
            traceView.setId(id);
        }
        final String contextRef = element.getAttribute("contextRef");
        if (!"".equals(contextRef)) {
            traceView.setContextRef(contextRef);
        }
        traceView.resolveContext(definitions);
        final String traceDataRef = element.getAttribute("traceDataRef");
        if (!"".equals(traceDataRef)) {
            traceView.setTraceDataRef(traceDataRef);
            traceView.setFromAttribute(element.getAttribute("from"));
            traceView.setToAttribute(element.getAttribute("to"));
            traceView.setSelectedTree(definitions);
        } else {
            // <traceView> does not have 'traceDataRef' attribute; then it is used to -
            // group child <traceView> elements.
            final NodeList list = element.getElementsByTagName("traceView");
            final int nData = list.getLength();
            if (0 != nData) {
                traceView.initChildViewList();
                for (int i = 0; i < nData; i++) {
                    final Element traceViewElement = (Element) list.item(i);
                    final TraceView childTV = this.getTraceView(traceViewElement, definitions);
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
     * 
     * @param element the TraceGroup element
     * @return TraceGroup data object
     * @throws InkMLException
     */
    protected TraceGroup getTraceGroup(final Element element) throws InkMLException {
        final TraceGroup traceGroup = new TraceGroup();

        // bind attribute
        final String id = element.getAttribute("id");
        final String contextref = element.getAttribute("contextRef");
        final String brushRef = element.getAttribute("brushRef");
        if (!"".equals(id)) {
            traceGroup.setId(id);
        }
        if (!"".equals(contextref)) {
            traceGroup.setContextRef(contextref);
        }
        if (!"".equals(brushRef)) {
            traceGroup.setBrushRef(brushRef);
        }

        // bind child element
        final NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            final Node node = list.item(i);
            if (!(node instanceof Element)) {
                continue;
            }
            final Element child = (Element) node;
            final String tagName = child.getLocalName();
            if ("trace".equals(tagName)) {
                final Trace trace = this.getTrace(child);
                trace.setParentTraceGroup(traceGroup);
                traceGroup.addToTraceData(trace);
            } else if ("traceGroup".equals(tagName)) {
                final TraceGroup childTG = this.getTraceGroup(child);
                childTG.setParentTraceGroup(traceGroup);
                traceGroup.addToTraceData(childTG);
            }
        }
        return traceGroup;
    }

    /**
     * Method to bind Trace element
     * 
     * @param element the Trace element
     * @return Trace data object
     * @throws InkMLException
     */
    protected Trace getTrace(final Element element) throws InkMLException {
        final Trace trace = new Trace();
        // set value of the object from the value of the DOM element
        // Extract and set Attribute values
        final NamedNodeMap attrMap = element.getAttributes();
        final int length = attrMap.getLength();
        for (int i = 0; i < length; i++) {
            final Node attr = attrMap.item(i);
            trace.setAttribute(attr.getLocalName(), attr.getNodeValue());
        }
        // get trace data
        String traceText = "";
        final NodeList nl = element.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeType() == Node.TEXT_NODE) {
                traceText += nl.item(i).getNodeValue();
            }
        }
        final Ink ink = this.inkMLProcessor.getInk();
        final Context currCtx = ink.getCurrentContext();
        final Definitions defs = ink.getDefinitions();
        trace.resolveAssociatedContext(currCtx, defs);
        trace.processTraceElement(traceText, currCtx, defs);

        return trace;
    }

    /**
     * Method to bind the children of Definitions element
     * 
     * @param elements the Definitions child element
     * @param definitions the definitions data object to resolve the reference attributes
     * @throws InkMLException
     */
    protected void addToDefinitions(final NodeList elements, final Definitions definitions) throws InkMLException {
        for (int i = 0; i < elements.getLength(); i++) {
            final Node node = elements.item(i);
            if (!(node instanceof Element)) {
                continue;
            }
            final Element element = (Element) node;
            final String tagName = element.getLocalName();
            final String id = element.getAttribute("id");
            if ("".equals(id)) {
                throw new InkMLException("Elements within a <definitions> block must have an 'id' attribute. A " + tagName + " element do not have 'id' attribute.");
            }
            InkElement inkElement = null;
            // when the object of child elements created, the elements having a non empty id are added to definitions.
            // This is done in the constructors of all the child elements of Definitions
            if ("brush".equalsIgnoreCase(tagName)) {
                inkElement = this.getBrush(element);
            } else if ("traceFormat".equalsIgnoreCase(tagName)) {
                inkElement = this.getTraceFormat(element, definitions);
            } else if ("context".equalsIgnoreCase(tagName)) {
                final Context context = this.getContext(element, definitions);
                // resolve implicit references for contextual elements to their counterpart in the "DefaultContext"
                context.resolveImplicitReferenceWithDefaultContext();
                inkElement = context;
            } else if ("inkSource".equalsIgnoreCase(tagName)) {
                inkElement = this.getInkSource(element, definitions);
            } else if ("trace".equalsIgnoreCase(tagName)) {
                inkElement = this.getTrace(element);
            } else if ("traceGroup".equalsIgnoreCase(tagName)) {
                inkElement = this.getTraceGroup(element);
            } else if ("traceView".equalsIgnoreCase(tagName)) {
                inkElement = this.getTraceView(element, definitions);
            } else if ("canvas".equalsIgnoreCase(tagName)) {
                inkElement = this.getCanvas(element, definitions);
            } else if ("canvasTransform".equalsIgnoreCase(tagName)) {
                inkElement = this.getCanvasTransform(element);
            } else if ("timestamp".equalsIgnoreCase(tagName)) {
                inkElement = this.getTimestamp(element);
            } else if ("mapping".equalsIgnoreCase(tagName)) {
                inkElement = this.getMapping(element);
            } else {
                throw new InkMLException("Parse Error: The element " + tagName + "should not be a child to <definitions> element.\n");
            }
            definitions.addToDirectChildrenMap(inkElement);
        }
    }

    /**
     * Method to bind Mapping element
     * 
     * @param element the Mapping element
     * @return Mapping data object
     * @throws InkMLException
     */
    protected Mapping getMapping(final Element element) throws InkMLException {
        final Mapping mapping = new Mapping();
        // Extract and set Attribute values
        final String type = element.getAttribute("type");
        InkMLDOMParser.LOG.info("mapping type=" + type);
        if (!"identity".equalsIgnoreCase(type) && !"unknown".equalsIgnoreCase(type)) {
            throw new InkMLException("Feature not implemented: 'Mapping' with 'type' attribute other than identity and unknown");
        }
        mapping.setType(type);
        final String id = element.getAttribute("id");
        if (!id.equals("")) {
            mapping.setId(id);
        }
        return mapping;
    }

    /**
     * Method to bind Timestamp element
     * 
     * @param element the Timestamp element
     * @return Timestamp data object
     * @throws InkMLException
     */
    protected Timestamp getTimestamp(final Element element) throws InkMLException {
        throw new InkMLException("Feature not implemented. Mapping of type other than identity or unknown");
    }

    /**
     * Method to bind CanvasTransform element
     * 
     * @param element the CanvasTransform element
     * @return CanvasTransform data object
     * @throws InkMLException
     */
    protected CanvasTransform getCanvasTransform(final Element element) throws InkMLException {
        final CanvasTransform canvasTransform = new CanvasTransform();
        // Extract and set Attribute values
        final NamedNodeMap attrMap = element.getAttributes();
        final int length = attrMap.getLength();
        for (int i = 0; i < length; i++) {
            final Node attr = attrMap.item(i);
            canvasTransform.setAttribute(attr.getLocalName(), attr.getNodeValue());
        }

        final NodeList list = element.getElementsByTagName("mapping");
        final int nMappingChildren = list.getLength();
        if (nMappingChildren == 2) {
            canvasTransform.setForwardMapping(this.getMapping((Element) list.item(0)));
            canvasTransform.setReverseMapping(this.getMapping((Element) list.item(1)));
        } else if (nMappingChildren == 1) {
            canvasTransform.setForwardMapping(this.getMapping((Element) list.item(0)));
        }
        return canvasTransform;
    }

    /**
     * Method to bind Canvas element
     * 
     * @param element the Canvas element
     * @param definitions the definitions data object to resolve the reference attributes
     * @return Canvas data object
     * @throws InkMLException
     */
    protected Canvas getCanvas(final Element element, final Definitions definitions) throws InkMLException {
        final Canvas canvas = new Canvas();
        // Extract and set Attribute values
        final NamedNodeMap attrMap = element.getAttributes();
        final int length = attrMap.getLength();
        for (int i = 0; i < length; i++) {
            final Node attr = attrMap.item(i);
            canvas.setAttribute(attr.getLocalName(), attr.getNodeValue());
        }

        final NodeList list = element.getElementsByTagName("traceFormat");
        if (list.getLength() != 0) {
            canvas.setTraceFormat(this.getTraceFormat((Element) list.item(0), definitions));
        }

        return canvas;
    }

    /**
     * Method to bind InkSource element
     * 
     * @param element the InkSource element
     * @param definitions the definitions data object to resolve the reference attributes
     * @return InkSource data object
     * @throws InkMLException
     */
    protected InkSource getInkSource(final Element element, final Definitions definitions) throws InkMLException {
        final InkSource inkSrc = new InkSource();
        // Extract and set Attribute values
        final NamedNodeMap attrMap = element.getAttributes();
        final int length = attrMap.getLength();
        for (int i = 0; i < length; i++) {
            final Node attr = attrMap.item(i);
            inkSrc.setAttribute(attr.getLocalName(), attr.getNodeValue());
        }

        NodeList list = element.getElementsByTagName("traceFormat");
        if (list.getLength() != 0) {
            inkSrc.setTraceFormat(this.getTraceFormat((Element) list.item(0), definitions));
        }

        list = element.getElementsByTagName("sampleRate");
        if (list.getLength() != 0) {
            inkSrc.setSampleRate(this.getSampleRate((Element) list.item(0), inkSrc));
        }
        list = element.getElementsByTagName("latency");
        if (list.getLength() != 0) {
            inkSrc.setLatency(this.getLatency((Element) list.item(0), inkSrc));
        }
        list = element.getElementsByTagName("activeArea");
        if (list.getLength() != 0) {
            inkSrc.setActiveArea(this.getActiveArea((Element) list.item(0), inkSrc));
        }
        list = element.getElementsByTagName("srcProperty");
        for (int i = 0; i < list.getLength(); i++) {
            inkSrc.addSourceProperty(this.getSourceProperty((Element) list.item(i), inkSrc));
        }
        list = element.getElementsByTagName("channelProperties");
        if (list.getLength() != 0) {
            inkSrc.setChannelProperties(this.getChannelProperties((Element) list.item(0), inkSrc));
        }
        return inkSrc;
    }

    /**
     * Method to bind InkSource.Latency element
     * 
     * @param element the InkSource.Latency element
     * @param inkSrc the enclosing InkSource data object
     * @return InkSource.Latency data object
     * @throws InkMLException
     */
    protected InkSource.Latency getLatency(final Element element, final InkSource inkSrc) throws InkMLException {
        final String value = element.getAttribute("value");
        if ("".equals(value)) {
            return inkSrc.new Latency(new Double(value).doubleValue());
        }
        return null;
    }

    /**
     * Method to bind InkSource.ActiveArea element
     * 
     * @param element the InkSource.ActiveArea element
     * @param inkSrc the enclosing InkSource data object
     * @return InkSource.ActiveArea data object
     * @throws InkMLException
     */
    protected InkSource.ActiveArea getActiveArea(final Element element, final InkSource inkSrc) throws InkMLException {
        final InkSource.ActiveArea activeArea = inkSrc.new ActiveArea();
        final NamedNodeMap attrMap = element.getAttributes();
        final int length = attrMap.getLength();
        for (int i = 0; i < length; i++) {
            final Node attr = attrMap.item(i);
            final String attrName = attr.getLocalName();
            if ("size".equals(attrName)) {
                activeArea.setSize(attr.getNodeValue());
            }
            if ("height".equals(attrName)) {
                activeArea.setHegiht(new Double(attr.getNodeValue()).doubleValue());
            }
            if ("width".equals(attrName)) {
                activeArea.setWidth(new Double(attr.getNodeValue()).doubleValue());
            }
            if ("units".equals(attrName)) {
                activeArea.setUnits(attr.getNodeValue());
            }
        }
        return activeArea;
    }

    /**
     * Method to bind InkSource.SampleRate element
     * 
     * @param element the InkSource.SampleRate element
     * @param inkSrc the enclosing InkSource data object
     * @return InkSource.SampleRate data object
     * @throws InkMLException
     */
    protected InkSource.SampleRate getSampleRate(final Element element, final InkSource inkSrc) throws InkMLException {
        final String isUniform = element.getAttribute("uniform");
        final String value = element.getAttribute("value");
        InkSource.SampleRate sampleRate;
        if ("".equals(isUniform)) {
            sampleRate = inkSrc.new SampleRate(new Double(value).doubleValue());
        } else {
            sampleRate = inkSrc.new SampleRate(new Double(value).doubleValue(), new Boolean(isUniform).booleanValue());
        }
        return sampleRate;
    }

    /**
     * Method to bind InkSource.ChannelProperties element
     * 
     * @param element the InkSource.ChannelProperties element
     * @param inkSrc the enclosing InkSource data object
     * @return InkSource.ChannelProperties data object
     * @throws InkMLException
     */
    protected InkSource.ChannelProperties getChannelProperties(final Element element, final InkSource inkSrc) throws InkMLException {
        final NodeList list = element.getElementsByTagName("channelProperty");
        final int nChnProperty = list.getLength();
        if (0 != nChnProperty) {
            final InkSource.ChannelProperties chnProps = inkSrc.new ChannelProperties();
            InkSource.ChannelProperties.ChannelProperty chnProp;
            for (int i = 0; i < nChnProperty; i++) {
                chnProp = this.getChannelProperty((Element) list.item(i), chnProps);
                chnProps.addChannelProperty(chnProp);
            }
            return chnProps;
        }
        return null;
    }

    /**
     * Method to bind InkSource.ChannelProperties.ChannelProperty element
     * 
     * @param element the InkSource.ChannelProperties.ChannelProperty element
     * @param chnProps the enclosing InkSource.ChannelProperties data object
     * @return InkSource.ChannelProperties.ChannelProperty data object
     * @throws InkMLException
     */
    protected InkSource.ChannelProperties.ChannelProperty getChannelProperty(final Element element, final InkSource.ChannelProperties chnProps) throws InkMLException {
        InkSource.ChannelProperties.ChannelProperty chnProp = null;
        final String channel = element.getAttribute("channel");
        final String name = element.getAttribute("name");
        final String value = element.getAttribute("value");
        final String units = element.getAttribute("units");
        if ("".equals(units)) {
            chnProp = chnProps.new ChannelProperty(channel, name, new Double(value).doubleValue());
        } else {
            chnProp = chnProps.new ChannelProperty(channel, name, new Double(value).doubleValue(), units);
        }
        return chnProp;
    }

    /**
     * Method to bind InkSource.SourceProperty element
     * 
     * @param element the InkSource.SourceProperty element
     * @param inkSrc the enclosing InkSource data object
     * @return InkSource.SourceProperty data object
     * @throws InkMLException
     */
    protected InkSource.SourceProperty getSourceProperty(final Element element, final InkSource inkSrc) throws InkMLException {
        final String name = element.getAttribute("name");
        final String value = element.getAttribute("value");
        final String units = element.getAttribute("units");
        InkSource.SourceProperty srcProperty;
        if ("".equals(units)) {
            srcProperty = inkSrc.new SourceProperty(name, new Double(value).doubleValue());
        } else {
            srcProperty = inkSrc.new SourceProperty(name, new Double(value).doubleValue(), units);
        }
        return srcProperty;
    }

    /**
     * Method to bind Context element
     * 
     * @param element the Context element
     * @param definitions the definitions data object to resolve the reference attributes
     * @return Context data object
     * @throws InkMLException
     */
    protected Context getContext(final Element element, final Definitions definitions) throws InkMLException {
        final Context context = new Context();
        // set value of the object from the value of the DOM element
        // Extract and set Attribute values
        final NamedNodeMap attrMap = element.getAttributes();
        final int length = attrMap.getLength();
        for (int i = 0; i < length; i++) {
            final Node attr = attrMap.item(i);
            context.setAttribute(attr.getLocalName(), attr.getNodeValue());
        }
        final NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            final Node node = list.item(i);
            if (!(node instanceof Element)) {
                continue;
            }
            final Element child = (Element) node;
            this.addToContextChildrenList(child, context, definitions);
        }
        return context;
    }

    /**
     * Method to bind the children of context element
     * 
     * @param ctxChild child element of context element
     * @param context context data object
     * @param definitions the definitions data object to resolve the reference attributes
     * @throws InkMLException
     */
    protected void addToContextChildrenList(final Element ctxChild, final Context context, final Definitions definitions) throws InkMLException {
        final String tagName = ctxChild.getLocalName();

        // Content of <context> is ( brush | traceFormat | )
        if (tagName.equals("brush")) {
            final Brush brush = this.getBrush(ctxChild);
            context.addToContextElementList(brush);
        } else if (tagName.equals("traceFormat")) {
            final TraceFormat tf = this.getTraceFormat(ctxChild, definitions);
            context.addToContextElementList(tf);
        } else if (tagName.equals("canvas")) {
            final Canvas canvas = this.getCanvas(ctxChild, definitions);
            context.addToContextElementList(canvas);
        } else if (tagName.equals("canvasTransform")) {
            final CanvasTransform canvasTransform = this.getCanvasTransform(ctxChild);
            context.addToContextElementList(canvasTransform);
        } else if (tagName.equals("inkSource")) {
            final InkSource inkSrc = this.getInkSource(ctxChild, definitions);
            context.addToContextElementList(inkSrc);
        } else if (tagName.equals("timeStamp")) {
            final Timestamp timeStamp = this.getTimestamp(ctxChild);
            context.addToContextElementList(timeStamp);
        }
    }

    /**
     * Method to bind brush element
     * 
     * @param element brush element
     * @return brush data object
     * @throws InkMLException
     */
    protected Brush getBrush(final Element element) throws InkMLException {
        final String id = element.getAttribute("id");
        final Brush brush = new Brush(id);
        final String brushRef = element.getAttribute("brushRef");
        if (!"".equals(brushRef)) {
            brush.setBrushRef(brushRef);
        }

        // Process the child <annotation>, <annotationXML> elements
        final NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            final Node node = list.item(i);
            if (!(node instanceof Element)) {
                continue;
            }
            final Element childElement = (Element) node;
            final String tagName = childElement.getLocalName();
            if ("annotationXML".equalsIgnoreCase(tagName)) {
                final AnnotationXML aXml = this.getAnnotationXML(childElement);
                brush.setAnnotationXML(aXml);
            } else if ("annotation".equalsIgnoreCase(tagName)) {
                final Annotation annotation = this.getAnnotation(childElement);
                brush.setAnnotation(annotation);
            }
        }
        return brush;
    }

    /**
     * Method to bind TraceFormat
     * 
     * @param element TraceFormat element
     * @param definitions the definitions data object to resolve the reference attributes
     * @return TraceFormat data object
     * @throws InkMLException
     */
    protected TraceFormat getTraceFormat(final Element element, final Definitions definitions) throws InkMLException {
        final TraceFormat traceFormat = new TraceFormat();
        final String id = element.getAttribute("id");
        if (!"".equals(id)) {
            traceFormat.setId(element.getAttribute("id"));
            definitions.addToIndirectChildrenMap(traceFormat);
        }
        final String href = element.getAttribute("href");
        if (!"".equals(href)) {
            final TraceFormat refferedTF = definitions.getTraceFormatRefElement(href);
            traceFormat.setHref(href);
            traceFormat.setChannelList(refferedTF.getChannelList());
        }
        final NodeList list = element.getElementsByTagName("channel");
        final int nChannel = list.getLength();

        /*
         * if(0 == nChannel) { // if no channel available in the traceFormat, copy the default channels if(traceFormat.channelMap.size()==0){ // add the channel
         * for default TraceFormat Channel xChannel = new Channel("X",Channel.ChannelType.DECIMAL); Channel yChannel = new
         * Channel("Y",Channel.ChannelType.DECIMAL); traceFormat.addChannel(xChannel); traceFormat.addChannel(yChannel); } }
         */
        for (int i = 0; i < nChannel; i++) {
            final Element channelElmnt = (Element) list.item(i);
            final Channel ch = this.getChannel(channelElmnt);
            traceFormat.addChannel(ch);
        }
        return traceFormat;
    }

    /**
     * Method to bind Channel element
     * 
     * @param channelElement Channel element
     * @return Channel data object
     * @throws InkMLException
     */
    protected Channel getChannel(final Element channelElement) throws InkMLException {
        Channel channel = null;
        final String chnName = channelElement.getAttribute("name");
        if ("".equals(chnName)) {
            throw new InkMLException("Channel element must have value for 'name' attribute");
        } else {
            channel = new Channel(chnName);
        }
        // checking for intermittend channel
        if ("intermittentChannels".equalsIgnoreCase(channelElement.getParentNode().getLocalName())) {
            channel.setIntermittent(true);
        }

        // Extract and set Attribute values
        final NamedNodeMap attrMap = channelElement.getAttributes();
        final int length = attrMap.getLength();
        for (int i = 0; i < length; i++) {
            final Node attr = attrMap.item(i);
            channel.setAttribute(attr.getLocalName(), attr.getNodeValue());
        }
        return channel;
    }

    /** Factory pour les schemas */
    private final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); // NOPMD - not transient

    /**
     * Method to parse the InkML file identified by the inkmlFilename given in the parameter and creates data objects. It performs validation with schema. It
     * must have "ink" as root element with InkML name space specified with xmlns="http://www.w3.org/2003/InkML". The schema location may be specified. If it is
     * not specified or if relative path of the InkML.xsd file is specified, then the InkML.xsd file path must be added to * CLASSPATH for the parser to locate
     * it. An example of a typical header is, <ink xmlns="http://www.w3.org/2003/InkML" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     * xsi:schemaLocation="http://www.w3.org/2003/InkML C:\project\schema\inkml.xsd">
     * 
     * @param inkmlFileName
     * @throws InkMLException
     */
    @Override
    public void parseInkMLFile(final String inkmlFileName) throws InkMLException {

        // Get the DOM document object by parsing the InkML input file
        FileInputStream inputStream = null; // NOPMD - init
        try {
            inputStream = new FileInputStream(inkmlFileName);
            this.parseInkMLFile(inputStream);
        } catch (final IOException e) {
            throw new InkMLException("I/O error while parsing Input InkML XML file.\n Message: " + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Method to parse the InkML file identified by the inkmlFilename given in the parameter and creates data objects. It performs validation with schema. It
     * must have "ink" as root element with InkML name space specified with xmlns="http://www.w3.org/2003/InkML". The schema location may be specified. If it is
     * not specified or if relative path of the InkML.xsd file is specified, then the InkML.xsd file path must be added to * CLASSPATH for the parser to locate
     * it. An example of a typical header is, <ink xmlns="http://www.w3.org/2003/InkML" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     * xsi:schemaLocation="http://www.w3.org/2003/InkML C:\project\schema\inkml.xsd">
     * 
     * @param inkmlFileName
     * @throws InkMLException
     */
    @Override
    public void parseInkMLFile(final InputStream inputStream) throws InkMLException {

        // Get the DOM document object by parsing the InkML input file
        try {
            final InputSource input = new InputSource(inputStream);

            // get the DOM document object of the input InkML XML file.
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setIgnoringComments(true);
            dbFactory.setNamespaceAware(true);
            dbFactory.setIgnoringElementContentWhitespace(true);
            if (this.isSchemaValidationEnabled()) {
                InkMLDOMParser.LOG.info("Validation using schema is enabled.");
                dbFactory.setSchema(this.factory.newSchema(this.getClass().getResource("/schema/inkml.xsd")));
                dbFactory.setValidating(true);
            } else {
                InkMLDOMParser.LOG.info("Validation using schema is disabled.");
                dbFactory.setValidating(false);
            }
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            dBuilder.setErrorHandler(this);
            this.inkmlDOMDocument = dBuilder.parse(input);
            InkMLDOMParser.LOG.info("\nInput InkML XML file parsing is completed.\n");
            this.inkMLProcessor.beginInkSession();
            this.bindData(this.inkMLProcessor.getInk());
        } catch (final ParserConfigurationException e) {
            throw new InkMLException("Error in parsing Input InkML XML file.\n Message: " + e.getMessage(), e);
        } catch (final SAXException e) {
            throw new InkMLException("Error in parsing Input InkML XML file.\n Message: " + e.getMessage(), e);
        } catch (final IOException e) {
            throw new InkMLException("I/O error while parsing Input InkML XML file.\n Message: " + e.getMessage(), e);
        }
    }

    /**
     * @return
     */
    private boolean isSchemaValidationEnabled() {
        // check the JVM parameter java -DschemaValidation={"true"|"false"}
        boolean isSchemaValidationEnabled = false;
        final String validationStatus = StringUtils.trimToEmpty(System.getProperty("com.hp.hpl.inkml.SchemaValidation"));
        if (StringUtils.isNotEmpty(validationStatus)) {
            if ("true".equalsIgnoreCase(validationStatus)) {
                isSchemaValidationEnabled = true;
            } else {
                isSchemaValidationEnabled = false;
            }
        }
        return isSchemaValidationEnabled;
    }

    /**
     * Method to parse the InkML string markup data identified by the inkmlStr given
     * 
     * @param inkmlStr String markup data
     * @throws InkMLException
     */
    @Override
    public void parseInkMLString(final String inkmlStrParam) throws InkMLException {
        final String inkmlStr;
        if (inkmlStrParam.indexOf("ink") == -1) {
            // the given welformed inkmlStr does not contain complete <ink> document as string.
            // wrap it with a false root element, <inkMLFramgment>. It is called as fragment.
            inkmlStr = "<inkMLFramgment>" + inkmlStrParam + " </inkMLFramgment>";
        } else {
            inkmlStr = inkmlStrParam;
        }
        InkMLDOMParser.LOG.fine(inkmlStr);

        try {
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setIgnoringComments(true);
            dbFactory.setIgnoringElementContentWhitespace(true);
            dbFactory.setValidating(false);
            InkMLDOMParser.LOG.info("Validation using schema is disabled.");
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            this.inkmlDOMDocument = dBuilder.parse(inkmlStr);
            this.bindData(this.inkMLProcessor.getInk());
        } catch (final ParserConfigurationException e) {
            throw new InkMLException("Error in parsing Input InkML XML file.\n Message: " + e.getMessage(), e);
        } catch (final SAXException e) {
            throw new InkMLException("Error in parsing Input InkML XML file.\n Message: " + e.getMessage(), e);
        } catch (final IOException e) {
            throw new InkMLException("I/O error while parsing Input InkML XML file.\n Message: " + e.getMessage(), e);
        }
    }

    /** Prints the error message. */
    private void printError(final String messageType, final SAXParseException exception) {

        System.err.print("[");
        System.err.print(messageType);
        System.err.print("] ");
        String systemId = exception.getSystemId();
        if (systemId != null) {
            final int index = systemId.lastIndexOf('/');
            if (index != -1) {
                systemId = systemId.substring(index + 1);
            }
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
    @Override
    public void error(final SAXParseException exception) throws SAXException {
        this.printError("Error", exception);
        System.exit(-1);
    }

    /**
     * Method to display XML Parse and schema validation fatalError messages
     */
    @Override
    public void fatalError(final SAXParseException exception) throws SAXException {
        this.printError("FatalError", exception);
        System.exit(-1);
    }

    /**
     * Method to display XML Parse and schema validation warning messages
     */
    @Override
    public void warning(final SAXParseException exception) throws SAXException {
        this.printError("Warning", exception);
    }
}
