package com.example.lastminute;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TripsFragment extends Fragment {

    FloatingActionButton goToAddTrips;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trips, container, false);

        goToAddTrips = v.findViewById(R.id.addTripsButton);

        goToAddTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTripsFragment addTrips = new AddTripsFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.mainLayout, addTrips);
                transaction.commit();
            }
        });
        return v;
    }
}