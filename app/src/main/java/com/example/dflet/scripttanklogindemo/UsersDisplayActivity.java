package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


//simple display list of user in db. check index.js for server side code
public class UsersDisplayActivity extends AppCompatActivity {


    private ArrayList<String> data;
    private ArrayAdapter<String> adapter;
    private ListView userList;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_display);
        tv = findViewById(R.id.user_data);
        Intent recvIntent = getIntent();
        if (recvIntent.getExtras() != null) {
            User userProf = (User) recvIntent.getSerializableExtra("USER_PROFILE");
            CharSequence seq = userProf.name + " " + userProf.email + " " + userProf.key + " " +
                    userProf.type;
            tv.setText(seq);


        }
        userList = findViewById(R.id.userList);

        data = new ArrayList<>();
        adapter = new ArrayAdapter<>(UsersDisplayActivity.this,
                R.layout.list_item, data);
        adapter.notifyDataSetChanged();
        userList.setAdapter(adapter);
        grabAllUsers().addOnCompleteListener(new OnCompleteListener<ArrayList>() {
            @Override
            public void onComplete(@NonNull Task<ArrayList> task) {
                if (task.isSuccessful()) {
                    updateUserAdapter(task.getResult());
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

        final Button logOutButton = findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
    }

    private void logOut() {
        this.deleteFile(getString(R.string.user_prof_file_name));
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }



    private void updateUserAdapter(ArrayList received) {
        for (Object datum : received) {
            System.out.println(datum);
            if (datum instanceof String) {
                data.add((String)datum);
            }
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    private Task<ArrayList> grabAllUsers() {
        Map<String, Object> data = new HashMap<>();
        data.put("push", true);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("grabAllUsers")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, ArrayList>() {
                    @Override
                    public ArrayList then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        ArrayList result = (ArrayList)task.getResult().getData();
                        return result;
                    }
                });
    }
}
