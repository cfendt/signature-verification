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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * This class models the {@code <traceFormat>} InkML element.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0 Creation date : 6-May-2007
 */

public final class TraceFormat implements InkElement {
    private String id = "";
    private String href = "";
    // Arrylist to hold the all child elements (both regular and intermittend chennales together)
    // ArrayList<Channel> channelList;
    LinkedHashMap<String, Channel> channelMap;

    /**
     * This no argument constructor initializes the ChannelList to an empty list.
     */
    public TraceFormat() {
        super();
        this.channelMap = new LinkedHashMap<String, Channel>();
    }

    /**
     * Method to populate channels of the traceFormat from the givel Channel list
     * 
     * @param channelList
     */
    public void setChannelList(final ArrayList<Channel> channelList) {
        final Iterator<Channel> itr = channelList.iterator();
        while (itr.hasNext()) {
            final Channel channel = itr.next();
            this.addChannel(channel);
        }
    }

    /**
     * Method to construct the Default TraceFormat objet which should have only the X and Y channels.
     * 
     * @return the default TraceFormat InkML Object
     */
    public static TraceFormat getDefaultTraceFormat() {
        final TraceFormat defaultTF = new TraceFormat();
        defaultTF.setId("DefaultTraceFormat");
        final Channel xChannel = new Channel("X", Channel.ChannelType.DECIMAL);
        final Channel yChannel = new Channel("Y", Channel.ChannelType.DECIMAL);
        defaultTF.addChannel(xChannel);
        defaultTF.addChannel(yChannel);
        return defaultTF;
    }

    /**
     * This method returns the Channel InkML object in the channelList of the TraceFormat object identified by the 'channalName' parameter
     * 
     * @param channelName the Name of the Channel
     * @return the Channel InkML object identified by the 'channalName' parameter
     */
    public Channel getChannel(final String channelName) {
        Channel resultChannel = null;
        final ArrayList<Channel> channelList = new ArrayList<Channel>();
        channelList.addAll(this.channelMap.values());
        for (final Channel channel : channelList) {
            if (channel.getName().equals(channelName)) {
                resultChannel = channel;
            }
        }
        return resultChannel;
    }

    /**
     * This method returns the List of Channel objects hold by the TraceFormat object.
     * 
     * @return the Channels object List
     */
    public ArrayList<Channel> getChannelList() {
        final ArrayList<Channel> channelList = new ArrayList<Channel>();
        channelList.addAll(this.channelMap.values());
        return channelList;
    }

    /**
     * This method returns the list of Name of the Channels in the TraceFormat object.
     * 
     * @return the list of name of Channels
     */
    public ArrayList<String> getChannelsName() {
        final ArrayList<Channel> channelList = (ArrayList<Channel>) this.channelMap.values();
        final ArrayList<String> channelsName = new ArrayList<String>();
        for (final Channel channel : channelList) {
            channelsName.add(channel.getName());
        }
        return channelsName;
    }

    /**
     * This method returns the "ID" attribute value of the TraceFormat Object
     * 
     * @return ID string of the TraceFormat object
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * This method returns the class name. This is used to identify the type of InkElement.
     * 
     * @return Class Name String
     */
    @Override
    public String getInkElementType() {
        return "TraceFormat";
    }

    /**
     * Method to compare two traceFormat objects
     * 
     * @param traceFormat
     * @return status of equality comparision of the traceFormat objects
     */
    public boolean equals(final TraceFormat traceFormat) {
        final boolean isEqual = true;
        final java.util.Collection<Channel> channelList1 = this.channelMap.values();
        final java.util.Collection<Channel> channelList2 = traceFormat.getChannelList();
        if (channelList1.size() != channelList2.size()) {
            return false;
        }
        if (!channelList1.containsAll(channelList2)) {
            return false;
        }
        return isEqual;
    }

    /**
     * method to set 'id' attribute
     * 
     * @param id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * method to add a channel to the list of channels of the traceFormat
     * 
     * @param chn
     */
    public void addChannel(final Channel chn) {
        final String channelName = chn.getName();
        this.channelMap.put(channelName, chn);
    }

    /**
     * method to get the InkML markup of the traceFormat as String
     */
    @Override
    public String toInkML() {
        String traceFormatElement = "<traceFormat ";
        String intermittentChannelsElement = null;
        if (!"".equals(this.id)) {
            traceFormatElement += "id='" + this.id + "'>";
        }
        final ArrayList<Channel> channelList = (ArrayList<Channel>) this.channelMap.values();
        final int nChannel = channelList.size();
        if (0 != nChannel) {
            for (int i = 0; i < nChannel; i++) {
                final Channel channel = channelList.get(i);
                if (channel.isIntermittent()) {
                    if (null == intermittentChannelsElement) {
                        intermittentChannelsElement = "<intermittentChannels>";
                    }
                    intermittentChannelsElement += channel.toInkML();
                } else {
                    traceFormatElement += channel.toInkML();
                }
            }
            // add intermittentChannelsElement at the end of traceFormat element
            if (null != intermittentChannelsElement) {
                intermittentChannelsElement += "</intermittentChannels>";
                traceFormatElement += intermittentChannelsElement;
            }
        }
        traceFormatElement += "</traceFormat>";
        return traceFormatElement;
    }

    /**
     * method to write InkML markup of the traceFormat in to a file or other output stream
     */
    @Override
    public void writeXML(final InkMLWriter writer) {
        HashMap<String, String> attr = null;
        if (!"".equals(this.id)) {
            attr = new HashMap<String, String>();
            attr.put("id", this.id);
        }
        if (!"".equals(this.href)) {
            attr = new HashMap<String, String>();
            attr.put("href", this.href);
            writer.writeEmptyStartTag("traceFormat", attr);
        } else {
            final ArrayList<Channel> channelList = new ArrayList<Channel>();
            channelList.addAll(this.channelMap.values());
            final int nChannel = channelList.size();
            boolean isEqualToDefault = false;
            if (2 == nChannel) {
                final TraceFormat defaultTF = TraceFormat.getDefaultTraceFormat();
                final Channel chnVal1_1 = channelList.get(0);
                final Channel chnVal2_1 = defaultTF.getChannel(chnVal1_1.getName());
                final Channel chnVal1_2 = channelList.get(1);
                final Channel chnVal2_2 = defaultTF.getChannel(chnVal1_2.getName());
                if (chnVal1_1.equals(chnVal2_1) && chnVal1_2.equals(chnVal2_2)) {
                    isEqualToDefault = true;
                }
            }
            if (!isEqualToDefault) {
                if (0 != nChannel) {
                    StringBuffer intermittentChannelsElement = null;
                    writer.writeStartTag("traceFormat", attr);
                    writer.incrementTagLevel();
                    for (int i = 0; i < nChannel; i++) {
                        final Channel channel = channelList.get(i);
                        if (channel.isIntermittent()) {
                            if (null == intermittentChannelsElement) {
                                intermittentChannelsElement = new StringBuffer("<intermittentChannels>\r\n");
                            }
                            writer.incrementTagLevel();
                            intermittentChannelsElement.append(writer.getEmptyStartTagXML("channel", channel.getAttributesMap()));
                            writer.decrementTagLevel();
                        } else {
                            channel.writeXML(writer);
                        }
                    }
                    // add intermittentChannelsElement at the end of traceFormat element
                    if (null != intermittentChannelsElement) {
                        for (int i = 0; i < writer.getTagLevel(); i++) {
                            intermittentChannelsElement.append(writer.getTabSpace());
                        }
                        intermittentChannelsElement.append("</intermittentChannels>");
                        writer.writeXML(intermittentChannelsElement.toString());
                    }
                    writer.decrementTagLevel();
                    writer.writeEndTag("traceFormat");
                }
            }
        }
    }

    /**
     * Method to override (inherit the channel definitions from the given traceFormat
     * 
     * @param format
     */
    public void override(final TraceFormat format) {
        final ArrayList<Channel> channelList = format.getChannelList();
        for (final Channel ch : channelList) {
            this.channelMap.put(ch.getName(), ch);
        }
    }

    /**
     * method to get 'href' attribute
     * 
     * @return the href
     */
    public String getHref() {
        return this.href;
    }

    /**
     * method to set 'href' attribute
     * 
     * @param href the href to set
     */
    public void setHref(final String href) {

        this.href = href;
    }
}
