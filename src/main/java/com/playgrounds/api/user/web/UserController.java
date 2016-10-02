package com.playgrounds.api.user.web;

import com.playgrounds.api.playground.model.GeneralRate;
import com.playgrounds.api.playground.model.Playground;
import com.playgrounds.api.playground.repository.PlaygroundRepository;
import com.playgrounds.api.playground.web.PlaygroundController;
import com.playgrounds.api.playground.web.PlaygroundNotFoundException;
import com.playgrounds.api.user.repository.UserRepository;
import com.playgrounds.api.user.model.Favorite;
import com.playgrounds.api.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by christos on 17/5/2016.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private UserRepository userRepository;
    private PlaygroundRepository playgroundRepository;

    @Autowired
    public UserController(UserRepository userRepository, PlaygroundRepository playgroundRepository){
        this.userRepository = userRepository;
        this.playgroundRepository = playgroundRepository;
    }

    @RequestMapping(method = RequestMethod.POST, consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders saveUser(@RequestBody User user){
        User newUser = userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(UserController.class).slash(newUser.getId()).toUri());
        return headers;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Resource<User>> getAllUsers(){
        List<User> users = userRepository.findAll();
        List<Resource<User>> allUsers = new ArrayList<Resource<User>>();
        for(User user: users){
            Resource<User> resource = new Resource<User>(user);
            resource.add(linkTo(UserController.class).slash(user.getId()).withSelfRel());
            allUsers.add(resource);
        }
        return allUsers;
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Resource<User> getUser(@PathVariable("id") String id){
        User user = userRepository.findById(id);
        if(user == null) throw new UserNotFoundException(id);
        Resource<User> resource = new Resource<User>(user);
        resource.add(linkTo(UserController.class).slash(user.getId()).withSelfRel());
        return resource;
    }

    @RequestMapping(value = "/{user_id}/favorites", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders addFavorite(@RequestBody Favorite favorite, @PathVariable("user_id") String user_id){
        Playground playground = playgroundRepository.findById(favorite.getPlayground());
        if(playground == null) throw new PlaygroundNotFoundException(favorite.getPlayground());
        userRepository.addFavorite(user_id,favorite);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(UserController.class).slash(user_id).slash("favorites").toUri());
        return headers;
    }

    @RequestMapping(value = "/{user_id}/favorites", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Resource<GeneralRate>> getUserFavorites(@PathVariable("user_id") String user_id){
        User user = userRepository.findById(user_id);
        List<GeneralRate> playgrounds_list = new ArrayList<GeneralRate>();
        for(Favorite favorite : user.getFavorites()){
            GeneralRate generalRate = playgroundRepository.getPlaygroundGeneral(favorite.getPlayground());
            playgrounds_list.add(generalRate);
        }
        List<Resource<GeneralRate>> resources = new ArrayList<Resource<GeneralRate>>();
        for(GeneralRate generalRate : playgrounds_list){
            Resource<GeneralRate> resource = new Resource<GeneralRate>(generalRate);
            resource.add(linkTo(PlaygroundController.class).slash(generalRate.getId()).withSelfRel());
            resources.add(resource);
        }

        return resources;
    }



}
