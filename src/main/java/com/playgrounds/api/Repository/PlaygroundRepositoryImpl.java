package com.playgrounds.api.Repository;

import com.mongodb.WriteResult;
import com.playgrounds.api.Domain.GeneralRate;
import com.playgrounds.api.Domain.Playground;
import com.playgrounds.api.Domain.Rate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

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
    public List<GeneralRate> findByCityOrderByRate(String city) {
        Aggregation agg = newAggregation(
                match(Criteria.where("city").is(city)),
                unwind("rate"),
                group("name").avg("rate.general_rate").as("rate"),
                project("rate").and("name").previousOperation(),
                sort(Sort.Direction.DESC, "rate")

        );

        AggregationResults<GeneralRate> groupResults = mongo.aggregate(agg, Playground.class , GeneralRate.class);
        List<GeneralRate> result = groupResults.getMappedResults();

        return result;
    }
}
