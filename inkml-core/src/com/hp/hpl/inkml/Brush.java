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
 * This class models the {@code <brush>} element in InkML. It maintains a HashMap
 * in which the property name as the Key and Property value as the Map entry value.
 * The brush properties can be defined using {@code <annotationXML>} element, in which case
 * the child elements are defined using tags with name same as property name and
 * the value as element text. The other way to define brush properties is using
 * the {@code <annotation>} element in which the properties value will be given as string
 * which may have some delimitter to separate values of different property.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 * Creation date : 11-May-2007
 */
public class Brush implements ContextElement, Cloneable{
	private String id = "";
	private String brushRef = "";
	// reference to the associated annotationXML object if defined in the InkML data.
	private AnnotationXML annotationXML;
	// reference to the associated annotation object if defined in the InkML data.
	private Annotation annotation;
	// Create logger instance for logging
	private static Logger logger =
		 Logger.getLogger(Brush.class.getName());

	/**
	 * No argument constructor, creates an empty Brush Element.
	 * @see #setBrushProperty(String, String) and
 	 * @see #setBrushRef(String) methods to set property.
	 */
	public Brush() {	}
	/**
	 * Constructor to create an empty brush with given id in the parameter.
	 * @param id
	 * @see #setBrushProperty(String, String) and
	 * @see #setBrushRef(String) methods to set property.
	 */
	public Brush(String id) {
		if(null != id)
			this.id=id;
		else
			this.id=InkMLIDGenerator.getNextIDForBrush();
	}

	/**
	 * Copy constructor used to make a clone of the brush in the parameter
	 * @param brush
	 */
	public Brush(Brush brush) {
		AnnotationXML aXML = brush.getAnnotationXML();
		if(aXML != null){
			this.annotationXML = new AnnotationXML();
			// copy the property data
			this.annotationXML.override(brush.getAnnotationXML());
		}
	}
	/**
	 * Method to get a default brush, with "blue color" and "stroke width = 1".
	 * @return 'default' Brush object.
	 */
	public static Brush getDefaultBrush() {
		Brush defaultBrush = new Brush();
		defaultBrush.setId("DefaultBrush");
		AnnotationXML annotationXML = new AnnotationXML();
		annotationXML.setProperty("color", "blue");
		annotationXML.setProperty("width", "1");
		defaultBrush.setAnnotationXML(annotationXML);
		return defaultBrush;
	}

	/**
	 * Method to get the id attribute of {@code <brush>} element.
	 * @return 'id' attribute of the Brush object.
	 */
	public String getId() {
		return id;
	}
	/**
	 * Method to set the 'id' attribute of this brush object.
	 * @param id String value for 'id'
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * Method to return the value of a brush property given in the parameter.
	 * @param property name of the brush property.
	 * @return the value of the property if there is an xml element with parameter
	 *  'property' value as the tag name. Otherwise returns a null value.
	 * @throws InkMLException when there is no {@code <annotationXML>} child element defined under the <brush> element.
	 */
	public String getBrushProperty(String property) throws InkMLException {
		String value = null;
		if(null == this.annotationXML) {
			String brushId=this.getId();
			if(!brushId.equals("")){
				logger.severe("The <brush> with id='"+brushId+"', do not have <annotationXML> child element");
				throw new InkMLException("The <brush> with id='"+brushId+"', do not have <annotationXML> child element");
			}
			else {
				logger.severe("The <brush> do not have <annotationXML> child element");
				throw new InkMLException("The <brush> do not have <annotationXML> child element");
			}
		} else {
			value = this.annotationXML.getProperty(property);
		}
		return value;
	}
	/**
	 * Method to update the brush property identified by the parameter 'name' with value identified by the param 'value'
	 * @param name String property name
	 * @param value String property value
	 */
	public void setBrushProperty(String name, String value)
	{
		if(this.annotationXML == null)
			this.annotationXML = new AnnotationXML();
		this.annotationXML.setProperty(name, value);
	}

	/**
	 * Method to know the type of InkML entity, in other words the Ink Element.
	 * @return the string value representing the type of the iNk element. It is nothing but the class name.
	 */
	public String getInkElementType() {
		return "Brush";
	}

	/**
	 * Method to get the instance of associated {@code <annotation>} element object.
	 * @return the 'Annotation' object that associated with the Brush object.
	 */
	Annotation getAnnotation() {
		return annotation;
	}

    /**
     * Method to set the child {@code <annotation>} element object.
     * @param annotation
     */
	void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	/**
	 * Method to get the instance of associated {@code <annotationXML>} element object.
	 * @return the 'AnnotatioXML' object that associated with the Brush object.
	 */
	AnnotationXML getAnnotationXML() {
		return annotationXML;
	}

    /**
     * Method to set the child {@code <annotationXML>} element object.
     */
	void setAnnotationXML(AnnotationXML annotationXML) {
		this.annotationXML = annotationXML;
	}

	/**
	 * Method to compare Brush objects by comparing the brush properties.
	 * @param brush object to compared with this brush object.
	 * @return boolean value indicating if the brush object are equal or not.
	 */
	public boolean equals(Brush brush) {
		boolean isEqual = true;
		if( (null == this.annotation && null != brush.annotation) ||
			(null != this.annotation && null == brush.annotation)
		  ) {
			return false;
		} else if (! (null == this.annotation && null == brush.annotation)){
			isEqual = this.annotation.equals(brush.annotation);
		}
		if(false == isEqual)
			return false;
		if( (null == this.annotationXML && null != brush.annotationXML)||
			(null != this.annotationXML && null == brush.annotationXML)
		  ) {
			return false;
		} else if(! (null == this.annotationXML && null == brush.annotationXML))
			isEqual = this.annotationXML.equals(brush.annotationXML);
		return isEqual;
	}

	/**
	 * Method to serialize the markup data of this brush object as a String
	 */
	public String toInkML() {
		String brushElement = new String("<brush ");
		if(!"".equals(this.id))
			brushElement+= "id='"+this.id+"' ";
		brushElement+= "> ";
		if(null!= this.annotation)
			brushElement += this.annotation.toInkML();
		if(null != this.annotationXML)
			brushElement += this.annotationXML.toInkML();
		brushElement+= " </brush> ";
		return brushElement;
	}

	/**
	 * Method to override this brush property with the properties of the brush object of the context object in the parameter.
	 * @param context Context object
	 * @throws InkMLException
	 */
	public void override(Context context) throws InkMLException{
		if(null == context){
			logger.warning("Can not override brush property from a NULL context");
		}
		if(null != this.annotationXML)
			this.annotationXML.override(context.getBrush().getAnnotationXML());
	}

	/**
	 * Method to override this brush property with the properties of the brush object in the parameter.
	 * @param brush object from which properties to be derived from.
	 * @throws InkMLException
	 */
	public void override(Brush brush) throws InkMLException{
		this.id=brush.getId();
		if(null != this.annotationXML)
			this.annotationXML.override(brush.getAnnotationXML());
	}

	/**
	 * Method to create a copy of this brush object. Creates a deep copy of this brush object.
	 * @return cloned copy of the brush object
	 */
	public Brush clone() {
		Brush clone =  null;
		AnnotationXML propertyCollection;
	    try {
	    	clone = (Brush)super.clone();
			if(this.annotationXML != null) {
				propertyCollection = new AnnotationXML();
				// copy the property data
				propertyCollection.override(this.annotationXML);
			}
			else {
				propertyCollection=null;
			}
			clone.setAnnotationXML(propertyCollection);
	    } catch (CloneNotSupportedException ex) {
	    	logger.info("System Error: Cloning Brush is not supported");
	    	throw new InternalError(ex.toString());
	    }
	   return clone;
	  }

	/**
	 * Method to set the 'brushref' attribute of this brush data object with the given value in the parameter.
	 * @param brushRef String value
	 */
	public void setBrushRef(String brushRef) {
		this.brushRef=brushRef;
	}

	/**
	 * Method to serialize the brush object to markup date for writing using the Writer given in the parameter.
	 * This method is used to save the InkML markup data of brush to file.
	 * @param writer InkMLWriter object
	 */
	public void writeXML(InkMLWriter writer) {
		HashMap<String,String> attrs;
		if(!"".equals(this.id) || !"".equals(this.brushRef)){
			attrs = new HashMap<String,String>();
			if(!"".equals(this.id))
				attrs.put("id", this.id);
			if(!"".equals(this.brushRef))
				attrs.put("brushRef", this.brushRef);
		} else {
			attrs=null;
		}
		if(null!= this.annotation || null != this.annotationXML){
			writer.writeStartTag("brush", attrs);
			writer.incrementTagLevel();
			if(null!= this.annotation)
				this.annotation.writeXML(writer);
			if(null != this.annotationXML)
				this.annotationXML.writeXML(writer);
			writer.decrementTagLevel();
			writer.writeEndTag("brush");
		} else {
			writer.writeEmptyStartTag("brush", attrs);
		}
	}
}