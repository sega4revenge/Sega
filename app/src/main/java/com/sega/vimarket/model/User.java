package com.sega.vimarket.model;

/**
 * Created by Sega on 05/07/2016.
 */
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

import android.os.Parcel;
import android.os.Parcelable;

/**
 duong 12345
 */
public class User implements Parcelable{
    public int userid;
    public String username;
    public String email;
    public String phone;
    public String address;
    public String area;
    public String userpic;
    private String datecreate;
    public String rate;
    public String count;


    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public User(){

    }
    public User(int userid, String username, String email, String phone,
                String address, String area, String userpic, String datecreate,String rate,String count){
        this.userid = userid;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.area = area;
        this.userpic = userpic;
        this.datecreate = datecreate;
        this.rate = rate;
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userid);
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.address);
        dest.writeString(this.area);
        dest.writeString(this.userpic);
        dest.writeString(this.datecreate);
        dest.writeString(this.rate);
        dest.writeString(this.count);
    }

    private User(Parcel in) {
        this.userid = in.readInt();
        this.username = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.address = in.readString();
        this.area = in.readString();
        this.userpic = in.readString();
        this.datecreate = in.readString();
        this.rate = in.readString();
        this.count = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}