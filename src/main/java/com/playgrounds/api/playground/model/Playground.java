package com.playgrounds.api.playground.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by christos on 15/5/2016.
 */
@Document
public class Playground {

    private static final DecimalFormat df = new DecimalFormat(".#");

    @Id
    private String id;

    @NotNull
    @NotEmpty
    private String name;

    private String city;

    @NotNull
    @NotEmpty
    private String address;

    @Indexed
    private Location location;

    private long phone;

    private URL website;

    private double popularity =0;

    private Date date_added = Calendar.getInstance().getTime();

    @NotNull
    @NotEmpty
    private String added_by;

    private URL imageURL = new URL("http://83.212.122.113:8080/playgrounds/images/58148adecce2c09ffda9d1c8");

    private List<URL> images = new ArrayList<>();

    @Transient
    private String base64Image;

    private int rates_num =0;

    private double general_rate =0;

    private double general_environment =0;

    private double general_equipment =0;

    private double general_prices =0;

    private double general_kids_supervision =0;

    @Indexed
    private LinkedList<Rate> rate = new LinkedList<>();

    private List<Report> reports = new ArrayList<>();

    @JsonIgnore
    private String distance;

    public Playground() throws MalformedURLException {
    }

    public Playground(String name, String address, Location location, String added_by, String base64Image) throws MalformedURLException {
        this.name = name;
        this.address = address;
        this.location = location;
        this.added_by = added_by;
        this.base64Image = base64Image;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public URL getWebsite() {
        return website;
    }

    public void setWebsite(URL website) {
        this.website = website;
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

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public double getPopularity() {
        return Double.valueOf(df.format(popularity));
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
        return Double.valueOf(df.format(general_rate));
    }

    public void setGeneral_rate(double general_rate) {
        this.general_rate = general_rate;
    }

    public double getGeneral_environment() {
        return Double.valueOf(df.format(general_environment));
    }

    public void setGeneral_environment(double general_environment) {
        this.general_environment = general_environment;
    }

    public double getGeneral_equipment() {
        return Double.valueOf(df.format(general_equipment));
    }

    public void setGeneral_equipment(double general_equipment) {
        this.general_equipment = general_equipment;
    }

    public double getGeneral_prices() {
        return Double.valueOf(df.format(general_prices));
    }

    public void setGeneral_prices(double general_prices) {
        this.general_prices = general_prices;
    }

    public double getGeneral_kids_supervision() {
        return Double.valueOf(df.format(general_kids_supervision));
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
