package com.example.dflet.scripttanklogindemo;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class writer_search extends AppCompatActivity {
    private Toolbar toolbar;

    private TextView keywordBox;
    private TextView writerBox;

    private CheckBox fantasyBox;
    private CheckBox sciFiBox;
    private CheckBox selfHelpBox;
    private CheckBox romanceBox;
    private CheckBox thrillerBox;
    private CheckBox mysteryBox;
    private CheckBox actionBox;
    private CheckBox dramaBox;
    private CheckBox satireBox;
    private CheckBox historicalFicBox;
    private CheckBox youngAdultBox;
    private CheckBox suspenseBox;

    private FloatingActionButton searchButton;

    private DrawerLayout drawerLayout;

    public String keyword;
    public String writer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_search);

        toolbar = findViewById(R.id.toolbar);

        keywordBox = findViewById(R.id.keywordsSearchBox);
        writerBox = findViewById(R.id.writersSearchBox);

        fantasyBox = findViewById(R.id.fantasyBox);
        sciFiBox = findViewById(R.id.scifiBox);
        selfHelpBox = findViewById(R.id.selfhelpBox);
        romanceBox = findViewById(R.id.romanceBox);
        thrillerBox = findViewById(R.id.thrillerBox);
        mysteryBox = findViewById(R.id.mysteryBox);
        actionBox = findViewById(R.id.actionBox);
        dramaBox = findViewById(R.id.dramaBox);
        satireBox = findViewById(R.id.satireBox);
        historicalFicBox = findViewById(R.id.historicalFicBox);
        youngAdultBox = findViewById(R.id.youngAdultBox);
        suspenseBox = findViewById(R.id.suspenseBox);

        searchButton = findViewById(R.id.searchButton);

        setSupportActionBar(toolbar); //Creates toolbar at the top of the screen

        // Creates button in toolbar to access side menu
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        // When the floating action button (search button) is pressed, searches for keywords/writers (if textboxes aren't
        // empty) and genres (when checkboxes are checked)
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keyword = keywordBox.getText().toString();
                writer = writerBox.getText().toString();
                if(keyword.matches("")){
                    // Dont include in search
                }
                else {
                    // Search for keyword
                }

                if(writer.matches("")){
                    // Dont include in search
                }

                else {
                    // Search for writer
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Opens the side menu
        switch(item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
