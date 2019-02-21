package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*Login activity for any user that has already registered and created an account */
public class LoginActivityST extends AppCompatActivity {


    //input fields
    private EditText pwdEditText, emailEditText;
    //text field for error message (strictly for debugging)
    private TextView errorDisplay;
    //Firebase Authentication object so that we can interface w/ server.
    private FirebaseAuth mFBAuth;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_st);

        pwdEditText = findViewById(R.id.pwdEditText);
        emailEditText = findViewById(R.id.emailEditText);
        errorDisplay = findViewById(R.id.errorDisplay);

        final Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //grab instance of firebase authentication object. this will have contain info on
        //any current user attached to this device
        mFBAuth = FirebaseAuth.getInstance();
    }

    private void attemptLogin() {
        mFBAuth.signOut(); //sign out current user

        //reset error values on EditTexts
        errorDisplay.setVisibility(View.INVISIBLE);
        pwdEditText.setError(null);
        emailEditText.setError(null);

        //cache input values
        email = emailEditText.getText().toString();
        String pwd = pwdEditText.getText().toString();

        //check if either input value is empty and alert user that they are both required
        if (TextUtils.isEmpty(pwd)) {
            pwdEditText.requestFocus();
            if (TextUtils.isEmpty(email)) {
                emailEditText.setError(getString(R.string.error_field_required));
                emailEditText.requestFocus();
            }
            pwdEditText.setError(getString(R.string.error_field_required));
            return;
        }
        //if everything is all set, call login method
        loginBackground(email, pwd);
    }

    private void handleResponse(String code) {
        //checks which code was passed from the login task. codes are self-explanatory
        switch (code) {
            case "ERROR_USER_NOT_FOUND":
                emailEditText.setError(getString(R.string.user_not_found));
                emailEditText.requestFocus();
                return;
            case "ERROR_INVALID_EMAIL":
                emailEditText.setError(getString(R.string.error_invalid_email));
                emailEditText.requestFocus();
                return;
            case "ERROR_USER_DISABLED":
                emailEditText.setError(getString(R.string.error_account_disabled));
                emailEditText.requestFocus();
                return;
            case "ERROR_WRONG_PASSWORD":
                pwdEditText.setError(getString(R.string.error_incorrect_password));
                pwdEditText.requestFocus();
                return;
            case "SUCCESS":
                getUserProfile().addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
                    @Override
                    public void onComplete(@NonNull Task<HashMap<String, Object>> task) {
                        if (task.isSuccessful()) {
                            ArrayList<HashMap<String, Object>> res_set = (ArrayList<HashMap<String, Object>>)task.getResult().get("data");
                            HashMap<String, Object> result = (HashMap<String, Object>)res_set.get(0);
                            String key = (String)task.getResult().get("key");
                            User userProfile = new User(email, (String)result.get("phoneNumber"),
                                    (String)result.get("name"), (String)result.get("type"), key);
                            Intent intent = new Intent(LoginActivityST.this, DatabaseWriteService.class);
                            intent.putExtra(getString(R.string.user_profile_intent), (Parcelable)userProfile);
                            intent.putExtra("DO_NOT_WRITE_TO_DB", true);
                            startService(intent);
                            intent = new Intent(LoginActivityST.this, HomeActivity.class);
                            intent.putExtra(getString(R.string.user_profile_intent), (Parcelable)userProfile);
                            startActivity(intent);
                            finish();
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
                return;
            default:
                errorDisplay.setVisibility(View.VISIBLE);
                errorDisplay.setText(code);
        }
    }

    private void loginBackground(String email, String pwd) {
        if (mFBAuth.getCurrentUser() != null) { //check for current user
            return;
        }

        //sign in with firebase email and pwd. This method is async, meaning it will operate in the
        //background once it is called. the OnCompleteListener will then wait for the task to finish
        //and pass a return value to handleResponse, which will update UI based on server info.
        mFBAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    System.out.println("Success"); //the login was successful
                    handleResponse("SUCCESS");

                } else {
                    FirebaseAuthException fbe = (FirebaseAuthException) task.getException();
                    handleResponse(fbe.getErrorCode()); //login not successful, pass one of firebase's
                                                        //predefined error codes to handler
                }
            }
        });

    }

    private Task<HashMap<String, Object>> getUserProfile() {
        Map<String, Object> data = new HashMap<>();
        data.put("push", true);
        data.put("email", email);


        //loads user profile
        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("loadUserProfile")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        System.out.println(result);
                        return result;
                    }
                });
    }

}
