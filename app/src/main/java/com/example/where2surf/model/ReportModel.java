package com.example.where2surf.model;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ReportModel {
    List<Report> reports;
    public static final ReportModel instance = new ReportModel();

    public interface Listener<T> {
        void onComplete(T data);
    }

    public LiveData<List<Report>> getAllReports(Spot spot) {
        LiveData<List<Report>> liveData = AppLocalDb.db.reportDao().getSpotReports(spot.getName());
        refreshSpotReportsList(spot);
        return liveData;
    }

    public void refreshSpotReportsList(final Spot spot) {
        ReportFirebase.getAllReports(spot, new Listener<List<Report>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onComplete(final List<Report> data) {
                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        AppLocalDb.db.reportDao().insertAll(data.toArray(new Report[data.size()]));
                        return "";
                    }
                }.execute();

            }
        });
    }

    public void getAllReports(User user, final ReportModel.Listener<Report> listener) {

    }
}
