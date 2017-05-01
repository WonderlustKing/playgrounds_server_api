package com.playgrounds.api.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.client.RestTemplate;


@Configuration
@EnableMongoRepositories(value = {"com.playgrounds.api.playground.repository", "com.playgrounds.api.user.repository"})
public class MongoConfig extends AbstractMongoConfiguration{

    @Override
    protected String getDatabaseName() {
        return "playgrounds_db";
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient();
    }

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }

}
