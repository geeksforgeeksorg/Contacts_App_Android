package org.geeksforgeeks.demo;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // List to store contacts
    private final ArrayList<Contacts> contactsArrayList = new ArrayList<>();

    // RecyclerView for displaying contacts
    private RecyclerView contactRV;

    // Adapter for RecyclerView
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView
        contactRV = findViewById(R.id.rv);

        // Floating Action Button to add new contact
        FloatingActionButton addNewContactFAB = findViewById(R.id.addButton);

        // Setup RecyclerView and request necessary permissions
        prepareContactRV();
        requestPermissions();

        // Handle click event on FloatingActionButton to open CreateNewContactActivity
        addNewContactFAB.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CreateNewContactActivity.class))
        );
    }

    // Initialize RecyclerView with adapter and layout manager
    private void prepareContactRV() {
        adapter = new Adapter(this, contactsArrayList);
        contactRV.setLayoutManager(new LinearLayoutManager(this));
        contactRV.setAdapter(adapter);
    }

    // Request necessary permissions using Dexter
    private void requestPermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.WRITE_CONTACTS
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(MainActivity.this, "All permissions granted", Toast.LENGTH_SHORT).show();
                            getContacts();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(
                            List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(error ->
                        Toast.makeText(getApplicationContext(), "Error occurred!", Toast.LENGTH_SHORT).show()
                )
                .onSameThread()
                .check();
    }

    // Show a dialog directing the user to app settings if permissions are denied permanently
    private void showSettingsDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Need Permissions")
                .setMessage("This app needs permission to use this feature. You can grant them in app settings.")
                .setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
                    dialog.cancel();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 101);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    // Fetch contacts from the device's contacts list
    private void getContacts() {
        Cursor cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                // Check if the contact has a phone number
                int hasPhoneNumber = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null
                    );

                    if (phoneCursor != null) {
                        if (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contactsArrayList.add(new Contacts(displayName, phoneNumber));
                        }
                        phoneCursor.close();
                    }
                }
            }
            cursor.close();
        }

        // Notify the adapter about dataset changes
        adapter.notifyItemRangeChanged(0, contactsArrayList.size());
    }
}