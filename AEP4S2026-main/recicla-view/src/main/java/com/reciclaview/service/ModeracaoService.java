package com.reciclaview.service;

import com.reciclaview.dto.ModeracaoRequest;
import com.reciclaview.dto.VideoResponse;
import com.reciclaview.model.Decisao;
import com.reciclaview.model.Moderacao;
import com.reciclaview.model.Moderador;
import com.reciclaview.model.StatusVideo;
import com.reciclaview.model.Video;
import com.reciclaview.repository.ModeracaoRepository;
import com.reciclaview.repository.VideoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ModeracaoService {

    private final VideoRepository videos;
    private final ModeracaoRepository moderacoes;

    public ModeracaoService(VideoRepository videos, ModeracaoRepository moderacoes) {
        this.videos = videos;
        this.moderacoes = moderacoes;
    }

    @Transactional
    public VideoResponse moderar(Integer videoId, Moderador moderador, ModeracaoRequest req) {
        Video video = videos.findById(videoId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Vídeo não encontrado."));

        if (video.getStatus() != StatusVideo.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este vídeo já foi moderado.");
        }

        String justificativa = req.justificativa() == null ? "" : req.justificativa().trim();
        if (req.decisao() == Decisao.REPROVADO && justificativa.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe a justificativa para reprovar.");
        }

        Moderacao moderacao = new Moderacao();
        moderacao.setVideo(video);
        moderacao.setModerador(moderador);
        moderacao.setDecisao(req.decisao());
        moderacao.setJustificativa(justificativa.isEmpty() ? null : justificativa);
        moderacoes.save(moderacao);

        video.setStatus(StatusVideo.valueOf(req.decisao().name()));
        videos.save(video);

        return VideoResponse.from(video);
    }
}
