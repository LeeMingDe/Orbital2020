package com.example.lastminute.Trips;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;
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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class TripsEntryRecyclerAdapter extends FirestoreRecyclerAdapter<TripEntryDetails, TripsEntryRecyclerAdapter.TripEntryViewHolder> {
    private TripListener tripListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TripsEntryRecyclerAdapter(@NonNull FirestoreRecyclerOptions<TripEntryDetails> options
            , TripListener tripListener) {
        super(options);
        this.tripListener = tripListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull TripEntryViewHolder holder, int position,
                                    @NonNull TripEntryDetails model) {
        holder.tripName.setText(model.getTripName());
        holder.tripPlace.setText(model.getTripPlace());
        holder.startDate.setText(model.getStartDate());
        holder.endDate.setText(model.getEndDate());

    }

    @NonNull
    @Override
    public TripEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.trips_view, parent, false);
        return new TripEntryViewHolder(view);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class TripEntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private TextView tripName, tripPlace, startDate, endDate;
        private ImageButton moreButton;

        public TripEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            setUpUIView(itemView);
            EditTripEntry(itemView);
            goToActivities(itemView);
            openPopup(itemView);
        }

        private void setUpUIView(View itemView) {
            tripName = (TextView) itemView.findViewById(R.id.tripName);
            tripPlace = (TextView) itemView.findViewById(R.id.tripPlace);
            startDate = (TextView) itemView.findViewById(R.id.startDate);
            endDate = (TextView) itemView.findViewById(R.id.endDate);
            moreButton = (ImageButton) itemView.findViewById(R.id.moreTripButton);
        }

        private void EditTripEntry(View itemView) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    tripListener.handleEditTrip(snapshot, v);
                    return true;
                }
            });
        }

        private void goToActivities(View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                    tripListener.handleActivities(snapshot, v);
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
            popupMenu.inflate(R.menu.trips_more_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.popup_edit:
                    DocumentSnapshot snapshotEdit = getSnapshots().getSnapshot(getAdapterPosition());
                    tripListener.handleEditTrip(snapshotEdit, itemView);
                    return true;
                case R.id.popup_delete:
                    deleteItem(getAdapterPosition());
                    return true;
                case R.id.popup_share:
                    DocumentSnapshot snapshotShare = getSnapshots().getSnapshot(getAdapterPosition());
                    tripListener.handleShareTrip(snapshotShare, itemView);
                    return true;

                default:
                    return false;
            }
        }
    }

    public interface TripListener {
        void handleEditTrip(DocumentSnapshot snapshot, View v);
        void handleActivities(DocumentSnapshot snapshot, View v);
        void handleShareTrip(DocumentSnapshot snapshot, View v);
    }

}

