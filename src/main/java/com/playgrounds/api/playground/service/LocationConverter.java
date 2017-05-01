package com.playgrounds.api.playground.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LocationConverter {

    private GoogleRestClient googleRestClient;

    @Autowired
    public LocationConverter(GoogleRestClient googleRestClient) {
        this.googleRestClient = googleRestClient;
    }

    public String getCityNameFromCoordinates(Double latitude, Double longitude) {
        String[] latlon = {Double.toString(latitude), Double.toString(longitude)};
        String locationToEnglish = googleRestClient.findLocation(latlon[0] + "," + latlon[1]);
        return locationToEnglish;
    }
}
