package br.com.baozistore.controller;

import br.com.baozistore.model.Produto;
import br.com.baozistore.repository.ProdutoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.net.URI;
import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/produtos", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProdutoController {
    private final ProdutoRepository repositorio;

    public ProdutoController(ProdutoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public record Entrada(@NotBlank @Size(max = 120) String nome,
                         @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal preco,
                         @NotNull Boolean estoque) {}
    public record Resposta(Long id, String nome, BigDecimal preco, boolean estoque) {}

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resposta> criar(@Valid @RequestBody Entrada entrada) {
        var registro = repositorio.save(new Produto(entrada.nome(), entrada.preco(), entrada.estoque()));
        return ResponseEntity.created(URI.create("/produtos/" + registro.getId())).body(converter(registro));
    }

    @GetMapping
    public List<Resposta> listar() {
        return repositorio.findAll(Sort.by("id")).stream().map(ProdutoController::converter).toList();
    }

    @GetMapping("/{id}")
    public Resposta consultar(@PathVariable Long id) {
        return converter(buscar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        repositorio.delete(buscar(id));
        return ResponseEntity.noContent().build();
    }

    private Produto buscar(Long id) {
        return repositorio.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
    }

    private static Resposta converter(Produto c) {
        return new Resposta(c.getId(), c.getNome(), c.getPreco(), c.isEstoque());
    }
}
