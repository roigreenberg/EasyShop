package com.roigreenberg.easyshop.models;

/**
 * Created by Roi on 09/06/2017.
 */

public class ItemInList {

    private String mItemID;
    private String mQuantity;
    private String mState;
    private String mAssignee;

    private String mName;

    public ItemInList(String mItemID, String mQuantity, String mState, String mAssignee, String mName) {
        this.mItemID = mItemID;
        this.mQuantity = mQuantity;
        this.mState = mState;
        this.mAssignee = mAssignee;
        this.mName = mName;
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

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

}
