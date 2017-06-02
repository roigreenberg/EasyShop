package com.roigreenberg.easyshop;

/**
 * Created by Roi on 02/06/2017.
 */

public class ListItem {

    private String mItemID;
    private String mQuantity;

    public ListItem(String mItemID, String mQuantity) {
        this.mItemID = mItemID;
        this.mQuantity = mQuantity;
    }

    public String getItemID() {
        return mItemID;
    }

    public void setItemID(String mItemID) {
        this.mItemID = mItemID;
    }

    public String getQuantity() {
        return mQuantity;
    }

    public void setQuantity(String mQuantity) {
        this.mQuantity = mQuantity;
    }
}
