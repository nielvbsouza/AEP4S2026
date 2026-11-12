package com.reciclaview.controller;

import com.reciclaview.dto.VideoRequest;
import com.reciclaview.dto.VideoResponse;
import com.reciclaview.model.Categoria;
import com.reciclaview.model.Moderacao;
import com.reciclaview.model.StatusVideo;
import com.reciclaview.model.Usuario;
import com.reciclaview.model.Video;
import com.reciclaview.repository.CategoriaRepository;
import com.reciclaview.repository.ModeracaoRepository;
import com.reciclaview.repository.VideoRepository;
import com.reciclaview.security.AutenticacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoRepository videos;
    private final CategoriaRepository categorias;
    private final ModeracaoRepository moderacoes;
    private final AutenticacaoService auth;

    public VideoController(VideoRepository videos, CategoriaRepository categorias,
                           ModeracaoRepository moderacoes, AutenticacaoService auth) {
        this.videos = videos;
        this.categorias = categorias;
        this.moderacoes = moderacoes;
        this.auth = auth;
    }

    @GetMapping
    public List<VideoResponse> listar(@RequestParam(required = false) Integer categoriaId,
                                      @RequestParam(required = false) String busca) {
        String termo = busca == null ? "" : busca.trim().toLowerCase();
        return videos.findByStatusOrderByDataPublicacaoDesc(StatusVideo.APROVADO).stream()
                .filter(v -> categoriaId == null
                        || v.getCategorias().stream().anyMatch(c -> c.getId().equals(categoriaId)))
                .filter(v -> termo.isEmpty() || v.getTitulo().toLowerCase().contains(termo))
                .map(VideoResponse::from)
                .toList();
    }

    @GetMapping("/meus")
    public List<VideoResponse> meus(@RequestHeader(value = "Authorization", required = false) String cabecalho) {
        Usuario usuario = auth.exigirUsuario(cabecalho);
        return videos.findByUsuario_IdOrderByDataPublicacaoDesc(usuario.getId()).stream()
                .map(v -> {
                    String justificativa = v.getStatus() == StatusVideo.REPROVADO
                            ? moderacoes.findFirstByVideo_IdOrderByIdDesc(v.getId())
                                .map(Moderacao::getJustificativa).orElse(null)
                            : null;
                    return VideoResponse.from(v, justificativa);
                })
                .toList();
    }

    @GetMapping("/{id}")
    public VideoResponse buscar(@PathVariable Integer id,
                                @RequestHeader(value = "Authorization", required = false) String cabecalho) {
        Video video = videos.findById(id).orElseThrow(this::naoEncontrado);
        if (video.getStatus() != StatusVideo.APROVADO) {
            Usuario usuario = auth.usuarioOpcional(cabecalho).orElseThrow(this::naoEncontrado);
            boolean dono = usuario.getId().equals(video.getUsuario().getId());
            if (!dono && !auth.ehModerador(usuario)) {
                throw naoEncontrado();
            }
        }
        return VideoResponse.from(video);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VideoResponse publicar(@RequestHeader(value = "Authorization", required = false) String cabecalho,
                                  @Valid @RequestBody VideoRequest req) {
        Usuario usuario = auth.exigirUsuario(cabecalho);

        Set<Integer> ids = req.categoriaIds();
        List<Categoria> encontradas = categorias.findAllById(ids);
        if (encontradas.size() != ids.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria inválida.");
        }

        Video video = new Video();
        video.setUsuario(usuario);
        video.setTitulo(req.titulo().trim());
        video.setDescricao(req.descricao() == null ? null : req.descricao().trim());
        video.setUrl(req.url().trim());
        video.setStatus(StatusVideo.PENDENTE);
        video.setCategorias(new HashSet<>(encontradas));
        return VideoResponse.from(videos.save(video));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id,
                        @RequestHeader(value = "Authorization", required = false) String cabecalho) {
        Usuario usuario = auth.exigirUsuario(cabecalho);
        Video video = videos.findById(id).orElseThrow(this::naoEncontrado);
        boolean dono = video.getUsuario().getId().equals(usuario.getId());
        if (!dono && !auth.ehModerador(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode excluir este vídeo.");
        }
        videos.delete(video);
    }

    private ResponseStatusException naoEncontrado() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Vídeo não encontrado.");
    }
}
