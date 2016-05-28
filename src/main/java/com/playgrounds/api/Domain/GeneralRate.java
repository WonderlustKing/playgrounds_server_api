package com.playgrounds.api.Domain;

/**
 * Created by christos on 28/5/2016.
 */
public class GeneralRate {

    private String name;
    private double rate;

    public GeneralRate(){}

    public GeneralRate(String name, double rate){
        this.name = name;
        this.rate = rate;
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


}
