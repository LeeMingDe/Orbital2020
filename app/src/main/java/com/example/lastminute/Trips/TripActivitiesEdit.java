package com.example.lastminute.Trips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lastminute.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TripActivitiesEdit extends AppCompatActivity implements OnClickListener {

    EditText editActivityName, editActivityPlace, editActivityDate, editActivityTime, editActivityDescription;
    Button saveActivityButton, deleteActivityButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference dr;
    String pathToTripDoc;
    int h, min;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUIView();
        getPath();
        addTextWatcher();
        getDataFromActivity();
        openDatePicker();
        openTimePicker();

    }

    private void setUpUIView() {
        setContentView(R.layout.activity_trip_activities_edit);
        editActivityName = findViewById(R.id.editActivityNameInput);
        editActivityPlace = findViewById(R.id.editActivityPlaceInput);
        editActivityDate = findViewById(R.id.editActivityDateInput);
        editActivityTime = findViewById(R.id.editActivityTimeInput);
        editActivityDescription = findViewById(R.id.editActivityDescriptionInput);

        findViewById(R.id.saveActivityButton).setOnClickListener(this);
        findViewById(R.id.deleteActivityButton).setOnClickListener(this);
        saveActivityButton = findViewById(R.id.saveActivityButton);
        deleteActivityButton = findViewById(R.id.deleteActivityButton);
        editActivityDate.setInputType(InputType.TYPE_NULL);
        editActivityTime.setInputType(InputType.TYPE_NULL);

    }

    private void openDatePicker() {
        editActivityDate.setOnClickListener(new View.OnClickListener() {

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
                        TripActivitiesEdit.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String date = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL).format(c.getTime());
//                                updatedYear = year;
//                                updateMonth = month;
//                                updatedDay = dayOfMonth;
                                editActivityDate.setText(date);
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
        editActivityTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        TripActivitiesEdit.this,
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
                                editActivityTime.setText(time);
                            }
                        }, 12, 0, false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(h, min);
                timePickerDialog.show();
            }
        });
    }

    private void getPath() {
        String pathToActivityDoc = getIntent().getStringExtra("pathToActivityDoc");
        dr = db.document(pathToActivityDoc);
        pathToTripDoc = getIntent().getStringExtra("pathToTripDoc");
    }

    public void addTextWatcher() {
        editActivityName.addTextChangedListener(activityTextWatcher);
        editActivityPlace.addTextChangedListener(activityTextWatcher);
        editActivityDate.addTextChangedListener(activityTextWatcher);
        editActivityTime.addTextChangedListener(activityTextWatcher);
    }

    public void getDataFromActivity() {
        editActivityName.setText(getIntent().getStringExtra("activityName"));
        editActivityPlace.setText(getIntent().getStringExtra("activityPlace"));
        editActivityDate.setText(getIntent().getStringExtra("activityDate"));
        editActivityTime.setText(getIntent().getStringExtra("activityTime"));
        editActivityDescription.setText(getIntent().getStringExtra("activityDescription"));
    }

    private void deleteActivity() {
        dr.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(TripActivitiesEdit.this, "Delete Success", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent a = new Intent(TripActivitiesEdit.this, TripActivities.class);
                    a.putExtra("pathToTripDoc", pathToTripDoc);
                    startActivity(a);
                }
            }
        });
    }

    private void updateActivity() {
        String activityName = editActivityName.getText().toString().trim();
        String activityPlace = editActivityPlace.getText().toString().trim();
        String activityDate = editActivityDate.getText().toString().trim();
        String activityTime = editActivityTime.getText().toString().trim();
        String activityDescription = editActivityDescription.getText().toString().trim();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dr.set(new TripActivitiesDetails(activityName, activityPlace, activityDate, activityTime, activityDescription, userID)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(TripActivitiesEdit.this, "Save Activity", Toast.LENGTH_SHORT).show();
                finish();
                Intent a = new Intent(TripActivitiesEdit.this, TripActivities.class);
                a.putExtra("pathToTripDoc", pathToTripDoc);
                startActivity(a);
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case(R.id.deleteActivityButton):
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure?");
                builder.setMessage("Deletion is Permanent.");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteActivity();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
                break;
            case (R.id.saveActivityButton):
                updateActivity();
                break;
        }

    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TripActivitiesEdit.this, TripActivities.class);
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
            String name = editActivityName.getText().toString().trim();
            String place = editActivityPlace.getText().toString().trim();
            String date = editActivityDate.getText().toString().trim();
            String time = editActivityTime.getText().toString().trim();

            saveActivityButton.setEnabled(!name.isEmpty() && !place.isEmpty() && !date.isEmpty() && !time.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}