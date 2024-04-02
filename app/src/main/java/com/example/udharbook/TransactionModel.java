package com.example.udharbook;

public class TransactionModel {
    String date, method;
    int amount, pending;

    public TransactionModel(int amount, String date, String method, int pending){
        this.amount = amount;
        this.date = date;
        this.method = method;
        this.pending = pending;
    }
}
