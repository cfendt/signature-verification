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
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * This class models the <definitions> InkML Element. It takes a parsed -
 * Definitions DOM Element and construct the InkML Definitions object.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 * Creation date : 8 May 2007
 */

public class Definitions {
	private static Logger logger =
		 Logger.getLogger(Definitions.class.getName());

	// A map with Element "ID" as the Key and the InkML Element object as the value.
	// It contains the elements wrapped within a <definintions> element. But it also
	// contains <context> elements (with/without 'id' attribute) that present -
	// outside <definitions> element.
	private HashMap<String,InkElement> directChildrenMap;

	// This map containd element with 'id' attribute that present outside <definitions>
	// element.
	private HashMap<String,InkElement> indirectChildrenMap;

	/**
	 * No argument Constructor that creates an Definitions object with empty definitionsMap.
	 *
	 */
	public Definitions() {
		directChildrenMap = new HashMap<String,InkElement>();
		indirectChildrenMap = new HashMap<String,InkElement>();

		// Add the DefaultContext elements to the definitions
		InkElement[] defaultContextObjects = new InkElement[7];
		defaultContextObjects[0] = Canvas.getDefaultCanvas();
		defaultContextObjects[1] = CanvasTransform.getDefaultCanvasTransform();
		defaultContextObjects[2] = TraceFormat.getDefaultTraceFormat();
		defaultContextObjects[3] = InkSource.getDefaultInkSource();
		defaultContextObjects[4] = Brush.getDefaultBrush();
		defaultContextObjects[5] = Timestamp.getDefaultTimestamp();
		defaultContextObjects[6] = Context.getDefaultContext();
		addToIndirectChildrenMap(defaultContextObjects);
	}

	/**
	 * Method to add elements with an 'id' and also wrapped by a {@code <definitions>} element.
	 * @param inkElement to be added to definitions state
	 * @return the id of the inkElement added. If the element already exist, then that id will be sent.
	 */
	public String addToDirectChildrenMap(InkElement inkElement) {
		String id="";
		try{
			id = inkElement.getId();

		if(! "".equals(id)) {
			if(directChildrenMap.containsKey(id)){
				logger.warning("The Ink Element already exist in the definitions."+
						"Ignoring to save it again in definitions.\nElement ID: "+ id);
				return id;
			}
			directChildrenMap.put(id, inkElement);
		}  else {
			logger.warning("The Ink Element does not have value for id; It is not added to definitions.\nElement: "+ inkElement);
		}
		}catch(NullPointerException nullExp) {
			logger.warning("addToDirectChildrenMap: the InkElement in the argument is null.");
		}
		return id;
	}
	/**
	 * Method to add elements as the have 'id' attribute defined may potentially be referred in future using the 'id',
	 * and are NOT wrapped by a {@code <definitions>} element.
	 * @param inkElement to be added to definitions state
	 * @return the 'id' attribute of the added element, if that element preesist in definitions then that old id is returned.
	 */
	public String addToIndirectChildrenMap(InkElement inkElement) {
		String id = inkElement.getId();
		if(! "".equals(id)) {
			if(indirectChildrenMap.containsKey(id))
				return id;
			indirectChildrenMap.put(id, inkElement);
		}
		return id;
	}

	/**
	 * Method to add elements with an 'id' and also wrapped by a {@code <definitions>} element.
	 * @param inkElements array to be added to definitions state
	 */
	public void addToDirectChildrenMap(InkElement[] inkElements) {
		for(int index=0; index<inkElements.length; index++) {
			String id = inkElements[index].getId();
			if(! "".equals(id)) {
				if(directChildrenMap.containsKey(id)) {
					logger.warning("The Ink Element already exist in the definitions."+
							"Ignoring to save it again in definitions.\nElement ID: "+ id);
					return;
				}
				directChildrenMap.put(id, inkElements[index]);
			}  else {
				logger.warning("The Ink Element does not have value for id; It will be ignored.\n"+ inkElements[index]);
			}
		}
	}

	/**
	 * Method to add elements as the have 'id' attribute defined may potentially be referred in future using the 'id',
	 * and are NOT wrapped by a {@code <definitions>} element.
	 * @param inkElements array to be added to definitions state
	 */
	public void addToIndirectChildrenMap(InkElement[] inkElements) {
		for(int index=0; index<inkElements.length; index++) {
			String id = inkElements[index].getId();
			if(! "".equals(id)) {
				if(indirectChildrenMap.containsKey(id)) {
					logger.warning("The Ink Element already exist in the definitions."+
							"Ignoring to save it again in definitions.\nElement ID: "+ id);
					return;
				}
				indirectChildrenMap.put(id, inkElements[index]);
			}  else {
				logger.warning("The Ink Element does not have value for id; It will be ignored.\n"+ inkElements[index]);
			}
		}
	}

	// This method is used to retrieve the InkElement object that referred by
	// any 'Ref' attribute such as brushRef, contextRef and etc.
	private InkElement getReferredInkElement(String refAttribute) throws InkMLException{
		InkElement inkElement = null;
		StringTokenizer st = new StringTokenizer(refAttribute, "#");
		if(0 == st.countTokens() || 3 <= st.countTokens()  ) {
			throw new InkMLException("\nError: Invalid attribute value given, "+refAttribute+
												 "\nUsage: [url]#elementId\n");
		} else if(2 == st.countTokens()) {
			// To Do: Yet to implement reference outside the current Ink documents
			throw new InkMLException("Feature not supported. Yet to implement reference outside the current Ink documents.");
		} else {
			String key = st.nextToken();
			inkElement = directChildrenMap.get(key);
			if(null == inkElement)
				inkElement = indirectChildrenMap.get(key);
			if(null == inkElement){
				throw new InkMLException("\nError: There is no element exist with the given id, "+key);
			}
		}
		return inkElement;
	}

	/**
	 * This method retrieve the Context object stored in the definitionMap with
	 * key equal to the contextRef given in the parameter.
	 * @param contextRef  The Key of the Context object to be retrived from definitionsMap.
	 * @return The Context object that is referred by the "contextRef" attribute.
	 * @throws InkMLException
	 */
	public Context getContextRefElement(String contextRef) throws InkMLException{
		InkElement inkElement = getReferredInkElement(contextRef);
		if(! "Context".equals(inkElement.getInkElementType())) {
			throw new InkMLException("The given Reference attribute value, "+contextRef+
													"is not the 'id' of a Context Element");
		}
		return new Context((Context) inkElement);
	}

	/**
	 * This method retrieve the Brush object stored in the definitionMap with
	 * key equal to the brushRef given in the parameter.
	 * @param brushRef The Key of the InkElement to be retrived from definitionsMap.
	 * @return The Brush object that is referred by the "brushRef" attribute.
	 * @throws InkMLException
	 */
	public Brush getBrushRefElement(String brushRef) throws InkMLException{
		Brush brush = null;
		InkElement inkElement = getReferredInkElement(brushRef);
		if(! "Brush".equals(inkElement.getInkElementType())) {
			throw new InkMLException("The given Reference attribute value, "+brushRef+
													"is not the 'id' of a Brush Element");
		} else
			brush = (Brush) inkElement;
		return brush;
	}

	/**
	 * This method retrieve the TraceFormat object stored in the definitionMap with
	 * key equal to the traceFormatRef given in the parameter.
	 * @param traceFormatRef The Key of the TraceFormat to be retrived from definitionsMap.
	 * @return The TraceFormat object that is referred by the "traceFormatRef" attribute.
	 * @throws InkMLException
	 */
	public TraceFormat getTraceFormatRefElement(String traceFormatRef) throws InkMLException{
		TraceFormat traceFormat = null;
		InkElement inkElement = getReferredInkElement(traceFormatRef);
		if(! "TraceFormat".equals(inkElement.getInkElementType())) {
			throw new InkMLException("The given Reference attribute value, "+traceFormatRef+
													"is not the 'id' of a TraceFormat Element");
		} else
			traceFormat = (TraceFormat) inkElement;
		return traceFormat;
	}

	/**
	 * This method retrieve the TraceDataElement object stored in the definitionMap with
	 * key equal to the traceDataRef given in the parameter.
	 * @param traceDataRef The Key of the TraceDataElement to be retrived from definitionsMap.
	 * @return TraceDataElement referred by the 'traceDataRef' attribute
	 * @throws InkMLException
	 */
	public TraceDataElement getTraceDataRefElement(String traceDataRef) throws InkMLException {
		TraceDataElement traceDataElement = null;
		InkElement inkElement = getReferredInkElement(traceDataRef);
		if("Trace".equals(inkElement.getInkElementType())) {
			traceDataElement = (Trace) inkElement;
		} else if("TraceGroup".equals(inkElement.getInkElementType())) {
			traceDataElement = (TraceGroup) inkElement;
		} else if("TraceView".equals(inkElement.getInkElementType())) {
			traceDataElement = (TraceView) inkElement;
		} else {
			throw new InkMLException("The given Reference attribute value, "+traceDataRef+
													"is not the 'id' of a Trace Data Element");
		}
		return traceDataElement;
	}

	/**
	 * This method retrieve the InkSource object stored in the definitionMap with
	 * key equal to the inkSourceRef given in the parameter.
	 * @param inkSourceRef The Key of the InkSource object to be retrived from definitionsMap.
	 * @return InkSource object referred by the 'inkSourceRef' attribute
	 * @throws InkMLException
	 */
	public InkSource getInkSourceRefElement(String inkSourceRef) throws InkMLException {
		InkSource inkSource = null;
		InkElement inkElement = getReferredInkElement(inkSourceRef);
		if(! "InkSource".equals(inkElement.getInkElementType())) {
			throw new InkMLException("The given Reference attribute value, "+inkSourceRef+
													"is not the 'id' of a InkSource Element");
		} else
			inkSource = (InkSource) inkElement;
		return inkSource;
	}

	/**
	 * This method retrieve the Canvas object stored in the definitionMap with
	 * key equal to the canvasRef given in the parameter.
	 * @param canvasRef The Key of the Canvas object to be retrived from definitionsMap.
	 * @return Canvas object referred by the 'canvasRef' attribute
	 * @throws InkMLException
	 */
	public Canvas getCanvasRefElement(String canvasRef) throws InkMLException {
		Canvas canvas = null;
		InkElement inkElement = getReferredInkElement(canvasRef);
		if(! "Canvas".equals(inkElement.getInkElementType())) {
			throw new InkMLException("The given Reference attribute value, "+canvasRef+
													"is not the 'id' of a Canvas Element");
		} else
			canvas = (Canvas) inkElement;
		return canvas;
	}

	/**
	 * This method retrieve the CanvasTransform object stored in the definitionMap with
	 * key equal to the canvasTransformRef given in the parameter.
	 * @param canvasTransformRef The Key of the CanvasTransform object to be retrived from definitionsMap.
	 * @return CanvasTransform object referred by the 'canvasTransformRef' attribute
	 * @throws InkMLException
	 */
	public CanvasTransform getCanvasTransformRefElement(String canvasTransformRef) throws InkMLException {
		CanvasTransform canvasTransform = null;
		InkElement inkElement = getReferredInkElement(canvasTransformRef);
		if(! "CanvasTransform".equals(inkElement.getInkElementType())) {
			throw new InkMLException("The given Reference attribute value, "+canvasTransformRef+
													"is not the 'id' of a CanvasTransform Element");
		} else
			canvasTransform = (CanvasTransform) inkElement;
		return canvasTransform;
	}

	/**
	 * This method retrieve the Timestamp object stored in the definitionMap with
	 * key equal to the timestampRef given in the parameter.
	 * @param timestampRef The Key of the Timestamp object to be retrived from definitionsMap.
	 * @return Timestamp object referred by the 'timestampRef' attribute
	 * @throws InkMLException
	 */
	public Timestamp getTimestampRefElement(String timestampRef) throws InkMLException {
		Timestamp timestamp = null;
		InkElement inkElement = getReferredInkElement(timestampRef);
		if(! "Timestamp".equals(inkElement.getInkElementType())) {
			throw new InkMLException("The given Reference attribute value, "+timestampRef+
													"is not the 'id' of a Timestamp Element");
		} else
			timestamp = (Timestamp) inkElement;
		return timestamp;
	}

	/**
	 * This method retrieve the Mapping object stored in the definitionMap with
	 * key equal to the mappingRef given in the parameter.
	 * @param mappingRef The Key of the Mapping object to be retrived from definitionsMap.
	 * @return Mapping object referred by the 'mappingRef' attribute
	 * @throws InkMLException
	 */
	public Mapping getMappingRefElement(String mappingRef) throws InkMLException {
		Mapping mapping = null;
		InkElement inkElement = getReferredInkElement(mappingRef);
		if(! "Mapping".equals(inkElement.getInkElementType())) {
			throw new InkMLException("The given Reference attribute value, "+mappingRef+
													"is not the 'id' of a Mapping Element");
		} else
			mapping = (Mapping) inkElement;
		return mapping;
	}

	/**
	 * Method to give the markup string data of the Definitions data object
	 * @return String markup string
	 */
	public String toInkML() {
		if(directChildrenMap == null || directChildrenMap.size() == 0)
			return "";
		String definitionsElement = "<definitions>";
		Set<Entry<String, InkElement>> entriess = directChildrenMap.entrySet();
		Iterator <Entry<String, InkElement>> children = entriess.iterator();
		while(children.hasNext()) {
			Entry<String, InkElement> child = children.next();
			InkElement inkObject = child.getValue();
			definitionsElement += "\n" + inkObject.toInkML();
		}
		definitionsElement += "\n</definitions>";
		return definitionsElement;
	}

	/**
	 * Method to know if the definitions state is empty.
	 * It decided by the count elements that have already been wrapped in {@code <definitions>} element.
	 * @return the status if the definitions is empty
	 */
	public boolean isEmpty() {
		if ((this.directChildrenMap == null) &&
				(this.indirectChildrenMap == null))
			return true;
		if((this.directChildrenMap.size()==0) &&
				(this.indirectChildrenMap.size()==0))
			return true;
		return false;
	}
	/**
	 * Method to give a list of the elements in the children list,
	 * i.e. elements that have already been wrapped in {@code <definitions>} element.
	 * @return list of child elements of the associated {@code <definitions>} element of the ink document data object.
	 */
	public ArrayList<InkElement> getChildrenList() {
		return (ArrayList<InkElement>) directChildrenMap.values();
	}

	/**
	 * This method retrieve the AnnotationXML object stored in the definitionMap with
	 * key equal to the 'href' given in the parameter.
	 * @param href The Key of the AnnotationXML object to be retrived from definitionsMap.
	 * @return AnnotationXML object referred by the 'href' attribute
	 * @throws InkMLException
	 */
	public AnnotationXML getAnnotationXMLRefElement(String href) throws InkMLException {
		AnnotationXML annotationXML = null;
		InkElement inkElement = getReferredInkElement(href);
		if(! "AnnotationXML".equals(inkElement.getInkElementType())) {
			throw new InkMLException("The given Reference attribute value, "+href+
													"is not the 'id' of a AnnotationXML Element");
		} else
			annotationXML = (AnnotationXML) inkElement;
		return annotationXML;
	}

	/**
	 * Method to check if the element identified by 'id' given in the parameter exist in definitions
	 * @param id
	 * @return the sttaus if the element identified by the 'id' in the parameter.
	 */
	public boolean contains(String id) {
		Set directChildrenKeys = this.directChildrenMap.keySet();
		if(!directChildrenKeys.contains(id)){
			Set indirectChildrenKeys = this.indirectChildrenMap.keySet();
			if(!indirectChildrenKeys.contains(id)){
				return false;
			}
		}
		return true;
	}

	/**
	 * Method used by the Archiver component (InkMLWriter) to save the markup data of the Definitions data object to file or other data stream
	 */
	public void writeXML(InkMLWriter writer) {
		int nChildren=this.directChildrenMap.size();
		if(0==nChildren)
			return;
		writer.writeStartTag("definitions", null);
		writer.incrementTagLevel();
		Iterator<InkElement> values = directChildrenMap.values().iterator();
		while(values.hasNext()){
			InkElement child = values.next();
			child.writeXML(writer);
		}
		writer.decrementTagLevel();
		writer.writeEndTag("definitions");
	}
}
