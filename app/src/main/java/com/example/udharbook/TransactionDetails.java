package com.example.udharbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class TransactionDetails extends AppCompatActivity {

    TextView tvName, tvMobile, tvPending, tvNoTransData;
    RecyclerView transactionRecyclerView;
    ArrayList<TransactionModel> arrayTransactions = new ArrayList<>();
    long userID;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        tvName = findViewById(R.id.textView3);
        tvMobile = findViewById(R.id.textView4);
        tvPending = findViewById(R.id.textView5);
        tvNoTransData =findViewById(R.id.textView13);
        tvNoTransData.setVisibility(View.GONE);
        transactionRecyclerView = findViewById(R.id.transactionListRecyclerView);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new DBHelper(this);
        userID = getIntent().getLongExtra("userID", -1);
        if(userID == -1){
            finish();
            return;
        }
        CustomerDetails customerDetails = db.getCustomer(userID);
        if(customerDetails == null){
            finish();
            return;
        }

        tvName.setText("Name| "+customerDetails.name);
        tvMobile.setText("Mobile| "+customerDetails.mobileNo);
        tvPending.setText("Pending| "+customerDetails.pending+"/-");



        arrayTransactions = db.getTransactionDetails(userID);
        if(arrayTransactions.size()==0){
            tvNoTransData.setVisibility(View.VISIBLE);
        } else {
            RecyclerTransactionAdapter adapter = new RecyclerTransactionAdapter(this, arrayTransactions);

            transactionRecyclerView.setAdapter(adapter);
        }





    }
}