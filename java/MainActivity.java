package com.example.a.projectmeetingapp;
/**
 * MainActivity.java
 * This class displays up coming meetings in a list
 * A user can add a meeting by press a plus button
 *
 * @author Luke Cook
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity  {
    TextView meeting_Id;
    private SharedPreferences prefs;
    private String prefName = "MyPref";
    private static final String FONT_SIZE_KEY = "fontsize";
    private static final String FONT_COLOUR = "colourvalue";

    /**
     * on create adds the toolbar and add button for entry
      * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //used to add meeting entry
        FloatingActionButton addButton = findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MeetingEntry.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Add the main menu which includes a few activates that the user may look at
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * the available items in the menu are settings, history, Maps, and attendees to find meetings
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
        //settings
        if (id == R.id.action_settings) {
            Intent objIndent = new Intent(getApplicationContext(),Settings.class);
            startActivity(objIndent);
            //history
        } else if (id == R.id.action_history){
            Intent objIndent = new Intent(getApplicationContext(),History.class);
            startActivity(objIndent);
            //maps
        } else if (id == R.id.action_maps){
        Intent objIndent = new Intent(getApplicationContext(),MapsActivity.class);
            objIndent.putExtra("map_op", true);
        startActivity(objIndent);
        //attendees
    }else if (id == R.id.action_att){
            Intent objIndent = new Intent(getApplicationContext(),AttToMeeting.class);
            startActivity(objIndent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *creating list which can be clicked on to see each meeting entry
     */
    @Override
    protected void onResume(){
        super.onResume();
        DBHandle handle = new DBHandle(this);
        ArrayList<HashMap<String, String>> meetingList =  handle.getMeetingList();
        if(meetingList.size()!=0) {
            ListView lv = findViewById(R.id.list);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //once a meeting has been clicked they can view the entry
                    meeting_Id = view.findViewById(R.id.meeting_ID);
                    String meetingID = meeting_Id.getText().toString();
                    Intent objIndent = new Intent(getApplicationContext(),MeetingEntry.class);
                    objIndent.putExtra("meeting_Id", Integer.parseInt(meetingID));
                    startActivity(objIndent);
                }
            });
            ListAdapter adapter = new SimpleAdapter( MainActivity.this,meetingList, R.layout.meeting_display, new String[] { "id","name"}, new int[] {R.id.meeting_ID, R.id.meeting_name}){
                public View getView (int position, View convertView, ViewGroup parent) {
                    View view = super.getView (position, convertView, parent);
                    TextView text = view.findViewById (R.id.meeting_name);

                    // set the list to correct preferences
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
