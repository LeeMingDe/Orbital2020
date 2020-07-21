package com.example.lastminute.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.lastminute.Converter.ConverterFragment;
import com.example.lastminute.Diary.DiaryEntry;
import com.example.lastminute.Diary.ViewDiaryEntry;
import com.example.lastminute.MainActivity;
import com.example.lastminute.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Help extends AppCompatActivity {
    Toolbar settingsToolbar;
    ArrayList<HelpDetails> list;
    private RecyclerView helpRecycler;
    private HelpAdapter helpRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setUpUIView();
        customizeToolbar();
        initalizeRecyclerView();
    }

    private void setUpUIView() {
        settingsToolbar = findViewById(R.id.settingsToolbar);
        helpRecycler = findViewById(R.id.helpRecycler);
    }

    private void initalizeRecyclerView() {
        list = new ArrayList<>();
        list.add(new HelpDetails("What is lastMinute?"));
        list.add(new HelpDetails("Trips"));
        list.add(new HelpDetails("Diary"));
        list.add(new HelpDetails("Map"));
        list.add(new HelpDetails("Currency Converter"));
        layoutManager = new LinearLayoutManager(this);
        helpRecyclerAdapter = new HelpAdapter(list);
        helpRecycler.setLayoutManager(layoutManager);
        helpRecycler.setAdapter(helpRecyclerAdapter);

        helpRecyclerAdapter.setOnItemClickListener(new HelpAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {
                    Intent intent0 = new Intent(Help.this, AboutLastMinute.class);
                    startActivity(intent0);
                    overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                    finish();

                } else if (position == 1) {
                    Intent intent1 = new Intent(Help.this, AboutTrips.class);
                    startActivity(intent1);
                    overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                    finish();

                } else if (position == 2) {
                    Intent intent2 = new Intent(Help.this, AboutDiary.class);
                    startActivity(intent2);
                    overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                    finish();

                } else if (position == 3) {
                    Intent intent3 = new Intent(Help.this, AboutMaps.class);
                    startActivity(intent3);
                    overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                    finish();

                } else {
                    Intent intent4 = new Intent(Help.this, AboutCurrencyConverter.class);
                    startActivity(intent4);
                    overridePendingTransition(R.transition.slide_in_right, R.transition.slide_out_left);
                    finish();

                }
            }
        });
    }

    private void customizeToolbar() {
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setTitle("HELP");
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