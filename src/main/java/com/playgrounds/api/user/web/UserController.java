package com.playgrounds.api.user.web;

import com.playgrounds.api.playground.model.FavoriteGeneralRate;
import com.playgrounds.api.playground.model.GeneralRate;
import com.playgrounds.api.playground.model.Playground;
import com.playgrounds.api.playground.service.PlaygroundService;
import com.playgrounds.api.playground.web.PlaygroundController;
import com.playgrounds.api.user.model.Favorite;
import com.playgrounds.api.user.model.User;
import com.playgrounds.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;


@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private PlaygroundService playgroundService;

    @Autowired
    public UserController(UserService userService, PlaygroundService playgroundService){
        this.userService = userService;
        this.playgroundService = playgroundService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders saveUser(@RequestBody User user){
        return userService.addUserIfNotExist(user);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Resource<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        List<Resource<User>> allUsers = new ArrayList<>();
        for(User user: users){
            Resource<User> resource = new Resource<>(user);
            resource.add(linkTo(UserController.class).slash(user.getId()).withSelfRel());
            allUsers.add(resource);
        }
        return allUsers;
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Resource<User>> getUser(@PathVariable("id") String userId){
        return userService.getUser(userId);
    }

    @RequestMapping(value = "/{user_id}/favorites", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders addFavorite(@RequestBody Favorite favorite, @PathVariable("user_id") String userId){
        return userService.addFavorite(userId, favorite);
    }

    @RequestMapping(value = "/{user_id}/favorites", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<GeneralRate>> getUserFavorites(@PathVariable("user_id") String userId){
        return userService.getUserFavorites(userId);
    }

    @RequestMapping(value = "/{user_id}/favorites", method = RequestMethod.DELETE, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void removeFavorite(@RequestBody Favorite favorite, @PathVariable("user_id") String userId){
        userService.removeFavorite(userId, favorite);
    }

    @RequestMapping(value = "/images/{image_id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public byte[] getPlaygroundImage(@PathVariable("image_id") String image_id) {
        return playgroundService.getImage(image_id);
    }

}
