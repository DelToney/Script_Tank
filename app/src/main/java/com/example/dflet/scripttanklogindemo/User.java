package com.example.dflet.scripttanklogindemo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.Serializable;

//user object. firebase allows us to store java objects directly in the db
//and it will serialize it and convert it to JSON. simple fields are provided here

@IgnoreExtraProperties
public abstract class User implements Serializable {

    public String email, name, phoneNumber, type, key, fb_id, token, age;

    public User() {
        return;
    }
    public User(String email, String phoneNumber, String name, String type, String id, String age) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.name = name;
        this.fb_id = id;
        this.age = age;
    }

    //set unique key from push operation
    public void setKey(String key) {
        this.key = key;
    }

    public void setToken(String token) {
        this.token = token;
    }

}