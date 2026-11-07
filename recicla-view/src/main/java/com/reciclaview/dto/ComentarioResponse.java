package com.reciclaview.dto;

import com.reciclaview.model.Comentario;

import java.time.LocalDateTime;

public record ComentarioResponse(Integer id, String texto, LocalDateTime dataComentario, String autor) {

    public static ComentarioResponse from(Comentario c) {
        return new ComentarioResponse(c.getId(), c.getTexto(), c.getDataComentario(), c.getUsuario().getNome());
    }
}
