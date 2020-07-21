package com.example.lastminute.Settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lastminute.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.HelpViewHolder> {
    private ArrayList<HelpDetails> list;
    private OnItemClickListener helpListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        helpListener = listener;
    }

    public HelpAdapter(ArrayList<HelpDetails> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public HelpAdapter.HelpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_help_view, parent, false);
        HelpViewHolder helpViewHolder = new HelpViewHolder(v, helpListener);
        return helpViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HelpAdapter.HelpViewHolder holder, int position) {
        HelpDetails helpDetails = list.get(position);
        holder.helpName.setText(helpDetails.getHelpName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class HelpViewHolder extends RecyclerView.ViewHolder {
        public TextView helpName;

        public HelpViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            setUpUIView();
            goToHelp(itemView, listener);

        }

        private void setUpUIView() {
            helpName = itemView.findViewById(R.id.helpName);
        }

        private void goToHelp(View itemView, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    listener.onItemClick(position);
                }
            });
        }
    }
}
