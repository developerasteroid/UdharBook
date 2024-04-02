package com.example.udharbook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddNew extends AppCompatActivity {
    EditText edName, edMobile;
    TextView errName, errMobile;
    Button createBtn;

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        edName = findViewById(R.id.editTextText);
        edMobile = findViewById(R.id.editTextNumber);
        errName = findViewById(R.id.textViewNameError);
        errMobile = findViewById(R.id.textViewMobileError);
        createBtn = findViewById(R.id.button);

        db = new DBHelper(this);

        int maxUser = db.getMaxUser();
        int currentUsers = db.getCurrentUsers();
        if(currentUsers>=maxUser){

            return;
        }

        int userCountLeft = maxUser - currentUsers;
        if(userCountLeft <= 5){
            new AlertDialog.Builder(AddNew.this)
                    .setTitle("Only "+userCountLeft+" slot left?")
                    .setMessage("Click yes to increase limit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getApplicationContext(), ExtendUserLimit.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }



        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edName.getText().toString();
                String mobile = edMobile.getText().toString();
                errName.setVisibility(View.GONE);
                errMobile.setVisibility(View.GONE);
                if(name.isEmpty()){
                    errName.setText("Name field cannot be empty.");
                    errName.setVisibility(View.VISIBLE);
                    edName.requestFocus();
                } else if (name.startsWith(" ")) {
                    errName.setText("Name cannot start with space. Try again");
                    errName.setVisibility(View.VISIBLE);
                    edName.setText(name.trim());
                    edName.requestFocus();
                } else if (name.endsWith(" ")) {
                    errName.setText("Name cannot end with space. Try again");
                    errName.setVisibility(View.VISIBLE);
                    edName.setText(name.trim());
                    edName.setSelection(edName.getText().length());
                    edName.requestFocus();
                } else if (db.userExist(name)) {
                    errName.setText(name+" already exists.");
                    errName.setVisibility(View.VISIBLE);
                } else {
                    long l = db.addNewCustomerData(name, mobile);
                    if(l!=-1){
                        Intent i = new Intent(getApplicationContext(), UserInfoNav.class);
                        i.putExtra("userID", l);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(AddNew.this, "Try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}