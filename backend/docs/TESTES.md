# Testes de integração: service-discovery, api-gateway e sala-service

Este documento registra como testamos a comunicação entre os microsserviços pelo Eureka e pelo API Gateway.

- **Data do teste:** 21/11/2026
- **Resultado:** ✅ todos os 11 testes passaram
- **Fora do escopo:** `gerenciamento-salas` (será testado depois, ver [Pendências](#pendências))

## Arquitetura testada

```
cliente ──► api-gateway (:8080) ──lb://sala-service──► sala-service (:8081) ──► PostgreSQL (:5432)
                 │                                          │
                 └──────── registram-se no ─────────────────┘
                          service-discovery / Eureka (:8761)
```

O cliente chama apenas o gateway. O gateway consulta o Eureka para descobrir o endereço do `sala-service` (rota `lb://sala-service`) e repassa a requisição.

| Serviço | Porta | Nome no Eureka |
|---|---|---|
| service-discovery | 8761 | (é o próprio servidor) |
| api-gateway | 8080 | `API-GATEWAY` |
| sala-service | 8081 | `SALA-SERVICE` |
| PostgreSQL (Docker) | 5432 | — |

## Pré-requisitos

- Java 21
- Docker (para o PostgreSQL)
- `curl` (os comandos abaixo foram executados no Git Bash)

## Como subir o ambiente

A ordem importa: o Eureka precisa estar no ar antes dos clientes, ou eles ficam tentando se registrar até ele subir.

```bash
# 1. Banco de dados do sala-service
cd sala-service
docker compose up -d

# 2. Service discovery (Eureka) — em um terminal
cd service-discovery
./mvnw spring-boot:run

# 3. sala-service — em outro terminal
cd sala-service
./mvnw spring-boot:run

# 4. api-gateway — em outro terminal
cd api-gateway
./mvnw spring-boot:run
```

Cada serviço está pronto quando aparece a linha `Started ...Application` no log.

> **Atenção:** depois que tudo sobe, o gateway pode levar **até ~1 minuto** para enxergar o `sala-service`, porque o cliente Eureka atualiza o cache a cada 30 segundos. Nesse intervalo, chamadas pelo gateway podem retornar `503`.

## Problema encontrado e correção

Na primeira execução, toda chamada pelo gateway retornava **500**, embora o `sala-service` respondesse normalmente acessado direto na porta 8081. O log do gateway mostrava:

```
java.net.UnknownHostException: Failed to resolve 'DESKTOP-XXXX.mshome.net'
```

**Causa:** por padrão, o serviço se registra no Eureka com o *hostname* da máquina. O gateway (que usa Netty) não conseguia resolver esse nome no Windows.

**Correção:** fazer o serviço se registrar pelo **IP**, adicionando ao `application.properties`:

```properties
eureka.instance.prefer-ip-address=true
```

A linha foi adicionada no `sala-service` e também no `gerenciamento-salas`, que vai ter o mesmo problema. Depois disso, o Eureka passou a registrar o `sala-service` com o IP da máquina e o roteamento funcionou.

## Testes executados

Todas as chamadas abaixo, exceto o teste 1, passam **pelo gateway** (`http://localhost:8080`), e não direto no `sala-service`. Assim validamos o caminho completo: gateway → Eureka → sala-service → banco.

| # | Teste | Requisição | Esperado | Obtido |
|---|---|---|---|---|
| 1 | Eureka no ar e serviços registrados | `GET http://localhost:8761/eureka/apps` | `API-GATEWAY` e `SALA-SERVICE` com status `UP` | ✅ |
| 2 | Cadastrar sala | `POST /salas` | 201 + sala com `estadoSala: DISPONIVEL` | ✅ 201 |
| 3 | Listar salas | `GET /salas` | 200 | ✅ 200 |
| 4 | Buscar por ID | `GET /salas/{id}` | 200 + sala criada no teste 2 | ✅ 200 |
| 5 | Filtrar | `GET /salas/filtro?tipo=LABORATORIO&capacidade=30` | 200 + lista contendo a sala | ✅ 200 |
| 6 | Atualizar | `PUT /salas/{id}` | 200 + capacidade e tipo alterados | ✅ 200 |
| 7 | Nome duplicado | `POST /salas` com mesmo nome | 409 Conflict | ✅ 409 |
| 8 | Dados inválidos | `POST /salas` com nome vazio e capacidade 0 | 400 Bad Request | ✅ 400 |
| 9 | Remover | `DELETE /salas/{id}` | 204 No Content | ✅ 204 |
| 10 | Buscar sala removida | `GET /salas/{id}` | 404 Not Found | ✅ 404 |
| 11 | Rota não mapeada no gateway | `GET /nao-existe` | 404 Not Found | ✅ 404 |

## Exemplos de requisição por método

Os exemplos abaixo usam **dados fictícios** e servem como modelo para testar a API em qualquer cliente HTTP (Postman, Insomnia, extensão REST Client etc.).

- **URL base:** `http://localhost:8080` (sempre pelo gateway)
- **Header** para requisições com corpo: `Content-Type: application/json`
- **Valores aceitos:**
  - `tipoSala`: `SALA_COMUM`, `LABORATORIO` ou `AUDITORIO`
  - `estadoSala`: `DISPONIVEL`, `RESERVADA` ou `EM_MANUTENCAO`. Não é enviado no corpo; toda sala nova começa como `DISPONIVEL`.

O ID `3f2b8c1e-7a4d-4e9b-9c6a-1d2e3f4a5b6c` usado nos exemplos é inventado. Ao testar, use o `id` retornado pelo cadastro.

### POST: cadastrar sala

```http
POST http://localhost:8080/salas
Content-Type: application/json

{
  "nome": "Laboratório de Informática 01",
  "capacidade": 35,
  "tipoSala": "LABORATORIO"
}
```

Resposta `201 Created` (o header `Location` traz a URL da sala criada):

```json
{
  "id": "3f2b8c1e-7a4d-4e9b-9c6a-1d2e3f4a5b6c",
  "nome": "Laboratório de Informática 01",
  "capacidade": 35,
  "tipoSala": "LABORATORIO",
  "estadoSala": "DISPONIVEL"
}
```

Erros possíveis:

| Situação | Exemplo de corpo | Status |
|---|---|---|
| Campo obrigatório vazio ou inválido | `{"nome": "", "capacidade": 0}` | `400 Bad Request` |
| Nome já cadastrado (sem diferenciar maiúsculas) | `{"nome": "laboratório de informática 01", "capacidade": 20, "tipoSala": "SALA_COMUM"}` | `409 Conflict` |

### GET: listar todas as salas

```http
GET http://localhost:8080/salas
```

Resposta `200 OK`:

```json
[
  {
    "id": "3f2b8c1e-7a4d-4e9b-9c6a-1d2e3f4a5b6c",
    "nome": "Laboratório de Informática 01",
    "capacidade": 35,
    "tipoSala": "LABORATORIO",
    "estadoSala": "DISPONIVEL"
  },
  {
    "id": "8a7d6c5b-4e3f-4a2b-8c1d-0e9f8a7b6c5d",
    "nome": "Auditório Central",
    "capacidade": 200,
    "tipoSala": "AUDITORIO",
    "estadoSala": "RESERVADA"
  }
]
```

### GET: buscar sala por ID

```http
GET http://localhost:8080/salas/3f2b8c1e-7a4d-4e9b-9c6a-1d2e3f4a5b6c
```

Resposta `200 OK`:

```json
{
  "id": "3f2b8c1e-7a4d-4e9b-9c6a-1d2e3f4a5b6c",
  "nome": "Laboratório de Informática 01",
  "capacidade": 35,
  "tipoSala": "LABORATORIO",
  "estadoSala": "DISPONIVEL"
}
```

Erros possíveis: `404 Not Found` se o ID não existir, `400 Bad Request` se o ID não for um UUID válido.

### GET: filtrar salas

Todos os parâmetros são opcionais e podem ser combinados:

| Parâmetro | Significado | Exemplo |
|---|---|---|
| `tipo` | Tipo da sala | `LABORATORIO` |
| `capacidade` | Capacidade **mínima** (≥ 1) | `30` |
| `estado` | Estado atual da sala | `DISPONIVEL` |

```http
GET http://localhost:8080/salas/filtro?tipo=LABORATORIO&capacidade=30&estado=DISPONIVEL
```

Resposta `200 OK` (lista vazia `[]` se nada combinar):

```json
[
  {
    "id": "3f2b8c1e-7a4d-4e9b-9c6a-1d2e3f4a5b6c",
    "nome": "Laboratório de Informática 01",
    "capacidade": 35,
    "tipoSala": "LABORATORIO",
    "estadoSala": "DISPONIVEL"
  }
]
```

Erro possível: `400 Bad Request` se um valor de enum não existir (ex.: `tipo=COZINHA`).

### PUT: atualizar sala

O corpo é o mesmo do cadastro, com todos os campos obrigatórios. O `estadoSala` não muda por esta rota.

```http
PUT http://localhost:8080/salas/3f2b8c1e-7a4d-4e9b-9c6a-1d2e3f4a5b6c
Content-Type: application/json

{
  "nome": "Laboratório de Informática 01",
  "capacidade": 40,
  "tipoSala": "LABORATORIO"
}
```

Resposta `200 OK`:

```json
{
  "id": "3f2b8c1e-7a4d-4e9b-9c6a-1d2e3f4a5b6c",
  "nome": "Laboratório de Informática 01",
  "capacidade": 40,
  "tipoSala": "LABORATORIO",
  "estadoSala": "DISPONIVEL"
}
```

Erros possíveis: `400` (dados inválidos), `404` (ID não existe), `409` (nome já usado por **outra** sala).

### DELETE: remover sala

```http
DELETE http://localhost:8080/salas/3f2b8c1e-7a4d-4e9b-9c6a-1d2e3f4a5b6c
```

Resposta `204 No Content`, sem corpo.

Erro possível: `404 Not Found` se o ID não existir.

### Formato das respostas de erro

Os erros `404` e `409` vêm neste formato:

```json
{
  "timestamp": "2026-11-21T14:30:00.000",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Sala com id 3f2b8c1e-7a4d-4e9b-9c6a-1d2e3f4a5b6c não encontrada."
}
```

### Exemplos de resposta obtidos

Cadastro (teste 2), `201 Created`:

```json
{"id":"9812f255-6d35-4d86-93e7-4bc4c51edac5","nome":"Teste Gateway 12422","capacidade":40,"tipoSala":"LABORATORIO","estadoSala":"DISPONIVEL"}
```

Nome duplicado (teste 7), `409 Conflict`:

```json
{"timestamp":"2026-11-21T13:15:26.9502641","status":409,"erro":"Conflict","mensagem":"Já existe uma sala cadastrada com o nome 'Teste Gateway 12422'."}
```

Sala removida (teste 10), `404 Not Found`:

```json
{"timestamp":"2026-11-21T13:15:27.3291209","status":404,"erro":"Not Found","mensagem":"Sala com id 9812f255-6d35-4d86-93e7-4bc4c51edac5 não encontrada."}
```

## Pendências

### gerenciamento-salas (não testado)

O módulo já está configurado como cliente do Eureka (porta `8082`, nome `gerenciamento-salas`, `prefer-ip-address=true`). O gateway já tem a rota `/api/** → lb://gerenciamento-salas`. Para testar, ainda falta:

- Criar o banco `gerenciamento_salas` no PostgreSQL (usuário e senha `postgres`). O serviço usa `ddl-auto=validate` e não tem migrations, então as tabelas precisam existir antes.
- Definir a variável de ambiente `GROQ_API_KEY`, que o Spring AI exige para subir.
- O módulo não tem Maven Wrapper (`mvnw`). Rode pelo IntelliJ ou com `../sala-service/mvnw -f pom.xml spring-boot:run`.
