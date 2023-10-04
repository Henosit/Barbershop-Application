package com.example.barbershopapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.barbershopapp.R;
import com.example.barbershopapp.fragments.FragmentLogin;
import com.example.barbershopapp.fragments.FragmentUserProfile;
import com.example.barbershopapp.utils.ErrorHandler;
import com.example.barbershopapp.utils.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {
    private EditText editTextUpdateName, editTextUpdateID, editTextUpdateDate, editTextUpdateMobile;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String textFullName, textID, textDate, textGender, textMobile;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        progressBar = findViewById(R.id.progressBar);
        editTextUpdateID = findViewById(R.id.editText_update_ID);
        editTextUpdateDate = findViewById(R.id.editText_update_Date);
        editTextUpdateMobile = findViewById(R.id.editText_update_Mobile);
        editTextUpdateName = findViewById(R.id.editText_update_Profile_Name);
        radioGroupUpdateGender = findViewById(R.id.radioGroup_Update_Gender);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        //Show profile Data
        showProfile(firebaseUser);
        Button buttonUpdateEmail=findViewById(R.id.buttonUpdateEmail);
        buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // Setting up Date Picker on EditText
        editTextUpdateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textSADoB[] = textDate.split("/");
                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1]) - 1;
                int year = Integer.parseInt(textSADoB[2]);
                DatePickerDialog picker;
                // Data Picker Dialog
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextUpdateDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year); // month+1 so it's from 1 to 12
                    }
                }, year, month, day);
                picker.show();
            }
        });
        Button buttonUpdateProfile=findViewById(R.id.buttonUpdateProfile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile(firebaseUser);
            }
        });
    }

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID=radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected=findViewById(selectedGenderID);
        // Validate mobile number using Matcher and Pattern (Regular Expression)
        String mobileRegex = "05[0-9]{8}";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);

        // Validation logic
        // Full Name
        if (TextUtils.isEmpty(textFullName)) {
            ErrorHandler.showError(UpdateProfileActivity.this, "Please enter your full name", editTextUpdateName, "Full Name is required");
            // Email
            // ID
        } else if (TextUtils.isEmpty(textID)) {
            ErrorHandler.showError(UpdateProfileActivity.this,"Please enter your ID", editTextUpdateDate, "ID is required");
            // Gender
        } else if (TextUtils.isEmpty(textDate)) {
            ErrorHandler.showError(UpdateProfileActivity.this,"Please enter your date of birth", editTextUpdateDate, "Date of Birth is required");
            // Gender
        } else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())) {
            Toast.makeText(UpdateProfileActivity.this,"Please select your gender", Toast.LENGTH_SHORT).show();
            radioButtonUpdateGenderSelected.setError("Gender is required");
            radioButtonUpdateGenderSelected.requestFocus();
            // Mobile Number
        }
        else if (TextUtils.isEmpty(textMobile)) {
            ErrorHandler.showError(UpdateProfileActivity.this,"Please enter your mobile no.", editTextUpdateMobile, "Mobile No. is required");
        } else if (textMobile.length() != 10) {
            ErrorHandler.showError(UpdateProfileActivity.this,"Please re-enter your mobile no.", editTextUpdateMobile, "Mobile No. should be 10 digits");
        }
        else if (!mobileMatcher.find()) {
            ErrorHandler.showError(UpdateProfileActivity.this,"Please re-enter your mobile no.", editTextUpdateMobile, "Mobile No. is not valid");
            // Password
        }
        else {
            textGender=radioButtonUpdateGenderSelected.getText().toString();
            textFullName=editTextUpdateName.getText().toString();
            textID = editTextUpdateID.getText().toString();
            textDate=editTextUpdateDate.getText().toString();
            textMobile=editTextUpdateMobile.getText().toString();
            //Entering user data to the Firebase
            ReadWriteUserDetails writeUserDetails=new ReadWriteUserDetails(textID,textDate,textGender,textMobile);
            //Extract Reference
            DatabaseReference referenceProfile=FirebaseDatabase.getInstance().getReference("Registered Users");
            String userID= firebaseUser.getUid();
            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //Update and set new display name
                        UserProfileChangeRequest profileUpdate=new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileUpdate);
                        Toast.makeText(UpdateProfileActivity.this,"Updated Successfully!",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(UpdateProfileActivity.this, FragmentUserProfile.class);
                        getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//clear stack
                        startActivity(new Intent(UpdateProfileActivity.this, FragmentUserProfile.class));
                        finish();
                    }else{
                        try{
                            throw task.getException();
                        }catch(Exception e){
                            Toast.makeText(UpdateProfileActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.VISIBLE);

                }
            });
        }
    }

    //Update Profile

    //fetch data
    private void showProfile(FirebaseUser firebaseUser) {
        String uidRegistered=firebaseUser.getUid();
        //Extract reference
        DatabaseReference referencePfp= FirebaseDatabase.getInstance().getReference("Registered Users");
        progressBar.setVisibility(View.VISIBLE);
        referencePfp.child(uidRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails= snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails!=null){
                    textFullName=firebaseUser.getDisplayName();
                    textDate=readUserDetails.getBirthday();
                    textID=readUserDetails.getID();
                    textGender=readUserDetails.getGender();
                    textMobile=readUserDetails.getMobile();
                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDate.setText(textDate);
                    editTextUpdateMobile.setText(textMobile);
                    if(textGender.equals("Male")){
                        radioButtonUpdateGenderSelected=findViewById(R.id.radio_updateMale);
                    }
                    else {
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_updateFemale);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                }
                else {
                    Toast.makeText(UpdateProfileActivity.this,"Something Went Wrong!",Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this,"Something Went Wrong!",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}