package com.example.lastminute.Diary;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lastminute.Login.LoginPage;
import com.example.lastminute.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ShowImageUpload extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private String documentPath;
    private RecyclerAdapterBeforeUploading mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image_upload);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                initializeRecyclerView();
            }
        });
        thread.start();
    }

    private void initializeRecyclerView() {
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("FromView")) {
            documentPath = getIntent().getStringExtra("documentPath");
            final DocumentReference docRef = FirebaseFirestore.getInstance().document(documentPath);
            Query query = FirebaseFirestore.getInstance()
                    .collection(("Diary"))
                    .document(docRef.getId())
                    .collection("Upload");


            FirestoreRecyclerOptions<PhotoUploadDetails> options =
                    new FirestoreRecyclerOptions.Builder<PhotoUploadDetails>()
                            .setQuery(query, PhotoUploadDetails.class)
                            .build();
            imageAdapter = new ImageAdapter(options);
            recyclerView.setAdapter(imageAdapter);
            imageAdapter.startListening();
        } else if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("FromEntry")) {
            recyclerView.setHasFixedSize(true);
            ArrayList<String> lst = getIntent().getStringArrayListExtra("imageList");
            mAdapter = new RecyclerAdapterBeforeUploading(ShowImageUpload.this, lst);
            recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(ShowImageUpload.this, LoginPage.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

}
