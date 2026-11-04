package com.example.contact_content;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText editName, editPhone;
    Button btnAdd, btnLoad;
    ListView contactList;
    ArrayList<String> contacts;
    ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editName = findViewById(R.id.editTextText);
        editPhone = findViewById(R.id.editTextPhone);
        btnAdd = findViewById(R.id.button);
        btnLoad = findViewById(R.id.button2);
        contactList = findViewById(R.id.contactlist);
        contacts = new ArrayList<>();

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean read = result.get(
                            Manifest.permission.READ_CONTACTS);
                    Boolean write = result.get(
                            Manifest.permission.WRITE_CONTACTS);

                    if (Boolean.TRUE.equals(read)
                            && Boolean.TRUE.equals(write)) {
                        loadContacts();
                    } else {
                        Toast.makeText(this,
                                "Contacts permission denied",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        btnAdd.setOnClickListener(v -> {

            if (checkPermissions()) {
                addContact();
            }
        });

        btnLoad.setOnClickListener(v -> {

            if (checkPermissions()) {
                loadContacts();
            }
        });
    }
    private boolean checkPermissions() {
        boolean read = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;

        boolean write = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;

        if (!read || !write) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
            });
            return false;
        }
        return true;
    }
    private void addContact() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this,
                    "Please enter all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<ContentProviderOperation> operations =
                new ArrayList<>();
        operations.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        operations.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        name)
                .build());

        operations.add(ContentProviderOperation.newInsert(
                        ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        phone)
                .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            getContentResolver().applyBatch(
                    ContactsContract.AUTHORITY, operations);

            Toast.makeText(this,
                    "Contact Added Successfully",
                    Toast.LENGTH_SHORT).show();

            editName.setText("");
            editPhone.setText("");

            loadContacts();

        } catch (Exception e) {
            Toast.makeText(this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
    private void loadContacts() {
        contacts.clear();
        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        );
        if (cursor != null) {
            try {
                int nameIndex = cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                int numberIndex = cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER);

                while (cursor.moveToNext()) {

                    String name = cursor.getString(nameIndex);
                    String number = cursor.getString(numberIndex);

                    contacts.add("Name: " + name +
                            "\nNumber: " + number);
                }
            } finally {
                cursor.close();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                contacts
        );
        contactList.setAdapter(adapter);
        if (contacts.isEmpty()) {
            Toast.makeText(this,
                    "No Contacts Found",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
