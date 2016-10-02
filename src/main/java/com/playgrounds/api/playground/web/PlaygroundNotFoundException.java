package com.playgrounds.api.playground.web;

/**
 * Created by christos on 23/5/2016.
 */
public class PlaygroundNotFoundException extends RuntimeException {
    public  PlaygroundNotFoundException(String id){
        super("Playground: "+ id + " not found");
    }

    public PlaygroundNotFoundException(String cityName, String name){
        super("Playground: "+name + "in "+cityName+" not found");
    }
}
