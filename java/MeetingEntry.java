package com.example.a.projectmeetingapp;

/**
 * MeetingEntry.java
 * Allows the user to enter the name, date/time, notes attendees and location of a meeting and save
 * it on the database
 *or cancel
 * @author Luke Cook
 */

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;


public class MeetingEntry extends AppCompatActivity implements android.view.View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    ImageButton btnSave, btnClose;
    EditText editName;
    TextView editDT;
    TextView editLocation;
    TextView editAttendees;
    EditText editNotes;
    private int meetingID =0;
    private double lat = 0;
    private double lng = 0;
    ArrayList<Integer> groupAttendees = new ArrayList<>();
    int hour, min, day, month, year;
    int hourR, minR, dayR, monthR, yearR;

    /**
     * creates links between the meeting entry xml design
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_entry);

        btnSave = findViewById(R.id.btnSave);
        btnClose = findViewById(R.id.btnClose);

        //link to xml
        editName = findViewById(R.id.editMeetingName);
        editDT = findViewById(R.id.editDT);
        editAttendees = findViewById(R.id.editAtt);
        editNotes = findViewById(R.id.editNotes);
        editLocation = findViewById(R.id.editLoc);

        btnSave.setOnClickListener(this);
        btnClose.setOnClickListener(this);


        Intent intent = getIntent();
        //get selected meeting id
        meetingID =intent.getIntExtra("meeting_Id", 0);
        //set up handlers for the database
        DBHandle handle = new DBHandle(this);
        MeetingDB meeting = new MeetingDB();
        MaHandle att = new MaHandle(MeetingEntry.this);
        meeting = handle.getMeetingById(meetingID);

        //set the database name if already complete
        editAttendees.setText(att.getAttWithMeetingId(meetingID));
        editName.setText(meeting.name);
        editDT.setText(meeting.date);
        editNotes.setText(meeting.notes);
        lat = meeting.lat;
        lng = meeting.lng;
        editLocation.setText(meeting.location);
    }


    /**
     * deals with entry clicks
     * if someone presses the entries for date/time, location and attendees the information will be
     * entered
     * there are options to save and close
     * @param view
     */
    public void onClick(View view) {
        // saves the information to the database
        if (view == findViewById(R.id.btnSave)){
            DBHandle handle = new DBHandle(this);
            MeetingDB meeting = new MeetingDB();

            meeting.date = editDT.getText().toString();

            //If there is no name to the meeting give it one
            if(editName.getText().toString().contentEquals("")){
                meeting.name = getString(R.string.entry);
            }else{
                meeting.name = editName.getText().toString();
            }
            // save location
            meeting.notes = editNotes.getText().toString();
            meeting.lng = lng;
            meeting.lat = lat;
            meeting.location = editLocation.getText().toString();
            meeting.meetingID = meetingID;

            if (meetingID == 0){
                // new meetings to be saved
                meetingID= handle.insert(meeting);
                MaHandle groupHandle = new MaHandle(MeetingEntry.this);
                //get attendees from arraylist to save to the meeting attendees table
                for(int x= 0 ; x <groupAttendees.size(); x++){
                    MaDB ma = new MaDB();
                    ma.group_ID = groupHandle.getLastId();
                    ma.meeting = meetingID;
                    ma.attendees = groupAttendees.get(x);
                    groupHandle.insert(ma);

                }

                Toast.makeText(this,getString(R.string.add_meeting) ,Toast.LENGTH_SHORT).show();
                finish();
            }else{
                // for updating a meeting entry
                handle.update(meeting);
                MaHandle groupHandle = new MaHandle(MeetingEntry.this);
                for(int x= 0 ; x <groupAttendees.size(); x++){
                    MaDB ma = new MaDB();
                    ma.group_ID = groupHandle.getLastId();
                    ma.meeting = meetingID;
                    ma.attendees = groupAttendees.get(x);
                    groupHandle.insert(ma);

                }
                Toast.makeText(this,getString(R.string.update_meeting),Toast.LENGTH_SHORT).show();
                finish();
            }
        }else if (view== findViewById(R.id.btnClose)){
                finish();
        }
        //edit date time
        else if (view== findViewById(R.id.editDT)){
            Calendar calendar = Calendar.getInstance();
            //set to current date
            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);

            //create date picker
            DatePickerDialog datePickerDialog = new DatePickerDialog(MeetingEntry.this, MeetingEntry.this,
                    year, month, day);
            datePickerDialog.show();
            //send location information to map
        } else if (view == findViewById(R.id.editLoc)){
            //pass location to map
            Intent newIntent = new Intent(this, MapsActivity.class);
            newIntent.putExtra("Lat", lat);
            newIntent.putExtra("Lng", lng);
            newIntent.putExtra("Name", editLocation.getText());
            startActivityForResult(newIntent, 10);
            //give meeting id value to the attendee selector
        } else  if (view == findViewById(R.id.editAtt)){
            Intent pass = new Intent(view.getContext(), Att.class);
            pass.putExtra("idValue",meetingID);
            startActivityForResult(pass, 20);
        }

    }

    /**
     * collect the location and the attendees from the other activates
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Location information
        if (resultCode == RESULT_OK && requestCode == 10) {
            if (data.hasExtra("returnLat")) {
                lat = data.getExtras().getDouble("returnLat");
            }
            if (data.hasExtra("returnLng")) {
                lng = data.getExtras().getDouble("returnLng");
            }
            if (data.hasExtra("returnName")) {
                editLocation.setText(data.getExtras().getString("returnName"));
            }
        }
        if (resultCode == RESULT_OK && requestCode == 20) {
            if (data.hasExtra("returnAtt")) {
                ArrayList<Integer> group = data.getExtras().getIntegerArrayList("returnAtt");

                for(int i= 0 ; i <group.size(); i++){
                    if(!(groupAttendees.contains(group.get(i))))
                    groupAttendees.add(group.get(i));
                }
            }
            //display a string of selected attendees from arraylist before saving
            AttDBHandle li = new AttDBHandle(MeetingEntry.this);
            AttDB l = new AttDB();
            String tname = "";
            String name = "";
            Log.d("Data",String.valueOf(editAttendees.getText()));
                MaHandle att = new MaHandle(MeetingEntry.this);
                tname = att.getAttWithMeetingId(meetingID);
            for (Integer i : groupAttendees){
                 l = li.getAttById(i);
                  name += l.name + " ";

            }
            editAttendees.setText(tname + name);
        }
    }

    /**
     * select date
     * @param view the activity to be viewable from
     * @param y year
     * @param m month
     * @param d day
     */
    @Override
    public void onDateSet(DatePicker view, int y, int m, int d) {
        dayR = d;
        monthR = m;
        yearR = y;
        //once date is collected from user set the time to the current time
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        //then the user can select time
        TimePickerDialog timePickerDialog = new TimePickerDialog(MeetingEntry.this,MeetingEntry.this,
        hour, min, true);
        timePickerDialog.show();
    }

    /**
     * Select the time
     * @param view the activity to be viewable from
     * @param hourOfDay
     * @param minute
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String hours, minutes, days, months;
        hours = String.valueOf(hourOfDay);
        minutes= String.valueOf(minute);
        days = String.valueOf(dayR);
        months = String.valueOf(monthR + 1);
        String.valueOf(hourOfDay);
        // formats the date and time
        if(hourOfDay<10){
            hours = "0" + String.valueOf(hourOfDay);
        }
        if(minute<10){
            minutes = "0" + String.valueOf(minute);
        }
        if(dayR<10){
            days = "0" + String.valueOf(dayR);
        }
        if(monthR<10){
            months = "0" + String.valueOf(monthR +1);
        }

        //show the date and time
        editDT.setText(yearR + "-" + months + "-" + days + " " + hours + ":" + minutes + ":" + "00");
    }
}
