package com.sega.vimarket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sega on 10/01/2017.
 */


public class Room implements Parcelable {
    public String room,username,time,userpic;

    public Room(String room,String username,String userpic) {
        this.room = room;
        this.username=username;
        this.userpic=userpic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.room);
        dest.writeString(this.username);



        dest.writeString(this.userpic);

    }

    public Room(Parcel in) {

        this.username = in.readString();
        this.room = in.readString();


        this.userpic = in.readString();

    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel source) {
            return new Room(source);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };
}
