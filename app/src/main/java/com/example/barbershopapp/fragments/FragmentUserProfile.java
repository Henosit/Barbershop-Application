package com.example.barbershopapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barbershopapp.activities.AppointmentActivity;
import com.example.barbershopapp.activities.MainActivity;
import com.example.barbershopapp.utils.ErrorHandler;
import com.example.barbershopapp.R;
import com.example.barbershopapp.utils.ReadWriteAppointmentDetails;
import com.example.barbershopapp.utils.ReadWriteUserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentUserProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUserProfile extends Fragment {

    private FragmentActivity fragmentActivity;
    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewBirthday, textViewGender, textViewMobile, textViewShowAppointment;
    private ProgressBar progressBar;
    private String fullName, email, birthday, gender, mobile, appointment;
    private ImageView imageViewProfile;
    private FirebaseAuth authProfile;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentUserProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentUserProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentUserProfile newInstance(String param1, String param2) {
        FragmentUserProfile fragment = new FragmentUserProfile();
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
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);

        textViewWelcome = v.findViewById(R.id.textView_show_welcome);
        textViewFullName = v.findViewById(R.id.textView_show_full_name);
        textViewEmail = v.findViewById(R.id.textView_show_email);
        textViewBirthday = v.findViewById(R.id.textView_show_birthday);
        textViewGender = v.findViewById(R.id.textView_show_gender);
        textViewMobile = v.findViewById(R.id.textView_show_mobile);
        textViewShowAppointment = v.findViewById(R.id.textView_show_appointment);
        progressBar = v.findViewById(R.id.progressBar);

        // Toolbar
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity!=null) {
            Toolbar toolbar = mainActivity.getToolbar();
            toolbar.setVisibility(View.VISIBLE);
        }

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(fragmentActivity,"Something went wrong! User's details are not available at the moment",Toast.LENGTH_LONG).show();
        } else {
            checkifEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
        //debug
        /*Intent intent= new Intent(mainActivity, AppointmentActivity.class);
        startActivity(intent);*/
        return v;
    }

    private void checkifEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            ErrorHandler.showAlertDialog(fragmentActivity,"Please verify your email now. You cannot login without email verification next time.");
        }
    }


    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        // Extracting User Reference from DB for "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        DatabaseReference referenceAppointment = FirebaseDatabase.getInstance().getReference("Appointments");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    birthday = readUserDetails.getBirthday();
                    gender = readUserDetails.getGender();
                    mobile = readUserDetails.getMobile();

                    textViewWelcome.setText("Welcome, " + fullName + "!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewBirthday.setText(birthday);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(fragmentActivity,"Something went wrong!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        referenceAppointment.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteAppointmentDetails readAppointmentDetails = snapshot.getValue(ReadWriteAppointmentDetails.class);
                if (readAppointmentDetails != null) {
                    appointment = readAppointmentDetails.getAppointmentDetails();
                    textViewShowAppointment.setText(appointment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(fragmentActivity,"Something went wrong!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void refresh() {
        showUserProfile(FirebaseAuth.getInstance().getCurrentUser());
    }

    public void signOut() {
        authProfile.signOut();
    }
}