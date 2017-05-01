package com.playgrounds.api.playground.service;

import com.playgrounds.api.playground.model.Response.Distance;
import com.playgrounds.api.playground.model.Response.DistanceMatrixResult;
import com.playgrounds.api.playground.model.Response.AddressComponent;
import com.playgrounds.api.playground.model.Response.RequestLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@Configuration
@ComponentScan
public class GoogleRestClient {
    private final Logger logger = LoggerFactory.getLogger(GoogleRestClient.class.getName());

    private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    private static final String ENDPOINT2 = "https://maps.googleapis.com/maps/api/geocode/json?";

    private RestTemplate restTemplate;

    @Autowired
    public GoogleRestClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public Distance findDistance(double originX, double originY, double x1, double y1){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ENDPOINT)
                .queryParam("origins",originX+","+originY)
                .queryParam("destinations",x1+","+y1)
                .queryParam("key", "AIzaSyDxNwOneT8pnWYYgf6YL50wN1M6GgQG-SM");
        //logger.info(builder.toUriString());

        DistanceMatrixResult distanceMatrixResult = restTemplate.getForObject(builder.toUriString(), DistanceMatrixResult.class);
        Distance distance = distanceMatrixResult.getRows().get(0).getElements().get(0).getDistance();
        //logger.info(distance.getText());
        return distance;
    }


    public String findLocation(String latlng) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ENDPOINT2)
                .queryParam("sensor", "true")
                .queryParam("language", "en")
                .queryParam("latlng", latlng)
                .queryParam("key","AIzaSyA8qu3YsDcHO7Q1KuRA0G7qvRs6q260M8g");

        logger.info(builder.toUriString());

        RequestLocation requestLocation = restTemplate.getForObject(builder.toUriString(), RequestLocation.class);
        logger.info(requestLocation.getResults().get(0).getAddress_components().size() + "");
        String failOverLocation = "";
        List<AddressComponent> addressComponents = requestLocation.getResults().get(0).getAddress_components();
        for(AddressComponent addressComponent : addressComponents){
            List<String> types = addressComponent.getTypes();
            for(String type: types){
                if(type.equals("locality")){
                    logger.info(addressComponent.getLong_name().toLowerCase());
                    return addressComponent.getLong_name().toLowerCase();
                }
                else if (type.equals("political") && failOverLocation.equals("")) {
                    failOverLocation = addressComponent.getLong_name().toLowerCase();
                    logger.info("failoverLocationName", failOverLocation);
                }
            }
        }
        return failOverLocation;
    }
}
