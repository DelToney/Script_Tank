package com.example.dflet.scripttanklogindemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IdeaProfile extends Activity {

    protected ScriptTankApplication myApp;
    private static User m_User;
    private String mIdeaKey, UserID;
    boolean hasPublisher;
    Button buyIdeaButton;
    private TextView mIdeaTitle, mIdeaAbstract, mWriterName, mGenre, mDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_profile);
        myApp = (ScriptTankApplication) this.getApplicationContext();
        Intent intent = getIntent();
        m_User = myApp.getM_User();
        myApp.setCurrActivity(this);

        //set text fields
        mIdeaTitle = findViewById(R.id.ideaTitle);
        mIdeaAbstract = findViewById(R.id.ideaAbstract);
        mWriterName = findViewById(R.id.authorName);
        mGenre = findViewById(R.id.genre);
        mDescription = findViewById(R.id.IdeaDescption);

        mIdeaKey = intent.getStringExtra("IdeaID");

        System.out.println(mIdeaKey);

        buyIdeaButton = findViewById(R.id.buyIdeaButton);
        final Button profileButton = findViewById(R.id.ProfileButton);

        if (!m_User.type.equals("Publisher")) {
            buyIdeaButton.setVisibility(View.INVISIBLE);
        }




        GetIdeaInfo(mIdeaKey).addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, Object>> task) {
                if (task.isSuccessful()) {
                    SetIdea(task.getResult());
                } else {
                    System.out.println(task.getResult());
                }
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IdeaProfile.this,
                        WriterProfileActivity.class);
                intent.putExtra("UserID", UserID);
                startActivity(intent);
            }
        });

        buyIdeaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {AlertDialog.Builder builder = new AlertDialog.Builder(IdeaProfile.this);
                builder.setMessage("Are you sure you want to purchase?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                System.out.println(m_User.key);
                                buyIdea(m_User.key, mIdeaKey, UserID);
//                                Snackbar.make(, )
                                Intent intent = new Intent(IdeaProfile.this, HomeActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void SetIdea(HashMap<String, Object> result) {
        mIdeaTitle.setText((String)result.get("IdeaName"));
        mIdeaAbstract.setText((String)result.get("Abstract"));
        mWriterName.setText((String)result.get("WriterName"));
        mGenre.setText((String)result.get("Genre"));
        mDescription.setText((String)result.get("Description"));
        UserID = (String)result.get("WriterID");
        hasPublisher = result.containsKey("Publisher");
        if (hasPublisher) hasPublisher = !result.get("Publisher").toString().isEmpty();
        if (hasPublisher) buyIdeaButton.setVisibility(View.INVISIBLE);
    }

    private Task<HashMap<String, Object>> GetIdeaInfo(String ideaKey) {
        Map<String, Object> data = new HashMap<>();

        data.put("ideaKey", ideaKey);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("getIdeaByID")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap <String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                });
    }
    private void buyIdea(String publisherID, String ideaID, String writerID){
        Map<String, Object> data = new HashMap<>();
        data.put("writerID", writerID);
        data.put("boughtIdeaID", ideaID);
        data.put("newPublisherID", publisherID);
        System.out.println(publisherID +
                "\n" +
                ideaID +
                "\n" +
                writerID);
        FirebaseFunctions ff = FirebaseFunctions.getInstance();
        ff.getHttpsCallable("buyIdea").call(data);
        System.out.println("Success!");
    }


}
