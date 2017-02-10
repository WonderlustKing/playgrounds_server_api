package com.playgrounds.api.playground.model;

/**
 * Created by chris on 9/1/2017.
 */
public class PlaygroundToMap {

    private String id;
    private String name;
    private double[] coordinates;

    public PlaygroundToMap(String id, String name, double[] coordinates) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

}
