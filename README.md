# ContentProvider-Contact

# Ex.No:4 Create Your Own Content Providers to get Contacts details.

## AIM:

To create your own content providers to get contacts details using Android Studio.

## EQUIPMENTS REQUIRED:

Android Studio(Min. required Artic Fox)

## ALGORITHM:

Step 1: Open Android Stdio and then click on File -> New -> New project.

Step 2: Then type the Application name as “ex.no.3″ and click Next. 

Step 3: Then select the Minimum SDK as shown below and click Next.

Step 4: Then select the Empty Activity and click Next. Finally click Finish.

Step 5: Design layout in activity_main.xml.

Step 6: Get contacts details and Display details give in MainActivity file.

Step 7: Save and run the application.

## PROGRAM:
```
/*
Program to print the text create your own content providers to get contacts details.
Developed by: Aniruth S
Registeration Number : 212225040020
*/
```
**MainActivity.java**
```java
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
```
**Activitymain.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/textView4"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Contact Provider"
        android:textAlignment="center"
        android:textSize="34sp"
        android:textStyle="bold"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.048" />

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#E6ED9B"
        android:text="Contact Details"
        android:textColor="#ED1B0D"
        android:textSize="24sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.064"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.141" />

    <TextView
        android:id="@+id/textView2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#FAF3F3"
        android:text="Name"
        android:textSize="20sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.067"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.207" />

    <EditText
        android:id="@+id/editTextText"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:ems="10"
        android:hint="type the name"
        android:inputType="text"
        android:minHeight="48dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.119"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.265" />

    <TextView
        android:id="@+id/textView3"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#FBF6F6"
        android:text="Mobile Number"
        android:textSize="20sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.087"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.336" />

    <EditText
        android:id="@+id/editTextPhone"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:ems="10"
        android:hint="enter number"
        android:inputType="phone"
        android:minHeight="48dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.119"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.415" />

    <Button
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Add to contact"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.498"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.834" />

    <Button
        android:id="@+id/button2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Load contacts"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.503"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.939" />

    <ListView
        android:id="@+id/contactlist"
        android:layout_width="329dp"
        android:layout_height="165dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.508"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.623" />

</androidx.constraintlayout.widget.ConstraintLayout>
```
**AndroidManifest.xml**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.READ_CONTACTS"/>
    <uses-permission android:name="android.permission.WRITE_CONTACTS"/>

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Contact_content">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```
## OUTPUT

![alt text](<Screenshot 2026-11-04 142242.png>)

![alt text](<Screenshot 2026-11-04 142302.png>)

![alt text](<Screenshot 2026-11-04 142326.png>)

![alt text](<Screenshot 2026-11-04 142340.png>)

![alt text](<Screenshot 2026-11-04 142350.png>)

![alt text](<Screenshot 2026-11-04 142420.png>)

![alt text](<Screenshot 2026-11-04 142447.png>)

## RESULT
Thus a Simple Android Application create your own content providers to get contacts details using Android Studio is developed and executed successfully.
