package com.playgrounds.api.playground.service;

import com.playgrounds.api.playground.model.*;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by chris on 29/9/2016.
 */
public interface PlaygroundService {
    HttpHeaders addPlayground(Playground playground);
    HttpHeaders addRate(String playground_id, Rate rate);
    void updateRate(String playground_id, Rate rate);
    ResponseEntity<Resource<Playground>> getPlayground(String playground_id, String user_id);
    ResponseEntity<List<PlaygroundToMap>> getAllPlaygroundsToMap();
    ResponseEntity<List<GeneralRate>> getPlaygroundsByCity(Double latitude, Double longitude);
    ResponseEntity<Resource<Playground>> getPlaygroundByLocationAndByName(Double latitude, Double longitude, String playgroundName);
    ResponseEntity<List<GeneralRate>> nearByPlaygrounds(double latitude, double longitude, int maxDistance, String sortBy);
    HttpHeaders reportPlayground(String playground_id, Report report);
    boolean addImageToPlayground(String playground_id, String user_id, MultipartFile image) throws MalformedURLException;
    byte[] getImage(String image_id);
    GeneralRate getGeneralRate(String playground_id);
}
