package com.example.where2surf.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.where2surf.MyApplication;

@Database(entities = {Spot.class, Report.class, User.class}, version = 1)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract SpotDao spotDao();

    public abstract ReportDao reportDao();

    public abstract UserDao userDao();
}

public class AppLocalDb {
    static public AppLocalDbRepository db = Room.databaseBuilder(MyApplication.context,
            AppLocalDbRepository.class, "Where2SurfDb.db")
            .fallbackToDestructiveMigration().build();
}
