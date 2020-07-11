package com.example.lastminute.Diary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lastminute.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends FirestoreRecyclerAdapter<PhotoUploadDetails, ImageAdapter.ImageViewHolder> {

    public ImageAdapter(@NonNull FirestoreRecyclerOptions<PhotoUploadDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ImageViewHolder holder, int position, @NonNull PhotoUploadDetails model) {
        Picasso.get()
                .load(model.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.image_view_upload);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_view_upload;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            image_view_upload = itemView.findViewById(R.id.image_view_upload);
        }

    }

}
