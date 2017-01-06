package com.sega.vimarket.provider;
/**a
 * Created by Sega on 8/1/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**a
 * Created by Sega on 22/06/2016.
 */
public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "vimarket";
    // Login table name
    private static final String TABLE_USER = "users";
    // Login Table Columns names
    private static final String KEY_USERID = "userid";
    private static final String KEY_USER = "user";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONENUMBER = "phonenumber";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_AREA = "area";
    private static final String KEY_USERPIC = "userpic";
    private static final String KEY_DATECREATE = "datecreate";
    private static final String KEY_RATE = "rate";
    private static final String KEY_COUNT = "count";
    //===============================================================================
    private static final String TABLE_PRODUCT = "product";
    private static final String KEY_PRODUCTID = "productid";
    private static final String KEY_PRODUCTNAME = "productname";
    private static final String KEY_PRICE = "price";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_CATEGORYNAME = "categoryname";
    private static final String KEY_AREAPRODUCT = "areaproduct";
    private static final String KEY_PRODUCTTYPE = "producttype";
    private static final String KEY_PRODUCTIMAGE = "productimage";
    private static final String KEY_PRODUCTDATE = "productdate";
    private static final String KEY_PRODUCTSTATUS = "productstatus";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_LOT = "lot";
    private static final String KEY_LAT = "lat";
    //===============================================================================
    private static final String TABLE_CURRENCY = "currency";
    private static final String KEY_CURRENCYNAME = "currencyname";
    private static final String KEY_CURRENCYRATE = "currencyrate";
    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USERID + " TEXT PRIMARY KEY,"
                + KEY_USER + " TEXT ,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_PHONENUMBER + " TEXT UNIQUE,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_AREA + " TEXT,"
                + KEY_USERPIC + " TEXT,"
                + KEY_DATECREATE + " TEXT,"
                + KEY_RATE + " TEXT,"
                + KEY_COUNT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        //==================================================================================================
        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "("
                + KEY_PRODUCTID + " TEXT PRIMARY KEY," + KEY_PRODUCTNAME + " TEXT,"
                + KEY_PRICE + " TEXT," + KEY_USERNAME + " TEXT," + KEY_CATEGORYNAME + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_AREAPRODUCT + " TEXT,"
                + KEY_PRODUCTTYPE + " TEXT,"
                + KEY_PRODUCTSTATUS + " TEXT,"
                + KEY_PRODUCTIMAGE + " TEXT,"
                + KEY_PRODUCTDATE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_LAT + " TEXT,"
                + KEY_LOT + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCT_TABLE);
        //=====================================================================================================
        String CREATE_CURRENCY_TABLE = "CREATE TABLE " + TABLE_CURRENCY + "("
                + KEY_CURRENCYNAME + " TEXT PRIMARY KEY,"
                + KEY_CURRENCYRATE + " TEXT" + ")";
        db.execSQL(CREATE_CURRENCY_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCY);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(int userid, String username, String email, String phonenumber,
                        String address, String area, String userpic, String datecreate, String rate,
                        String count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERID, userid); // Name
        values.put(KEY_USER, username); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_PHONENUMBER, phonenumber); // Email
        values.put(KEY_ADDRESS, address); // Created At
        values.put(KEY_AREA, area);
        values.put(KEY_USERPIC, userpic);
        values.put(KEY_DATECREATE, datecreate);
        values.put(KEY_RATE, rate);
        values.put(KEY_COUNT, count);
        // Inserting Row
        db.close(); // Closing database connection
    }

    public HashMap<String, String> getUserDetails(int id) {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE userid = '" + id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("userid", cursor.getString(0));
            user.put("username", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("phonenumber", cursor.getString(3));
            user.put("address", cursor.getString(4));
            user.put("area", cursor.getString(5));
            user.put("userpic", cursor.getString(6));
            user.put("datecreate", cursor.getString(7));
            user.put("rate", cursor.getString(8));
            user.put("count", cursor.getString(9));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());
        return user;
    }


    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }

}