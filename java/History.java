package com.example.a906771.projectmeetingapp;
/**
 * History.java
 * This class displays a list of History meetings
 * If a user clicks on a meeting then they can view the meeting
 *
 * @author Luke Cook (906771)
 */
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class History  extends AppCompatActivity {
    TextView meeting_Id;
    private SharedPreferences prefs;
    private String prefName = "MyPref";
    private static final String FONT_SIZE_KEY = "fontsize";
    private static final String FONT_COLOUR = "colourvalue";


    /**
     * when created it produces a toolbar with a up button
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarhist);
        setSupportActionBar(toolbar);
        //set up button
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
     * refresh the history meeting list
     * if you click on a meeting you can see the entry
     */
    @Override
    protected void onResume(){
        super.onResume();
        DBHandle handle = new DBHandle(this);
        ArrayList<HashMap<String, String>> meetingList =  handle.getMeetingListHistory();
        if(meetingList.size()!=0) {
            ListView lv = findViewById(R.id.listhist);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    meeting_Id = view.findViewById(R.id.meeting_ID);
                    String meetingID = meeting_Id.getText().toString();
                    //get the meeting entry by the id once clicked
                    Intent objIndent = new Intent(getApplicationContext(),MeetingEntry.class);
                    objIndent.putExtra("meeting_Id", Integer.parseInt(meetingID));
                    startActivity(objIndent);
                }
            });
            ListAdapter adapter = new SimpleAdapter( History.this,meetingList, R.layout.meeting_display, new String[] { "id","name"}, new int[] {R.id.meeting_ID, R.id.meeting_name}){
                public View getView (int position, View convertView, ViewGroup parent) {
                    View view = super.getView (position, convertView, parent);
                    TextView text = view.findViewById (R.id.meeting_name);
                    prefs = getSharedPreferences(prefName,MODE_PRIVATE);

                    //Set list to follow preferences
                    text.setTextSize(TypedValue.COMPLEX_UNIT_PX,prefs.getFloat(FONT_SIZE_KEY,  getResources().getInteger(R.integer.alter_font_size)));
                    text.setTextColor ((int) prefs.getFloat(FONT_COLOUR, getResources().getInteger(R.integer.alter_font_colour)) );
                    return view;
                }
            };
            lv.setAdapter(adapter);

        }
    }

}
