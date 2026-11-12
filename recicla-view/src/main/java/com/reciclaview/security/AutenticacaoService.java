package com.reciclaview.security;

import com.reciclaview.model.Moderador;
import com.reciclaview.model.Usuario;
import com.reciclaview.repository.ModeradorRepository;
import com.reciclaview.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AutenticacaoService {

    private final TokenService tokens;
    private final UsuarioRepository usuarios;
    private final ModeradorRepository moderadores;

    public AutenticacaoService(TokenService tokens, UsuarioRepository usuarios, ModeradorRepository moderadores) {
        this.tokens = tokens;
        this.usuarios = usuarios;
        this.moderadores = moderadores;
    }

    public Optional<Usuario> usuarioOpcional(String cabecalho) {
        if (cabecalho == null || !cabecalho.startsWith("Bearer ")) {
            return Optional.empty();
        }
        return tokens.validar(cabecalho.substring(7))
                .flatMap(usuarios::findById)
                .filter(u -> Boolean.TRUE.equals(u.getAtivo()));
    }

    public Usuario exigirUsuario(String cabecalho) {
        return usuarioOpcional(cabecalho).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Faça login para continuar."));
    }

    public Moderador exigirModerador(String cabecalho) {
        Usuario usuario = exigirUsuario(cabecalho);
        return moderadores.findByUsuario_Id(usuario.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas moderadores podem fazer isso."));
    }

    public boolean ehModerador(Usuario usuario) {
        return moderadores.existsByUsuario_Id(usuario.getId());
    }
}
