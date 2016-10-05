package com.playgrounds.api.playground.service;

import com.playgrounds.api.playground.PlaygroundInteractor;
import com.playgrounds.api.playground.model.GeneralRate;
import com.playgrounds.api.playground.model.Playground;
import com.playgrounds.api.playground.model.Rate;
import com.playgrounds.api.playground.model.Report;
import com.playgrounds.api.playground.repository.PlaygroundRepository;
import com.playgrounds.api.playground.service.PlaygroundService;
import com.playgrounds.api.playground.web.PlaygroundNotFoundException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by chris on 29/9/2016.
 */
@Component
public class PlaygroundServiceImpl implements PlaygroundService {
    private PlaygroundRepository repository;
    private PlaygroundInteractor interactor;

    @Autowired
    public PlaygroundServiceImpl(PlaygroundRepository repository){
        this.repository = repository;
        this.interactor = new PlaygroundInteractor();
    }

    @Override
    public Playground create(Playground playground) {
        interactor.createPlayground(playground);
        return repository.save(playground);
    }

    @Override
    public Playground addRate(String playground_id, Rate rate) {
        Playground playground = repository.findById(playground_id);
        if(playground == null) throw new PlaygroundNotFoundException(playground_id);
        interactor.addRate(playground,rate);
        return repository.addRate(playground,rate);
    }

    @Override
    public Playground updateRate(String playground_id, Rate rate) {
        Playground playground = repository.findById(playground_id);
        if(playground == null) throw new PlaygroundNotFoundException(playground_id);
        return repository.updateRate(playground,rate);
    }

    @Override
    public Playground getPlayground(String playground_id, String user_id) {
        Playground playground = repository.findById(playground_id);
        if(user_id != null) interactor.moveUserRateOnTop(playground,user_id);
        return playground;
    }

    @Override
    public List<GeneralRate> getPlaygroundsByCity(String city) {
        return repository.findByCityOrderByRate(city);
    }

    @Override
    public Playground getPlaygroundByCityAndByName(String city, String name) {
        Playground playground = repository.findByCityIgnoreCaseAndNameIgnoreCase(city,name);
        if(playground == null) throw new PlaygroundNotFoundException(city,name);
        return playground;
    }

    @Override
    public List<GeneralRate> nearByPlaygrounds(double latitude, double longitude, int maxDistance, String sortBy) {
        return repository.nearMePlaygrounds(latitude,longitude,maxDistance,sortBy);
    }

    @Override
    public Playground reportPlayground(String playground_id, Report report) {
        Playground playground = repository.findById(playground_id);
        if(playground == null) throw new PlaygroundNotFoundException(playground_id);
        interactor.addReport(playground,report);
        return  repository.addReport(report,playground);
    }

    @Override
    public boolean addImageToPlayground(String playground_id, String user_id, MultipartFile image) throws MalformedURLException {
        if(image == null) throw new RuntimeException("Cant upload null image");
        Playground playground = repository.findById(playground_id);
        if(playground == null) throw new PlaygroundNotFoundException(playground_id);
        String fileName = interactor.uploadedImageName(playground);
        String uploaded_image_id = repository.uploadImage(playground_id, user_id, fileName, image);
        URL imageUrl = interactor.addImage(playground,uploaded_image_id);
        repository.updateImageField(playground,imageUrl);
        if(playground.getImages().size() == 1) repository.addImageProfile(playground,imageUrl);
        return true;
    }

    @Override
    public GeneralRate getGeneralRate(String playground_id) {
        Playground playground = repository.findById(playground_id);
        if(playground == null) throw new PlaygroundNotFoundException(playground_id);
        GeneralRate generalRate = repository.getPlaygroundGeneral(playground_id);
        return generalRate;
    }

    @Override
    public byte[] getImage(String image_id) {
        InputStream inputStream = repository.findImageById(image_id);
        if(inputStream == null) throw new RuntimeException("Can't found image");
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
