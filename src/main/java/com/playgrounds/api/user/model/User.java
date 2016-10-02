package com.playgrounds.api.user.model;

import com.playgrounds.api.user.model.Favorite;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by christos on 15/5/2016.
 */
@Document
public class User {

    @Id
    private String id;

    private String username;

    private String email;

    private URL imageUrl;

    private List<Favorite> favorites = new ArrayList<Favorite>();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }
}
