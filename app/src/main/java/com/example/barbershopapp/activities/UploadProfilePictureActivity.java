package com.example.barbershopapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.barbershopapp.R;
import com.example.barbershopapp.fragments.FragmentUserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadProfilePictureActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView imageViewPfpUpload;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;

    // For image selection
    private ActivityResultLauncher<Intent> imageChooserLauncher;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_picture);

        Button buttonUploadPictureChoose = findViewById(R.id.upload_pic_choose_button);
        Button buttonUploadPic = findViewById(R.id.upload_pic_button);
        imageViewPfpUpload = findViewById(R.id.imageView_profile_dp);
        progressBar = findViewById(R.id.progressBar);

        Uri uri = null;
        try {
            authProfile = FirebaseAuth.getInstance();
            firebaseUser = authProfile.getCurrentUser();
            storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");
            uri = firebaseUser.getPhotoUrl();

            // Set User's current DP in ImageView (if uploaded already). We"ll use Picasso since ImageViewer SetImage doesn't work on Regular URIs.
            Picasso.get().load(uri).into(imageViewPfpUpload);
        } catch (Exception e) {
            // Handle any exceptions that may occur during Firebase initialization
            e.printStackTrace();
            Toast.makeText(UploadProfilePictureActivity.this, "Firebase initialization error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        // Set User's current DP in ImageView (if uploaded already). We"ll use Picasso since ImageViewer SetImage doesn't work on Regular URIs.
        Picasso.get().load(uri).into(imageViewPfpUpload);

        // Initialize the ActivityResultLauncher for image selection
        imageChooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            // Handle the selected image URI as needed
                            imageViewPfpUpload.setImageURI(selectedImageUri);
                            uriImage = selectedImageUri;  // Set the selected image URI to uriImage
                        }
                    } catch (Exception e) {
                        // Handle any exceptions that may occur during image selection
                        e.printStackTrace();
                        Toast.makeText(UploadProfilePictureActivity.this, "Error selecting image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


        buttonUploadPictureChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        buttonUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                try {
                    UploadPic();
                } catch (Exception e) {
                    Toast.makeText(UploadProfilePictureActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Use the ActivityResultLauncher to start the image selection activity
        imageChooserLauncher.launch(intent);
    }

    private void UploadPic() {
        if (uriImage != null) {
            // Save the image with uid of the currently logged user
            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid() + "."
                    + getFileExtension(uriImage));

            // Upload image to Storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = authProfile.getCurrentUser();

                            // Finally, set up the display image of the user after upload
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileChangeRequest);
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadProfilePictureActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UploadProfilePictureActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadProfilePictureActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(UploadProfilePictureActivity.this, "No File Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    // Obtain File Extension of the image
    private String getFileExtension(Uri uri) {
        // A content provider manages access to a central repository of the data.
        // When you want to access data in a content provider, you use the ContentResolver object
        // in your application's Context to communicate with the provider as a client.
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        // Return the extension
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
