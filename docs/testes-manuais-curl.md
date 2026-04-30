# Testes manuais de LCRUD users e notes com curl

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

---

## 9) Create (POST /notes)

Observacao:

- `userId` precisa existir na base (ex.: crie um usuario antes).

```bash
curl -i -X POST http://localhost:8080/notes \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Primeira nota\",\"content\":\"Conteudo da nota\",\"userId\":1}"
```

Esperado:

- `201 Created`
- Body com `id`, `title`, `content`, `userId`

## 10) List (GET /notes)

```bash
curl -i http://localhost:8080/notes
```

Esperado:

- `200 OK`
- Lista JSON de notas

## 11) Read por id (GET /notes/{id})

```bash
curl -i http://localhost:8080/notes/1
```

Esperado:

- `200 OK` quando existe
- `404 Not Found` quando nao existe

## 12) Update (PUT /notes/{id})

```bash
curl -i -X PUT http://localhost:8080/notes/1 \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Nota atualizada\",\"content\":\"Novo conteudo\"}"
```

Esperado:

- `200 OK`
- Body atualizado (`id`, `title`, `content`, `userId`)

## 13) Delete (DELETE /notes/{id})

```bash
curl -i -X DELETE http://localhost:8080/notes/1
```

Esperado:

- `204 No Content`
- Nova busca `GET /notes/1` deve retornar `404`

## 14) Cenarios de erro para estudar (notes)

### 14.1 Criar nota com `userId` inexistente

```bash
curl -i -X POST http://localhost:8080/notes \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Nota orfa\",\"content\":\"Sem usuario\",\"userId\":9999}"
```

Esperado:

- `404 Not Found`

### 14.2 Payload invalido

```bash
curl -i -X POST http://localhost:8080/notes \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"\",\"content\":\"\",\"userId\":null}"
```

Esperado:

- `400 Bad Request`
- JSON com mensagem e erros de validacao por campo

### 14.3 Update em id inexistente

```bash
curl -i -X PUT http://localhost:8080/notes/9999 \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"x\",\"content\":\"y\"}"
```

Esperado:

- `404 Not Found`

## 15) Sequencia rapida de verificacao ponta a ponta (notes)

1. `POST /notes` (criar)
2. `GET /notes` (listar)
3. `GET /notes/{id}` (ler)
4. `PUT /notes/{id}` (atualizar)
5. `DELETE /notes/{id}` (deletar)
6. `GET /notes/{id}` (confirmar 404)

