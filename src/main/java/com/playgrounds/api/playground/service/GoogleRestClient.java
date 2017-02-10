package com.playgrounds.api.playground.service;

import com.playgrounds.api.playground.model.Distance;
import com.playgrounds.api.playground.model.DistanceMatrixResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by chris on 17/12/2016.
 */
@Configuration
public class GoogleRestClient {
    private final Logger logger = LoggerFactory.getLogger(GoogleRestClient.class.getName());

    private static final String ENDPOINT = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    private RestTemplate restTemplate;

    public GoogleRestClient(RestTemplateBuilder builder){
        restTemplate = builder.build();
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
}
