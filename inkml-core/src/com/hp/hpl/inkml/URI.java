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
 * This class models the URI attribute of the many InkML elements.
 * ** Note ** : Implementation is not complete.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 */
public class URI {
	private String uriString;

	/**
	 *  Constructor that build the string value of the URI attribute
	 * @param uriString
	 */
	public URI(String uriString) {
		//check the existence of the given URI.

		// throw InkMLException when the given URI is not valid.

		// set the uriString member data with the value of param, uriString
		this.uriString = uriString;
	}
	/**
	 * Method to get the 'URI' attribute value
	 * @return URI attribute value as string
	 */
	public String getURIString() {
		return this.uriString;
	}
}
