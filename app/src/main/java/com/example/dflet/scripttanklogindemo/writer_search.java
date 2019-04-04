package com.example.dflet.scripttanklogindemo;

import android.support.annotation.NonNull;
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

public class writer_search extends AppCompatActivity {
    // Initialization of variables
    private Toolbar toolbar;
    private TextView keywordBox;
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    public String keyword;
    private FirebaseFunctions functions;
    private ArrayList<String> ideaTitles;
    private ArrayList<String> writers;
    private ArrayList<WriterSearchResult> writerSearchResults;
    private SearchResultAdapter adapter;
    private WriterSearchResult result;
    private Button button;

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
        // Firebase initialization
        functions = FirebaseFunctions.getInstance();
        // Array of results
        writerSearchResults = new ArrayList<>();
        // Search button
        button = findViewById(R.id.button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        setSupportActionBar(toolbar); //Creates toolbar at the top of the screen

        // Creates button in toolbar to access side menu (when I create it)
/*        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);*/

        // RecyclerView Adapter
        adapter = new SearchResultAdapter(writerSearchResults);
        recyclerView.setAdapter(adapter);

        // Checks when button is pressed
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // Clear current search results
                writerSearchResults.clear();

                // Update RecyclerView with results
                searchForIdeas(keywordBox.getText().toString()).addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>(){
                    @Override
                    public void onComplete(@NonNull Task<HashMap<String, Object>> task){
                        // If the firebase function executes successfully
                        if(task.isSuccessful()){
                            // Log 'success' in the Logcat
                            Log.d("Success", "Success");
                            // Set results to the result of the firebase function
                            HashMap<String, Object> results = task.getResult();
                            // Update RecyclerView with results
                            updateListAdapter(results);
                            // Debug stuff
                            System.out.println(results.size());
                            System.out.println(results);
                        }
                        else {
                            // If the firebase fails
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                // Logs error in the firebase console
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }
                            // Prints error message in Logcat
                            System.err.println(e.getMessage());
                        }
                    }
                });
            }
        });


    }
    // Search function used in updating search query
    private Task<HashMap<String, Object>> searchForIdeas(String query){
        // Data put into function
        Map<String, Object> data = new HashMap<>();
        data.put("query", query);

        // Call firebase function
        return functions.getHttpsCallable("searchForIdeas")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                });
    }

    // Updates adapter with data from search result
    private void updateListAdapter(HashMap<String, Object> results){
        // Arrays for different text in the RecyclerView entry
        ideaTitles = (ArrayList<String>)results.get("Ideas");
        writers = (ArrayList<String>)results.get("Writers");

        // For each entry in ideaTitles
        for(int i = 0; i < ideaTitles.size(); i++) {
            // Add new search result
            writerSearchResults.add(new WriterSearchResult(R.drawable.ic_person_black_24dp, ideaTitles.get(i), writers.get(i)));
        }

        // Notify the adapter that the data has changed, changing the RecyclerView entries
        adapter.notifyDataSetChanged();
    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Opens the side menu
        switch(item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

}

// NOTES TO SELF
// https://us-central1-scripttankdemo.cloudfunctions.net/searchForIdeas