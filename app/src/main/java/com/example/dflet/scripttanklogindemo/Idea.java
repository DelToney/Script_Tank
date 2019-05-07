package com.example.dflet.scripttanklogindemo;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Idea implements Serializable {

    public String IdeaName, Abstract, Description, Genre, Publisher, Length,  key, WriterID, WriterName, Worth, WriterPicture, FileName;

    public Idea() {
        return;
    }

    public Idea(String IdeaName, String ideaAbstract, String ideaDescription, String genre, String length, String authorName, String authorID, String ideaWorth) {
        this.IdeaName = IdeaName;
        this.Abstract = ideaAbstract;
        this.Description = ideaDescription;
        this.Genre = genre;
        this.Length = length;
        this.Publisher = null;
        this.WriterID = authorID;
        this.WriterName = authorName;
        this.Worth = ideaWorth;
    }

    public Idea(String IdeaName, String ideaAbstract, String ideaDescription, String genre, String length, String authorName, String authorID, String ideaWorth, String FileName) {
        this.IdeaName = IdeaName;
        this.Abstract = ideaAbstract;
        this.Description = ideaDescription;
        this.Genre = genre;
        this.Length = length;
        this.Publisher = null;
        this.WriterID = authorID;
        this.WriterName = authorName;
        this.Worth = ideaWorth;
        this.FileName = FileName;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
