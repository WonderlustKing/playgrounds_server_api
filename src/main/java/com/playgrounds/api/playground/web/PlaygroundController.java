package com.playgrounds.api.playground.web;

import com.playgrounds.api.playground.model.*;
import com.playgrounds.api.playground.service.GoogleRestClient;
import com.playgrounds.api.playground.service.PlaygroundService;
import com.playgrounds.api.user.service.UserService;
import com.playgrounds.api.user.validator.RateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.MalformedURLException;
import java.util.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by christos on 17/5/2016.
 */
@RestController
@RequestMapping("/playgrounds")
public class PlaygroundController {

    private PlaygroundService playgroundService;
    private UserService userService;

    @Autowired
    public PlaygroundController(PlaygroundService playgroundService, UserService userService) {
        this.playgroundService = playgroundService;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders addPlayground(@RequestBody @Valid Playground playground) {
        return playgroundService.addPlayground(playground);
    }

    @RequestMapping(value = "/coordinates", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<PlaygroundToMap>> getPlaygroundsCoordinates() {
        return playgroundService.getAllPlaygroundsToMap();
    }

    @RequestMapping(value = "/{playground_id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Resource<Playground>> getPlaygroundDetails(@PathVariable("playground_id") String id, @RequestParam(value = "user", required = false) String user) {
        return playgroundService.getPlayground(id, user);
    }


    @RequestMapping(value = "/rate/{playground_id}", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders addRate(@RequestBody Rate rate, @PathVariable("playground_id") String id) {
        //userService.userExist(rate.getUser());
        return playgroundService.addRate(id, rate);
        //headers.setLocation(linkTo(PlaygroundController.class).slash("rate").slash(playground.getId()).slash(user.getUsername().toLowerCase()).toUri());
    }

    @RequestMapping(value = "/rate/{playground_id}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void updateRate(@RequestBody Rate rate, @PathVariable("playground_id") String id) {
        //userService.userExist(rate.getUser());
        playgroundService.updateRate(id, rate);
        //headers.setLocation(linkTo(PlaygroundController.class).slash("rate").slash(playground.getId()).slash(user.getUsername().toLowerCase()).toUri());
    }


    @RequestMapping(value = "/location", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<GeneralRate>> getAllPlaygroundsByLocation(@RequestParam(value = "x", required = false) Double latitude,
                                                                         @RequestParam(value = "y", required = false) Double longitude) throws Exception {
        return playgroundService.getPlaygroundsByCity(latitude, longitude);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Resource<Playground>> searchPlayground(@RequestParam(value = "x") Double latitude,
                                                                 @RequestParam(value = "y") Double longitude,
                                                                 @RequestParam(value = "playground") String playgoundName) {
        return playgroundService.getPlaygroundByLocationAndByName(latitude, longitude, playgoundName);
    }

    @RequestMapping(value = "/near_me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<GeneralRate>> nearMe(@RequestParam(value = "x") double latitude,
                                                    @RequestParam(value = "y") double longitude,
                                                    @RequestParam(value = "maxDistance") int max,
                                                    @RequestParam(value = "sort", defaultValue = "distance") String sort) {

        return playgroundService.nearByPlaygrounds(latitude, longitude, max, sort);
    }

    @RequestMapping(value = "/report/{id}", method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders reportPlayground(@RequestBody Report report, @PathVariable("id") String playground_id) {
        //userService.userExist(report.getUser_id());
        return playgroundService.reportPlayground(playground_id, report);
    }


    @RequestMapping(value = "/upload/{playground_id}", method = RequestMethod.POST, produces = "text/plain")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadImage(@PathVariable("playground_id") String playground_id,
                              @RequestParam(value = "user") String user_id,
                              @RequestParam(value = "file") MultipartFile image) throws MalformedURLException {
        userService.userExist(user_id);
        playgroundService.addImageToPlayground(playground_id, user_id, image);
        return "Image upload successfully";
    }

    @RequestMapping(value = "/images/{image_id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public byte[] getPlaygroundImage(@PathVariable("image_id") String image_id) {
        return playgroundService.getImage(image_id);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void notFoundRoute() {

    }
}
