package com.example.udharbook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ReceiveMoney extends AppCompatActivity {

    TextView tvName, tvMobile, tvPending, errMsg;

    TextView tvEdDate;
    EditText edAmount;
    Button receiveBtn;
    long userID;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_money);

        tvName = findViewById(R.id.textView3);
        tvMobile = findViewById(R.id.textView4);
        tvPending = findViewById(R.id.textView5);

        tvEdDate = findViewById(R.id.textView6);
        edAmount = findViewById(R.id.editTextNumber2);
        receiveBtn = findViewById(R.id.button3);
        errMsg = findViewById(R.id.textView12);

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



        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        tvEdDate.setText(day+"/"+(month+1)+"/"+year);
        tvEdDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ReceiveMoney.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                tvEdDate.setText(i2+"/"+(i1+1)+"/"+i);
                            }
                        },year, month, day);
                datePickerDialog.show();
            }
        });

        receiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errMsg.setVisibility(View.GONE);
                int amount;
                if(edAmount.getText().toString().isEmpty()){
                    errMsg.setText("Amount cannot be empty.");
                    errMsg.setVisibility(View.VISIBLE);
                    edAmount.requestFocus();
                } else if (!TextUtils.isDigitsOnly(edAmount.getText().toString())) {
                    errMsg.setText("Amount Should only contain number.");
                    errMsg.setVisibility(View.VISIBLE);
                    edAmount.requestFocus();
                } else {
                    amount = Integer.parseInt(edAmount.getText().toString());

                    if(amount<=0){
                        errMsg.setText("Amount cannot be 0.");
                        errMsg.setVisibility(View.VISIBLE);
                        edAmount.requestFocus();
                    } else if(amount > customerDetails.pending){
                        new AlertDialog.Builder(ReceiveMoney.this)
                                .setTitle("Received Amount is More then Pending")
                                .setMessage("Continue anyway?")
                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        updateAmount(userID, customerDetails, amount);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                    } else {
                        updateAmount(userID, customerDetails, amount);
                    }

                }
            }

        });

    }

    public void updateAmount(long userID, CustomerDetails customerDetails, int amount){
        if(db.updateAmount(userID, customerDetails.pending - amount)){
            if(!db.addTransaction(userID, amount, "received", tvEdDate.getText().toString(), null, customerDetails.pending - amount)){
                db.errorReport("Received Transaction not updated of "+customerDetails.name+" -> "+userID);
                Toast.makeText(ReceiveMoney.this, "Amount Updated\nTransaction details was not Updated", Toast.LENGTH_LONG).show();
            }
            finish();
        } else {
            db.errorReport("Received amount not updated of "+customerDetails.name+" -> "+userID);
            errMsg.setText("Amount not Updated. Try again");
            errMsg.setVisibility(View.VISIBLE);
        }
    }
}