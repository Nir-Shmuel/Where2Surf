package com.example.where2surf.model;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SpotModel {
    public static final SpotModel instance = new SpotModel();

    public interface Listener<T> {
        void onComplete(T data);
    }

    public LiveData<List<Spot>> getAllSpots() {
        LiveData<List<Spot>> liveData = AppLocalDb.db.spotDao().getAll();
        refreshSpotsList();
        return liveData;
    }

    public void refreshSpotsList() {
        SpotFirebase.getAllSpots(new Listener<List<Spot>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onComplete(final List<Spot> data) {
                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        AppLocalDb.db.spotDao().insertAll(data.toArray(new Spot[data.size()]));
                        return "";
                    }
                }.execute();
            }
        });
    }
}
