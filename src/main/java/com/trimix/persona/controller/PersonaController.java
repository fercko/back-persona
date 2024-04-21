package com.trimix.persona.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trimix.persona.entity.Filters;
import com.trimix.persona.entity.Persona;
import com.trimix.persona.service.PersonaServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("persona-app")
@CrossOrigin(value = "http://localhost:3000")
public class PersonaController {

    private static final Logger logger = LoggerFactory.getLogger(PersonaController.class);

    @Autowired
    private PersonaServicio personaServicio;

    @GetMapping("/personas")
    public ResponseEntity<Map<String, Object>> getPersonas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String filters,
            @RequestParam(required = false) String search
    ) {
        Map<String, Object> respuesta = new HashMap<>();

        try {
            Pageable pageable = PageRequest.of(page, pageSize);
            Page<Persona> personasPage = null;

            if (search != null && !search.isEmpty()) {
                personasPage = this.personaServicio.searchPersonas(search, pageable);
            } else if (filters != null && !filters.isEmpty()) {

                ObjectMapper objectMapper = new ObjectMapper();
                Filters filtros = objectMapper.readValue(filters, Filters.class);

                personasPage = personaServicio.getPersonasFilter(pageable, filtros);
            }else {
                personasPage = this.personaServicio.getPersonas(pageable);
            }

            respuesta.put("status", HttpStatus.OK.value());
            respuesta.put("data", personasPage.getContent());
            respuesta.put("current_page", personasPage.getNumber() + 1);
            respuesta.put("from", page * pageSize + 1);
            respuesta.put("to", (page * pageSize) + personasPage.getNumberOfElements());
            respuesta.put("per_page", personasPage.getSize());
            respuesta.put("total", personasPage.getTotalElements());
            respuesta.put("ok", true);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            respuesta.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            respuesta.put("error", "Error al traer los datos: " + e.getMessage());
            respuesta.put("ok", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    @PostMapping("/personas")
    public ResponseEntity<Map<String, Object>> create(
            @RequestBody Persona persona
    ){
        Map<String, Object> response = new HashMap<>();
        try {
            personaServicio.createOrUpdate(persona);
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Persona creada");
            response.put("ok", true);
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("UK_mptusfc12gk0vh8usvj9fy8rx")) {
                response.put("status", HttpStatus.BAD_REQUEST.value());
                response.put("error", "Error al crear la persona: el DNI proporcionado ya está registrado.");
            } else {
                response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.put("error", "Error interno del servidor.");
            }
            response.put("ok", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("error", "Error al crear la persona: " + e.getMessage());
            response.put("ok", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/personas/{idPersona}")
    public ResponseEntity<Map<String, Object>> getPersona(
            @PathVariable Long idPersona
    ) {
        Map<String, Object> response = new HashMap<>();
        try{
            Persona persona = this.personaServicio.getPersonaById(idPersona);

            response.put("status", HttpStatus.OK.value());
            response.put("data", persona);
            response.put("ok", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("error", "Error al obtener a la persona: " + e.getMessage());
            response.put("ok", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/personas/{idPersona}")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long idPersona,
            @RequestBody Persona personsaRecibida
    ){
        Map<String, Object> response = new HashMap<>();
        try {
            Persona personaUpdate = this.personaServicio.getPersonaById(idPersona);

            personaUpdate.setPerNombre(personsaRecibida.getPerNombre());
            personaUpdate.setPerApellido(personsaRecibida.getPerApellido());
            personaUpdate.setPerNumeroDocumento(personsaRecibida.getPerNumeroDocumento());
            personaUpdate.setPerTipoDocumento(personsaRecibida.getPerTipoDocumento());
            personaUpdate.setPerFechaNacimiento(personsaRecibida.getPerFechaNacimiento());

            this.personaServicio.createOrUpdate(personaUpdate);

            response.put("status", HttpStatus.OK.value());
            response.put("message", "Persona modificada");
            response.put("ok", true);
            return ResponseEntity.ok(response);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("UK_mptusfc12gk0vh8usvj9fy8rx")) {
                response.put("status", HttpStatus.BAD_REQUEST.value());
                response.put("error", "Error al modificar la persona: el DNI proporcionado ya está registrado.");
            } else {
                response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.put("error", "Error interno del servidor.");
            }
            response.put("ok", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("error", "Error al editar la persona: " + e.getMessage());
            response.put("ok", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/personas/{idPersona}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long idPersona){
        Map<String, Object> response = new HashMap<>();
        try{
            Persona persona = this.personaServicio.getPersonaById(idPersona);
            this.personaServicio.delete(idPersona);

            response.put("status", HttpStatus.OK.value());
            response.put("message", "Persona eliminada");
            response.put("ok", true);

            return ResponseEntity.ok(response);
        }catch (Exception e) {
            response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.put("error", "Error al editar la persona: " + e.getMessage());
            response.put("ok", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
