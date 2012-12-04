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
 * This generic exception class is used to crteate any InkML specific Exceptions.
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 * Creation date : 7-May-2007
 */
public class InkMLException extends Exception {

	/**
	 * Randomly generated static SerialVerison ID.
	 * It is recommanded to define this as the Exception class is serializable.
	 */
	private static final long serialVersionUID = -3284213657128760183L;

	/**
	 * Constructor creates the Excpetion class object with the given String
	 *   parameter as the exception message.
	 * @param message the exception message
	 */
	public InkMLException(String message) {
		super(message);
	}

	/**
	 * Constructor creates the Excpetion class object with the given String
	 *   parameter as the exception message which is appended with the message
	 *    from the exception object in the parameter list.
	 * @param message the exception message
	 * @param exception the base exception object
	 */
	public InkMLException(String message, Exception exception) {
		super(message+"\n"+exception.getMessage());
	}

	/**
	 * Constructor creates the Excpetion class object with the message of
	 * the given exception object in the parameter.
	 * @param exception the base exception object
	 */
	public InkMLException(Exception exception) {
		super(exception.getMessage());
	}
}
