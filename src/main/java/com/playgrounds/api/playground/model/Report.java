package com.playgrounds.api.playground.model;

import com.playgrounds.api.user.validator.UserExist;

public class Report {

    @UserExist
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
