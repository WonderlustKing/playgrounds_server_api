package com.playgrounds.api.user.repository;

import com.playgrounds.api.user.model.User;
import com.playgrounds.api.user.repository.UserOperations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


public interface UserRepository extends MongoRepository<User,String>, UserOperations {
    User findById(String id);
    User findByUsername(String username);
    User findByEmail(String email);
}
