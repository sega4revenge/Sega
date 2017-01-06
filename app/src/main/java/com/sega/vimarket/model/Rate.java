package com.sega.vimarket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**a
 * Created by Sega on 08/09/2016.
 */
public class Rate implements Parcelable {
    public int userid;
    public int fivestar,fourstar,threestar,twostar,onestar;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userid);
        dest.writeInt(this.fivestar);
        dest.writeInt(this.fourstar);
        dest.writeInt(this.threestar);
        dest.writeInt(this.twostar);
        dest.writeInt(this.onestar);
    }

    public Rate() {
    }

    private Rate(Parcel in) {
        this.userid = in.readInt();
        this.fivestar = in.readInt();
        this.fourstar = in.readInt();
        this.threestar = in.readInt();
        this.twostar = in.readInt();
        this.onestar = in.readInt();
    }

    public static final Parcelable.Creator<Rate> CREATOR = new Parcelable.Creator<Rate>() {
        @Override
        public Rate createFromParcel(Parcel source) {
            return new Rate(source);
        }

        @Override
        public Rate[] newArray(int size) {
            return new Rate[size];
        }
    };
}
