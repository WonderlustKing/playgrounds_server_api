package com.playgrounds.api.user.service;

import com.playgrounds.api.user.model.User;
import com.playgrounds.api.user.repository.UserRepository;
import com.playgrounds.api.user.service.UserService;
import com.playgrounds.api.user.web.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public void userExist(String user_id) {
        User user =repository.findById(user_id);
        if (user == null) throw new UserNotFoundException(user_id);
    }
}
