package com.playgrounds.api.playground.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.playgrounds.api.playground.model.*;
import org.apache.batik.ext.awt.image.spi.ImageWriterParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.geo.Distance;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;


public class PlaygroundRepositoryImpl implements PlaygroundOperations {

    @Autowired
    private MongoOperations mongo;

    @Autowired
    private GridFsOperations gridOperations;

    @Override
    public Playground updatePlayground(Playground playground) {
        Criteria where = where("id").is(playground.getId());
        Query query = Query.query(where);
        Update update = new Update();
        update.set("name", playground.getName());
        update.set("address", playground.getAddress());
        update.set("location", playground.getLocation());
        update.set("phone", playground.getPhone());
        if (playground.getWebsite() == null || playground.getWebsite().toString().equals("")) {
            update.unset("website");
        } else {
            update.set("website", playground.getWebsite());
        }
        mongo.updateFirst(query, update, Playground.class);
        return playground;
    }

    @Override
    public WriteResult updateOptionalFields(OptionalFields optionalFields) {
        Criteria where = where("id").is(optionalFields.getId());
        Query query = Query.query(where);
        Update update = new Update();
        if (optionalFields.getPhone() != 0) {
            update.set("phone", optionalFields.getPhone());
        }
        if (optionalFields.getWebsite() != null) {
            update.set("website", optionalFields.getWebsite());
        }
        return mongo.updateFirst(query, update, Playground.class);
    }

    @Override
    public Playground addRate(Playground playground, Rate rate) {
        Criteria where = where("id").is(playground.getId());
        Query query = Query.query(where);
        Update update = new Update();
        update.push("rate", rate);
        update.set("rates_num", playground.getRate().size() + 1);
        mongo.updateFirst(query, update, Playground.class);


        Aggregation agg = newAggregation(
                match(where("id").is(playground.getId())),
                unwind("rate"),
                group("id")
                        .first("name").as("name")
                        .first("popularity").as("popularity")
                        .first("date_added").as("date_added")
                        .sum("rate.general_rate").as("rate")
                        .count().as("rates_num")
                        .sum("$rate.environment").as("environment")
                        .sum("$rate.equipment").as("equipment")
                        .sum("$rate.prices").as("prices")
                        .sum("$rate.kids_supervision").as("kids_supervision"),
                score()
        );

        AggregationResults<Playground> groupResults = mongo.aggregate(agg, Playground.class, Playground.class);
        Playground playground1 = groupResults.getUniqueMappedResult();

        Update update1 = new Update();
        update1.set("popularity", playground1.getPopularity());
        update1.set("general_rate", playground1.getGeneral_rate());
        update1.set("general_environment", playground1.getGeneral_environment());
        update1.set("general_equipment", playground1.getGeneral_equipment());
        update1.set("general_prices", playground1.getGeneral_prices());
        update1.set("general_kids_supervision", playground1.getGeneral_kids_supervision());
        mongo.updateFirst(query, update1, Playground.class);

        return playground1;
    }

    @Override
    public Playground updateRate(Playground playground, Rate rate) {
        Criteria where = where("id").is(playground.getId()).and("rate.user").is(rate.getUser());
        Query query = Query.query(where);
        Update update = new Update();
        update.set("rate.$.general_rate", rate.getGeneral_rate());
        update.set("rate.$.comment", rate.getComment());
        update.set("rate.$.environment", rate.getEnvironment());
        update.set("rate.$.equipment", rate.getEquipment());
        update.set("rate.$.prices", rate.getPrices());
        update.set("rate.$.kids_supervision", rate.getKids_supervision());
        mongo.updateFirst(query, update, Playground.class);

        Aggregation agg = newAggregation(
                match(where("id").is(playground.getId())),
                unwind("rate"),
                group("id")
                        .first("name").as("name")
                        .first("popularity").as("popularity")
                        .first("date_added").as("date_added")
                        .sum("rate.general_rate").as("rate")
                        .count().as("rates_num")
                        .sum("$rate.environment").as("environment")
                        .sum("$rate.equipment").as("equipment")
                        .sum("$rate.prices").as("prices")
                        .sum("$rate.kids_supervision").as("kids_supervision"),
                score()
        );

        AggregationResults<Playground> groupResults = mongo.aggregate(agg, Playground.class, Playground.class);
        Playground playground1 = groupResults.getUniqueMappedResult();

        Update update1 = new Update();
        update1.set("popularity", playground1.getPopularity());
        update1.set("general_rate", playground1.getGeneral_rate());
        update1.set("general_environment", playground1.getGeneral_environment());
        update1.set("general_equipment", playground1.getGeneral_equipment());
        update1.set("general_prices", playground1.getGeneral_prices());
        update1.set("general_kids_supervision", playground1.getGeneral_kids_supervision());
        mongo.updateFirst(query, update1, Playground.class);

        return playground1;

    }


    @Override
    public List<GeneralRate> findByCityOrderByRate(String city) {

        Aggregation agg = newAggregation(
                match(where("city").is(city)),
                group("id")
                        .first("name").as("name")
                        .first("popularity").as("popularity")
                        .first("location.coordinates").as("coordinates")
                        //.first("date_added").as("date_added")
                        .first("general_rate").as("rate")
                        .first("imageURL").as("image"),
                sort(Sort.Direction.DESC, "popularity")

        );

        AggregationResults<GeneralRate> groupResults = mongo.aggregate(agg, Playground.class, GeneralRate.class);
        List<GeneralRate> result = groupResults.getMappedResults();

        return result;

    }

    @Override
    public List<GeneralRate> findByCityOrderByRateWithDistance(String city, Double latitude, Double longitude) {

        NearQuery query = NearQuery.near(latitude, longitude).in(Metrics.KILOMETERS).spherical(true);
        Aggregation agg = newAggregation(
                geoNear(query, "distance"),
                match(where("city").is(city)),
                group("id")
                        .addToSet("distance").as("distance")
                        .first("name").as("name")
                        .first("popularity").as("popularity")
                        .first("location.coordinates").as("coordinates")
                        //.first("date_added").as("date_added")
                        .first("general_rate").as("rate")
                        .first("imageURL").as("image"),
                sort(Sort.Direction.DESC, "popularity")

        );

        AggregationResults<GeneralRate> groupResults = mongo.aggregate(agg, Playground.class, GeneralRate.class);
        List<GeneralRate> result = groupResults.getMappedResults();

        return result;
    }

    @Override
    public List<GeneralRate> nearMePlaygrounds(double longitude, double latitude, double maxDistance, String sort) {
        Distance distance=new Distance(maxDistance,Metrics.KILOMETERS);
        NearQuery query = NearQuery.near(longitude,latitude).maxDistance(distance).spherical(true);

        query.num(25);

        Aggregation agg;

        if(sort.equals("popularity")) {
            agg = newAggregation(
                    geoNear(query, "distance"),
                    group("id")
                            .addToSet("distance").as("distance")
                            .first("name").as("name")
                            .first("general_rate").as("rate")
                            .first("location.coordinates").as("coordinates")
                            .first("popularity").as("popularity")
                            .first("imageURL").as("image"),
                    sort(Sort.Direction.ASC, "popularity")
            );
        }else{
            agg = newAggregation(
                    geoNear(query, "distance"),
                    group("id")
                            .addToSet("distance").as("distance")
                            .first("name").as("name")
                            .first("general_rate").as("rate")
                            .first("location.coordinates").as("coordinates")
                            .first("imageURL").as("image"),
                    sort(Sort.Direction.DESC, "distance")
            );
        }

        AggregationResults<GeneralRate> groupResults = mongo.aggregate(agg, Playground.class , GeneralRate.class);
        List<GeneralRate> result = groupResults.getMappedResults();

        return result;


    }

    @Override
    public List<PlaygroundToMap> findAllPlaygroundsToMap() {
        Aggregation aggregation = newAggregation(
                group("id")
                        .first("id").as("playground_id")
                        .first("name").as("name")
                        .first("location.coordinates").as("coordinates")
        );

        AggregationResults<PlaygroundToMap> results = mongo.aggregate(aggregation, Playground.class, PlaygroundToMap.class);
        List<PlaygroundToMap> playgroundToMapsList = results.getMappedResults();
        return playgroundToMapsList;
    }

    @Override
    public Playground addReport(Report report, Playground playground) {
        Criteria where = where("id").is(playground.getId());
        Query query = Query.query(where);
        Update update = new Update();
        update.push("reports",report);
        mongo.updateFirst(query,update,Playground.class);
        return playground;
    }

    @Override
    public Rate findRate(String playground_id, String user_id) {

        Criteria findPlaygroundCriteria = Criteria.where("id").is(playground_id);

        Criteria findRateCriteria = Criteria.where("rate").elemMatch(Criteria.where("comment").is("Good"));

        BasicQuery query = new BasicQuery(findPlaygroundCriteria.getCriteriaObject(), findRateCriteria.getCriteriaObject());

        return mongo.findOne(query,Rate.class);
    }

    @Override
    public GeneralRate getPlaygroundGeneral(String playground_id) {
        Aggregation agg = newAggregation(
                match(where("id").is(playground_id)),
                group("id")
                        .first("name").as("name")
                        .first("popularity").as("popularity")
                        .first("general_rate").as("rate")
                        .first("location.coordinates").as("coordinates")

        );

        AggregationResults<GeneralRate> groupResults = mongo.aggregate(agg, Playground.class, GeneralRate.class);
        GeneralRate result = groupResults.getUniqueMappedResult();

        return result;
    }

    @Override
    public GeneralRate getFavoriteGeneral(String playground_id) {
        Aggregation agg = newAggregation(
                match(where("id").is(playground_id)),
                group("id")
                        .first("id").as("playground_id")
                        .first("name").as("name")
                        .first("general_rate").as("rate")
                        .first("imageURL").as("image")
                        .first("location.coordinates").as("coordinates")

        );
        AggregationResults<GeneralRate> groupResults = mongo.aggregate(agg, Playground.class, GeneralRate.class);
        return groupResults.getUniqueMappedResult();
    }

    @Override
    public String uploadImage(String playground_id, String user_id, String fileName, byte[] bytes) {

        String result = null;
        try {
            InputStream bis = new ByteArrayInputStream(bytes);
            DBObject metaData = new BasicDBObject();
            metaData.put("playground",playground_id);
            metaData.put("user", user_id);
            GridFSFile upload_image = gridOperations.store(bis,fileName,"image/jpeg");
            result = upload_image.getId().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
        return result;
    }

    @Override
    public WriteResult updateImageField(Playground playground, URL imageURL){
        Criteria where = where("id").is(playground.getId());
        Query query = Query.query(where);
        Update update = new Update();
        update.push("images",imageURL);
        WriteResult result = mongo.updateFirst(query,update,Playground.class);
        return result;
    }

    @Override
    public WriteResult addImageProfile(Playground playground, URL imageURL) throws MalformedURLException {
        Criteria where = where("id").is(playground.getId());
        Query query = Query.query(where);
        Update update = new Update();
        update.set("imageURL",imageURL);
        WriteResult result = mongo.updateFirst(query,update,Playground.class);
        return result;
    }


    @Override
    public InputStream findImageById(String image_id) {
        //ApplicationContext ctx =
          //      new AnnotationConfigApplicationContext(MongoConfig.class);
        //GridFsOperations gridOperations =
        //        (GridFsOperations) ctx.getBean("gridFsTemplate");

        GridFSDBFile result = gridOperations.findOne(new Query().addCriteria(Criteria.where("_id").is(image_id)));
        if(result == null ) return null;
        return result.getInputStream();

    }

    private CustomGroupOperation score(){
        DBObject myProject = (DBObject)new BasicDBObject(
                "$project", new BasicDBObject(
                "id","$_id"
        ).append(
                "name","$name"
        ).append(
                "general_environment",new BasicDBObject(
                        "$divide",new Object[]{
                        "$environment","$rates_num"
                }
                )
        ).append(
                "general_equipment",new BasicDBObject(
                        "$divide",new Object[]{
                        "$equipment","$rates_num"
                }
                )
        ).append(
                "general_prices",new BasicDBObject(
                        "$divide",new Object[]{
                        "$prices","$rates_num"
                }
                )
        ).append(
                "general_kids_supervision",new BasicDBObject(
                        "$divide",new Object[]{
                        "$kids_supervision","$rates_num"
                }
                )
        ).append(
                "general_rate",new BasicDBObject(
                        "$divide",new Object[]{
                        "$rate","$rates_num"
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
                                "$rates_num",0.95
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
                                new java.util.Date(),"$date_added"
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

}


