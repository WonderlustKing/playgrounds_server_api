package com.playgrounds.api.playground.model;

import org.springframework.hateoas.ResourceSupport;

import java.net.URL;

public class FavoriteGeneralRate extends ResourceSupport {

    private String playground_id;
    private String name;
    private double rate =0;
    private URL image;
    private double[] coordinates;

    public String getPlayground_id() {
        return playground_id;
    }

    public void setPlayground_id(String id) {
        this.playground_id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public URL getImage() {
        return image;
    }

    public void setImage(URL image) {
        this.image = image;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
}
