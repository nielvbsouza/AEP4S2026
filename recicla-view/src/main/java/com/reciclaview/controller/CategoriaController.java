package com.reciclaview.controller;

import com.reciclaview.dto.CategoriaRequest;
import com.reciclaview.dto.CategoriaResponse;
import com.reciclaview.model.Categoria;
import com.reciclaview.repository.CategoriaRepository;
import com.reciclaview.security.AutenticacaoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaRepository categorias;
    private final AutenticacaoService auth;

    public CategoriaController(CategoriaRepository categorias, AutenticacaoService auth) {
        this.categorias = categorias;
        this.auth = auth;
    }

    @GetMapping
    public List<CategoriaResponse> listar() {
        return categorias.findAll(Sort.by("nome")).stream().map(CategoriaResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse criar(@RequestHeader(value = "Authorization", required = false) String cabecalho,
                                   @Valid @RequestBody CategoriaRequest req) {
        auth.exigirModerador(cabecalho);
        String nome = req.nome().trim();
        if (categorias.existsByNomeIgnoreCase(nome)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma categoria com esse nome.");
        }
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        categoria.setDescricao(req.descricao() == null ? null : req.descricao().trim());
        return CategoriaResponse.from(categorias.save(categoria));
    }
}
