package com.tronix.kwakvoca;

import java.util.List;

public class WordData {
    String word;
    String meaning;  // Deprecated
    List<String> meanings;
    String user;
    String group;
    String uid;
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
}
