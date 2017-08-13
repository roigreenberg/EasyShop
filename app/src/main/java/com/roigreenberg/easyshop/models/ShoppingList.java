package com.roigreenberg.easyshop.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.roigreenberg.easyshop.utils.Constants;

import java.util.HashMap;

/**
 * Created by Roi on 04/06/2017.
 */

public class ShoppingList {

    private String mListName;
    private HashMap<String, Log> logLastChanged;

    public ShoppingList() {
    }

    public ShoppingList(String mListName, HashMap<String, Log> logLastChanged) {
        this.mListName = mListName;
        this.logLastChanged = logLastChanged;
    }

    public String getListName() {
        return mListName;
    }

    public void setListName(String mListName) { this.mListName = mListName; }


    public HashMap<String, Log> getLogLastChanged() {
        return logLastChanged;
    }

   /* @JsonIgnore
    public Log setLogLastChangedLog() {

        return (Log) logLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    } */

    public void setLogLastChanged(HashMap<String, Log> logLastChanged) {
        this.logLastChanged = logLastChanged;
    }
}
