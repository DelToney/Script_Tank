package com.example.dflet.scripttanklogindemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

    private RecyclerView writerList;
    private static ArrayList<String> writerNames, keys;
    private WriterRequestListAdapter writerRequestListAdapter;
    private static User m_User;
    protected ScriptTankApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_list_writers);
        myApp = (ScriptTankApplication)this.getApplicationContext(); //get application context
        m_User = myApp.getM_User(); //get user object for in activity use
        myApp.setCurrActivity(this); //set this as current activity at application level
        writerList = findViewById(R.id.writersList);
        writerList.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        writerList.addItemDecoration(itemDecoration);
        writerNames = new ArrayList<>();
        keys = new ArrayList<>();
        writerRequestListAdapter = new WriterRequestListAdapter(this, writerNames);
        writerRequestListAdapter.notifyDataSetChanged();
        writerList.setAdapter(writerRequestListAdapter);
        writerList.setLayoutManager(new LinearLayoutManager(this));


        //ATTN: FIREBASE CODE

        //for grabAllWriters, first make the function call, which is the 'grabAllWriters' below
        //next, add the callback 'OnCompleteListener'. This function is called once 'grabAllWriters'
        //has finished.
        //Inside the listener, if the request returns no errors, the results sent back from the
        //server are retrieved. YOU NEED TO KNOW WHAT IS BEING RETURNED BY THE SERVER AND THEN CAST
        //IT INTO APPROPRIATE DATA TYPE. I generally use hashmap, because it allows for key, value pairs
        //but those values can be arraylists, as seen below, so those need to be dealt with client side
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
        writerRequestListAdapter.setOnItemClickListener(new WriterRequestListAdapter.WriterClickListener() {
            @Override
            public void onItemClicked(int pos) {
                String dest_key = keys.get(pos);
                String sender_name = m_User.name;
                String body = sender_name + " has sent you an Editor request";
                sendEditorRequest(body, dest_key).addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            System.out.println("The message was successful " + task.getResult());
                            Toast.makeText(EditorListWritersActivity.this, "Request was successfully sent!", Toast.LENGTH_LONG).show();
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
        data.put("push", true); //always include this, please. It is unknown what happeneds,
                                        // if it ain't there.

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

    private Task<String> sendEditorRequest(String body, String key) {

        Map<String, Object> data = new HashMap<>();
        data.put("push", true);
        data.put("body", body);
        data.put("dest_key", key);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff.getHttpsCallable("sendWriterRequest")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult,String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = "";
                        return result;
                    }
                });
    }
}
