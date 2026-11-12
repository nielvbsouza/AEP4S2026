package com.reciclaview.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "é obrigatório")
        @Size(max = 100, message = "máximo de 100 caracteres")
        String nome,

        @NotBlank(message = "é obrigatório")
        @Email(message = "e-mail inválido")
        @Size(max = 150, message = "máximo de 150 caracteres")
        String email,

        @NotBlank(message = "é obrigatória")
        @Size(min = 6, max = 72, message = "deve ter de 6 a 72 caracteres")
        String senha
) {
}
