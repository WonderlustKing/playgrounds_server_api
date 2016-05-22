package com.playgrounds.api.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by christos on 15/5/2016.
 */

public class Rate {

    private User user;

    private Date date;

    private double general_rate;

    private String comment;



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
}
