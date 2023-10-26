package com.example.barbershopapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.barbershopapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;


public class UploadProfilePictureActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView imageViewPfpUpload;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_picture);
        getSupportActionBar().setTitle("Upload your Profile Picture");
        Button buttonUploadPictureChoose= findViewById(R.id.upload_pic_choose_button);
        Button buttonUploadPic=findViewById(R.id.upload_pic_button);
        imageViewPfpUpload=findViewById(R.id.imageView_profile_dp);
        authProfile=FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


    }
}