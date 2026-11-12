package com.reciclaview.controller;

import com.reciclaview.repository.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatusController {

    private final UsuarioRepository usuarios;
    private final CategoriaRepository categorias;
    private final ModeradorRepository moderadores;
    private final VideoRepository videos;
    private final ComentarioRepository comentarios;
    private final ModeracaoRepository moderacoes;

    public StatusController(UsuarioRepository usuarios,
                            CategoriaRepository categorias,
                            ModeradorRepository moderadores,
                            VideoRepository videos,
                            ComentarioRepository comentarios,
                            ModeracaoRepository moderacoes) {
        this.usuarios = usuarios;
        this.categorias = categorias;
        this.moderadores = moderadores;
        this.videos = videos;
        this.comentarios = comentarios;
        this.moderacoes = moderacoes;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("aplicacao", "Recicla View");
        resposta.put("banco", "conectado");
        resposta.put("usuarios", usuarios.count());
        resposta.put("categorias", categorias.count());
        resposta.put("moderadores", moderadores.count());
        resposta.put("videos", videos.count());
        resposta.put("comentarios", comentarios.count());
        resposta.put("moderacoes", moderacoes.count());
        return resposta;
    }
}
