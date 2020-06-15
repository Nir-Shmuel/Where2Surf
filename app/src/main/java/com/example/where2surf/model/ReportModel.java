package com.example.where2surf.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.where2surf.MyApplication;

import java.util.List;

public class ReportModel {
    List<Report> reports;
    public static final ReportModel instance = new ReportModel();

    public interface Listener<T> {
        void onComplete(T data);
    }

    public interface CompleteListener {
        void onComplete();
    }

    public LiveData<List<Report>> getAllReports(Spot spot) {
        LiveData<List<Report>> liveData = AppLocalDb.db.reportDao().getSpotReports(spot.getName());
        refreshSpotReportsList(spot, null);
        return liveData;
    }

    public void refreshSpotReportsList(final Spot spot, final CompleteListener listener) {
        long lastUpdated = MyApplication.context.getSharedPreferences("lastUpdated", Context.MODE_PRIVATE)
                .getLong("ReportsLastUpdateDate", 0);
        ReportFirebase.getAllReports(spot, lastUpdated, new Listener<List<Report>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onComplete(final List<Report> data) {
                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        long lastUpdated = 0;
                        AppLocalDb.db.reportDao().insertAll(data.toArray(new Report[data.size()]));
                        for (Report report : data) {
                            if (report.getLastUpdated() > lastUpdated)
                                lastUpdated = report.getLastUpdated();
                        }
                        SharedPreferences.Editor editor = MyApplication.context.getSharedPreferences("lastUpdated", Context.MODE_PRIVATE).edit();
                        editor.putLong("ReportsLastUpdateDate", lastUpdated).apply();
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

    public void addReport(Report report, Listener<Boolean> listener) {
        ReportFirebase.addReport(report, listener);
    }

    public void getAllReports(User user, final ReportModel.Listener<Report> listener) {

    }
}
