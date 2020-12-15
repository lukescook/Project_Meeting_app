package com.example.a906771.projectmeetingapp;

/**
 * MaHandle.java
 *This class controls the input and output for matching attendees with meetings
 * where a user can insert, update, delete a record in the table
 * you can get a row of the table with the id of the row
 * you can see the next key id value in the table
 * you can see if a attendee are linked with a meeting
 * You can get a string of all meetings an attendee is in
 * You can get a string of all the attendees for a meeting
 *
 * @author Luke Cook (906771)
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MaHandle {
    private DataBaseGroup dbHelper;

    /**
     * Links meeting and attendee handler to the database
     * @param context - the class that wants to access the database
     */
    public MaHandle(Context context) {
        dbHelper = new DataBaseGroup(context);
    }

    /**
     * Add a meeting id  and attendee id together to the database table to create a link between
     * meetings and attendees
     * @param group - an object that contains the attendee id and meeting id information
     */
    public void insert(MaDB group) {

        //Open connection to write data
        //check id's to make sure what is inserted doesn't already exists
        if(checkExistence(group.meeting,group.attendees)) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MaDB.KEY_ID, group.group_ID);
            values.put(MaDB.KEY_meeting, group.meeting);
            values.put(MaDB.KEY_att, group.attendees);

            // Inserting Row
            db.insert(MaDB.TABLE, null, values);
            db.close(); // Closing database connection
        }
    }


    /**
     *Remove an attendee meeting link from the database
     * @param ma_id - the attendee by id
     */
    public void delete(int ma_id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(MaDB.TABLE, MaDB.KEY_ID + "= ?", new String[]{String.valueOf(ma_id)});
        db.close(); // Closing database connection
    }

    /**
     * get attendee-meeting link information by the id of the row in the database
     * @param Id - the id of the row you want to select
     * @return a meeting-attendee object with their data
     */
    public MaDB getMaById(int Id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                MaDB.KEY_ID + "," +
                MaDB.KEY_meeting + "," +
                MaDB.KEY_att +
                " FROM " + MaDB.TABLE
                + " WHERE " +
                MaDB.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        MaDB ma = new MaDB();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(Id)});

        if (cursor.moveToFirst()) {
            do {
                ma.group_ID = cursor.getInt(cursor.getColumnIndex(MaDB.KEY_ID));
                ma.meeting = cursor.getInt(cursor.getColumnIndex(MaDB.KEY_meeting));
                ma.attendees = cursor.getInt(cursor.getColumnIndex(MaDB.KEY_att));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return ma;
    }
    /**
     * Use the meeting id to get the name of all the attendees that are in the meeting
     * @param Id the id of the meetings you want the attendee for
     * @return a string of all the name of the attendees
     */
    public String getAttWithMeetingId(int Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT *" +
                " FROM " + MaDB.TABLE + "," + MeetingDB.TABLE + "," + AttDB.TABLE
                + " WHERE " + MeetingDB.KEY_ID + " = " + MaDB.KEY_meeting + " AND " +
                AttDB.KEY_ID + " = " + MaDB.KEY_att + " AND " +
                MaDB.KEY_meeting + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        AttDB att = new AttDB();
        String list = "";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(Id)});

        if (cursor.moveToFirst()) {
            do {
                att.people_ID = cursor.getInt(cursor.getColumnIndex(AttDB.KEY_ID));
                att.name = cursor.getString(cursor.getColumnIndex(AttDB.KEY_name));
                Log.d("data", att.name);
                //add attendee names to the list
                list += att.name + " ";

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;

    }

    /**
     * Use the attendee id to get a the name of all the meetings they are in
     * @param Id the id of the attendee you want the meeting for
     * @return a string of all the name of the meetings
     */
    public String getMeetingWithAttId(int Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //use all tables to collect the meetings
        String selectQuery = "SELECT *" +
                " FROM " + MaDB.TABLE + "," + MeetingDB.TABLE + "," + AttDB.TABLE
                + " WHERE " + MeetingDB.KEY_ID + " = " + MaDB.KEY_meeting + " AND " +
                AttDB.KEY_ID + " = " + MaDB.KEY_att + " AND " +
                MaDB.KEY_att + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        AttDB att = new AttDB();
        String list = "";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(Id)});

        if (cursor.moveToFirst()) {
            do {
                //collects all meeting information
                att.people_ID = cursor.getInt(cursor.getColumnIndex(MeetingDB.KEY_ID));
                att.name = cursor.getString(cursor.getColumnIndex(MeetingDB.KEY_name));
                Log.d("data", att.name);
                //in this case we only need the names
                list += " " + att.name + "\n";

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;

    }
    /**
     * returns the next id value for the meeting-attendee link table
     * @return the int id of the next space in the table
     */
    public int getLastId (){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Long id = 0L;
        String selectQuery = "SELECT  " +
                MaDB.KEY_ID +
                " FROM " + MaDB.TABLE + " ORDER BY "
                + MaDB.KEY_ID + " DESC  limit 1";
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor != null && cursor.moveToFirst()) {
           id = cursor.getLong(0);
           //get the next id value
           id = id + 1;
            cursor.close();
        }
        db.close();
        return id.intValue();
    }

    /**
     * check if an attendee is in a meeting
     * @param id - of the meeting
     * @param id2 - id of the attendee
     * @return true if there is a link between the meeting and attendee, false if not
     */
    public boolean checkExistence(int id, int id2){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                MaDB.KEY_ID  +
                " FROM " + MaDB.TABLE
                + " WHERE " +
                MaDB.KEY_meeting + "=? AND " +
                MaDB.KEY_att + "=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id), String.valueOf(id2)});
        //if the cursor is not greater than 0 we know there is no link between this meeting and attendee
        boolean value = !(cursor.getCount() > 0);
        cursor.close();
        db.close();
        return value;

    }
}