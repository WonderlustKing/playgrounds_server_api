package com.playgrounds.api.playground.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by christos on 15/5/2016.
 */
@Document
public class Playground {

    @Id
    private String id;

    @NotNull
    private String name;

    @NotNull
    private String city;

    @Indexed
    private Location location;

    private double popularity =0;

    private Date date_added;

    @NotNull
    private String added_by;

    private URL imageURL = new URL("http://localhost:8080/playgrounds/images/default.jpg");

    private List<URL> images = new ArrayList<URL>();

    private int rates_num =0;

    private double general_rate =0;

    private double general_environment =0;

    private double general_equipment =0;

    private double general_prices =0;

    private double general_kids_supervision =0;

    @Indexed
    private LinkedList<Rate> rate = new LinkedList<Rate>();

    private List<Report> reports = new ArrayList<Report>();

    public Playground() throws MalformedURLException {
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LinkedList<Rate> getRate() {
        return rate;
    }

    public void setRate(LinkedList<Rate> rate) {
        this.rate = rate;
    }

    public URL getImageURL() {
        return imageURL;
    }

    public void setImageURL(URL imageURL) {
        this.imageURL = imageURL;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public Date getDate_added() {
        return date_added;
    }

    public void setDate_added(Date date_added) {
        this.date_added = date_added;
    }

    public int getRates_num() {
        return rates_num;
    }

    public void setRates_num(int rates_num) {
        this.rates_num = rates_num;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public double getGeneral_rate() {
        return general_rate;
    }

    public void setGeneral_rate(double general_rate) {
        this.general_rate = general_rate;
    }

    public double getGeneral_environment() {
        return general_environment;
    }

    public void setGeneral_environment(double general_environment) {
        this.general_environment = general_environment;
    }

    public double getGeneral_equipment() {
        return general_equipment;
    }

    public void setGeneral_equipment(double general_equipment) {
        this.general_equipment = general_equipment;
    }

    public double getGeneral_prices() {
        return general_prices;
    }

    public void setGeneral_prices(double general_prices) {
        this.general_prices = general_prices;
    }

    public double getGeneral_kids_supervision() {
        return general_kids_supervision;
    }

    public void setGeneral_kids_supervision(double general_kids_supervision) {
        this.general_kids_supervision = general_kids_supervision;
    }

    public List<URL> getImages() {
        return images;
    }

    public void setImages(List<URL> images) {
        this.images = images;
    }
}
