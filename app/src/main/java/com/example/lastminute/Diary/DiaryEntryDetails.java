package com.example.lastminute.Diary;

import com.google.firebase.Timestamp;

public class DiaryEntryDetails {
    private String text;
    private Timestamp created;
    private String userID;
    private String title;


    public DiaryEntryDetails() {}

    public DiaryEntryDetails(String title, String text, Timestamp created, String userID) {
        this.title = title;
        this.text = text;
        this.created = created;
        this.userID = userID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "DiaryEntryDetails{" +
                "text='" + text + '\'' +
                ", created=" + created +
                ", userID='" + userID + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
