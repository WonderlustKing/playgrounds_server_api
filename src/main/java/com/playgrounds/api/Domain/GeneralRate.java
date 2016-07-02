package com.playgrounds.api.Domain;

/**
 * Created by christos on 28/5/2016.
 */
public class GeneralRate {

    private String id;
    private String name;
    private double rate;

    public GeneralRate(){
        rate = 0;
    }

    public GeneralRate(String id,String name, double rate){
        this.id = id;
        this.name = name;
        this.rate = rate;
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


}