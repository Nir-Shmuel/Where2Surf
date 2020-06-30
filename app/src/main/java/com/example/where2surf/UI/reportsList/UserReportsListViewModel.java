package com.example.where2surf.UI.reportsList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.where2surf.model.Report;
import com.example.where2surf.model.ReportModel;

import java.util.List;

public class UserReportsListViewModel extends ViewModel {
    private LiveData<List<Report>> liveData;

    public LiveData<List<Report>> getLiveData(String userId) {
        if (liveData == null)
            liveData = ReportModel.instance.getUserReports(userId);
        return liveData;
    }

    public void refresh(String userId, ReportModel.CompleteListener listener) {
        ReportModel.instance.refreshUserReportsList(userId, listener);
    }
}
