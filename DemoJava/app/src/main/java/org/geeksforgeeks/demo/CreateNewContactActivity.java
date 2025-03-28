package org.geeksforgeeks.demo;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity to create a new contact and save it to the device's contact list.
 */
public class CreateNewContactActivity extends AppCompatActivity {

    // UI elements
    private EditText nameEdt, phoneEdt, emailEdt;

    private static final int REQUEST_CODE_ADD_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_contact);

        // Initialize UI elements
        nameEdt = findViewById(R.id.enterName);
        phoneEdt = findViewById(R.id.enterNumber);
        emailEdt = findViewById(R.id.enterEmail);
        Button addContactBtn = findViewById(R.id.saveButton);

        // Set click listener on the "Add Contact" button
        addContactBtn.setOnClickListener(v -> {
            String name = nameEdt.getText().toString().trim();
            String phone = phoneEdt.getText().toString().trim();
            String email = emailEdt.getText().toString().trim();

            // Check if any field is empty before proceeding
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
                Toast.makeText(CreateNewContactActivity.this, "Please enter data in all fields.", Toast.LENGTH_SHORT).show();
            } else {
                addContact(name, phone, email);
            }
        });
    }

    /**
     * Opens the contacts app to add a new contact with the provided details.
     *
     * @param name  The contact's name.
     * @param phone The contact's phone number.
     * @param email The contact's email address.
     */
    private void addContact(String name, String phone, String email) {
        Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        contactIntent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        contactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        contactIntent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
        startActivityForResult(contactIntent, REQUEST_CODE_ADD_CONTACT);
    }

    /**
     * Handles the result after attempting to add a contact.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_CONTACT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Contact has been added.", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled adding contact.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Navigates back to the main activity after successfully adding a contact.
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}