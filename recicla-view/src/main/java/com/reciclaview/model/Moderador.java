package com.reciclaview.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "moderador")
public class Moderador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "data_promocao", insertable = false, updatable = false)
    private LocalDateTime dataPromocao;

    public Moderador() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getDataPromocao() { return dataPromocao; }
}
