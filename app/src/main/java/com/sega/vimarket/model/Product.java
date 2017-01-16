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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
duong 12345
 */
public class Product implements Parcelable {
    public int productid,userid;
    public String productname;
    public String username;
    public String categoryname;
    public String productaddress;
    public String areaproduct;
    private String productstatus;
    public String productdate;
    public String description;
    public  String sharecount;
    public ArrayList<String> productimage = new ArrayList<>();
    public double price;
    public LatLng location;


    private Product(Parcel in) {
        productid = in.readInt();
        productname = in.readString();
        userid = in.readInt();
        username = in.readString();
        categoryname = in.readString();
        productaddress=in.readString();
        areaproduct = in.readString();
        productstatus = in.readString();
        productdate = in.readString();
      in.readStringList(productimage);
        price = in.readDouble();
        description=in.readString();
        sharecount=in.readString();
        location = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };



    public Product(int productid, String productname, double price, int userid, String username,
                   String categoryname, String productaddress, String areaproduct, String productstatus, ArrayList<String> productimage, String productdate, String description, String sharecount, double lat, double lot) {

        this.productid = productid;
        this.productname = productname;
        this.price = price;
        this.userid = userid;
        this.username = username;
        this.categoryname = categoryname;
        this.productaddress = productaddress;
        this.areaproduct = areaproduct;
        this.productstatus = productstatus;
        this.productimage = productimage;
        this.productdate = productdate;
        this.description = description;
        this.sharecount = sharecount;
        this.location = new LatLng(lat,lot);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(productid);
        parcel.writeString(productname);
        parcel.writeString(username);
        parcel.writeString(categoryname);
        parcel.writeString(productaddress);
        parcel.writeString(areaproduct);
        parcel.writeString(productstatus);
        parcel.writeString(productdate);
        parcel.writeStringList(productimage);
        parcel.writeDouble(price);
        parcel.writeString(description);
        parcel.writeString(sharecount);
        parcel.writeParcelable(location, i);
    }
}