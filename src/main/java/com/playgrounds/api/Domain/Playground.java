package com.playgrounds.api.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by christos on 15/5/2016.
 */
@Document
public class Playground {

    @Id
    private String id;

    private String name;

    private String city;

    @Indexed
    private Location location;

    private int rates_num;

    private List<Rate> rate = new ArrayList<Rate>();

    private URL imageURL;

    private double popularity =0;

    private Date date_added;


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

    public List<Rate> getRate() {
        return rate;
    }

    public void setRate(List<Rate> rate) {
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
}
