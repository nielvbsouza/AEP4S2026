package com.reciclaview.dto;

import com.reciclaview.model.Decisao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ModeracaoRequest(
        @NotNull(message = "informe a decisão (APROVADO ou REPROVADO)")
        Decisao decisao,

        @Size(max = 500, message = "máximo de 500 caracteres")
        String justificativa
) {
}
