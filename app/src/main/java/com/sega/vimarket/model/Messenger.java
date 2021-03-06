package com.sega.vimarket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sega on 09/01/2017.
 */


public class Messenger implements Parcelable {
    public static final Creator<Messenger> CREATOR = new Creator<Messenger>() {
        @Override
        public Messenger createFromParcel(Parcel source) {
            return new Messenger(source);
        }

        @Override
        public Messenger[] newArray(int size) {
            return new Messenger[size];
        }
    };
    public String username,time,contentcomment,userpic;

    public Messenger(String username,String time,String contentcomment,String userpic) {

        this.username=username;
        this.time=time;
        this.contentcomment=contentcomment;
        this.userpic=userpic;
    }

    private Messenger(Parcel in) {

        this.username = in.readString();

        this.time = in.readString();
        this.contentcomment = in.readString();
        this.userpic = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.username);

        dest.writeString(this.time);
        dest.writeString(this.contentcomment);
        dest.writeString(this.userpic);

    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        }
        else {
            Messenger employee = (Messenger) object;
            if (this.username.equals(employee.username) && this.userpic.equals(employee.userpic)) {
                result = true;
            }
        }
        return result;
    }
}
