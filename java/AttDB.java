package com.example.a.projectmeetingapp;

/**
 * AttDB.java
 * contains attendees database table information
 *
 * @author Luke Cook
 */
public class AttDB {
    public static final String TABLE = "People";

    // Labels Table Columns names
    public static final String KEY_ID = "attid";
    public static final String KEY_name = "attname";

    // property help us to keep data
    public int people_ID;
    public String name;
}
