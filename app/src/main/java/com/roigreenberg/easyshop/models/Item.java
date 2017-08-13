package com.roigreenberg.easyshop.models;

/**
 * Created by Roi on 02/06/2017.
 */



public class Item {

    private String ID;
    private String name;
    private String brand;
    private String weight;
    private String volume;
    private String barcode;
    private String category;
    private String quantity;
    private String state;
    private String assignee;
    private String[] mImage;


    private static final int MAX_IMAGES_QUANTITY = 5;

    public Item() {
    }

    public Item(String ID, String name, String brand, String weight, String volume, String barcode, String category, String quantity, String state, String assignee, String[] mImage) {
        this.ID = ID;
        this.name = name;
        this.brand = brand;
        this.weight = weight;
        this.volume = volume;
        this.barcode = barcode;
        this.category = category;
        this.quantity = quantity;
        this.state = state;
        this.assignee = assignee;
        this.mImage = mImage;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        this.name = mName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String mVolume) {
        this.volume = mVolume;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String mBarcode) {
        this.barcode = mBarcode;
    }

    public String[] getImage() {
        return mImage;
    }

    public void setImage(String[] mImage) {
        this.mImage = mImage;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
}
