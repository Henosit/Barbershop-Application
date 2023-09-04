package com.example.barbershopapp.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

public class ErrorHandler {

    public static void showError(FragmentActivity fragmentActivity, String toastText, EditText editText, String errorText) {
        if (toastText!=null) {
            Toast.makeText(fragmentActivity, toastText, Toast.LENGTH_SHORT).show();
        }
        if (editText!=null) {
            editText.setError(errorText);
            editText.requestFocus();
        }
    }

    public static void showAlertDialog(FragmentActivity fragmentActivity, String builderMessage) {

        // Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
        builder.setTitle("Email not verified");
        builder.setMessage(builderMessage);

        // Open Email Apps if user clicks/taps on Continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Opens the email app in a new window and not within our app
                fragmentActivity.startActivity(intent);
            }
        });

        // Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        // Show the AlertDialog
        alertDialog.show();
    }

}
