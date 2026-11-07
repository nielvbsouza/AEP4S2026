package com.reciclaview.repository;

import com.reciclaview.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {

    List<Comentario> findByVideo_Id(Integer videoId);

    List<Comentario> findByVideo_IdOrderByDataComentarioAsc(Integer videoId);
}
