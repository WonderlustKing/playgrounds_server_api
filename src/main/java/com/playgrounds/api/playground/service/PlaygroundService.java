package com.playgrounds.api.playground.service;

import com.playgrounds.api.playground.model.GeneralRate;
import com.playgrounds.api.playground.model.Playground;
import com.playgrounds.api.playground.model.Rate;
import com.playgrounds.api.playground.model.Report;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by chris on 29/9/2016.
 */
public interface PlaygroundService {
    Playground create(Playground playground);
    Playground addRate(String playground_id, Rate rate);
    Playground updateRate(String playground_id, Rate rate);
    Playground getPlayground(String playground_id, String user_id);
    List<GeneralRate> getPlaygroundsByCity(String city);
    Playground getPlaygroundByCityAndByName(String city, String name);
    List<GeneralRate> nearByPlaygrounds(double latitude, double longitude, int maxDistance, String sortBy);
    Playground reportPlayground(String playground_id, Report report);
    boolean addImageToPlayground(String playground_id, String user_id, MultipartFile image);
    byte[] getImage(String image_id);
}
