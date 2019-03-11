package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity {

    protected ScriptTankApplication myApp;
    private DrawerLayout m_Layout;
    private NavigationView m_NavigationView;
    private static User m_User;
    private static Editor m_Editor;
    private TextView editorBoy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
<<<<<<< Updated upstream
        Intent launchIntent = getIntent();
        m_User = (User)launchIntent.getSerializableExtra(getString(R.string.user_profile_intent));
        final Toolbar toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
=======
        myApp = (ScriptTankApplication)this.getApplicationContext(); //these three lines need to be in every
                                                                    //activity that uses the user profile
        m_User = myApp.getM_User();
        myApp.setCurrActivity(this);
        Toolbar toolbar = findViewById(R.id.toolbar); //grab user toolbar
        this.setSupportActionBar(toolbar); //set it as the action bar
        ActionBar ab = getSupportActionBar(); // grab new action bar and set properties
>>>>>>> Stashed changes
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        m_Layout = findViewById(R.id.drawer_layout);
        m_NavigationView = findViewById(R.id.nav_view);
        editorBoy = findViewById(R.id.editorTest);
        setNavMenu();




        //Navigation button logic
        m_NavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {



                        //publisher buttons
                    case R.id.ViewExtracts:
                        menuItem.setChecked(true);
                        m_Layout.closeDrawers();
                        Intent intent = new Intent(HomeActivity.this,
                                ViewExtractsActivity.class);
                        menuItem.setChecked(false);
                        startActivity(intent);
                        return true;



                        //writer buttons
                    case R.id.ViewUploads:
                        menuItem.setChecked(true);
                        m_Layout.closeDrawers();
                        intent = new Intent(HomeActivity.this,
                                ViewUploadsActivity.class);
                        intent.putExtra(getString(R.string.user_profile_intent), (Parcelable) m_User);
                        menuItem.setChecked(false);
                        startActivity(intent);
                        return true;

                    case R.id.UploadFiles:
                        menuItem.setChecked(true);
                        m_Layout.closeDrawers();
                        intent = new Intent(HomeActivity.this,
                                UploadFileActivity.class);
<<<<<<< Updated upstream
                        intent.putExtra(getString(R.string.user_profile_intent), (Parcelable) m_User);
                        intent.putExtra(getString(R.string.navMenu_intent), (Parcelable) m_NavigationView);
                        intent.putExtra(getString(R.string.layout_intent), (Parcelable) m_Layout);
=======
>>>>>>> Stashed changes
                        menuItem.setChecked(false);
                        startActivity(intent);
                        return true;



                        //editor Buttons
                    case R.id.contactWriters:
                        menuItem.setChecked(true);
                        m_Layout.closeDrawers();
                        intent = new Intent(HomeActivity.this,
                                EditorListWritersActivity.class);
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
        final Button button1 = findViewById(R.id.editorButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editorBoy.setText(m_Editor.getAssociations());
            }
        });
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        this.deleteFile(getString(R.string.user_prof_file_name));
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void setNavMenu() {
        if (m_User == null) {
            m_NavigationView.inflateMenu(R.xml.drawer_view_writer);
            return;
        }


        switch(m_User.type) {
            case "Writer":
                m_Editor = (Editor)m_User;
                m_NavigationView.inflateMenu(R.xml.drawer_view_writer);
                return;
            case "Publisher":
                m_Editor = (Editor)m_User;
                m_NavigationView.inflateMenu(R.xml.drawer_view_publisher);
                return;
            case "Editor":
                m_Editor = (Editor)m_User;
                m_NavigationView.inflateMenu(R.xml.drawer_view_editor);
                return;
            default:
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
