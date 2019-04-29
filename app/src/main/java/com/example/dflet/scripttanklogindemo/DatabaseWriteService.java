package com.example.dflet.scripttanklogindemo;

import android.app.Application;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;

//db write service. this operates in the background for any long writes
//currently no error handling and it will only write the created user profile to
//db.
public class DatabaseWriteService extends IntentService {

    protected ScriptTankApplication myApp;
    private static int lastInId;

    public DatabaseWriteService() {
        super("DatabaseWriteService");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = (ScriptTankApplication)this.getApplicationContext();
    }


        @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lastInId = startId;
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                Bundle extra = intent.getExtras();
                String process = extra.getString("PROCESS");
                switch (process) {
                    case "ACCOUNT_CREATION":


                        User newUser = myApp.getM_User();
                        FirebaseDatabase fb = FirebaseDatabase.getInstance("https://scripttankdemo.firebaseio.com/");
                        DatabaseReference myRef = fb.getReference("/Users/");
                        DatabaseReference pushRef = myRef.push(); //push new object to db
                        pushRef.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    System.out.println("The Write was processed");
                                } else {
                                    System.err.println(task.getException().getMessage());
                                }
                            }
                        });
                        newUser.setKey(pushRef.getKey());//grab unique identifier created from push operation
                        //this value in the future will be cached in a file on the device, along with the rest of the
                        //user object as well
                        writeUserToFile(newUser);
                        break;
                    case "ACCOUNT_LOGIN":
                        User loginUser = myApp.getM_User();
                        writeUserToFile(loginUser);
                        break;
                    case "TOKEN_UPDATE":
                        String token = extra.getString("token");


                        User user = myApp.getM_User();

                        FirebaseDatabase fb_token = FirebaseDatabase.getInstance("https://scripttankdemo.firebaseio.com/");
                        DatabaseReference myRef_token = fb_token.getReference("/Users/" + user.getKey() + "/");
                        myRef_token.child("token").setValue(token);
                        writeUserToFile(user);
                        break;

                    case "DOWNLOAD_FILE":
                        //TO-DO
                        break;
                    case "UPLOAD_FILE":
                        Uri userFileURI = (Uri)extra.get("user_uri");
                        uploadFile(grabFileTitle(userFileURI), userFileURI);
                        break;
                    default:
                        break;
                }
                stopSelf(lastInId);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private void writeUserToFile(User newUser) {
        try {
            String filename = getString(R.string.user_prof_file_name);
            FileOutputStream outputStream = openFileOutput(filename,
                    Context.MODE_PRIVATE);
            ObjectOutputStream objOut = new ObjectOutputStream(outputStream);
            objOut.writeObject(newUser);
            objOut.close();
            outputStream.close();
        } catch (FileNotFoundException FNF) {
            System.err.println(FNF.getMessage());
        } catch (IOException IO) {
            System.err.println(IO.getMessage());
        }
    }

    private String grabFileTitle(Uri userFileUri) {

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

    private void uploadFile(String name, Uri userFileUri) {
        try {

            User m_User = myApp.getM_User();
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
                    Toast.makeText(DatabaseWriteService.this,
                            "File Upload Successful", Toast.LENGTH_LONG).show();
                    System.out.println("File Uploaded!");
                }
            });
        } catch (Exception FNF) {
            System.err.println(FNF.getMessage());
        }
    }
}
