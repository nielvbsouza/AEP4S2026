package com.reciclaview.controller;

import com.reciclaview.dto.ModeracaoRequest;
import com.reciclaview.dto.VideoResponse;
import com.reciclaview.model.Moderador;
import com.reciclaview.model.StatusVideo;
import com.reciclaview.repository.VideoRepository;
import com.reciclaview.security.AutenticacaoService;
import com.reciclaview.service.ModeracaoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderacao")
public class ModeracaoController {

    private final VideoRepository videos;
    private final ModeracaoService servico;
    private final AutenticacaoService auth;

    public ModeracaoController(VideoRepository videos, ModeracaoService servico, AutenticacaoService auth) {
        this.videos = videos;
        this.servico = servico;
        this.auth = auth;
    }

    @GetMapping("/pendentes")
    public List<VideoResponse> pendentes(@RequestHeader(value = "Authorization", required = false) String cabecalho) {
        auth.exigirModerador(cabecalho);
        return videos.findByStatusOrderByDataPublicacaoDesc(StatusVideo.PENDENTE).stream()
                .map(VideoResponse::from)
                .toList();
    }

    @PostMapping("/videos/{id}")
    public VideoResponse moderar(@PathVariable Integer id,
                                 @RequestHeader(value = "Authorization", required = false) String cabecalho,
                                 @Valid @RequestBody ModeracaoRequest req) {
        Moderador moderador = auth.exigirModerador(cabecalho);
        return servico.moderar(id, moderador, req);
    }
}
