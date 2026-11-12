package com.reciclaview.repository;

import com.reciclaview.model.Moderacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModeracaoRepository extends JpaRepository<Moderacao, Integer> {

    List<Moderacao> findByVideo_Id(Integer videoId);

    Optional<Moderacao> findFirstByVideo_IdOrderByIdDesc(Integer videoId);
}
