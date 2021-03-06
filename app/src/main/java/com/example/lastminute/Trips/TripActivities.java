package com.example.lastminute.Trips;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class TripActivities extends AppCompatActivity implements FirebaseAuth.AuthStateListener, TripActivitiesRecyclerAdapter.TripActivitiesListener {

    private  FloatingActionButton addActivitiesButton;
    private RecyclerView activities_recycler;
    TripActivitiesRecyclerAdapter addActivitiesAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference dr;
    String pathToTripDoc;
    private static final String TAG = "TripActivities: ";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpUIView();
        getPathToTrip();
        entry();
        addDividerInRecyclerView();
        initializeRecyclerView(FirebaseAuth.getInstance().getCurrentUser());
        LinearLayoutManager llm = new LinearLayoutManager(TripActivities.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        activities_recycler.setLayoutManager(llm);
        swipeToDelete();

    }

    private void setUpUIView() { // declaring elements
        setContentView(R.layout.activity_trip_activities);
        addActivitiesButton = (FloatingActionButton) findViewById(R.id.addActivitiesButton);
        activities_recycler = (RecyclerView) findViewById(R.id.tripActivitiesRecycler);
    }

    private void getPathToTrip() {
        pathToTripDoc = getIntent().getStringExtra("pathToTripDoc");
        dr = db.document(pathToTripDoc);
    }

    private void entry() { // transiting to add trips
        addActivitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(TripActivities.this, TripActivitiesEntry.class);
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
//        if (addActivitiesAdapter != null) {
//            addActivitiesAdapter.stopListening();
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeRecyclerView(FirebaseUser user) {
        final ArrayList<TripActivitiesDetails> activities = new ArrayList<>();
        Query query = FirebaseFirestore.getInstance().collection("Trips").document(dr.getId()).collection("Activities")
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Map<String, Object> activity = document.getData();
//                                String activityName = activity.get("activityName").toString();
//                                String activityPlace = activity.get("activityPlace").toString();
//                                String activityDate = activity.get("activityDate").toString();
//                                String activityTime = activity.get("activityTime").toString();
//                                String activityDescription = activity.get("activityDescription").toString();
//                                TripActivitiesDetails a = new TripActivitiesDetails(activityName, activityPlace, activityDate, activityTime, activityDescription, FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                activities.add(a);
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//        activities.sort(new Comparator<TripActivitiesDetails>() {
//            @Override
//            public int compare(TripActivitiesDetails o1, TripActivitiesDetails o2) {
//                String stringDate1 = o1.getActivityDate();
//                String stringDate2 = o2.getActivityDate();
//                SimpleDateFormat input = new SimpleDateFormat("EEEE, MMMM d, yyyy");
//                SimpleDateFormat output = new SimpleDateFormat("yyyyMMdd");
//                try {
//                    String newDate1 = output.format((input.parse(stringDate1)));
//                    String newDate2 = output.format((input.parse(stringDate2)));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                return -1;
//            }
//        });
                .whereEqualTo("userID", user.getUid());
//                .orderBy("activityDate", Query.Direction.ASCENDING)
//                .orderBy("activityTime", Query.Direction.ASCENDING)
//                .orderBy("activityName", Query.Direction.ASCENDING)
//                .orderBy("activityPlace", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<TripActivitiesDetails> options =
                new FirestoreRecyclerOptions.Builder<TripActivitiesDetails>()
                        .setQuery(query, TripActivitiesDetails.class)
                        .build();
        addActivitiesAdapter = new TripActivitiesRecyclerAdapter(options, this);
        activities_recycler.setAdapter(addActivitiesAdapter);
        addActivitiesAdapter.startListening();
//        testAdapter = new testAdapter(activities);
//        activities_recycler.setAdapter(testAdapter);
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

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TripActivities.this, MainActivity.class);
        intent.putExtra("TripsPage", true);
        startActivity(intent);
    }

}

