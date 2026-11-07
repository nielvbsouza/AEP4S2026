-- =====================================================
-- RECICLA VIEW - Estrutura do banco (MySQL / MariaDB)
-- Execute no phpMyAdmin da Hostinger, na aba SQL, com o banco ja selecionado.
-- Pode ser executado mais de uma vez (usa IF NOT EXISTS).
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS usuario (
    id            INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    senha         VARCHAR(255) NOT NULL,
    data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo         TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS categoria (
    id        INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nome      VARCHAR(80) NOT NULL,
    descricao VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_categoria_nome (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS moderador (
    id            INT UNSIGNED NOT NULL AUTO_INCREMENT,
    usuario_id    INT UNSIGNED NOT NULL,
    data_promocao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_moderador_usuario (usuario_id),
    CONSTRAINT fk_moderador_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS video (
    id              INT UNSIGNED NOT NULL AUTO_INCREMENT,
    usuario_id      INT UNSIGNED NOT NULL,
    titulo          VARCHAR(150) NOT NULL,
    descricao       TEXT NULL,
    url             VARCHAR(500) NOT NULL,
    status          ENUM('PENDENTE','APROVADO','REPROVADO') NOT NULL DEFAULT 'PENDENTE',
    data_publicacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_video_usuario (usuario_id),
    KEY idx_video_status (status),
    CONSTRAINT fk_video_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS video_categoria (
    video_id     INT UNSIGNED NOT NULL,
    categoria_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (video_id, categoria_id),
    KEY idx_vc_categoria (categoria_id),
    CONSTRAINT fk_vc_video
        FOREIGN KEY (video_id) REFERENCES video (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_vc_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS comentario (
    id              INT UNSIGNED NOT NULL AUTO_INCREMENT,
    usuario_id      INT UNSIGNED NOT NULL,
    video_id        INT UNSIGNED NOT NULL,
    texto           VARCHAR(1000) NOT NULL,
    data_comentario DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_comentario_video (video_id),
    KEY idx_comentario_usuario (usuario_id),
    CONSTRAINT fk_comentario_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_comentario_video
        FOREIGN KEY (video_id) REFERENCES video (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS moderacao (
    id              INT UNSIGNED NOT NULL AUTO_INCREMENT,
    video_id        INT UNSIGNED NOT NULL,
    moderador_id    INT UNSIGNED NOT NULL,
    decisao         ENUM('APROVADO','REPROVADO') NOT NULL,
    justificativa   VARCHAR(500) NULL,
    data_moderacao  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_moderacao_video (video_id),
    KEY idx_moderacao_moderador (moderador_id),
    CONSTRAINT fk_moderacao_video
        FOREIGN KEY (video_id) REFERENCES video (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_moderacao_moderador
        FOREIGN KEY (moderador_id) REFERENCES moderador (id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
