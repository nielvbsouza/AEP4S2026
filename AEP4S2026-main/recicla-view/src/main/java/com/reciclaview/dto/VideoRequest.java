package com.reciclaview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record VideoRequest(
        @NotBlank(message = "é obrigatório")
        @Size(max = 150, message = "máximo de 150 caracteres")
        String titulo,

        @Size(max = 2000, message = "máximo de 2000 caracteres")
        String descricao,

        @NotBlank(message = "é obrigatório")
        @Size(max = 500, message = "máximo de 500 caracteres")
        @Pattern(regexp = "^https?://\\S+$", message = "deve começar com http:// ou https://")
        String url,

        @NotEmpty(message = "escolha ao menos uma categoria")
        @Size(max = 10, message = "máximo de 10 categorias")
        Set<Integer> categoriaIds
) {
}
