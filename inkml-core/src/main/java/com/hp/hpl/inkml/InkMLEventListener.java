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
 * SVN MACROS $Revision: 259 $ $Author: selvarmu $ $LastChangedDate: 2008-07-06 14:36:54 +0530 (Sun, 06 Jul 2008) $
 ************************************************************************************/
package com.hp.hpl.inkml;

/**
 * This interface is for classes that listen for InkML events.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0 Creation Date: 27-July-2007
 */
public interface InkMLEventListener {
    /**
     * This method is invoked when a brush change occurs.
     * 
     * @param brush
     */
    void brushChangedEvent(final Brush brush);

    /**
     * This method is invoked when a traceFormat change occurs.
     * 
     * @param traceFormat
     */
    void traceFormatChangedEvent(final TraceFormat traceFormat);

    /**
     * This method is invoked when an canvas change occurs.
     * 
     * @param canvas
     */
    void canvasChangedEvent(final Canvas canvas);

    /**
     * This method is invoked when a canvasTransform change occurs.
     * 
     * @param canvasTransform
     */
    void canvasTransformChangedEvent(final CanvasTransform canvasTransform);

    /**
     * This method is invoked when an inkSource change occurs.
     * 
     * @param inkSource
     */
    void inkSourceChangedEvent(final InkSource inkSource);

    /**
     * This method is invoked when a timestamp change occurs.
     * 
     * @param timestamp
     */
    void timestampChangedEvent(final Timestamp timestamp);

    /**
     * This method is invoked when a context change occurs.
     * 
     * @param context
     */
    void contextChangedEvent(final Context context);

    /**
     * This method is invoked when a trace received.
     * 
     * @param trace
     */
    void traceReceivedEvent(final Trace trace);

    /**
     * This method is invoked when a traceView received.
     * 
     * @param traceView
     */
    void traceViewReceivedEvent(final TraceView traceView);

    /**
     * This method is invoked when a traceGroup received.
     * 
     * @param traceGroup
     */
    void traceGroupReceivedEvent(final TraceGroup traceGroup);
}