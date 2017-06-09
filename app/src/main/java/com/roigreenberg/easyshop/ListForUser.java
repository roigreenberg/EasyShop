package com.roigreenberg.easyshop;

/**
 * Created by Roi on 09/06/2017.
 */

public class ListForUser {

    private String mListID;

    public ListForUser(String mListID) {

        this.mListID = mListID;

    }

    public ListForUser() {
    }

    public String getListID() {
        return mListID;
    }

    public void setListID(String mListID) {
        this.mListID = mListID;
    }

}
