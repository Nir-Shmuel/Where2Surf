package com.example.where2surf.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.where2surf.MyApplication;

import java.util.List;

public class ReportModel {

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

    public LiveData<List<Report>> getUserReports(String userId) {
        LiveData<List<Report>> liveData = AppLocalDb.db.reportDao().getUserReports(userId);
        refreshUserReportsList(userId, null);
        return liveData;
    }

    public void refreshUserReportsList(String userId, final CompleteListener listener) {
        long lastUpdated = MyApplication.context.getSharedPreferences("lastUpdated", Context.MODE_PRIVATE)
                .getLong("ReportsLastUpdateDate", 0);
        ReportFirebase.getAllUserReportsSince(userId, lastUpdated, new Listener<List<Report>>() {
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

    public LiveData<Report> getReport(String reportId) {
        LiveData<Report> reportLiveData = AppLocalDb.db.reportDao().getReport(reportId);
        refreshReportDetails(reportId);
        return reportLiveData;
    }

    @SuppressLint("StaticFieldLeak")
    public void refreshReportDetails(String reportId) {
        ReportFirebase.getReportById(reportId, new Listener<Report>() {
            @Override
            public void onComplete(final Report data) {
                if (data != null) {
                    new AsyncTask<String, String, String>() {
                        @Override
                        protected String doInBackground(String... strings) {
                            AppLocalDb.db.reportDao().insertAll(data);
                            return null;
                        }
                    }.execute("");
                }
            }
        });
    }

    public void addReport(final Report report, final Listener<Report> listener) {
        ReportFirebase.addReport(report, listener);
    }

    @SuppressLint("StaticFieldLeak")
    public void updateReport(final Report report, final Listener<Boolean> listener) {
        ReportFirebase.updateReport(report, new Listener<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                if (data) {
                    new AsyncTask<String, String, String>() {
                        @Override
                        protected String doInBackground(String... strings) {
                            AppLocalDb.db.reportDao().insertAll(report);
                            return null;
                        }
                    }.execute("");
                }
                listener.onComplete(data);
            }
        });
    }

    public void setReportImageUrl(String reportId, String
            imageUrl, Listener<Boolean> listener) {
        ReportFirebase.setReportImageUrl(reportId, imageUrl, listener);
    }

    @SuppressLint("StaticFieldLeak")
    public void updateReportRating(final Report report, float newRating,
                                   final Listener<Boolean> listener) {
        final int n = report.getNumReliabilityCritics();
        float oldRating = report.getReliabilityRating();
        final float avgRating = oldRating * n / (n + 1) + newRating / (n + 1);
        ReportFirebase.updateReportRating(report.getId(), avgRating, n + 1, new Listener<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                if (data) {
                    report.setReliabilityRating(avgRating);
                    report.setNumReliabilityCritics(n + 1);
                    new AsyncTask<String, String, String>() {
                        @Override
                        protected String doInBackground(String... strings) {
                            AppLocalDb.db.reportDao().insertAll(report);
                            return null;
                        }
                    }.execute();
                }
                listener.onComplete(data);
            }
        });
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


}
