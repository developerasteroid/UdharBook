package com.example.udharbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditProfile extends AppCompatActivity {
    EditText edName, edMobile;
    Button saveBtn;
    TextView errMsg;
    long userID;
    String previousName, previousMobileNo;
    DBHelper db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edName = findViewById(R.id.editTextText);
        edMobile = findViewById(R.id.editTextNumber);
        saveBtn = findViewById(R.id.button);
        errMsg = findViewById(R.id.textView10);

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
        previousName = customerDetails.name;
        previousMobileNo = customerDetails.mobileNo;
        edName.setText(previousName);
        edName.setSelection(edName.getText().length());
        edMobile.setText(previousMobileNo);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errMsg.setVisibility(View.GONE);
                String name = edName.getText().toString();
                String mobile = edMobile.getText().toString();
                if (name.isEmpty()) {
                    errMsg.setText("Name field cannot be empty.");
                    errMsg.setVisibility(View.VISIBLE);
                    edName.requestFocus();
                } else if (name.startsWith(" ")) {
                    errMsg.setText("Name cannot start with space. Try again");
                    errMsg.setVisibility(View.VISIBLE);
                    edName.setText(name.trim());
                    edName.requestFocus();
                } else if (name.endsWith(" ")) {
                    errMsg.setText("Name cannot end with space. Try again");
                    errMsg.setVisibility(View.VISIBLE);
                    edName.setText(name.trim());
                    edName.setSelection(edName.getText().length());
                    edName.requestFocus();
                } else if (db.userExistExceptUserID(userID, name)) {
                    errMsg.setText(name+" already exists");
                    errMsg.setVisibility(View.VISIBLE);
                } else {
                    if(db.updateCustomerData(userID, name, mobile)){
                        finish();
                    } else {
                        Toast.makeText(EditProfile.this, "Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}