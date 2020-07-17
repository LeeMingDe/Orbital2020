package com.example.lastminute.Trips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lastminute.MainActivity;
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

import java.util.HashMap;
import java.util.Map;

public class TripsEdit extends AppCompatActivity implements OnClickListener {

    EditText editTripName, editTripDestination, editStartDate, editEndDate;
    Button saveButton, deleteButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUIView();
        addTextWatcher();
        getDataFromTrip();

    }

    private void setUpUIView() {
        setContentView(R.layout.activity_trips_edit);
        editTripName = findViewById(R.id.editTripNameInput);
        editTripDestination = findViewById(R.id.editTripDestinationInput);
        editStartDate = findViewById(R.id.editStartDateInput);
        editEndDate = findViewById(R.id.editEndDateInput);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        findViewById(R.id.saveButton).setOnClickListener(this);
        findViewById(R.id.deleteButton).setOnClickListener(this);
    }

    private void getDataFromTrip() {
        editTripName.setText(getIntent().getStringExtra("tripName"));
        editTripDestination.setText(getIntent().getStringExtra("tripPlace"));
        editStartDate.setText(getIntent().getStringExtra("startDate"));
        editEndDate.setText(getIntent().getStringExtra("endDate"));
        String pathToTripDoc = getIntent().getStringExtra("pathToTripDoc");
        d = db.document(pathToTripDoc);
    }

    private void addTextWatcher() {
        editTripName.addTextChangedListener(tripTextWatcher);
        editTripDestination.addTextChangedListener(tripTextWatcher);
        editStartDate.addTextChangedListener(tripTextWatcher);
        editEndDate.addTextChangedListener(tripTextWatcher);
    }

    private void deleteTrip() {
        d.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(TripsEdit.this, "Delete Success", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(TripsEdit.this, MainActivity.class));
                }
            }
        });
    }

    private void updateTrip() {
        String tripName = editTripName.getText().toString().trim();
        String tripPlace = editTripDestination.getText().toString().trim();
        String startDate = editStartDate.getText().toString().trim();
        String endDate = editEndDate.getText().toString().trim();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        d.set(new TripEntryDetails(tripName, tripPlace, startDate, endDate, userID)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(TripsEdit.this, "Save Trip", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(TripsEdit.this, MainActivity.class));
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case(R.id.deleteButton):
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Are you sure?");
                builder.setMessage("Deletion is Permanent.");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteTrip();
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
            case (R.id.saveButton):
                updateTrip();
                break;
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TripsEdit.this, MainActivity.class);
        intent.putExtra("TripsPage", true);
        startActivity(intent);
    }

    private TextWatcher tripTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = editTripName.getText().toString().trim();
            String place = editTripDestination.getText().toString().trim();
            String startDate = editStartDate.getText().toString().trim();
            String endDate = editEndDate.getText().toString().trim();

            saveButton.setEnabled(!name.isEmpty() && !place.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}