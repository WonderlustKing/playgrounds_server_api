package com.playgrounds.api.Repository;

import com.playgrounds.api.Domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by christos on 17/5/2016.
 */
public interface UserRepository extends MongoRepository<User,String> {
    User findByUsername(String username);

}
