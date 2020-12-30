package com.example.a.projectmeetingapp;
/**
 * Settings.java
 * The user can select the colour and text size with sliders
 *
 * @author Luke Cook
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {


    private SharedPreferences prefs;
    private String prefName = "MyPref";
    private TextView listText;
    private SeekBar seekBar, seekColour;
    private float fontSize,rColour;

    private static final String FONT_SIZE_KEY = "fontsize";
    private static final String FONT_COLOUR = "colourvalue";

    /**
     * link with the xml file and and create two sliders one for colour and the other
     * for text size
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        listText = findViewById(R.id.sample_text);
        seekBar = findViewById(R.id.text_seek);
        seekColour = findViewById(R.id.colour_seek);


        //set preferences
        prefs = getSharedPreferences(prefName,MODE_PRIVATE);

       //set colour and size to last set values
        fontSize = prefs.getFloat(FONT_SIZE_KEY, getResources().getInteger(R.integer.alter_font_size));
        rColour = prefs.getFloat(FONT_COLOUR, getResources().getInteger(R.integer.alter_font_colour));

        //create seekbars and set sample text
        seekBar.setProgress(((int) fontSize)- getResources().getInteger(R.integer.alter_font_size));
        seekColour.setProgress(((int) rColour) - getResources().getInteger(R.integer.alter_font_colour));
        listText.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontSize);
        listText.setTextColor((int)rColour);

        //create slider for colour
        seekColour.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putFloat(FONT_COLOUR, listText.getCurrentTextColor());
                //save the preference
                editor.commit();
                Toast.makeText(getBaseContext(),
                        getString(R.string.font_colour_save),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //change sample text colour
                listText.setTextColor(progress + getResources().getInteger(R.integer.alter_font_colour));

            }
        });
        //create slider for text size
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = prefs.edit();


                editor.putFloat(FONT_SIZE_KEY, listText.getTextSize());
                //save the preference
                editor.commit();
                Toast.makeText(getBaseContext(),
                        getString(R.string.font_size_save)+" "+listText.getTextSize(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                //change sample text size
                listText.setTextSize(TypedValue.COMPLEX_UNIT_PX,progress + getResources().getInteger(R.integer.alter_font_size));
            }
        });
    }

}
