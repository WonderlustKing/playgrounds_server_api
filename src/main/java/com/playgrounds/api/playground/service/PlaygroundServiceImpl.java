package com.playgrounds.api.playground.service;

import com.playgrounds.api.playground.PlaygroundInteractor;
import com.playgrounds.api.playground.model.*;
import com.playgrounds.api.playground.repository.PlaygroundRepository;
import com.playgrounds.api.playground.web.PlaygroundController;
import com.playgrounds.api.playground.web.PlaygroundNotFoundException;
import com.playgrounds.api.user.service.UserService;
import com.playgrounds.api.user.web.UserController;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;


@Component
public class PlaygroundServiceImpl implements PlaygroundService {

    private PlaygroundRepository repository;
    private PlaygroundInteractor interactor;
    private LocationConverter locationConverter;
    private UserService userService;

    @Autowired
    public PlaygroundServiceImpl(PlaygroundRepository repository, LocationConverter locationConverter, UserService userService) {
        this.repository = repository;
        this.interactor = new PlaygroundInteractor();
        this.locationConverter = locationConverter;
        this.userService = userService;
    }

    @Override
    public HttpHeaders addPlayground(Playground playground) {
        addCityFromLocation(playground);
        playground = repository.save(playground);
        try {
            addImagesIfExist(playground);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addPlaygroundHeaders(playground);
    }

    @Override
    public HttpHeaders updatePlayground(Playground playground) {
        Playground playgroundFromDb = repository.findById(playground.getId());
        if (playgroundFromDb == null) {
            throw new PlaygroundNotFoundException(playground.getId());
        }
        playground = repository.updatePlayground(playground);
        try {
            addImagesIfExist(playground);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addPlaygroundHeaders(playground);
    }

    @Override
    public void updatePlaygroundOptionalFields(OptionalFields optionalFields) {
        Playground playgroundFromDb = repository.findById(optionalFields.getId());
        if (playgroundFromDb == null) {
            throw new PlaygroundNotFoundException(optionalFields.getId());
        }
        repository.updateOptionalFields(optionalFields);
    }

    private void addImagesIfExist(Playground playground) throws IOException {
        if (playground.getBase64Image() != null) {
            byte[] bytesImage = Base64.decodeBase64(playground.getBase64Image());
            String uploadImageId = uploadImage(playground, playground.getAdded_by(), bytesImage);
            URL uploadImageURL = updatePlaygroundImageField(playground, uploadImageId);
            repository.addImageProfile(playground, uploadImageURL);
        }
    }

    private void addCityFromLocation(Playground playground) {
        double playgroundLatitude = playground.getLocation().getCoordinates()[0];
        double playgroundLongitude = playground.getLocation().getCoordinates()[1];
        String city = locationConverter.getCityNameFromCoordinates(playgroundLatitude, playgroundLongitude);
        playground.setCity(city);
    }

    @Override
    public RateFields addRate(String playground_id, Rate rate) {
        Playground playground = getPlaygroundById(playground_id);
        if (playground == null) throw new PlaygroundNotFoundException(playground_id);
        interactor.checkIfRateExist(playground, rate);
        playground = repository.addRate(playground, rate);
        return getUpdatedRates(playground);
    }

    @Override
    public RateFields updateRate(String playground_id, Rate rate) {
        Playground playground = getPlaygroundById(playground_id);
        if (playground == null) throw new PlaygroundNotFoundException(playground_id);
        playground = repository.updateRate(playground, rate);
        return getUpdatedRates(playground);
    }

    private RateFields getUpdatedRates(Playground playground) {
        RateFields updatedRates = new RateFields(playground.getGeneral_rate(),
                playground.getGeneral_environment(),
                playground.getGeneral_equipment(),
                playground.getGeneral_prices(),
                playground.getGeneral_kids_supervision());
        return updatedRates;
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
        return new ResponseEntity<>(resource, HttpStatus.OK);
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
    public ResponseEntity<List<GeneralRate>> getPlaygroundsByCity(Double latitude, Double longitude, boolean distance) {
        String city = locationConverter.getCityNameFromCoordinates(latitude, longitude);

        List<GeneralRate> generalRates = distance ?
                repository.findByCityOrderByRateWithDistance(city, latitude, longitude) :
                repository.findByCityOrderByRate(city);
        for (GeneralRate generalRate : generalRates) {
            generalRate.add(linkTo(PlaygroundController.class).slash(generalRate.getPlaygroundId()).withSelfRel());
        }
        return new ResponseEntity<>(generalRates, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Resource<List<Playground>>> getPlaygroundByLocationAndByName(Double latitude, Double longitude, String playgroundName) {
        String city = locationConverter.getCityNameFromCoordinates(latitude, longitude);
        List<Playground> playgrounds = new ArrayList<>();
        if (playgroundName == null || playgroundName.equals("")) {
            playgrounds = repository.findByCity(city);
        } else {
            Playground playground = repository.findByCityIgnoreCaseAndNameIgnoreCase(city, playgroundName);
            playgrounds.add(playground);
        }
        Resource<List<Playground>> playgroundResource = new Resource<>(playgrounds);
        playgroundResource.add(linkTo(PlaygroundController.class).slash(playgroundResource.getId()).withSelfRel());
        return new ResponseEntity<>(playgroundResource, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<GeneralRate>> nearByPlaygrounds(double latitude, double longitude, int maxDistance, String sortBy) {
        List<GeneralRate> generalRates = repository.nearMePlaygrounds(latitude, longitude, maxDistance, sortBy);
        for (GeneralRate generalRate : generalRates) {
            generalRate.add(linkTo(PlaygroundController.class).slash(generalRate.getPlaygroundId()).withSelfRel());
        }
        //change returned list to an ArrayList so it can be modifiable
        List<GeneralRate> modifiableList = new ArrayList<>(generalRates);
        Collections.reverse(modifiableList);
        return new ResponseEntity<>(modifiableList, HttpStatus.OK);
    }

    @Override
    public HttpHeaders reportPlayground(String playground_id, Report report) {
        Playground playground = repository.findById(playground_id);
        if (playground == null) throw new PlaygroundNotFoundException(playground_id);
        playground = repository.addReport(report, playground);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(PlaygroundController.class).slash("report").slash(playground.getId()).slash(report.getUser_id()).toUri());
        return headers;
    }

    @Override
    public HttpHeaders addImageToPlayground(String playground_id, List<Image> images) throws IOException {
        Playground playground = repository.findById(playground_id);
        if (playground == null) throw new PlaygroundNotFoundException(playground_id);

        if (images == null || images.size() == 0 ) {
            throw new RuntimeException("Can't upload null image");
        }
        boolean isProfileImageUpdated = false;
        for (Image image : images) {
            String uploadImageId = uploadImage(playground, image.getUserId(), image.getMultipartFile().getBytes());
            URL uploadImageURL = updatePlaygroundImageField(playground, uploadImageId);
            if (playground.getImages().size() == 1 && !isProfileImageUpdated) {
                repository.addImageProfile(playground, uploadImageURL);
                isProfileImageUpdated = true;
            }
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        //httpHeaders.setLocation(linkTo(PlaygroundController.class).slash("images").slash(uploadImageId).toUri());
        return httpHeaders;
    }

    private String uploadImage(Playground playground, String user_id, byte[] bytes) throws IOException {
        String fileName = interactor.uploadedImageName(playground);
        return repository.uploadImage(playground.getId(), user_id, fileName, bytes);
    }

    private URL updatePlaygroundImageField(Playground playground, String imageId) throws MalformedURLException{
        URL imageUrl = interactor.addImage(playground, imageId);
        repository.updateImageField(playground, imageUrl);
        return imageUrl;
    }

    @Override
    public GeneralRate getGeneralRate(String playground_id) {
        Playground playground = repository.findById(playground_id);
        if (playground == null) throw new PlaygroundNotFoundException(playground_id);
        return repository.getPlaygroundGeneral(playground_id);
    }

    @Override
    public byte[] getImage(String image_id) {
        InputStream inputStream = repository.findImageById(image_id);
        if (inputStream == null) throw new RuntimeException("Can't found image");
        try {
            return IOUtils.toByteArray(inputStream);
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
