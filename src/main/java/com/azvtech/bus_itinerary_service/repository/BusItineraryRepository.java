package com.azvtech.bus_itinerary_service.repository;

import com.azvtech.bus_itinerary_service.model.BusItinerary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface BusItineraryRepository extends MongoRepository<BusItinerary, String> {

    // Consulta otimizada para buscar linhas distintas
    @Query(value = "{}", fields = "{ 'servico' : 1 }")
    List<BusItinerary> findAllServicos();

    default List<String> findAllServicosDistinct() {
        return findAllServicos().stream()
                .map(BusItinerary::getServico)
                .distinct()
                .sorted()
                .toList();
    }

    @Query(value = "{}", fields = "{ 'consorcio' : 1 }")
    List<BusItinerary> findAllConsorcios();

    default List<String> findAllConsorciosDistinct() {
        return findAllConsorcios().stream()
                .map(BusItinerary::getConsorcio)
                .distinct()
                .filter(c -> c != null && !c.isEmpty())
                .sorted()
                .toList();
    }

    @Query(value = "{}", fields = "{ 'tipoRota' : 1 }")
    List<BusItinerary> findAllTiposRota();

    default List<String> findAllTiposRotaDistinct() {
        return findAllTiposRota().stream()
                .map(BusItinerary::getTipoRota)
                .distinct()
                .filter(t -> t != null && !t.isEmpty())
                .sorted()
                .toList();
    }

    // Buscar por serviço (linha)
    List<BusItinerary> findByServico(String servico);

    // Buscar por múltiplos serviços
    List<BusItinerary> findByServicoIn(List<String> servicos);
}
