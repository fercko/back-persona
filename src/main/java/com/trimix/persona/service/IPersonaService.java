package com.trimix.persona.service;

import com.trimix.persona.entity.Filters;
import com.trimix.persona.entity.Persona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPersonaService {
    public Page<Persona> getPersonas(Pageable pageable);
    public Page<Persona> getPersonasFilter(Pageable pageable, Filters filters);
    public Persona getPersonaById(Long idPersona);
    public void createOrUpdate(Persona persona);
    public void delete(Long idPersona);
    public Page<Persona> searchPersonas(String search, Pageable pageable);
}
