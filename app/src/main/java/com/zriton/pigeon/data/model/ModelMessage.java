package com.zriton.pigeon.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by aditya on 24/10/16.
 */

public class ModelMessage implements Parcelable,Comparator<ModelMessage> {


    public String getAddress() {
        return address;
    }

    public void setAddress(String pAddress) {
        address = pAddress;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String pBody) {
        body = pBody;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String pDate) {
        date = pDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long pTimestamp) {
        timestamp = pTimestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String pType) {
        type = pType;
    }

    private String address;
    private String body;
    private long timestamp;
    private String date;



    private String type;

    @Override
    public int compare(ModelMessage pModelMessage, ModelMessage pT1) {
        return (int) (pModelMessage.getTimestamp()-pT1.getTimestamp());
    }

    @Override
    public boolean equals(Object obj) {
        ModelMessage lModelMessage = (ModelMessage) obj;
        return lModelMessage.getAddress().equals(this.getAddress());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pParcel, int pI) {

    }
}
