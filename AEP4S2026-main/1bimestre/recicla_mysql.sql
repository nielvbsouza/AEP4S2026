CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE moderador (
    id BIGINT PRIMARY KEY,
    CONSTRAINT fk_moderador_usuario
        FOREIGN KEY (id) REFERENCES usuario(id)
        ON DELETE CASCADE
);

CREATE TABLE categoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE video (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    url VARCHAR(500) NOT NULL,
    duracao INT NOT NULL,
    data_publicacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDENTE','APROVADO','REPROVADO') NOT NULL DEFAULT 'PENDENTE',
    autor_id BIGINT NOT NULL,
    CONSTRAINT fk_video_autor
        FOREIGN KEY (autor_id) REFERENCES usuario(id)
        ON DELETE RESTRICT
);

CREATE TABLE video_categoria (
    video_id BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    PRIMARY KEY (video_id, categoria_id),
    CONSTRAINT fk_video_categoria_video
        FOREIGN KEY (video_id) REFERENCES video(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_video_categoria_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria(id)
        ON DELETE CASCADE
);

CREATE TABLE comentario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    texto TEXT NOT NULL,
    data TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    autor_id BIGINT NOT NULL,
    video_id BIGINT NOT NULL,
    CONSTRAINT fk_comentario_autor
        FOREIGN KEY (autor_id) REFERENCES usuario(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_comentario_video
        FOREIGN KEY (video_id) REFERENCES video(id)
        ON DELETE CASCADE
);

CREATE TABLE moderacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status ENUM('PENDENTE','APROVADO','REPROVADO') NOT NULL DEFAULT 'PENDENTE',
    data_revisao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    moderador_id BIGINT NOT NULL,
    video_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_moderacao_moderador
        FOREIGN KEY (moderador_id) REFERENCES moderador(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_moderacao_video
        FOREIGN KEY (video_id) REFERENCES video(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_video_autor ON video(autor_id);
CREATE INDEX idx_comentario_autor ON comentario(autor_id);
CREATE INDEX idx_comentario_video ON comentario(video_id);
CREATE INDEX idx_video_categoria_categoria ON video_categoria(categoria_id);
CREATE INDEX idx_moderacao_moderador ON moderacao(moderador_id);

INSERT INTO usuario (nome, email) VALUES
('João Silva', 'joao@email.com'),
('Maria Souza', 'maria@email.com'),
('Carlos Oliveira', 'carlos@email.com');

INSERT INTO moderador (id)
SELECT id FROM usuario WHERE email = 'carlos@email.com';

INSERT INTO categoria (nome) VALUES
('Reciclagem'),
('Meio Ambiente'),
('Sustentabilidade');

INSERT INTO video (titulo, url, duracao, status, autor_id)
SELECT 'Como separar materiais recicláveis',
       'https://exemplo.com/video1',
       180,
       'APROVADO',
       id
FROM usuario
WHERE email = 'joao@email.com';

INSERT INTO video (titulo, url, duracao, status, autor_id)
SELECT 'Importância da reciclagem',
       'https://exemplo.com/video2',
       240,
       'PENDENTE',
       id
FROM usuario
WHERE email = 'maria@email.com';

INSERT INTO video_categoria (video_id, categoria_id)
SELECT v.id, c.id
FROM video v
CROSS JOIN categoria c
WHERE v.titulo = 'Como separar materiais recicláveis'
  AND c.nome IN ('Reciclagem', 'Meio Ambiente');

INSERT INTO video_categoria (video_id, categoria_id)
SELECT v.id, c.id
FROM video v
CROSS JOIN categoria c
WHERE v.titulo = 'Importância da reciclagem'
  AND c.nome IN ('Reciclagem', 'Sustentabilidade');

INSERT INTO comentario (texto, autor_id, video_id)
SELECT 'Ótimo conteúdo!', u.id, v.id
FROM usuario u
CROSS JOIN video v
WHERE u.email = 'maria@email.com'
  AND v.titulo = 'Como separar materiais recicláveis';

INSERT INTO moderacao (status, moderador_id, video_id)
SELECT 'APROVADO', m.id, v.id
FROM moderador m
JOIN usuario u ON u.id = m.id
JOIN video v ON v.titulo = 'Como separar materiais recicláveis'
WHERE u.email = 'carlos@email.com';

SELECT v.id, v.titulo, v.url, v.duracao, v.status, u.nome AS autor
FROM video v
JOIN usuario u ON u.id = v.autor_id
ORDER BY v.id;
