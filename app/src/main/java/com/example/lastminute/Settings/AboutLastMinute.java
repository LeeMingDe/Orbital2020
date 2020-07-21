package com.example.lastminute.Settings;

import android.content.Intent;
import android.os.Bundle;

import com.example.lastminute.MainActivity;
import com.example.lastminute.Trips.TripsEdit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.example.lastminute.R;

public class AboutLastMinute extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_last_minute);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AboutLastMinute.this, Help.class);
        startActivity(intent);
        overridePendingTransition(R.transition.slide_in_left, R.transition.slide_out_right);
//        finish();
    }

//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.transition.slide_in_left,R.transition.slide_out_right);
//    }
}