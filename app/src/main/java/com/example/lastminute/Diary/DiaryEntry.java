package com.example.lastminute.Diary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lastminute.MainActivity;
import com.example.lastminute.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryEntry extends AppCompatActivity implements View.OnTouchListener {
	private TextView dateOfEntry;
	private FloatingActionButton attachPhoto, location, doneButton, cancelButton;
	private EditText journalContent, journalTitle;
	private Timestamp time;
	private static List<Uri> imageNameList = new ArrayList<>();
	private SharedPreferences pref;
	private String currentPhotoPath;
	private GestureDetector detector;
	private ConstraintLayout constraintLayout;

	private StorageReference storageReference;
	private DocumentReference documentReference;

	private static final int PICK_IMAGE_REQUEST = 1;
	private static final int CAPTURE_IMAGE_REQUEST = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diary_entry);
		setUpUIView();
		constraintLayout.setOnTouchListener(this);
		journalContent.setOnTouchListener(this);
		journalTitle.setOnTouchListener(this);
		dateOfEntry.setOnTouchListener(this);
		viewPhotos();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				journalDate();
				cancelledEntry();
				doneEntry();
				editEntry();
				attachPhoto();
				selectPhoto();
				takePhoto();
			}
		});
		thread.start();
		storageReference = FirebaseStorage.getInstance()
				.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
		documentReference = FirebaseFirestore.getInstance()
				.collection("Diary").document();
		pref = getSharedPreferences("DiaryPref", MODE_PRIVATE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (pref.getString("content", "") != null
				&& pref.getString("title", "") != null
				&& getIntent().getExtras() != null
				&& (getIntent().getExtras().getBoolean("SelectImage")
				|| getIntent().getExtras().getBoolean("takePhoto"))) {
			journalContent.setText(pref.getString("content", ""));
			journalTitle.setText(pref.getString("title", ""));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("title", journalTitle.getText().toString());
		editor.putString("content", journalContent.getText().toString());
		editor.apply();
	}

	private void setUpUIView() {
		dateOfEntry = (TextView) findViewById(R.id.dateOfEntry);
		attachPhoto = (FloatingActionButton) findViewById(R.id.attachPhoto);
		location = (FloatingActionButton) findViewById(R.id.location);
		doneButton = (FloatingActionButton) findViewById(R.id.doneButton);
		cancelButton = (FloatingActionButton) findViewById(R.id.cancelButton);
		journalContent = (EditText) findViewById(R.id.journalContent);
		journalTitle = (EditText) findViewById(R.id.journalTitle);
		constraintLayout = findViewById(R.id.ConstraintLayout);
	}

	private void journalDate() {
		String date = new SimpleDateFormat("MMMM dd",
				Locale.getDefault()).format(new Date());
		dateOfEntry.setText(date);
	}

	private void journalDate(Timestamp timestamp) {
		String date = new SimpleDateFormat("MMMM dd",
				Locale.getDefault()).format(timestamp.toDate());
		dateOfEntry.setText(date);
	}

	private void cancelledEntry() {
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(DiaryEntry.this, MainActivity.class);
				i.putExtra("DiaryPage", true);
				startActivity(i);
			}
		});
	}

	private void doneEntry() {
		doneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addEntry(journalTitle.getText().toString(), journalContent.getText().toString());
				Intent i = new Intent(DiaryEntry.this, MainActivity.class);
				i.putExtra("DiaryPage", true);
				startActivity(i);
			}
		});
	}

	private void doneEntry(final DocumentReference docRef) {
		doneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addEntry(docRef, journalTitle.getText().toString(), journalContent.getText().toString());
				Intent i = new Intent(DiaryEntry.this, MainActivity.class);
				i.putExtra("DiaryPage", true);
				startActivity(i);
			}
		});
	}

	private void addEntry(String title, String text) {
		String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
		if (journalTitle.getText().toString().trim() == "") {
		    Toast.makeText(DiaryEntry.this, "Missing Title", Toast.LENGTH_SHORT).show();
        } else {
            DiaryEntryDetails entry = new DiaryEntryDetails(title, text, new Timestamp(new Date()), userId);
            documentReference.set(entry);
            uploadPhoto();
        }
	}

	private void addEntry(DocumentReference docRef, String title, String text) {
		String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
		DiaryEntryDetails entry = new DiaryEntryDetails(title, text, time, userId);
		docRef.set(entry);
	}

	private void editEntry() {
		Intent i =  getIntent();
		String title = i.getStringExtra("titleDetails");
		String text = i.getStringExtra("textDetails");
		String documentPath = i.getStringExtra("documentPath");
		time = i.getParcelableExtra("dateCreated");
		if (time != null && documentPath != null) {
			DocumentReference documentReference = FirebaseFirestore.getInstance().document(documentPath);
			journalDate(time);
			journalTitle.setText(title);
			journalContent.setText(text);
			doneEntry(documentReference);
		}
	}

	private void attachPhoto() {
		attachPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PhotoUploadFragment photoUploadFragment = new PhotoUploadFragment();
				photoUploadFragment.show(getSupportFragmentManager(), "photoUpload");
			}
		});
	}

    private void selectPhoto() {
		if (getIntent().getExtras()!= null && getIntent().getExtras().getBoolean("SelectImage")) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, PICK_IMAGE_REQUEST);
		}
	}

	private void takePhoto() {
		if (getIntent().getExtras()!= null && getIntent().getExtras().getBoolean("takePhoto")) {
			dispatchTakePictureIntent();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
			if (data != null && data.getClipData() != null) {
				int totalItems = data.getClipData().getItemCount();

				for (int i = 0; i < totalItems; i++) {
					Uri imageUri = data.getClipData().getItemAt(i).getUri();
					imageNameList.add(imageUri);

				}
			} else if (data != null && data.getData() != null) {
				Uri imageUri = data.getData();
				imageNameList.add(imageUri);
			}
		} else if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
			File f = new File(currentPhotoPath);
			imageNameList.add(Uri.fromFile(f));
		}
	}

	private String getFileExtension(Uri uri) {
		ContentResolver contentResolver = getContentResolver();
		MimeTypeMap mime = MimeTypeMap.getSingleton();
		String fileExtension = mime.getExtensionFromMimeType(contentResolver.getType(uri));
		if (fileExtension == null) {
			fileExtension = "jpg";
		}
		return fileExtension;
	}


    private void uploadPhoto() {
		if (!imageNameList.isEmpty()) {
			for (int k = 0; k < imageNameList.size(); k++) {
				final StorageReference fileRef = storageReference.child(System.currentTimeMillis()
						+ "." + getFileExtension(imageNameList.get(k)));
				UploadTask uploadTask = fileRef.putFile(imageNameList.get(k));
                final PhotoUploadDetails photoUploadDetails = new PhotoUploadDetails("");
                final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
					@Override
					public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
						if (!task.isSuccessful()) {
							throw task.getException();
						}
						return fileRef.getDownloadUrl();
					}
				}).addOnCompleteListener(new OnCompleteListener<Uri>() {
					@Override
					public void onComplete(@NonNull Task<Uri> task) {
						if (task.isSuccessful()) {
							Uri downloadUri = task.getResult();
							String downloadURL = downloadUri.toString();

							photoUploadDetails.setImageUrl(downloadURL);

							FirebaseFirestore.getInstance().collection("Diary")
									.document(documentReference.getId())
									.collection("Upload")
									.add(photoUploadDetails);
						} else {
							Toast.makeText(DiaryEntry.this, "Failed",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
			imageNameList.clear();
		}
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		currentPhotoPath = image.getAbsolutePath();
		return image;
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File

			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				Uri photoURI = FileProvider.getUriForFile(this,
						"com.example.android.fileprovider",
						photoFile);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
			}
		}
	}

	private void viewPhotos() {
		final ArrayList<String> lst = new ArrayList<>();
		detector = new GestureDetector(this, new onSwipeListener() {
			@Override
			public boolean onSwipe(Direction direction) {
				if (direction == Direction.left) {
					Intent intent = new Intent(DiaryEntry.this, ShowImageUpload.class);
					intent.putExtra("FromEntry", true);
					for (int k = 0; k < imageNameList.size(); k++) {
						lst.add(imageNameList.get(k).toString());
					}
					intent.putStringArrayListExtra("imageList",lst);
					startActivity(intent);
				}
				return super.onSwipe(direction);
			}
		});
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		detector.onTouchEvent(motionEvent);
		return false;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(DiaryEntry.this, MainActivity.class);
		intent.putExtra("DiaryPage", true);
		startActivity(intent);
	}
}
