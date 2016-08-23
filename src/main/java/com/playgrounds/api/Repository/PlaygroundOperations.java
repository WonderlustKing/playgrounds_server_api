package com.playgrounds.api.Repository;

import com.mongodb.WriteResult;
import com.playgrounds.api.Domain.GeneralRate;
import com.playgrounds.api.Domain.Playground;
import com.playgrounds.api.Domain.Rate;
import com.playgrounds.api.Domain.Report;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.annotation.Secured;

import java.awt.*;
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
}
