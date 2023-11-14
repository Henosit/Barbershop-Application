package com.example.barbershopapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barbershopapp.R;
import com.example.barbershopapp.fragments.FragmentUserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private EditText editTextCurrentPwd, editTextNewPwd;
    private TextView textViewAuthenticated;
    private Button buttonAuthenticate, buttonChangePwd;
    private ProgressBar progressBar;
    private String userCurrentPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextNewPwd = findViewById(R.id.editText_change_pwd_new);
        editTextCurrentPwd = findViewById(R.id.editText_change_pwd_current);
        textViewAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
        buttonAuthenticate = findViewById(R.id.button_change_pwd_authenticate);
        buttonChangePwd = findViewById(R.id.button_change_pwd);
        progressBar = findViewById(R.id.progressBar);

        // Disable editText for New Password + make change password button unclickable till user is authenticated
        editTextNewPwd.setEnabled(false);
        buttonChangePwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")) {
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong! User's details aren't available", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}