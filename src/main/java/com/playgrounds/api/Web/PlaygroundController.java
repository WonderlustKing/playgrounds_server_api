package com.playgrounds.api.Web;

import com.playgrounds.api.Domain.*;
import com.playgrounds.api.Repository.PlaygroundRepository;
import com.playgrounds.api.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by christos on 17/5/2016.
 */
@RestController
@RequestMapping("/playgrounds")
public class PlaygroundController {

    private PlaygroundRepository playgroundRepository;
    private UserRepository userRepository;

    @Autowired
    public PlaygroundController(PlaygroundRepository playgroundRepository, UserRepository userRepository){
        this.playgroundRepository = playgroundRepository;
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders savePlayground(@RequestBody Playground playground){
        Playground newPlayground = playgroundRepository.save(playground);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(PlaygroundController.class).slash(newPlayground.getId()).toUri());
        return headers;
    }

    @RequestMapping(value = "/rate", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders addRate(@RequestBody Rate rate, @RequestParam(value = "name") String playground_name, @RequestParam(value = "city") String city){
        Playground playground = playgroundRepository.findByCityIgnoreCaseAndNameIgnoreCase(city,playground_name);
        String username = rate.getUser().getUsername();
        User user = userRepository.findByUsername(username);
        if(user == null) throw new UsernameNotFoundException(username);
        if(playground == null) throw new PlaygroundNotFoundException(city, playground_name);
        rate.getUser().setId(user.getId());
        playgroundRepository.addRate(playground, rate);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(PlaygroundController.class).slash("rate").slash(playground.getId()).slash(username.toLowerCase()).toUri());
        return headers;
    }

    @RequestMapping(value = "/rate", method = RequestMethod.PUT, consumes = "application/json")
    public HttpHeaders updateRate(@RequestBody Rate rate, @RequestParam(value = "name") String playground_name, @RequestParam(value = "city") String city){
        Playground playground = playgroundRepository.findByCityIgnoreCaseAndNameIgnoreCase(city,playground_name);
        String username = rate.getUser().getUsername();
        User user = userRepository.findByUsername(username);
        if(user == null) throw new UsernameNotFoundException(username);
        if(playground == null) throw new PlaygroundNotFoundException(city, playground_name);
        rate.getUser().setId(user.getId());
        playgroundRepository.updateRate(playground, rate);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(PlaygroundController.class).slash("rate").slash(playground.getId()).slash(username.toLowerCase()).toUri());
        return headers;
    }


    @RequestMapping(value = "/{city_name}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Resource<GeneralRate>> getAllPlaygroundsByCity(@PathVariable("city_name") String city){
        List<GeneralRate> ratedPlaygrounds = playgroundRepository.findByCityOrderByRate(city);
        List<GeneralRate> unratedPlaygrounds = playgroundRepository.findUnRatePlaygrounds(city);
        List<GeneralRate> playgrounds = new ArrayList<GeneralRate>();
        playgrounds.addAll(ratedPlaygrounds);
        playgrounds.addAll(unratedPlaygrounds);


        List<Resource<GeneralRate>> allPlaygrounds = new ArrayList<Resource<GeneralRate>>();
        for(GeneralRate rate_playground: playgrounds){
            Resource<GeneralRate> resource = new Resource<GeneralRate>(rate_playground);
            //Playground playground = playgroundRepository.findByCityIgnoreCaseAndNameIgnoreCase(city,rate_playground.getName());
            resource.add(linkTo(PlaygroundController.class).slash(rate_playground.getId()).withSelfRel());
            allPlaygrounds.add(resource);
        }
        return allPlaygrounds;
    }

    @RequestMapping(value = "/{city_name}/{playground_name}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Resource<Playground> getPlayground(@PathVariable("city_name") String city, @PathVariable("playground_name") String name){
        Playground playground = playgroundRepository.findByCityIgnoreCaseAndNameIgnoreCase(city,name);
        Resource<Playground> resource = new Resource<Playground>(playground);
        resource.add(linkTo(PlaygroundController.class).slash(playground.getCity()).slash(playground.getName()).withSelfRel());
        return resource;
    }

    @RequestMapping(value="/near_me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Resource<GeneralRate>> nearMe(@RequestParam(value = "x") double x, @RequestParam(value = "y") double y, @RequestParam(value = "maxDistance") int max){
        List<GeneralRate> playgrounds = playgroundRepository.nearMePlaygrounds(x,y,max);
        List<Resource<GeneralRate>> resource_playgrounds = new ArrayList<Resource<GeneralRate>>();
        for(GeneralRate playground : playgrounds){
            Resource<GeneralRate> resource = new Resource<GeneralRate>(playground);
            resource.add(linkTo(PlaygroundController.class).slash(playground.getId()).withSelfRel());
            resource_playgrounds.add(resource);
        }
        return resource_playgrounds;
    }




}
