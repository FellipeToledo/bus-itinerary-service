package com.azvtech.bus_itinerary_service.model;

import java.util.List;

public class GeoJsonLineString {

    private String type = "LineString";
    private List<List<Double>> coordinates;
    private Object crs;

    public GeoJsonLineString() {
    }

    public GeoJsonLineString(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }

    public Object getCrs() {
        return crs;
    }

    public void setCrs(Object crs) {
        this.crs = crs;
    }
}
