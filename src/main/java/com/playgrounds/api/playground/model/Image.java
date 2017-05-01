package com.playgrounds.api.playground.model;

import com.playgrounds.api.user.validator.UserExist;
import org.springframework.web.multipart.MultipartFile;


public class Image {

    @UserExist(message = "User not exist")
    private String userId;

    private MultipartFile multipartFile;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }
}
