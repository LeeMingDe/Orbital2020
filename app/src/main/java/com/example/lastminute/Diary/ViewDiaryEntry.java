package com.example.lastminute.Diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lastminute.MainActivity;
import com.example.lastminute.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewDiaryEntry extends AppCompatActivity implements View.OnTouchListener {
    private Toolbar toolbar;
    private TextView view_diary_entry_date, view_diary_entry_content;
    private String title,text,documentPath;
    private Timestamp time;
    private ConstraintLayout constraintLayout;
    private GestureDetector detector;
    private ArrayList<String> imageUrlList;


    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_diary_entry);
        setUpUIView();
        customizeToolbar();
        viewEntry();
        viewPhotos();
        constraintLayout.setOnTouchListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.diary_view, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ViewDiaryEntry.this, MainActivity.class);
        intent.putExtra("DiaryPage", true);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                Intent intent = new Intent(ViewDiaryEntry.this, DiaryEntry.class);
                intent.putExtra("textDetails", text);
                intent.putExtra("titleDetails", title);
                intent.putExtra("dateCreated", time);
                intent.putExtra("documentPath", documentPath);
                startActivity(intent);
                return true;
            case R.id.delete:
                deleteEntry();
                return true;
            case android.R.id.home:
                Intent i = new Intent(ViewDiaryEntry.this, MainActivity.class);
                i.putExtra("DiaryPage", true);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteEntry() {
        final DocumentReference documentReference = FirebaseFirestore.getInstance().document(documentPath);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(ViewDiaryEntry.this, "Delete failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        getImageUrlList(documentReference);
    }

    private void getImageUrlList(final DocumentReference documentReference) {
        imageUrlList = new ArrayList<>();
        documentReference.collection("Upload")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<PhotoUploadDetails> lst = queryDocumentSnapshots.toObjects(PhotoUploadDetails.class);
                            for (int j = 0; j < lst.size(); j++) {
                                deleteDocumentsInUpload(documentReference);
                                imageUrlList.add(lst.get(j).getImageUrl());
                            }
                        }
                        UndoDeleteFromMain();
                    }
                });
    }

    private void deleteDocumentsInUpload(final DocumentReference documentReference) {
        FirebaseFirestore.getInstance().collection("Diary")
                .document(documentReference.getId())
                .collection("Upload")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                documentReference.collection("Upload")
                                        .document(documentSnapshot.getId()).delete();
                            }
                        }
                    }
                });
    }

    private void UndoDeleteFromMain() {
        Intent intent = new Intent(ViewDiaryEntry.this, MainActivity.class);
        intent.putExtra("Undo", true);
        intent.putExtra("textDetails", text);
        intent.putExtra("titleDetails", title);
        intent.putExtra("dateCreated", time);
        intent.putExtra("documentPath", documentPath);
        intent.putStringArrayListExtra("imageList",imageUrlList);
        startActivity(intent);
    }

    private void setUpUIView() {
        toolbar = findViewById(R.id.toolbar);
        view_diary_entry_date = findViewById(R.id.view_diary_entry_date);
        view_diary_entry_content = findViewById(R.id.view_diary_entry_content);
        constraintLayout = findViewById(R.id.ConstraintLayout);
    }

    private void customizeToolbar() {
        setSupportActionBar(toolbar);
    }

    private void viewEntry() {
        Intent i =  getIntent();
        title = i.getStringExtra("titleDetails");
        text = i.getStringExtra("textDetails");
        documentPath = i.getStringExtra("documentPath");
        time = i.getParcelableExtra("dateCreated");
        if (time != null) {
            String date = new SimpleDateFormat("MMMM dd",
                    Locale.getDefault()).format(time.toDate());
            view_diary_entry_date.setText(title);
            getSupportActionBar().setTitle(date);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextAppearance(this, R.style.montserrat_light);
            view_diary_entry_content.setText(text);
        }
    }

    private void viewPhotos() {
        detector = new GestureDetector(this, new onSwipeListener() {
            @Override
            public boolean onSwipe(Direction direction) {
                if (direction == Direction.left) {
                    Intent intent = new Intent(ViewDiaryEntry.this, ShowImageUpload.class);
                    intent.putExtra("documentPath", documentPath);
                    intent.putExtra("FromView", true);
                    startActivity(intent);
                }
                return super.onSwipe(direction);
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        detector.onTouchEvent(motionEvent);
        return true;
    }
}
