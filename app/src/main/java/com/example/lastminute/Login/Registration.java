package com.example.lastminute.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lastminute.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {
	private EditText registerName, registerPassword, registerEmail;
	private Button registerButton;
	private TextView loginPage;
	private FirebaseAuth firebaseAuth;
	String name, password, email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		setUpUIView();
		firebaseAuth = FirebaseAuth.getInstance();
		registerPerson();
		goBackLogin();
	}

	/**
	 * Retrieve all the widgets in the UI.
	 */
	private void setUpUIView() {
		registerButton = (Button) findViewById(R.id.registerButton);
		registerName = (EditText) findViewById(R.id.registerName);
		registerPassword = (EditText) findViewById(R.id.registerPassword);
		registerEmail = (EditText) findViewById(R.id.registerEmail);
		loginPage = (TextView) findViewById(R.id.loginPage);
	}

	/**
	 * Bring users back to login page when login page is pressed
	 */
	private void goBackLogin() {
		loginPage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Registration.this, LoginPage.class));
			}
		});
	}

	/**
	 * Sets the user up for registration when register button is pressed
	 */
	private void registerPerson() {
		registerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validateInformation()) {
					String user_email = registerEmail.getText().toString().trim();
					String user_password = registerPassword.getText().toString().trim();
					if (user_password.length() < 6) {
						Toast.makeText(Registration.this, "Password must be 6 characters or more"
							, Toast.LENGTH_SHORT).show();
					} else {
						authentication(user_email, user_password);
					}

				}
			}
		});
	}

	/**
	 * Registers the user in the database
	 * @param user_email Email that user is signing up with
	 * @param user_password Password that user is signing up with
	 */
	private void authentication(String user_email, String user_password) {
		firebaseAuth.createUserWithEmailAndPassword(user_email, user_password)
				.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if(task.isSuccessful()) {
							sendEmailVerification();
						} else {
							Toast.makeText(Registration.this, "Registration Failed",
									Toast.LENGTH_SHORT).show();
						}

					}
				});
	}

	/**
	 * Validates the information that the user has entered
	 * @return True if user has not left any field for name, email and password blank
	 */
	private Boolean validateInformation() {
		Boolean result = false;
		name = registerName.getText().toString();
		password = registerPassword.getText().toString();
		email = registerEmail.getText().toString();

		if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
			Toast.makeText(this, "Please fill in all the required fields."
					, Toast.LENGTH_SHORT).show();
		}
		else {
			result = true;
		}
		return result;
	}

	/**
	 * Send a email to the user after a successful registration
	 */
	private void sendEmailVerification() {
		FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
		if (firebaseUser != null) {
			firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
				@Override
				public void onComplete(@NonNull Task<Void> task) {
					if (task.isSuccessful()) {
						sendUserData();
						Toast.makeText(Registration.this, "Registration Successful. Verification mail sent"
								, Toast.LENGTH_SHORT).show();
						firebaseAuth.signOut();
						finish();
						startActivity(new Intent(Registration.this, LoginPage.class));
					} else {
						Toast.makeText(Registration.this,"Verification mail not sent"
						, Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

	/**
	 * Sends the user data into the database
	 */
	private void sendUserData() {
		FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
		DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
		UserProfile userProfile = new UserProfile(email, name);
		myRef.setValue(userProfile);
	}

}
