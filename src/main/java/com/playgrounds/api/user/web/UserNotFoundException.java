package com.playgrounds.api.user.web;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username){
        super("User: "+username+" not found");
    }
}
