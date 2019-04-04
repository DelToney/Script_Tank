package com.example.dflet.scripttanklogindemo;

import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
        // RecyclerView Adapter
        adapter = new SearchResultAdapter(writerSearchResults);
        // Array of results
        writerSearchResults = new ArrayList<>();
        // Search button
        button = findViewById(R.id.button);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        setSupportActionBar(toolbar); //Creates toolbar at the top of the screen

        searchForIdeas(keyword).addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>(){
            @Override
            public void onComplete(@NonNull Task<HashMap<String, Object>> task){
                if(task.isSuccessful()){
                    System.out.println("Success");
                    updateListAdapter(task.getResult());
                }
                else {
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

        // Creates button in toolbar to access side menu (when I create it)
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                searchForIdeas(keyword);
                adapter.notifyDataSetChanged();
            }
        });

    }
    // Search function used in updating search query
    private Task<HashMap<String, Object>> searchForIdeas(String query){
        Map<String, Object> data = new HashMap<>();
        data.put("query", query);

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

    private void updateListAdapter(HashMap<String, Object> results){

        ideaTitles = (ArrayList<String>)results.get("Ideas");
        writers = (ArrayList<String>)results.get("Writers");
        for(int i = 0; i < ideaTitles.size(); i++) {
            writerSearchResults.add(new WriterSearchResult(R.drawable.ic_person_black_24dp, ideaTitles.get(i), writers.get(i)));
        }

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