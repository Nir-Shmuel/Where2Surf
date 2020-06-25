package com.example.where2surf;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.where2surf.model.Report;
import com.example.where2surf.model.ReportModel;
import com.example.where2surf.model.Spot;

import java.util.List;

public class SpotReportsListViewModel extends ViewModel {
    LiveData<List<Report>> liveData;

    public LiveData<List<Report>> getLiveData(Spot spot){
        if(liveData == null)
            liveData= ReportModel.instance.getAllReports(spot);
        return liveData;
    }

    public void refresh(Spot spot,ReportModel.CompleteListener listener){
        ReportModel.instance.refreshSpotReportsList(spot,listener);
    }
}
