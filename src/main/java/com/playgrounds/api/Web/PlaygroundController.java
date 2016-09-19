package com.playgrounds.api.Web;

import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

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
        User user = userRepository.findById(playground.getAdded_by());
        if(user == null) throw new UsernameNotFoundException(playground.getAdded_by());
        Playground newPlayground = playgroundRepository.save(playground);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(PlaygroundController.class).slash(newPlayground.getId()).toUri());
        return headers;
    }

    @RequestMapping(value = "/{playground_id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Resource<Playground> playground_details(@PathVariable("playground_id") String id,@RequestParam(value = "user", required = false) String user){
        Playground playground = playgroundRepository.findById(id);
        playground.setRates_num(playground.getRate().size());
        if(user != null) {
            Iterator it = playground.getRate().iterator();
            while (it.hasNext()) {
                Rate rate1 = (Rate) it.next();
                if (rate1.getUser().equals(user)) {
                    it.remove();
                    playground.getRate().addFirst(rate1);
                    break;
                }
            }
        }

        Resource<Playground> resource = new Resource<Playground>(playground);
        resource.add(linkTo(UserController.class).slash(playground.getAdded_by()).withRel("added_by"));

        return resource;
    }

    @RequestMapping(value = "/rate/{playground_id}", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders addRate(@RequestBody Rate rate, @PathVariable("playground_id") String id){
        Playground playground = playgroundRepository.findById(id);
        String user_id = rate.getUser();
        User user = userRepository.findById(user_id);
        if(playground.getRate().size() > 0) {
            ListIterator<Rate> list = playground.getRate().listIterator();
            while (list.hasNext()) {
                Rate rateSearch = list.next();
                if (rateSearch.getUser().equals(rate.getUser())) throw new UserRateExistException(user_id);
            }
        }
        if(user == null) throw new UsernameNotFoundException(user_id);
        if(playground == null) throw new PlaygroundNotFoundException(id);
        //rate.setUser(user_id);
        playgroundRepository.addRate(playground, rate);

        HttpHeaders headers = new HttpHeaders();
        //headers.setLocation(linkTo(PlaygroundController.class).slash("rate").slash(playground.getId()).slash(user.getUsername().toLowerCase()).toUri());
        return headers;
    }

    @RequestMapping(value = "/rate/{playground_id}", method = RequestMethod.PUT, consumes = "application/json")
    public HttpHeaders updateRate(@RequestBody Rate rate, @PathVariable("playground_id") String id){
        Playground playground = playgroundRepository.findById(id);
        String user_id = rate.getUser();
        User user = userRepository.findById(user_id);
        if(user == null) throw new UsernameNotFoundException(user_id);
        if(playground == null) throw new PlaygroundNotFoundException(id);
        //rate.getUser().setId(user.getId());
        playgroundRepository.updateRate(playground, rate);

        HttpHeaders headers = new HttpHeaders();
        //headers.setLocation(linkTo(PlaygroundController.class).slash("rate").slash(playground.getId()).slash(user.getUsername().toLowerCase()).toUri());
        return headers;
    }


    @RequestMapping(value = "/city/{city_name}", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Resource<GeneralRate>> getAllPlaygroundsByCity(@PathVariable("city_name") String city){
        List<GeneralRate> playgrounds = playgroundRepository.findByCityOrderByRate(city);

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
        Playground playground = playgroundRepository.findByCityIgnoreCaseAndNameIgnoreCase(city,name);
        if(playground == null) throw new PlaygroundNotFoundException(city,name);
        Resource<Playground> resource = new Resource<Playground>(playground);
        resource.add(linkTo(PlaygroundController.class).slash(playground.getCity()).slash(playground.getName()).withSelfRel());
        return resource;
    }

    @RequestMapping(value="/near_me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<Resource<GeneralRate>> nearMe(@RequestParam(value = "x") double x, @RequestParam(value = "y") double y, @RequestParam(value = "maxDistance") int max, @RequestParam(value="sort",defaultValue = "distance") String sort){
        List<GeneralRate> playgrounds = playgroundRepository.nearMePlaygrounds(x,y,max,sort);
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
        Playground playground = playgroundRepository.findById(playground_id);
        if(playground == null) throw new PlaygroundNotFoundException(playground_id);
        playgroundRepository.addReport(report,playground);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(PlaygroundController.class).slash("report").slash(playground_id).slash(report.getUser_id()).toUri());
        return headers;
    }

    @RequestMapping(value = "/rate/{playground_id}/{user_id}", method = RequestMethod.GET, consumes = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Resource<Rate> getUserRate(@PathVariable("playground_id") String playground_id, @PathVariable("user_id") String user_id){
        Rate rate = playgroundRepository.findRate(playground_id,user_id);

            Resource<Rate> resource = new Resource<Rate>(rate);
            resource.add(linkTo(PlaygroundController.class).slash("rate").slash(playground_id).slash(user_id).withSelfRel());
            return resource;

    }

    @RequestMapping(value = "/upload/{playground_id}", method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders uploadImage(@PathVariable("playground_id") String playground_id, @RequestParam(value="file") MultipartFile image){
        if(image != null) playgroundRepository.uploadImage(image);

        return new HttpHeaders();
    }
}
