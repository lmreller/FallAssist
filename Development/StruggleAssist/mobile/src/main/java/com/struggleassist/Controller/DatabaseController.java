package com.struggleassist.Controller;

/**
 * Created by lucas on 11/25/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseController {
    //user table
    static final String TABLE_USER = "User";
    static final String KEY_UID = "uid";    //text
    static final String KEY_FIRSTNAME = "firstName";    //text
    static final String KEY_LASTNAME = "lastName";  //text
    static final String KEY_DOB = "dateOfBirth";    //numeric
    static final String KEY_EMERGENCY = "emergencyContact"; //numeric
    static final String CREATE_USER = "create table User (uid text not null, firstName text not null, lastName text not null, dateOfBirth numeric not null, emergencyContact numeric not null, PRIMARY KEY (id));";

    //records table
    static final String TABLE_RECORDS = "Records";
    static final String CREATE_RECORDS = "create table Records (_id integer primary key autoincrement, " + "name text not null, email text not null);";

    static final String TAG = "Database";
    static final String DATABASE_NAME = "StruggleAssist_DB";

    static final int DATABASE_VERSION = 1;
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    static boolean dbExists = true;

    public DatabaseController(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_USER);
                //db.execSQL(CREATE_RECORDS);
                //db.execSQL(CREATE_HOLDS);
                dbExists = false;
            } catch (SQLException e) {
                e.printStackTrace();
                dbExists = true;
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            //db.execSQL("DROP TABLE IF EXISTS contacts");
            onCreate(db);
        }
    }

    //---opens the database--
    public DatabaseController open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database--
    public void close() {
        DBHelper.close();
    }

    //USER OPERATIONS
    //---insert a contact into the database--
    public long insertUser(String fName, String lName, String dob, String contact) {
        String id = fName + lName + dob;
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_UID, id);
        initialValues.put(KEY_FIRSTNAME, fName);
        initialValues.put(KEY_LASTNAME, lName);
        initialValues.put(KEY_DOB, dob);
        initialValues.put(KEY_EMERGENCY, contact);
        return db.insert(TABLE_USER, null, initialValues);
    }

    //---deletes a particular contact--
    public boolean deleteUser(String id) {
        return db.delete(TABLE_USER, KEY_UID + "=" + id, null) > 0;
    }

    //---retrieves all the contacts--
    public Cursor getAllUsers() {
        return db.query(TABLE_USER, new String[]{KEY_UID, KEY_FIRSTNAME, KEY_LASTNAME, KEY_DOB, KEY_EMERGENCY}, null, null, null, null, null);
    }

    //---retrieves a particular contact--
    public Cursor getUser(String id) throws SQLException {
        Cursor mCursor = db.query(true, TABLE_USER, new String[]{KEY_UID, KEY_FIRSTNAME, KEY_LASTNAME, KEY_DOB, KEY_EMERGENCY}, KEY_UID + "=" + id, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a contact--
    public boolean updateUser(String id, String fName, String lName, String dob, String contact) {
        ContentValues args = new ContentValues();
        args.put(KEY_UID, id);
        args.put(KEY_FIRSTNAME, fName);
        args.put(KEY_LASTNAME, lName);
        args.put(KEY_DOB, dob);
        args.put(KEY_EMERGENCY, contact);
        return db.update(TABLE_USER, args, KEY_UID + "=" + id, null) > 0;
    }

/*

    //RECORDS OPERATIONS
    //---insert a contact into the database--
    public long insertContact(String name, String email) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_EMAIL, email);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular contact--
    public boolean deleteContact(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_UID + "=" + rowId, null) > 0;
    }

    //---retrieves all the contacts--
    public Cursor getAllContacts() {
        return db.query(DATABASE_TABLE, new String[]{KEY_UID, KEY_NAME, KEY_EMAIL}, null, null, null, null, null);
    }

    //---retrieves a particular contact--
    public Cursor getContact(long rowId) throws SQLException {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{KEY_UID, KEY_NAME, KEY_EMAIL}, KEY_UID + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a contact--
    public boolean updateContact(long rowId, String name, String email) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_EMAIL, email);
        return db.update(DATABASE_TABLE, args, KEY_UID + "=" + rowId, null) > 0;
    }

    */

}

