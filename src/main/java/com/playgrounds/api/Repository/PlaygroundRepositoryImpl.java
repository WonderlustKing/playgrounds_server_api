package com.playgrounds.api.Repository;

import com.mongodb.WriteResult;
import com.playgrounds.api.Domain.GeneralRate;
import com.playgrounds.api.Domain.Playground;
import com.playgrounds.api.Domain.Rate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.awt.*;
import java.awt.Point;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by christos on 16/5/2016.
 */
public class PlaygroundRepositoryImpl implements PlaygroundOperations {

    @Autowired
    private MongoOperations mongo;

    @Override
    public WriteResult addRate(Playground playground, Rate rate) {
        Criteria where = Criteria.where("name").is(playground.getName()).and("city").is(playground.getCity());
        Query query = Query.query(where);
        Update update = new Update();
        update.push("rate",rate);
        return mongo.updateFirst(query, update, Playground.class);
    }

    @Override
    public WriteResult updateRate(Playground playground, Rate rate) {
        Criteria where = Criteria.where("id").is(playground.getId()).and("rate.user.username").is(rate.getUser().getUsername());
        Query query = Query.query(where);
        Update update = new Update();
        update.set("rate.$.general_rate",rate.getGeneral_rate());
        update.set("rate.$.comment",rate.getComment());
        return mongo.updateFirst(query, update, Playground.class);
    }


    @Override
    public List<GeneralRate> findByCityOrderByRate(String city) {
        Aggregation agg = newAggregation(
                match(Criteria.where("city").is(city)),
                unwind("rate"),
                group("id").first("name").as("name").avg("rate.general_rate").as("rate"),
                project("rate","name").and("_id").previousOperation(),
                sort(Sort.Direction.DESC, "rate")

        );

        AggregationResults<GeneralRate> groupResults = mongo.aggregate(agg, Playground.class , GeneralRate.class);
        List<GeneralRate> result = groupResults.getMappedResults();

        return result;
    }

    @Override
    public List<GeneralRate> findUnRatePlaygrounds(String city) {
        Aggregation agg = newAggregation(
                match(Criteria.where("city").is(city).and("rate").size(0)),
                project("name").and("rate")

        );
        AggregationResults<GeneralRate> groupResults = mongo.aggregate(agg, Playground.class , GeneralRate.class);
        List<GeneralRate> result = groupResults.getMappedResults();

        return result;
    }

    @Override
    public List<GeneralRate> nearMePlaygrounds(double longitude, double latitude, double maxDistance) {
        Distance distance=new Distance(maxDistance,Metrics.KILOMETERS);
        Criteria where = Criteria.where("location").nearSphere(new GeoJsonPoint(longitude, latitude)).maxDistance(maxDistance);
        NearQuery query = NearQuery.near(longitude,latitude).maxDistance(distance).spherical(true);
        //return mongo.find(query,Playground.class);

        Aggregation agg = newAggregation(
                geoNear(query,"distance"),
                unwind("rate"),
                group("id").first("name").as("name").avg("rate.general_rate").as("rate"),
                project("rate","name").and("_id").previousOperation(),
                sort(Sort.Direction.DESC, "rate")
        );

        AggregationResults<GeneralRate> groupResults = mongo.aggregate(agg, Playground.class , GeneralRate.class);
        List<GeneralRate> result = groupResults.getMappedResults();

        return result;


    }

}
