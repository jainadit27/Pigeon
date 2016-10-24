package com.zriton.pigeon.data.model;

import android.util.Log;

/**
 * Created by aditya on 24/10/16.
 */

public class ModelMessage implements Comparable {


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
    public boolean equals(Object obj) {
        ModelMessage lModelMessage = (ModelMessage) obj;
        return lModelMessage.getAddress().equals(this.getAddress());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public int compareTo(Object pO) {
        ModelMessage lModelMessage = (ModelMessage) pO;
        long diff =  this.getTimestamp() - lModelMessage.getTimestamp();
        Log.d("asdd",String.valueOf(diff));
        return (int) (this.getTimestamp() - lModelMessage.getTimestamp());
    }
}
