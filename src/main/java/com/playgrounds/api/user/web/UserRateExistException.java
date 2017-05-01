package com.playgrounds.api.user.web;


public class UserRateExistException extends RuntimeException {
    public UserRateExistException(String id){
        super("Rate of user: "+id+" already exists");
    }
}
