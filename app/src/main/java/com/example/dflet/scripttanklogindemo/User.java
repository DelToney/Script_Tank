package com.example.dflet.scripttanklogindemo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.Serializable;

//user object. firebase allows us to store java objects directly in the db
//and it will serialize it and convert it to JSON. simple fields are provided here

@IgnoreExtraProperties
public class User implements Parcelable, Serializable {

    public String email, name, phoneNumber, type, key, fb_id, token;

   public User(String email, String phoneNumber, String name,  String type, String id) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.name = name;
        this.fb_id = id;
    }
    //set unique key from push operation
    public void setKey(String key) {
        this.key = key;
    }

    private User(Parcel in) {
        this.email = in.readString();
        this.name = in.readString();
        this.phoneNumber = in.readString();
        this.type = in.readString();
        this.key = in.readString();
        this.fb_id = in.readString();
        this.token = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.email);
        parcel.writeString(this.name);
        parcel.writeString(this.phoneNumber);
        parcel.writeString(this.type);
        parcel.writeString(this.key);
        parcel.writeString(this.fb_id);
        parcel.writeString(this.token);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
