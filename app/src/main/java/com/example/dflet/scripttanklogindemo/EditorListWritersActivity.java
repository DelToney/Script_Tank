package com.example.dflet.scripttanklogindemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditorListWritersActivity extends AppCompatActivity {

    private ListView writerList;
    private ArrayList<String> writerNames, keys;
    private WriterRequestListAdapter writerRequestListAdapter;
    private User m_User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_list_writers);
        m_User = (User)getIntent().getSerializableExtra(getString(R.string.user_profile_intent));
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()) {
                    m_User.token = task.getResult().getToken();
                }
            }
        });
        writerList = findViewById(R.id.writerList);
        writerNames = new ArrayList<>();
        keys = new ArrayList<>();
        writerRequestListAdapter = new WriterRequestListAdapter(this, writerNames);
        writerRequestListAdapter.notifyDataSetChanged();
        writerList.setAdapter(writerRequestListAdapter);
        grabAllWriters().addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, Object>> task) {
             if (task.isSuccessful()) {
                HashMap<String, Object> results = task.getResult();
                ArrayList<String> tempNames, tempKeys;
                tempKeys = (ArrayList<String>)results.get("db_ids");
                tempNames = (ArrayList<String>)results.get("names");
                for (String id : tempKeys) {
                    keys.add(id);
                }
                for (String name : tempNames) {
                    writerNames.add(name);
                }
                System.out.println(keys);
                System.out.println(writerNames);
                updateListAdapter();
            } else {
                Exception e = task.getException();
                if (e instanceof FirebaseFunctionsException) {
                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                    FirebaseFunctionsException.Code code = ffe.getCode();
                    Object details = ffe.getDetails();
                }
                System.err.println(e.getMessage());
            }

            }

        });
        writerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String dest_key = keys.get(i);
                String sender_name = m_User.name;
                String token = m_User.token;
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            m_User.token = task.getResult().getToken();
                        }
                    }
                });
                String body = sender_name + " has sent an Editor request";
                sendEditorRequest(body, dest_key, token).addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            System.out.println("The message was successful " + task.getResult());
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

    private void updateListAdapter() {
        writerRequestListAdapter.notifyDataSetChanged();
    }

    private Task<HashMap<String, Object>> grabAllWriters() {
        Map<String, Object> data = new HashMap<>();
        data.put("push", true);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("grabAllWriters")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                });
    }

    private Task<String> sendEditorRequest(String body, String key, String token) {

        Map<String, Object> data = new HashMap<>();
        data.put("push", true);
        data.put("body", body);
        data.put("dest_key", key);
        data.put("token", token);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff.getHttpsCallable("sendWriterRequest")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult,String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = "";//(String) task.getResult().getData();
                        return result;
                    }
                });
    }
}
