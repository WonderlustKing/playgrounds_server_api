package com.playgrounds.api.Web;

/**
 * Created by christos on 9/8/2016.
 */
public class UserRateExistException extends RuntimeException {
    public UserRateExistException(String id){
        super("Rate of user: "+id+" already exists");
    }
}
