package com.example.where2surf.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SpotDao {
    @Query("SELECT * FROM Spots")
    LiveData<List<Spot>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Spot... spots);

    @Query("SELECT * FROM Spots WHERE name LIKE :spotName")
    LiveData<Spot> getSpot(String spotName);
}
