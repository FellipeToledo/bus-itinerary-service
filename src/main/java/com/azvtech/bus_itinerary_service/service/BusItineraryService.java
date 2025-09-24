package com.azvtech.bus_itinerary_service.service;

import com.azvtech.bus_itinerary_service.dto.ItineraryFeature;
import com.azvtech.bus_itinerary_service.dto.ItineraryGeoJsonResponse;
import com.azvtech.bus_itinerary_service.dto.ItineraryGeometry;
import com.azvtech.bus_itinerary_service.dto.ItineraryProperties;
import com.azvtech.bus_itinerary_service.model.BusItinerary;
import com.azvtech.bus_itinerary_service.model.GeoJsonLineString;
import com.azvtech.bus_itinerary_service.repository.BusItineraryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BusItineraryService {

    private static final String BASE_URL = "https://pgeo3.rio.rj.gov.br/arcgis/rest/services/Hosted/Itinerários_da_rede_de_transporte_público_por_ônibus_(SPPO)/FeatureServer/1/query";
    private final MongoTemplate mongoTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final BusItineraryRepository repository;

    public BusItineraryService(MongoTemplate mongoTemplate, BusItineraryRepository repository) {
        this.mongoTemplate = mongoTemplate;
        this.repository = repository;
    }

    @PostConstruct
    public void loadInitialData() {
        if (repository.count() > 0) {
            System.out.println("[INFO] Itinerários já existem no MongoDB. Pulando carga.");
            return;
        }

        System.out.println("[INFO] Carregando itinerários da API pública...");
        List<ItineraryFeature> features = loadAllItineraries();

        List<BusItinerary> busItineraries = features.stream().map(feature -> {
            ItineraryProperties props = feature.getProperties();
            ItineraryGeometry geometry = feature.getGeometry();

            BusItinerary itinerary = new BusItinerary();
            itinerary.setFid(props.getFid());
            itinerary.setExtensao(props.getExtensao());
            itinerary.setDataInicio(props.getData_inicio());
            itinerary.setConsorcio(props.getConsorcio());
            itinerary.setDescricaoDesvio(props.getDescricao_desvio());
            itinerary.setDataFim(props.getData_fim());
            itinerary.setTipoRota(props.getTipo_rota());
            itinerary.setShapeId(props.getShape_id());
            itinerary.setDirecao(props.getDirecao());
            itinerary.setDestino(props.getDestino());
            itinerary.setServico(props.getServico());
            itinerary.setShapeLength(props.getSHAPE__Length());

            if (geometry != null && geometry.getCoordinates() != null) {
                GeoJsonLineString lineString = new GeoJsonLineString(geometry.getCoordinates());
                itinerary.setGeometry(lineString);
            }

            return itinerary;
        }).toList();

        repository.saveAll(busItineraries);
        System.out.printf("[INFO] %d itinerários salvos no MongoDB.%n", busItineraries.size());
    }

    private List<ItineraryFeature> loadAllItineraries() {
        List<ItineraryFeature> allFeatures = new ArrayList<>();
        int offset = 0;
        final int PAGE_SIZE = 1000;

        while (true) {
            URI uri = UriComponentsBuilder
                    .fromUriString(BASE_URL)
                    .queryParam("outFields", "*")
                    .queryParam("where", "1=1")
                    .queryParam("f", "geojson")
                    .queryParam("resultRecordCount", PAGE_SIZE)
                    .queryParam("resultOffset", offset)
                    .build()
                    .toUri();

            ItineraryGeoJsonResponse page = restTemplate.getForObject(uri, ItineraryGeoJsonResponse.class);

            if (page == null || page.getFeatures() == null || page.getFeatures().isEmpty()) {
                break;
            }

            allFeatures.addAll(page.getFeatures());

            if (page.getFeatures().size() < PAGE_SIZE) {
                break;
            }

            offset += PAGE_SIZE;
        }

        return allFeatures;
    }

    // MÉTODO PRINCIPAL: Buscar por linhas específicas
    public List<BusItinerary> findByServicos(List<String> servicos) {
        Query query = new Query();

        if (servicos != null && !servicos.isEmpty()) {
            query.addCriteria(Criteria.where("servico").in(servicos));
        }

        // Projeção para retornar apenas campos necessários
        query.fields()
                .include("servico")
                .include("destino")
                .include("consorcio")
                .include("tipoRota")
                .include("direcao")
                .include("extensao")
                .include("geometry")
                .exclude("id");

        return mongoTemplate.find(query, BusItinerary.class);
    }

    // Métodos auxiliares para metadados
    public List<String> findAllServicos() {
        return repository.findAllServicosDistinct();
    }

    public List<String> findAllConsorcios() {
        return repository.findAllConsorciosDistinct();
    }

    public List<String> findAllTiposRota() {
        return repository.findAllTiposRotaDistinct();
    }

    public void updateItineraries() {
        System.out.println("[INFO] Recarregando dados de itinerários da API...");
        repository.deleteAll();
        loadInitialData();
    }

    // Método para buscar por linhas com filtros
    public List<BusItinerary> findByServicosAndFilters(List<String> servicos, String consortium, Integer direction) {
        Query query = new Query();

        // Critério principal: linhas selecionadas
        if (servicos != null && !servicos.isEmpty()) {
            query.addCriteria(Criteria.where("servico").in(servicos));
        }

        // Filtro por consórcio
        if (consortium != null && !consortium.trim().isEmpty()) {
            query.addCriteria(Criteria.where("consorcio").is(consortium));
        }

        // Filtro por direção
        if (direction != null) {
            query.addCriteria(Criteria.where("direcao").is(direction));
        }

        // Projeção para retornar apenas campos necessários
        query.fields()
                .include("servico")
                .include("destino")
                .include("consorcio")
                .include("tipoRota")
                .include("direcao")
                .include("extensao")
                .include("geometry")
                .exclude("id");

        return mongoTemplate.find(query, BusItinerary.class);
    }
}
