package com.example.dflet.scripttanklogindemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class LaunchActivity extends AppCompatActivity {
    private static String filename;
    protected ScriptTankApplication myApp;
    private final static int MY_PERMISSIONS_REQUEST_READ_WRITE_EXTERNAL = 87;
    private String[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_WRITE_EXTERNAL);
        filename = getString(R.string.user_prof_file_name);

        File myFile = new File(getFilesDir(), filename);
        if (myFile.exists()) {
            User userProf = null;
            try {
                FileInputStream fi = new FileInputStream(myFile);
                ObjectInputStream ois = new ObjectInputStream(fi);
                userProf = (User) ois.readObject();
                ois.close();
                fi.close();
            }catch (IOException IO) {
                System.err.println(IO.getMessage());
                this.deleteFile(getString(R.string.user_prof_file_name));
                startWelcome();
                return;
            } catch (ClassNotFoundException CNF) {
                System.err.println(CNF.getMessage());
                this.deleteFile(getString(R.string.user_prof_file_name));
                startWelcome();
                return;
            }
            myApp = (ScriptTankApplication)this.getApplicationContext();
            myApp.setM_User(userProf);
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();

        } else {
            System.out.println("File not stored on device");
            startWelcome();
        }
    }

    private void startWelcome() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_WRITE_EXTERNAL) {
            Log.i("TAG", "MY_PERMISSIONS_REQUEST --> YES");
        }
    }
}
