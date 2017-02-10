package com.playgrounds.api.playground.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 17/12/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistanceMatrixResult {

    private List<Row> rows = new ArrayList<Row>();

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
}
