package com.example.where2surf.UI.reportDetails;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.where2surf.model.Report;
import com.example.where2surf.model.ReportModel;

public class ReportDetailsViewModel extends ViewModel {
    private LiveData<Report> liveData;

    public LiveData<Report> getLiveData(String reportId){
        if(liveData==null)
            liveData = ReportModel.instance.getReport(reportId);
        return liveData;
    }
}
