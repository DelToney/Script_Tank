package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.lang.reflect.Array;


//register and create account. similar to the login activity
public class CreateAccountActivity extends AppCompatActivity {


    private ArrayAdapter<CharSequence> spinnerAdapter;
    private Spinner spinner;
    private MyListener myListener;
    private EditText phoneNumberEditText, confirmPwdEditText, pwdOriginalEditText, emailEditText, nameEditText;
    private String phone, email, name, type;
    private FirebaseAuth fbAuth;
    protected ScriptTankApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        confirmPwdEditText = findViewById(R.id.confirmEditText);
        pwdOriginalEditText = findViewById(R.id.pwdEditTextCreate);
        emailEditText = findViewById(R.id.emailEditTextCreate);
        nameEditText = findViewById(R.id.nameEditText);
        spinner = findViewById(R.id.typeSpinner);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.account_types,
                android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        myListener = new MyListener();
        spinner.setOnItemSelectedListener(myListener);
        final Button createButton = findViewById(R.id.createAccountFromFormButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fbAuth = FirebaseAuth.getInstance();
    }

    private void handleResponse(String code) {
        //if the registration is successful, call the DatabaseWriteService to then upload
        //user object to users database
        if (code.equals("Success")) {
            FirebaseInstanceId fb_id = FirebaseInstanceId.getInstance();
            fb_id.getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (task.isSuccessful()) {
                        User userProfile = createProfile(email, phone, name, type, task.getResult().getId(), task.getResult().getToken());

                        userProfile.setToken(task.getResult().getToken());
                        myApp = (ScriptTankApplication)CreateAccountActivity.this.getApplicationContext();
                        myApp.setM_User(userProfile);
                        Intent intent = new Intent(CreateAccountActivity.this, DatabaseWriteService.class);
                       //intent.putExtra(getString(R.string.user_profile_intent), (Parcelable)userProfile);

                        intent.putExtra("PROCESS", "ACCOUNT_CREATION");
                        startService(intent);
                        intent = new Intent(CreateAccountActivity.this, HomeActivity.class);
                       // intent.putExtra(getString(R.string.user_profile_intent), (Parcelable)userProfile);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity(intent); //must put this in to kill activity off stack, so user cannot go back.
                    }
                }
            });





        } else {
            switch (code) {
                case "ERROR_INVALID_EMAIL":
                    emailEditText.setError(getString(R.string.error_invalid_email));
                    emailEditText.requestFocus();
                    return;
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    emailEditText.setError(getString(R.string.error_invalid_email));
                    emailEditText.requestFocus();
                    return;
                case "ERROR_WEAK_PASSWORD":
                    pwdOriginalEditText.setError(getString(R.string.error_invalid_password));
                    pwdOriginalEditText.requestFocus();
                    return;
                default:
                    return;
            }
        }
    }

    private User createProfile(String email, String phone, String name, String type, String id, String token) {
        switch (type) {
            case "Editor":
                String [] data = {"NOT", "An", "Editor", "DUDE"};
                User newProfile = new Editor(email, phone, name, type, id, data);
                newProfile.setToken(token);
                return newProfile;
            default:
                return new Editor(email, phone, name, type, id, new String[4]);
        }
    }

    private void createAccount() {
        fbAuth.signOut();
        String pwd, confirmPwd;

        emailEditText.setError(null);
        pwdOriginalEditText.setError(null);
        confirmPwdEditText.setError(null);
        phoneNumberEditText.setError(null);
        nameEditText.setError(null);

        pwd = pwdOriginalEditText.getText().toString();
        confirmPwd = confirmPwdEditText.getText().toString();
        email = emailEditText.getText().toString();
        phone = phoneNumberEditText.getText().toString();
        name = nameEditText.getText().toString();
        type = myListener.getUserChoice();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_field_required));
            emailEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            pwdOriginalEditText.setError(getString(R.string.error_field_required));
            pwdOriginalEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPwd)) {
            confirmPwdEditText.setError(getString(R.string.error_field_required));
            confirmPwdEditText.requestFocus();
            return;
        }

        if (!(TextUtils.equals(confirmPwd, pwd))) {
            pwdOriginalEditText.setError(getString(R.string.error_pwd_not_equal));
            confirmPwdEditText.setError(getString(R.string.error_pwd_not_equal));
            confirmPwdEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError(getString(R.string.error_field_required));
            nameEditText.requestFocus();
            return;
        }

        fbCreateWithEmailAndPwd(email, pwd);
    }

    private void fbCreateWithEmailAndPwd(String email, String pwd) {
        fbAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    System.out.println("Success");
                    handleResponse("Success");

                } else {
                    FirebaseAuthException fbe = (FirebaseAuthException) task.getException();
                    handleResponse(fbe.getErrorCode());
                }
            }
        });
    }
    //listener class for spinner
    public class MyListener implements AdapterView.OnItemSelectedListener {

        private String userChoice = "";

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            this.userChoice = parent.getItemAtPosition(pos).toString();
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

        public String getUserChoice() {
            return this.userChoice;
        }

    }


}
