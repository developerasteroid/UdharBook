package com.example.udharbook;

public class CustomerDetails {
    long userID;
    String name, mobileNo, createdDate;
    int pending;
    public CustomerDetails(long userID, String name, String mobileNo, String createdDate, int pending){
        this.userID = userID;
        this.name = name;
        this.mobileNo = mobileNo;
        this.createdDate = createdDate;
        this.pending = pending;
    }
}
