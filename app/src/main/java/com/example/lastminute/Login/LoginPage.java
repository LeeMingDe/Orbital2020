package com.example.lastminute.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lastminute.MainActivity;
import com.example.lastminute.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {
    private Button login;
    private EditText email, password;
    private TextView userSignUp, forgotPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        setUpUIView();
        checkIfLogin();
        toRegistrationPage();
        toTripsFragmentPage();
        toPasswordRecoveryPage();

        Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {
            if (bundle.getString("some") != null) {
                Toast.makeText(getApplicationContext(), "User" + bundle.getString("some"), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Retrieve all the widgets in the UI.
     */
    private void setUpUIView() {
        login = (Button) findViewById(R.id.loginButton);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        userSignUp = (TextView) findViewById(R.id.signUp);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        progressDialog = new ProgressDialog(this);
    }

    /**
     * Bring users to registration page when register is pressed.
     */
    private void toRegistrationPage() {
        userSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, com.example.lastminute.Login.Registration.class));
            }
        });
    }

    /**
     * Login the user and bring the user to trip fragment page when login button is pressed
     */
    private void toTripsFragmentPage() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNameAndPassword(email.getText().toString(), password.getText().toString());
            }
        });
    }

    /**
     * Checks the user's information in the database before logging him in
     * @param email Account email inputted by user
     * @param password Account password inputted by user
     */
    private void checkNameAndPassword(String email, String password) {
        progressDialog.setMessage("Verifying");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                             checkEmailVerification();
                        } else {
                            Toast.makeText(LoginPage.this, "Login Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Checks if the user is already logged in. If logged in, first page will be trip fragment page.
     */
    private void checkIfLogin() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            finish();
            startActivity(new Intent(LoginPage.this, MainActivity.class));
        }
    }

    /**
     * Bring users to password recovery page when 'forgot password' is pressed
     */
    private void toPasswordRecoveryPage() {
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, com.example.lastminute.Login.PasswordRecovery.class));
            }
        });
    }

    /**
     * Checks if user has verified his email address.
     */
    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        Boolean isVerified = firebaseUser.isEmailVerified();

        if (isVerified) {
            Toast.makeText(LoginPage.this, "Login Successful",
                    Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(LoginPage.this, MainActivity.class));
        } else {
            Toast.makeText(LoginPage.this, "Please verify your email",
                    Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}
