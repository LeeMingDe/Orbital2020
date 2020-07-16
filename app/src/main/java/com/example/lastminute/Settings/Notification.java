package com.example.lastminute.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.lastminute.R;

public class Notification extends AppCompatActivity {
    Toolbar settingsToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setUpUIView();
        customizeToolbar();
    }

    private void setUpUIView() {
        settingsToolbar = findViewById(R.id.settingsToolbar);

    }

    private void customizeToolbar() {
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setTitle("NOTIFICATION");
        settingsToolbar.setTitleTextColor(getResources().getColor(R.color.diaryColor));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settingsToolbar.setTitleTextAppearance(this, R.style.gillsan_condensed);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}