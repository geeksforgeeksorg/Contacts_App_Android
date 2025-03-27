package org.geeksforgeeks.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * Activity to display contact details and provide call and messaging functionalities.
 */
public class ContactDetailActivity extends AppCompatActivity {

    // Variables to store contact details
    private String contactName;
    private String contactNumber;

    // UI elements
    private TextView contactTV, nameTV;
    private ImageView contactIV, callIV, messageIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        // Retrieve contact details from intent extras
        contactName = getIntent().getStringExtra("name");
        contactNumber = getIntent().getStringExtra("contact");

        // Initialize UI elements
        nameTV = findViewById(R.id.name);
        contactTV = findViewById(R.id.number);
        contactIV = findViewById(R.id.profileImage);
        callIV = findViewById(R.id.callButton);
        messageIV = findViewById(R.id.messageButton);

        // Set contact details in UI
        nameTV.setText(contactName);
        contactTV.setText(contactNumber);

        // Handle call button click
        callIV.setOnClickListener(v -> makeCall(contactNumber));

        // Handle message button click
        messageIV.setOnClickListener(v -> sendMessage(contactNumber));
    }

    /**
     * Opens the default messaging app with the contact number pre-filled.
     *
     * @param contactNumber The phone number to send a message to.
     */
    private void sendMessage(String contactNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + contactNumber));
        intent.putExtra("sms_body", "Enter your message");
        startActivity(intent);
    }

    /**
     * Initiates a phone call to the given contact number.
     * Checks for CALL_PHONE permission before making the call.
     *
     * @param contactNumber The phone number to call.
     */
    private void makeCall(String contactNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + contactNumber));

        // Check if CALL_PHONE permission is granted before making the call
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }
}