package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
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

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView chatList;
    private ChatListAdapater adapter;
    protected ScriptTankApplication myApp;
    private static ArrayList<String> chatNames, keys, thread_ids;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        myApp = (ScriptTankApplication)this.getApplicationContext();
        chatList = findViewById(R.id.chatListRecyclerView);
        chatList.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        chatList.addItemDecoration(itemDecoration);
        chatNames = new ArrayList<>();
        keys = new ArrayList<>();
        adapter = new ChatListAdapater(ChatListActivity.this, chatNames);
        adapter.notifyDataSetChanged();
        chatList.setAdapter(adapter);
        chatList.setLayoutManager(new LinearLayoutManager(this));
        grabUserFriends().addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
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
        adapter.setOnItemClickListener(new ChatListAdapater.ChatListClickListener() {
            @Override
            public void onItemClicked(int pos) {
                Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
                intent.putExtra("thread_id", thread_ids.get(pos));
                intent.putExtra("receiver_id", keys.get(pos));
                startActivity(intent);
            }


        });
    }
    private void updateListAdapter(HashMap<String, Object> results) {

        ArrayList<String> tempNames, tempKeys;
        tempKeys = (ArrayList<String>)results.get("keys");
        tempNames = (ArrayList<String>)results.get("friend_names");
        thread_ids = (ArrayList<String>)results.get("threads");
        if (!(keys.isEmpty()))
            keys.clear();
        if (!(chatNames.isEmpty()))
            chatNames.clear();
        for (String id : tempKeys) {
            keys.add(id);
        }
        for (String name : tempNames) {
            System.out.println(name);
            chatNames.add(name);
        }
        adapter.notifyDataSetChanged();
    }

    private Task<HashMap<String, Object>> grabUserFriends() {
        Map<String, Object> data = new HashMap<>();
        String user_id = myApp.getM_User().getKey();
        data.put("user_id", user_id);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("grabUserFriends")
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
