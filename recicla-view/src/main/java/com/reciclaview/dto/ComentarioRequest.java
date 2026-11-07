package com.reciclaview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComentarioRequest(
        @NotBlank(message = "escreva algo para comentar")
        @Size(max = 1000, message = "máximo de 1000 caracteres")
        String texto
) {
}
