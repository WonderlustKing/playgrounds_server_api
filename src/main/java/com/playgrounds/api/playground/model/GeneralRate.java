package com.playgrounds.api.playground.model;

import java.net.URL;

/**
 * Created by christos on 28/5/2016.
 */
public class GeneralRate {

    private String id;
    private String name;
    private double rate =0;
    private URL image;
    private double distance;

    public GeneralRate(){
    }

    public GeneralRate(String id, String name, double rate, URL image){
        this.id = id;
        this.name = name;
        this.rate = rate;
        this.image = image;
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
}
