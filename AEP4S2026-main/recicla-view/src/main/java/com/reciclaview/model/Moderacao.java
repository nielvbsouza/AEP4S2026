package com.reciclaview.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "moderacao")
public class Moderacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(optional = false)
    @JoinColumn(name = "moderador_id", nullable = false)
    private Moderador moderador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Decisao decisao;

    @Column(length = 500)
    private String justificativa;

    @Column(name = "data_moderacao", insertable = false, updatable = false)
    private LocalDateTime dataModeracao;

    public Moderacao() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Video getVideo() { return video; }
    public void setVideo(Video video) { this.video = video; }

    public Moderador getModerador() { return moderador; }
    public void setModerador(Moderador moderador) { this.moderador = moderador; }

    public Decisao getDecisao() { return decisao; }
    public void setDecisao(Decisao decisao) { this.decisao = decisao; }

    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }

    public LocalDateTime getDataModeracao() { return dataModeracao; }
}
