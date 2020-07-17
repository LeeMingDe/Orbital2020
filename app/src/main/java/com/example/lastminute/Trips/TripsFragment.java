package com.example.lastminute.Trips;

import android.app.usage.NetworkStats;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lastminute.Login.LoginPage;
import com.example.lastminute.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripsFragment extends Fragment implements FirebaseAuth.AuthStateListener, TripsEntryRecyclerAdapter.TripListener {

    private  FloatingActionButton addTripsButton;
    private RecyclerView trips_recycler;
    TripsEntryRecyclerAdapter addTripsAdapter;
    private static final String TAG = "TripsFragment";
    FirebaseUser u;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trips, container, false);
        setUpUIView(v);
        entry();
        addDividerInRecyclerView();
        initializeRecyclerView(FirebaseAuth.getInstance().getCurrentUser());
        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        trips_recycler.setLayoutManager(llm);
        recyclerViewActions();

        return v;
    }

    private void setUpUIView(View v) { // declaring elements
        addTripsButton = (FloatingActionButton) v.findViewById(R.id.addTripsButton);
        trips_recycler = (RecyclerView) v.findViewById(R.id.trips_recycler);
    }

    private void entry() { // transiting to add trips
        addTripsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TripsEntry.class));
            }
        });
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) { // ensure user is logged in if not direct to login page
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginPage.class));
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
//        if (addTripsAdapter != null) {
//            addTripsAdapter.stopListening();
//        }
    }

    private void initializeRecyclerView(FirebaseUser user) {
        u = user;
        Query query = FirebaseFirestore.getInstance()
                .collection("Trips")
                .whereEqualTo("userID", user.getUid());
        FirestoreRecyclerOptions<TripEntryDetails> options =
                new FirestoreRecyclerOptions.Builder<TripEntryDetails>()
                        .setQuery(query, TripEntryDetails.class)
                        .build();

        addTripsAdapter = new TripsEntryRecyclerAdapter(options, this);
        trips_recycler.setAdapter(addTripsAdapter);
        addTripsAdapter.startListening();
}

    private void addDividerInRecyclerView() { // margin between each each item
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        trips_recycler.addItemDecoration(divider);
    }

    private void recyclerViewActions() {
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
                        addTripsAdapter.deleteItem(viewHolder.getAdapterPosition());
                        Toast.makeText(getActivity(), "Trip Deleted", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }).attachToRecyclerView(trips_recycler);
    }

    @Override
    public void handleEditTrip(DocumentSnapshot snapshot, View v) {
            TripEntryDetails tripEntryDetails = snapshot.toObject(TripEntryDetails.class);
            String tripName = tripEntryDetails.getTripName().toString();
            String tripPlace = tripEntryDetails.getTripPlace().toString();
            String startDate = tripEntryDetails.getStartDate().toString();
            String endDate = tripEntryDetails.getEndDate().toString();
            String pathToTripDoc = snapshot.getReference().getPath();
            Intent intent = new Intent(getActivity(), TripsEdit.class);
            intent.putExtra("tripName", tripName);
            intent.putExtra("tripPlace", tripPlace);
            intent.putExtra("startDate", startDate);
            intent.putExtra("endDate", endDate);
            intent.putExtra("pathToTripDoc", pathToTripDoc);
            v.getContext().startActivity(intent);
    }

    @Override
    public void handleActivities(DocumentSnapshot snapshot, View v) {
        String pathToTripDoc = snapshot.getReference().getPath();
        Intent intent = new Intent(getActivity(), TripActivities.class);
        intent.putExtra("pathToTripDoc", pathToTripDoc);
        v.getContext().startActivity(intent);
    }

    @Override
    public void handleShareTrip(DocumentSnapshot snapshot, View v) {
        TripEntryDetails tripEntryDetails = snapshot.toObject(TripEntryDetails.class);
        final String tripName = tripEntryDetails.getTripName().toString();
        final String tripPlace = tripEntryDetails.getTripPlace().toString();
        final String startDate = tripEntryDetails.getStartDate().toString();
        final String endDate = tripEntryDetails.getEndDate().toString();
        FirebaseFirestore.getInstance()
                .document(snapshot.getReference().getPath()).collection("Activities")
                .whereEqualTo("userID", u.getUid())
                .orderBy("activityDate", Query.Direction.ASCENDING)
                .orderBy("activityTime", Query.Direction.ASCENDING)
                .orderBy("activityName", Query.Direction.ASCENDING)
                .orderBy("activityPlace", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder shareSubject = new StringBuilder();
                            StringBuilder shareBody = new StringBuilder();
                            shareSubject.append("Check out my itinerary to ").append(tripPlace).append("!");
                            shareBody.append("TRIP DETAILS\n")
                                    .append("Trip Name: " + tripName + "\n")
                                    .append("Destination: " + tripPlace + "\n")
                                    .append("Duration: " + startDate + " to " + endDate + "\n\n");
                            shareBody.append("ACTIVITIES\n");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> activity = document.getData();
                                String activityName = activity.get("activityName").toString();
                                String activityPlace = activity.get("activityPlace").toString();
                                String activityDate = activity.get("activityDate").toString();
                                String activityTime = activity.get("activityTime").toString();
                                shareBody.append(activityDate + " " + activityTime + " - " + activityName + " at " + activityPlace + "\n");
                            }
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, shareBody.toString());
                            intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject.toString());
                            startActivity(Intent.createChooser(intent, "Share via"));
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}