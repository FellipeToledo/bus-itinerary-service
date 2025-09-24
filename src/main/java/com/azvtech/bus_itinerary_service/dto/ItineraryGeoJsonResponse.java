package com.azvtech.bus_itinerary_service.dto;

import java.util.List;

public class ItineraryGeoJsonResponse {

    private List<ItineraryFeature> features;
    private String type;
    private ItineraryCollectionProperties properties;

    public List<ItineraryFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<ItineraryFeature> features) {
        this.features = features;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ItineraryCollectionProperties getProperties() {
        return properties;
    }

    public void setProperties(ItineraryCollectionProperties properties) {
        this.properties = properties;
    }
}
