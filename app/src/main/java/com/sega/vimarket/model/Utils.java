/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sega.vimarket.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.NumberFormat;

/**
 * This class contains shared static utility methods that both the mobile and
 * wearable apps can use.
 */
public class Utils {

    private static final String DISTANCE_KM_POSTFIX = "km";
    private static final String DISTANCE_M_POSTFIX = "m";

    /**
     * Check if the app has access to fine location permission. On pre-M
     * devices this will always return true.
     */


    /**
     * Calculate distance between two LatLng points and format it nicely for
     * display. As this is a sample, it only statically supports metric units.
     * A production app should check locale and support the correct units.
     */
    public static String formatDistanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }
        int distanceint;
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        double distance = Math.round(SphericalUtil.computeDistanceBetween(point1, point2));

        // Adjust to KM if M goes over 1000 (see javadoc of method for note
        // on only supporting metric)
        if (distance >= 1000) {
            numberFormat.setMaximumFractionDigits(1);

            String dis = String.valueOf(distance / 1000);
            if(dis.length()>=6)
            {
              distanceint = (int) (distance /1000);
                return numberFormat.format(distanceint) + DISTANCE_KM_POSTFIX;}
            else


            return numberFormat.format(distance/1000) + DISTANCE_KM_POSTFIX;
        }
        return numberFormat.format(distance) + DISTANCE_M_POSTFIX;
    }

    /**
     * Store the location in the app preferences.
     */

    /**
     * Fetch the location from app preferences.
     */


    /**
     * Store if geofencing triggers will show a notification in app preferences.
     */


    /**
     * Retrieve if geofencing triggers should show a notification from app preferences.
     */


    /**
     * Convert an asset into a bitmap object synchronously. Only call this
     * method from a background thread (it should never be called from the
     * main/UI thread as it blocks).
     */


    /**
     * Create a wearable asset from a bitmap.
     */


    /**
     * Get a list of all wearable nodes that are connected synchronously.
     * Only call this method from a background thread (it should never be
     * called from the main/UI thread as it blocks).
     */


    /**
     * Calculates the square insets on a round device. If the system insets are not set
     * (set to 0) then the inner square of the circle is applied instead.
     *
     * @param display device default display
     * @param systemInsets the system insets
     * @return adjusted square insets for use on a round device
     */

}
