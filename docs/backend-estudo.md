# Guia de estudo backend do projeto

## 1) O que foi implementado agora

Foi implementado o LCRUD de `users` com:

- `POST /users` para criar
- `GET /users` para listar
- `GET /users/{id}` para buscar por id
- `PUT /users/{id}` para atualizar
- `DELETE /users/{id}` para remover

Camadas usadas:

- Entrada HTTP: `adapter/input/web/controller/UserController.kt`
- Aplicacao (casos de uso): `application/port/input/UserUseCase.kt`
- Regras de orquestracao: `application/service/UserService.kt`
- Porta de persistencia: `application/port/output/UserRepositoryPort.kt`
- Adapter de persistencia: `adapter/output/persistence/UserPersistenceAdapter.kt`
- Repositorio JPA: `repository/UserRepository.kt`

## 2) Hexagonal e Clean Architecture no contexto do projeto

Na pratica, as duas abordagens aqui convergem para o mesmo objetivo:

- Dominio e aplicacao nao dependem do framework web.
- O controller conhece o caso de uso, nao conhece SQL/JPA diretamente.
- Persistencia entra como detalhe de infraestrutura via porta (`UserRepositoryPort`).

Fluxo:

1. Requisicao HTTP entra no controller.
2. Controller converte DTO em command.
3. Use case/service aplica regra de negocio.
4. Service usa porta de saida.
5. Adapter da porta chama JPA/Postgres.
6. Resultado volta para o controller como resposta HTTP.

Isso facilita trocar banco/framework sem quebrar regra de negocio central.

## 3) HTTP methods e LCRUD

- `POST` cria recurso novo.
  - Retorno esperado: `201 Created`.
- `GET` consulta recurso.
  - `200 OK` para sucesso.
- `PUT` atualiza recurso existente.
  - `200 OK` com recurso atualizado.
- `DELETE` remove recurso.
  - `204 No Content`.

Erros esperados no projeto:

- `400 Bad Request`: validacao de entrada falhou.
- `404 Not Found`: id inexistente.
- `409 Conflict`: e-mail duplicado.

## 4) API, DTO e contrato

DTOs de entrada:

- `CreateUserRequest` (email, password)
- `UpdateUserRequest` (email, password)

DTO de saida:

- `UserResponse` (id, email)

Por que DTO?

- Evita vazar entidade interna.
- Mantem contrato da API estavel.
- Facilita evolucao de dominio sem quebrar cliente.

## 5) Validacao e tratamento de erros

Foi implementado:

- Validacao com Jakarta Validation nos DTOs (`@Email`, `@NotBlank`, `@Size`).
- Handler global em `ApiExceptionHandler` para padronizar respostas de erro.

Isso evita if/else repetido em controller e padroniza payload de erro.

## 6) Migration: como pensar e como evoluir este projeto

Estado atual:

- `spring.jpa.hibernate.ddl-auto=update` no `application.properties`.

Isso ajuda no inicio, mas para ambiente real o ideal e migration versionada.

Recomendacao de evolucao:

1. Escolher ferramenta: Flyway ou Liquibase.
2. Mudar `ddl-auto` para `validate` (ou `none`) em ambiente produtivo.
3. Criar scripts versionados (`V1__create_users.sql`, etc).
4. Rodar migration no startup da aplicacao.

Beneficios:

- Historico de schema.
- Reprodutibilidade em todos ambientes.
- Rollforward mais seguro.

## 7) Docker: como colocar API + banco juntos

Conceito:

- Um container para API Spring.
- Um container para Postgres.
- Orquestracao por `docker compose`.

Passos sugeridos:

1. Criar `Dockerfile` para a API (build do jar e runtime Java).
2. Criar `docker-compose.yml` com servicos `app` e `db`.
3. Configurar variaveis de ambiente para datasource.
4. Subir com `docker compose up -d`.

Depois disso, os mesmos curls funcionam mudando apenas a URL base.

## 8) Sequencia recomendada de estudo

1. Rodar projeto local e executar curls de LCRUD.
2. Ler `UserController` e `UserService` lado a lado.
3. Seguir o mapeamento `UserPersistenceAdapter` ate o `UserRepository`.
4. Implementar migration com Flyway como proximo desafio.
5. Adicionar auth JWT no fluxo (cadastro/login/protecao de rotas).

## 9) Portas 5432 vs 8080 (o ponto mais importante)

- `5432` e a porta padrao do Postgres (banco de dados).
- `8080` e a porta da API HTTP (Spring Boot).

Quem usa cada uma:

- DBeaver usa `5432` para falar direto com o banco.
- API Client (Postman/Insomnia/curl) usa `8080` para falar com a API.

Fluxo real:

1. Voce chama `http://localhost:8080/users` no API Client.
2. A API recebe e internamente abre conexao JDBC no banco (`localhost:5432` fora do Docker, ou `db:5432` dentro do Docker network).
3. O banco responde e a API devolve JSON.

Conclusao:

- Nao e `5432` ou `8080`.
- Voce normalmente usa as duas ao mesmo tempo, cada uma com funcao diferente.

## 10) Como rodar com Docker neste projeto

Arquivos criados:

- `Dockerfile`
- `docker-compose.yml`
- `.dockerignore`
- `application-docker.properties`

Comandos:

1. Subir tudo:
   - `docker compose up -d --build`
2. Ver logs da API:
   - `docker compose logs -f api`
3. Parar:
   - `docker compose down`
4. Parar e apagar volume do banco (reset total):
   - `docker compose down -v`

Conexoes:

- API: `http://localhost:8080`
- Postgres no DBeaver:
  - Host: `localhost`
  - Port: `5432`
  - Database: `notes_db`
  - User: `postgres`
  - Password: `minhasenha`
