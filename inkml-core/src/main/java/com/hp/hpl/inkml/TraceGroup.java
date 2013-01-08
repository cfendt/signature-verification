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
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * This class models the {@code <traceGroup>} InkML Element.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0 Creation date : 12th May 2007
 */

public class TraceGroup implements TraceDataElement {

    // meber data for the corresponding attributes of the <traceGroup> InkML Element
    private String id = "";
    private String contextRef = "";
    private String brushRef = "";

    // List containing the traceData
    ArrayList<TraceDataElement> traceDataList;

    // The references of the contextual objects that associated with this TraceGroup object
    private Context associatedContext;
    private TraceGroup parentTraceGroup;

    // Create logger instance for logging
    private static Logger logger = Logger.getLogger(TraceGroup.class.getName());

    /**
     * No agrument constructor to create an emty TraceGroup object.
     */
    public TraceGroup() {
        super();
        this.traceDataList = new ArrayList<TraceDataElement>();
    }

    /**
     * Constructor to create TraceGroup InkML object from the TraceGroup DOM element
     * 
     * @param element TraceGroup DOM element
     * @param context The current context object that associated with the containing Ink document
     * @param definitions The global definition state associated with the containing Ink document
     * @throws InkMLException
     */

    Context resolveAssociatedContext(final Context currentContext) {
        Context associatedContext;
        if (null != this.parentTraceGroup) {
            associatedContext = this.parentTraceGroup.getAssociatedContext();
            if (null != associatedContext) {
                return associatedContext;
            }
        }
        if (null != currentContext) {
            return currentContext;
        }
        return Context.getDefaultContext();
    }

    /**
     * This method returns a list of Trace Objects that this Tracegroup object encapsulating
     * 
     * @return List of Trace Objects
     */
    public ArrayList<Trace> getTraceList() {
        final ArrayList<Trace> ret = new ArrayList<Trace>();
        for (final TraceDataElement traceData : this.traceDataList) {
            if ("TraceGroup".equals(traceData.getInkElementType())) {
                ret.addAll(((TraceGroup) traceData).getTraceList());
            } else {
                ret.add((Trace) traceData);
            }
        }
        return ret;
    }

    /**
     * This method assigns the associated context to the TraceGroup Object.
     * 
     * @param associatedContext the Context object to be associated with the TraceGroup object
     */
    @Override
    public void setAssociatedContext(final Context associatedContext) {
        this.associatedContext = associatedContext;

    }

    /**
     * This method gives the Context object that associated with this TraceGroup Object
     * 
     * @return the associated context object
     */
    public Context getAssociatedContext() {
        return this.associatedContext;
    }

    /**
     * This method gives the "id" attribute value of <traceGroup> element.
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
        return "TraceGroup";
    }

    /**
     * This method gives the Brush object associated with this TraceGroup object
     * 
     * @return associated Brush Object
     */
    public Brush getAssociatedBrush() {
        return this.associatedContext.getBrush();
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
        if (from == null) {
            throw new InkMLException("NULL value for the parameter 'from'");
        }
        if (to == null) {
            throw new InkMLException("NULL value for the parameter 'to'");
        }
        TraceGroup.logger.fine("In TG from: " + from + "; to: " + to);
        TraceGroup traceDataElement = null;
        // boolean selectWithinTG=false;
        int[] fromArr, toArr;
        if ("".equals(from) && "".equals(to)) {
            traceDataElement = this;
        } else {
            traceDataElement = new TraceGroup();
            // set the contextual attribute values of the TraceGroup to -
            // this newly created TraceGroup by Range selection
            traceDataElement.setAssociatedContext(this.associatedContext);

            if ("".equals(to)) {
                fromArr = this.parseRange(from);
                if (!this.isRangeDataValid(fromArr)) {
                    throw new InkMLException("The given 'from' RangeString, " + from + " is not valid");
                }
                TraceDataElement subtree = this;
                TraceGroup parentTG = this;
                if (1 == fromArr.length) {
                    subtree = parentTG.getTraceDataAt(fromArr[0]);
                    if ("Trace".equals(subtree.getInkElementType())) {
                        ((Trace) subtree).setParentTraceGroup(parentTG);
                    } else {
                        ((TraceGroup) subtree).setParentTraceGroup(parentTG);
                    }
                    traceDataElement.addToTraceData(subtree);
                } else {
                    // TG->T or T->S.D
                    parentTG = (TraceGroup) subtree;
                    subtree = ((TraceGroup) subtree).getTraceDataAt(fromArr[0]);
                    if ("Trace".equals(subtree.getInkElementType())) {
                        // traceData at index '0' is a Trace element.
                        final Trace data = (Trace) ((Trace) subtree).getSelectedTraceDataByRange(String.valueOf(fromArr[1]), "");
                        data.setParentTraceGroup(parentTG);
                        traceDataElement.addToTraceData(data);

                    } else {
                        // at index '0' we have a TraceGroup, so call this method recursively -
                        // with the remaining index string.
                        // construct the from attribute value excuding the first index
                        // example 4:2:1 --> 2:1 (remove first 2 chracters)
                        final String subFrom = from.substring(2);

                        final TraceGroup data = (TraceGroup) ((TraceGroup) subtree).getSelectedTraceDataByRange(subFrom, "");
                        data.setParentTraceGroup(parentTG);
                        traceDataElement.addToTraceData(data);
                    }
                }

                // Add the remaining elements in the traceDataRef after the -
                // starting index value of 'from' rangeString.
                //
                // Example if 'from' = "2:1"
                // and 'to' is not specified, so we have to get all elements from 3 to all the
                // rest of elements. Since the traceDataList ArrayList stores elements from -
                // starting index of 0, we are getting elements starting from fromArr[0],
                // instead of fromArr[0] + 1.
                for (int j = fromArr[0]; j < this.traceDataList.size(); j++) {
                    traceDataElement.addToTraceData(this.traceDataList.get(j));
                }

            } else if ("".equals(from)) {
                toArr = this.parseRange(to);
                if (!this.isRangeDataValid(toArr)) {
                    throw new InkMLException("The given 'to' RangeString, " + to + " is not valid");
                }

                // Add all the elements in the traceDataRef before the -
                // starting index value of 'to' rangeString.
                //
                // Example if 'to' equal to "4:1"
                // and 'from' is not specified, so we have to get all elements before 4 which are -
                // from index 0 to toArr[0] - 1.
                for (int j = 0; j < toArr[0] - 1; j++) {
                    traceDataElement.addToTraceData(this.traceDataList.get(j));
                }

                TraceDataElement subtree = this;
                TraceGroup parentTG = this;
                if (1 == toArr.length) {
                    subtree = parentTG.getTraceDataAt(toArr[0]);
                    if ("Trace".equals(subtree.getInkElementType())) {
                        ((Trace) subtree).setParentTraceGroup(parentTG);
                    } else {
                        ((TraceGroup) subtree).setParentTraceGroup(parentTG);
                    }
                    traceDataElement.addToTraceData(subtree);
                } else {
                    // TG->T or T->S.D
                    parentTG = (TraceGroup) subtree;
                    subtree = ((TraceGroup) subtree).getTraceDataAt(toArr[0]);
                    if ("Trace".equals(subtree.getInkElementType())) {
                        // traceData at index '0' is a Trace element.
                        final Trace data = (Trace) ((Trace) subtree).getSelectedTraceDataByRange("", String.valueOf(toArr[1]));
                        data.setParentTraceGroup(parentTG);
                        traceDataElement.addToTraceData(data);

                    } else {
                        // at index '0' we have a TraceGroup, so call this method recursively -
                        // with the remaining index string.
                        // construct the from attribute value excuding the first index
                        // example 4:2:1 --> 2:1 (remove first 2 chracters)
                        final String subTo = to.substring(2);

                        final TraceGroup data = (TraceGroup) ((TraceGroup) subtree).getSelectedTraceDataByRange("", subTo);
                        data.setParentTraceGroup(parentTG);
                        traceDataElement.addToTraceData(data);
                    }
                }
            } else { // both 'from' and 'to' rangeString are having non empty values.
                // get elements and add them in order to the subtree under selection
                // check range value validity
                fromArr = this.parseRange(from);
                if (!this.isRangeDataValid(fromArr)) {
                    throw new InkMLException("The given 'from' RangeString, " + from + " is not valid");
                }
                toArr = this.parseRange(to);
                if (!this.isRangeDataValid(toArr)) {
                    throw new InkMLException("The given 'to' RangeString, " + to + " is not valid");
                }

                // 'from' element
                TraceDataElement subtree = this;
                TraceGroup parentTG = this;
                if (1 == fromArr.length) {
                    subtree = parentTG.getTraceDataAt(fromArr[0]);
                    if ("Trace".equals(subtree.getInkElementType())) {
                        ((Trace) subtree).setParentTraceGroup(parentTG);
                    } else {
                        ((TraceGroup) subtree).setParentTraceGroup(parentTG);
                    }
                    traceDataElement.addToTraceData(subtree);
                } else {
                    // TG->T or T->S.D
                    parentTG = (TraceGroup) subtree;
                    subtree = ((TraceGroup) subtree).getTraceDataAt(fromArr[0]);
                    if ("Trace".equals(subtree.getInkElementType())) {
                        // traceData at index '0' is a Trace element.
                        final Trace data = (Trace) ((Trace) subtree).getSelectedTraceDataByRange(String.valueOf(fromArr[1]), "");
                        data.setParentTraceGroup(parentTG);
                        traceDataElement.addToTraceData(data);

                    } else {
                        // at index '0' we have a TraceGroup, so call this method recursively -
                        // with the remaining index string.
                        // construct the from attribute value excuding the first index
                        // example 4:2:1 --> 2:1 (remove first 2 chracters)
                        final String subFrom = from.substring(2);

                        final TraceGroup data = (TraceGroup) ((TraceGroup) subtree).getSelectedTraceDataByRange(subFrom, "");
                        data.setParentTraceGroup(parentTG);
                        traceDataElement.addToTraceData(data);
                    }
                }

                // intermediate element(s)
                final int difference = toArr[0] - fromArr[0];
                final int nIntermitElmnts = difference - 1;
                for (int k = fromArr[0], j = 0; j < nIntermitElmnts; j++) {
                    traceDataElement.addToTraceData(this.traceDataList.get(k + j));
                }

                // 'to' element
                subtree = parentTG = this;
                // selectWithinTG=false;
                if (1 == toArr.length) {
                    subtree = parentTG.getTraceDataAt(toArr[0]);
                    if ("Trace".equals(subtree.getInkElementType())) {
                        ((Trace) subtree).setParentTraceGroup(parentTG);
                    } else {
                        ((TraceGroup) subtree).setParentTraceGroup(parentTG);
                    }
                    traceDataElement.addToTraceData(subtree);
                } else {
                    // TG->T or T->S.D
                    parentTG = (TraceGroup) subtree;
                    subtree = ((TraceGroup) subtree).getTraceDataAt(toArr[0]);
                    if ("Trace".equals(subtree.getInkElementType())) {
                        // traceData at index '0' is a Trace element.
                        final Trace data = (Trace) ((Trace) subtree).getSelectedTraceDataByRange("", String.valueOf(toArr[1]));
                        data.setParentTraceGroup(parentTG);
                        traceDataElement.addToTraceData(data);

                    } else {
                        // at index '0' we have a TraceGroup, so call this method recursively -
                        // with the remaining index string.
                        // construct the from attribute value excuding the first index
                        // example 4:2:1 --> 2:1 (remove first 2 chracters)
                        final String subTo = to.substring(2);

                        final TraceGroup data = (TraceGroup) ((TraceGroup) subtree).getSelectedTraceDataByRange("", subTo);
                        data.setParentTraceGroup(parentTG);
                        traceDataElement.addToTraceData(data);
                    }
                }
            }
        }
        return traceDataElement;
    }

    /**
     * This method adds the TarceDataElement given in the parameter to - the traceData of this TraceGroup object.
     * 
     * @param subtree The TraceData object to be added to the traceDataList of this TraceGroup object
     */
    public void addToTraceData(final TraceDataElement subtree) {
        if (null == this.traceDataList) {
            this.traceDataList = new ArrayList<TraceDataElement>();
        }
        if ("Trace".equals(subtree.getInkElementType())) {
            ((Trace) subtree).setParentTraceGroup(this);
        } else if ("TraceGroup".equals(subtree.getInkElementType())) {
            ((TraceGroup) subtree).setParentTraceGroup(this);
        }
        this.traceDataList.add(subtree);
    }

    public void removeTraceDataAt(final int index) {
        this.traceDataList.remove(index);
    }

    public void removeTraceDataAtFirst(final TraceDataElement data) {
        final int firstIndex = this.traceDataList.indexOf(data);
        this.traceDataList.remove(firstIndex);
    }

    public void removeTraceDataAtLast(final TraceDataElement data) {
        final int lastIndex = this.traceDataList.lastIndexOf(data);
        this.traceDataList.remove(lastIndex);
    }

    /*
     * This method gives an integer array of index number by parsing - the rangeString of the format "n1:n2:n3..."
     */
    private int[] parseRange(final String rangeStr) {
        int rangeArr[] = null;
        final StringTokenizer tokens = new StringTokenizer(rangeStr, ":");
        final int length = tokens.countTokens();
        rangeArr = new int[length];
        for (int i = 0; tokens.hasMoreTokens(); i++) {
            rangeArr[i] = Integer.parseInt(tokens.nextToken());
        }
        return rangeArr;
    }

    /*
     * This method is used to validate the value given in from/to range parameters in a TraceView object selecting data from the TraceData object
     */
    private boolean isRangeDataValid(final int[] rangeArr) throws InkMLException {

        // Check if the first index value of rangeArr value is a valid value.
        if (rangeArr[0] < 1 || rangeArr[0] > this.traceDataList.size()) {
            return false;
        }

        // Check the range data if it try to select within a single point (traceSample data)
        boolean isValid = true;
        final int length = rangeArr.length;
        TraceDataElement temp = this;
        for (int i = 0; i < length; i++) {
            temp = ((TraceGroup) temp).getTraceDataAt(rangeArr[i]);
            if ("Trace".equals(temp.getInkElementType())) {
                if (length - i > 2) {
                    // if the element is Trace then there could be only one more -
                    // level to select. So if the difference between currend index(i)
                    // and the length should not be greater than 2 (it is '2', -
                    // since the for loop index has to start with 0 not with 1).
                    isValid = false;
                }
                break;
            }
        }
        return isValid;
    }

    /**
     * This method gives the traceData object in the index given in - the parameter 'index' from the ordered list of traceData encapsulated - by this traceGroup
     * object
     * 
     * @param index Index of the traceData
     * @return TraceDataElement object in the given 'index' parameter
     * @throws InkMLException
     */
    public TraceDataElement getTraceDataAt(int index) throws InkMLException {
        index--; // Note: The starting value of the parameter index is designed to 1 whereas
                 // the starting value of the ArrayList is 0.
        if (index < 0 || index > this.traceDataList.size()) {
            throw new InkMLException("The indexOutofBound exception in getting TraceData");
        }
        return this.traceDataList.get(index);
    }

    public void setTraceData(final ArrayList<TraceDataElement> newTraceData) {
        this.traceDataList = newTraceData;
    }

    /**
     * This method gives the TraceFormat object associated with this TraceGroup Object
     * 
     * @return The associated TraceFormat object
     */
    public TraceFormat getAssociatedTraceFormat() {
        return this.associatedContext.getTraceFormat();
    }

    void setAssociatedBrush(final Brush brush) {
        this.associatedContext.setBrush(brush);
    }

    /**
     * This method gives the parent Trace Group if any of this Trace object
     * 
     * @return TraceGroup object
     */
    public TraceGroup getParentTraceGroup() {
        return this.parentTraceGroup;
    }

    /**
     * This method assigns the parentTraceGroup data member of this object with - the object given in the parameter
     * 
     * @param parentTraceGroup the object of the Parent TraceGroup of this Trace object
     */
    public void setParentTraceGroup(final TraceGroup parentTraceGroup) {
        this.parentTraceGroup = parentTraceGroup;
    }

    /**
     * Method to create a String of the InkML markup data
     * 
     * @return string of the markup data
     */
    @Override
    public String toInkML() {
        final StringBuffer traceGroupStrBuff = new StringBuffer("<traceGroup");
        if (!"".equals(this.id)) {
            traceGroupStrBuff.append(" id=\"" + this.id + "\"");
        }
        if (!"".equals(this.contextRef)) {
            traceGroupStrBuff.append(" contextRef=\"" + this.contextRef + "\"");
        }
        if (!"".equals(this.brushRef)) {
            traceGroupStrBuff.append(" brushRef=\"" + this.brushRef + "\"");
        }
        final int size = this.traceDataList.size();
        if (size != 0) {
            traceGroupStrBuff.append(">");
            final Iterator<TraceDataElement> iterator = this.traceDataList.iterator();
            while (iterator.hasNext()) {
                final TraceDataElement child = iterator.next();
                traceGroupStrBuff.append(child.toInkML());
            }
            traceGroupStrBuff.append("</traceGroup>");
        } else {
            traceGroupStrBuff.append(" />");
        }
        return traceGroupStrBuff.toString();
    }

    /**
     * method to get the 'contextRef' attribute value
     * 
     * @return string the attribute value
     */
    public String getContextRef() {
        return this.contextRef;
    }

    /**
     * Method to write the InkML markup data in to file or other output stream
     * 
     * @param writer
     */
    @Override
    public void writeXML(final InkMLWriter writer) {
        java.util.HashMap<String, String> attrs = new java.util.HashMap<String, String>();
        if (!"".equals(this.id)) {
            attrs.put("id", this.id);
        }
        if (!"".equals(this.contextRef)) {
            attrs.put("contextRef", this.contextRef);
        }
        if (!"".equals(this.brushRef)) {
            attrs.put("brushRef", this.brushRef);
        }
        if (0 == attrs.size()) {
            attrs = null;
        }
        final int size = this.traceDataList.size();
        if (size != 0) {
            writer.writeStartTag("traceGroup", attrs);
            writer.incrementTagLevel();
            final Iterator<TraceDataElement> iterator = this.traceDataList.iterator();
            while (iterator.hasNext()) {
                final TraceDataElement child = iterator.next();
                child.writeXML(writer);
            }
            writer.decrementTagLevel();
            writer.writeEndTag("traceGroup");
        } else {
            writer.writeEmptyStartTag("traceGroup", attrs);
        }
    }

    /**
     * method to get the 'brushRef' attribute value
     * 
     * @return the brushRef
     */
    public String getBrushRef() {
        return this.brushRef;
    }

    /**
     * method to set the 'brushRef' attribute value
     * 
     * @param brushRef the brushRef to set
     */
    public void setBrushRef(final String brushRef) {
        this.brushRef = brushRef;
    }

    /**
     * method to set the 'contextRef' attribute value
     * 
     * @param contextRef the contextRef to set
     */
    public void setContextRef(final String contextRef) {
        this.contextRef = contextRef;
    }

    /**
     * method to set the 'id' attribute value
     * 
     * @param id the id to set
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * to get a list of traceData such as traces, nested traceGroups enclosed by this traceGroup
     * 
     * @return traceData list
     */
    public ArrayList<TraceDataElement> getTraceDataList() {
        return this.traceDataList;
    }

    /**
     * Method to log the traceGroup trace data
     */
    public void printTraceGroup() {
        final ArrayList<TraceDataElement> list = this.getTraceDataList();
        if (null != list) {
            TraceGroup.logger.fine("<traceGroup>");
            final Iterator<TraceDataElement> itr = list.iterator();

            while (itr.hasNext()) {
                final TraceDataElement td = itr.next();
                if ("Trace".equals(td.getInkElementType())) {
                    ((Trace) td).printTrace();
                } else {
                    ((TraceGroup) td).printTraceGroup();
                }
            }
            TraceGroup.logger.fine("</traceGroup>");
        }
    }
}
