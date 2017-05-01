package com.playgrounds.api.playground.repository;

import com.mongodb.WriteResult;
import com.playgrounds.api.playground.model.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public interface PlaygroundOperations {
    //@CacheEvict(value = "playgroundsCache", key = "#result.id")
    Playground updatePlayground(Playground playground);

    //@CacheEvict(value = "playgroundsCache")
    WriteResult updateOptionalFields(OptionalFields optionalFields);

    //@CacheEvict(value = "playgroundsCache", key = "#result.id")
    Playground addRate(Playground playground, Rate rate);

    //@CacheEvict(value = "playgroundsCache", key = "#result.id")
    Playground updateRate(Playground playground, Rate rate);

    //@Cacheable(value = "playgroundsCache")
    List<GeneralRate> findByCityOrderByRate(String city);

    //@Cacheable(value = "playgroundsCache")
    List<GeneralRate> findByCityOrderByRateWithDistance(String city, Double latitude, Double longitude);

    //@Cacheable("playgroundsCache")
    List<GeneralRate> nearMePlaygrounds(double longitude, double latitude, double maxDistance, String sort);

    List<PlaygroundToMap> findAllPlaygroundsToMap();
    //@CachePut(value = "playgroundsCache", key = "#result.id")
    Playground addReport(Report report, Playground playground);

    Rate findRate(String playground_id, String user_id);
    GeneralRate getPlaygroundGeneral(String playground_id);
    GeneralRate getFavoriteGeneral(String playground_id);
    String uploadImage(String playground_id,String user_id,String fileName, byte[] bytes);
    WriteResult updateImageField(Playground playground, URL imageURL) throws MalformedURLException;
    WriteResult addImageProfile(Playground playground, URL imageURL) throws MalformedURLException;
    InputStream findImageById(String image_id);
}
