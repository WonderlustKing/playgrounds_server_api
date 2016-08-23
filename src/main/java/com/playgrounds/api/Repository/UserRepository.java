package com.playgrounds.api.Repository;

import com.playgrounds.api.Domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by christos on 17/5/2016.
 */

public interface UserRepository extends MongoRepository<User,String>, UserOperations {
    User findById(String id);
    User findByUsername(String username);
}
