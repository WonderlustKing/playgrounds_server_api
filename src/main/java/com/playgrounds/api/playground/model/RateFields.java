package com.playgrounds.api.playground.model;


public class RateFields {

    private double general_rate;

    private double environment;

    private double equipment;

    private double prices;

    private double kids_supervision;

    public RateFields(double general_rate, double environment, double equipment, double prices, double kids_supervision) {
        this.general_rate = general_rate;
        this.environment = environment;
        this.equipment = equipment;
        this.prices = prices;
        this.kids_supervision = kids_supervision;
    }

    public double getGeneral_rate() {
        return general_rate;
    }

    public void setGeneral_rate(double general_rate) {
        this.general_rate = general_rate;
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
