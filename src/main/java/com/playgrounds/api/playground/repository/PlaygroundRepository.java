package com.playgrounds.api.playground.repository;

import com.playgrounds.api.playground.model.Playground;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by christos on 16/5/2016.
 */
public interface PlaygroundRepository extends MongoRepository<Playground,String>, PlaygroundOperations {
    @Cacheable("playgroundsCache")
    Playground findById(String id);
    List<Playground> findByCity(String city);
    Playground findByCityIgnoreCaseAndNameIgnoreCase(String city, String name);
    @CachePut(value = "playgroundsCache", key = "#result.id")
    Playground save(Playground playground);


}
