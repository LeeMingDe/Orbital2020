package com.example.lastminute;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.lastminute.Diary.DiaryEntryDetails;
import com.example.lastminute.Diary.DiaryFragment;
import com.example.lastminute.Diary.PhotoUploadDetails;
import com.example.lastminute.Maps.MapFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // AddTrips Button
        TripsFragment tripsFragment = new TripsFragment();
        FragmentManager fm = getSupportFragmentManager();
//        fm.beginTransaction().add(R.id.mainLayout, tripsFragment).commit();

        // Goback button
        AddTripsFragment tripsFragment1 = new AddTripsFragment();
        FragmentManager fm1 = getSupportFragmentManager();
//        fm.beginTransaction().add(R.id.mainLayout, tripsFragment).commit();

        // For bottom navigation
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // To start app beginning with Trips Page
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new TripsFragment()).commit();

        undoDelete();
        toDiaryFragment();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_trips:
                            selectedFragment = new TripsFragment();
                            break;
                        case R.id.nav_dairy:
                            selectedFragment = new DiaryFragment();
                            break;
                        case R.id.nav_reviews:
                            if (isServicesWorking()) {
                                selectedFragment = new MapFragment();
                            } else {
                                Toast.makeText(MainActivity.this, "Unable to display map",
                                        Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case R.id.nav_converter:
                            selectedFragment = new ConverterFragment();
                            break;
                        case R.id.nav_settings:
                            selectedFragment = new SettingsFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };

    private void undoDelete() {
        if (getIntent().getExtras()!= null && getIntent().getExtras().getBoolean("Undo")) {
            String title = getIntent().getStringExtra("titleDetails");
            String text = getIntent().getStringExtra("textDetails");
            String documentPath = getIntent().getStringExtra("documentPath");
            Timestamp time = getIntent().getParcelableExtra("dateCreated");
            final ArrayList<String> arr = getIntent().getStringArrayListExtra("imageList");
            final DocumentReference docRef = FirebaseFirestore.getInstance().document(documentPath);
            final DiaryEntryDetails entry = new DiaryEntryDetails(title, text, time,
                    FirebaseAuth.getInstance().getCurrentUser().getUid());
            Snackbar.make(findViewById(android.R.id.content),"Item deleted",Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                docRef.set(entry);
                                for (int k = 0; k < arr.size(); k++) {
                                    PhotoUploadDetails photoUploadDetails = new PhotoUploadDetails(arr.get(k));
                                    docRef.collection("Upload").add(photoUploadDetails);

                                }
                            }
                        })
                    .show();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DiaryFragment()).commit();
            bottomNav.getMenu().findItem(R.id.nav_dairy).setChecked(true);
        }
    }

    private void toDiaryFragment() {
        if (getIntent().getExtras()!= null && getIntent().getExtras().getBoolean("DiaryPage")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DiaryFragment()).commit();
            bottomNav.getMenu().findItem(R.id.nav_dairy).setChecked(true);
        }
    }

    public boolean isServicesWorking() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,
                    available, 9001);
            dialog.show();
        }
        return false;
    }

}
