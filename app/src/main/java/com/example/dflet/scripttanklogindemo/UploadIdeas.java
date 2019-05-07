package com.example.dflet.scripttanklogindemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UploadIdeas extends AppCompatActivity {

    protected ScriptTankApplication myApp;
    private static User m_User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_ideas);
        myApp = (ScriptTankApplication) this.getApplicationContext();
        super.onCreate(savedInstanceState);
        m_User = myApp.getM_User();
    }
}
