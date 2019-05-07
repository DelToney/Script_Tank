package com.example.dflet.scripttanklogindemo;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PublisherIdeaList extends AppCompatActivity {

    private static User m_User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publisher_idea_list);
        Intent launchIntent = getIntent();
        m_User = (User)launchIntent.getSerializableExtra(getString(R.string.user_profile_intent));
    }

//    Intent intent = new Intent(HomeActivity.this,
//            ViewExtractsActivity.class);
//    intent.putExtra(getString(R.string.user_profile_intent), (Parcelable) m_User);
}
