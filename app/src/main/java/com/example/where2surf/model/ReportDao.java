package com.example.where2surf.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReportDao {
    @Query("SELECT * FROM Reports")
    List<Report> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Report... reports);

    @Query("SELECT * FROM Reports WHERE spotName LIKE :name")
    LiveData<List<Report>> getSpotReports(String name);

    @Query("SELECT * FROM Reports WHERE reporterId LIKE :userId")
    LiveData<List<Report>> getUserReports(String userId);

    @Delete
    void delete(Report report);
}
