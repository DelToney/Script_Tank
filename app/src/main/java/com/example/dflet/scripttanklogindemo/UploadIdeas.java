package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.Key;

public class UploadIdeas extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 10;
    private String fileName = "";
    private Uri userFileUri = null;
    private TextView fileTitle;
    protected ScriptTankApplication myApp;
    private EditText mIdeaNameEditText, mIdeaAbstractEditText, mIdeaDescriptionEditText, mIdeaGenreEditText, mPageLengthEditText, mIdeaWorthEditText, mAliasEditText;
    private static User m_User;
    private Idea myIdea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_ideas);
        myApp = (ScriptTankApplication) this.getApplicationContext();
        m_User = myApp.getM_User();
        myApp.setCurrActivity(this);


        //getting field data to construct idea object
        mIdeaNameEditText = findViewById(R.id.ideaNameEdit);
        mIdeaAbstractEditText = findViewById(R.id.ideaAbstractEdit);
        mIdeaDescriptionEditText = findViewById(R.id.IdeaDescriptionEdit);
        mIdeaGenreEditText = findViewById(R.id.ideaGenreEdit);
        mPageLengthEditText = findViewById(R.id.ideaPageLengthEdit);
        mIdeaWorthEditText = findViewById(R.id.ideaWorthEdit);
        mAliasEditText = findViewById(R.id.aliasEdit);
        fileTitle = findViewById(R.id.fileTitle);



        final Button uploadButton = findViewById(R.id.uploadIdeaButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {SetIdea(); UploadIdea();
            }
        });
        final Button chooseFileButton = findViewById(R.id.chooseFileButton2);
        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFileToUpload();
            }
        });
    }

    //set all the idea info and construct the object
    private void SetIdea() {
        String name = mIdeaNameEditText.getText().toString();
        String ideaAbstract = mIdeaAbstractEditText.getText().toString();
        String ideaDescription = mIdeaDescriptionEditText.getText().toString();
        String genre = mIdeaGenreEditText.getText().toString();
        String pageLength = mPageLengthEditText.getText().toString();
        String ideaWorth = mIdeaWorthEditText.getText().toString();
        String alias = mAliasEditText.getText().toString();
        myIdea = new Idea(name, ideaAbstract, ideaDescription, genre, pageLength, ((alias=="")?m_User.name:alias), m_User.key, ideaWorth, fileName);
    }

    private void UploadIdea() {
        try {

            System.out.println("Uploading idea...");

            //upload idea data
            FirebaseDatabase fb = FirebaseDatabase.getInstance("https://scripttankdemo.firebaseio.com/");
            DatabaseReference myref = fb.getReference("/Ideas/" + m_User.key);
            DatabaseReference pushRef = myref.push();
            pushRef.setValue(myIdea).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    System.err.println(exception.getMessage());
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    System.out.println("Idea Uploaded!");
                }
            });


            //if no file selected skip file uploaded. I have it this way so that i can upload ideas without killing data limits
            if (userFileUri != null) {
                //upload idea file
                FirebaseStorage fs = FirebaseStorage.getInstance("gs://scripttankdemo.appspot.com");
                String serverPath = "Files/" + m_User.key + "/" + myIdea.IdeaName;
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
                    }
                });
            }

            Intent intent = new Intent(UploadIdeas.this,
                    HomeActivity.class);
            startActivity(intent);


        } catch (Exception FNF) {
            System.err.println(FNF.getMessage());
        }
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
}
