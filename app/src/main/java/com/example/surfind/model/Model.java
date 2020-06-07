package com.example.surfind.model;

import java.util.LinkedList;
import java.util.List;

public class Model {
    private List<Coast> coasts;
    public static final Model instance = new Model();

    private Model() {
        coasts = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            boolean p = i % 2 == 0;
            Coast coast = new Coast("coast " + i, "location " + i, p);
            coast.addReport(new Report("reporter "+i,1,"",1,1,0,false));
            coasts.add(coast);
        }
    }

    public List<Coast> getCoasts() {
        return coasts;
    }
}
