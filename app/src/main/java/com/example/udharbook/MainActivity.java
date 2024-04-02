package com.example.udharbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    boolean backupResult = false;

    ImageView addUserBtn;
    RecyclerView customerRecyclerView;
    SearchView searchView;
    TextView noResult;

    ArrayList<CustomerModel> arrayCustomers = new ArrayList<>();
    ArrayList<CustomerModel> sortedArrayCustomers = new ArrayList<>();

    DBHelper db;

    boolean reCreateActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBHelper(this);

        addUserBtn = findViewById(R.id.imageView);
        customerRecyclerView = findViewById(R.id.recyclerViewCustomerList);
        customerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchView = findViewById(R.id.searchViewMain);
        noResult = findViewById(R.id.textViewEmptyResult);

        init();


        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddNew.class);
                startActivity(intent);

            }
        });

    }

    public void setAdapterToRecyclerView(ArrayList<CustomerModel> array){
        RecyclerCustomerAdapter adapter = new RecyclerCustomerAdapter(this, array){
            @Override
            public void callBack(int userID){
                Intent i = new Intent(getApplicationContext(), UserInfoNav.class);
                i.putExtra("userID", Long.valueOf(userID));
                startActivity(i);

            }
        };

        customerRecyclerView.setAdapter(adapter);
    }

    public void init(){
        noResult.setVisibility(View.GONE);
        arrayCustomers = db.getCustomerList();
        setAdapterToRecyclerView(arrayCustomers);
        searchView.setQuery("", false);
        searchView.clearFocus();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                noResult.setVisibility(View.GONE);
                sortedArrayCustomers.clear();
                if(s.isEmpty()){
                    setAdapterToRecyclerView(arrayCustomers);
                    return false;
                }
                if(TextUtils.isDigitsOnly(s)){
                    for(CustomerModel item: arrayCustomers){
                        if(stringLike(item.mobileNo.toLowerCase(), s.toLowerCase()) || stringLike(item.name.toLowerCase(), s.toLowerCase())){
                            sortedArrayCustomers.add(item);
                        }
                    }
                } else {
                    for(CustomerModel item: arrayCustomers){
                        if(stringLike(item.name.toLowerCase(), s.toLowerCase())){
                            sortedArrayCustomers.add(item);
                        }
                    }
                }

                if(sortedArrayCustomers.size()==0){
                    noResult.setVisibility(View.VISIBLE);
                }
                setAdapterToRecyclerView(sortedArrayCustomers);
                return false;
            }
        });
    }



    public boolean stringLike(String str, String like){
        if(like==null||like.isEmpty()) {
            return true;
        }
        int strLength, likeLength;
        strLength = str.length();
        likeLength = like.length();
        if(likeLength>strLength){
            return false;
        }
        int count = 0;
        int checkfor = 0;
        int lastCheck = likeLength - 1;

        for (int i = 0; i < strLength; i++){
            if(str.charAt(i) == like.charAt(checkfor)){
                count++;
                if(checkfor==lastCheck){
                    break;
                }
                checkfor++;
            }
        }


        if(count==likeLength){
            return true;
        }
        return false;
    }


    public boolean backupData(){
        Uri uri = db.exportCSV(MainActivity.this);
        String uuid = db.getUUID();
        if(uuid==null){
            Toast.makeText(this, "Error occurred while getting ID for backup", Toast.LENGTH_LONG).show();
            return false;
        }
        backupResult = false;

        if(uri == null){
            return false;
        }
        // Create a Cloud Storage reference from the app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        //StorageReference storageRef = FirebaseStorage.getInstance("gs://udharbook01-f03fd.appspot.com").getReference();



        // Create a reference to 'UdharBook/offlineBackupData/(UUID)/data.csv'
        StorageReference dataFileRef = storageRef.child("UdharBook/offlineBackupData/"+uuid+"/data.csv");
        dataFileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this, "Backup Successful", Toast.LENGTH_LONG).show();
                backupResult = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to Backup", Toast.LENGTH_LONG).show();
                backupResult = false;
            }
        });

        return backupResult;
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

    public void backup(View view) {
        backupData();
    }
}