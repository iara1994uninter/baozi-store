package br.com.baozistore.controller;

import br.com.baozistore.model.Pedido;
import br.com.baozistore.model.Cliente;
import br.com.baozistore.model.Produto;
import br.com.baozistore.repository.PedidoRepository;
import br.com.baozistore.repository.ClienteRepository;
import br.com.baozistore.repository.ProdutoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/pedidos", produces = MediaType.APPLICATION_JSON_VALUE)
public class PedidoController {
    private final PedidoRepository repositorio;
    private final ClienteRepository clientes;
    private final ProdutoRepository produtos;

    public PedidoController(PedidoRepository repositorio, ClienteRepository clientes, ProdutoRepository produtos) {
        this.repositorio = repositorio;
        this.clientes = clientes;
        this.produtos = produtos;
    }

    public record Entrada(@NotNull @Positive Long clienteId, @NotNull @Positive Long produtoId,
                         @NotNull @Positive Integer quantidade) {}
    public record Resposta(Long id, Long clienteId, Long produtoId, Integer quantidade) {}

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Resposta> criar(@Valid @RequestBody Entrada entrada) {
        Cliente cliente = clientes.findById(entrada.clienteId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        Produto produto = produtos.findById(entrada.produtoId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        var registro = repositorio.save(new Pedido(cliente, produto, entrada.quantidade()));
        return ResponseEntity.created(URI.create("/pedidos/" + registro.getId())).body(converter(registro));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<Resposta> listar() {
        return repositorio.findAll(Sort.by("id")).stream().map(PedidoController::converter).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public Resposta consultar(@PathVariable Long id) {
        return converter(buscar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        repositorio.delete(buscar(id));
        return ResponseEntity.noContent().build();
    }

    private Pedido buscar(Long id) {
        return repositorio.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    private static Resposta converter(Pedido c) {
        return new Resposta(c.getId(), c.getCliente().getId(), c.getProduto().getId(), c.getQuantidade());
    }
}
