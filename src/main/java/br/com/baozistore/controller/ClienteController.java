package br.com.baozistore.controller;

import br.com.baozistore.model.Cliente;
import br.com.baozistore.repository.ClienteRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.net.URI;
import java.util.List;
import java.time.LocalDate;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/clientes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClienteController {
    private final ClienteRepository repositorio;

    public ClienteController(ClienteRepository repositorio) {
        this.repositorio = repositorio;
    }

    public record Entrada(@NotBlank @Size(max = 120) String nome,
                         @NotNull @PastOrPresent LocalDate clienteDesde) {}
    public record Resposta(Long id, String nome, LocalDate clienteDesde) {}

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resposta> criar(@Valid @RequestBody Entrada entrada) {
        var registro = repositorio.save(new Cliente(entrada.nome(), entrada.clienteDesde()));
        return ResponseEntity.created(URI.create("/clientes/" + registro.getId())).body(converter(registro));
    }

    @GetMapping
    public List<Resposta> listar() {
        return repositorio.findAll(Sort.by("id")).stream().map(ClienteController::converter).toList();
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

    private Cliente buscar(Long id) {
        return repositorio.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    private static Resposta converter(Cliente c) {
        return new Resposta(c.getId(), c.getNome(), c.getClienteDesde());
    }
}
