package com.example.dflet.scripttanklogindemo;

import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SymbolTable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewUploadsActivity extends AppCompatActivity {

    protected ScriptTankApplication myApp;
    private static User m_User;
    private RecyclerView mRecyclerView;
    private ViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<TestItem> testItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_uploads);
        myApp = (ScriptTankApplication) this.getApplicationContext();
        Intent intent = getIntent();
        m_User = myApp.getM_User();
        myApp.setCurrActivity(this);

        String intentKey;


        if ((intentKey = intent.getStringExtra("UserID")) == null) {
            intentKey = myApp.getM_User().key;
        }


//        Toolbar toolbar = findViewById(R.id.toolbar);
//        this.setSupportActionBar(toolbar);
//        ActionBar ab = getSupportActionBar();
//        ab.setDisplayHomeAsUpEnabled(true);
//        ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);





        //creating test Items

//        testItems.add(new TestItem(R.drawable.ic_person_black_24dp, "Story 1", "desc 1"));
//        testItems.add(new TestItem(R.drawable.ic_person_black_24dp, "Story 2", "desc 2"));
//        testItems.add(new TestItem(R.drawable.ic_person_black_24dp, "Story 3", "desc 3"));


        //assigning the recycler view adapters

        mRecyclerView = findViewById(R.id.uploadedRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ViewAdapter(testItems);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //on idea click, open its profile
        mAdapter.setOnItemClickListener(new ViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
            Intent intent = new Intent(ViewUploadsActivity.this,
                    IdeaProfile.class);
            intent.putExtra("IdeaID", testItems.get(position).getText2());
            startActivity(intent);
            }
        });

        getUserIdeas(intentKey).addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, Object>> task) {
                if (task.isSuccessful()) {
                    System.out.println(m_User.key);
                    updateListAdapter(task.getResult());
                } else {
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                        System.out.println(code + (String)details);
                    }
                    System.err.println(e.getMessage());
                }
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myApp.getmCurrentUser()!= null){
            myApp.setmCurrentUser(null);
        }
    }

    private Task<HashMap<String, Object>> getUserIdeas(Object key) {
        Map<String, Object> data = new HashMap<>();
        //data.put("push", true); //always include this, please. It is unknown what happens,
        // if it ain't there.
        data.put("userID", key);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("getUserIdeas")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                });
    }

    private void updateListAdapter(HashMap<String, Object> results) {

        ArrayList<String> names = (ArrayList<String>)results.get("IdeaNames");
        ArrayList<String> ideaIDs = (ArrayList<String>)results.get("IdeaIDs");
        for (int i = 0; i < names.size();i++ ) {
            testItems.add(new TestItem(R.drawable.ic_person_black_24dp, names.get(i), ideaIDs.get(i)));
        }
        mAdapter.notifyDataSetChanged();

    }

}
