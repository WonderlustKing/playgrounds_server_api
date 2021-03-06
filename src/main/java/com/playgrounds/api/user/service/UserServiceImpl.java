package com.playgrounds.api.user.service;

import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.playgrounds.api.playground.model.FavoriteGeneralRate;
import com.playgrounds.api.playground.model.GeneralRate;
import com.playgrounds.api.playground.model.Playground;
import com.playgrounds.api.playground.repository.PlaygroundRepository;
import com.playgrounds.api.playground.web.PlaygroundController;
import com.playgrounds.api.playground.web.PlaygroundNotFoundException;
import com.playgrounds.api.user.model.Favorite;
import com.playgrounds.api.user.model.User;
import com.playgrounds.api.user.repository.UserRepository;
import com.playgrounds.api.user.web.UserController;
import com.playgrounds.api.user.web.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;


@Component
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PlaygroundRepository playgroundRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PlaygroundRepository playgroundRepository){
        this.userRepository = userRepository;
        this.playgroundRepository = playgroundRepository;
    }

    @Override
    public HttpHeaders addUserIfNotExist(User userToAdd) {
        User user = userRepository.findByEmail(userToAdd.getEmail());
        if(user != null) {
            return addHeadersToUser(user);
        }
        user = userRepository.save(userToAdd);
        return addHeadersToUser(user);
    }

    @Override
    public ResponseEntity<Resource<User>> getUser(String user_id) {
        User user = userRepository.findById(user_id);
        if (user == null) {
            throw new UserNotFoundException(user_id);
        }
        Resource<User> userResource = new Resource<>(user);
        userResource.add(linkTo(UserController.class).slash(user.getId()).withSelfRel());
        ResponseEntity<Resource<User>> responseEntity = new ResponseEntity<>(userResource, HttpStatus.OK);
        return responseEntity;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public HttpHeaders addFavorite(String userId, Favorite favorite) {
        playgroundExists(favorite.getPlayground());
        User user = findUser(userId);
        WriteResult writeResult = userRepository.addFavorite(userId,favorite);
        if (writeResult.getN() == 1) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(linkTo(UserController.class).slash(user.getId()).slash("favorites").toUri());
            return headers;
        } else {
            throw new MongoException("Something went wrong");
        }
    }

    @Override
    public void removeFavorite(String userId, Favorite favorite) {
        playgroundExists(favorite.getPlayground());
        User user = findUser(userId);
        WriteResult writeResult = userRepository.removeFromFavorites(userId, favorite);
        if (writeResult.getN() == 1) {
            return;
        }else {
            throw new MongoException("Something went wrong");
        }
    }

    private void playgroundExists(String playgroundId) {
        Playground playground = playgroundRepository.findById(playgroundId);
        if (playground == null) {
            throw new PlaygroundNotFoundException(playgroundId);
        }
    }

    @Override
    public ResponseEntity<List<GeneralRate>> getUserFavorites(String userId) {
        User user = findUser(userId);

        List<GeneralRate> playgrounds_list = new ArrayList<>();
        for(Favorite favorite : user.getFavorites()){
            GeneralRate generalRate = playgroundRepository.getFavoriteGeneral(favorite.getPlayground());
            generalRate.add(linkTo(PlaygroundController.class).slash(favorite.getPlayground()).withSelfRel());
            playgrounds_list.add(generalRate);
        }

        ResponseEntity<List<GeneralRate>> responseEntity = new ResponseEntity<>(playgrounds_list, HttpStatus.OK);
        return responseEntity;
    }

    @Override
    public User findUser(String user_id) {
        User user = userRepository.findById(user_id);
        if (user == null) {
            throw new UserNotFoundException(user_id);
        }
        return user;
    }

    private HttpHeaders addHeadersToUser(User user) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(linkTo(UserController.class).slash(user.getId()).toUri());
        return httpHeaders;
    }
}
