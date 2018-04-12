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

//import com.struggleassist.Model.Record;
import com.struggleassist.Model.Record;
import com.struggleassist.Model.ViewContext;

public class DatabaseController {
    //user table
    static final String TABLE_USER = "User";
    static final String KEY_UID = "uid";    //text
    static final String KEY_FIRSTNAME = "firstName";    //text
    static final String KEY_LASTNAME = "lastName";  //text
    static final String KEY_DOB = "dateOfBirth";    //numeric
    static final String KEY_EMERGENCY_ID = "emergencyContactID"; //numeric
    static final String KEY_EMERGENCY_NUMBER = "emergencyContactNumber"; //numeric
    static final String CREATE_USER = "CREATE TABLE IF NOT EXISTS User (uid text, firstName text, lastName text, dateOfBirth text, emergencyContactID text, emergencyContactNumber text, PRIMARY KEY (uid));";

    //Records table
    static final String TABLE_RECORDS = "Records";
    static final String KEY_RID = "rid";
    static final String KEY_DOI = "dateOfIncident";
    static final String KEY_LOCATION = "incidentLocation";
    static final String KEY_VIDEO = "incidentVideo";
    static final String KEY_NOTES = "incidentNotes";
    static final String KEY_SCORE = "incidentScore";
    static final String KEY_RESPONSE = "userResponse";
    static final String CREATE_RECORDS = "CREATE TABLE IF NOT EXISTS Records (rid text, dateOfIncident text, incidentLocation text, incidentVideo text, incidentNotes text, incidentScore text, userResponse text, PRIMARY KEY (rid));";


    static final String TAG = "Database";
    static final String DATABASE_NAME = "StruggleAssist_DB";

    static final int DATABASE_VERSION = 2;
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DatabaseController(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
        open();
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_RECORDS);
        close();
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_USER);
                db.execSQL(CREATE_RECORDS);
                //db.execSQL(CREATE_HOLDS);
            } catch (SQLException e) {
                e.printStackTrace();
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

    //dev purposes only
    public void reset(){
        db.execSQL("DROP TABLE "+TABLE_USER);
        db.execSQL("DROP TABLE "+TABLE_RECORDS);
    }

    public boolean userExists(){
        String count = "SELECT count(*) FROM " + TABLE_USER;
        Cursor mcursor = db.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        boolean exists = icount > 0;
        Log.d("COUNT_USER", Integer.toString(icount));
        return exists;
    }

    //USER OPERATIONS
    //---insert a contact into the database--
    public long insertUser(String fName, String lName, String dob, String contactID, String contactNumber) {
        String id = fName + lName;
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_UID, id);
        initialValues.put(KEY_FIRSTNAME, fName);
        initialValues.put(KEY_LASTNAME, lName);
        initialValues.put(KEY_DOB, dob);
        initialValues.put(KEY_EMERGENCY_ID, contactID);
        initialValues.put(KEY_EMERGENCY_NUMBER, contactNumber);
        return db.insert(TABLE_USER, null, initialValues);
    }

    //---deletes a particular user--(not used currently)
    public boolean deleteUser(String id) {
        return db.delete(TABLE_USER, KEY_UID + "=" + id, null) > 0;
    }

    //---retrieves all the users--
    public Cursor getAllUsers() {
        return db.query(TABLE_USER, new String[]{KEY_UID, KEY_FIRSTNAME, KEY_LASTNAME, KEY_DOB, KEY_EMERGENCY_ID, KEY_EMERGENCY_NUMBER}, null, null, null, null, null);
    }

    //---retrieves a particular user--(not used currently)
    public Cursor getUser(String id) throws SQLException {
        Cursor mCursor = db.query(true, TABLE_USER, new String[]{KEY_UID, KEY_FIRSTNAME, KEY_LASTNAME, KEY_DOB, KEY_EMERGENCY_ID, KEY_EMERGENCY_NUMBER}, KEY_UID + "=" + id, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a user--
    public boolean updateUser(String id, String fName, String lName, String dob, String contactID, String contactNumber) {
        ContentValues args = new ContentValues();
        args.put(KEY_FIRSTNAME, fName);
        args.put(KEY_LASTNAME, lName);
        args.put(KEY_DOB, dob);
        args.put(KEY_EMERGENCY_ID, contactID);
        args.put(KEY_EMERGENCY_NUMBER, contactNumber);
        return db.update(TABLE_USER, args, KEY_UID + "=?", new String[]{id}) > 0;
    }


    //Records Operations
    //---insert record into database
    public long insertRecord(Record record){

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RID,       record.getId());
        initialValues.put(KEY_DOI,       record.getDateOfIncident());
        initialValues.put(KEY_LOCATION,  record.getIncidentLocation());
        initialValues.put(KEY_VIDEO,     record.getIncidentVideo());
        initialValues.put(KEY_NOTES,     record.getIncidentNotes());
        initialValues.put(KEY_SCORE,     Float.toString(record.getIncidentScore()));
        initialValues.put(KEY_RESPONSE,  record.getUserResponse());
        Log.d("DBInsertRecord:", record.getId() + "|" + record.getDateOfIncident() + "|" + record.getIncidentLocation() + "|" +
                record.getIncidentVideo() + "|" + record.getIncidentNotes() + "|" + Float.toString(record.getIncidentScore()) + "|" + record.getUserResponse());
        return db.insert(TABLE_RECORDS, null, initialValues);
    }

    //---update record
    public boolean updateRecord(Record record){
        ContentValues args = new ContentValues();
        args.put(KEY_RID,       record.getId());
        args.put(KEY_DOI,       record.getDateOfIncident());
        args.put(KEY_LOCATION,  record.getIncidentLocation());
        args.put(KEY_VIDEO,     record.getIncidentVideo());
        args.put(KEY_NOTES,     record.getIncidentNotes());
        args.put(KEY_SCORE,     record.getIncidentScore());
        args.put(KEY_RESPONSE,  record.getUserResponse());
        Log.d("DBUpdateRecord:",record.getIncidentNotes());
        return db.update(TABLE_RECORDS, args, KEY_RID + "=?", new String[]{record.getId()}) > 0;
    }

    //---remove record
    public boolean deleteRecord(Record record) {
        return db.delete(TABLE_RECORDS, KEY_RID + "=" + record.getId(), null) > 0;
    }

    //---Retrieve all records
    public Cursor getAllRecords() {
        return db.query(TABLE_RECORDS, new String[]{KEY_RID, KEY_DOI, KEY_LOCATION, KEY_VIDEO, KEY_NOTES, KEY_SCORE, KEY_RESPONSE}, null, null, null, null, null);
    }

    //---Retrieve a particular record
    public Cursor getRecord(Record record) throws SQLException {
        Cursor mCursor = db.query(true, TABLE_USER, new String[]{KEY_RID, KEY_DOI, KEY_LOCATION, KEY_VIDEO, KEY_NOTES, KEY_SCORE, KEY_RESPONSE}, KEY_RID + "=" + record.getId(), null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}
