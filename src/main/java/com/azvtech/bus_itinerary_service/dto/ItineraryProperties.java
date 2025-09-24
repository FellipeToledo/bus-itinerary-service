package com.azvtech.bus_itinerary_service.dto;

public class ItineraryProperties {

    private Integer fid;
    private Double extensao;
    private String data_inicio;
    private String consorcio;
    private String descricao_desvio;
    private String data_fim;
    private String tipo_rota;
    private String shape_id;
    private Integer direcao;
    private String destino;
    private String servico;
    private Double SHAPE__Length;

    // Getters and Setters
    public Integer getFid() { return fid; }
    public void setFid(Integer fid) { this.fid = fid; }

    public Double getExtensao() { return extensao; }
    public void setExtensao(Double extensao) { this.extensao = extensao; }

    public String getData_inicio() { return data_inicio; }
    public void setData_inicio(String data_inicio) { this.data_inicio = data_inicio; }

    public String getConsorcio() { return consorcio; }
    public void setConsorcio(String consorcio) { this.consorcio = consorcio; }

    public String getDescricao_desvio() { return descricao_desvio; }
    public void setDescricao_desvio(String descricao_desvio) { this.descricao_desvio = descricao_desvio; }

    public String getData_fim() { return data_fim; }
    public void setData_fim(String data_fim) { this.data_fim = data_fim; }

    public String getTipo_rota() { return tipo_rota; }
    public void setTipo_rota(String tipo_rota) { this.tipo_rota = tipo_rota; }

    public String getShape_id() { return shape_id; }
    public void setShape_id(String shape_id) { this.shape_id = shape_id; }

    public Integer getDirecao() { return direcao; }
    public void setDirecao(Integer direcao) { this.direcao = direcao; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }

    public Double getSHAPE__Length() { return SHAPE__Length; }
    public void setSHAPE__Length(Double SHAPE__Length) { this.SHAPE__Length = SHAPE__Length; }
}
