package com.playgrounds.api.playground.web;

import com.playgrounds.api.playground.model.*;
import com.playgrounds.api.playground.service.PlaygroundService;
import com.playgrounds.api.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;


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

    @RequestMapping(method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public HttpHeaders updatePlayground(@RequestBody @Valid Playground playground) {
        return playgroundService.updatePlayground(playground);
    }

    @RequestMapping(value = "/optionalFields",method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void updatePlaygroundOptionalFields(@RequestBody OptionalFields playground) {
        playgroundService.updatePlaygroundOptionalFields(playground);
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
    public HttpHeaders addRate(@RequestBody @Valid Rate rate, @PathVariable("playground_id") String id) {
        return playgroundService.addRate(id, rate);
    }

    @RequestMapping(value = "/rate/{playground_id}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public void updateRate(@RequestBody @Valid Rate rate, @PathVariable("playground_id") String id) {
        playgroundService.updateRate(id, rate);
    }


    @RequestMapping(value = "/location", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<GeneralRate>> getAllPlaygroundsByLocation(@RequestParam(value = "x") Double latitude,
                                                                         @RequestParam(value = "y") Double longitude,
                                                                         @RequestParam(value = "distance") boolean distance) throws Exception {
        return playgroundService.getPlaygroundsByCity(latitude, longitude, distance);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Resource<List<Playground>>> searchPlayground(@RequestParam(value = "x") Double latitude,
                                                                 @RequestParam(value = "y") Double longitude,
                                                                 @RequestParam(value = "playground", required = false) String playgroundName) {
        return playgroundService.getPlaygroundByLocationAndByName(latitude, longitude, playgroundName);
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
    public HttpHeaders reportPlayground(@RequestBody @Valid Report report, @PathVariable("id") String playground_id) {
        return playgroundService.reportPlayground(playground_id, report);
    }


    @RequestMapping(value = "/upload/{playground_id}", method = RequestMethod.POST, produces = "text/plain")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders uploadImage(@PathVariable("playground_id") String playground_id,
                                    @RequestBody Image image) throws IOException {
        //userService.userExist(userId);
        return playgroundService.addImageToPlayground(playground_id, image);
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
