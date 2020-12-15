package com.example.a906771.projectmeetingapp;

/**
 * AttDBHandle.java
 *This class controls the input and output from the Meeting table
 * where a user can insert, update, delete a record in the table
 * you can get a row of the table with the id of the row
 * you can get a list of a hashmap with id and name of meetings
 *
 * @author Luke Cook (906771)
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DBHandle {
    private DataBaseGroup dbHelper;

    /**
     * Links meeting handler to the database
     * @param context - the class that wants to access the database
     */
    public DBHandle (Context context) {
        dbHelper = new DataBaseGroup(context);
    }

    /**
     * Add a meeting to the database table meetings
     * @param meeting - an object that contains the attendee information
     * @return the key id for the attendee
     */
    public int insert(MeetingDB meeting) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MeetingDB.KEY_Date, meeting.date);
        values.put(MeetingDB.KEY_name, meeting.name);
        values.put(MeetingDB.KEY_Notes, meeting.notes);
        values.put(MeetingDB.KEY_LAT, meeting.lat);
        values.put(MeetingDB.KEY_LNG, meeting.lng);
        values.put(MeetingDB.KEY_LATLNG_NAME, meeting.location);

        // Inserting Row
        long meeting_Id = db.insert(MeetingDB.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) meeting_Id;
    }

    /**
     *Remove a meeting from the database
     * @param meeting_id - the meeting by id
     */
    public void delete(int meeting_id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(MeetingDB.TABLE, MeetingDB.KEY_ID + "= ?", new String[] { String.valueOf(meeting_id) });
        db.delete(MaDB.TABLE, MaDB.KEY_meeting + "= ?", new String[] { String.valueOf(meeting_id) });
        db.close(); // Closing database connection
    }

    /**
     * change a row of an meeting that has already been created
     * @param meeting the meeting information object
     */
    public void update(MeetingDB meeting) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MeetingDB.KEY_Date, meeting.date);
        values.put(MeetingDB.KEY_name, meeting.name);
        values.put(MeetingDB.KEY_Notes, meeting.notes);
        values.put(MeetingDB.KEY_LAT, meeting.lat);
        values.put(MeetingDB.KEY_LNG, meeting.lng);
        values.put(MeetingDB.KEY_LATLNG_NAME, meeting.location);

        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(MeetingDB.TABLE, values, MeetingDB.KEY_ID + "= ?", new String[] { String.valueOf(meeting.meetingID) });
        db.close(); // Closing database connection
    }

    /**
     * get an arraylist of all up coming meetings in the database stored in a hashmap
     * so you can collect the id and name
     * @return arraylist hashmap with the meeting id and name
     */
    public ArrayList<HashMap<String, String>>  getMeetingList() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                MeetingDB.KEY_ID + "," +
                MeetingDB.KEY_name + "," +
                MeetingDB.KEY_Date + "," +
                MeetingDB.KEY_Notes + "," +
                MeetingDB.KEY_LAT + "," +
                MeetingDB.KEY_LNG + "," +
                MeetingDB.KEY_LATLNG_NAME +
                " FROM " + MeetingDB.TABLE;

       //contains meetings
        ArrayList<HashMap<String, String>> meetingList = new ArrayList<>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                try {
                    SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date strDate = format.parse(cursor.getString(cursor.getColumnIndex(MeetingDB.KEY_Date)));
                    // if it isn't a past meeting add to the list
                    if (!(new Date().after(strDate))) {
                        HashMap<String, String> meeting = new HashMap<>();
                        meeting.put("id", cursor.getString(cursor.getColumnIndex(MeetingDB.KEY_ID)));
                        meeting.put("name", cursor.getString(cursor.getColumnIndex(MeetingDB.KEY_name)));
                        meetingList.add(meeting);
                    }

                } catch (ParseException e) {
                    //check that it has a date if it doesn't, display on the main screen
                    HashMap<String, String> meeting = new HashMap<>();
                    meeting.put("id", cursor.getString(cursor.getColumnIndex(MeetingDB.KEY_ID)));
                    meeting.put("name", cursor.getString(cursor.getColumnIndex(MeetingDB.KEY_name)));
                    meetingList.add(meeting);
                    //handle exception
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return meetingList;

    }
    /**
     * get an arraylist of all history meetings in the database stored in a hashmap
     * so you can collect the id and name
     * @return arraylist hashmap with the meeting id and name
     */
    public ArrayList<HashMap<String, String>>  getMeetingListHistory() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                MeetingDB.KEY_ID + "," +
                MeetingDB.KEY_name + "," +
                MeetingDB.KEY_Date + "," +
                MeetingDB.KEY_Notes + "," +
                MeetingDB.KEY_LAT + "," +
                MeetingDB.KEY_LNG + "," +
                MeetingDB.KEY_LATLNG_NAME +
                " FROM " + MeetingDB.TABLE;

        //stores meetings
        ArrayList<HashMap<String, String>> meetingList = new ArrayList<>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                try {
                    SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date strDate = format.parse(cursor.getString(cursor.getColumnIndex(MeetingDB.KEY_Date)));
                    if (new Date().after(strDate)) {
                        HashMap<String, String> meeting = new HashMap<>();
                        meeting.put("id", cursor.getString(cursor.getColumnIndex(MeetingDB.KEY_ID)));
                        meeting.put("name", cursor.getString(cursor.getColumnIndex(MeetingDB.KEY_name)));
                        meetingList.add(meeting);
                    }

                } catch (ParseException e) {
                    //Exception handled in above meeting
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return meetingList;

    }

    /**
     * get meeting information by the id of the row in the database
     * @param Id - the id of the row you want to select
     * @return an meeting object with their data
     */
    public MeetingDB getMeetingById(int Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                MeetingDB.KEY_ID + "," +
                MeetingDB.KEY_name + "," +
                MeetingDB.KEY_Date + "," +
                MeetingDB.KEY_Notes + "," +
                MeetingDB.KEY_LAT + "," +
                MeetingDB.KEY_LNG + "," +
                MeetingDB.KEY_LATLNG_NAME +
                " FROM " + MeetingDB.TABLE
                + " WHERE " +
                MeetingDB.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        MeetingDB meeting = new MeetingDB();

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );

        if (cursor.moveToFirst()) {
            do {
                //all the data for a meeting
                meeting.meetingID =cursor.getInt(cursor.getColumnIndex(MeetingDB.KEY_ID));
                meeting.name =cursor.getString(cursor.getColumnIndex(MeetingDB.KEY_name));
                meeting.date  =cursor.getString(cursor.getColumnIndex(MeetingDB.KEY_Date));
                meeting.notes =cursor.getString(cursor.getColumnIndex(MeetingDB.KEY_Notes));
                meeting.lat =cursor.getDouble(cursor.getColumnIndex(MeetingDB.KEY_LAT));
                meeting.lng =cursor.getDouble(cursor.getColumnIndex(MeetingDB.KEY_LNG));
                meeting.location =cursor.getString(cursor.getColumnIndex(MeetingDB.KEY_LATLNG_NAME));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return meeting;
    }

    /**
     * returns the next id value for the meeting table
     * @return the int id of the next space in the table
     */
    public int getLastId (){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Long id = 0L;
        String selectQuery = "SELECT  " +
                MeetingDB.KEY_ID +
                " FROM " + MeetingDB.TABLE + " ORDER BY "
                + MeetingDB.KEY_ID + " DESC  limit 1";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getLong(0);
            id = id + 1;
            cursor.close();
        }
        db.close();
        return id.intValue();
    }
}
