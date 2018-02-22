package com.roigreenberg.easyshop.models;

import java.util.HashMap;

/**
 * Created by Roi on 04/06/2017.
 */

public class ShoppingList {

    private String mListName;
    private HashMap<String, Object> logLastChanged;

    public ShoppingList() {
    }

    public ShoppingList(String mListName, HashMap<String, Object> logLastChanged) {
        this.mListName = mListName;
        this.logLastChanged = logLastChanged;
    }

    public String getListName() {
        return mListName;
    }

    public void setListName(String mListName) { this.mListName = mListName; }


    public HashMap<String, Object> getLogLastChanged() {
        return logLastChanged;
    }

   /* @JsonIgnore
    public ActionLog setLogLastChangedLog() {

        return (ActionLog) logLastChanged.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    } */

    public void setLogLastChanged(HashMap<String, Object> logLastChanged) {
        this.logLastChanged = logLastChanged;
    }
}
