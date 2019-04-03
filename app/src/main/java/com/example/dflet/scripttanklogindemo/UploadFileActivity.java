package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UploadFileActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 10;
    private String fileName = "";
    private Uri userFileUri = null;
    private EditText fileTitle;
    private static User m_User;
    protected ScriptTankApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);
        myApp = (ScriptTankApplication)this.getApplicationContext();
        m_User = myApp.getM_User();
        myApp.setCurrActivity(this);
        fileTitle = findViewById(R.id.fileNameEditText);
        final Button chooseFileButton = findViewById(R.id.chooseFileButton);
        final Button uploadFileButton = findViewById(R.id.uploadFileButton);
        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFileToUpload();
            }
        });
        uploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile(fileName);
            }
        });
    }

    private void chooseFileToUpload() {
        Intent openFileManager = new Intent(Intent.ACTION_GET_CONTENT);
        openFileManager.addCategory(Intent.CATEGORY_OPENABLE);
        openFileManager.setType("application/pdf");
        Intent intent = Intent.createChooser(openFileManager, "Choose a file");
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    private String grabFileTitle() {

        String name = "";
        Cursor cursor = getContentResolver()
                .query(userFileUri, null, null, null, null, null);

        try {

            if (cursor != null && cursor.moveToFirst()) {


                name = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());

        } finally {
            cursor.close();
        }
        return name;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        if (requestCode == MY_REQUEST_CODE) {
            userFileUri = data.getData();

            fileName = grabFileTitle();
            fileTitle.setText(fileName);
        }
    }

    private void uploadFile(String name) {
        try {

            if (userFileUri == null)
                return;

            FirebaseStorage fs = FirebaseStorage.getInstance("gs://scripttankdemo.appspot.com");
            String serverPath = "Files/" + m_User.key + "/" + name;
            StorageReference fRef = fs.getReference().child(serverPath);
            UploadTask ut = fRef.putFile(userFileUri);
            ut.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    System.err.println(exception.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(UploadFileActivity.this,
                            "File Upload Successful", Toast.LENGTH_LONG).show();
                    System.out.println("File Uploaded!");
                }
            });
        } catch (Exception FNF) {
            System.err.println(FNF.getMessage());
        }
    }
}
