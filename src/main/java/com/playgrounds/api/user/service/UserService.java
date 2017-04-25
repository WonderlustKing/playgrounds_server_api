package com.playgrounds.api.user.service;

import com.playgrounds.api.playground.model.FavoriteGeneralRate;
import com.playgrounds.api.playground.model.GeneralRate;
import com.playgrounds.api.user.model.Favorite;
import com.playgrounds.api.user.model.User;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface UserService {
    HttpHeaders addUserIfNotExist(User userToAdd);
    User findUser(String userId);
    ResponseEntity<Resource<User>> getUser(String userId);
    List<User> getAllUsers();
    HttpHeaders addFavorite(String userId, Favorite favorite);
    void removeFavorite(String userId, Favorite favorite);
    ResponseEntity<List<FavoriteGeneralRate>> getUserFavorites(String userId);
}
