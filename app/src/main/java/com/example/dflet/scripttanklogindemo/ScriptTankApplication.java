package com.example.dflet.scripttanklogindemo;

import android.app.Activity;
import android.app.Application;

public class ScriptTankApplication extends Application {


    //this class is the application level class. The app instance can always be used to find the
    //values stored within.
    private static User m_User;
    private Activity currActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        m_User = null;
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

    public static User getM_User() {
        return m_User;
    }
}
