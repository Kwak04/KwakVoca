package com.tronix.kwakvoca;

import com.google.firebase.Timestamp;

import java.util.List;

public class WordData {
    String word;
    String meaning;  // Deprecated
    List<String> meanings;
    String user;
    String group;
    String uid;
    Timestamp time;
    boolean isBookmarked;
    String documentId;

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

    public List<String> getMeanings() {
        return meanings;
    }

    public String getUser() {
        return user;
    }

    public String getGroup() {
        return group;
    }

    public String getUid() {
        return uid;
    }

    public Timestamp getTime() {
        return time;
    }
}
