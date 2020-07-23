package com.example.lastminute.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lastminute.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Security extends AppCompatActivity {
    private TextView changePassword;
    private Toolbar settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        changePassword = findViewById(R.id.changePassword);
        settingsToolbar = findViewById(R.id.settingsToolbar);
        customizeToolbar();
        onClickPassword();
    }

    private void customizeToolbar() {
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setTitle("Security");
        settingsToolbar.setTitleTextColor(getResources().getColor(R.color.diaryColor));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settingsToolbar.setTitleTextAppearance(this, R.style.roboto_medium);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void changePassword(String oldPassword, final String newPassword) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), oldPassword);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Security.this, "Password Changed"
                                                , Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Security.this, "Password Change failed"
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Security.this, "Wrong Password"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onClickPassword() {
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Security.this);
                LayoutInflater inflater = getLayoutInflater();
                final View alertDialogView = inflater.inflate(R.layout.change_password,null);
                builder.setView(alertDialogView)
                        .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText oldPassword = (EditText) alertDialogView.findViewById(R.id.oldPassword);
                                EditText newPassword = (EditText) alertDialogView.findViewById(R.id.newPassword);
                                changePassword(oldPassword.getText().toString(),
                                        newPassword.getText().toString());
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(Security.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
                layoutParams.weight = 10;
                btnPositive.setLayoutParams(layoutParams);
                btnNegative.setLayoutParams(layoutParams);
            }
        });
    }
}