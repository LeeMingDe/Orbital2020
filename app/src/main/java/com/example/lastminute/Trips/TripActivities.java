package com.example.lastminute.Trips;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lastminute.Login.LoginPage;
import com.example.lastminute.MainActivity;
import com.example.lastminute.R;
import com.example.lastminute.Trips.TripActivitiesDetails;
import com.example.lastminute.Trips.TripActivitiesEdit;
import com.example.lastminute.Trips.TripActivitiesEntry;
import com.example.lastminute.Trips.TripActivitiesRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class TripActivities extends AppCompatActivity implements FirebaseAuth.AuthStateListener, TripActivitiesRecyclerAdapter.TripActivitiesListener {

    private  FloatingActionButton addActivitiesButton;
    private RecyclerView activities_recycler;
    TripActivitiesRecyclerAdapter addActivitiesAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference dr;
    String pathToTripDoc;

    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_activities);
        pathToTripDoc = getIntent().getStringExtra("pathToTripDoc");
//        System.out.println("HELLO");
//        System.out.println(path);
        dr = db.document(pathToTripDoc);
        setUpUIView();
        entry();
        addDividerInRecyclerView();
        initializeRecyclerView(FirebaseAuth.getInstance().getCurrentUser());
        LinearLayoutManager llm = new LinearLayoutManager(TripActivities.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        activities_recycler.setLayoutManager(llm);
        swipeToDelete();

    }

    private void setUpUIView() { // declaring elements
        addActivitiesButton = (FloatingActionButton) findViewById(R.id.addActivitiesButton);
        activities_recycler = (RecyclerView) findViewById(R.id.tripActivitiesRecycler);
    }

    private void entry() { // transiting to add trips
        addActivitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(TripActivities.this, TripActivitiesEntry.class);
//                String path = getIntent().getStringExtra("path");
                a.putExtra("pathToTripDoc", pathToTripDoc);
                startActivity(a);
            }
        });
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) { // ensure user is logged in if not direct to login page
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(TripActivities.this, LoginPage.class));
            return;
        }
    }

    @Override
    public void onStart() { // assign authStateListener
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    public void onStop() { // remove authStateListener
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        if (addActivitiesAdapter != null) {
            addActivitiesAdapter.stopListening();
        }
    }

    private void initializeRecyclerView(FirebaseUser user) {
//        String path1 = getIntent().getStringExtra("path");
//        System.out.println("HELLO");
//        System.out.println(path1);
//        dr = db.document(path1);
        Query query = FirebaseFirestore.getInstance().collection("Trips").document(dr.getId()).collection("Activities")
                .whereEqualTo("userID", user.getUid())
                .orderBy("activityDate", Query.Direction.ASCENDING)
                .orderBy("activityTime", Query.Direction.ASCENDING)
                .orderBy("activityName", Query.Direction.ASCENDING)
                .orderBy("activityPlace", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<TripActivitiesDetails> options =
                new FirestoreRecyclerOptions.Builder<TripActivitiesDetails>()
                        .setQuery(query, TripActivitiesDetails.class)
                        .build();
        addActivitiesAdapter = new TripActivitiesRecyclerAdapter(options, this);
        activities_recycler.setAdapter(addActivitiesAdapter);
        addActivitiesAdapter.startListening();
    }

    private void addDividerInRecyclerView() { // margin between each each item
        DividerItemDecoration divider = new DividerItemDecoration(TripActivities.this,
                DividerItemDecoration.VERTICAL);
        activities_recycler.addItemDecoration(divider);
    }

    private void swipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                    case ItemTouchHelper.RIGHT:
                        Toast.makeText(TripActivities.this, "Activity Deleted", Toast.LENGTH_SHORT).show();
                        addActivitiesAdapter.deleteItem(viewHolder.getAdapterPosition());
                        break;
                }

            }
        }).attachToRecyclerView(activities_recycler);
    }

    @Override
    public void handleEditTripActivities(DocumentSnapshot snapshot, View v) {
        TripActivitiesDetails tripActivitiesDetails = snapshot.toObject(TripActivitiesDetails.class);
        String activityName = tripActivitiesDetails.getActivityName().toString();
        String activityPlace = tripActivitiesDetails.getActivityPlace().toString();
        String activityDate = tripActivitiesDetails.getActivityDate().toString();
        String activityTime = tripActivitiesDetails.getActivityTime().toString();
        String activityDescription = tripActivitiesDetails.getActivityDescription().toString();
        String pathToActivityDoc = snapshot.getReference().getPath();
        Intent intent = new Intent(TripActivities.this, TripActivitiesEdit.class);
        intent.putExtra("activityName", activityName);
        intent.putExtra("activityPlace", activityPlace);
        intent.putExtra("activityDate", activityDate);
        intent.putExtra("activityTime", activityTime);
        intent.putExtra("activityDescription", activityDescription);
        intent.putExtra("pathToActivityDoc", pathToActivityDoc);
        intent.putExtra("pathToTripDoc", pathToTripDoc);
        v.getContext().startActivity(intent);
    }

//    @Override
//    public void handleActivities(DocumentSnapshot snapshot, View v) {
//        Intent intent = new Intent(getActivity(), TripActivities.class);
//        v.getContext().startActivity(intent);
//    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TripActivities.this, MainActivity.class);
        intent.putExtra("TripsPage", true);
        startActivity(intent);
    }

}

