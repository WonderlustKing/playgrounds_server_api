package com.playgrounds.api.playground.model.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 17/12/2016.
 */
public class Row {

    private List<Element> elements = new ArrayList<Element>();

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
}
