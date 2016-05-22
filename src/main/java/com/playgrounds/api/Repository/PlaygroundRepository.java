package com.playgrounds.api.Repository;

import com.playgrounds.api.Domain.Playground;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by christos on 16/5/2016.
 */
public interface PlaygroundRepository extends MongoRepository<Playground,String>, PlaygroundOperations {
    List<Playground> findByCity(String city);
    Playground findByCityAndName(String city, String name);

}
