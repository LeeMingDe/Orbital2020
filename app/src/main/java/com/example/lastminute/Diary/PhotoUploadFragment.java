package com.example.lastminute.Diary;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lastminute.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PhotoUploadFragment extends BottomSheetDialogFragment {
    private TextView gallery, photo;
    private ImageView cameraPicture, galleryPicture;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_upload_photo, container, false);
        setUpUIView(v);
        uploadImageFromGallery();
        storageReference = FirebaseStorage.getInstance()
                .getReference(FirebaseAuth.getInstance().getUid());
        takeImageWithPhone();
        return v;
    }


    private void setUpUIView(View v) {
        gallery = (TextView) v.findViewById(R.id.gallery);
        photo = (TextView) v.findViewById(R.id.photo);
        cameraPicture = (ImageView) v.findViewById(R.id.cameraPicture);
        galleryPicture = (ImageView) v.findViewById(R.id.galleryPicture);
    }

    private void uploadImageFromGallery() {
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiaryEntry.class);
                intent.putExtra("SelectImage", true);
                startActivity(intent);;
            }
        });
        galleryPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiaryEntry.class);
                intent.putExtra("SelectImage", true);
                startActivity(intent);
            }
        });
    }

    private void takeImageWithPhone() {
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiaryEntry.class);
                intent.putExtra("takePhoto", true);
                startActivity(intent);
            }
        });
        cameraPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DiaryEntry.class);
                intent.putExtra("takePhoto", true);
                startActivity(intent);
            }
        });
    }


}
