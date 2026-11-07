-- =====================================================
-- RECICLA VIEW - Tornar um usuario moderador
--
-- 1) Cadastre sua conta pelo site (tela "Cadastrar").
-- 2) Troque o e-mail abaixo pelo e-mail que voce cadastrou.
-- 3) Execute no phpMyAdmin (aba SQL).
-- 4) Saia do site e entre de novo: o menu "Moderação" vai aparecer.
-- =====================================================

INSERT INTO moderador (usuario_id)
SELECT id FROM usuario WHERE email = 'COLOQUE_SEU_EMAIL_AQUI';

-- Para conferir:
-- SELECT u.id, u.nome, u.email FROM moderador m JOIN usuario u ON u.id = m.usuario_id;
