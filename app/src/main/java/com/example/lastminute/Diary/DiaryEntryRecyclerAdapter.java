package com.example.lastminute.Diary;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lastminute.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class DiaryEntryRecyclerAdapter extends FirestoreRecyclerAdapter<DiaryEntryDetails,
        DiaryEntryRecyclerAdapter.DiaryEntryViewHolder> {
    private DiaryListener diaryListener;


    DiaryEntryRecyclerAdapter(@NonNull FirestoreRecyclerOptions<DiaryEntryDetails> options
            , DiaryListener diaryListener) {
        super(options);
        this.diaryListener = diaryListener;
    }

    DiaryListener getDiaryListener() {
        return diaryListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull DiaryEntryViewHolder holder, int position,
                                        @NonNull DiaryEntryDetails model) {
        holder.titleTextView.setText(model.getTitle());
        holder.journalContent.setText(model.getText());
        CharSequence dateCharSeq = DateFormat.format("dd MMMM yyyy . E hh:mm:ss a zzz",
                model.getCreated().toDate());
        holder.dateTextView.setText(dateCharSeq);
    }

    @NonNull
    @Override
    public DiaryEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.diary_view, parent, false);
        return new DiaryEntryViewHolder(view);
    }

    class DiaryEntryViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView, dateTextView, journalContent;

        public DiaryEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            setUpUIView(itemView);
            ViewDiaryEntry(itemView);
        }

        private void setUpUIView(View itemView) {
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            journalContent = itemView.findViewById(R.id.journalContent);
        }

        private void ViewDiaryEntry(View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    diaryListener.handleViewDiary(snapshot, v);
                }
            });
        }
    }

    interface DiaryListener {
        void handleViewDiary(DocumentSnapshot snapshot, View v);
    }
}
