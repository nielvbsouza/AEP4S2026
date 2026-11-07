package com.reciclaview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "é obrigatório")
        @Size(max = 80, message = "máximo de 80 caracteres")
        String nome,

        @Size(max = 255, message = "máximo de 255 caracteres")
        String descricao
) {
}
