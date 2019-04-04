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


public class HomeActivity extends AppCompatActivity {

    private DrawerLayout m_Layout;
    private NavigationView m_NavigationView;
    private static User m_User;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent launchIntent = getIntent();
        m_User = (User)launchIntent.getSerializableExtra(getString(R.string.user_profile_intent));
        Toolbar toolbar = findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        m_Layout = findViewById(R.id.drawer_layout);
        m_NavigationView = findViewById(R.id.nav_view);
        searchButton = findViewById(R.id.searchUIButton);
        setNavMenu();
        m_NavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.ViewExtracts:
                        menuItem.setChecked(true);
                        m_Layout.closeDrawers();
                        Intent intent = new Intent(HomeActivity.this,
                                ViewExtractsActivity.class);
                        intent.putExtra(getString(R.string.user_profile_intent), (Parcelable) m_User);
                        menuItem.setChecked(false);
                        startActivity(intent);
                        return true;
                    case R.id.UploadFiles:
                        menuItem.setChecked(true);
                        m_Layout.closeDrawers();
                        intent = new Intent(HomeActivity.this,
                                UploadFileActivity.class);
                        intent.putExtra(getString(R.string.user_profile_intent), (Parcelable) m_User);
                        menuItem.setChecked(false);
                        startActivity(intent);
                        return true;
                    case R.id.contactWriters:
                        menuItem.setChecked(true);
                        m_Layout.closeDrawers();
                        intent = new Intent(HomeActivity.this,
                                EditorListWritersActivity.class);
                        intent.putExtra(getString(R.string.user_profile_intent), (Parcelable) m_User);
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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, writer_search.class));
            }
        });

    }

    private void logOut() {
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
                m_NavigationView.inflateMenu(R.xml.drawer_view_writer);
                return;
            case "Publisher":
                m_NavigationView.inflateMenu(R.xml.drawer_view_publisher);
                return;
            case "Editor":
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
