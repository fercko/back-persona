package com.trimix.persona.repository;

import com.trimix.persona.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonaRepository extends JpaRepository<Persona,Long>, JpaSpecificationExecutor<Persona> {

}
