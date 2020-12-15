package com.example.a906771.projectmeetingapp;
/**
 * DataBaseGroup.java
 * Used to create three tables (meetings , attendees and another to see which meetings
 * attendees go to
 *
 * @author Luke Cook (906771)
 */



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseGroup extends SQLiteOpenHelper {
    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "crud.db";

    public DataBaseGroup(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * create the three tables for meetings, attendees and table to link the two
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here

        String CREATE_TABLE_MEETING = "CREATE TABLE " + MeetingDB.TABLE  + "("
                + MeetingDB.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + MeetingDB.KEY_name + " TEXT, "
                + MeetingDB.KEY_Date + " TEXT, "
                + MeetingDB.KEY_Notes + " TEXT, "
                + MeetingDB.KEY_LAT + " REAL, "
                + MeetingDB.KEY_LNG + " REAL,"
                + MeetingDB.KEY_LATLNG_NAME + " TEXT)";

        String CREATE_TABLE_ATTENDEES = "CREATE TABLE " + AttDB.TABLE  + "("
                + AttDB.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + AttDB.KEY_name + " TEXT) ";

        String CREATE_TABLE_GROUP = "CREATE TABLE " + MaDB.TABLE  + "("
                + MaDB.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + MaDB.KEY_meeting + " INTEGER, "
                + MaDB.KEY_att + " INTEGER) ";

        db.execSQL(CREATE_TABLE_MEETING);
        db.execSQL(CREATE_TABLE_ATTENDEES);
        db.execSQL(CREATE_TABLE_GROUP);


    }

    /**
     * If there is a new table then the old one will be dropped
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + MeetingDB.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + AttDB.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MaDB.TABLE);

        // Create tables again
        onCreate(db);

    }
}
