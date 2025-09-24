package com.azvtech.bus_itinerary_service.controller;

import com.azvtech.bus_itinerary_service.dto.*;
import com.azvtech.bus_itinerary_service.model.BusItinerary;
import com.azvtech.bus_itinerary_service.service.BusItineraryService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/itineraries")
public class BusItineraryController {

    private final BusItineraryService service;

    public BusItineraryController(BusItineraryService service) {
        this.service = service;
    }

    // ENDPOINT PRINCIPAL: Buscar itinerários por linhas
    @GetMapping("/by-lines")
    public ItineraryGeoJsonResponse getItinerariesByLines(
            @RequestParam(required = false) List<String> lines) {

        System.out.println("Buscando itinerários para as linhas: " + lines);

        List<BusItinerary> busItineraries;
        if (lines == null || lines.isEmpty()) {
            busItineraries = List.of(); // Retorna vazio se não especificar linhas
        } else {
            busItineraries = service.findByServicos(lines);
        }

        System.out.println("Encontrados " + busItineraries.size() + " itinerários");
        return convertToGeoJson(busItineraries);
    }

    // Endpoints de metadados
    @GetMapping("/metadata/lines")
    public List<String> getAllLines() {
        return service.findAllServicos();
    }

    @GetMapping("/metadata/consortiums")
    public List<String> getAllConsortiums() {
        return service.findAllConsorcios();
    }

    @GetMapping("/metadata/route-types")
    public List<String> getAllRouteTypes() {
        return service.findAllTiposRota();
    }

    @PostMapping("/reload")
    public String reloadItineraries() {
        service.updateItineraries();
        return "Itinerários recarregados com sucesso";
    }

    private ItineraryGeoJsonResponse convertToGeoJson(List<BusItinerary> busItineraries) {
        ItineraryGeoJsonResponse response = new ItineraryGeoJsonResponse();
        List<ItineraryFeature> features = new ArrayList<>();

        for (BusItinerary itinerary : busItineraries) {
            ItineraryFeature feature = new ItineraryFeature();
            ItineraryProperties properties = new ItineraryProperties();
            ItineraryGeometry geometry = new ItineraryGeometry();

            // Configurar properties
            properties.setFid(itinerary.getFid());
            properties.setExtensao(itinerary.getExtensao());
            properties.setData_inicio(itinerary.getDataInicio());
            properties.setConsorcio(itinerary.getConsorcio());
            properties.setDescricao_desvio(itinerary.getDescricaoDesvio());
            properties.setData_fim(itinerary.getDataFim());
            properties.setTipo_rota(itinerary.getTipoRota());
            properties.setShape_id(itinerary.getShapeId());
            properties.setDirecao(itinerary.getDirecao());
            properties.setDestino(itinerary.getDestino());
            properties.setServico(itinerary.getServico());
            properties.setSHAPE__Length(itinerary.getShapeLength());

            // Configurar geometry
            geometry.setType("LineString");
            if (itinerary.getGeometry() != null) {
                geometry.setCoordinates(itinerary.getGeometry().getCoordinates());
            }

            feature.setProperties(properties);
            feature.setGeometry(geometry);
            feature.setType("Feature");

            features.add(feature);
        }

        response.setFeatures(features);
        response.setType("FeatureCollection");

        ItineraryCollectionProperties collectionProps = new ItineraryCollectionProperties();
        collectionProps.setExceededTransferLimit(false);
        response.setProperties(collectionProps);

        return response;
    }
}
