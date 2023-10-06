package com.example.barbershopapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barbershopapp.R;
import com.example.barbershopapp.fragments.FragmentUserProfile;
import com.example.barbershopapp.utils.ErrorHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userOldEmail, userNewEmail, userPwd;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail, editTextPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        progressBar = findViewById(R.id.progressBar);
        textViewAuthenticated = findViewById(R.id.textView_update_email_authenticated);
        editTextNewEmail = findViewById(R.id.editText_update_email_new);
        editTextPwd = findViewById(R.id.editText_update_email_verify_password);
        buttonUpdateEmail = findViewById(R.id.button_update_email);

        // Disabled until user authentication
        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        // Set old email address on TextView
        userOldEmail = firebaseUser.getEmail();
        TextView textViewOldEmail = findViewById(R.id.textView_update_email_old);
        textViewOldEmail.setText(userOldEmail);

        if (firebaseUser.equals("")) {
            Toast.makeText(UpdateEmailActivity.this,"Something went wrong! User's details are not available.",Toast.LENGTH_LONG).show();
        } else {
            reAuthenticate(firebaseUser);
        }
    }

    // Verify user before updating email
    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser = findViewById(R.id.button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtain password for authentication
                userPwd = editTextPwd.getText().toString();
                if (TextUtils.isEmpty(userPwd)) {
                    ErrorHandler.showError(UpdateEmailActivity.this,"Password is needed to continue",editTextPwd,"Please enter your password for authentication");
                    editTextPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail,userPwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(UpdateEmailActivity.this,"Password has been verified."+"You can update your email now.",Toast.LENGTH_LONG).show();
                                // Disable and enable the corresponding buttons and EditTexts
                                textViewAuthenticated.setText("You are authenticated. You can update your email now.");
                                editTextPwd.setEnabled(false);
                                buttonVerifyUser.setEnabled(false);
                                buttonUpdateEmail.setEnabled(true);
                                editTextNewEmail.setEnabled(true);
                                // Change color of the UpdateEmailButton
                                buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,
                                        R.color.dark_green));
                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        userNewEmail = editTextNewEmail.getText().toString();
                                        if (TextUtils.isEmpty(userNewEmail)) {
                                            ErrorHandler.showError(UpdateEmailActivity.this,"New Email is required",editTextNewEmail,"Please enter new Email");
                                            editTextNewEmail.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            ErrorHandler.showError(UpdateEmailActivity.this,"Please enter valid Email",editTextNewEmail,"Please provide valid Email");
                                            editTextNewEmail.requestFocus();
                                        } else if (userOldEmail.matches(userNewEmail)) {
                                            ErrorHandler.showError(UpdateEmailActivity.this,"New Email cannot be same as old Email",editTextNewEmail,"Please enter new Email");
                                            editTextNewEmail.requestFocus();
                                        } else {
                                            progressBar.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(UpdateEmailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {
                    // Verify Email
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmailActivity.this,"Email has been updated. Please verify your new Email. You may need to restart your application.",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UpdateEmailActivity.this, FragmentUserProfile.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(UpdateEmailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}