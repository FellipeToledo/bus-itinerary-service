package com.azvtech.bus_itinerary_service.dto;

public class ItineraryFeature {

    private ItineraryGeometry geometry;
    private String id;
    private String type;
    private ItineraryProperties properties;

    public ItineraryGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(ItineraryGeometry geometry) {
        this.geometry = geometry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ItineraryProperties getProperties() {
        return properties;
    }

    public void setProperties(ItineraryProperties properties) {
        this.properties = properties;
    }
}
