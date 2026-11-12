package com.reciclaview.dto;

import com.reciclaview.model.StatusVideo;
import com.reciclaview.model.Video;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record VideoResponse(
        Integer id,
        String titulo,
        String descricao,
        String url,
        StatusVideo status,
        LocalDateTime dataPublicacao,
        String autor,
        List<CategoriaResponse> categorias,
        String justificativa
) {

    public static VideoResponse from(Video v) {
        return from(v, null);
    }

    public static VideoResponse from(Video v, String justificativa) {
        List<CategoriaResponse> categorias = v.getCategorias().stream()
                .map(CategoriaResponse::from)
                .sorted(Comparator.comparing(CategoriaResponse::nome))
                .toList();
        return new VideoResponse(
                v.getId(),
                v.getTitulo(),
                v.getDescricao(),
                v.getUrl(),
                v.getStatus(),
                v.getDataPublicacao(),
                v.getUsuario().getNome(),
                categorias,
                justificativa
        );
    }
}
