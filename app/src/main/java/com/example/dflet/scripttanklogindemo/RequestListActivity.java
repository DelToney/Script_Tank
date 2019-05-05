package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestListActivity extends AppCompatActivity implements RequestDialogFragment.OnFragmentInteractionListener {

    protected ScriptTankApplication myApp;
    private RecyclerView reqList;
    private RequestListAdapter adapter;
    private static ArrayList<String> requestNames, keys, request_ids;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_list);
        myApp = (ScriptTankApplication)this.getApplicationContext();
        reqList = findViewById(R.id.requestListRecyclerView);
        reqList.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        reqList.addItemDecoration(itemDecoration);
        requestNames = new ArrayList<>();
        request_ids = new ArrayList<>();
        keys = new ArrayList<>();
        adapter = new RequestListAdapter(RequestListActivity.this, requestNames);
        adapter.notifyDataSetChanged();
        reqList.setAdapter(adapter);
        reqList.setLayoutManager(new LinearLayoutManager(this));
        grabUsersRequests().addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
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
        adapter.setOnItemClickListener(new RequestListAdapter.RequestListClickListener() {
            @Override
            public void onItemClicked(int pos) {


                Bundle bundle = new Bundle();
                bundle.putString("userName", requestNames.get(pos));

                bundle.putString("request_id", request_ids.get(pos));
                bundle.putString("user_id", keys.get(pos));
                RequestDialogFragment fragobj = new
                        RequestDialogFragment();
                fragobj.setArguments(bundle);
                fragobj.show(getSupportFragmentManager(), "REQUEST_DIALOG");
               // FragmentManager fragmentManager = getSupportFragmentManager();



            }


        });
    }

      public void onRequestHandled(String result) {

      }


    private void updateListAdapter(HashMap<String, Object> results) {

        ArrayList<String> tempNames, tempKeys, tempReqIds;
        tempKeys = (ArrayList<String>) results.get("user_ids");
        tempNames = (ArrayList<String>) results.get("names");
        tempReqIds = (ArrayList<String>) results.get("request_ids");
        if (!(keys.isEmpty()))
            keys.clear();
        if (!(requestNames.isEmpty()))
            requestNames.clear();
        if (!(request_ids.isEmpty()))
            request_ids.clear();
        for (String id : tempKeys) {
            keys.add(id);
        }
        for (String name : tempNames) {
            requestNames.add(name);
        }
        for (String id : tempReqIds) {
            request_ids.add(id);
        }
        adapter.notifyDataSetChanged();
    }

    private Task<HashMap<String, Object>> grabUsersRequests() {
        Map<String, Object> data = new HashMap<>();
        String user_id = myApp.getM_User().getKey();
        data.put("user_id", user_id);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("grabUserRequests")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                });
    }


    public Task<String> handleRequest(String result, String request_id, String requester_id) {
        Map<String, Object> data = new HashMap<>();
        String user_id = myApp.getM_User().getKey();
        data.put("user_id", user_id);
        data.put("request_id", request_id);
        data.put("requester_id", requester_id);
        data.put("result", result);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("handleRequest")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        return "";
                    }
                });
    }

}
