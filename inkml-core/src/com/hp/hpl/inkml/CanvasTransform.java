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

import java.util.HashMap;
import java.util.logging.Logger;


/**
 * This class models the {@code <canvasTransform>} element in InkML.
 * The {@code <canvasTransform>} element provides the mapping between the virtual space defined in the associated {@code <canvas>} and {@code <inkSource>} elements.
 * Please refer, http://www.w3.org/TR/InkML/#canvasTransformElement for more details.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 * Creation date : 11-May-2007
 */
public class CanvasTransform implements ContextElement {
	private HashMap<String, String> attributesMap;
	private Mapping forwardMapping;
	private Mapping reverseMapping;
	private static Logger logger = Logger.getLogger(
			CanvasTransform.class.getName());


	public CanvasTransform() {
		attributesMap = new HashMap<String, String>();
		this.forwardMapping = Mapping.getDefaultMapping();
		this.reverseMapping = Mapping.getDefaultMapping();
	}

	public void setAttribute(String attrName, String attrValue) {
		if(this.attributesMap == null)
			this.attributesMap = new HashMap<String, String>();
		this.attributesMap.put(attrName, attrValue);
	}

	public void setForwardMapping(Mapping mapping) {
		this.forwardMapping = mapping;
	}

	public void setReverseMapping(Mapping mapping) {
		this.reverseMapping = mapping;
	}

	public void resetForwardMappingToDefault(Mapping mapping) {
		this.forwardMapping = Mapping.getDefaultMapping();
	}

	public void resetReverseMappingToDefault(Mapping mapping) {
		this.reverseMapping = Mapping.getDefaultMapping();
	}

	public String getId() {
		String idAttr = this.attributesMap.get("id");
		if(idAttr != null)
			return idAttr;
		else
			return "";
	}

	public String getInkElementType() {
		return "CanvasTransform";
	}

	public static CanvasTransform getDefaultCanvasTransform() {
		CanvasTransform canvasTransform =  new CanvasTransform();
		canvasTransform.setId("DefaultCanvasTransform");
		return canvasTransform;
	}

	public void setId(String id) {
		this.attributesMap.put("id", id);
	}

	public boolean equals(CanvasTransform transform) {
		if(transform == null)
			return false;
		boolean isEqual = true;
		if(isInvertible() != transform.isInvertible())
			return false;
		if( (this.forwardMapping == null && this.reverseMapping != null) ||
				(this.forwardMapping != null && this.reverseMapping == null)
		  ) {
			return false;
		} else {
			if(! this.forwardMapping.equals(transform.forwardMapping))
				return false;
			if(! this.reverseMapping.equals(transform.reverseMapping))
				return false;
		}
		return isEqual;
	}


	public String toInkML() {
		String canvasTransform = "<canvasTransform ";
		String id = getId();
		if(!"".equals(id))
			canvasTransform += "id='"+id+"' ";
		boolean isInvertible = isInvertible();
		if(isInvertible){
			canvasTransform += "invertible='"+String.valueOf(isInvertible)+"' ";
		}
		canvasTransform += ">";
		if(null!=this.forwardMapping){
			canvasTransform += this.forwardMapping.toInkML();
		} else {
			canvasTransform += "<mapping type='unknown'/>";
		}
		if(null!=this.reverseMapping){
			canvasTransform += this.reverseMapping.toInkML();
		}
		canvasTransform += "</canvasTransform>";
		return canvasTransform;
	}

	/**
	 * Method to get the value of 'invertible' attribute
	 * @return boolean
	 */
	public boolean isInvertible() {
			String invertibleAttr = this.attributesMap.get("invertible");
			if(invertibleAttr != null)
				try{
					boolean status = new Boolean(invertibleAttr).booleanValue();
					return status;
				}catch (Exception e){
					logger.severe("Improper value to 'invertible' attribute, value = "+invertibleAttr+
							". Returning the default value of false.");
				}
		return false; // the default value of 'invertible' attribute
	}

	/**
	 * Method to set value for the 'invertible' attribute
	 * @param isInvertible
	 */

	public void setInvertible(boolean isInvertible) {
		this.attributesMap.put("invertible", String.valueOf(isInvertible));
	}

	public Mapping getForwardMapping() {
		return forwardMapping;
	}

	public Mapping getReverseMapping() {
		return reverseMapping;
	}

	public void writeXML(InkMLWriter writer) {
		writer.writeStartTag("canvasTransform", attributesMap);
		writer.incrementTagLevel();
		if(null!=this.forwardMapping){
			this.forwardMapping.writeXML(writer);
		} else {
			writer.writeXML("<mapping type='unknown'/>");
		}
		if(null!=this.reverseMapping){
			this.reverseMapping.writeXML(writer);
		}
	}
}