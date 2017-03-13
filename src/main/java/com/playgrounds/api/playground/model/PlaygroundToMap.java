package com.playgrounds.api.playground.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by chris on 9/1/2017.
 */
public class PlaygroundToMap extends ResourceSupport {


    private String id;
    private String name;
    private double[] coordinates;

    public PlaygroundToMap(String id, String name, double[] coordinates) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
    }

    @JsonIgnore
    public String getPlaygroundId() {
        return id;
    }

    public void setPlaygroundId(String id) {
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
