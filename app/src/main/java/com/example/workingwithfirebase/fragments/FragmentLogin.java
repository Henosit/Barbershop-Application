package com.example.workingwithfirebase.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.workingwithfirebase.utils.ErrorHandler;
import com.example.workingwithfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLogin extends Fragment {

    private FragmentActivity fragmentActivity;
    private EditText editTextLoginEmail, editTextLoginPwd;
    private TextView textViewRegister;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "FragmentLogin";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private FirebaseAuth mAuth;

    public FragmentLogin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentLogin.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentLogin newInstance(String param1, String param2) {
        FragmentLogin fragment = new FragmentLogin();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        try {
            fragmentActivity = getActivity();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        editTextLoginEmail = v.findViewById(R.id.emailUser);
        editTextLoginPwd = v.findViewById(R.id.passUser);
        textViewRegister = v.findViewById(R.id.textViewRegisterClick);
        progressBar = v.findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();

        // Reset Password
        Button buttonForgotPwd = v.findViewById(R.id.buttonForgotPassword);
        buttonForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(fragmentActivity, "You can reset your password now!", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(FragmentLogin.this,ForgotPasswordActivity.class));
            }
        });

        // Hide password icon
        ImageView imageViewShowHidePwd = v.findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    // If password is visible then hide it
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // Change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }
                else {
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        // Create a SpannableString with underlined blue text
        String textViewRegisterText = textViewRegister.getText().toString();
        SpannableString spannableString = new SpannableString(textViewRegisterText);
        spannableString.setSpan(new UnderlineSpan(), 0, textViewRegisterText.length(),0);
        textViewRegister.setText(spannableString);

        // Login user
        Button buttonLogin = v.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                // Validation logic
                // Email
                if (TextUtils.isEmpty(textEmail)) {
                    ErrorHandler.showError(fragmentActivity,"Plase enter your email",editTextLoginEmail,"Email is required");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    ErrorHandler.showError(fragmentActivity,"Please re-enter your email", editTextLoginEmail, "Valid email is required");

                    // Password
                } else if (TextUtils.isEmpty(textPwd)) {
                    ErrorHandler.showError(fragmentActivity,"Please enter your password",editTextLoginPwd,"Password is required");

                    // User entered everything correctly
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                }
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_fragmentInitial_to_fragmentReg);
            }
        });

        return v;
    }

    private void loginUser(String textEmail, String textPwd) {
        authProfile.signInWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // Get instance of the current User
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    // Check if email is verified before user can access their profile
                    if (firebaseUser.isEmailVerified()) {
                        Toast.makeText(fragmentActivity, "You are logged in now", Toast.LENGTH_SHORT).show();

                        // Navigate to the user profile
                        View view = getView();
                        if (view != null) {
                            try {
                                Navigation.findNavController(view).navigate(R.id.action_fragmentInitial_to_fragmentUserProfile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut(); // Sign out user
                        ErrorHandler.showAlertDialog(fragmentActivity,"Please verify your email now. You cannot login without email verification");
                    }

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        ErrorHandler.showError(fragmentActivity,null,editTextLoginEmail,"User doesn't exist or is no longer valid. Please register again.");
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        ErrorHandler.showError(fragmentActivity,null,editTextLoginEmail,"Invalid credentials. Kindly, check and re-enter.");
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(fragmentActivity, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                 }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

       // Check if user is already logged in. In that case, straightaway take the user to the user profile
        @Override
        public void onStart() {
            super.onStart();
            if (authProfile.getCurrentUser()!=null) {
                Toast.makeText(fragmentActivity,"Already logged in!", Toast.LENGTH_SHORT).show();

                // Start the UserProfileActivity
                View v = getView();
                try {
                    if (v!=null) {
                        Navigation.findNavController(v).navigate(R.id.action_fragmentInitial_to_fragmentUserProfile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(fragmentActivity,"You can login now.", Toast.LENGTH_SHORT).show();
            }
        }
}