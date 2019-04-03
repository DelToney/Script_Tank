package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


//simple welcome activity. probably will be dsicarded, but shows how to launch new activities with
//explicit intents
public class WelcomeActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        final Button signInButton = findViewById(R.id.signInButton);
        final Button createButton = findViewById(R.id.createAccountButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchLoginActivity();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCreateAcctActivity();
            }
        });
    }
    //launch an activity based on the button clicked
    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivityST.class);
        this.startActivity(intent);
    }

    private void launchCreateAcctActivity() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        this.startActivity(intent);
    }

}
