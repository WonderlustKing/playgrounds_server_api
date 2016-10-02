package com.playgrounds.api.playground.model;

/**
 * Created by christos on 5/8/2016.
 */
public class Report {

    private String user_id;
    private String reason;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
