package com.reciclaview.repository;

import com.reciclaview.model.StatusVideo;
import com.reciclaview.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Integer> {

    List<Video> findByStatus(StatusVideo status);

    List<Video> findByCategorias_Id(Integer categoriaId);

    List<Video> findByStatusOrderByDataPublicacaoDesc(StatusVideo status);

    List<Video> findByUsuario_IdOrderByDataPublicacaoDesc(Integer usuarioId);
}
