package com.example.barbershopapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.barbershopapp.R;
import com.example.barbershopapp.fragments.FragmentLogin;
import com.example.barbershopapp.utils.ErrorHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import kotlin.internal.UProgressionUtilKt;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button buttonPwdReset;
    private EditText editTextPwdResetEmail;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        editTextPwdResetEmail=findViewById(R.id.editText_password_reset_email);
        buttonPwdReset=findViewById(R.id.button_password_reset);
        progressBar=findViewById(R.id.progressBar);
        buttonPwdReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email=editTextPwdResetEmail.getText().toString();
                if(TextUtils.isEmpty(email)){
                    ErrorHandler.showError(ForgotPasswordActivity.this,"Please enter your email that you have registered with",editTextPwdResetEmail,"Email Required!");
                    editTextPwdResetEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    ErrorHandler.showError(ForgotPasswordActivity.this,"Please enter your email in a correct form",editTextPwdResetEmail,"Email Required in correct form!");
                    editTextPwdResetEmail.requestFocus();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });
    }

    private void resetPassword(String email) {
        authProfile=FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "You may need to restart your application,Please check your email", Toast.LENGTH_SHORT).show();

                    getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//clear stack
                    startActivity(new Intent(ForgotPasswordActivity.this, FragmentLogin.class));
                    finish();
                }
                else{
                    Toast.makeText(ForgotPasswordActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}