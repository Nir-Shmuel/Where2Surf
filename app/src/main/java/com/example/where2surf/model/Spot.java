package com.example.where2surf.model;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Spots")
public class Spot implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String location;
    private boolean isWindProtected;
    long lastUpdated;

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Spot() {
    }

    public Spot(String name, String location, boolean isWindProtected) {
        this.name = name;
        this.location = location;
        this.isWindProtected = isWindProtected;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isWindProtected() {
        return isWindProtected;
    }

    public void setWindProtected(boolean windProtected) {
        isWindProtected = windProtected;
    }

    @SuppressLint("StaticFieldLeak")
    public void addReport(final Report report) {
        if (report != null)
            new AsyncTask<String,String,String>(){

                @Override
                protected String doInBackground(String... strings) {
                    AppLocalDb.db.reportDao().insertAll(report);
                    return null;
                }
            }.execute();

    }
}
