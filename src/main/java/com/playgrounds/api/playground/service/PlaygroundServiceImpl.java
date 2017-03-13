package com.playgrounds.api.playground.service;

import com.playgrounds.api.playground.PlaygroundInteractor;
import com.playgrounds.api.playground.model.*;
import com.playgrounds.api.playground.repository.PlaygroundRepository;
import com.playgrounds.api.playground.web.PlaygroundController;
import com.playgrounds.api.playground.web.PlaygroundNotFoundException;
import com.playgrounds.api.user.web.UserController;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by chris on 29/9/2016.
 */
@Component
public class PlaygroundServiceImpl implements PlaygroundService {

    private PlaygroundRepository repository;
    private PlaygroundInteractor interactor;
    private LocationConverter locationConverter;

    @Autowired
    public PlaygroundServiceImpl(PlaygroundRepository repository, LocationConverter locationConverter) {
        this.repository = repository;
        this.interactor = new PlaygroundInteractor();
        this.locationConverter = locationConverter;
    }

    @Override
    public HttpHeaders addPlayground(Playground playground) {
        playground = repository.save(playground);
        return addPlaygroundHeaders(playground);
    }

    @Override
    public HttpHeaders addRate(String playground_id, Rate rate) {
        Playground playground = getPlaygroundById(playground_id);
        interactor.checkIfRateExist(playground, rate);
        playground = repository.addRate(playground, rate);
        return addPlaygroundHeaders(playground);
    }

    @Override
    public void updateRate(String playground_id, Rate rate) {
        Playground playground = getPlaygroundById(playground_id);
        if (playground == null) throw new PlaygroundNotFoundException(playground_id);
        repository.updateRate(playground, rate);
    }

    @Override
    public ResponseEntity<Resource<Playground>> getPlayground(String playground_id, String user_id) {
        Playground playground = repository.findById(playground_id);
        if (playground == null) {
            throw new PlaygroundNotFoundException(playground_id);
        }
        if (user_id != null) {
            interactor.moveUserRateOnTop(playground, user_id);
        }

        Resource<Playground> resource = new Resource<>(playground);
        resource.add(linkTo(UserController.class).slash(playground.getAdded_by()).withRel("added_by"));
        ResponseEntity<Resource<Playground>> responseEntity = new ResponseEntity<>(resource, HttpStatus.OK);
        return responseEntity;
    }

    @Override
    public ResponseEntity<List<PlaygroundToMap>> getAllPlaygroundsToMap() {
        List<PlaygroundToMap> playgroundToMaps = repository.findAllPlaygroundsToMap();
        for (PlaygroundToMap playgroundToMap : playgroundToMaps) {
            playgroundToMap.add(linkTo(PlaygroundController.class).slash(playgroundToMap.getPlaygroundId()).withSelfRel());
        }
        return new ResponseEntity<>(playgroundToMaps, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<GeneralRate>> getPlaygroundsByCity(Double latitude, Double longitude) {
        String city = locationConverter.getCityNameFromCoordinates(latitude, longitude);
        List<GeneralRate> generalRates = repository.findByCityOrderByRateWithDistance(city, latitude, longitude);
        for (GeneralRate generalRate : generalRates) {
            generalRate.add(linkTo(PlaygroundController.class).slash(generalRate.getPlaygroundId()).withSelfRel());
        }
        return new ResponseEntity<>(generalRates, HttpStatus.OK);

        /*
        if (latitude != null && longitude != null) {
            return repository.findByCityOrderByRateWithDistance(city, latitude, longitude);
        } else {
            return repository.findByCityOrderByRate(city);
        }
        */
    }

    @Override
    public ResponseEntity<Resource<Playground>> getPlaygroundByLocationAndByName(Double latitude, Double longitude, String playgroundName) {
        String city = locationConverter.getCityNameFromCoordinates(latitude, longitude);
        Playground playground = repository.findByCityIgnoreCaseAndNameIgnoreCase(city, playgroundName);
        if (playground == null) {
            throw new PlaygroundNotFoundException(city, playgroundName);
        }
        Resource<Playground> playgroundResource = new Resource<>(playground);
        playgroundResource.add(linkTo(PlaygroundController.class).slash(playgroundResource.getId()).withSelfRel());
        ResponseEntity<Resource<Playground>> responseEntity = new ResponseEntity<>(playgroundResource, HttpStatus.OK);
        return responseEntity;
    }

    @Override
    public ResponseEntity<List<GeneralRate>> nearByPlaygrounds(double latitude, double longitude, int maxDistance, String sortBy) {
        List<GeneralRate> generalRates = repository.nearMePlaygrounds(latitude, longitude, maxDistance, sortBy);
        for (GeneralRate generalRate : generalRates) {
            generalRate.add(linkTo(PlaygroundController.class).slash(generalRate.getPlaygroundId()).withSelfRel());
        }
        Collections.reverse(generalRates);
        return new ResponseEntity<>(generalRates, HttpStatus.OK);
    }

    @Override
    public HttpHeaders reportPlayground(String playground_id, Report report) {
        Playground playground = repository.findById(playground_id);
        if (playground == null) throw new PlaygroundNotFoundException(playground_id);
        interactor.addReport(playground, report);
        playground = repository.addReport(report, playground);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(PlaygroundController.class).slash("report").slash(playground.getId()).slash(report.getUser_id()).toUri());
        return headers;
    }

    @Override
    public boolean addImageToPlayground(String playground_id, String user_id, MultipartFile image) throws MalformedURLException {
        if (image == null) throw new RuntimeException("Cant upload null image");
        Playground playground = repository.findById(playground_id);
        if (playground == null) throw new PlaygroundNotFoundException(playground_id);
        String fileName = interactor.uploadedImageName(playground);
        String uploaded_image_id = repository.uploadImage(playground_id, user_id, fileName, image);
        URL imageUrl = interactor.addImage(playground, uploaded_image_id);
        repository.updateImageField(playground, imageUrl);
        if (playground.getImages().size() == 1) repository.addImageProfile(playground, imageUrl);
        return true;
    }

    @Override
    public GeneralRate getGeneralRate(String playground_id) {
        Playground playground = repository.findById(playground_id);
        if (playground == null) throw new PlaygroundNotFoundException(playground_id);
        GeneralRate generalRate = repository.getPlaygroundGeneral(playground_id);
        return generalRate;
    }

    @Override
    public byte[] getImage(String image_id) {
        InputStream inputStream = repository.findImageById(image_id);
        if (inputStream == null) throw new RuntimeException("Can't found image");
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private HttpHeaders addPlaygroundHeaders(Playground playground) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(PlaygroundController.class).slash(playground.getId()).toUri());
        return headers;
    }

    private Playground getPlaygroundById(String playgroundId) {
        Playground playground = repository.findById(playgroundId);
        if (playground == null) throw new PlaygroundNotFoundException(playgroundId);
        return playground;
    }


}
