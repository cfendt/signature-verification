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
import java.util.Iterator;
import java.util.Set;

/**
 * This class models the {@code <mapping>} element of InkML.
 * Please refer, http://www.w3.org/TR/InkML/#mappingElement for more descriptions.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 */
public class Mapping implements ContextElement {
	private String id = "";
	private String type = "";
	private HashMap<String, Double> channelFactorMap;
	private HashMap<String, String> bindVarsMap;
	private URI mappingRef;
	/**
	 * Constructor creates a default mapping element with type set to 'unknown'.
	 *
	 */
	public Mapping() {
		this.id = "DefaultMapping";
		this.type="unknown";
		channelFactorMap = new HashMap<String, Double>();
		channelFactorMap.put("X", new Double(1));
		channelFactorMap.put("Y", new Double(1));
		bindVarsMap = new HashMap<String, String>();
	}
	/**
	 * to get 'id' attribute
	 */
	public String getId() {
		return id;
	}

	/**
	 * to get 'type' attribute
	 */
	public String getInkElementType() {
		return "Mapping";
	}

	/**
	 * method that returns the defaultmapping data object
	 * @return default mapping data object which is having 'identity' mapping
	 */
	public static Mapping getDefaultMapping() {
		return new Mapping();
	}

	/**
	 * method to get the simple multiplication factor that can be used as a transform factor
	 * @param channelName
	 * @return multiplication transform factor to be applied on the sample point data of a the given channel
	 */
	public double getChannelFactor(String channelName) {

		return channelFactorMap.get(channelName).doubleValue();

	}
	/**
	 * method to set the simple multiplication factor that can be used as a transform factor
	 * @param channelName
	 * @param factor tarnsform factor data for a given channel
	 */

	public void setChannelFactor(String channelName, double factor) {
		channelFactorMap.put(channelName, new Double(factor));
	}

	/**
	 * equals method to compare two mapping objects
	 * @param mapping
	 * @return status on equality of the objects
	 */
	public boolean equals(Mapping mapping) {
		if(mapping == null)
			return false;
		if(!this.type.equalsIgnoreCase(mapping.getType())){
			return false;
		}
		boolean isEqual = true;
		if((this.channelFactorMap.size() ==0) && (0 ==  mapping.channelFactorMap.size()))
			return true;
		if(this.channelFactorMap.size() != mapping.channelFactorMap.size())
			return false;
		Set<String> keys = channelFactorMap.keySet();
		if(!keys.containsAll(mapping.channelFactorMap.keySet())) {
			return false;
		} else {
			Iterator iterator = keys.iterator();
			while(iterator.hasNext()) {
				String key = (String)iterator.next();
				double value1 =0, value2=0;
				value1 = getChannelFactor(key);
				value2 =mapping.getChannelFactor(key);
				if(value1 != value2)
					return false;
			}
		}
		return isEqual;
	}

	/**
	 * method to give inkml markup string of the mapping data object
	 */
	public String toInkML() {
		/* <mapping type='mathml'>
				<bind  target='X' variable='x'/>
				<math xmlns ='http://www.w3.org/1998/Math/MathML'>
				  	<apply>
				   		<times/>
				   		<cn type='integer'>s*</cn>
				      	<ci>x</ci>
				    </apply>
			  	</math>
			</mapping>
		 */
		String mappingElement = new String("<mapping ");
		Set keySet = bindVarsMap.keySet();
		if(this.type.equalsIgnoreCase("mathml") && (0 != keySet.size())) {
			mappingElement+= new String("type='mathml'>");
			Iterator keys = keySet.iterator();
			String[] applyElements = new String[keySet.size()];
			int applyElmntsIdx = 0;
			while(keys.hasNext()) {
				String var = (String) keys.next();
				String chnName = bindVarsMap.get(var);
				Double timesFactor = channelFactorMap.get(chnName);

				//create a bind element
				String bindElmnt = new String("<bind target='"+chnName+"' variable='"+var+"' />");
				mappingElement+="\n   "+bindElmnt;

				//create an apply element
				String applyElement = new String("<apply>\n  <times/>\n  "+
						"<cn type='integer' >"+String.valueOf(timesFactor)+"</cn>\n  "+
						"<ci>"+var+"</ci>\n  </apply>");
				applyElements[applyElmntsIdx++] = applyElement;
			}
			String mathElement = new String("<math xmlns ='http://www.w3.org/1998/Math/MathML'>\n  ");
			if(applyElements[1]!=null) {
				String listElement = new String("<list>\n  ");
				for(int j=0; j<applyElements.length; j++) {
					if(applyElements[j]!=null) {
						listElement+=applyElements[j];
					}
				}
				listElement+="</list>\n  ";
				mathElement+=listElement;
			} else {
				if(applyElements[0]!=null) {
					mathElement+=applyElements[0];
				}
			}
			mathElement+="</math>\n  ";
			mappingElement+=mathElement;
			mappingElement+="</mapping>";
		}  else if(this.type.equalsIgnoreCase("identity")) {
			return "<mapping type='identity'/>";
		} else if(this.type.equalsIgnoreCase("unknown")) {
			return "<mapping type='unknown'/>";
		}
		return mappingElement;
	}

	/**
	 * method to get the 'type' attribute
	 * @return type attribute
	 */
	public String getType() {
		return type;
	}
	/**
	 * method to set the 'type' attribute
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * method to set the 'id' attribute
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * method to archive inkml markup data to output writer to write in to files and streams
	 */
	public void writeXML(InkMLWriter writer) {
		boolean isIDEmpty = this.id.equals("");
		boolean isTypeEmpty = this.type.equals("");
		if(isIDEmpty && isTypeEmpty){
			return; // nothing to write
		} else {
			HashMap<String, String> attributesMap = new HashMap<String, String>();
			if(!isIDEmpty){
				attributesMap.put("id", this.id);
			}
			if(!isTypeEmpty){
				attributesMap.put("type", this.type);
			}
			writer.writeEmptyStartTag("context", attributesMap);
		}
	}

	/**
	 * to get 'mappingRef' attribute value
	 * @return 'mappingRef' attribute value
	 */
	public URI getMappingRef() {
		return mappingRef;
	}
	/**
	 * to set 'mappingRef' attribute value
	 * @param mappingRef as URI
	 */
	public void setMappingRef(URI mappingRef) {
		this.mappingRef = mappingRef;
	}
	/**
	 * to set 'mappingRef' attribute value
	 * @param mappingRef as String
	 */
	public void setMappingRef(String mappingRef) {
		this.mappingRef = new URI(mappingRef);
	}
}
