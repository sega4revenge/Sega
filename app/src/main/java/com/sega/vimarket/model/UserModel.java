package com.sega.vimarket.model;

import com.google.firebase.database.Exclude;

/**a
 * Created by Sega on 04/01/2017.
 */

public class UserModel {

    private String id;
    private String name;
    private String photo_profile;
    public UserModel(){

    }


    public UserModel(String name, String photo_profile, String id) {
        this.name = name;
        this.photo_profile = photo_profile;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto_profile() {
        return photo_profile;
    }



    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
