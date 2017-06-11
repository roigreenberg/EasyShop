package com.roigreenberg.easyshop;

/**
 * Created by Roi on 09/06/2017.
 */

public class ItemInList {

    private String mItemID;
    private String mQuantity;
    private String mState;
    private String mAssignee;

    public ItemInList(String mItemID, String mQuantity, String mState, String mAssignee) {
        this.mItemID = mItemID;
        this.mQuantity = mQuantity;
        this.mState = mState;
        this.mAssignee = mAssignee;
    }

    public ItemInList() {
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

    public String getState() {
        return mState;
    }

    public void setState(String mState) {
        this.mState = mState;
    }

    public String getAssignee() {
        return mAssignee;
    }

    public void setAssignee(String mAsignee) {
        this.mAssignee = mAsignee;
    }
}
