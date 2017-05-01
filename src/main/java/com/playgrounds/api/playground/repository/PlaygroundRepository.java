package com.playgrounds.api.playground.repository;

import com.playgrounds.api.playground.model.Playground;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface PlaygroundRepository extends MongoRepository<Playground,String>, PlaygroundOperations {

    //@Cacheable("playgroundsCache")
    Playground findById(String id);

    List<Playground> findByCity(String city);

    Playground findByCityIgnoreCaseAndNameIgnoreCase(String city, String name);

    //@CacheEvict(value = "playgroundsCache", key = "#result.id")
    Playground save(Playground playground);
}
