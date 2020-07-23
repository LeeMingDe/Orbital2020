package com.example.lastminute.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lastminute.Login.LoginPage;
import com.example.lastminute.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordRecovery extends AppCompatActivity {
    private EditText passwordEmail;
    private TextView backToLogin;
    private Button resetPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);
        setUpUIView();
        firebaseAuth = FirebaseAuth.getInstance();
        passwordReset();
        backToLogin();
        addTextWatcher();
    }

    /**
     * Retrieve all the widgets in the UI.
     */
    private void setUpUIView() {
        passwordEmail = (EditText) findViewById(R.id.passwordEmail);
        resetPassword = (Button) findViewById(R.id.resetPassword);
        backToLogin = (TextView) findViewById(R.id.backToLogin);
    }

    /**
     * Enables user to request for a password reset email when reset password is pressed.
     */
    private void passwordReset() {
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = passwordEmail.getText().toString().trim();

                if (userEmail.equals("")) {
                    Toast.makeText(PasswordRecovery.this, "Please enter your registered email ID",
                            Toast.LENGTH_SHORT).show();

                } else {
                    sendPasswordResetEmail(userEmail);
                }
            }
        });
    }

    /**
     * Send an email to the user for a password reset
     * @param userEmail Account email of user
     */
    private void sendPasswordResetEmail(String userEmail) {
        firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PasswordRecovery.this, "Password reset email sent"
                            , Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(PasswordRecovery.this, LoginPage.class));
                } else {
                    Toast.makeText(PasswordRecovery.this, "Error in sending password reset email"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void backToLogin() {
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addTextWatcher() {
        passwordEmail.addTextChangedListener(recoveryTextWatcher);
    }

    private TextWatcher recoveryTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String pe = passwordEmail.getText().toString().trim();

            resetPassword.setEnabled(!pe.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
