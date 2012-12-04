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

/**
 * This interface represents the TraceData elements {@code <trace>, <traceGroup>
 *  and <traceView>} which can be referred by the <i>'traceDataRef'</i> attribute
 *   of the {@code <traceView>} element.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 * Creation date : 12-May-2007
 */

public interface TraceDataElement extends InkElement {

	/**
	 * The method selectes the traceData identified by the range parameters 'from' and 'to'.
	 * @param from the starting index of the range for selection
	 * @param to the end index of the range for selection
	 * @return the selected traceData
	 * @throws InkMLException
	 */
	public TraceDataElement getSelectedTraceDataByRange(String from, String to) throws InkMLException;

	/**
	 * This method assigns the Context associated with the traceData.
	 * @param associatedContext the Context object to be associated with the traceData
	 */
	public void setAssociatedContext(Context associatedContext);
	/**
	 * Method to generate the InkML markup of the traceData element as string
	 */
	public String toInkML();
	/**
	 * Method to write the InkML markup of the traceData element in to file or other output streams
	 */
	public void writeXML(InkMLWriter writer);
}
