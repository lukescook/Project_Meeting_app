package com.example.a906771.projectmeetingapp;

/**
 * MaDB.java
 * contains the meeting attendee database table information
 *which links attendees with meetings
 *
 * @author Luke Cook (906771)
 */

public class MaDB {
    public static final String TABLE = "Attendees";

    // Labels Table Columns names
    public static final String KEY_ID = "grid";
    public static final String KEY_meeting = "meeting";
    public static final String KEY_att = "grattendees";



    // property help us to keep data
    public int group_ID;
    public int meeting;
    public int attendees;
}
