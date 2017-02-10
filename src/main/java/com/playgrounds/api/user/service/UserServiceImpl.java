package com.playgrounds.api.user.service;

import com.playgrounds.api.user.model.Favorite;
import com.playgrounds.api.user.model.User;
import com.playgrounds.api.user.repository.UserRepository;
import com.playgrounds.api.user.service.UserService;
import com.playgrounds.api.user.web.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by chris on 30/9/2016.
 */
@Component
public class UserServiceImpl implements UserService {
    private UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository){
        this.repository = repository;
    }

    @Override
    public User addUserIfNotExist(User userToAdd) {
        User alreadyExist = repository.findByEmail(userToAdd.getEmail());
        if(alreadyExist != null) {
            return alreadyExist;
        }
        return repository.save(userToAdd);
    }

    @Override
    public User getUser(String user_id) {
        User user =repository.findById(user_id);
        if (user == null) {
            throw new UserNotFoundException(user_id);
        }
        return user;
    }

    @Override
    public void userExist(String user_id) {
        User user =repository.findById(user_id);
        if (user == null) {
            throw new UserNotFoundException(user_id);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public void addFavorite(String user_id, Favorite favorite) {
        userExist(user_id);
        repository.addFavorite(user_id,favorite);
    }
}
