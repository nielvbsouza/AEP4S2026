package com.reciclaview.repository;

import com.reciclaview.model.Moderador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModeradorRepository extends JpaRepository<Moderador, Integer> {

    Optional<Moderador> findByUsuario_Id(Integer usuarioId);

    boolean existsByUsuario_Id(Integer usuarioId);
}
