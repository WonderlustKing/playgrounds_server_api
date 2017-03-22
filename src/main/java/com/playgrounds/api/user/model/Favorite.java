package com.playgrounds.api.user.model;

import com.playgrounds.api.user.validator.UserExist;

/**
 * Created by christos on 22/8/2016.
 */
public class Favorite {

    @UserExist
    private String playground;

    public String getPlayground() {
        return playground;
    }

    public void setPlayground(String playground) {
        this.playground = playground;
    }
}
