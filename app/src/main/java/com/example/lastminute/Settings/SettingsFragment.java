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

public class SettingsFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private TextView general, notification, security, data, help, logOut;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        setUpUIView(v);
        logOutFromAccount();
        return v;
    }

    private void setUpUIView(View v) {
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
}