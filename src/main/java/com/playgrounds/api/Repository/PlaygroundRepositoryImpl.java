package com.playgrounds.api.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.playgrounds.api.CustomGroupOperation;
import com.playgrounds.api.Domain.GeneralRate;
import com.playgrounds.api.Domain.Playground;
import com.playgrounds.api.Domain.Rate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.*;

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
                unrate(),
                unwind("rate"),
                group("id").first("name").as("name").first("popularity").as("popularity").first("date_added").as("date_added").sum("rate.general_rate").as("rate").count().as("num_rates")
                        .avg("$rate.environment").as("environment")
                        .avg("$rate.equipment").as("equipment")
                        .avg("$rate.prices").as("prices")
                        .avg("$rate.kids_supervision").as("kids_supervision"),
                score(),
                //project("rate","name").and("_id").previousOperation().andExpression("rate * num_rates").as("popularity"),
                sort(Sort.Direction.DESC, "popularity")

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
    public List<GeneralRate> nearMePlaygrounds(double longitude, double latitude, double maxDistance, String sort) {
        Distance distance=new Distance(maxDistance,Metrics.KILOMETERS);
        Criteria where = Criteria.where("location").nearSphere(new GeoJsonPoint(longitude, latitude)).maxDistance(maxDistance);
        NearQuery query = NearQuery.near(longitude,latitude).maxDistance(distance).spherical(true);
        //return mongo.find(query,Playground.class);
        Aggregation agg;
        if(sort.equals("popularity")) {
            agg = newAggregation(
                    geoNear(query, "distance"),
                    unrate(),
                    unwind("rate"),
                    group("id").first("name").as("name").sum("$rate.general_rate").as("rate").count().as("num_rates"),
                    //project("rate", "name").and("_id").previousOperation().andExpression("rate / num_rates").as("rate")
                    score(),
                    sort(Sort.Direction.ASC, "popularity")
            );
        }else{
            agg = newAggregation(
                    geoNear(query, "distance"),
                    unrate(),
                    unwind("rate"),
                    group("id").first("name").as("name").sum("$rate.general_rate").as("rate").count().as("num_rates"),
                    project("rate", "name").and("_id").previousOperation().andExpression("rate / num_rates").as("rate")
            );
        }

        AggregationResults<GeneralRate> groupResults = mongo.aggregate(agg, Playground.class , GeneralRate.class);
        List<GeneralRate> result = groupResults.getMappedResults();


        return result;


    }

    private CustomGroupOperation popularity(){
        DBObject myProject = (DBObject)new BasicDBObject(
                "$project", new BasicDBObject(
                "id","$_id"
        ).append("name", "$name").append("rate","$rate").append(
                "suntelestis", new BasicDBObject(
                        "$cond",new Object[]{
                        new BasicDBObject(
                                "$gte", new Object[]{
                                "$rate",4
                        }),
                        new BasicDBObject("$divide",new Object[]{
                                "$num_rates",2
                        }), // if true
                        new BasicDBObject(
                                "$cond",new Object[]{
                                new BasicDBObject(
                                        "$gte", new Object[]{
                                        "$rate",3
                                }),
                                new BasicDBObject("$divide",new Object[]{
                                        "$num_rates",1.5
                                }),
                                new BasicDBObject(
                                        "$cond",new Object[]{
                                        new BasicDBObject(
                                                "$gte", new Object[]{
                                                "$rate",2
                                        }),
                                        new BasicDBObject("$divide",new Object[]{
                                                "$num_rates", 1
                                        }),2
                                })
                        }

                        ) // if false
                }
                )
        ).append("logarithmos",new BasicDBObject("$ln", new Object[]{
                "$suntelestis"
        })).append("popularity", new BasicDBObject("$multiply", new Object[]{
                "$logarithmos","$rate"
        })));

        return new CustomGroupOperation(myProject);
    }

    private CustomGroupOperation score(){
        DBObject myProject = (DBObject)new BasicDBObject(
                "$project", new BasicDBObject(
                "id","$_id"
        ).append(
                "name","$name"
        ).append(
                "environment","$environment"
        ).append(
                "equipment","$equipment"
        ).append(
                "prices","$prices"
        ).append(
                "kids_supervision","$kids_supervision"
        ).append(
                "rate",new BasicDBObject(
                        "$divide",new Object[]{
                        "$rate","$num_rates"
                }
                )
        ).append("popularity", new BasicDBObject(
                "$divide",new Object[]{
                new BasicDBObject(
                        "$multiply",new Object[]{
                        "$rate",new BasicDBObject(
                        "$ln", new Object[]{
                        new BasicDBObject(
                                "$divide",new Object[]{
                                "$num_rates",0.95
                        }
                        )
                }
                )
                }
                ),new BasicDBObject(
                "$pow",new Object[]{
                new BasicDBObject(
                        "$divide",new Object[]{
                        new BasicDBObject(
                                "$subtract",new Object[]{
                                "new Date()","$date_added"
                        }
                        ),86400000
                }
                ),0.05
        }
        )
        }
        )));

        return new CustomGroupOperation(myProject);
    }

    private CustomGroupOperation unrate(){
        DBObject myProject = (DBObject)new BasicDBObject(
                "$project", new BasicDBObject(
                "name","$name").append(
                "rate", new BasicDBObject(
                "$ifNull", new Object[]{
                "$rate", new Object[]{
                        "general_rate", 0
                }
        })
        ));

        return new CustomGroupOperation(myProject);
    }

    private CustomGroupOperation projectNear(){
        DBObject myProject = (DBObject)new BasicDBObject(
                "$project", new BasicDBObject(
                "name","$name")
                .append("id","$_id")
                .append("rate",new BasicDBObject(
                "$avg",new Object[]{
                        "rate.general_rate"
                }

        ))
        );
        return new CustomGroupOperation(myProject);
    }

}
