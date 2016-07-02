package com.playgrounds.api.Repository;

import com.mongodb.WriteResult;
import com.playgrounds.api.Domain.GeneralRate;
import com.playgrounds.api.Domain.Playground;
import com.playgrounds.api.Domain.Rate;

import java.awt.*;
import java.util.List;

/**
 * Created by christos on 16/5/2016.
 */
public interface PlaygroundOperations {
    public WriteResult addRate(Playground playground, Rate rate);
    public WriteResult updateRate(Playground playground, Rate rate);
    public List<GeneralRate> findByCityOrderByRate(String city);
    public List<GeneralRate> findUnRatePlaygrounds(String city);
    public List<GeneralRate> nearMePlaygrounds(double longitude, double latitude, double maxDistance);
}
