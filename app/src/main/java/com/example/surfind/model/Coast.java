package com.example.surfind.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Coast implements Serializable {
    private String name;
    private String location;
    private boolean isProtected;
    private List<Report> reports;

    public Coast() {
    }

    public Coast(String name, String location, boolean isProtected) {
        this.name = name;
        this.location = location;
        this.isProtected = isProtected;
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

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public void addReport(Report report) {
        if (report != null)
            reports.add(report);
    }
}
