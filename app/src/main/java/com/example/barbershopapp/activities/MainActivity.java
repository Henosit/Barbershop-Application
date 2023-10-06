package com.example.barbershopapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.barbershopapp.R;
import com.example.barbershopapp.fragments.FragmentUserProfile;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the Toolbar by its ID
        toolbar = findViewById(R.id.toolbar);

        // Set the Toolbar as the support action bar
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        // Find the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        if (navHostFragment!=null) {
            // Find the FragmentUserProfile within the NavHostFragment
            try {
                FragmentUserProfile fragmentUserProfile = (FragmentUserProfile) navHostFragment.getChildFragmentManager().getFragments().get(0);
                if (fragmentUserProfile != null) {
                    MenuItem refreshMenuItem = menu.findItem(R.id.menu_refresh);
                    refreshMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // Refresh
                            fragmentUserProfile.refresh();
                            overridePendingTransition(0,0);
                            return true;
                        }
                    });

                MenuItem updateProfileMenuItem = menu.findItem(R.id.menu_update_profile);
                updateProfileMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Update Profile
                        Intent intent = new Intent (MainActivity.this, UpdateProfileActivity.class);
                        startActivity(intent);
                        return true;
                    }
                });

                MenuItem updateEmailMenuItem = menu.findItem(R.id.menu_update_email);
                updateEmailMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Update Email
                        Intent intent = new Intent (MainActivity.this, UpdateEmailActivity.class);
                        startActivity(intent);
                        return true;
                    }
                });

                    MenuItem settingsMenuItem = menu.findItem(R.id.menu_settings);
                    settingsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // Settings
                            Toast.makeText(MainActivity.this,"Settings",Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });

//                MenuItem changePasswordMenuItem = menu.findItem(R.id.menu_change_password);
//                changePasswordMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        // Change Password
//                        Intent intent = new Intent (MainActivity.this, ChangePasswordActivity.class);
//                        startActivity(intent);
//                        return true;
//                    }
//                });
//
//                MenuItem deleteProfileMenuItem = menu.findItem(R.id.menu_delete_profile);
//                deleteProfileMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        // Delete Profile
//                        Intent intent = new Intent (MainActivity.this, DeleteProfileActivity.class);
//                        startActivity(intent);
//                        return true;
//                    }
//                });

                    MenuItem logoutMenuItem = menu.findItem(R.id.menu_logout);
                    logoutMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // Sign Out
                            fragmentUserProfile.signOut();
                            Toast.makeText(MainActivity.this,"Logged Out",Toast.LENGTH_LONG).show();
                            Navigation.findNavController(MainActivity.this,R.id.fragmentContainerView).navigate(R.id.action_fragmentUserProfile_to_fragmentInitial );
                            toolbar.setVisibility(View.GONE);
                            return true;
                        }
                    });

                    MenuItem bookingMenuItem = menu.findItem(R.id.menu_booking);
                bookingMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Delete Profile
                        Intent intent = new Intent (MainActivity.this, AppointmentActivity.class);
                        startActivity(intent);
                        return true;
                    }
                });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return true;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}