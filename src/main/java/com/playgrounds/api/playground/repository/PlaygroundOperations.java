package com.playgrounds.api.playground.repository;

import com.mongodb.WriteResult;
import com.playgrounds.api.playground.model.GeneralRate;
import com.playgrounds.api.playground.model.Playground;
import com.playgrounds.api.playground.model.Rate;
import com.playgrounds.api.playground.model.Report;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by christos on 16/5/2016.
 */
public interface PlaygroundOperations {
    @CachePut(value = "playgroundsCache", key = "#result.id")
    Playground addRate(Playground playground, Rate rate);
    @CachePut(value = "playgroundsCache", key = "#result.id")
    Playground updateRate(Playground playground, Rate rate);
    @Cacheable(value = "playgroundsCache")
    List<GeneralRate> findByCityOrderByRate(String city);
    @Cacheable("playgroundsCache")
    List<GeneralRate> nearMePlaygrounds(double longitude, double latitude, double maxDistance, String sort);
    @CachePut(value = "playgroundsCache", key = "#result.id")
    Playground addReport(Report report, Playground playground);
    Rate findRate(String playground_id, String user_id);
    GeneralRate getPlaygroundGeneral(String playground_id);
    String uploadImage(String playground_id,String user_id,String fileName, MultipartFile file);
    WriteResult updateImageField(Playground playground, URL imageURL) throws MalformedURLException;
    WriteResult addImageProfile(Playground playground, URL imageURL) throws MalformedURLException;
    InputStream findImageById(String image_id);
}
