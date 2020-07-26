package com.example.lastminute.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lastminute.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


public class Notification extends AppCompatActivity {
    private Toolbar settingsToolbar;
    private SwitchCompat enableNotification;
    private TextView day, selectTime;
    private Calendar calendar;
    private Context mContext = this;
    private NotificationSettingsDetails settingsDetails;
    private DocumentReference documentReference;
    private int timeHour = 20;
    private int timeMinute = 0;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    private static boolean notificationEnabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        calendar = calendar.getInstance();
        documentReference = FirebaseFirestore.getInstance().collection("Settings")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        setUpUIView();
        check();
        checkSwitch();
        timePicker();
        customizeToolbar();
    }

    private void setUpUIView() {
        settingsToolbar = findViewById(R.id.settingsToolbar);
        enableNotification = findViewById(R.id.enableNotification);
        day = (TextView) findViewById(R.id.day);
        selectTime = (TextView) findViewById(R.id.selectTime);
    }

    private void customizeToolbar() {
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setTitle("Notification & Reminder");
        settingsToolbar.setTitleTextColor(getResources().getColor(R.color.diaryColor));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settingsToolbar.setTitleTextAppearance(this, R.style.roboto_medium);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setBold() {
        SpannableStringBuilder forTime = new SpannableStringBuilder("Day" + "\n" + "Daily");
        StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
        forTime.setSpan(bold, 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        day.setText(forTime);

        forTime = new SpannableStringBuilder("Time" + "\n" + "20:00");
        forTime.setSpan(bold, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        selectTime.setText(forTime);
    }

    private void check() {
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        timeHour =  documentSnapshot.getLong("hour").intValue();
                        timeMinute = documentSnapshot.getLong("minute").intValue();
                        notificationEnabled = documentSnapshot.getBoolean("enabled");
                        SpannableStringBuilder forTime = new SpannableStringBuilder("Day" + "\n" + "Daily");
                        StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
                        forTime.setSpan(bold, 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        day.setText(forTime);
                        forTime = new SpannableStringBuilder("Time" + "\n"
                                + String.format("%02d", timeHour) + ":"
                                + String.format("%02d", timeMinute));
                        forTime.setSpan(bold, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        selectTime.setText(forTime);
                        if (notificationEnabled) {
                            enableNotification.performClick();
                        }

                    } else {
                        setBold();
                        settingsDetails = new NotificationSettingsDetails(false, timeHour, timeMinute);
                        documentReference.set(settingsDetails);
                    }
                }
            }
        });
    }

    private void checkSwitch() {
        enableNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enableNotification.isChecked()) {
                    notificationEnabled = true;
                    day.setTextColor(Color.rgb(0,0,0));
                    selectTime.setTextColor(Color.rgb(0,0,0));
                    day.setClickable(true);
                    selectTime.setClickable(true);
                    settingsDetails = new NotificationSettingsDetails(true, timeHour, timeMinute);
                    documentReference.set(settingsDetails);
                    createAlarm(timeHour,timeMinute);
                } else {
                    if (alarmMgr != null) {
                        alarmIntent.cancel();
                    }
                    notificationEnabled = false;
                    day.setTextColor(Color.rgb(211,211,211));
                    selectTime.setTextColor(Color.rgb(211,211,211));
                    day.setClickable(false);
                    selectTime.setClickable(false);
                    settingsDetails = new NotificationSettingsDetails(false, timeHour, timeMinute);
                    documentReference.set(settingsDetails);
                }
            }
        });
    }

    private void timePicker() {
        final int hour = Calendar.HOUR_OF_DAY;
        final int minute = Calendar.MINUTE;
        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationEnabled) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                            SpannableStringBuilder forTime = new SpannableStringBuilder("Time"
                                    + "\n" + String.format("%02d", hourOfDay) + ":" + String.format("%02d", min));
                            StyleSpan bold = new StyleSpan(android.graphics.Typeface.BOLD);
                            forTime.setSpan(bold, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                            selectTime.setText(forTime);
                            settingsDetails = new NotificationSettingsDetails(true, hourOfDay, min);
                            documentReference.set(settingsDetails);
                        }
                    }, hour, minute, false);
                    timePickerDialog.show();
                }
            }
        });
        createAlarm(timeHour, timeMinute);
    }

    private void createAlarm(int hr, int min) {
        calendar.set(Calendar.HOUR_OF_DAY, hr);
        calendar.set(Calendar.MINUTE, min);

        Intent intent1 = new Intent(Notification.this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(Notification.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr = (AlarmManager) Notification.this.getSystemService(Notification.this.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        Toast.makeText(Notification.this, "Reminder set at " + String.format("%02d", hr) + ":" + String.format("%02d", min), Toast.LENGTH_SHORT).show();
    }
}