package com.example.a906771.projectmeetingapp;

/**
 * AttDBHandle.java
 *This class controls the input and output from the Attendees table
 * where a user can insert, update, delete a record in the table
 * you can get a row of the table with the id of the row
 * you can get a list of a hashmap with id and name of attendees
 *
 * @author Luke Cook (906771)
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class AttDBHandle {
    private DataBaseGroup dbHelper;

    /**
     * Links attendees handler to the database
     * @param context - the class that wants to access the database
     */
    public AttDBHandle (Context context) {
        dbHelper = new DataBaseGroup(context);
    }

    /**
     * Add an attendee to the database attendees
     * @param att - an object that contains the attendee information (name)
     * @return the key id for the attendee
     */
    public int insert(AttDB att) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AttDB.KEY_name, att.name);

        // Inserting Row
        long att_Id = db.insert(AttDB.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) att_Id;
    }

    /**
     *Remove an attendee from the database
     * @param att_Id - the attendee by id
     */
    public void delete(int att_Id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(AttDB.TABLE, AttDB.KEY_ID + "= ?", new String[]{String.valueOf(att_Id)});
        db.delete(MaDB.TABLE, MaDB.KEY_att + "= ?", new String[]{String.valueOf(att_Id)});
        db.close(); // Closing database connection
    }

    /**
     * change a row of an attendee that has already been created
     * @param att the attendee information object
     */
    public void update(AttDB att) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(AttDB.KEY_ID, att.people_ID);
        values.put(AttDB.KEY_name, att.name);

        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(AttDB.TABLE, values, AttDB.KEY_ID + "= ?", new String[]{String.valueOf(att.people_ID)});
        db.update(AttDB.TABLE, values, AttDB.KEY_name + "= ?", new String[]{String.valueOf(att.name)});
        db.close(); // Closing database connection
    }

    /**
     * get an arraylist of all attendees in the database stored in a hashmap
     * so you can collect the id and name
     * @return arraylist hashmap with all attendees information
     */
    public ArrayList<HashMap<String, String>> getAttList() {
        //Open connection to read only
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                AttDB.KEY_ID + "," +
                AttDB.KEY_name +
                " FROM " + AttDB.TABLE;

        //contains attendees
        ArrayList<HashMap<String, String>> attList = new ArrayList<>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> att = new HashMap<>();
                att.put("id", cursor.getString(cursor.getColumnIndex(AttDB.KEY_ID)));
                att.put("name", cursor.getString(cursor.getColumnIndex(AttDB.KEY_name)));
                attList.add(att);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return attList;

    }

    /**
     * get attendees information by the id of the row in the database
     * @param Id - the id of the row you want to select
     * @return an attendees object with their data
     */
    public AttDB getAttById(int Id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT  " +
                AttDB.KEY_ID + "," +
                AttDB.KEY_name +
                " FROM " + AttDB.TABLE
                + " WHERE " +
                AttDB.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        AttDB att = new AttDB();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(Id)});

        if (cursor.moveToFirst()) {
            do {
                //put the data into the attendees object
                att.people_ID = cursor.getInt(cursor.getColumnIndex(AttDB.KEY_ID));
                att.name = cursor.getString(cursor.getColumnIndex(AttDB.KEY_name));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return att;
    }

}