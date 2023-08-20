package com.example.workingwithfirebase.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.workingwithfirebase.utils.ErrorHandler;
import com.example.workingwithfirebase.R;
import com.example.workingwithfirebase.utils.ReadWriteUserDetails;
import com.example.workingwithfirebase.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRegister#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRegister extends Fragment {

    private MainActivity mainActivity;
    private EditText editTextRegFullName, editTextRegEmail, editTextRegID, editTextRegBirthday, editTextRegMobile, editTextRegPwd, editTextRegConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegGender;
    private RadioButton radioButtonRegGenderSelected;
    private DatePickerDialog picker;
    private static final String TAG = "FragmentRegister";
    private FragmentActivity fragmentActivity;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentRegister() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRegister.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRegister newInstance(String param1, String param2) {
        FragmentRegister fragment = new FragmentRegister();
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
            if (fragmentActivity != null) {
                Toast.makeText(getActivity(), "You can register now", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reg, container, false);
        editTextRegFullName = v.findViewById(R.id.nameUserReg);
        editTextRegEmail = v.findViewById(R.id.emailUserReg);
        editTextRegID = v.findViewById(R.id.idUserReg);
        editTextRegBirthday = v.findViewById(R.id.birthdayUserReg);
        editTextRegMobile = v.findViewById(R.id.phoneUserReg);
        editTextRegPwd = v.findViewById(R.id.passUserReg);
        editTextRegConfirmPwd = v.findViewById(R.id.confirmPassUserReg);

        progressBar = v.findViewById(R.id.progressBar);

        radioGroupRegGender = v.findViewById(R.id.radioGroupRegGender);
        radioGroupRegGender.clearCheck();

        // Setting up Date Picker on EditText
        editTextRegBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // Data Picker Dialog
                picker = new DatePickerDialog(fragmentActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextRegBirthday.setText(dayOfMonth+"/"+(month+1)+"/"+year); // month+1 so it's from 1 to 12
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button buttonReg = v.findViewById(R.id.buttonReg2);
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId = radioGroupRegGender.getCheckedRadioButtonId();
                radioButtonRegGenderSelected = v.findViewById(selectedGenderId);

                // Obtain the entered data
                String textFullName = editTextRegFullName.getText().toString();
                String textEmail = editTextRegEmail.getText().toString();
                String textID = editTextRegID.getText().toString();
                String textBirthday = editTextRegBirthday.getText().toString();
                String textMobile = editTextRegMobile.getText().toString();
                String textPwd = editTextRegPwd.getText().toString();
                String textConfirmPwd = editTextRegConfirmPwd.getText().toString();
                String textGender = null; // Can't obtain the value before verifying if any button was selected or not

               if (ValidateRegAndHandleErrors(fragmentActivity, editTextRegFullName, editTextRegEmail,
                        editTextRegID, editTextRegBirthday, editTextRegMobile,
                        editTextRegPwd, editTextRegConfirmPwd, textFullName,
                        textEmail, textID, textBirthday, textMobile, textPwd, textConfirmPwd,
                       progressBar, textGender, radioButtonRegGenderSelected, radioGroupRegGender)==true) {
                   registerUser(textFullName, textEmail, textID, textBirthday, textGender, textMobile, textPwd);
               }
            }
        });

        return v;
    }

    // Register User using the credentials given
    public void registerUser(String textFullName, String textEmail, String textID, String textBirthday, String textGender, String textMobile, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Create User Profile
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    // Update the user's display name
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    // Enter User Data into the Firebase Realtime DB
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textID, textBirthday, textGender, textMobile);

                    // Extracting user reference from DB for "Registered Users"
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Send verification email
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(fragmentActivity,"User registered successfully. Please verify your email", Toast.LENGTH_SHORT).show();

                                // Navigate to the user profile
                                Navigation.findNavController(getView()).navigate(R.id.action_fragmentReg_to_fragmentLoginOK);
                            }
                            else {
                                Toast.makeText(fragmentActivity,"User registration failed. Please try again.",Toast.LENGTH_SHORT).show();
                            }
                            // Hide progressbar whether user creation is successful or not
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        ErrorHandler.showError(fragmentActivity,null, editTextRegPwd,"Your password is too weak. Kindly use a mix of alphabets, numbers and special characters.");
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        ErrorHandler.showError(fragmentActivity,null, editTextRegEmail,"Your email is invalid or already in use. Kindly re-enter.");
                    } catch (FirebaseAuthUserCollisionException e) {
                        ErrorHandler.showError(fragmentActivity,null,editTextRegEmail,"User is already registered with this email. Use another email.");
                    }
                    catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(fragmentActivity,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                    // Hide progressbar whether user creation is successful or not
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public static boolean ValidateRegAndHandleErrors(FragmentActivity fragmentActivity, EditText editTextRegFullName, EditText editTextRegEmail,
                                                     EditText editTextRegID, EditText editTextRegBirthday, EditText editTextRegMobile,
                                                     EditText editTextRegPwd, EditText editTextRegConfirmPwd, String textFullName,
                                                     String textEmail, String textID, String textBirthday, String textMobile,
                                                     String textPwd, String textConfirmPwd, ProgressBar progressBar,
                                                     String textGender, RadioButton radioButtonRegGenderSelected, RadioGroup radioGroupRegGender) {
        // Validate mobile number using Matcher and Pattern (Regular Expression)
        String mobileRegex = "05[0-9]{8}";
        Matcher mobileMatcher;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobile);

        // Validation logic
        // Full Name
        if (TextUtils.isEmpty(textFullName)) {
            ErrorHandler.showError(fragmentActivity,"Please enter your full name", editTextRegFullName, "Full Name is required");
            return false;
            // Email
        } else if (TextUtils.isEmpty(textEmail)) {
            ErrorHandler.showError(fragmentActivity,"Please enter your email", editTextRegEmail, "Email is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
            ErrorHandler.showError(fragmentActivity,"Please re-enter your email", editTextRegEmail, "Valid email is required");
            return false;
            // ID
        } else if (TextUtils.isEmpty(textID)) {
            ErrorHandler.showError(fragmentActivity,"Please enter your ID", editTextRegID, "ID is required");
            return false;
            // Birthday
        } else if (TextUtils.isEmpty(textBirthday)) {
            ErrorHandler.showError(fragmentActivity,"Please enter your date of birth", editTextRegBirthday, "Date of Birth is required");
            return false;
            // Gender
        } else if (radioGroupRegGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(fragmentActivity,"Please select your gender", Toast.LENGTH_SHORT).show();
            radioButtonRegGenderSelected.setError("Gender is required");
            radioButtonRegGenderSelected.requestFocus();
            return false;
            // Mobile Number
        } else if (TextUtils.isEmpty(textMobile)) {
            ErrorHandler.showError(fragmentActivity,"Please enter your mobile no.", editTextRegMobile, "Mobile No. is required");
            return false;
        } else if (textMobile.length() != 10) {
            ErrorHandler.showError(fragmentActivity,"Please re-enter your mobile no.", editTextRegMobile, "Mobile No. should be 10 digits");
            return false;
        }
        else if (!mobileMatcher.find()) {
            ErrorHandler.showError(fragmentActivity,"Please re-enter your mobile no.", editTextRegMobile, "Mobile No. is not valid");
            return false;
            // Password
        } else if (TextUtils.isEmpty(textPwd)) {
            ErrorHandler.showError(fragmentActivity,"Please enter your password", editTextRegPwd, "Password is required");
            return false;
        } else if (textPwd.length() < 6) {
            ErrorHandler.showError(fragmentActivity,"Password should be at least 6 digits", editTextRegPwd, "Password too weak");
            return false;
        } else if (TextUtils.isEmpty(textConfirmPwd)) {
            ErrorHandler.showError(fragmentActivity,"Please confirm your password", editTextRegConfirmPwd, "Password Confirmation is required");
            return false;
        } else if (!textPwd.equals(textConfirmPwd)) {
            ErrorHandler.showError(fragmentActivity,"Passwords should be identical", editTextRegConfirmPwd, "Password Confirmation is required");
            // Clear the entered password
            editTextRegPwd.getText().clear();
            editTextRegConfirmPwd.getText().clear();
            return false;
            // User entered everything correctly
        } else {
            textGender = radioButtonRegGenderSelected.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
            return true;
        }
    }
}