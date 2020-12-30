package com.example.a.projectmeetingapp;

/**
 * MeetingDB.java
 * contains meeting database table information
 *
 * @author Luke Cook
 */
public class MeetingDB {
    // Labels table name
    public static final String TABLE = "MeetingTable";

    // Labels Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_name = "name";
    public static final String KEY_Date = "date";
    public static final String KEY_Notes = "notes";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";
    public static final String KEY_LATLNG_NAME = "location";

    // property help us to keep data
    public int meetingID;
    public String name;
    public String date;
    public String notes;
    public double lat;
    public double lng;
    public String location;
}
