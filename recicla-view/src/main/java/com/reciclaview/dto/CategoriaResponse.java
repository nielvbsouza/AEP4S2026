package com.reciclaview.dto;

import com.reciclaview.model.Categoria;

public record CategoriaResponse(Integer id, String nome, String descricao) {

    public static CategoriaResponse from(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNome(), c.getDescricao());
    }
}
