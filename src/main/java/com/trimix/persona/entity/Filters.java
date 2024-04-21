package com.trimix.persona.entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Filters {
    @JsonProperty("perNombre")
    private String perNombre;

    @JsonProperty("perTipoDocumento")
    private String perTipoDocumento;

}
