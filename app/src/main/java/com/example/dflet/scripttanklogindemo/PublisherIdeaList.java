package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PublisherIdeaList extends AppCompatActivity {

    private static User m_User;
    protected ScriptTankApplication myApp;
    private RecyclerView recyclerView;
    private ArrayList<String> ideaTitles;
    private ArrayList<String> writers;
    //MAKE ADAPTER JAVA FILE
    //ARRAY LIST OF OBJECTS
    private FirebaseFunctions functions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher_idea_list);
        myApp = (ScriptTankApplication)this.getApplicationContext(); //these three lines need to be in every
        m_User = myApp.getM_User();
        functions = FirebaseFunctions.getInstance();
        getPublisherIdeas(m_User.fb_id);
    }


    // Gets all bought Ideas
    private Task<HashMap<String, Object>> getPublisherIdeas(String query){
        // Data put into function
        Map<String, Object> data = new HashMap<>();
        data.put("query", query);

        // Call firebase function
        return functions.getHttpsCallable("getPublisherIdeas")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                });
    }



//    m_User.fb_id
//    Intent intent = new Intent(HomeActivity.this,
//            ViewExtractsActivity.class);
//    intent.putExtra(getString(R.string.user_profile_intent), (Parcelable) m_User);
}
