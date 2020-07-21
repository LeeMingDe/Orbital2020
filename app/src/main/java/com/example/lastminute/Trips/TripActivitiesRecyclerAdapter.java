package com.example.lastminute.Trips;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lastminute.R;
import com.example.lastminute.Trips.TripActivitiesDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class TripActivitiesRecyclerAdapter extends FirestoreRecyclerAdapter<TripActivitiesDetails, TripActivitiesRecyclerAdapter.TripActivitiesViewHolder> {
    private TripActivitiesListener tripActivitiesListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TripActivitiesRecyclerAdapter(@NonNull FirestoreRecyclerOptions<TripActivitiesDetails> options
            , TripActivitiesListener tripActivitiesListener) {
        super(options);
        this.tripActivitiesListener = tripActivitiesListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull TripActivitiesViewHolder holder, int position,
                                    @NonNull TripActivitiesDetails model) {
        holder.activityName.setText(model.getActivityName());
        holder.activityPlace.setText(model.getActivityPlace());
        holder.activityDate.setText(model.getActivityDate());
        holder.activityTime.setText(model.getActivityTime());

    }

    @NonNull
    @Override
    public TripActivitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.activities_view, parent, false);
        return new TripActivitiesViewHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class TripActivitiesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private TextView activityName, activityPlace, activityDate, activityTime;
        private ImageButton moreButton;

        public TripActivitiesViewHolder(@NonNull View itemView) {
            super(itemView);
            setUpUIView(itemView);
            EditTripActivities(itemView);
            openPopup(itemView);
        }

        private void setUpUIView(View itemView) {
            activityName = (TextView) itemView.findViewById(R.id.activityName);
            activityPlace = (TextView) itemView.findViewById(R.id.activityPlace);
            activityDate = (TextView) itemView.findViewById(R.id.activityDate);
            activityTime = (TextView) itemView.findViewById(R.id.activityTime);
            moreButton = (ImageButton) itemView.findViewById(R.id.moreActivityButton);
        }

        private void EditTripActivities(View itemView) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    tripActivitiesListener.handleEditTripActivities(snapshot, v);
                    return true;
                }
            });
        }

        private void openPopup(View itemView) {
            moreButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            showPopup(v);
        }

        private void showPopup(View v) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), v);
            popupMenu.inflate(R.menu.activity_more_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.popup_activity_edit:
                    DocumentSnapshot snapshotEdit = getSnapshots().getSnapshot(getAdapterPosition());
                    tripActivitiesListener.handleEditTripActivities(snapshotEdit, itemView);
                    return true;
                case R.id.popup_activity_delete:
                    deleteItem(getAdapterPosition());
                    return true;
                default:
                    return false;
            }
        }
    }

    public interface TripActivitiesListener {
        void handleEditTripActivities(DocumentSnapshot snapshot, View v);
    }
}