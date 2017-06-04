package com.roigreenberg.easyshop;

/**
 * Created by Roi on 04/06/2017.
 */

public class List {

    private String mListID;
    private String mListName;

    public List() {
    }

    public List(String mListID, String mListName) {
        this.mListID = mListID;
        this.mListName = mListName;
    }

    public String getListID() {
        return mListID;
    }

    public void setListID(String mListID) {
        this.mListID = mListID;
    }

    public String getListName() {
        return mListName;
    }

    public void setListName(String mListName) {
        this.mListName = mListName;
    }
}
