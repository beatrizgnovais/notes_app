# Guia de estudo backend do projeto

Documentação complementar:

- **[README](../README.md)** — como rodar, variáveis de ambiente, endpoints resumidos.
- **[arquitetura.md](arquitetura.md)** — explicação profunda da arquitetura hexagonal, fluxogramas e walkthrough `POST /notes` ponta a ponta.
- **[testes-manuais-curl.md](testes-manuais-curl.md)** — curls e PowerShell para todos os cenários.

---

## 1) O que foi implementado

### LCRUD de `users`

- `POST /users` — criar
- `GET /users` — listar
- `GET /users/{id}` — buscar por id
- `PUT /users/{id}` — atualizar
- `DELETE /users/{id}` — remover

Camadas (users):

- Entrada HTTP: `adapter/input/web/controller/UserController.kt`
- Aplicação (casos de uso): `application/port/input/UserUseCase.kt`
- Regras de orquestração: `application/service/UserService.kt`
- Porta de persistência: `application/port/output/UserRepositoryPort.kt`
- Adapter de persistência: `adapter/output/persistence/UserPersistenceAdapter.kt`
- Repositório JPA: `adapter/output/persistence/repository/UserRepository.kt`

### LCRUD de `notes`

- `POST /notes` — criar (body inclui `userId`; o usuário precisa existir)
- `GET /notes` — listar
- `GET /notes/{id}` — buscar por id
- `PUT /notes/{id}` — atualizar título/conteúdo
- `DELETE /notes/{id}` — remover

Camadas (notes):

- Entrada HTTP: `adapter/input/web/controller/NoteController.kt`
- Aplicação: `application/port/input/NoteUseCase.kt`
- Regras de orquestração: `application/service/NoteService.kt`
- Portas de saída: `application/port/output/NoteRepositoryPort.kt` e `UserRepositoryPort.kt` (validação de `userId` na criação)
- Adapter de persistência: `adapter/output/persistence/NotePersistenceAdapter.kt`
- Repositório JPA: `adapter/output/persistence/repository/NoteRepository.kt`

---

## 2) Hexagonal e Clean Architecture no contexto do projeto

Na prática, as duas abordagens aqui convergem para o mesmo objetivo:

- Domínio e aplicação não dependem do framework web.
- O controller conhece o caso de uso, não conhece SQL/JPA diretamente.
- Persistência entra como detalhe de infraestrutura via portas (`UserRepositoryPort`, `NoteRepositoryPort`).

Fluxo:

1. Requisição HTTP entra no controller.
2. Controller converte DTO em command.
3. Use case/service aplica regra de negócio.
4. Service usa porta de saída.
5. Adapter da porta chama JPA/Postgres.
6. Resultado volta para o controller como resposta HTTP.

Isso facilita trocar banco/framework sem quebrar regra de negócio central.

Para diagramas, sequência completa de uma requisição e comparação Domain/DTO/Entity/Command, leia **[arquitetura.md](arquitetura.md)**.

---

## 3) HTTP methods e LCRUD

- `POST` cria recurso novo → retorno esperado: `201 Created`.
- `GET` consulta recurso → `200 OK`.
- `PUT` atualiza recurso existente → `200 OK` com recurso atualizado.
- `DELETE` remove recurso → `204 No Content`.

Erros esperados no projeto:

- `400 Bad Request`: validação de entrada falhou.
- `404 Not Found`: id inexistente ou (em `POST /notes`) `userId` inexistente.
- `409 Conflict`: e-mail duplicado em usuários.

---

## 4) API, DTO e contrato

### Users

DTOs de entrada:

- `CreateUserRequest` (email, password)
- `UpdateUserRequest` (email, password)

DTO de saída:

- `UserResponse` (id, email) — senha não é exposta.

### Notes

DTOs de entrada:

- `CreateNoteRequest` (title, content, userId)
- `UpdateNoteRequest` (title, content)

DTO de saída:

- `NoteResponse` (id, title, content, userId)

Por que DTO?

- Evita vazar entidade interna.
- Mantém contrato da API estável.
- Facilita evolução de domínio sem quebrar cliente.

---

## 5) Validação e tratamento de erros

Implementado:

- Validação com Jakarta Validation nos DTOs (`@Email`, `@NotBlank`, `@Size`, `@NotNull` onde aplicável).
- Handler global em `ApiExceptionHandler` para padronizar respostas de erro (`400`, `404`, `409`).

Isso evita `if/else` repetido em controller e padroniza payload de erro.

---

## 6) Segurança (estado atual)

Em `config/SecurityConfig.kt`:

- CSRF desabilitado.
- `authorizeHttpRequests { anyRequest().permitAll() }` — **todas as rotas públicas** para facilitar estudo e testes manuais de CRUD.
- CORS liberado para `http://localhost:5173` e `http://localhost:3000`.

O projeto já declara `jjwt-api` no Gradle, mas **JWT ainda não está ligado** ao fluxo HTTP — próximo passo natural é cadastro/login e proteção de rotas. Detalhes em [arquitetura.md](arquitetura.md) seção “Próximas evoluções”.

---

## 7) Migration: como pensar e como evoluir este projeto

Estado atual:

- `spring.jpa.hibernate.ddl-auto=update` no `application.properties`.

Isso ajuda no início, mas para ambiente real o ideal é migration versionada.

Recomendação de evolução:

1. Escolher ferramenta: Flyway ou Liquibase.
2. Mudar `ddl-auto` para `validate` (ou `none`) em ambiente produtivo.
3. Criar scripts versionados (`V1__create_users.sql`, etc).
4. Rodar migration no startup da aplicação.

Benefícios:

- Histórico de schema.
- Reprodutibilidade em todos ambientes.
- Rollforward mais seguro.

---

## 8) Docker: API + banco juntos

Conceito:

- Um container para API Spring (`api` no `docker-compose.yml`).
- Um container para Postgres (`db`).
- Orquestração por `docker compose`.

Arquivos:

- `Dockerfile`
- `docker-compose.yml`
- `.dockerignore`
- `application-docker.properties`

Depois de subir, os mesmos testes funcionam na URL base `http://localhost:8080`. Ver [README](../README.md).

---

## 9) Sequência recomendada de estudo

1. Rodar o projeto (Docker ou local) e executar os testes em [testes-manuais-curl.md](testes-manuais-curl.md).
2. Ler `UserController` e `UserService` lado a lado; depois `NoteController` e `NoteService`.
3. Seguir o mapeamento `UserPersistenceAdapter` / `NotePersistenceAdapter` até os `JpaRepository`.
4. Ler **[arquitetura.md](arquitetura.md)** — walkthrough `POST /notes` e diagramas.
5. Implementar Flyway como próximo desafio de infraestrutura.
6. (Opcional) Integrar JWT e substituir `permitAll()` por regras por rota.

---

## 10) Portas 5432 vs 8080 (o ponto mais importante)

- `5432` é a porta padrão do Postgres (banco de dados).
- `8080` é a porta da API HTTP (Spring Boot).

Quem usa cada uma:

- DBeaver usa `5432` para falar direto com o banco.
- Postman/Insomnia/curl/PowerShell usa `8080` para falar com a API.

Fluxo real:

1. Você chama `http://localhost:8080/users` ou `/notes` no cliente HTTP.
2. A API recebe e internamente abre conexão JDBC (`localhost:5432` fora do Docker, ou `db:5432` dentro da rede do Compose).
3. O banco responde e a API devolve JSON.

Conclusão: normalmente você usa **as duas portas ao mesmo tempo**, cada uma com função diferente.

---

## 11) Como rodar com Docker neste projeto

Comandos:

1. Subir tudo: `docker compose up -d --build`
2. Ver logs da API: `docker compose logs -f api`
3. Parar: `docker compose down`
4. Parar e apagar volume do banco (reset total): `docker compose down -v`

Conexões:

- API: `http://localhost:8080`
- Postgres no DBeaver:
  - Host: `localhost`
  - Port: `5432`
  - Database: `notes_db`
  - User: `postgres`
  - Password: `minhasenha`
