package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EditorListWritersActivity extends AppCompatActivity {

    private RecyclerView writerList;
    private SearchView userSearch;
    private Button filterButton;
    private static ArrayList<String> writerNames, keys;
    private WriterRequestListAdapter writerRequestListAdapter;
    private boolean sortDirection, sorting; // false == A to Z , true == Z to A
    private static User m_User;
    protected ScriptTankApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_list_writers);
        myApp = (ScriptTankApplication)this.getApplicationContext(); //get application context
        m_User = myApp.getM_User(); //get user object for in activity use
        myApp.setCurrActivity(this);//set this as current activity at application level
        sortDirection = false;
        sorting = false;
        writerList = findViewById(R.id.writersList);
        userSearch = findViewById(R.id.userSearchView);
        filterButton = findViewById(R.id.filterButton);
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

                updateListAdapter(results);
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
                Intent intent = new Intent(EditorListWritersActivity.this, ProfileActivity.class);
                intent.putExtra("key", keys.get(pos));
                startActivity(intent);
            }
        });

        userSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                searchForWriters(s).addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
                    @Override
                    public void onComplete(@NonNull Task<HashMap<String, Object>> task) {
                        if (task.isSuccessful()) {
                            HashMap<String, Object> results = task.getResult();

                            updateListAdapter(results);
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
                return false;
            }
        });
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(sorting)) {
                    sorting = true;
                    sortList();
                    sortDirection = !sortDirection;
                    sorting = false;
                }
            }
        });

    }





    private void updateListAdapter(HashMap<String, Object> results) {

        ArrayList<String> tempNames, tempKeys;
        tempKeys = (ArrayList<String>)results.get("db_ids");
        tempNames = (ArrayList<String>)results.get("names");
        if (!(keys.isEmpty()))
            keys.clear();
        if (!(writerNames.isEmpty()))
            writerNames.clear();
        for (String id : tempKeys) {
            keys.add(id);
        }
        for (String name : tempNames) {
            writerNames.add(name);
        }
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

    private Task<HashMap<String, Object>> searchForWriters(String query) {
        Map<String, Object> data = new HashMap<>();
        //data.put("push", true); //always include this, please. It is unknown what happens,
        // if it ain't there.
        data.put("query", query);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("searchForWriters")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                });
    }

    private void sortList() {
        HashMap<String, String> keyNamePairs = new  HashMap<>();
        int userSetSize = writerNames.size();
        for (int i = 0; i<userSetSize; i++) {
            keyNamePairs.put( keys.get(i), writerNames.get(i));
        }
        List<Map.Entry<String, String> > sortedKeys =
                new LinkedList<>(keyNamePairs.entrySet());
        Collections.sort(sortedKeys, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> stringStringEntry, Map.Entry<String, String> t1) {
                if (sortDirection)
                    return -(stringStringEntry.getValue().compareTo(t1.getValue()));
                return stringStringEntry.getValue().compareTo(t1.getValue());
            }
        });
        ArrayList<String> tempNames, tempKeys;
        tempNames = new ArrayList<>();
        tempKeys = new ArrayList<>();
        for (Map.Entry<String, String> user : sortedKeys) {
            tempNames.add(user.getValue());
            tempKeys.add(user.getKey());
        }
        HashMap<String, Object> dataSet = new HashMap<>();
        dataSet.put("db_ids", tempKeys);
        dataSet.put("names", tempNames);
        updateListAdapter(dataSet);
    }
}
