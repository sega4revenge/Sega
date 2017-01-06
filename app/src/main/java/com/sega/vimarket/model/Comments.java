package com.sega.vimarket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**a
 * Created by Sega on 8/3/2016.
 */
public class Comments implements Parcelable {
    public String userid,username,productid,time,contentcomment,userpic,rate;

    public Comments(String userid,String username,String productid,String time,String contentcomment,String userpic,String rate) {
        this.userid=userid;
        this.username=username;
        this.productid=productid;
        this.time=time;
        this.contentcomment=contentcomment;
        this.userpic=userpic;
        this.rate=rate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userid);
        dest.writeString(this.username);
        dest.writeString(this.productid);
        dest.writeString(this.time);
        dest.writeString(this.contentcomment);
        dest.writeString(this.userpic);
        dest.writeString(this.rate);
    }

    private Comments(Parcel in) {
        this.userid = in.readString();
        this.username = in.readString();
        this.productid = in.readString();
        this.time = in.readString();
        this.contentcomment = in.readString();
        this.userpic = in.readString();
        this.rate = in.readString();
    }

    public static final Creator<Comments> CREATOR = new Creator<Comments>() {
        @Override
        public Comments createFromParcel(Parcel source) {
            return new Comments(source);
        }

        @Override
        public Comments[] newArray(int size) {
            return new Comments[size];
        }
    };
}
