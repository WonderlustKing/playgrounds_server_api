package com.playgrounds.api.playground.model;

import com.playgrounds.api.user.validator.UserExist;

import java.util.Date;

/**
 * Created by christos on 15/5/2016.
 */


public class Rate {

    @UserExist(message = "User not exist")
    private String user;

    private Date date;

    private double general_rate;

    private double environment;

    private double equipment;

    private double prices;

    private double kids_supervision;

    private String comment;



    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public double getGeneral_rate() {
        return general_rate;
    }

    public void setGeneral_rate(double general_rate) {
        this.general_rate = general_rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getEnvironment() {
        return environment;
    }

    public void setEnvironment(double environment) {
        this.environment = environment;
    }

    public double getEquipment() {
        return equipment;
    }

    public void setEquipment(double equipment) {
        this.equipment = equipment;
    }

    public double getPrices() {
        return prices;
    }

    public void setPrices(double prices) {
        this.prices = prices;
    }

    public double getKids_supervision() {
        return kids_supervision;
    }

    public void setKids_supervision(double kids_supervision) {
        this.kids_supervision = kids_supervision;
    }
}
