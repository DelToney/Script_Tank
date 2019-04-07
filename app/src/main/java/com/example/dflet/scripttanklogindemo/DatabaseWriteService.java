package com.example.dflet.scripttanklogindemo;

import android.app.Application;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public DatabaseWriteService() {
        super("DatabaseWriteService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                myApp = (ScriptTankApplication)this.getApplicationContext();
                Bundle extra = intent.getExtras();

                User newUser = myApp.getM_User();

                if (extra.getString("PROCESS").equals("ACCOUNT_CREATION")) {
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

                }

                writeUserToFile(newUser);
                stopSelf();
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
            System.out.println("The Object  was succesfully written to a file");
        } catch (FileNotFoundException FNF) {
            System.err.println(FNF.getMessage());
        } catch (IOException IO) {
            System.err.println(IO.getMessage());
        }
    }
}
