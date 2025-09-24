package com.azvtech.bus_itinerary_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ItineraryMapController {

    @GetMapping("/itinerary-map")
    public String showItineraryMap() {
        return "itinerary-map";
    }
}
