package com.tronix.kwakvoca;

import java.util.List;

class VersionData {
    String version;
    int versionInt;
    String date;
    List<String> improvements;

    public String getVersion() {
        return version;
    }

    public int getVersionInt() {
        return versionInt;
    }

    public String getDate() {
        return date;
    }

    public List<String> getImprovements() {
        return improvements;
    }
}