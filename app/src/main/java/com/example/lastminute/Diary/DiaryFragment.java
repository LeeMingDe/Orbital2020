package com.example.lastminute.Diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lastminute.Login.LoginPage;
import com.example.lastminute.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class DiaryFragment extends Fragment implements FirebaseAuth.AuthStateListener,
        DiaryEntryRecyclerAdapter.DiaryListener {
    private FloatingActionButton addEntry;
    private RecyclerView entryList;
    private DiaryEntryRecyclerAdapter diaryEntryRecyclerAdapter;
    private Toolbar toolbar;
    private ArrayList<DiaryEntryDetails> diaryList = new ArrayList<>();
    private DiaryEntryRecyclerAdapter.DiaryListener diaryListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_diary, container, false);
        setUpUIView(view);
        customizeToolbar();
        initializeRecyclerView(FirebaseAuth.getInstance().getCurrentUser());
        setHasOptionsMenu(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                entry();
                addDividerInRecyclerView();
            }
        });
        thread.start();
        return view;
    }

    private void setUpUIView(View v) {
        addEntry = (FloatingActionButton) v.findViewById(R.id.addEntry);
        entryList = (RecyclerView) v.findViewById(R.id.entryList);
        toolbar = v.findViewById(R.id.toolbar);
    }

    private void entry() {
        addEntry.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getActivity(), DiaryEntry.class));
        }
    });
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginPage.class));
            return;
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

    private void initializeRecyclerView(FirebaseUser user) {
        Query query = FirebaseFirestore.getInstance()
                .collection(("Diary"))
                .whereEqualTo("userID", user.getUid())
                .orderBy("created", Query.Direction.DESCENDING)
                .orderBy("title", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<DiaryEntryDetails> options =
                    new FirestoreRecyclerOptions.Builder<DiaryEntryDetails>()
                        .setQuery(query, DiaryEntryDetails.class)
                        .build();
        diaryEntryRecyclerAdapter = new DiaryEntryRecyclerAdapter(options, this);
        entryList.setAdapter(diaryEntryRecyclerAdapter);
        diaryEntryRecyclerAdapter.startListening();
    }

    private void addDividerInRecyclerView() {
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        entryList.addItemDecoration(divider);
    }

    @Override
    public void handleViewDiary(DocumentSnapshot snapshot, View v) {
        DiaryEntryDetails diaryEntryDetails = snapshot.toObject(DiaryEntryDetails.class);
        String textContent = diaryEntryDetails.getText().toString();
        String titleContent = diaryEntryDetails.getTitle().toString();
        Timestamp timeCreated = diaryEntryDetails.getCreated();
        String documentPath = snapshot.getReference().getPath();
        Intent intent = new Intent(getActivity(), ViewDiaryEntry.class);
        intent.putExtra("textDetails", textContent);
        intent.putExtra("titleDetails", titleContent);
        intent.putExtra("dateCreated", timeCreated);
        intent.putExtra("documentPath", documentPath);
        v.getContext().startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_diary, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setQueryHint("Search here");
        filterSearch(searchView);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void customizeToolbar() {
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(4);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void filterSearch(SearchView searchView) {
        diaryListener = diaryEntryRecyclerAdapter.getDiaryListener();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                Query query = FirebaseFirestore.getInstance().collection("Diary")
                        .whereEqualTo("userID", firebaseUser.getUid())
                        .orderBy("title")
                        .startAt(newText.toLowerCase());
                if (!newText.trim().isEmpty()) {
                    FirestoreRecyclerOptions<DiaryEntryDetails> options =
                            new FirestoreRecyclerOptions.Builder<DiaryEntryDetails>()
                                    .setQuery(query, DiaryEntryDetails.class)
                                    .build();
                    diaryEntryRecyclerAdapter = new DiaryEntryRecyclerAdapter(options, diaryListener);
                    entryList.setAdapter(diaryEntryRecyclerAdapter);
                    diaryEntryRecyclerAdapter.startListening();
                } else {
                    initializeRecyclerView(firebaseUser);
                }
                return false;
            }
        });
    }

}
