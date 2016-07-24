package com.playgrounds.api.Domain;

/**
 * Created by christos on 28/5/2016.
 */
public class GeneralRate {

    private String id;
    private String name;
    private double rate =0;
    private double environment =0;
    private double equipment =0;
    private double prices =0;
    private double kids_supervision =0;

    public GeneralRate(){
    }

    public GeneralRate(String id,String name, double rate, double environment, double equipment, double prices,double kids_supervision){
        this.id = id;
        this.name = name;
        this.rate = rate;
        this.environment = environment;
        this.equipment = equipment;
        this.prices = prices;
        this.kids_supervision = kids_supervision;
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
