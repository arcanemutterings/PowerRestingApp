package com.example.nikosz.powerrestingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.concurrent.TimeUnit;

import cn.fanrunqi.waveprogress.WaveProgressView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int[] START_RGB = new int[]{244, 67, 54};
    private static final int[] MID_RGB = new int[]{255, 235, 59};
    private static final int[] END_RGB = new int[]{76, 175, 80};
    private static final double RED_DIFF_ONE = START_RGB[0] - MID_RGB[0];
    public static final double GREEN_DIFF_ONE = START_RGB[1] - MID_RGB[1];
    public static final double BLUE_DIFF_ONE = START_RGB[2] - MID_RGB[2];
    private static final double RED_DIFF_TWO = MID_RGB[0] - END_RGB[0];
    public static final double GREEN_DIFF_TWO = MID_RGB[1] - END_RGB[1];
    public static final double BLUE_DIFF_TWO = MID_RGB[2] - END_RGB[2];

    public static final String PREFERENCE_FILE_KEY = "PowerRestPref";
    public static final String MINUTES_KEY = "minutes";
    public static final String SECONDS_KEY = "seconds";
    public static final String SETS_KEY = "sets";
    public static final int REQUEST_CODE = 0;

    private int maxTimeMillis;
    private int currSet = 0;
    private int maxSet;

    TextView setsTextView;

    private WaveProgressView waveProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // get time from shared preferences
        SharedPreferences sharedPreferences = getApplicationContext()
                .getSharedPreferences(PREFERENCE_FILE_KEY, MODE_PRIVATE);
        int minutes = sharedPreferences.getInt(MINUTES_KEY, 3);
        int seconds = sharedPreferences.getInt(SECONDS_KEY, 0);
        maxSet = sharedPreferences.getInt(SETS_KEY, 3);
        maxTimeMillis = minutes*60*1000 + seconds*1000;


        // find views
        Button startRestButton = findViewById(R.id.rest_button);
        waveProgressView = findViewById(R.id.waveprogressbar);
        waveProgressView.setWave(60f, 200f);
        setsTextView = findViewById(R.id.sets_textview);

        // set up sets textview
        String setsString = getString(R.string.main_screen_sets, currSet, maxSet);
        setsTextView.setText(setsString);

        // set up progress bar
        waveProgressView.setText("#000000", 65);
        waveProgressView.setMaxProgress(maxTimeMillis);
        waveProgressView.setCurrent(0, millisecsToMinSec(maxTimeMillis));


        // set up counter
        final CountDownTimer countDownTimer = new CountDownTimer(maxTimeMillis, 200) {

            public void onTick(long millisUntilFinished) {
                int currentProgress = maxTimeMillis - (int) millisUntilFinished;
                waveProgressView.setCurrent(currentProgress, millisecsToMinSec(millisUntilFinished));
                waveProgressView.setWaveColor(getCurrentColor(getProgress(maxTimeMillis, millisUntilFinished)));
            }

            public void onFinish() {
                if (currSet < maxSet) currSet++;
                String setsString = getString(R.string.main_screen_sets, currSet, maxSet);
                setsTextView.setText(setsString);
            }
        };

        // set on click listener to button
        startRestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetView(waveProgressView);
                waveProgressView.setCurrent(0, millisecsToMinSec(maxTimeMillis));
                waveProgressView.setWaveColor(getCurrentColor(0));
                countDownTimer.start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_menu_item:
                Intent intent = new Intent(MainActivity.this, EditDetailsActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // restart activity
        if (requestCode == REQUEST_CODE) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }


    // HELPERS:

    /** Convert milliseconds to min:sec format */
    private String millisecsToMinSec(long millis) {
        return String.format("%01d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    private int getProgress(int maxTimeMillis, long millisUntilFinished) {
        return 100 - (int) millisUntilFinished * 100 / maxTimeMillis;
    }

    /** Gets progress from 0 to 100 and returns current gradient in hex String format */
    private String getCurrentColor(int progress) {
        int red;
        int green;
        int blue;
        //red to yellow
        if (progress < 50) {
            red = (int) (START_RGB[0] - (RED_DIFF_ONE / 50.0) * progress);
            green = (int) (START_RGB[1] - (GREEN_DIFF_ONE / 50.0) * progress);
            blue = (int) (START_RGB[2] - (BLUE_DIFF_ONE / 50.0) * progress);
        }
        // yellow to red
        else {
            red = (int) (MID_RGB[0] - (RED_DIFF_TWO / 50.0) * (progress - 50));
            green = (int) (MID_RGB[1] - (GREEN_DIFF_TWO / 50.0) * (progress - 50));
            blue = (int) (MID_RGB[2] - (BLUE_DIFF_TWO / 50.0) * (progress - 50));
        }

        return String.format("#%02x%02x%02x", red, green, blue);
    }

    private void resetView(View view) {
        view.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }


}
