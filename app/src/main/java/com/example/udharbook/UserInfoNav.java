package com.example.udharbook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoNav extends AppCompatActivity {

    TextView tvName, tvMobile, tvCreated, tvPending;
    Button addBtn, receiveBtn, transactionBtn;
    ImageView editBtn, deleteBtn;

    long userID;

    DBHelper db;

    boolean reCreateActivity = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_nav);
        tvName = findViewById(R.id.textView3);
        tvMobile = findViewById(R.id.textView4);
        tvCreated = findViewById(R.id.textView9);
        tvPending = findViewById(R.id.textView5);
        addBtn = findViewById(R.id.button5);
        receiveBtn = findViewById(R.id.button4);
        transactionBtn = findViewById(R.id.button2);
        editBtn = findViewById(R.id.imageView3);
        deleteBtn = findViewById(R.id.imageView2);

        db = new DBHelper(this);

        userID = getIntent().getLongExtra("userID", -1);

        if(userID == -1){
            tvName.setText("No Information Found");
            return;
        }

        init();






        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EditProfile.class);
                i.putExtra("userID", userID);
                startActivity(i);
            }
        });





        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddMoney.class);
                i.putExtra("userID", userID);
                startActivity(i);
            }
        });

        receiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ReceiveMoney.class);
                i.putExtra("userID", userID);
                startActivity(i);
            }
        });

        transactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), TransactionDetails.class);
                i.putExtra("userID", userID);
                startActivity(i);
            }
        });

    }

    public void init(){
        deleteBtn.setVisibility(View.GONE);
        //get and Display Customer Information
        CustomerDetails customerDetails = db.getCustomer(userID);
        if(customerDetails == null){
            tvName.setText("No Information Found");
            return;
        }
        tvName.setText("Name| "+customerDetails.name);
        tvMobile.setText("Mobile| "+customerDetails.mobileNo);
        tvCreated.setText("Created| "+customerDetails.createdDate);
        tvPending.setText("Pending| "+customerDetails.pending+"/-");

        if(customerDetails.pending == 0){
            deleteBtn.setVisibility(View.VISIBLE);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(UserInfoNav.this)
                            .setTitle("Delete User")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(db.deleteCustomer(userID)){
                                        finish();
                                    }else {
                                        Toast.makeText(UserInfoNav.this, "User not deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setIcon(R.drawable.baseline_delete_30)
                            .show();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!reCreateActivity){
            reCreateActivity = true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(reCreateActivity){
            init();
            reCreateActivity = false;
        }

    }


}