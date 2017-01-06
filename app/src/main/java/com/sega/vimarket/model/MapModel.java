package com.sega.vimarket.model;

/**a
 * Created by Sega on 04/01/2017.
 */

public class MapModel {
    private String latitude;
    private String longitude;
    public MapModel(){

    }
    public MapModel(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }


    public String getLongitude() {
        return longitude;
    }


}
