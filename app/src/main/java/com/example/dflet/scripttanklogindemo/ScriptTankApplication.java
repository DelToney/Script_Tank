package com.example.dflet.scripttanklogindemo;

import android.app.Activity;
import android.app.Application;

public class ScriptTankApplication extends Application {


    //this class is the application level class. The app instance can always be used to find the
    //values stored within.
    private static User m_User;



    private static Idea mCurrentIdea = new Idea();



    private static User mCurrentUser;
    private Activity currActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        m_User = null;
        mCurrentUser = null;
        // Required initialization logic here!
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void setCurrActivity(Activity currActivity) {
        this.currActivity = currActivity;
    }

    public Activity getCurrActivity() {
        return this.currActivity;
    }


    public void setM_User(User user) {m_User = user;}
    public static User getM_User() {return m_User;}


    public void setmCurrentUser(User user) {mCurrentUser = user;}
    public static User getmCurrentUser() {return mCurrentUser;}
    public static void setmCurrentUserKey(String key) {mCurrentUser.setKey(key);}


    public static Idea getmCurrentIdea() {return mCurrentIdea;}
    public static void setmCurrentIdea(Idea CurrentIdea) {mCurrentIdea = CurrentIdea;}
    public static void setmCurrentIdeaKey(String key) {mCurrentIdea.setKey(key);}
}
