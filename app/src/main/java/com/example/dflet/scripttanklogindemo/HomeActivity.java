package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


public class HomeActivity extends AppCompatActivity {

    protected ScriptTankApplication myApp;
    private DrawerLayout m_Layout;
    private NavigationView m_NavigationView;
    private static User m_User;

    private static Editor m_Editor;
    private TextView misterMister;
    private ImageView delPic;
    private boolean imgAlreadySet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        myApp = (ScriptTankApplication)this.getApplicationContext(); //these three lines need to be in every
                                                                    //activity that uses the user profile
        m_User = myApp.getM_User();
        myApp.setCurrActivity(this);
        Toolbar toolbar = findViewById(R.id.toolbar); //grab user toolbar
        this.setSupportActionBar(toolbar); //set it as the action bar
        ActionBar ab = getSupportActionBar(); // grab new action bar and set properties
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        m_Layout = findViewById(R.id.drawer_layout);
        m_NavigationView = findViewById(R.id.nav_view);
        misterMister = findViewById(R.id.greetingsText);

        delPic = findViewById(R.id.delPic);
        setNavMenu();

        // Side menu options
        m_NavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.ViewMyFiles:
                    menuItem.setChecked(true);
                    m_Layout.closeDrawers();
                    Intent intent = new Intent(HomeActivity.this,
                            ViewUploadsActivity.class);
                    menuItem.setChecked(false);
                    startActivity(intent);
                    return true;
                case R.id.IdeasSearch:
                    menuItem.setChecked(true);
                    m_Layout.closeDrawers();
                    intent = new Intent(HomeActivity.this,
                           writer_search.class);
                    menuItem.setChecked(false);
                    startActivity(intent);
                    return true;
                case R.id.ViewProfile:
                    menuItem.setChecked(true);
                    m_Layout.closeDrawers();
                    intent = new Intent(HomeActivity.this,
                           WriterProfileActivity.class);
                    menuItem.setChecked(false);
                    startActivity(intent);
                    return true;
                case R.id.UploadIdea:
                    menuItem.setChecked(true);
                    m_Layout.closeDrawers();
                    intent = new Intent(HomeActivity.this,
                           UploadIdeas.class);
                    menuItem.setChecked(false);
                    startActivity(intent);
                    return true;
                case R.id.contactWriters:
                    menuItem.setChecked(true);
                    m_Layout.closeDrawers();
                    intent = new Intent(HomeActivity.this,
                            EditorListWritersActivity.class);
                    menuItem.setChecked(false);
                    startActivity(intent);
                    return true;
                case R.id.sendMessageDrawer:
                    menuItem.setChecked(true);
                    m_Layout.closeDrawers();
                    intent = new Intent(HomeActivity.this,
                            ChatListActivity.class);
                    menuItem.setChecked(false);
                    startActivity(intent);
                    return true;
                case R.id.settings:
                    menuItem.setChecked(true);
                    m_Layout.closeDrawers();
                    intent = new Intent(HomeActivity.this,
                            SettingsActivity.class);
                    menuItem.setChecked(false);
                    startActivity(intent);
                    return true;



                    case R.id.PublisherIdeas:
                        menuItem.setChecked(true);
                        m_Layout.closeDrawers();
                        intent = new Intent(HomeActivity.this,
                                PublisherIdeaList.class);
                        menuItem.setChecked(false);
                        startActivity(intent);
                        return true;
                    case R.id.sendSuggestionDrawer:
                        menuItem.setChecked(true);
                        m_Layout.closeDrawers();
                        intent = new Intent(HomeActivity.this,
                                PublisherSuggestionActivity.class);
                        menuItem.setChecked(false);
                        startActivity(intent);
                        return true;
                    case R.id.viewRequestsDrawer:
                        menuItem.setChecked(true);
                        m_Layout.closeDrawers();
                        intent = new Intent(HomeActivity.this,
                                RequestListActivity.class);
                        menuItem.setChecked(false);
                        startActivity(intent);
                        return true;
                    default:
                        return true;
                }

            }
        });

        final Button button = findViewById(R.id.logOutHomeAct);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
        imgAlreadySet = false;
        checkSettings();
        misterMister.setText("Greetings, " + m_User.getName() + "\nWelcome to ScriptTank");



    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSettings();
    }

    private void checkSettings() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        Boolean delIsIdiot = sharedPreferences.getBoolean("delIsIdiot" , false);
        if (delIsIdiot) {
            if (!(imgAlreadySet)) {
                imgAlreadySet = true;

                getDelImage();
            }
        } else {
            imgAlreadySet = false;

            delPic.setImageResource(android.R.color.transparent);
        }
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        this.deleteFile(getString(R.string.user_prof_file_name));
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().clear();
        sharedPreferences.edit().apply();
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void getDelImage() {

        try {
            FirebaseStorage fs = FirebaseStorage.getInstance("gs://scripttankdemo.appspot.com");
            StorageReference fRef = fs.getReference().child("del_boy.jpg");
            fRef.getBytes(10 * 1024 * 1024).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    if (task.isSuccessful()) {
                        int size = task.getResult().length;
                        byte[] img_bytes = task.getResult();
                        Bitmap bitmap_tmp;
                        bitmap_tmp = BitmapFactory.decodeByteArray(img_bytes, 0, size);
                        delPic = findViewById(R.id.delPic);
                        delPic.setImageBitmap(bitmap_tmp);
                    }
                }
            });
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void setNavMenu() {
        if (m_User == null) {

            //m_NavigationView.inflateHeaderView(R.layout.drawer_header_publisher);
            m_NavigationView.inflateMenu(R.xml.drawer_view_writer);
            return;
        }

        System.out.println(m_User.fb_id);
        switch(m_User.type) {
            case "Writer":
                m_Editor = (Editor)m_User;
                //m_NavigationView.inflateHeaderView(R.layout.drawer_header_publisher);
                m_NavigationView.inflateMenu(R.xml.drawer_view_writer);
                return;
            case "Publisher":
                m_Editor = (Editor)m_User;
                //m_NavigationView.inflateHeaderView(R.layout.drawer_header_publisher);
                m_NavigationView.inflateMenu(R.xml.drawer_view_publisher);
                return;
            case "Editor":
                m_Editor = (Editor)m_User;
                //m_NavigationView.inflateHeaderView(R.layout.drawer_header_publisher);
                m_NavigationView.inflateMenu(R.xml.drawer_view_editor);
                return;
            default:
               // m_NavigationView.inflateHeaderView(R.layout.drawer_header_publisher);
                m_NavigationView.inflateMenu(R.xml.drawer_view_writer);
                System.err.println("Navmenus: something unexpected happened!");
                return;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        m_User = (User) intent.getSerializableExtra(getString(R.string.user_profile_intent));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                m_Layout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
