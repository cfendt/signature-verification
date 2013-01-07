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

/**
 * A change in context is recorded in the definitions state and the following trace data's explicit reference to associated context has should be set with the
 * 'id' of the new context element. If the new context element do not have value for 'id' attribute, then an 'id' attribute value is generated and assigned.
 * This helper class generates value for 'id' attribute of inkml:context element and it's children.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0
 */
public class InkMLIDGenerator {
    static long idForContext = 0;
    static long idForBrush = 0;
    static long idForTF = 0;
    static long idForInkSrc = 0;
    static long idForCanvas = 0;
    static long idForCT = 0;
    static long idForTS = 0;

    /**
     * Provides ID for Context
     * 
     * @return string value for 'id' attribute
     */
    public static String getNextIDForContext() {
        String id = "Ctx" + idForContext;
        idForContext++;
        return id;
    }

    /**
     * Provides ID for Brush
     * 
     * @return string value for 'id' attribute
     */
    public static String getNextIDForBrush() {
        String id = "Brush" + idForBrush;
        idForBrush++;
        return id;
    }

    /**
     * Provides ID for TraceFormat
     * 
     * @return string value for 'id' attribute
     */
    public static String getNextIDForTF() {
        String id = "TF" + idForTF;
        idForTF++;
        return id;
    }

    /**
     * Provides ID for InkSource
     * 
     * @return string value for 'id' attribute
     */
    public static String getNextIDForInkSource() {
        String id = "IS" + idForInkSrc;
        idForInkSrc++;
        return id;
    }

    /**
     * Provides ID for Canvas
     * 
     * @return string value for 'id' attribute
     */
    public static String getNextIDForCanvas() {
        String id = "CAnvas" + idForCanvas;
        idForCanvas++;
        return id;
    }

    /**
     * Provides ID for CanvasTransform
     * 
     * @return string value for 'id' attribute
     */
    public static String getNextIDForCanvasTransform() {
        String id = "CT" + idForCT;
        idForCT++;
        return id;
    }

    /**
     * Provides ID for Timestamp
     * 
     * @return string value for 'id' attribute
     */
    public static String getNextIDForTimeStamp() {
        String id = "TS" + idForTS;
        idForTS++;
        return id;
    }
}
