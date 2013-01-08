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

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * This clas models the <channel> InkML element.
 * 
 * @author Muthuselvam Selvaraj
 * @version 0.5.0 Creation date : 7-May-2007
 */

public class Channel implements InkElement {
    private final HashMap<String, String> attributesMap = new HashMap<String, String>();

    /**
     * Different channel types to indicate the data type of the channel. Possible values are 'decimal', 'integer' and 'boolean'. The default value is 'decimal'.
     */
    public static enum ChannelType {
        /**
         * Decimal channel indicated by "decimal" in the InkML data.
         */
        DECIMAL,
        /**
         * Interger channel indicated by "integer" in the InkML data.
         */
        INTEGER,
        /**
         * Bollean channel indicated by "boolean" in the InkML data.
         */
        BOOLEAN
    }

    /**
     * The orientaion of Channel. Possible values are "+ve" and "-ve". Default value is "+ve".
     */
    public static enum OrientationType {
        /**
         * Positive direction indicated by "+ve".
         */
        POSITIVE,
        /**
         * Negative direction indicated by "-ve".
         */
        NEGATIVE
    }

    // Flag to indicate the Intermittend typr of channels
    private boolean isIntermittent;

    // Create logger instance for logging
    private static Logger logger = Logger.getLogger(Channel.class.getName());

    /**
     * This method is used to take all <channel> attributes set through params to create a Channel object.
     * 
     * @param name name of the channel
     * @param id unique 'id' of the channel
     * @param type the Channel.ChannelType enumuration value
     * @param min the min dimension of the channel
     * @param max the max dimension of the channel
     * @param units units of the channel
     * @param orientation to indicate the orientataion of the channel as Positive/Negative orientation.
     * @param respectTo timestamp to specify the frame of reference for the traceData Sample having the channel value.
     * @throws InkMLException
     */
    public Channel(final String name, final String id, final ChannelType type, final String min, final String max, final String units, final OrientationType orientation, final URI respectTo) {
        this.attributesMap.put("name", name);
        this.attributesMap.put("id", id);
        this.attributesMap.put("type", type.toString());
        this.attributesMap.put("min", min);
        this.attributesMap.put("max", max);
        this.attributesMap.put("units", units);
        this.attributesMap.put("orientation", orientation.toString());
        if (null != respectTo) {
            this.attributesMap.put("respectTo", respectTo.toString());
        }
    }

    /**
     * Constructor to build a Channel Object with the given Channel Name and - Channel Type and default values for all other members.
     * 
     * @param name The Channel Name
     * @param type The Channel Type
     */
    public Channel(final String name, final ChannelType type) {
        this.attributesMap.put("name", name);
        this.attributesMap.put("type", type.toString());
        this.attributesMap.put("orientation", OrientationType.POSITIVE.toString());
    }

    /**
     * Constructor that constructs a channel object with the mandatory 'name' attribute. For all other attributes the default values will be assumed.
     * 
     * @param name name of the Channel.
     */
    public Channel(final String name) {
        this.attributesMap.put("name", name);
    }

    /**
     * Method to check if the Channel is Intermittent.
     * 
     * @return boolean value that defines the type of the channel.
     */
    public boolean isIntermittent() {
        return this.isIntermittent;
    }

    /**
     * Method to get the 'name' of the channel.
     * 
     * @return the Channel Name.
     */
    public String getName() {
        final String value = this.attributesMap.get("name");
        return null == value ? "" : value;
    }

    /**
     * Method to get Channel Type.
     * 
     * @return the Channel Type.
     */
    public ChannelType getChannelType() {
        final String value = this.attributesMap.get("type");
        if (null != value) {
            final ChannelType type = ChannelType.valueOf(value.toUpperCase());
            return type;
        }
        return ChannelType.DECIMAL;
    }

    /**
     * Method to get Channel Unit.
     * 
     * @return the Channel unit.
     */
    public String getUnits() {
        final String value = this.attributesMap.get("units");
        return null == value ? "" : value;
    }

    /**
     * Method to give default channel value. It set for by 'intermittend'.
     * 
     * @return the default value of the Channel.
     */
    public String getDefaultValue() {
        final String value = this.attributesMap.get("default");
        if (null == value) {
            if (this.getChannelType() == ChannelType.DECIMAL || this.getChannelType() == ChannelType.INTEGER) {
                return "0";
            } else {
                // boolean channel
                return "F";
            }
        }
        return value;
    }

    /**
     * Method to get the ID attribute of the Channel object.
     * 
     * @return the ID attribute of the channel.
     */
    @Override
    public String getId() {
        final String value = this.attributesMap.get("id");
        return null == value ? "" : value;
    }

    /**
     * Method to get the Orientation value of the Channel object.
     * 
     * @return the OrientationType attribute of the channel.
     */
    public OrientationType getOrientation() {
        final String value = this.attributesMap.get("orientation");
        if (null != value) {
            try {
                final OrientationType type = OrientationType.valueOf(value);
                return type;
            } catch (final Exception exp) {
                Channel.logger.severe("The 'orientation' attribute of channel " + this.getName() + " is invalid. Given value is " + value);
            }
        }
        return OrientationType.POSITIVE;
    }

    /**
     * Method to get the Minimum Channel dimension. If unspecified means - that the channel is unbounded in that direction.
     * 
     * @return min attribute of the channel.
     */
    public String getMin() {
        final String value = this.attributesMap.get("min");
        return null == value ? "" : value;
    }

    /**
     * Method to get the Maximum Channel dimension. If unspecified means - that the channel is unbounded in that direction.
     * 
     * @return max attribute of the channel.
     */
    public String getMax() {
        final String value = this.attributesMap.get("max");
        return null == value ? "" : value;
    }

    /**
     * Method to know the type of InkML entity, in other words the Ink Element.
     * 
     * @return the string value representing the type of the Ink element. It is nothing but the class name.
     */
    @Override
    public String getInkElementType() {
        return "Channel";
    }

    /**
     * Method to get the 'respectTo' attribute value which Specifies that the values are relative to another reference point..
     * 
     * @return String URI String
     */
    public String getRespectTo() {
        final String value = this.attributesMap.get("respectTo");
        return null == value ? "" : value;
    }

    /**
     * Method to set value for the 'respectTo' attribute which Specifies that the values are relative to another reference point.
     * 
     * @param respectTo URI
     */
    public void setRespectTo(final URI respectTo) {
        if (null != respectTo) {
            this.attributesMap.put("respectTo", respectTo.getURIString());
        }
    }

    /**
     * Method to set value for the 'respectTo' attribute which Specifies that the values are relative to another reference point.
     * 
     * @param respectTo String
     */

    public void setRespectTo(final String respectTo) {
        if (null != respectTo && !"".equals(respectTo)) {
            // Have to check validity of string parameter if its a valid URIString
            this.attributesMap.put("respectTo", respectTo);
        }
    }

    /**
     * Method to set 'type' attribute. "integer" or "decimal" or "boolean" are the possible values.
     * 
     * @param channelType Enumeration of one of the above 3 possible values.
     */
    public void setChannelType(final ChannelType channelType) {
        if (null != channelType) {
            this.attributesMap.put("type", channelType.toString());
        }
    }

    /**
     * Method to set 'type' attribute. "integer" or "decimal" or "boolean" are the possible values.
     * 
     * @param channelType String of one of the above 3 possible values.
     */
    public void setChannelType(final String channelType) {
        if (null != channelType && !"".equals(channelType)) {
            this.attributesMap.put("type", channelType.toUpperCase());
        }
    }

    /**
     * Method to set vale for 'defaultValue' attribute, required for intermittent channels.
     * 
     * @param defaultValue Object as per the type of the channel
     */
    public void setDefaultValue(final Object defaultValue) {
        if (null != defaultValue) {
            this.attributesMap.put("default", defaultValue.toString());
        }
    }

    /**
     * Method to set vale for 'defaultValue' attribute, required for intermittent channels.
     * 
     * @param defaultValue value as String for the channel
     */
    public void setDefaultValue(final String defaultValue) {
        if (null != defaultValue && !"".equals(defaultValue)) {
            this.attributesMap.put("default", defaultValue);
        }
    }

    /**
     * Method to set 'id' attribute
     * 
     * @param id
     */
    public void setId(final String id) {
        if (null != id && !"".equals(id)) {
            this.attributesMap.put("id", id);
        }
    }

    /**
     * Method to set if the channel one of the is {@code <intermittentChannels>}
     * 
     * @param isIntermittent status of the kind of channel, false means 'regular channel' and 'true' means 'intermittentChannel'
     */
    public void setIntermittent(final boolean isIntermittent) {
        this.isIntermittent = isIntermittent;
    }

    /**
     * Method to set the 'max' attribute, the upper boundary for the channel value space.
     * 
     * @param max
     */
    public void setMax(final String max) {
        if (null != max && !"".equals(max)) {
            this.attributesMap.put("max", max);
        }
    }

    /**
     * Method to set the 'min' attribute, the lower boundary for the channel value space.
     * 
     * @param min
     */
    public void setMin(final String min) {
        if (null != min && !"".equals(min)) {
            this.attributesMap.put("min", min);
        }
    }

    /**
     * Method to set the 'name' attribute of the channel, it is a required attribute. The InkML specification is having a set of reserved channel names,
     * semantics of which must be preserved.
     * 
     * @param name
     */
    public void setName(final String name) {
        if (null != name && !"".equals(name)) {
            this.attributesMap.put("name", name);
        }
    }

    /**
     * Method to set 'orientation' attribute (+ve or -ve ; the default is "-ve").
     * 
     * @param orientation
     * @see OrientationType
     */
    public void setOrientation(final OrientationType orientation) {
        if (null != orientation) {
            this.attributesMap.put("orientation", orientation.toString());
        }
    }

    /**
     * Method to set the Units of the channel data
     * 
     * @param units
     */
    public void setUnits(final String units) {
        if (null != units && !"".equals(units)) {
            this.attributesMap.put("units", units);
        }
    }

    /**
     * Overriden equals method to compare channel objects
     * 
     * @param channel object to be compared with this channel object
     * @return status of equality
     */
    public boolean equals(final Channel channel) {
        if (channel == null) {
            return false;
        }
        final boolean isEqual = true;
        if (!this.getName().equalsIgnoreCase(channel.getName())) {
            return false;
        }
        if (this.getChannelType() != channel.getChannelType()) {
            return false;
        }
        if (this.getOrientation() != channel.getOrientation()) {
            return false;
        }
        if (this.isIntermittent() != channel.isIntermittent()) {
            return false;
        }
        if (!this.getRespectTo().equals(channel.getRespectTo())) {
            return false;
        }
        if (!this.getDefaultValue().equals(channel.getDefaultValue())) {
            return false;
        }
        if (!this.getMin().equals(channel.getMin())) {
            return false;
        }
        if (!this.getMax().equals(channel.getMax())) {
            return false;
        }
        if (!this.getUnits().equals(channel.getUnits())) {
            return false;
        }
        return isEqual;
    }

    /**
     * Method to give the markup string data of the channel data object
     * 
     * @return String markup string
     */
    @Override
    public String toInkML() {
        String channelElement = "<channel ";
        final String name = this.getName();
        if (!"".equals(name)) {
            channelElement += "name='" + name + "' ";
        }
        final String id = this.getId();
        if (!"".equals(id)) {
            channelElement += "id='" + id + "' ";
        }
        final String min = this.getMin();
        if (!"".equals(min)) {
            channelElement += "min='" + min + "' ";
        }
        final String max = this.getMax();
        if (!"".equals(max)) {
            channelElement += "max='" + max + "' ";
        }
        final String units = this.getUnits();
        if (!"".equals(units)) {
            channelElement += "units='" + units + "' ";
        }
        final String respectTo = this.getRespectTo();
        if (!"".endsWith(respectTo)) {
            channelElement += "respectTo='" + respectTo + "' ";
        }
        final String defaultValue = this.getDefaultValue();
        if (!"".equals(defaultValue)) {
            channelElement += "defaultValue='" + defaultValue + "' ";
        }

        ChannelType type;
        type = this.getChannelType();
        if (null != type) {
            channelElement += "type='" + type.toString() + "' ";
        }

        final OrientationType orientation = this.getOrientation();
        if (null != orientation) {
            channelElement += "orientation='" + orientation.toString() + "' ";
        }
        channelElement += "/>";
        return channelElement;
    }

    /**
     * Method used by the parser while binding the attributes data to this data object.
     * 
     * @param attrName name of the attribute
     * @param attrValue vale of the attribute as string from the markup data
     * @throws InkMLException
     */
    public void setAttribute(final String attrName, final String attrValue) throws InkMLException {
        Channel.logger.info("adding Channel attribute " + attrName + " = " + attrValue);
        if (attrName.equals("type")) {
            try {
                ChannelType.valueOf(attrValue.toUpperCase());
            } catch (final Exception exp) {
                throw new InkMLException("The type attribute of channel " + this.getName() + " is invalid. Given value is " + attrValue);
            }
        }
        this.attributesMap.put(attrName, attrValue);
    }

    /**
     * Method used by the Archiver component (InkMLWriter) to save the markup data of the channel data object to file or other data stream
     */
    @Override
    public void writeXML(final InkMLWriter writer) {
        writer.writeEmptyStartTag("channel", this.attributesMap);
    }

    /**
     * Method to get the map of all defined attributes of the channel data object
     * 
     * @return the attributesMap
     */
    HashMap<String, String> getAttributesMap() {
        return this.attributesMap;
    }
}
