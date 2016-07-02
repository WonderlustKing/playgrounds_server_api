package com.playgrounds.api.Web;

import com.playgrounds.api.Domain.User;
import com.playgrounds.api.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Autowired
    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
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

    @RequestMapping(value="/{username}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Resource<User> getUser(@PathVariable("username") String username){
        User user = userRepository.findByUsername(username);
        if(user == null) throw new UserNotFoundException(username);
        Resource<User> resource = new Resource<User>(user);
        resource.add(linkTo(UserController.class).slash(user.getId()).withSelfRel());
        return resource;
    }



}
