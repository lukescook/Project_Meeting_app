package com.example.a.projectmeetingapp;
/**
 * Att.java
 * This class displays a list of Attendees where a user can add people to the list
 * If a user clicks on an attendee then they it will be added to the meeting
 *
 * @author Luke Cook
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class Att extends AppCompatActivity  {
    TextView meeting_Id;
    int meeting_ID = 0;

    ArrayList<Integer> groupAttendees = new ArrayList<>();
    private int attendee;

    private SharedPreferences prefs;
    private String prefName = "MyPref";
    private static final String FONT_SIZE_KEY = "fontsize";
    private static final String FONT_COLOUR = "colourvalue";

    /**
     * on creation the att class sets up the add button for adding new attendees
     * and sets up a dialog box to add the attendees names
     * it will also show a list of all attendees
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.att_entry);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaratt);
        setSupportActionBar(toolbar);

        //Get the id value of the meeting to link the attendees to the meeting
        Bundle intent = getIntent().getExtras();
        meeting_ID =intent.getInt("idValue");

        //An add button to add attendees to meetings
        FloatingActionButton addButton = findViewById(R.id.addatt);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //When the user clicks add a small window will appear
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Att.this);

                //The small window will have a text area to enter someones name
                final EditText text = new EditText(Att.this);
                alertDialogBuilder.setView(text);

                //Have an ok and cancel for the entering of the name
                alertDialogBuilder.setMessage(getString(R.string.name_entry)).setPositiveButton(getString(R.string.btn_save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!(text.getText().toString().contentEquals(""))) {

                            //Use the attendee database handle
                            AttDBHandle handle = new AttDBHandle(Att.this);
                            // an attendee object
                            AttDB att = new AttDB();

                            //set the name of the user
                            att.name = text.getText().toString();
                            Toast.makeText(Att.this, att.name + " " + getString(R.string.name_added) ,Toast.LENGTH_SHORT).show();

                            //insert attendee into the database
                            handle.insert(att);
                            //update the attendees list
                            list(handle);
                        }
                    }
                }).setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Attendees not added
                        Toast.makeText(Att.this,getString(R.string.name_entry_cancel),Toast.LENGTH_SHORT).show();
                    }
                });

                // create the small window
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show the window
                alertDialog.show();
            }
        });
    }

    /**
     * Refreshes the attendees list if necessary
     */
    @Override
    protected void onResume(){
        super.onResume();
        AttDBHandle handle = new AttDBHandle(this);
        //refresh the attendees list
        list(handle);
    }

    /**
     * used to update attendees list from the database
     * also saves new attendees to be added to a meeting in a arraylist
     * @param handle - collection of attendees
     */
    private void list (AttDBHandle handle){
        ArrayList<HashMap<String, String>> attList =  handle.getAttList();
        if(attList.size()!=0) {
            ListView lv = findViewById(R.id.listatt);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // use the attendees group handle to group attendees with meetings
                    MaHandle groupHandle = new MaHandle(Att.this);
                    Log.d("Data", Integer.toString(groupHandle.getLastId()));
                    meeting_Id = view.findViewById(R.id.meeting_ID);
                    String att = meeting_Id.getText().toString();
                    //get attendees id
                    attendee = Integer.parseInt(att);
                    //check the the meeting id is not already with the attendee in the database
                    //check that the attendee is not in the arraylist
                    if(groupHandle.checkExistence(meeting_ID,attendee) && !(groupAttendees.contains(attendee))) {
                        //An arraylist of attendees ids ready to be saved
                        Toast.makeText(Att.this, getString(R.string.added_to_meeting) ,Toast.LENGTH_SHORT).show();
                        groupAttendees.add(attendee);
                    }
                }
            });
            ListAdapter adapter = new SimpleAdapter( Att.this,attList, R.layout.meeting_display, new String[] { "id","name"}, new int[] {R.id.meeting_ID, R.id.meeting_name}){
                public View getView (int position, View convertView, ViewGroup parent) {
                    View view = super.getView (position, convertView, parent);
                    TextView text = view.findViewById (R.id.meeting_name);

                    //set the attendees id's to the set preferences
                    prefs = getSharedPreferences(prefName,MODE_PRIVATE);
                    text.setTextSize(TypedValue.COMPLEX_UNIT_PX,prefs.getFloat(FONT_SIZE_KEY, getResources().getInteger(R.integer.alter_font_size)));
                    text.setTextColor ((int) prefs.getFloat(FONT_COLOUR, getResources().getInteger(R.integer.alter_font_colour)) );
                    return view;
                }
            };
            lv.setAdapter(adapter);

        }
    }
    /**
     * gives the attendees arraylist to meeting entry
     */
    @Override
    public void finish(){
        Intent data = new Intent();
        //send the attendees list to entry to be saved
        data.putExtra("returnAtt", groupAttendees);

        setResult(RESULT_OK, data);
        super.finish();
    }

}
