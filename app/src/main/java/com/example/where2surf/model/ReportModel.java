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
        ReportFirebase.getAllReportsSince(spot, lastUpdated, new Listener<List<Report>>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onComplete(final List<Report> data) {
                new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... strings) {
                        long lastUpdated = 0;
                        if (data != null) {
                            for (Report report : data) {
                                AppLocalDb.db.reportDao().insertAll(report);
                                if (report.getLastUpdated() > lastUpdated)
                                    lastUpdated = report.getLastUpdated();
                            }
                            SharedPreferences.Editor editor = MyApplication.context.getSharedPreferences("lastUpdated", Context.MODE_PRIVATE).edit();
                            editor.putLong("ReportsLastUpdateDate", lastUpdated).commit();
                        }
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

    public void addReport(final Report report, final Listener<Report> listener) {
        ReportFirebase.addReport(report, listener);
    }

    public void updateReport(Report report, Listener<Boolean> listener) {
        ReportFirebase.updateReport(report, listener);
    }

    public void setReportImageUrl(String reportId, String imageUrl, Listener<Boolean> listener) {
        ReportFirebase.setReportImageUrl(reportId, imageUrl, listener);
    }

    @SuppressLint("StaticFieldLeak")

    public void deleteReport(final Report report, Listener<Boolean> listener) {
        ReportFirebase.deleteReport(report, listener);
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {
                AppLocalDb.db.reportDao().delete(report);
                return null;
            }
        }.execute();

    }

    public void getAllReports(User user, final ReportModel.Listener<Report> listener) {

    }
}
