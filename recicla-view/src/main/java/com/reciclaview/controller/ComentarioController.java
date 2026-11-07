package com.reciclaview.controller;

import com.reciclaview.dto.ComentarioRequest;
import com.reciclaview.dto.ComentarioResponse;
import com.reciclaview.model.Comentario;
import com.reciclaview.model.StatusVideo;
import com.reciclaview.model.Usuario;
import com.reciclaview.model.Video;
import com.reciclaview.repository.ComentarioRepository;
import com.reciclaview.repository.VideoRepository;
import com.reciclaview.security.AutenticacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/videos/{videoId}/comentarios")
public class ComentarioController {

    private final ComentarioRepository comentarios;
    private final VideoRepository videos;
    private final AutenticacaoService auth;

    public ComentarioController(ComentarioRepository comentarios, VideoRepository videos, AutenticacaoService auth) {
        this.comentarios = comentarios;
        this.videos = videos;
        this.auth = auth;
    }

    @GetMapping
    public List<ComentarioResponse> listar(@PathVariable Integer videoId) {
        videoAprovado(videoId);
        return comentarios.findByVideo_IdOrderByDataComentarioAsc(videoId).stream()
                .map(ComentarioResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComentarioResponse comentar(@PathVariable Integer videoId,
                                       @RequestHeader(value = "Authorization", required = false) String cabecalho,
                                       @Valid @RequestBody ComentarioRequest req) {
        Usuario usuario = auth.exigirUsuario(cabecalho);
        Video video = videoAprovado(videoId);

        Comentario comentario = new Comentario();
        comentario.setUsuario(usuario);
        comentario.setVideo(video);
        comentario.setTexto(req.texto().trim());
        return ComentarioResponse.from(comentarios.save(comentario));
    }

    private Video videoAprovado(Integer id) {
        Video video = videos.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Vídeo não encontrado."));
        if (video.getStatus() != StatusVideo.APROVADO) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vídeo não encontrado.");
        }
        return video;
    }
}
