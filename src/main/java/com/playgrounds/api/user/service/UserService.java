package com.playgrounds.api.user.service;

import com.playgrounds.api.user.model.Favorite;
import com.playgrounds.api.user.model.User;

import java.util.List;

/**
 * Created by chris on 30/9/2016.
 */
public interface UserService {
    User addUser(User userToAdd);
    User userExist(String user_id);
    List<User> getAllUsers();
    void addFavorite(String user_id, Favorite favorite);
}
