package com.playgrounds.api.playground.web;

import com.playgrounds.api.playground.service.PlaygroundService;
import com.playgrounds.api.user.service.UserService;
import com.playgrounds.api.user.web.UserController;
import com.playgrounds.api.playground.model.GeneralRate;
import com.playgrounds.api.playground.model.Playground;
import com.playgrounds.api.playground.model.Rate;
import com.playgrounds.api.playground.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public PlaygroundController(PlaygroundService playgroundService, UserService userService){
        this.playgroundService = playgroundService;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders savePlayground(@RequestBody Playground playground){
        userService.userExist(playground.getAdded_by());
        Playground newPlayground = playgroundService.create(playground);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(PlaygroundController.class).slash(newPlayground.getId()).toUri());
        return headers;
    }

    @RequestMapping(value = "/{playground_id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Resource<Playground> playground_details(@PathVariable("playground_id") String id,@RequestParam(value = "user", required = false) String user){
        Playground playground = playgroundService.getPlayground(id,user);
        Resource<Playground> resource = new Resource<Playground>(playground);
        resource.add(linkTo(UserController.class).slash(playground.getAdded_by()).withRel("added_by"));
        return resource;
    }

    @RequestMapping(value = "/rate/{playground_id}", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders addRate(@RequestBody Rate rate, @PathVariable("playground_id") String id){
        userService.userExist(rate.getUser());
        playgroundService.addRate(id,rate);

        HttpHeaders headers = new HttpHeaders();
        //headers.setLocation(linkTo(PlaygroundController.class).slash("rate").slash(playground.getId()).slash(user.getUsername().toLowerCase()).toUri());
        return headers;
    }

    @RequestMapping(value = "/rate/{playground_id}", method = RequestMethod.PUT, consumes = "application/json")
    public HttpHeaders updateRate(@RequestBody Rate rate, @PathVariable("playground_id") String id){
        userService.userExist(rate.getUser());
        playgroundService.updateRate(id,rate);

        HttpHeaders headers = new HttpHeaders();
        //headers.setLocation(linkTo(PlaygroundController.class).slash("rate").slash(playground.getId()).slash(user.getUsername().toLowerCase()).toUri());
        return headers;
    }


    @RequestMapping(value = "/city/{city_name}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Resource<GeneralRate>> getAllPlaygroundsByCity(@PathVariable("city_name") String city){
        List<GeneralRate> playgrounds = playgroundService.getPlaygroundsByCity(city);
        List<Resource<GeneralRate>> allPlaygrounds = new ArrayList<Resource<GeneralRate>>();
        for(GeneralRate rate_playground: playgrounds){
            Resource<GeneralRate> resource = new Resource<GeneralRate>(rate_playground);
            //Playground playground = playgroundRepository.findByCityIgnoreCaseAndNameIgnoreCase(city,rate_playground.getName());
            resource.add(linkTo(PlaygroundController.class).slash(rate_playground.getId()).withSelfRel());
            allPlaygrounds.add(resource);
        }
        return allPlaygrounds;
    }

    @RequestMapping(value = "/search/{city_name}/{playground_name}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Resource<Playground> getPlayground(@PathVariable("city_name") String city, @PathVariable("playground_name") String name){
        Playground playground = playgroundService.getPlaygroundByCityAndByName(city,name);
        Resource<Playground> resource = new Resource<Playground>(playground);
        resource.add(linkTo(PlaygroundController.class).slash(playground.getCity()).slash(playground.getName()).withSelfRel());
        return resource;
    }

    @RequestMapping(value="/near_me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Resource<GeneralRate>> nearMe(@RequestParam(value = "x") double x, @RequestParam(value = "y") double y, @RequestParam(value = "maxDistance") int max, @RequestParam(value="sort",defaultValue = "distance") String sort){
        List<GeneralRate> playgrounds = playgroundService.nearByPlaygrounds(x,y,max,sort);
        List<Resource<GeneralRate>> resource_playgrounds = new ArrayList<Resource<GeneralRate>>();
        for(GeneralRate playground : playgrounds){
            Resource<GeneralRate> resource = new Resource<GeneralRate>(playground);
            resource.add(linkTo(PlaygroundController.class).slash(playground.getId()).withSelfRel());
            resource_playgrounds.add(resource);
        }
        Collections.reverse(resource_playgrounds);
        return  resource_playgrounds;
    }

    @RequestMapping(value = "/report/{id}", method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders reportPlayground(@RequestBody Report report, @PathVariable("id") String playground_id){
        userService.userExist(report.getUser_id());
        Playground playground = playgroundService.reportPlayground(playground_id,report);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(PlaygroundController.class).slash("report").slash(playground.getId()).slash(report.getUser_id()).toUri());
        return headers;
    }


    @RequestMapping(value = "/upload/{playground_id}", method = RequestMethod.POST, produces = "text/plain")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadImage(@PathVariable("playground_id") String playground_id,@RequestParam(value = "user") String user_id, @RequestParam(value="file") MultipartFile image) throws MalformedURLException {
        userService.userExist(user_id);
        playgroundService.addImageToPlayground(playground_id,user_id,image);
        return "Image upload successfully";
    }

    @RequestMapping(value = "/images/{image_id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public byte[] getPlaygroundImage(@PathVariable("image_id") String image_id){
        return playgroundService.getImage(image_id);
    }
}
