package com.example.surfind.model;

import java.io.Serializable;
import java.util.Date;

public class Report implements Serializable {
    private String reporterName;
    private String spotName;

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    private double date;
    private String image;
    private int wavesHeight;
    private int windSpeed;
    private int numOfSurfers;
    private boolean isContaminated;
    private int reliabilityRating;

    public Report(String reporterName, String spotName, double date, String image, int wavesHeight, int windSpeed, int numOfSurfers, boolean isContaminated) {
        this.reporterName = reporterName;
        this.spotName = spotName;
        this.date = date;
        this.image = image;
        this.wavesHeight = wavesHeight;
        this.windSpeed = windSpeed;
        this.numOfSurfers = numOfSurfers;
        this.isContaminated = isContaminated;
    }

    public Report() {
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public double getDate() {
        return date;
    }

    public void setDate(double date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
