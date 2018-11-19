package com.example.nikosz.powerrestingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class EditDetailsActivity extends AppCompatActivity {
    private static final String TAG = EditDetailsActivity.class.getSimpleName();
    public static final String PREFERENCE_FILE_KEY = "PowerRestPref";
    public static final String MINUTES_KEY = "minutes";
    public static final String SECONDS_KEY = "seconds";
    public static final String SETS_KEY = "sets";

    private int minutes = 0;
    private int seconds = 0;
    Integer sets;
    private SharedPreferences sharedPreferences;
    EditText numberOfSetEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_details);

        // find views
        NumberPicker minutePicker = findViewById(R.id.minutes_numberpicker);
        final NumberPicker secondPicker = findViewById(R.id.seconds_numberpicker);
        numberOfSetEditText = findViewById(R.id.sets_number_edittext);

        // get shared preferences
        Context context = getApplicationContext();
        sharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        minutes = sharedPreferences.getInt(MINUTES_KEY, 3);
        seconds = sharedPreferences.getInt(SECONDS_KEY, 0);
        sets = sharedPreferences.getInt(SETS_KEY, 3);

        // setup number pickers
        minutePicker.setMaxValue(9);
        minutePicker.setMinValue(0);
        minutePicker.setValue(minutes);
        secondPicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setValue(seconds);

        // setup sets edittext
        numberOfSetEditText.setText(sets.toString());

        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                minutes = newVal;
            }
        });

        secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                seconds = newVal;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_action:
                String setsString = numberOfSetEditText.getText().toString();
                if (TextUtils.isEmpty(setsString)) {
                    Toast.makeText(this, "Number of sets missing.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                sets = Integer.parseInt(setsString);

                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(MINUTES_KEY, minutes);
                editor.putInt(SECONDS_KEY, seconds);
                editor.putInt(SETS_KEY, sets);
                editor.apply();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
