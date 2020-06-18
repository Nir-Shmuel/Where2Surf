package com.example.where2surf.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Reports")
public class Report implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String reporterName;
    private String spotName;
    private long date;
    private String imageUrl;
    private int wavesHeight;
    private int windSpeed;
    private int numOfSurfers;
    private boolean isContaminated;
    private int reliabilityRating;
    private long lastUpdated;

    public Report(String reporterName, String spotName, long date, String imageUrl, int wavesHeight, int windSpeed, int numOfSurfers, boolean isContaminated) {
        this.reporterName = reporterName;
        this.spotName = spotName;
        this.date = date;
        this.imageUrl = imageUrl;
        this.wavesHeight = wavesHeight;
        this.windSpeed = windSpeed;
        this.numOfSurfers = numOfSurfers;
        this.isContaminated = isContaminated;
    }

    public Report() {
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }


    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getWavesHeight() {
        return wavesHeight;
    }

    public void setWavesHeight(int wavesHeight) {
        this.wavesHeight = wavesHeight;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getNumOfSurfers() {
        return numOfSurfers;
    }

    public void setNumOfSurfers(int numOfSurfers) {
        this.numOfSurfers = numOfSurfers;
    }

    public boolean isContaminated() {
        return isContaminated;
    }

    public void setContaminated(boolean contaminated) {
        isContaminated = contaminated;
    }

    public int getReliabilityRating() {
        return reliabilityRating;
    }

    public void setReliabilityRating(int reliabilityRating) {
        if (reliabilityRating < 0)
            this.reliabilityRating = 0;
        this.reliabilityRating = Math.min(5, reliabilityRating);
    }

}
