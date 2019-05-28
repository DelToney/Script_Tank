package com.example.dflet.scripttanklogindemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PublisherSuggestionActivity extends AppCompatActivity {

    private ArrayAdapter<String> spinnerAdapter;
    private ArrayList<String> names, ids;
    private EditText authorName, ideaName, comments;
    private Spinner publisherNames;
    private MyListener myListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher_suggestion);
        publisherNames = findViewById(R.id.pubSpinner);
        authorName = findViewById(R.id.authorNameEditText);
        ideaName = findViewById(R.id.ideaEditText);
        comments = findViewById(R.id.commentsEditText);
        names = new ArrayList<>();
        spinnerAdapter = new ArrayAdapter<String>(PublisherSuggestionActivity.this
                , android.R.layout.simple_spinner_item, names);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        publisherNames.setAdapter(spinnerAdapter);
        myListener = new MyListener();
        publisherNames.setOnItemSelectedListener(myListener);
        grabAllPubs().addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, Object>> task) {
                if (task.isSuccessful()) {
                    HashMap<String, Object> results = task.getResult();


                    ids = (ArrayList<String>) results.get("db_ids");
                    names = (ArrayList<String>) results.get("names");
                    System.out.println(names);
                    spinnerAdapter = new ArrayAdapter<String>(PublisherSuggestionActivity.this
                            , android.R.layout.simple_spinner_item, names);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    publisherNames.setAdapter(spinnerAdapter);
                }
            }
        });
        final Button subBut = findViewById(R.id.submitSuggestionFormButton);
        subBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSuggestion();
            }
        });
    }

    private void sendSuggestion() {

        authorName.setError(null);
        comments.setError(null);
        ideaName.setError(null);



        final String author = authorName.getText().toString();
        final String comm = comments.getText().toString();
        String idea = ideaName.getText().toString();


        if (TextUtils.isEmpty(author)) {
            authorName.setError(getString(R.string.error_field_required));
            authorName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(idea)) {
           ideaName.setError(getString(R.string.error_field_required));
            ideaName.requestFocus();
            return;
        }

        int pubPos = myListener.getPos();
        String pubId = ids.get(pubPos);

        pushSuggestion(pubId, author, idea, comm).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PublisherSuggestionActivity.this, "Suggestion is sent!", Toast.LENGTH_LONG).show();
                    comments.setText("");
                    authorName.setText("");
                    ideaName.setText("");
                }
            }
        });

    }

    private Task<HashMap<String, Object>> grabAllPubs() {
        Map<String, Object> data = new HashMap<>();


        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("grabAllPublishers")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                });


    }

    private Task<String> pushSuggestion(String id, String a_name, String idea, String comments) {
        Map<String, Object> data = new HashMap<>();
        data.put("pub_id", id);
        data.put("comments", comments);
        data.put("author",a_name);
        data.put("idea", idea);


        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("pushSuggestion")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {

                        return "";
                    }
                });
    }

    //listener class for spinner
    public class MyListener implements AdapterView.OnItemSelectedListener {

        private int pos;

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            this.pos = pos;
        }
        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }

        public int getPos() {
            return this.pos;
        }

    }

}
