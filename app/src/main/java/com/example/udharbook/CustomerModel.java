package com.example.udharbook;

public class CustomerModel {
    String name, mobileNo;
    int pending, userID;



    CustomerModel(int userID, String name, String mobileNo, int pending){
        this.userID = userID;
        this.name = name;
        this.mobileNo = mobileNo;
        this.pending = pending;
    }
}
