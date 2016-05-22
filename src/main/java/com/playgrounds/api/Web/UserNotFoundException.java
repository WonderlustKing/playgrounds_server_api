package com.playgrounds.api.Web;

/**
 * Created by christos on 17/5/2016.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username){
        super("User: "+username+" not found");
    }
}
