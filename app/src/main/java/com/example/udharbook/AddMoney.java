package com.example.udharbook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class AddMoney extends AppCompatActivity {

    TextView tvName, tvMobile, tvPending, errMsg;

    TextView tvEdDate;
    EditText edAmount;
    Button addBtn;
    long userID;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        tvName = findViewById(R.id.textView3);
        tvMobile = findViewById(R.id.textView4);
        tvPending = findViewById(R.id.textView5);

        tvEdDate = findViewById(R.id.textView6);
        edAmount = findViewById(R.id.editTextNumber2);
        addBtn = findViewById(R.id.button3);
        errMsg = findViewById(R.id.textView11);

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
                        AddMoney.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                tvEdDate.setText(i2+"/"+(i1+1)+"/"+i);
                            }
                        },year, month, day);
                datePickerDialog.show();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
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
                    } else {
                        if(db.updateAmount(userID, customerDetails.pending + amount)){
                            if(!db.addTransaction(userID, amount, "add", tvEdDate.getText().toString(), null, customerDetails.pending + amount)){
                                db.errorReport("ADD Transaction not updated of "+customerDetails.name+" -> "+userID);
                                Toast.makeText(AddMoney.this, "Amount Updated\nTransaction details was not Updated", Toast.LENGTH_LONG).show();
                            }
                            finish();
                        } else {
                            db.errorReport("ADD amount not updated of "+customerDetails.name+" -> "+userID);
                            errMsg.setText("Amount not Updated. Try again");
                            errMsg.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

    }
}