package com.playgrounds.api.playground.model;

/**
 * Created by christos on 11/6/2016.
 */
public class Location {
    private final String type = "Point";
    private double coordinates[] = new double[2];

    public String getType() {
        return type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
}
