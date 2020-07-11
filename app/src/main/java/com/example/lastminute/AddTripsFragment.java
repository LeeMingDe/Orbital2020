package com.example.lastminute;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 */

public class AddTripsFragment extends Fragment {

    FloatingActionButton backToTrips;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_trips, container, false);

        backToTrips = v.findViewById(R.id.backButton);

        backToTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripsFragment trips = new TripsFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.mainLayout, trips);
                transaction.commit();
            }
        });
        return v;
    }
}