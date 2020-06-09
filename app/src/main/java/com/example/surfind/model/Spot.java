package com.example.surfind.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Spot implements Serializable {
    private String name;
    private String location;
    private boolean isWindProtected;
    private List<Report> reports;

    public Spot() {
    }

    public Spot(String name, String location, boolean isWindProtected) {
        this.name = name;
        this.location = location;
        this.isWindProtected = isWindProtected;
        this.reports = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isWindProtected() {
        return isWindProtected;
    }

    public void setWindProtected(boolean windProtected) {
        isWindProtected = windProtected;
    }
    public List<Report> getReports(){
        return this.reports;
    }
    public void addReport(Report report) {
        if (report != null)
            reports.add(report);
    }
}
