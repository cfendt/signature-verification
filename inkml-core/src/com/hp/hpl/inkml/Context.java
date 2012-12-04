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
import java.util.Map;
import java.util.TreeMap;

import java.util.logging.Logger;

/**
 * This class models the {@code <context>} element in InkML.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 * Creation date : 8-May-2007
 */

public class Context implements InkElement, Cloneable {

	private HashMap<String, String> attributesMap;
	private ArrayList<ContextElement> contextElementList;
	private Brush brush;
	private TraceFormat traceFormat;
	private InkSource inkSource;
	private Canvas canvas;
	private CanvasTransform canvasTransform;
	private Timestamp timestamp;

	// Create logger instance for logging
	private static Logger logger =
		 Logger.getLogger(Context.class.getName());


	/**
	 * No argument Constructor used to create an empty Context data object.
	 *
	 */
	public Context() {
		this.attributesMap = new HashMap<String, String>();
		this.contextElementList = new ArrayList<ContextElement>();
	}
	/**
	 * Method to set an attribute of the data object.
	 * It is used by the Parser classes while binding the parsed data the data object
	 * @param attrName name of the attribute
	 * @param attrValue vale of the attribute as string
	 */
	public void setAttribute(String attrName, String attrValue) {
		this.attributesMap.put(attrName, attrValue);
	}

	/**
	 * Method to get the 'brushRef' attribute
	 * @return the 'brushRef' attribute value
	 */
	public String getBrushRef() {
		String reference = this.attributesMap.get("brushRef");
		return (null == reference)?"":reference;
	}

	/**
	 * Method to set the 'brushRef' attribute
	 *
	 */
	public void setBrushRef(String brushRef) {
		this.attributesMap.put("brushRef",brushRef);
	}

	/**
	 * Method to get the 'canvasRef' attribute
	 * @return String 'canvasRef' attribute value
	 */

	public String getCanvasRef() {
		String reference = this.attributesMap.get("canvasRef");
		return (null == reference)?"":reference;
	}
	/**
	 * Method to set the 'canvasRef' attribute
	 * @param canvasRef 'canvasRef' attribute value
	 */

	public void setCanvasRef(String canvasRef) {
		this.attributesMap.put("canvasRef",canvasRef);
	}

	/**
	 * Method to get the 'canvasTransformRef' attribute
	 * @return String
	 */

	public String getCanvasTransformRef() {
		String reference = this.attributesMap.get("canvasTransformRef");
		return (null == reference)?"":reference;
	}

	/**
	 * Method to set the 'canvasTransformRef' attribute
	 * @param canvasTransformRef
	 */
	public void setCanvasTransformRef(String canvasTransformRef) {
		this.attributesMap.put("canvasTransformRef",canvasTransformRef);
	}

	/**
	 * Method to get the 'inkSourceRef' attribute
	 * @return String
	 */
	public String getInkSourceRef() {
		String reference = this.attributesMap.get("inkSourceRef");
		return (null == reference)?"":reference;
	}

	/**
	 * Method to set the 'inkSourceRef' attribute
	 * @param inkSourceRef
	 */
	public void setInkSourceRef(String inkSourceRef) {
		this.attributesMap.put("inkSourceRef",inkSourceRef);
	}

	/**
	 * Method to get the 'timestampRef' attribute
	 * @return String timestampRef
	 */

	public String getTimestampRef() {
		String reference = this.attributesMap.get("timestampRef");
		return (null == reference)?"":reference;
	}

	/**
	 * Method to set the 'timestampRef' attribute
	 * @param timestampRef
	 */
	public void setTimestampRef(String timestampRef) {
		this.attributesMap.put("timestampRef",timestampRef);
	}

	/**
	 * Method to get the 'traceFormatRef' attribute
	 * @return String traceFormatRef
	 */

	public String getTraceFormatRef() {
		String reference = this.attributesMap.get("traceFormatRef");
		return (null == reference)?"":reference;
	}

	/**
	 * Method to set the 'traceFormatRef' attribute
	 * @param traceFormatRef
	 */
	public void setTraceFormatRef(String traceFormatRef) {
		this.attributesMap.put("traceFormatRef",traceFormatRef);
	}

	/**
	 * Method to set the 'canvasTransform' child
	 * @param canvasTransform
	 */
	public void setCanvasTransform(CanvasTransform canvasTransform) {
		this.canvasTransform=canvasTransform;
	}

	/**
	 * Method to set the 'contextRef' attribute
	 * @param contextRef
	 */
	public void setContextRef(String contextRef) {
		this.attributesMap.put("contextRef",contextRef);
	}

	/**
	 * Method to set the 'timestamp' child
	 * @param timestamp
	 */
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}


	/**
	 * Copy constructor to derive value from the given Context object in the parameter
	 * @param context
	 */
	public Context (Context context) {
		this.brush = context.getBrush();
		this.traceFormat = context.getTraceFormat();
		this.inkSource = context.getInkSource();
		this.canvas = context.getCanvas();
		this.canvasTransform = context.getCanvasTransform();
		this.timestamp = context.getTimestamp();
	}

	/**
	 * This method gives the defaultContext InkML object.
	 * @return the defaultContext InkML object.
	 * @throws InkMLException
	 */
	public static Context getDefaultContext() {
		Context defaultCtx = new Context();
		defaultCtx.setId("DefaultContext");
		defaultCtx.setCanvasRef("#DefaultCanvas");
		defaultCtx.setCanvas(Canvas.getDefaultCanvas());
		defaultCtx.setCanvasTransformRef("#DefaultCanvasTransform");
		defaultCtx.setCanvasTransform(CanvasTransform.getDefaultCanvasTransform());
		defaultCtx.setTraceFormatRef("#DefaultTraceFormat");
		defaultCtx.setTraceFormat(TraceFormat.getDefaultTraceFormat());
		defaultCtx.setInkSourceRef("#DefaultInkSource");
		defaultCtx.setInkSource(InkSource.getDefaultInkSource());
		defaultCtx.setBrushRef("#DefaultBrush");
		defaultCtx.setBrush(Brush.getDefaultBrush());
		defaultCtx.setTimestampRef("#DefaultTimestamp");
		defaultCtx.setTimestamp(Timestamp.getDefaultTimestamp());
		return defaultCtx;
	}

	/**
	 * This method gives the Canvas InkML object of the containing {@code <canvas>} child element.
	 * @return the Canvas InkML object
	 */
	public Canvas getCanvas() {
		return canvas;
	}

	/**
	 * This method gives the InkSource InkML object of the containing {@code <inkSource>} child element.
	 * @return the InkSource InkML object
	 */
	public InkSource getInkSource() {
		return inkSource;
	}

	/**
	 * This method gives the InkSource InkML object of the containing {@code <inkSource>} child element.
	 * @return the InkSource InkML object
	 */
	public CanvasTransform getCanvasTransform() {
		return this.canvasTransform;
	}

	/**
	 * This method gives the Timestamp InkML object of the containing {@code <timestamp>} child element.
	 * @return the Timestamp InkML object
	 */
	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	/**
	 * This method gives the TraceFormat InkML object of the containing {@code <traceFormat>} child element.
	 * @return the TraceFormat InkML object
	 */
	public TraceFormat getTraceFormat() {
		return this.traceFormat;
	}

	/**
	 * This method gives the value of the "id" attribute of the {@code <Context>} InkML object
	 */
	public String getId() {
		String reference = this.attributesMap.get("id");
		return (reference == null)?"":reference;
	}

	/**
	 * This method gives the Brush InkML object of the containing {@code <brush>} child element.
	 * @return the Brush InkML object
	 */
	public Brush getBrush() {
		return this.brush;
	}

	/**
	 * This method returns the Class name string. It is  used to identify the type of the InkElement.
	 * @return the class name String
	 */

	public String getInkElementType() {
		return "Context";
	}

	/**
	 * This method returns the contextRef attribute value of the Context element.
	 * @return the contextRef attribute value String
	 */
	public String getContextRef() {
		String value = this.attributesMap.get("contextRef");
		return (null == value)? "":value;
	}

	/**
	 * Method to set the 'brush' child
	 * @param brush
	 */
	public void setBrush(Brush brush) {
		this.brush = brush;
	}
	/**
	 * Method to set the 'TraceFormat' child
	 * @param traceFormat
	 */
	public void setTraceFormat(TraceFormat traceFormat) {
		this.traceFormat = traceFormat;
	}
	/**
	 * Method to set the 'Canvas' child
	 * @param canvas
	 */
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;

	}
	/**
	 * Method to set the 'InkSource' child
	 * @param inkSource
	 */
	public void setInkSource(InkSource inkSource) {
		this.inkSource = inkSource;
	}
	/**
	 * Overriden equals method to compare Context data objects
	 * @param context object to be compared with.
	 * @return boolean status of equality
	 */
	public boolean equals(Context context) {
		if(context == null)
			return false;
		if(! this.brush.equals(context.brush))
			return false;
		if(! this.traceFormat.equals(context.traceFormat))
			return false;
		if(! this.inkSource.equals(context.inkSource))
			return false;
		if(! this.canvas.equals(context.canvas))
			return false;
		if(! this.canvasTransform.equals(context.canvasTransform))
			return false;
		if(! this.timestamp.equals(context.timestamp))
			return false;
		return true;
	}
	/**
	 * Method to set the 'id' attribute
	 * @param id
	 */
	public void setId(String id) {
		this.attributesMap.put("id", id);
	}
	/**
	 * Method to set add the child contextual element to the children list
	 * @param ctxChild
	 */
	public void addToContextElementList(ContextElement ctxChild) {
		this.contextElementList.add(ctxChild);
	}

	/**
	 * This method derives the value for the contextual child from the values of -
	 *  'Ref' attributes such as contextRef and the given child elements.
	 * @param defs
	 * @param currentCtx
	 * @throws InkMLException
	 */
	public void deriveContextualChildrenData(Definitions defs, Context currentCtx) throws InkMLException{
		// TODO Auto-generated method stub
		//copyFromCurrentContext(currentCtx);
		String ctxRef = getContextRef();
		if(!"".equals(ctxRef))
			copyFromContextReference(defs, ctxRef);
		String brushRef=getBrushRef();
		if(!"".equals(brushRef)){
			//copyFromBrushReference
			this.brush.override(defs.getBrushRefElement(brushRef));
			if (this.brush.getId().equals("")){
				// assign a new ID
				String brushID = InkMLIDGenerator.getNextIDForBrush();
				while(defs.contains(brushID))
					brushID = InkMLIDGenerator.getNextIDForBrush();
				this.brush.setId(brushID);
			}
		} else {
			if(this.brush.getAnnotationXML()==null){
				// empty brush element
				String id = this.brush.getId();
				this.brush = currentCtx.getBrush();
				// reassign id if any
				if (!"".equals(id))
					this.brush.setId(id);
			}
		}
		String inkSourceRef=getInkSourceRef();
		if(!"".equals(inkSourceRef)){
			this.inkSource = defs.getInkSourceRefElement(inkSourceRef);
			this.traceFormat=this.inkSource.getTraceFormat();
		}
		String traceFormatRef=getTraceFormatRef();
		if(!"".equals(traceFormatRef)){
			this.traceFormat = defs.getTraceFormatRefElement(traceFormatRef);
		}
		// To Do: add for other ref elements

		int nChildren = this.contextElementList.size();
		logger.finer("CTX child List size: "+nChildren);
		if(0 != nChildren){
			String type;
			ContextElement child;
			Iterator<ContextElement> iterator = this.contextElementList.iterator();
			while(iterator.hasNext()){
				child = iterator.next();
				type = child.getInkElementType();
				if("Brush".equals(type)){
					logger.finer("CTX Brush child");
					Brush currBrush = currentCtx.getBrush();
					this.brush.override((Brush)child);
					String id = this.brush.getId();
					if(!"".equals(id)){
						String currentBrushId = currBrush.getId();
						if(!"".equals(currentBrushId)){
							((Brush)child).setBrushRef("#"+currentBrushId);
							defs.addToDirectChildrenMap(child);
						} else {
							defs.addToDirectChildrenMap(this.brush);
						}
						this.setBrushRef("#"+id);
						iterator.remove();
					}
				} else if("InkSource".equalsIgnoreCase(type)){
					this.inkSource = (InkSource)child;
					// the context traceFormat is assigned with -
					// the format of the inkSource.
					// It will be overwitten to the Context --> traceFormat -
					// if exist. (done in the next 'else if' block
					this.traceFormat = this.inkSource.getTraceFormat();
				} else if("TraceFormat".equals(type)){
					if(((TraceFormat)child).channelMap.size()==0){ // child TF is empty!
						if(this.traceFormat == null){ // not derived from inkSource too!
							// get it from current context
							this.traceFormat.override(currentCtx.getTraceFormat());
						}
					} else {
						logger.fine("overriding TF");
						this.traceFormat.override((TraceFormat)child);
						this.traceFormat = (TraceFormat)child;
					}
				} else if("Canvas".equalsIgnoreCase(type)){
					this.canvas = (Canvas)child;
				} else if("CanvasTransform".equalsIgnoreCase(type)){
					this.canvasTransform=(CanvasTransform)child;
				} else if("Timestamp".equalsIgnoreCase(type)){
					this.timestamp = (Timestamp)child;
				}
			}
		}
	}
	// copy data From the given context object in the parameter
	private void copyFromContext(Context context) {
		this.brush = context.getBrush().clone();
		// To do: implement the clone method to other context child objects
		this.canvas = context.getCanvas();
		this.canvasTransform = context.getCanvasTransform();
		this.inkSource = context.getInkSource();
		this.traceFormat = context.getTraceFormat();
		this.timestamp = context.getTimestamp();
	}
	// copy data From the given context reference object in the parameter
	private void copyFromContextReference(Definitions defs, String ctxRef) throws InkMLException{
		Context refferedContext = defs.getContextRefElement(ctxRef);
		copyFromContext(refferedContext);
	}
	/**
	 * Method to give the markup string data of the Context data object
	 * @return String markup string
	 */
	public String toInkML() {
		StringBuffer elementStrBuff = new StringBuffer("<context");
		if(attributesMap != null){
		   	// sort by attribute name
            Map<String, String> sortedAttrs =
            	new TreeMap<String, String>(attributesMap);

        	java.util.Set<String> keys = sortedAttrs.keySet();
        	java.util.Iterator<String> itr = keys.iterator();
        	while(itr.hasNext()){
        		elementStrBuff.append(' ');
                String key = itr.next();
                elementStrBuff.append(key);
                elementStrBuff.append("=\"");
                elementStrBuff.append(attributesMap.get(key));
                elementStrBuff.append('"');
            }
        }
		int size = this.contextElementList.size();
		if(size != 0){
			elementStrBuff.append(">");
			Iterator<ContextElement> iterator = contextElementList.iterator();
			while(iterator.hasNext()){
				ContextElement child = iterator.next();
				elementStrBuff.append(child.toInkML());
			}
			elementStrBuff.append("</context>");
		} else {
			elementStrBuff.append(" />");
		}
		return elementStrBuff.toString();
	}

	/**
	 * Method used by the Archiver component (InkMLWriter) to save the markup data of the Context data object to file or other data stream
	 */
	public void writeXML(InkMLWriter writer) {
		int size = this.contextElementList.size();
		if(size != 0){
			writer.writeStartTag("context", attributesMap);
			writer.incrementTagLevel();
			Iterator<ContextElement> iterator = contextElementList.iterator();
			while(iterator.hasNext()){
				ContextElement child = iterator.next();
				child.writeXML(writer);
			}
			writer.decrementTagLevel();
			writer.writeEndTag("context");
		} else {
			writer.writeEmptyStartTag("context", attributesMap);
		}
	}
	/**
	 * Method to resolve the impplicitg references to attributes from the Default Context
	 *
	 */
	public void resolveImplicitReferenceWithDefaultContext() {
		Context defaultCtx = Context.getDefaultContext();
		if (null == this.brush)
			this.brush = defaultCtx.getBrush();
		if (null == this.traceFormat)
			this.traceFormat = defaultCtx.getTraceFormat();
		if (null == this.inkSource)
			this.inkSource = defaultCtx.getInkSource();
		if (null == this.canvas)
			this.canvas = defaultCtx.getCanvas();
		if (null == this.canvasTransform)
			this.canvasTransform = defaultCtx.getCanvasTransform();
		if (null == this.timestamp)
			this.timestamp = defaultCtx.getTimestamp();
	}
}
