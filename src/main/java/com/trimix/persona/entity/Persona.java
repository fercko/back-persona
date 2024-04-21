package com.trimix.persona.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    Long perId;

    @Column(name="nombre",nullable = false)
    String perNombre;

    @Column(name="apellido",nullable = false)
    String perApellido;

    @Column(name="numero_documento",unique = true,nullable = false)
    Long perNumeroDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name="tipo_documento",nullable = false)
    private TipoDocumento perTipoDocumento;

    @Column(name="fecha_nacimiento",nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate perFechaNacimiento;

    public enum TipoDocumento {
        DNI, PASAPORTE, LIBRETA
    }
}
