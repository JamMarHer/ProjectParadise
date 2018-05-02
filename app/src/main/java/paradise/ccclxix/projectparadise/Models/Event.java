/*
 * Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package paradise.ccclxix.projectparadise.Models;

import java.util.*;

public class Event {
    @com.google.gson.annotations.SerializedName("status")
    private Integer status = null;
    @com.google.gson.annotations.SerializedName("name")
    private String name = null;
    @com.google.gson.annotations.SerializedName("host")
    private String host = null;
    @com.google.gson.annotations.SerializedName("eventID")
    private String eventID = null;
    @com.google.gson.annotations.SerializedName("privacy")
    private String privacy = null;
    @com.google.gson.annotations.SerializedName("latitude")
    private String latitude = null;
    @com.google.gson.annotations.SerializedName("longitude")
    private String longitude = null;
    @com.google.gson.annotations.SerializedName("active")
    private String active = null;
    @com.google.gson.annotations.SerializedName("ageTarget")
    private String ageTarget = null;
    @com.google.gson.annotations.SerializedName("attending")
    private List<EventAttendingItem> attending = null;
    @com.google.gson.annotations.SerializedName("attended")
    private List<EventAttendedItem> attended = null;


    public Event(HashMap<String, String> initialEventInfo){
        this.name = initialEventInfo.get("name_event");
        this.host = initialEventInfo.get("host");
        this.privacy = initialEventInfo.get("privacy");
        this.latitude = initialEventInfo.get("latitude");
        this.longitude = initialEventInfo.get("longitude");
        this.ageTarget = initialEventInfo.get("age_target");
        this.active = "true";
        this.eventID = initialEventInfo.get("event_id");

        EventAttendingItem eventAttendingItem = new EventAttendingItem();
        eventAttendingItem.setIn(System.currentTimeMillis());
        eventAttendingItem.setUsername(this.host);
        List<EventAttendingItem> attendingItems = new ArrayList<>();
        attendingItems.add(eventAttendingItem);
        List<EventAttendedItem> attendedItems = new ArrayList<>();
        attendedItems.add(new EventAttendedItem());
        this.setAttending(attendingItems);
        this.setAttended(attendedItems);
    }

    /**
     * Gets status
     *
     * @return status
     **/
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets the value of status.
     *
     * @param status the new value
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * Gets name
     *
     * @return name
     **/
    public String getName() {
        return name;
    }

    /**
     * Sets the value of name.
     *
     * @param name the new value
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets host
     *
     * @return host
     **/
    public String getHost() {
        return host;
    }

    /**
     * Sets the value of host.
     *
     * @param host the new value
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets eventID
     *
     * @return eventID
     **/
    public String getEventID() {
        return eventID;
    }

    /**
     * Sets the value of eventID.
     *
     * @param eventID the new value
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Gets privacy
     *
     * @return privacy
     **/
    public String getPrivacy() {
        return privacy;
    }

    /**
     * Sets the value of privacy.
     *
     * @param privacy the new value
     */
    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    /**
     * Gets latitude
     *
     * @return latitude
     **/
    public String getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of latitude.
     *
     * @param latitude the new value
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets longitude
     *
     * @return longitude
     **/
    public String getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of longitude.
     *
     * @param longitude the new value
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets active
     *
     * @return active
     **/
    public String getActive() {
        return active;
    }

    /**
     * Sets the value of active.
     *
     * @param active the new value
     */
    public void setActive(String active) {
        this.active = active;
    }

    /**
     * Gets ageTarget
     *
     * @return ageTarget
     **/
    public String getAgeTarget() {
        return ageTarget;
    }

    /**
     * Sets the value of ageTarget.
     *
     * @param ageTarget the new value
     */
    public void setAgeTarget(String ageTarget) {
        this.ageTarget = ageTarget;
    }

    /**
     * Gets attending
     *
     * @return attending
     **/
    public List<EventAttendingItem> getAttending() {
        return attending;
    }

    /**
     * Sets the value of attending.
     *
     * @param attending the new value
     */
    public void setAttending(List<EventAttendingItem> attending) {
        this.attending = attending;
    }

    /**
     * Gets attended
     *
     * @return attended
     **/
    public List<EventAttendedItem> getAttended() {
        return attended;
    }

    /**
     * Sets the value of attended.
     *
     * @param attended the new value
     */
    public void setAttended(List<EventAttendedItem> attended) {
        this.attended = attended;
    }

}
