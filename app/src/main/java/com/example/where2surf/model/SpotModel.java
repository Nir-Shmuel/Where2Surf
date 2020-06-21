package com.example.where2surf.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.where2surf.MyApplication;

import java.util.List;

public class SpotModel {
    public static final SpotModel instance = new SpotModel();

//        private SpotModel(){
//        MyApplication.context.deleteDatabase("Where2SurfDb.db");
//        for (int i = 0; i < 10; i++) {
//            boolean p = i % 2 == 0;
//            Spot spot = new Spot("spot " + i, "location " + i, p);
//            addSpot(spot,null);
//        }
//    }
    public interface Listener<T> {
        void onComplete(T data);
    }

    public interface CompleteListener {
        void onComplete();
    }

    public LiveData<List<Spot>> getAllSpots() {
        LiveData<List<Spot>> liveData = AppLocalDb.db.spotDao().getAll();
        refreshSpotsList(null);
        return liveData;
    }

    public void refreshSpotsList(final CompleteListener listener) {
        long lastUpdated = MyApplication.context.getSharedPreferences("lastUpdated", Context.MODE_PRIVATE)
                .getLong("SpotsLastUpdateDate", 0);
        SpotFirebase.getAllSpotsSince(lastUpdated, new Listener<List<Spot>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onComplete(final List<Spot> data) {
                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        long lastUpdated = 0;
                        for (Spot spot : data) {
                            AppLocalDb.db.spotDao().insertAll(spot);
                            if (spot.getLastUpdated() > lastUpdated)
                                lastUpdated = spot.getLastUpdated();
                        }
                        SharedPreferences.Editor editor = MyApplication.context.getSharedPreferences("lastUpdated", Context.MODE_PRIVATE).edit();
                        editor.putLong("SpotsLastUpdateDate", lastUpdated).apply();
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (listener != null) listener.onComplete();
                    }
                }.execute();
            }
        });
    }

    public void addSpot(Spot spot, Listener<Boolean> listener) {
        SpotFirebase.addSpot(spot, listener);
    }
}
