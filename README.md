# Notes App (API REST)

API REST em **Kotlin** + **Spring Boot** para gerenciamento de **usuários** e **notas**, organizada em **arquitetura hexagonal** (ports & adapters).

## Stack

| Tecnologia | Versão / observação |
|------------|---------------------|
| Kotlin | 2.2.21 |
| Spring Boot | 4.0.3 |
| Java | 24 |
| PostgreSQL | 16 |
| Build | Gradle |

Dependências principais: Spring Web MVC, Spring Data JPA, Validation, Spring Security (configuração atual libera todas as rotas — ver [Segurança](#segurança)).

## Pré-requisitos

### Rodar com Docker (recomendado)

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (ou Docker Engine + Compose)

### Rodar localmente sem Docker

- **JDK 24** (alinhado ao toolchain do projeto)
- **PostgreSQL 16** acessível em `localhost:5432`
- Banco de dados **`notes_db`** criado (usuário/senha compatíveis com as variáveis abaixo)

## Configuração

Variáveis de ambiente suportadas em [`src/main/resources/application.properties`](src/main/resources/application.properties):

| Variável | Padrão (local) | Descrição |
|----------|----------------|-----------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/notes_db` | JDBC URL |
| `DB_USER` | `postgres` | Usuário do banco |
| `DB_PASSWORD` | `minhasenha` | Senha do banco |
| `SERVER_PORT` | `8080` | Porta HTTP da API |

Com **Docker Compose**, o perfil `docker` é ativado e o host do banco vira `db` na rede interna — ver [`application-docker.properties`](src/main/resources/application-docker.properties).

## Como rodar

### Docker Compose

Na raiz do projeto:

```bash
docker compose up -d --build
```

- API: `http://localhost:8080`
- Logs da API: `docker compose logs -f api`
- Parar: `docker compose down`
- Parar e **apagar dados** do Postgres (volume): `docker compose down -v`

Credenciais padrão do Postgres no compose (útil para DBeaver): host `localhost`, porta `5432`, database `notes_db`, user `postgres`, password `minhasenha`.

### Local (Gradle)

**Linux / macOS:**

```bash
./gradlew bootRun
```

**Windows (CMD ou PowerShell):**

```powershell
.\gradlew.bat bootRun
```

### JAR

```bash
./gradlew bootJar
java -jar build/libs/NotesApp-0.0.1-SNAPSHOT.jar
```

(No Windows use `gradlew.bat bootJar`. O artefato segue `rootProject.name` em `settings.gradle.kts` + `version` em `build.gradle.kts`.)

## Endpoints

Base URL local: `http://localhost:8080`

### Usuários (`/users`)

| Método | Caminho | Status de sucesso |
|--------|---------|-------------------|
| `POST` | `/users` | `201 Created` |
| `GET` | `/users` | `200 OK` |
| `GET` | `/users/{id}` | `200 OK` |
| `PUT` | `/users/{id}` | `200 OK` |
| `DELETE` | `/users/{id}` | `204 No Content` |

### Notas (`/notes`)

| Método | Caminho | Status de sucesso |
|--------|---------|-------------------|
| `POST` | `/notes` | `201 Created` |
| `GET` | `/notes` | `200 OK` |
| `GET` | `/notes/{id}` | `200 OK` |
| `PUT` | `/notes/{id}` | `200 OK` |
| `DELETE` | `/notes/{id}` | `204 No Content` |

**Regra de negócio:** ao criar nota, `userId` no body deve referenciar um usuário existente (caso contrário `404`).

### Erros comuns

| HTTP | Situação |
|------|----------|
| `400` | Validação de entrada (DTO inválido) |
| `404` | Recurso não encontrado |
| `409` | Conflito (ex.: e-mail duplicado em usuários) |

## Como testar os endpoints

### cURL (rápido)

Criar usuário:

```bash
curl -i -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"ana@example.com\",\"password\":\"123456\"}"
```

Criar nota (substitua `userId` pelo `id` retornado):

```bash
curl -i -X POST http://localhost:8080/notes \
  -H "Content-Type: application/json" \
  -d "{\"title\":\"Primeira nota\",\"content\":\"Conteudo\",\"userId\":1}"
```

### PowerShell (`Invoke-RestMethod`)

```powershell
$base = "http://localhost:8080"
$user = Invoke-RestMethod -Method Post -Uri "$base/users" `
  -ContentType "application/json" `
  -Body '{"email":"ana@example.com","password":"123456"}'
$user

Invoke-RestMethod -Method Post -Uri "$base/notes" `
  -ContentType "application/json" `
  -Body (@{
    title = "Primeira nota"
    content = "Conteudo"
    userId = $user.id
  } | ConvertTo-Json)
```

Lista completa de cenários (incluindo erros): [**docs/testes-manuais-curl.md**](docs/testes-manuais-curl.md).

## Estrutura de pastas (hexagonal)

```
src/main/kotlin/com/beatrizgnovais/
├── domain/model/              # Modelos de domínio (sem JPA / sem Spring Web)
├── application/
│   ├── command/               # Comandos de entrada para casos de uso
│   ├── exception/             # Exceções de aplicação
│   ├── port/input/          # Portas de entrada (interfaces Use Case)
│   ├── port/output/         # Portas de saída (interfaces de persistência)
│   └── service/             # Implementação dos casos de uso
├── adapter/
│   ├── input/web/           # REST: controllers, DTOs, tratamento de erro HTTP
│   └── output/persistence/  # JPA: adapters, entities, repository (Spring Data)
└── config/                  # Spring (ex.: Security)
```

## Segurança

- **CSRF** desabilitado e **todas as rotas** com `permitAll()` para facilitar estudos e testes manuais de CRUD.
- **CORS** configurado para `http://localhost:5173` e `http://localhost:3000`.
- O projeto declara `jjwt-api` no Gradle; **JWT ainda não está integrado** ao fluxo HTTP — próximo passo natural é login + proteção de rotas.

Detalhes do fluxo ponta a ponta: [**docs/arquitetura.md**](docs/arquitetura.md).

## Documentação adicional

| Documento | Conteúdo |
|-----------|----------|
| [docs/arquitetura.md](docs/arquitetura.md) | Arquitetura hexagonal, fluxogramas, exemplo real `POST /notes`, erros |
| [docs/backend-estudo.md](docs/backend-estudo.md) | Guia de estudo resumido |
| [docs/testes-manuais-curl.md](docs/testes-manuais-curl.md) | Testes manuais com curl e PowerShell |

## Próximos passos sugeridos

- Autenticação JWT e restrição de rotas
- Migrations versionadas (Flyway ou Liquibase) e `ddl-auto` mais restritivo em produção
- Testes de aplicação mockando portas de saída

## Testes (`./gradlew.bat build`)

O teste `contextLoads` sobe o contexto Spring completo usando **H2 em memória** ([`src/test/resources/application.properties`](src/test/resources/application.properties)), para não depender de PostgreSQL na máquina ou no CI.

## Licença / projeto

Projeto de demonstração (`Demo project for Spring Boot` no `build.gradle.kts`).
