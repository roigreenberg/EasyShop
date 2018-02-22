package com.roigreenberg.easyshop.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import com.roigreenberg.easyshop.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;

/**
 * Created by Roi on 13/08/2017.
 */

public class ActionLog {
    private String userName;
    private String action;
    private String itemName;
    private HashMap<String, Object> timestamp;

    public ActionLog() {}

    public ActionLog(String userName, String action, String itemName) {
        this.userName = userName;
        this.action = action;
        this.itemName = itemName;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestamp = timestampNowObject;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public HashMap<String, Object> getTimestamp() {
        return timestamp;
    }

    @Exclude
    public long getTimestampLong() {

        return (long) timestamp.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
