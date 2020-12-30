package com.example.a.projectmeetingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * AttToMeeting.java
 *This class displays a list of all attendees, if you click on an attendee then a dialog box will
 * appear with all the meeting that they are in
 *
 * @author Luke Cook
 */
public class AttToMeeting extends AppCompatActivity  {
    TextView meeting_Id;
    private SharedPreferences prefs;
    private String prefName = "MyPref";
    private static final String FONT_SIZE_KEY = "fontsize";
    private static final String FONT_COLOUR = "colourvalue";
    private int attendee;

    /**
     * when created it produces a toolbar with a up button
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.att_meeting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbaratt_meeting);
        setSupportActionBar(toolbar);
        // create the up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * create a menu using the common menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    /**
     * Allows this activity to access the setting menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settingscom) {
            Intent objIndent = new Intent(getApplicationContext(),Settings.class);
            startActivity(objIndent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * the attendees list is updated if necessary
     */
    @Override
    protected void onResume(){
        super.onResume();
        AttDBHandle handle = new AttDBHandle(this);
        list(handle);
    }

    /**
     * shows the list of attendees
     * if you click on an attendee a dialog box will appear with the meetings that that attendee has
     * or will be in
     * @param handle uses to use and query the attendees
     */
    private void list (final AttDBHandle handle){
        ArrayList<HashMap<String, String>> attList =  handle.getAttList();
        if(attList.size()!=0) {
            ListView lv = findViewById(R.id.listatt);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MaHandle groupHandle = new MaHandle(AttToMeeting.this);
                    Log.d("Data", Integer.toString(groupHandle.getLastId()));
                    meeting_Id = view.findViewById(R.id.meeting_ID);
                    String att = meeting_Id.getText().toString();
                    //get attendee id
                    attendee = Integer.parseInt(att);

                    //once an attendee is click on the dialog box will appear
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AttToMeeting.this);

                    final TextView text = new TextView(AttToMeeting.this);

                    // use the attendee id to get the list of meeting they are in
                    text.setText(groupHandle.getMeetingWithAttId(attendee));

                    //For consistency of the activity the text should follow the preferences
                    text.setTextSize(TypedValue.COMPLEX_UNIT_PX,prefs.getFloat(FONT_SIZE_KEY, getResources().getInteger(R.integer.alter_font_size)));
                    text.setTextColor ((int) prefs.getFloat(FONT_COLOUR, getResources().getInteger(R.integer.alter_font_colour)) );

                    //Allows the information to be scrollable if an attendee is in a lot of meetings
                    text.setScroller(new Scroller(AttToMeeting.this));
                    text.setMaxLines(5);
                    text.setVerticalScrollBarEnabled(true);
                    text.setMovementMethod(new ScrollingMovementMethod());
                    alertDialogBuilder.setView(text);


                    // show the meeting
                    alertDialogBuilder.setMessage(getString(R.string.list_meeting)).setPositiveButton(getString(R.string.list_close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    // create small window
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    // show the window
                    alertDialog.show();


                }
            });
            ListAdapter adapter = new SimpleAdapter( AttToMeeting.this,attList, R.layout.meeting_display, new String[] { "id","name"}, new int[] {R.id.meeting_ID, R.id.meeting_name}){
                public View getView (int position, View convertView, ViewGroup parent) {
                    View view = super.getView (position, convertView, parent);
                    TextView text = view.findViewById (R.id.meeting_name);

                    //Set list to follow preferences
                    prefs = getSharedPreferences(prefName,MODE_PRIVATE);
                    text.setTextSize(TypedValue.COMPLEX_UNIT_PX,prefs.getFloat(FONT_SIZE_KEY, getResources().getInteger(R.integer.alter_font_size)));
                    text.setTextColor ((int) prefs.getFloat(FONT_COLOUR, getResources().getInteger(R.integer.alter_font_colour)) );
                    return view;
                }
            };
            lv.setAdapter(adapter);

        }
    }
}
