package com.example.lastminute.Trips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lastminute.MainActivity;
import com.example.lastminute.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TripsEntry extends AppCompatActivity {
    private Button addButton, cancelButton;
    private EditText  addTripNameInput, addTripPlaceInput, addStartDateInput, addEndDateInput;
//    int updatedYear, updateMonth, updatedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_entry);
        setUpUIView();
        addTextWatcher();
        cancelledEntry();
        doneEntry();
        editEntry();
        openDatePicker();
    }

    private void setUpUIView() {
        addTripNameInput =  (EditText) findViewById(R.id.addTripNameInput);
        addTripPlaceInput = (EditText) findViewById(R.id.addTripPlaceInput);
        addStartDateInput = (EditText) findViewById(R.id.addStartDateInput);
        addEndDateInput = (EditText) findViewById(R.id.addEndDateInput);
        addButton = (Button) findViewById(R.id.addButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        addStartDateInput.setInputType(InputType.TYPE_NULL);
        addEndDateInput.setInputType(InputType.TYPE_NULL);
    }

    private void openDatePicker() {
        addStartDateInput.setOnClickListener(new View.OnClickListener() {

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
                        TripsEntry.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String date = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
//                                updatedYear = year;
//                                updateMonth = month;
//                                updatedDay = dayOfMonth;
                                addStartDateInput.setText(date);
                            }
                        },
                        currentYear, currentMonth, currentDay);
//                startDatePickerDialog.getDatePicker().updateDate(updatedYear, updateMonth, updatedDay);
//                startDatePickerDialog.getDatePicker().init(updatedYear, updateMonth, updatedDay,null);
                startDatePickerDialog.show();
            }
        });

        addEndDateInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int currentYear = c.get(Calendar.YEAR);
                int currentMonth = c.get(Calendar.MONTH);
                int currentDay = c.get(Calendar.DAY_OF_MONTH);
//                updatedYear = currentYear;
//                updateMonth = currentMonth;
//                updatedDay = currentDay;

                DatePickerDialog endDatePickerDialog = new DatePickerDialog(
                        TripsEntry.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, month);
                                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String date = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
//                                updatedYear = year;
//                                updateMonth = month;
//                                updatedDay = dayOfMonth;
                                addEndDateInput.setText(date);
                            }
                        },
                        currentYear, currentMonth, currentDay);
//                startDatePickerDialog.getDatePicker().updateDate(updatedYear, updateMonth, updatedDay);
//                startDatePickerDialog.getDatePicker().init(updatedYear, updateMonth, updatedDay,null);
                endDatePickerDialog.show();
            }
        });

    }

    private void addTextWatcher() {
        addTripNameInput.addTextChangedListener(tripTextWatcher);
        addTripPlaceInput.addTextChangedListener(tripTextWatcher);
        addStartDateInput.addTextChangedListener(tripTextWatcher);
        addEndDateInput.addTextChangedListener(tripTextWatcher);
    }

    private void cancelledEntry() {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(TripsEntry.this, MainActivity.class);
                Toast.makeText(TripsEntry.this, "Entry cancelled", Toast.LENGTH_SHORT).show();
                startActivity(a);
            }
        });
    }

    private void doneEntry() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEntry(addTripNameInput.getText().toString(), addTripPlaceInput.getText().toString(), addStartDateInput.getText().toString(), addEndDateInput.getText().toString());
                Intent a = new Intent(TripsEntry.this, MainActivity.class);
                Toast.makeText(TripsEntry.this, "Trip Added", Toast.LENGTH_SHORT).show();
                startActivity(a);
            }
        });
    }

    private void addEntry(String name, String place, String start, String end) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TripEntryDetails entry = new TripEntryDetails(name, place, start, end, userId);
        FirebaseFirestore.getInstance()
                .collection("Trips")
                .add(entry);
    }

    private void editEntry() {
        Intent i =  getIntent();
        String tripName = i.getStringExtra("tripName");
        String tripPlace = i.getStringExtra("tripPlace");
        String startDate = i.getStringExtra("startDate");
        String endDate = i.getStringExtra("endDate");

        addTripNameInput.setText(tripName);
        addTripPlaceInput.setText(tripPlace);
        addStartDateInput.setText(startDate);
        addEndDateInput.setText(endDate);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TripsEntry.this, MainActivity.class);
        intent.putExtra("TripsPage", true);
        startActivity(intent);
    }

    private TextWatcher tripTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String name = addTripNameInput.getText().toString().trim();
            String place = addTripPlaceInput.getText().toString().trim();
            String startDate = addStartDateInput.getText().toString().trim();
            String endDate = addEndDateInput.getText().toString().trim();

            addButton.setEnabled(!name.isEmpty() && !place.isEmpty() && !startDate.isEmpty() && !endDate.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
