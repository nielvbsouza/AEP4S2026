package com.reciclaview.controller;

import com.reciclaview.dto.AuthResponse;
import com.reciclaview.dto.LoginRequest;
import com.reciclaview.dto.RegistroRequest;
import com.reciclaview.dto.UsuarioResponse;
import com.reciclaview.model.Usuario;
import com.reciclaview.repository.UsuarioRepository;
import com.reciclaview.security.AutenticacaoService;
import com.reciclaview.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final TokenService tokens;
    private final AutenticacaoService auth;

    public AuthController(UsuarioRepository usuarios, PasswordEncoder encoder,
                          TokenService tokens, AutenticacaoService auth) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.tokens = tokens;
        this.auth = auth;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registrar(@Valid @RequestBody RegistroRequest req) {
        String email = req.email().trim().toLowerCase();
        if (usuarios.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está cadastrado.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(req.nome().trim());
        usuario.setEmail(email);
        usuario.setSenha(encoder.encode(req.senha()));
        usuario = usuarios.save(usuario);
        return resposta(usuario);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        Usuario usuario = usuarios.findByEmail(req.email().trim().toLowerCase())
                .filter(u -> Boolean.TRUE.equals(u.getAtivo()) && encoder.matches(req.senha(), u.getSenha()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos."));
        return resposta(usuario);
    }

    @GetMapping("/eu")
    public UsuarioResponse eu(@RequestHeader(value = "Authorization", required = false) String cabecalho) {
        Usuario usuario = auth.exigirUsuario(cabecalho);
        return new UsuarioResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), auth.ehModerador(usuario));
    }

    private AuthResponse resposta(Usuario usuario) {
        UsuarioResponse dados = new UsuarioResponse(
                usuario.getId(), usuario.getNome(), usuario.getEmail(), auth.ehModerador(usuario));
        return new AuthResponse(tokens.gerar(usuario.getId()), dados);
    }
}
