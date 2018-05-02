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

import java.math.BigDecimal;

public class EventAttendedItem {
    @com.google.gson.annotations.SerializedName("username")
    private String username = null;
    @com.google.gson.annotations.SerializedName("in")
    private BigDecimal in = null;
    @com.google.gson.annotations.SerializedName("out")
    private BigDecimal out = null;

    /**
     * Gets username
     *
     * @return username
     **/
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of username.
     *
     * @param username the new value
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets in
     *
     * @return in
     **/
    public BigDecimal getIn() {
        return in;
    }

    /**
     * Sets the value of in.
     *
     * @param in the new value
     */
    public void setIn(BigDecimal in) {
        this.in = in;
    }

    /**
     * Gets out
     *
     * @return out
     **/
    public BigDecimal getOut() {
        return out;
    }

    /**
     * Sets the value of out.
     *
     * @param out the new value
     */
    public void setOut(BigDecimal out) {
        this.out = out;
    }

}
