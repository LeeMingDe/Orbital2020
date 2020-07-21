package com.example.lastminute.Trips;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.lastminute.MainActivity;
import com.example.lastminute.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TripActivitiesEntry extends AppCompatActivity {
    private Button addActivityButton, cancelActivityButton;
    private EditText addActivityNameInput, addActivityPlaceInput, addActivityDateInput, addActivityTimeInput, addActivityDescriptionInput;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference dr;
    String pathToTripDoc;
    int h, min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUIView();
        getPathToTrip();
        cancelledEntry();
        doneEntry();
        editEntry();
        openDatePicker();
        openTimePicker();
    }

    private void setUpUIView() {
        setContentView(R.layout.activity_trip_activities_entry);
        addActivityNameInput = (EditText) findViewById(R.id.addActivityNameInput);
        addActivityPlaceInput = (EditText) findViewById(R.id.addActivityPlaceInput);
        addActivityDateInput = (EditText) findViewById(R.id.addActivityDateInput);
        addActivityTimeInput = (EditText) findViewById(R.id.addActivityTimeInput);
        addActivityDescriptionInput = (EditText) findViewById(R.id.addActivityDescriptionInput);
        addActivityButton = (Button) findViewById(R.id.addActivityButton);
        cancelActivityButton = (Button) findViewById(R.id.cancelActivityButton);

        addActivityNameInput.addTextChangedListener(activityTextWatcher);
        addActivityPlaceInput.addTextChangedListener(activityTextWatcher);
        addActivityDateInput.addTextChangedListener(activityTextWatcher);
        addActivityTimeInput.addTextChangedListener(activityTextWatcher);
        addActivityDateInput.setInputType(InputType.TYPE_NULL);
        addActivityTimeInput.setInputType(InputType.TYPE_NULL);

    }

    private void openDatePicker() {
        addActivityDateInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int currentYear = c.get(Calendar.YEAR);
                int currentMonth = c.get(Calendar.MONTH);
                int currentDay = c.get(Calendar.DAY_OF_MONTH);
//                updatedYear = currentYear;
//                updateMonth = currentMonth;
//                updatedDay = currentDay;

                DatePickerDialog startDatePickerDialog = new DatePickerDialog(
                        TripActivitiesEntry.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String date = java.text.DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
//                                updatedYear = year;
//                                updateMonth = month;
//                                updatedDay = dayOfMonth;
                                addActivityDateInput.setText(date);
                            }
                        },
                        currentYear, currentMonth, currentDay);
//                startDatePickerDialog.getDatePicker().updateDate(updatedYear, updateMonth, updatedDay);
//                startDatePickerDialog.getDatePicker().init(updatedYear, updateMonth, updatedDay,null);
                startDatePickerDialog.show();
            }
        });
    }

    private void openTimePicker() {
        addActivityTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        TripActivitiesEntry.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                h = hourOfDay;
                                min = minute;
                                String time = "";
                                if (hourOfDay < 10) {
                                    time += "0"+ hourOfDay;
                                } else {
                                    time += hourOfDay;
                                }
                                time += ":";
                                if (minute < 10) {
                                    time += "0"+ minute;
                                } else {
                                    time += minute;
                                }
                                addActivityTimeInput.setText(time);
                            }
                        }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(h, min);
                timePickerDialog.show();
            }
        });
    }

    private void getPathToTrip() {
        pathToTripDoc = getIntent().getStringExtra("pathToTripDoc");
        dr = db.document(pathToTripDoc);
    }

    private void cancelledEntry() {
        cancelActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(TripActivitiesEntry.this, TripActivities.class);
                a.putExtra("pathToTripDoc", pathToTripDoc);
                Toast.makeText(TripActivitiesEntry.this, "Entry cancelled", Toast.LENGTH_SHORT).show();
                startActivity(a);
            }
        });
    }

    private void doneEntry() {
        addActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEntry(addActivityNameInput.getText().toString(), addActivityPlaceInput.getText().toString(), addActivityDateInput.getText().toString(),
                        addActivityTimeInput.getText().toString(), addActivityDescriptionInput.getText().toString());
                Intent a = new Intent(TripActivitiesEntry.this, TripActivities.class);
                a.putExtra("pathToTripDoc", pathToTripDoc);
                Toast.makeText(TripActivitiesEntry.this, "Activity Added", Toast.LENGTH_SHORT).show();
                startActivity(a);
            }
        });
    }

    private void addEntry(String name, String place, String start, String end, String description) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TripActivitiesDetails entry = new TripActivitiesDetails(name, place, start, end, description, userId);
        db.collection("Trips").document(dr.getId()).collection("Activities").add(entry);
    }

    private void editEntry() {
        Intent i =  getIntent();
        String activityName = i.getStringExtra("activityName");
        String activityPlace = i.getStringExtra("activityPlace");
        String activityDate = i.getStringExtra("activityDate");
        String activityTime = i.getStringExtra("activityTime");
        String activityDescription = i.getStringExtra("activityDescription");

        addActivityNameInput.setText(activityName);
        addActivityPlaceInput.setText(activityPlace);
        addActivityDateInput.setText(activityDate);
        addActivityTimeInput.setText(activityTime);
        addActivityDescriptionInput.setText(activityDescription);
    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TripActivitiesEntry.this, TripActivities.class);
        intent.putExtra("ActivitiesPage", true);
        intent.putExtra("pathToTripDoc", pathToTripDoc);
        startActivity(intent);
    }

    private TextWatcher activityTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = addActivityNameInput.getText().toString().trim();
            String place = addActivityPlaceInput.getText().toString().trim();
            String date = addActivityDateInput.getText().toString().trim();
            String time = addActivityTimeInput.getText().toString().trim();

            addActivityButton.setEnabled(!name.isEmpty() && !place.isEmpty() && !date.isEmpty() && !time.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}


