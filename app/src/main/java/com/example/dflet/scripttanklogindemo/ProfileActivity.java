package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText name, phone, email;
    private ImageView profilePicture;
    private Button requestButton;
    protected ScriptTankApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        final String profileID = (String)intent.getStringExtra("key");
        name = findViewById(R.id.nameEditTextProfile);
        phone = findViewById(R.id.phoneEditTextProfile);
        email = findViewById(R.id.emailEditTextProfile);
        profilePicture = findViewById(R.id.profilePicImageView);
        requestButton = findViewById(R.id.sendRequestProfile);
        grabUserProfile(profileID).addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, Object>> task) {
                if (task.isSuccessful()) {
                    HashMap<String, Object> result = task.getResult();
                    setProfileContent(result);
                }
            }
        });
        myApp = (ScriptTankApplication)this.getApplicationContext();
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestButton.getText().equals("Request Pending"))
                    return;

                String sender_name = myApp.getM_User().name;
                String body = sender_name + " has sent you an Editor request";
                sendEditorRequest(body, profileID).addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            System.out.println("The message was successful " + task.getResult());
                            updateRequestButtonText("Request Pending");
                            createRequestObject(myApp.getM_User().key, profileID).addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (task.isSuccessful()) {
                                        System.out.println("The Request object was made");
                                    }
                                }
                            });
                            Toast.makeText(ProfileActivity.this, "Request was successfully sent!", Toast.LENGTH_LONG).show();
                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }
                            System.err.println(e);
                        }
                    }
                });
            }
        });
    }

    private void setProfileContent(HashMap<String, Object> profileValues) {
        String res_name = (String)profileValues.get("name");
        String res_phone = (String)profileValues.get("phoneNumber");
        String res_email = (String)profileValues.get("email");
        name.setText(res_name);
        phone.setText(res_phone);
        email.setText(res_email);
        profilePicture.setImageResource(R.drawable.ic_person_black_192dp);
    }


    private Task<HashMap<String, Object>> grabUserProfile(String key) {

        Map<String, Object> data = new HashMap<>();
        data.put("push", true);
        data.put("key", key);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff.getHttpsCallable("loadUserProfileByKey")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult,HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                });
    }

    private Task<String> sendEditorRequest(String body, String key) {

        Map<String, Object> data = new HashMap<>();
        data.put("push", true);
        data.put("body", body);
        data.put("dest_key", key);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff.getHttpsCallable("sendEditorRequest")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult,String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = "";
                        return result;
                    }
                });
    }
    private Task<String> createRequestObject(String user_key, String receiver_key) {

        Map<String, Object> data = new HashMap<>();
        data.put("push", true);
        data.put("user_key", user_key);
        data.put("receiver_key", receiver_key);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff.getHttpsCallable("createRequest")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return "";

                    }
                });
    }

    private void updateRequestButtonText(String text) {
        requestButton.setText(text);
    }
}
