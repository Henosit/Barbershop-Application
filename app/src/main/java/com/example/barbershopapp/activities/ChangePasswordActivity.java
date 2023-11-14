package com.example.barbershopapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barbershopapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.view.Change;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private EditText editTextCurrentPwd, editTextNewPwd;
    private TextView textViewAuthenticated;
    private Button buttonReAuthenticate, buttonChangePwd;
    private ProgressBar progressBar;
    private String userCurrentPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextNewPwd = findViewById(R.id.editText_change_pwd_new);
        editTextCurrentPwd = findViewById(R.id.editText_change_pwd_current);
        textViewAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
        buttonReAuthenticate = findViewById(R.id.button_change_pwd_authenticate);
        buttonChangePwd = findViewById(R.id.button_change_pwd);
        progressBar = findViewById(R.id.progressBar);

        // Disable editText for New Password + make change password button unclickable till user is authenticated
        editTextNewPwd.setEnabled(false);
        buttonChangePwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")) {
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong! User's details aren't available", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            reAuthenticateUser();
        }
    }

    private void reAuthenticateUser() {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userCurrentPwd = editTextCurrentPwd.getText().toString();

                if (TextUtils.isEmpty(userCurrentPwd)) {
                    Toast.makeText(ChangePasswordActivity.this,"Password is needed!",Toast.LENGTH_SHORT).show();
                    editTextCurrentPwd.setError("Please enter your current password to authenticate");
                    editTextCurrentPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    // ReAuthenticate user now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),userCurrentPwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                editTextCurrentPwd.setEnabled(false);
                                editTextNewPwd.setEnabled(true);
                                buttonReAuthenticate.setEnabled(false);
                                buttonChangePwd.setEnabled(true);
                                textViewAuthenticated.setText("You are authenticated. You can change your password now!");
                                Toast.makeText(ChangePasswordActivity.this,"Password has been verified." + "Change password now",Toast.LENGTH_SHORT).show();

                                buttonChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this,R.color.dark_green));
                                buttonChangePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(ChangePasswordActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextNewPwd.getText().toString();
        if (TextUtils.isEmpty(userPwdNew)) {
            Toast.makeText(ChangePasswordActivity.this,"New Password is needed",Toast.LENGTH_SHORT).show();
            editTextNewPwd.requestFocus();
        } else if (userPwdNew.matches(userCurrentPwd)) {
            Toast.makeText(ChangePasswordActivity.this,"New Password cannot be same as old password",Toast.LENGTH_SHORT).show();
            editTextNewPwd.setError("Please enter a new password");
            editTextNewPwd.requestFocus();
        } else {
                progressBar.setVisibility(View.VISIBLE);
                firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this,"Password has been changed",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ChangePasswordActivity.this,MainActivity.class);
                            startActivity(intent);
                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                Toast.makeText(ChangePasswordActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
        }
    }
}