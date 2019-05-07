package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class WriterProfileActivity extends AppCompatActivity {

    protected ScriptTankApplication myApp;
    private static User m_User, profiledUser;
    TextView mWriterName, mDetails, mBio;
    String mEmail, mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_profile);
        myApp = (ScriptTankApplication)this.getApplicationContext();
        m_User = myApp.getM_User();
        myApp.setCurrActivity(this);

        profiledUser = ((m_User == myApp.getmCurrentUser())?m_User:myApp.getmCurrentUser());//when adding parcellable user id, itll go into null


        mWriterName = findViewById(R.id.authorTitle);
        mBio = findViewById(R.id.AuthorBio);
        mDetails = findViewById(R.id.authorDetails);


        GetUserInfo(profiledUser.key).addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
                                                          @Override
                                                          public void onComplete(@NonNull Task<HashMap<String, Object>> task) {
                                                              System.out.println(task.getResult());
                                                              UpdateProfileInfo(task.getResult());
                                                          }
                                                      }
        );

        final Button viewIdeasButton = findViewById(R.id.viewIdeas);
        viewIdeasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myApp.setmCurrentUser(profiledUser);
                Intent intent = new Intent(WriterProfileActivity.this,
                        ViewUploadsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void UpdateProfileInfo(HashMap<String, Object> results) {
        mWriterName.setText((String)results.get("name"));
        mEmail = (String)results.get("email");
        mPhoneNumber = (String)results.get("phoneNumber");
        mDetails.setText(mEmail + "\n" + mPhoneNumber);
    }

    private Task<HashMap<String, Object>> GetUserInfo(String key) {
        Map<String, Object> data = new HashMap<>();

        data.put("key", key);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("loadUserProfileByKey")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                });
    }
}
