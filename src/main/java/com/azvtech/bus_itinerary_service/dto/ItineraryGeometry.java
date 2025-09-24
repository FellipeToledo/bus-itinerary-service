package com.azvtech.bus_itinerary_service.dto;

import java.util.List;

public class ItineraryGeometry {

    private String type;
    private List<List<Double>> coordinates;
    private Object crs;

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
