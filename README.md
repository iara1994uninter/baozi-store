# Baozi Store | API REST

API didática para cadastrar clientes, produtos e pedidos da Baozi Store.
Java 21, Spring Boot, Spring MVC, Spring Data JPA e banco relacional H2.

## Executar

Pré-requisitos: JDK 21 e Maven 3.9.x.

```sh
mvn clean package
java -jar target/baozi-store-1.0.0.jar
```

A API fica em `http://localhost:8085`. O H2 gera os arquivos em `./data`, que não fazem parte do repositório.
O servidor aceita apenas conexões locais. Os corpos de criação são JSON (`Content-Type: application/json`).

| Recurso | Criar | Listar | Consultar | Excluir |
|---|---|---|---|---|
| Clientes | POST /clientes | GET /clientes | GET /clientes/{id} | DELETE /clientes/{id} |
| Produtos | POST /produtos | GET /produtos | GET /produtos/{id} | DELETE /produtos/{id} |
| Pedidos | POST /pedidos | GET /pedidos | GET /pedidos/{id} | DELETE /pedidos/{id} |

Exemplos de criação, na ordem:

```json
{"nome":"Cliente Exemplo","clienteDesde":"2026-11-27"}
{"nome":"Baozi de legumes","preco":8.50,"estoque":true}
{"clienteId":1,"produtoId":1,"quantidade":3}
```

Use no pedido os IDs retornados nas criações. Cada pedido vincula um cliente, um produto e uma quantidade positiva. A indicação de estoque é booleana e não representa quantidade. A remoção de cliente ou produto referenciado retorna 409; remova primeiro o pedido. Criação retorna 201; consultas, 200; remoção bem-sucedida, 204; ID inexistente, 404; dados inválidos, 400. A atualização por PUT, opcional no enunciado, não foi implementada.
