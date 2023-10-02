package com.example.barbershopapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.barbershopapp.R;
import com.example.barbershopapp.utils.ReadWriteUserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdateProfileActivity extends AppCompatActivity {
    private EditText editTextUpdateName,editTextUpdateDate,editTextUpdateMobile;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String textFullName,textDate,textGender,textMobile;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        progressBar=findViewById(R.id.progressBar);
        editTextUpdateDate=findViewById(R.id.editText_update_Date);
        editTextUpdateMobile=findViewById(R.id.editText_update_Mobile);
        editTextUpdateName=findViewById(R.id.editText_update_Profile_Name);
        radioGroupUpdateGender=findViewById(R.id.radioGroup_Update_Gender);
        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        //Show profile Data
        showProfile(firebaseUser);
//        Button buttonUpdateEmail=findViewById(R.id.buttonUpdateEmail);
//        buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent= new Intent(UpdateProfileActivity.this,UpdateEmailActivity.class);
//                StartActivity(intent);
//                finish();
//            }
//        });
        // Setting up Date Picker on EditText
        editTextUpdateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textSADoB[]=textDate.split("/");
                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1])-1;
                int year = Integer.parseInt(textSADoB[2]);
                DatePickerDialog picker;
                // Data Picker Dialog
                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextUpdateDate.setText(dayOfMonth+"/"+(month+1)+"/"+year); // month+1 so it's from 1 to 12
                    }
                }, year, month, day);
                picker.show();
            }
        });
    }
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