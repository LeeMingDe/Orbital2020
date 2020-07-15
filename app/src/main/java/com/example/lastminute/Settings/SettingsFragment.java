package com.example.lastminute.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lastminute.Login.LoginPage;
import com.example.lastminute.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private TextView accountName, accountEmail, general, notification, security, data, help, logOut;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        setUpUIView(v);
        setNameAndEmail();
        logOutFromAccount();
        toGeneralPage();
        toNotificationPage();
        toSecurityPage();
        toDataPage();
        toHelpPage();
        return v;
    }

    private void setUpUIView(View v) {
        accountName = (TextView) v.findViewById(R.id.accountName);
        accountEmail = (TextView) v.findViewById(R.id.accountEmail);
        general = (TextView) v.findViewById(R.id.general);
        notification = (TextView) v.findViewById(R.id.notification);
        security = (TextView) v.findViewById(R.id.security);
        data = (TextView) v.findViewById(R.id.data);
        help = (TextView) v.findViewById(R.id.help);
        logOut = (TextView) v.findViewById(R.id.logOut);
    }

    private void logOutFromAccount() {
        firebaseAuth = FirebaseAuth.getInstance();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), LoginPage.class));
            }
        });
    }

    private void setNameAndEmail() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                accountName.setText(snapshot.child("userName").getValue().toString());
                accountEmail.setText(snapshot.child("userEmail").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void toGeneralPage() {
        general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void toNotificationPage() {
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Notification.class);
                startActivity(intent);
            }
        });
    }

    private void toSecurityPage() {
        security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void toDataPage() {
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void toHelpPage() {
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}