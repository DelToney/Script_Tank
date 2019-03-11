package com.example.dflet.scripttanklogindemo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


//sample Editor class. This will change. But your classes for your user types must have the same
//same class definition as mine. User is base class and seriablizable so that it can be written to
//files
public class Editor extends User implements Serializable {

    //enter in your private variables unique to your class
    private String Associations,
            Experience,
            Employer,
            Specializations;

    public Editor(String email, String phoneNumber, String name, String type, String id,
                  String [] editorData) {
        super(email, phoneNumber, name, type, id);
        this.Employer = editorData[0];
        this.Experience = editorData[1];
        this.Specializations = editorData[2];
        this.Associations = editorData[3];
    }

    //write get/setters if you wish

    public String getAssociations() {
        return Associations;
    }
    public String getExperience() {
        return Experience;
    }

    public String getEmployer() {
        return Employer;
    }

    public String getSpecializations() {
        return Specializations;
    }


}

