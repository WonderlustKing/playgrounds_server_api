package com.playgrounds.api.Web;

import com.playgrounds.api.Repository.PlaygroundRepository;
import com.playgrounds.api.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by christos on 12/9/2016.
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    private PlaygroundRepository playgroundRepository;

    @Autowired
    public UploadController(PlaygroundRepository playgroundRepository){
        this.playgroundRepository = playgroundRepository;
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public HttpHeaders uploadImage(@RequestParam(value="file") MultipartFile image){
        if(image != null) playgroundRepository.uploadImage(image);

        return new HttpHeaders();
    }
}
