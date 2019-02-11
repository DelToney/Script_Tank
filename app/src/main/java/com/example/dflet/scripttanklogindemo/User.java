package com.example.dflet.scripttanklogindemo;

import com.google.firebase.database.IgnoreExtraProperties;

//sample user object. firebase allows us to store java objects directly in the db
//and it will serialize it and convert it to JSON. simple fields are provided here

@IgnoreExtraProperties
public class User {

    public String email, name, phoneNumber, type, key;

    public User(String email, String phoneNumber, String name,  String type) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.name = name;
    }
    //set unique key from push operation
    public void setKey(String key) {
        this.key = key;
    }
}
