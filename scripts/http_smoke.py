
import json
import sys
import urllib.error
import urllib.request

base = sys.argv[1] if len(sys.argv) > 1 else "http://127.0.0.1:8085"
def call(method, path, expected, data=None):
    req = urllib.request.Request(base + path,
        data=None if data is None else json.dumps(data).encode(),
        method=method, headers={"Content-Type": "application/json", "Accept": "application/json"})
    try:
        with urllib.request.urlopen(req, timeout=5) as r:
            status, raw = r.status, r.read()
    except urllib.error.HTTPError as r:
        status, raw = r.code, r.read()
    assert status == expected, (method, path, expected, status, raw)
    print(f"{method:6} {path:28} {status}")
    return json.loads(raw) if raw else None

c = call("POST", "/clientes", 201, {"nome":"Cliente de Teste", "clienteDesde":"2026-11-27"})
p = call("POST", "/produtos", 201, {"nome":"Baozi de legumes", "preco":8.50, "estoque":True})
b = {"clienteId":c["id"], "produtoId":p["id"], "quantidade":3}
o = call("POST", "/pedidos", 201, b)
assert all(o[k] == v for k, v in b.items())
for key, obj in (("clientes",c),("produtos",p),("pedidos",o)):
    assert obj in call("GET", "/"+key, 200)
    assert obj == call("GET", f"/{key}/{obj['id']}", 200)
call("POST", "/pedidos", 400, b | {"quantidade":0})
call("POST", "/pedidos", 404, b | {"produtoId":9999999})
call("DELETE", f"/clientes/{c['id']}", 409)
call("DELETE", f"/produtos/{p['id']}", 409)
for key, obj in (("pedidos",o),("produtos",p),("clientes",c)):
    path=f"/{key}/{obj['id']}"
    call("DELETE", path, 204)
    call("GET", path, 404)
print("Todos os testes passaram.")
