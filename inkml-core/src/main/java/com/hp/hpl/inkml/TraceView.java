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

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class models the {@code <traceView>} InkML element.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0 Creation date : 13-May-2007
 */
public class TraceView implements TraceDataElement {

    // member variable to hold the value of the attributes of <traceView> element
    private String id = "";
    private String contextRef = "";
    private Context associatedContext;
    private String traceDataRef = "";
    private String from = "", to = "";

    // Create logger instance for logging
    private static Logger logger = Logger.getLogger(TraceView.class.getName());

    // List to hold the child TraceViews
    private ArrayList<TraceView> childTraceViewList;

    // reference to the traceDataElement selected by processing -
    // the attributes 'traceDataRef' and 'from', 'to' if given.
    private TraceDataElement selectedTree;

    public TraceView() {
        super();
        this.childTraceViewList = new ArrayList<TraceView>();
    }

    /*
     * This method finds out the traceDataElement object that is created - by the view selection operation
     */
    void setSelectedTree(final Definitions definitions) throws InkMLException {
        TraceDataElement selectedTree = null;
        if (null != definitions) {
            final TraceDataElement traceDataElement = definitions.getTraceDataRefElement(this.traceDataRef);
            TraceView.logger.fine("The reffered traceData: " + traceDataElement.getInkElementType() + " - " + traceDataElement.getId());
            TraceView.logger.fine("Select from:" + this.from + ", to:" + this.to);
            selectedTree = traceDataElement.getSelectedTraceDataByRange(this.from, this.to);
        }
        this.selectedTree = selectedTree;
    }

    /**
     * This method gives the TraceData object that results by the TraceView selection - that has this object as TraceData Reference and a range is provided for
     * selecting data.
     * 
     * @param from the starting index of the selection range
     * @param to the end index of the the selection range
     */
    @Override
    public TraceDataElement getSelectedTraceDataByRange(final String from, final String to) throws InkMLException {
        // As per the spec,
        // "If the referenced object is a <traceView>, then the indexing is relative to -
        // the tree selected by the <traceView>, not relative to the original object."
        if (this.selectedTree != null) {
            return this.selectedTree.getSelectedTraceDataByRange(from, to);
        } else {
            throw new InkMLException("Error: Call to getSelectedTraceDataByRange(String,String) failed." + "Reason: the traceView selection sub-tree is not available");
        }
    }

    /**
     * This method gives the traceDataElement object is created - by the view selection operation
     * 
     * @return traceDataElement object such as Trace or TraceGroup
     */
    public TraceDataElement getSelectedTree() {
        return this.selectedTree;
    }

    /**
     * This method returns a list of Trace Objects that this TraceView object encapsulating
     * 
     * @return List of Trace Objects
     */
    public ArrayList<Trace> getTraceList() throws InkMLException {
        if (this.selectedTree == null) {
            throw new InkMLException("Error: Call to TraceView.getTraceList() failed." + "Reason: the traceView selection sub-tree is not available");
        }
        final ArrayList<Trace> traceList = new ArrayList<Trace>();
        if ("TraceGroup".equals(this.selectedTree.getInkElementType())) {
            traceList.addAll(((TraceGroup) this.selectedTree).getTraceList());
        } else {
            traceList.add((Trace) this.selectedTree);
        }
        return traceList;
    }

    /**
     * This method gives the "id" attribute value of <traceView> element.
     * 
     * @return id String
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * This method gives the type of this Ink element object which is the class name of this object.
     * 
     * @return the class name of this object as the Ink element type
     */
    @Override
    public String getInkElementType() {
        return "TraceView";
    }

    /**
     * This method gives the Context object that associated with this TraceView Object
     * 
     * @return the associated context object
     */
    public Context getAssociatedContext() {
        return this.associatedContext;
    }

    /**
     * This method assigns the associated context to the TraceView Object.
     * 
     * @param associatedContext the Context object to be associated with the TraceView object
     */

    @Override
    public void setAssociatedContext(final Context associatedContext) {
        this.associatedContext = associatedContext;

    }

    /**
     * method to set the childTraceView list
     * 
     * @param newTraceViewList
     */
    public void setchildTraceViewList(final ArrayList<TraceView> newTraceViewList) {
        this.childTraceViewList = newTraceViewList;
    }

    /**
     * method to get a child traceView at the given index
     * 
     * @param index
     * @return TraceView data object at the given index
     * @throws InkMLException
     */
    public TraceView getTraceViewAt(final int index) throws InkMLException {
        if (index >= 0 && index < this.childTraceViewList.size()) {
            return this.childTraceViewList.get(index);
        } else {
            throw new InkMLException("getTraceViewAt(int) called with outofBound index = " + index);
        }
    }

    /**
     * method to add a traceView to the childTraceView List
     * 
     * @param traceView
     */

    public void addToChildTraceViewList(final TraceView traceView) {
        this.childTraceViewList.add(traceView);
    }

    /**
     * method to remove a child TraceView list at the given index.
     * 
     * @param index
     * @throws InkMLException
     */
    public void removeTraceViewAt(final int index) throws InkMLException {
        if (index >= 0 && index < this.childTraceViewList.size()) {
            this.childTraceViewList.remove(index);
        } else {
            throw new InkMLException("removeTraceViewAt(int) called with outofBound index = " + index);
        }
    }

    /**
     * remove the first matching child traceView data object given in the parameter from the child trace view list of the traceView.
     * 
     * @param data
     * @throws InkMLException
     */
    public void removeTraceViewAtFirst(final TraceView data) throws InkMLException {
        if (this.childTraceViewList.size() > 0) {
            final int firstIndex = this.childTraceViewList.indexOf(data);
            this.childTraceViewList.remove(firstIndex);
        }
        throw new InkMLException("removeTraceViewAt(TraceView) called a empty childTraceViewList");
    }

    /**
     * remove the last matching child traceView data object given in the parameter from the child trace view list of the traceView.
     * 
     * @param data
     * @throws InkMLException
     */
    public void removeTraceViewAtLast(final TraceView data) throws InkMLException {
        if (this.childTraceViewList.size() > 0) {
            final int firstIndex = this.childTraceViewList.lastIndexOf(data);
            this.childTraceViewList.remove(firstIndex);
        }
        throw new InkMLException("removeTraceViewAt(TraceView) called a empty childTraceViewList");
    }

    /**
     * This method generate the String of the selectedTree, instead of the original traceView element
     * 
     * @return string of the markup of the selected tree
     */
    @Override
    public String toInkML() {
        if (this.selectedTree != null) {
            return this.selectedTree.toInkML();
        } else {
            TraceView.logger.severe("TraceView.toInkML method: Could not complete the operation." + "Reason: The selcted tree is NULL.");
        }
        return "";
    }

    /**
     * * This method archives the selectedTree, instead of the original traceView element
     * 
     * @param writer the writer used to write to the file or data stream
     */
    @Override
    public void writeXML(final InkMLWriter writer) {
        if (this.selectedTree != null) {
            this.selectedTree.writeXML(writer);
        } else {
            TraceView.logger.severe("TraceView.toInkML method: Could not complete the operation." + "Reason: The selcted tree is NULL.");
        }
        return;
    }

    /**
     * Method to set the 'id' attribute of the traceView
     * 
     * @param id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Method to set the 'contextRef' attribute of the traceView
     * 
     * @param contextRef
     */
    public void setContextRef(final String contextRef) {
        this.contextRef = contextRef;
    }

    /**
     * assign the traceview selection sub-tree from the parameter
     * 
     * @param selectedTree
     */
    public void setSelectedTree(final TraceDataElement selectedTree) {
        this.selectedTree = selectedTree;
    }

    /**
     * set the 'from' attribute value
     * 
     * @param from
     */
    public void setFromAttribute(final String from) {
        this.from = from;
    }

    /**
     * set the 'to' attribute value
     * 
     * @param to
     */
    public void setToAttribute(final String to) {
        this.to = to;
    }

    /**
     * set the 'traceDataRef' attribute value
     * 
     * @param traceDataRef
     */
    public void setTraceDataRef(final String traceDataRef) {
        this.traceDataRef = traceDataRef;
    }

    /**
     * method to resolve the effective context of the traceview element with reference to 'contextRef' attribute if any.
     * 
     * @param definitions
     * @throws InkMlException
     */
    public void resolveContext(final Definitions definitions) throws InkMLException {
        if (null != definitions && !"".equals(this.contextRef)) {
            Context context = null;
            try {
                context = definitions.getContextRefElement(this.contextRef);
            } catch (final InkMLException e) {
                TraceView.logger.severe("Error in TraceView::resolveContext." + "\nMessage: " + e.getMessage());
            }
            if (null != context) {
                this.setAssociatedContext(context);
                if (this.selectedTree == null) {
                    throw new InkMLException("TraceView.resolveContext() failed." + " Reason, there is no selected sub-tree exist");
                }
                this.selectedTree.setAssociatedContext(this.getAssociatedContext());
            }
        }
    }

    /**
     * Method that initialize the child Trace View list data structure in the presence of having child traceview element(s).
     */
    public void initChildViewList() {
        this.childTraceViewList = new ArrayList<TraceView>();
    }

    /**
     * Method to process and construct the tree of InkView selection command
     * 
     * @param definitions
     */
    public void processChildren(final Definitions definitions) {
        // setting the selectedTree of this traceView object with child <traceView> element(s)
        this.selectedTree = new TraceGroup();
        Context traceViewAssoContext = null;
        if (null != definitions && !"".equals(this.contextRef)) {
            try {
                traceViewAssoContext = definitions.getContextRefElement(this.contextRef);
                this.selectedTree.setAssociatedContext(traceViewAssoContext);
            } catch (final InkMLException e) {
                TraceView.logger.severe("Error in TraceView::processChildren." + "\nMessage: " + e.getMessage());
            }
        }
        for (int i = 0; i < this.childTraceViewList.size(); i++) {
            final TraceDataElement selectedSubtree = this.childTraceViewList.get(i).getSelectedTree();
            if (null != traceViewAssoContext) {
                selectedSubtree.setAssociatedContext(traceViewAssoContext);
            }
            ((TraceGroup) this.selectedTree).addToTraceData(selectedSubtree);
        }
    }

    /**
     * Method to log the selected tree resulting in traceView selection
     */
    public void printSelectedTree() {
        if (null == this.selectedTree) {
            TraceView.logger.fine("SelectedTree is NULL");
        } else {
            if ("Trace".equals(this.selectedTree.getInkElementType())) {
                ((Trace) this.selectedTree).printTrace();
            } else {
                ((TraceGroup) this.selectedTree).printTraceGroup();
            }
        }
    }
}
