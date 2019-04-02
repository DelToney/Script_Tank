package com.example.dflet.scripttanklogindemo;

import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class writer_search extends AppCompatActivity {
    // Initialization of variables
    private Toolbar toolbar;
    private TextView keywordBox;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    public String keyword;
    private String dbName;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writer_search);

        // Toolbar at top of screen
        toolbar = findViewById(R.id.toolbar);

        // Keyword text box
        keywordBox = findViewById(R.id.keywordsSearchBox);

        // Keyword text
        keyword = keywordBox.getText().toString();

        // List of results
        recyclerView = findViewById(R.id.resultsList);

        // Database URL (will remove if not needed) and Database reference
        dbName = "https://scripttankdemo.firebaseio.com/";
        db = FirebaseDatabase.getInstance().getReference();

        setSupportActionBar(toolbar); //Creates toolbar at the top of the screen

        // Creates button in toolbar to access side menu (when I create it)
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        // Checks to see if text in text box has been changed, then updates the query if it does
        keywordBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Technically don't need this. Was automatically added by android studio.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Technically don't need this either
            }
            // Below is where the query is updated
            @Override
            public void afterTextChanged(Editable s) {
                if(!keyword.isEmpty()) {
                    search(keyword);
                }
                else {
                    search("");
                }
            }
        });
    }
    // Search function used in updating search query
    private void search(String s) {
        Query query = db.orderByChild("title")
                .startAt(s) // Starts at the keyword searched for
                .endAt(s + "\uf8ff");
        // Ends at the same keyword plus a special character.
        // Ends same as started because it wants to search for only entries that include the search keyword.

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

// NOTES TO SELF
//          db.orderByChild("title")
//                  .startAt(keyword)
//                  .endAt(keyword+"\uf8ff");
// Fetch video - 11:01 (timestamp)