package com.roigreenberg.easyshop;

/**
 * Created by Roi on 02/06/2017.
 */



public class Item {

    private String mID;
    private String mName;
    private String mBrand;
    private String mWeight;
    private String mVolume;
    private String mBarcode;
    private String[] mImage;

    private static final int MAX_IMAGES_QUANTITY = 5;

    public Item(String mID, String mName, String mBrand, String mWeight, String mVolume, String mBarcode, String[] mImage) {
        this.mID = mID;
        this.mName = mName;
        this.mBrand = mBrand;
        this.mWeight = mWeight;
        this.mVolume = mVolume;
        this.mBarcode = mBarcode;
        this.mImage = mImage;
    }

    public String getID() {
        return mID;
    }

    public void setID(String mID) {
        this.mID = mID;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getBrand() {
        return mBrand;
    }

    public void setBrand(String mBrand) {
        this.mBrand = mBrand;
    }

    public String getWeight() {
        return mWeight;
    }

    public void setWeight(String mWeight) {
        this.mWeight = mWeight;
    }

    public String getVolume() {
        return mVolume;
    }

    public void setVolume(String mVolume) {
        this.mVolume = mVolume;
    }

    public String getBarcode() {
        return mBarcode;
    }

    public void setBarcode(String mBarcode) {
        this.mBarcode = mBarcode;
    }

    public String[] getImage() {
        return mImage;
    }

    public void setImage(String[] mImage) {
        this.mImage = mImage;
    }
}
