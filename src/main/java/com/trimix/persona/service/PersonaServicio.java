package com.trimix.persona.service;

import com.trimix.persona.entity.Filters;
import com.trimix.persona.entity.Persona;
import com.trimix.persona.repository.PersonaRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonaServicio implements IPersonaService{

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public Page<Persona> getPersonas(Pageable pageable) {
        return this.personaRepository.findAll(pageable);
    }

    @Override
    public Page<Persona> getPersonasFilter(Pageable pageable, Filters filters) {
        return personaRepository.findAll((Specification<Persona>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.getPerNombre() != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("perNombre")), "%" + filters.getPerNombre().toLowerCase() + "%"));
            }

            if (filters.getPerTipoDocumento() != null) {
                predicates.add(criteriaBuilder.equal(root.get("perTipoDocumento"), filters.getPerTipoDocumento()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Override
    public Persona getPersonaById(Long idPersona) {
        Persona persona = this.personaRepository.findById(idPersona).orElse(null);
        return persona;
    }

    @Override
    public void createOrUpdate(Persona persona) {
        this.personaRepository.save(persona);
    }

    @Override
    public void delete(Long idPersona) {
        this.personaRepository.deleteById(idPersona);
    }

    @Override
    public Page<Persona> searchPersonas(String search, Pageable pageable) {
        return this.personaRepository.findAll((Specification<Persona>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (search != null && !search.isEmpty()) {
                String likePattern = "%" + search.toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("perNombre")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("perApellido")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("perTipoDocumento")), likePattern)
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}
