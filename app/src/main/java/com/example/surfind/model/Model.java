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
            spot.addReport(new Report("reporter "+i,1,"",1,1,0,false));
            spots.add(spot);
        }
    }

    public List<Spot> getSpots() {
        return spots;
    }
}
