package com.example.where2surf;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.where2surf.model.Spot;
import com.example.where2surf.model.SpotModel;

import java.util.List;

public class SpotsListViewModel extends ViewModel {
    private LiveData<List<Spot>> liveData;

    public LiveData<List<Spot>> getLiveData() {
        if (liveData == null)
            liveData = SpotModel.instance.getAllSpots();
        return liveData;
    }
}