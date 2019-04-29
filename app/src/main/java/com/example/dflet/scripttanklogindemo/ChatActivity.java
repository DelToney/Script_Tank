package com.example.dflet.scripttanklogindemo;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private Button sendMessageButton;
    private RecyclerView chat;
    private ChatMessagesAdapter adapter;

    private EditText messageArea;
    private static ArrayList<String> messages, ids;
    protected ScriptTankApplication myApp;
    protected String receiver_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messages = new ArrayList<>();
        ids = new ArrayList<>();
        receiver_id = getIntent().getStringExtra("receiver_id");
        myApp = (ScriptTankApplication)getApplicationContext();
        String user_id = myApp.getM_User().getKey();
        chat = findViewById(R.id.chatAreaRecycleView);
        chat.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        chat.addItemDecoration(itemDecoration);
        adapter = new ChatMessagesAdapter(ChatActivity.this, messages, ids, user_id);
        adapter.notifyDataSetChanged();
        chat.setAdapter(adapter);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
         llm.setStackFromEnd(true);
        chat.setLayoutManager(llm);
        chat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                chat.scrollToPosition(messages.size()-1);
            }
        });
        sendMessageButton = findViewById(R.id.sendMessageChatButton);

        messageArea = findViewById(R.id.messageAreaEditText);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String data = messageArea.getText().toString();
                if (!(TextUtils.isEmpty(data)))
                    sendMessage(data).addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            HashMap<String, Object> results = new HashMap<>();
                            HashMap<String, Object> messages = new HashMap<>();
                            //String key = task.getResult();
                            results.put("key", ScriptTankApplication.getM_User().getKey());
                            results.put("data", data);

                            updateListAdapter(results, true);

                        }
                    });
                hideKeyboard();
                messageArea.setText("");
            }
        });
        retrieveMessages(getIntent().getStringExtra("thread_id")).addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, Object>> task) {
                if (task.isSuccessful()) {
                    HashMap<String, Object> results = task.getResult();

                    updateListAdapter(results, false);
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
    }

    private void updateListAdapter(HashMap<String, Object> results, boolean clientUpdate) {

        if (!(clientUpdate)) {
            ArrayList<HashMap<Object, Object>> messageObjects = (ArrayList<HashMap<Object, Object>>) results.get("messages");
            ArrayList<String> tempMessages, tempIds;
            tempMessages = new ArrayList<>();
            tempIds = new ArrayList<>();
            for (HashMap<Object, Object> messageObject : messageObjects) {
                tempMessages.add((String) messageObject.get("content"));
                tempIds.add((String) messageObject.get("id"));
            }

            if (!(messages.isEmpty()))
                messages.clear();
            if (!(ids.isEmpty()))
                ids.clear();
            for (String id : tempIds) {
                ids.add(id);
            }
            for (String name : tempMessages) {
                messages.add(name);
            }
        } else {
            ids.add((String)results.get("key"));
            messages.add((String)results.get("data"));
        }
        adapter.notifyDataSetChanged();
        chat.scrollToPosition(messages.size()-1);
    }

    private Task<String> sendMessage(String content) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("content", content);
        String id = myApp.getM_User().getKey();
        data.put("recv_id", receiver_id);
        data.put("sender_id", id);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff.getHttpsCallable("sendMessageFCM")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult,String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = "";
                        return result;
                    }
                });

    }

    private Task<HashMap<String, Object>> retrieveMessages(String thread_id) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("thread_id", thread_id);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("retrieveMessages")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object > result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                });
    }

    private void hideKeyboard() {
        Activity act = ChatActivity.this;
        InputMethodManager imm = (InputMethodManager)
                act.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(messageArea.getWindowToken(), 0);
    }


}
