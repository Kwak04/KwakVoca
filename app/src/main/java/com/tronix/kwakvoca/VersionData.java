package com.tronix.kwakvoca;

import java.util.List;

class VersionData {
    String version;
    int versionInt;
    String date;
    List<String> improvements;
    List<String> improvements_en;

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

    public List<String> getImprovements_en() {
        return improvements_en;
    }
}