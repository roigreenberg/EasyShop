package com.roigreenberg.easyshop.models;

/**
 * Created by Roi on 13/08/2017.
 */

public class Users {
    String userEmail;
    String userName;

    public Users() {
    }

    public Users(String userEmail, String userName) {
        this.userEmail = userEmail;
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
