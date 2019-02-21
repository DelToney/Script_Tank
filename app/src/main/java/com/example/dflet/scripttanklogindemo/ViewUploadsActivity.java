package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.database.Cursor;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ViewUploadsActivity extends AppCompatActivity {

    private static User m_User;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_uploads);
        Intent recvIntent = getIntent();
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        this.setSupportActionBar(toolbar);
//        ActionBar ab = getSupportActionBar();
//        ab.setDisplayHomeAsUpEnabled(true);
//        ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        m_User = (User)recvIntent.getSerializableExtra(getString(R.string.user_profile_intent));

        //creating test Items
        ArrayList<TestItem> testItems = new ArrayList<>();
        testItems.add(new TestItem(R.drawable.ic_person_black_24dp, "Story 1", "desc 1"));
        testItems.add(new TestItem(R.drawable.ic_person_black_24dp, "Story 2", "desc 2"));
        testItems.add(new TestItem(R.drawable.ic_person_black_24dp, "Story 3", "desc 3"));


        //assigning the recycler view adapters

        mRecyclerView = findViewById(R.id.uploadedRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ViewAdapter(testItems);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


    }

}
