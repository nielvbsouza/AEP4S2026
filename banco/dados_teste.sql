-- =====================================================
-- RECICLA VIEW - Dados de teste (OPCIONAL)
-- Execute UMA vez, depois de estrutura.sql. Rodar de novo duplica os dados
-- (e falha nos e-mails, que sao unicos).
--
-- As contas abaixo NAO conseguem fazer login (a senha nao e um hash valido).
-- Elas existem apenas como autores dos videos de exemplo.
-- Para entrar no sistema, cadastre sua propria conta pelo site.
-- =====================================================

INSERT INTO usuario (nome, email, senha) VALUES
('Administrador', 'admin@reciclaview.com', '!conta-de-exemplo-sem-login'),
('Maria Silva',   'maria@email.com',       '!conta-de-exemplo-sem-login'),
('João Souza',    'joao@email.com',        '!conta-de-exemplo-sem-login');

INSERT INTO categoria (nome, descricao) VALUES
('Reciclagem',        'Como separar e reciclar materiais'),
('Meio Ambiente',     'Preservação e conscientização ambiental'),
('Sustentabilidade',  'Hábitos e práticas sustentáveis'),
('Reaproveitamento',  'Ideias para reutilizar objetos e materiais');

INSERT INTO moderador (usuario_id) VALUES (1);

INSERT INTO video (usuario_id, titulo, descricao, url, status) VALUES
(2, 'Como separar o lixo em casa',
    'Passo a passo da coleta seletiva.',
    'https://www.youtube.com/watch?v=exemplo1', 'APROVADO'),
(3, 'Horta com garrafas PET',
    'Reaproveitando garrafas para plantar temperos.',
    'https://www.youtube.com/watch?v=exemplo2', 'PENDENTE');

INSERT INTO video_categoria (video_id, categoria_id) VALUES
(1, 1),
(1, 3),
(2, 4),
(2, 2);

INSERT INTO comentario (usuario_id, video_id, texto) VALUES
(3, 1, 'Muito útil, vou começar a separar o lixo hoje!');

INSERT INTO moderacao (video_id, moderador_id, decisao, justificativa) VALUES
(1, 1, 'APROVADO', 'Conteúdo adequado ao tema do site.');
