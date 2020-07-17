package com.example.lastminute.Trips;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lastminute.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TripActivitiesEntry extends AppCompatActivity {
    private Button addActivityButton, cancelActivityButton;
    private EditText addActivityNameInput, addActivityPlaceInput, addActivityDateInput, addActivityTimeInput, addActivityDescriptionInput;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference dr;
    String pathToTripDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUIView();
        getPathToTrip();
        cancelledEntry();
        doneEntry();
        editEntry();
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


