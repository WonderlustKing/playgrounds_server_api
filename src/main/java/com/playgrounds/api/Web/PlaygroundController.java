package com.playgrounds.api.Web;

import com.playgrounds.api.Domain.Playground;
import com.playgrounds.api.Repository.PlaygroundRepository;
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
@RequestMapping("/playgrounds")
public class PlaygroundController {

    private PlaygroundRepository playgroundRepository;

    @Autowired
    public PlaygroundController(PlaygroundRepository playgroundRepository){
        this.playgroundRepository = playgroundRepository;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders savePlayground(@RequestBody Playground playground){
        Playground newPlayground = playgroundRepository.save(playground);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(PlaygroundController.class).slash(newPlayground.getCity()).slash(newPlayground.getName()).toUri());
        return headers;
    }

    @RequestMapping(value = "/{city_name}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Resource<Playground>> getAllPlaygroundsByCity(@PathVariable("city_name") String city){
        List<Playground> playgrounds = playgroundRepository.findByCity(city);
        List<Resource<Playground>> allPlaygrounds = new ArrayList<Resource<Playground>>();
        for(Playground playground: playgrounds){
            Resource<Playground> resource = new Resource<Playground>(playground);
            resource.add(linkTo(PlaygroundController.class).slash(playground.getCity()).slash(playground.getName()).withSelfRel());
            allPlaygrounds.add(resource);
        }
        return allPlaygrounds;
    }

    @RequestMapping(value = "/{city_name}/{playground_name}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Resource<Playground> getPlayground(@PathVariable("city_name") String city, @PathVariable("playground_name") String name){
        Playground playground = playgroundRepository.findByCityAndName(city,name);
        Resource<Playground> resource = new Resource<Playground>(playground);
        resource.add(linkTo(PlaygroundController.class).slash(playground.getCity()).slash(playground.getName()).withSelfRel());
        return resource;
    }





}
