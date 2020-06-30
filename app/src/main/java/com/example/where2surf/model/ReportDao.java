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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Report... reports);

    @Query("SELECT * FROM Reports WHERE spotName LIKE :name")
    LiveData<List<Report>> getSpotReports(String name);

    @Query("SELECT * FROM Reports WHERE reporterId LIKE :userId")
    LiveData<List<Report>> getUserReports(String userId);

    @Query("SELECT * FROM Reports WHERE id LIKE :reportId")
    LiveData<Report> getReport(String reportId);

    @Delete
    void delete(Report report);
}
