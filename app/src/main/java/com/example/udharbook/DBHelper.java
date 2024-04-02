package com.example.udharbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class DBHelper extends SQLiteOpenHelper {
    //DATABASE INFO--------------------------------------
    private static final String DATABASE_NAME = "udhardb";
    private static final int DATABASE_VERSION = 1;


    //INFORMATION TABLE-----------------------------------------
    private static final String TABLE_INFORMATION = "information";
    private static final String INFO_COL_KEY= "keyIndex"; //text not null unique
    private static final String INFO_COL_VALUE= "value"; //text


    //CUSTOMER DETAILS TABLE
    private static final String TABLE_CUSTOMER_DETAILS = "customerDetails";
    private static final String CUST_COL_USER_ID = "userID"; //int primary autoincrement
    private static final String CUST_COL_NAME = "name"; //unique text not null
    private static final String CUST_COL_MOBILE_NO = "mobileNo"; //text
    private static final String CUST_COL_CREATED_DATE = "createdOn"; //text
    private static final String CUST_COL_PENDING = "pending"; //int


    //TRANSACTION DETAILS TABLE
    private static final String TABLE_TRANSACTION_DETAILS = "transactionDetails";
    private static final String TRANS_ID = "id"; //int primary autoincrement
    private static final String TRANS_USER_ID = "userID"; //int
    private static final String TRANS_AMOUNT = "amount";//int
    private static final String TRANS_ADD_OR_RECEIVE = "method";//text
    private static final String TRANS_DATE = "date";//text
    private static final String TRANS_NOTE = "note";//text
    private static final String TRANS_PENDING = "pending";//int


    //LOG TABLE
    private static final String TABLE_LOG_DETAILS = "logDetails";
    private static final String LOG_ID = "id";//int primary autoincrement
    private static final String LOG_VALUE = "value";//text


    SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE IF NOT EXISTS "+TABLE_CUSTOMER_DETAILS+" ("+CUST_COL_USER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CUST_COL_NAME+" TEXT NOT NULL UNIQUE, "+CUST_COL_MOBILE_NO+" TEXT, "+CUST_COL_CREATED_DATE+" TEXT, "+CUST_COL_PENDING+" INTEGER)";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS "+TABLE_INFORMATION+" ("+INFO_COL_KEY+" TEXT NOT NULL UNIQUE, "+INFO_COL_VALUE+" TEXT)";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS "+TABLE_TRANSACTION_DETAILS+" ("+TRANS_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+TRANS_USER_ID+" INTEGER NOT NULL, "+TRANS_AMOUNT+" INTEGER NOT NULL, "+TRANS_ADD_OR_RECEIVE+" TEXT NOT NULL, "+TRANS_DATE+" TEXT, "+TRANS_NOTE+" TEXT, "+TRANS_PENDING+" INTEGER)";
        sqLiteDatabase.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS "+TABLE_LOG_DETAILS+" ("+LOG_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+LOG_VALUE+" TEXT)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void errorReport(String errMsg){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String msg = sdf.format(new Date()) + " : " + errMsg;

            ContentValues cv = new ContentValues();
            cv.put(LOG_VALUE, msg);
            db.insert(TABLE_LOG_DETAILS, null, cv);
        } catch (Exception ignored){

        }
    }

    public boolean userExist(String name) {
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_CUSTOMER_DETAILS+" WHERE "+CUST_COL_NAME+" = ? COLLATE NOCASE", new String[]{name});
        if(c.getCount()>0){
            c.close();
            return true;
        }
        c.close();

        return false;
    }

    public long addNewCustomerData(String name, String mobile) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String date = sdf.format(new Date());
            ContentValues cv = new ContentValues();
            cv.put(CUST_COL_NAME, name);
            cv.put(CUST_COL_MOBILE_NO, mobile);
            cv.put(CUST_COL_CREATED_DATE, date);
            cv.put(CUST_COL_PENDING, 0);
            return db.insert(TABLE_CUSTOMER_DETAILS, null, cv);
        }catch (Exception e){
            errorReport("Add Customer => "+e.getMessage());
        }
        return -1;
    }

    public CustomerDetails getCustomer(long userID) {
        try {
            Cursor c = db.rawQuery("SELECT * FROM "+TABLE_CUSTOMER_DETAILS+" WHERE "+CUST_COL_USER_ID+" = ?",new String[]{String.valueOf(userID)});
            c.moveToFirst();
            String name = c.getString(c.getColumnIndex(CUST_COL_NAME));
            String mobileNo = c.getString(c.getColumnIndex(CUST_COL_MOBILE_NO));
            String createdOn = c.getString(c.getColumnIndex(CUST_COL_CREATED_DATE));
            int pending = c.getInt(c.getColumnIndex(CUST_COL_PENDING));
            c.close();
            return new CustomerDetails(userID, name, mobileNo, createdOn, pending);
        }catch (Exception e){
            errorReport("Get Customer Detail => "+e.getMessage());
            return null;
        }
    }

    //delete customer
    public boolean deleteCustomer(long userID) {
        boolean result = db.delete(TABLE_CUSTOMER_DETAILS, CUST_COL_USER_ID+" = ?", new String[]{String.valueOf(userID)}) > 0;
        db.delete(TABLE_TRANSACTION_DETAILS,TRANS_USER_ID+" = ?", new String[]{String.valueOf(userID)});
        return result;
    }

    public boolean userExistExceptUserID(long userID, String name) {
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_CUSTOMER_DETAILS+" WHERE "+CUST_COL_NAME+" = ? COLLATE NOCASE AND "+CUST_COL_USER_ID+" != ?", new String[]{name, String.valueOf(userID)});
        if(c.getCount()>0){
            c.close();
            return true;
        }
        c.close();
        return false;
    }

    public boolean updateCustomerData(long userID, String name, String mobile) {
        ContentValues cv = new ContentValues();
        cv.put(CUST_COL_NAME, name);
        cv.put(CUST_COL_MOBILE_NO, mobile);
        return db.update(TABLE_CUSTOMER_DETAILS,cv,CUST_COL_USER_ID+" = ?",new String[]{String.valueOf(userID)}) > 0;
    }

    public boolean updateAmount(long userID, int pending) {
        ContentValues cv = new ContentValues();
        cv.put(CUST_COL_PENDING, pending);
        return db.update(TABLE_CUSTOMER_DETAILS,cv,CUST_COL_USER_ID+" = ?",new String[]{String.valueOf(userID)}) > 0;
    }

    public boolean addTransaction(long userID, int amount, String method, String date, String note, int pending) {
        ContentValues cv = new ContentValues();
        cv.put(TRANS_USER_ID, userID);
        cv.put(TRANS_AMOUNT, amount);
        cv.put(TRANS_ADD_OR_RECEIVE, method);
        cv.put(TRANS_DATE, date);
        cv.put(TRANS_NOTE, note);
        cv.put(TRANS_PENDING, pending);
        if(db.insert(TABLE_TRANSACTION_DETAILS, null, cv)!=-1){
            return true;
        }
        return false;
    }

    public ArrayList<TransactionModel> getTransactionDetails(long userID) {
        ArrayList<TransactionModel> arrayList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_TRANSACTION_DETAILS+" WHERE "+TRANS_USER_ID+" = ? ORDER BY "+TRANS_ID+" DESC", new String[]{String.valueOf(userID)});
        if(c.getCount()<=0){
            return arrayList;
        }
        c.moveToFirst();
        int amount, pending;
        String date, method;
        do{
            amount = c.getInt(c.getColumnIndex(TRANS_AMOUNT));
            pending = c.getInt(c.getColumnIndex(TRANS_PENDING));
            date = c.getString(c.getColumnIndex(TRANS_DATE));
            method = c.getString(c.getColumnIndex(TRANS_ADD_OR_RECEIVE));
            arrayList.add(new TransactionModel(amount, date, method, pending));
        }while (c.moveToNext());

        return arrayList;
    }

    public ArrayList<CustomerModel> getCustomerList() {
        ArrayList<CustomerModel> arrayList = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_CUSTOMER_DETAILS+" ORDER BY "+CUST_COL_NAME, new String[]{});
        if(c.getCount() <= 0){
            return arrayList;
        }
        c.moveToFirst();
        int userID, pending;
        String name, mobile;
        do {
            userID = c.getInt(c.getColumnIndex(CUST_COL_USER_ID));
            pending = c.getInt(c.getColumnIndex(CUST_COL_PENDING));
            name = c.getString(c.getColumnIndex(CUST_COL_NAME));
            mobile = c.getString(c.getColumnIndex(CUST_COL_MOBILE_NO));
            arrayList.add(new CustomerModel(userID, name, mobile, pending));
        }while (c.moveToNext());

        return arrayList;
    }

    public Uri exportCSV(Context context){
        File file = new File(context.getCacheDir() + File.separator + "customerDetails.csv");
        String data = "";
        try {
            FileOutputStream out = new FileOutputStream(file);
            Cursor c = db.rawQuery("SELECT * FROM "+TABLE_CUSTOMER_DETAILS, new String[]{});
            for(int i = 1; i<c.getColumnCount(); i++){
                data += "\"" + c.getColumnName(i) +"\"";
                data += ",";
            }
            data += "\n";


            if(c.moveToFirst()){
                do {
                    for(int i = 1; i<c.getColumnCount(); i++){
                        data += "\"" + c.getString(i) +"\"";
                        data += ",";
                    }
                    data += "\n";
                }while (c.moveToNext());
            }
            out.write(data.getBytes());
            out.close();
            Uri u1;
            u1  =   Uri.fromFile(file);



            return u1;
        }catch (Exception e){
            return null;
        }
    }


    public String getUUID() {
        String key = "UUID";
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_INFORMATION+" WHERE "+INFO_COL_KEY+" = ?", new String[]{key});
        String uuid;
        if(c.getCount()<=0){
            uuid = UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
            ContentValues cv = new ContentValues();
            cv.put(INFO_COL_KEY, key);
            cv.put(INFO_COL_VALUE, uuid);
            if(db.insert(TABLE_INFORMATION, null, cv)==-1){
                errorReport("ADD INFORMATION => failed to add UUID");
                return null;
            }
        } else {
            c.moveToFirst();
            uuid = c.getString(1);
        }
        return uuid;
    }

    public int getMaxUser() {
        return 100;
    }

    public int getCurrentUsers() {
        Cursor c = db.rawQuery("SELECT "+CUST_COL_USER_ID+" FROM "+TABLE_CUSTOMER_DETAILS, new String[]{});
        return c.getCount();
    }
}
