package com.example.m1032561.pomodoroprototype;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    TextView textviewTime, textviewHeading;
    EditText editText;
    SeekBar seekBar;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textviewHeading = (TextView) findViewById(R.id.textview_heading);
        textviewTime = (TextView) findViewById(R.id.textview_time);
        editText = (EditText) findViewById(R.id.edittext);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        button = (Button) findViewById(R.id.button);

        textviewHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LogViewer.class));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleButtonClick();
            }
        });
    }

    private void handleButtonClick() {
        switch (button.getText().toString().toLowerCase()) {
            case "start":
                start_timer();
                presentState = "done";
                break;
            case "done":
                show_note_rate();
                presentState = "complete";
                break;
            case "complete":
                start_break();
                presentState = "finish";
                break;
            case "finish":
                stop_timer();
                presentState = "start";
                break;
        }
        button.setText(presentState.toUpperCase());
    }

    private Handler mHandler;
    private Runnable mRunnable;
    private long mSeconds;
    private String presentState;

    private long actualCountDownSec = 0;
    private long ellapsedTime = 0, rateTime = 0, rate = 0, breakTime = 0;
    private String note = "";
    private long id = 0;

    private void start_timer() {

        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        mHandler = new Handler();
        mSeconds = System.currentTimeMillis();
        actualCountDownSec = 10 * 60;//need to make it generic

        textviewTime.setVisibility(View.VISIBLE);
        editText.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.INVISIBLE);


        mRunnable = new Runnable() {
            @Override
            public void run() {
                long seconds = 0;

                if (presentState.equals("start")) {
                    seconds = actualCountDownSec - (System.currentTimeMillis() - mSeconds) / 1000;
                } else {
                    seconds = (System.currentTimeMillis() - mSeconds) / 1000;
                }

                if (seconds < 0) {
                    seconds = -seconds;
                    textviewTime.setTextColor(Color.RED);
                    textviewTime.setText(String.format("-%02d:%02d", seconds / 60, seconds % 60));
                } else {
                    textviewTime.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
                }
                mHandler.postDelayed(mRunnable, 1000L);
            }
        };

        mHandler.postDelayed(mRunnable, 0L);
    }

    private void show_note_rate() {
        ellapsedTime = (System.currentTimeMillis() - mSeconds) / 1000;
        _insert_log();

        mSeconds = System.currentTimeMillis();

        textviewTime.setVisibility(View.INVISIBLE);
        editText.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.VISIBLE);

    }

    private void start_break() {
        rateTime = (System.currentTimeMillis() - mSeconds) / 1000;
        rate = seekBar.getProgress();
        note = editText.getText().toString();
        _update_log();

        mSeconds = System.currentTimeMillis();
        mHandler.postDelayed(mRunnable, 0L);

        textviewTime.setTextColor(Color.GREEN);

        seekBar.setProgress(seekBar.getMax() / 2);
        textviewTime.setVisibility(View.VISIBLE);
        editText.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.INVISIBLE);

    }

    private void stop_timer() {
        breakTime = (System.currentTimeMillis() - mSeconds) / 1000;
        _update_log();

        /*Log.d("actual_time:",""+actualCountDownSec);
        Log.d("ellaped_time:",""+ellapsedTime);
        Log.d("rate_time",""+rateTime);
        Log.d("note:",""+note);
        Log.d("rate:",""+rate);
        Log.d("break_time:",""+breakTime);*/
        _show_log();


        mHandler.removeCallbacks(mRunnable);
        textviewTime.setTextColor(Color.BLACK);
        textviewTime.setText("00:00");
    }


    private void _insert_log() {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("actual_dur", actualCountDownSec + "");
        cv.put("ellapsed_dur", ellapsedTime + "");
        cv.put("rate_dur", rateTime + "");
        cv.put("rate", rate + "");
        cv.put("note", note + "");
        cv.put("breakTime", breakTime + "");


        id = db.insert(DBHelper.tablename, null, cv);


        Toast.makeText(MainActivity.this,
                "Inserted successfully", Toast.LENGTH_SHORT)
                .show();

        db.close();
    }

    private void _update_log() {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("actual_dur", actualCountDownSec + "");
        cv.put("ellapsed_dur", ellapsedTime + "");
        cv.put("rate_dur", rateTime + "");
        cv.put("rate", rate + "");
        cv.put("note", note + "");
        cv.put("breakTime", breakTime + "");
        db.update(DBHelper.tablename, cv, "id=" + id, null);
        db.close();

        Toast.makeText(MainActivity.this,
                "Updated successfully", Toast.LENGTH_SHORT)
                .show();
    }

    private void _show_log() {
        List<HashMap<String, String>> data = new DBHelper(getApplicationContext()).getFullLog();
        if (data.size() != 0) {   // if data is present do this
            for (HashMap<String, String> map : data) {
                Log.d("RECORD:", map.toString());
            }
            Toast.makeText(MainActivity.this,
                    "Records found:" + data.size(), Toast.LENGTH_SHORT)
                    .show();
        } else {  // if no data is present give the message no data in database.
            Toast.makeText(MainActivity.this,
                    "No data in database", Toast.LENGTH_SHORT)
                    .show();
        }
    }


}
