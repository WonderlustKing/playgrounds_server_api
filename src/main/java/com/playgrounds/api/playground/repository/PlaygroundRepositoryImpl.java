package com.playgrounds.api.playground.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.playgrounds.api.playground.model.*;
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

/**
 * Created by christos on 16/5/2016.
 */
public class PlaygroundRepositoryImpl implements PlaygroundOperations {

    @Autowired
    private MongoOperations mongo;

    @Autowired
    private GridFsOperations gridOperations;

    @Override
    public Playground addRate(Playground playground, Rate rate) {
        Criteria where = where("id").is(playground.getId());
        Query query = Query.query(where);
        Update update = new Update();
        update.push("rate",rate);
        update.set("rates_num",playground.getRate().size());
        mongo.updateFirst(query, update, Playground.class);


        Aggregation agg = newAggregation(
                match(where("id").is(playground.getId())),
                unwind("rate"),
                group("id")
                        .first("name").as("name")
                        .first("popularity").as("popularity")
                        .first("date_added").as("date_added")
                        .sum("rate.general_rate").as("rate")
                        .count().as("num_rates")
                        .sum("$rate.environment").as("environment")
                        .sum("$rate.equipment").as("equipment")
                        .sum("$rate.prices").as("prices")
                        .sum("$rate.kids_supervision").as("kids_supervision"),
                score()
        );

                AggregationResults<Playground> groupResults = mongo.aggregate(agg,Playground.class, Playground.class);
                Playground playground1 = groupResults.getUniqueMappedResult();

        Update update1 = new Update();
        update1.set("popularity",playground1.getPopularity());
        update1.set("general_rate", playground1.getGeneral_rate());
        update1.set("general_environment",playground1.getGeneral_environment());
        update1.set("general_equipment", playground1.getGeneral_equipment());
        update1.set("general_prices", playground1.getGeneral_prices());
        update1.set("general_kids_supervision", playground1.getGeneral_kids_supervision());
        mongo.updateFirst(query,update1,Playground.class);

        return playground;
    }

    @Override
    public Playground updateRate(Playground playground, Rate rate) {
        Criteria where = where("id").is(playground.getId()).and("rate.user_id").is(rate.getUser());
        Query query = Query.query(where);
        Update update = new Update();
        update.set("rate.$.general_rate",rate.getGeneral_rate());
        update.set("rate.$.comment",rate.getComment());
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
                        .count().as("num_rates")
                        .sum("$rate.environment").as("environment")
                        .sum("$rate.equipment").as("equipment")
                        .sum("$rate.prices").as("prices")
                        .sum("$rate.kids_supervision").as("kids_supervision"),
                score()
        );

        AggregationResults<Playground> groupResults = mongo.aggregate(agg,Playground.class, Playground.class);
        Playground playground1 = groupResults.getUniqueMappedResult();

        Update update1 = new Update();
        update1.set("popularity",playground1.getPopularity());
        update1.set("general_rate", playground1.getGeneral_rate());
        update1.set("general_environment",playground1.getGeneral_environment());
        update1.set("general_equipment", playground1.getGeneral_equipment());
        update1.set("general_prices", playground1.getGeneral_prices());
        update1.set("general_kids_supervision", playground1.getGeneral_kids_supervision());
        mongo.updateFirst(query,update1,Playground.class);

        return playground;

    }


    @Override
    public List<GeneralRate> findByCityOrderByRate(String city) {

        Aggregation agg = newAggregation(
                match(where("city").is(city)),
                group("id")
                        .first("name").as("name")
                        .first("popularity").as("popularity")
                        //.first("location.coordinates").as("coordinates")
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
                        //.first("location.coordinates").as("coordinates")
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
        //Criteria where = where("location").nearSphere(new GeoJsonPoint(longitude, latitude)).maxDistance(maxDistance);
        NearQuery query = NearQuery.near(longitude,latitude).maxDistance(distance).spherical(true);
        query.num(25);

        //return mongo.find(query,Playground.class);
        Aggregation agg;

        /*
        Aggregation agg1 = newAggregation(geoNear(query,"distance"));
        AggregationResults<LocationDistance> groupDistanceResults = mongo.aggregate(agg1, Playground.class , LocationDistance.class);
        List<LocationDistance> distanceResults = groupDistanceResults.getMappedResults();
        */

        if(sort.equals("popularity")) {
            agg = newAggregation(
                    geoNear(query, "distance"),
                    group("id")
                            .addToSet("distance").as("distance")
                            .first("name").as("name")
                            .first("general_rate").as("rate")
                            //.first("location.coordinates").as("coordinates")
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
                            //.first("location.coordinates").as("coordinates")
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
                        //.first("location.coordinates").as("coordinates")

        );

        AggregationResults<GeneralRate> groupResults = mongo.aggregate(agg, Playground.class, GeneralRate.class);
        GeneralRate result = groupResults.getUniqueMappedResult();

        return result;
    }

    @Override
    public String uploadImage(String playground_id, String user_id, String fileName, MultipartFile file) {

        //ApplicationContext ctx =
         //       new AnnotationConfigApplicationContext(MongoConfig.class);
       // GridFsOperations gridOperations =
         //       (GridFsOperations) ctx.getBean("gridFsTemplate");
        String result = null;
        try {
            byte[] bytes = file.getBytes();
            InputStream bis = new ByteArrayInputStream(bytes);
            //InputStream bis = new FileInputStream("src/main/resources/TestFile.txt");
            //BufferedOutputStream stream =
              //      new BufferedOutputStream(new FileOutputStream(new File("name")));
            //stream.write(bytes);
            //stream.close();

            //GridFS gfsPhoto = new GridFS()
            //GridFSInputFile gfsFile = gfsPhoto.createFile(bytes);
            //gfsFile.setFilename("FileName");
            //gfsFile.save();
            DBObject metaData = new BasicDBObject();
            metaData.put("playground",playground_id);
            metaData.put("user", "Chris");
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
                "general_environment",new BasicDBObject(
                        "$divide",new Object[]{
                        "$environment","$num_rates"
                }
                )
        ).append(
                "general_equipment",new BasicDBObject(
                        "$divide",new Object[]{
                        "$equipment","$num_rates"
                }
                )
        ).append(
                "general_prices",new BasicDBObject(
                        "$divide",new Object[]{
                        "$prices","$num_rates"
                }
                )
        ).append(
                "general_kids_supervision",new BasicDBObject(
                        "$divide",new Object[]{
                        "$kids_supervision","$num_rates"
                }
                )
        ).append(
                "general_rate",new BasicDBObject(
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

    private CustomGroupOperation unrate(){
        DBObject myProject = (DBObject)new BasicDBObject(
                "$project", new BasicDBObject(
                "name","$name").append(
                "rate", new BasicDBObject(
                "$ifNull", new Object[]{
                "$rate", new Object[]{
                        "general_rate", 0
                        ,"environment", 0
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

    private GeoNearOperationExt projectNEAR(){
        NearQuery near = null;
        return new GeoNearOperationExt(near);
    }

    private class GeoNearOperationExt implements AggregationOperation {

        private NearQuery nearQuery;

        private String distanceField = "distance";

        public GeoNearOperationExt(NearQuery nearQuery) {
            this.nearQuery = nearQuery;
        }

        /**
         * Default is distance
         * @return
         */
        public String getDistanceField() {
            return distanceField;
        }

        /**
         * Set distanceField value Default is distance
         * @param distanceField
         */
        public void setDistanceField(String distanceField) {
            this.distanceField = distanceField;
        }



        @Override
        public DBObject toDBObject(AggregationOperationContext context) {
            DBObject dbObject = context.getMappedObject(nearQuery.toDBObject());
            dbObject.put("distanceField", distanceField);
            return new BasicDBObject("$geoNear", dbObject);
        }
    }



}


