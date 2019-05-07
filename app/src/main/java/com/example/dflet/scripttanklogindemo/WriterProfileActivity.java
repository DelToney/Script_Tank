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
    private static User m_User;
    TextView mWriterName, mDetails, mBio;
    String mEmail, mPhoneNumber, profiledUserKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_profile);
        myApp = (ScriptTankApplication)this.getApplicationContext();
        Intent intent = getIntent();
        m_User = myApp.getM_User();
        myApp.setCurrActivity(this);

        profiledUserKey = ((intent.getStringExtra("UserID")==null)?m_User.key:intent.getStringExtra("UserID"));

                //((m_User == myApp.getmCurrentUser())?m_User:myApp.getmCurrentUser());


        mWriterName = findViewById(R.id.authorTitle);
        mBio = findViewById(R.id.AuthorBio);
        mDetails = findViewById(R.id.authorDetails);


        GetUserInfo(profiledUserKey).addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
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
                Intent intent = new Intent(WriterProfileActivity.this,
                        ViewUploadsActivity.class);
                intent.putExtra("UserID", profiledUserKey);
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
                .getHttpsCallable("getWriterProfileByKey")
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
