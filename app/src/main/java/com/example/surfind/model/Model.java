package com.example.surfind.model;

import java.util.LinkedList;
import java.util.List;

public class Model {
    private List<Spot> spots;
    public static final Model instance = new Model();

    private Model() {
        spots = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            boolean p = i % 2 == 0;
            Spot spot = new Spot("coast " + i, "location " + i, p);
            for (int j = 0; j < 10; j++) {
                Report r = new Report("reporter " + i + j, spot.getName(), 1, "", 1, 1, 0, i % 2 == 0);
                r.setReliabilityRating(j % 5);
                spot.addReport(r);
            }
            spots.add(spot);
        }
    }

    public List<Spot> getSpots() {
        return spots;
    }
}
