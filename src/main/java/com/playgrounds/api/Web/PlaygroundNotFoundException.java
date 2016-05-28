package com.playgrounds.api.Web;

/**
 * Created by christos on 23/5/2016.
 */
public class PlaygroundNotFoundException extends RuntimeException {
    public PlaygroundNotFoundException(String cityName, String name){
        super("Playground: "+name + "in "+cityName+" not found");
    }
}
