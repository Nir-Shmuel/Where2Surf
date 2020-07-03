package com.example.where2surf.UI.reports.addReport;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.where2surf.model.Spot;
import com.example.where2surf.model.SpotModel;

public class AddReportViewModel extends ViewModel {
    LiveData<Spot> spotLiveData;

    public LiveData<Spot> getSpotLiveData(String spotName) {
        if(spotLiveData==null)
            spotLiveData= SpotModel.instance.getSpot(spotName);
        return spotLiveData;
    }
}
