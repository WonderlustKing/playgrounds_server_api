package com.playgrounds.api.playground.model;


public class Location {

    private final String type = "Point";
    private double coordinates[] = new double[2];

    public Location(){}

    public Location(double[] coordinates) {
        this.coordinates = coordinates;
    }

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
