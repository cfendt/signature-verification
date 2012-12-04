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
 * This interface class represents the Trace Collection elements
 * {@code <traceGroup>} and {@code <traceView>}.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 * Creation date : 12th May 2007
 */

public interface TraceCollection extends TraceDataElement {

	/**
	 * It trace data at the index in the given parameter.
	 * The trace data can be any one of the {@code <trace>, <traceGroup>
	 *  and <traceView>} elements.
	 * @param index
	 * @return TraceDataElement
	 * @throws InkMLException
	 */
	TraceDataElement getTraceDataAt(int index) throws InkMLException;
}
