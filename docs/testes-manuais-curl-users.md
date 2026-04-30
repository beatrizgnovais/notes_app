# Testes manuais de LCRUD users com curl

## 1) Preparacao

Base URL local (ajuste a porta):

`http://localhost:8080`

Observacao:

- Se estiver usando Docker Compose deste projeto, a API ja sobe mapeada em `8080`.
- O banco continua em `5432` para conexao via DBeaver.
- Autenticacao foi removida para facilitar os testes manuais de CRUD.

Subir ambiente com Docker:

```bash
docker compose up -d --build
```

## 2) Create (POST /users)

```bash
curl -i -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"ana@example.com\",\"password\":\"123456\"}"
```

Esperado:

- `201 Created`
- Body com `id` e `email`

## 3) List (GET /users)

```bash
curl -i http://localhost:8080/users
```

Esperado:

- `200 OK`
- Lista JSON de usuarios

## 4) Read por id (GET /users/{id})

```bash
curl -i http://localhost:8080/users/1
```

Esperado:

- `200 OK` quando existe
- `404 Not Found` quando nao existe

## 5) Update (PUT /users/{id})

```bash
curl -i -X PUT http://localhost:8080/users/1 \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"ana.nova@example.com\",\"password\":\"abcdef\"}"
```

Esperado:

- `200 OK`
- Body atualizado (`id`, `email`)

## 6) Delete (DELETE /users/{id})

```bash
curl -i -X DELETE http://localhost:8080/users/1
```

Esperado:

- `204 No Content`
- Nova busca `GET /users/1` deve retornar `404`

## 7) Cenarios de erro para estudar

### 7.1 Email duplicado

1. Criar usuario A com `ana@example.com`
2. Tentar criar outro com mesmo e-mail

Esperado:

- `409 Conflict`

### 7.2 Payload invalido

```bash
curl -i -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"invalido\",\"password\":\"123\"}"
```

Esperado:

- `400 Bad Request`
- JSON com mensagem e erros de validacao por campo

### 7.3 Update em id inexistente

```bash
curl -i -X PUT http://localhost:8080/users/9999 \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"x@example.com\",\"password\":\"123456\"}"
```

Esperado:

- `404 Not Found`

## 8) Sequencia rapida de verificacao ponta a ponta

1. `POST /users` (criar)
2. `GET /users` (listar)
3. `GET /users/{id}` (ler)
4. `PUT /users/{id}` (atualizar)
5. `DELETE /users/{id}` (deletar)
6. `GET /users/{id}` (confirmar 404)

