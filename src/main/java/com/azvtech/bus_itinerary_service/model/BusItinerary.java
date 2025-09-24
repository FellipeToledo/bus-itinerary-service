package com.azvtech.bus_itinerary_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bus_itinerary")
public class BusItinerary {

    @Id
    private String id;

    @Indexed // Índice para consultas por fid
    private Integer fid;

    private Double extensao;
    private String dataInicio;

    @Indexed // Índice para consultas por consórcio
    private String consorcio;

    private String descricaoDesvio;
    private String dataFim;

    @Indexed // Índice para consultas por tipo de rota
    private String tipoRota;

    private String shapeId;
    private Integer direcao;
    private String destino;

    @Indexed // Índice para consultas por serviço (linha)
    private String servico;

    private Double shapeLength;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonLineString geometry;

    public BusItinerary() {
    }

    public BusItinerary(Integer fid, Double extensao, String dataInicio, String consorcio,
                        String descricaoDesvio, String dataFim, String tipoRota, String shapeId,
                        Integer direcao, String destino, String servico, Double shapeLength,
                        GeoJsonLineString geometry) {
        this.fid = fid;
        this.extensao = extensao;
        this.dataInicio = dataInicio;
        this.consorcio = consorcio;
        this.descricaoDesvio = descricaoDesvio;
        this.dataFim = dataFim;
        this.tipoRota = tipoRota;
        this.shapeId = shapeId;
        this.direcao = direcao;
        this.destino = destino;
        this.servico = servico;
        this.shapeLength = shapeLength;
        this.geometry = geometry;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Integer getFid() { return fid; }
    public void setFid(Integer fid) { this.fid = fid; }

    public Double getExtensao() { return extensao; }
    public void setExtensao(Double extensao) { this.extensao = extensao; }

    public String getDataInicio() { return dataInicio; }
    public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }

    public String getConsorcio() { return consorcio; }
    public void setConsorcio(String consorcio) { this.consorcio = consorcio; }

    public String getDescricaoDesvio() { return descricaoDesvio; }
    public void setDescricaoDesvio(String descricaoDesvio) { this.descricaoDesvio = descricaoDesvio; }

    public String getDataFim() { return dataFim; }
    public void setDataFim(String dataFim) { this.dataFim = dataFim; }

    public String getTipoRota() { return tipoRota; }
    public void setTipoRota(String tipoRota) { this.tipoRota = tipoRota; }

    public String getShapeId() { return shapeId; }
    public void setShapeId(String shapeId) { this.shapeId = shapeId; }

    public Integer getDirecao() { return direcao; }
    public void setDirecao(Integer direcao) { this.direcao = direcao; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }

    public Double getShapeLength() { return shapeLength; }
    public void setShapeLength(Double shapeLength) { this.shapeLength = shapeLength; }

    public GeoJsonLineString getGeometry() { return geometry; }
    public void setGeometry(GeoJsonLineString geometry) { this.geometry = geometry; }
}
