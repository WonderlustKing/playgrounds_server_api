package com.playgrounds.api.playground.model;

import org.springframework.hateoas.ResourceSupport;


public class PlaygroundToMap extends ResourceSupport {


    private String playground_id;
    private String name;
    private double[] coordinates;

    public PlaygroundToMap(String playground_id, String name, double[] coordinates) {
        this.playground_id = playground_id;
        this.name = name;
        this.coordinates = coordinates;
    }

    public String getPlaygroundId() {
        return playground_id;
    }

    public void setPlaygroundId(String id) {
        this.playground_id = id;
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
