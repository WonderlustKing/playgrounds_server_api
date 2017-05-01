package com.playgrounds.api.playground.service;

import com.playgrounds.api.playground.model.*;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;


public interface PlaygroundService {
    HttpHeaders addPlayground(Playground playground);
    HttpHeaders updatePlayground(Playground playground);
    void updatePlaygroundOptionalFields(OptionalFields optionalFields);
    RateFields addRate(String playground_id, Rate rate);
    RateFields updateRate(String playground_id, Rate rate);
    ResponseEntity<Resource<Playground>> getPlayground(String playground_id, String user_id);
    ResponseEntity<List<PlaygroundToMap>> getAllPlaygroundsToMap();
    ResponseEntity<List<GeneralRate>> getPlaygroundsByCity(Double latitude, Double longitude, boolean distance);
    ResponseEntity<Resource<List<Playground>>> getPlaygroundByLocationAndByName(Double latitude, Double longitude, String playgroundName);
    ResponseEntity<List<GeneralRate>> nearByPlaygrounds(double latitude, double longitude, int maxDistance, String sortBy);
    HttpHeaders reportPlayground(String playground_id, Report report);
    HttpHeaders addImageToPlayground(String playground_id, List<Image> images) throws IOException;
    byte[] getImage(String image_id);
    GeneralRate getGeneralRate(String playground_id);
}
