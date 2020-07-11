package com.example.lastminute.Diary;

import android.content.Context;
import android.provider.ContactsContract;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lastminute.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapterBeforeUploading extends
        RecyclerView.Adapter<RecyclerAdapterBeforeUploading.PhotoViewHolder> {
    private Context myContext;
    private List<String> mUploads;

    public RecyclerAdapterBeforeUploading(Context myContext, List<String> mUploads) {
        this.myContext = myContext;
        this.mUploads = mUploads;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(myContext).inflate(R.layout.image_item, parent, false);
        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String current = mUploads.get(position);
        Picasso.get()
                .load(current)
                .fit()
                .centerCrop()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById((R.id.image_view_upload));
        }
    }
}
