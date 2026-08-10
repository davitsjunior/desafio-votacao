# Desafio Votação

API REST desenvolvida em Java e Spring Boot para gerenciamento de sessões de votação de pautas em cooperativas.

Permite cadastrar pautas, abrir sessões de votação, registrar votos dos associados e consultar o resultado da apuração.

## Arquitetura

Arquitetura em camadas (*Layered Architecture*), simples e direta:

```
Controller  → recebe a requisição HTTP, valida (Bean Validation) e delega ao Service
Service     → contém as regras de negócio
Repository  → acesso a dados (Spring Data JPA)
Entity      → mapeamento JPA, schema validado (nunca gerado) pelo Hibernate
DTO         → Request/Response, desacoplados das entidades
Exception   → exceptions de negócio centralizadas em um GlobalExceptionHandler
```

Decisões de negócio relevantes:

- **Sessão única por pauta**: cada pauta pode ter apenas uma sessão de votação (`SessaoJaExisteException` ao tentar abrir uma segunda). Isso garante que a constraint `UNIQUE(sessao_id, associado_id)` do banco equivalha, na prática, a "um voto por associado por pauta".
- **Sessão aberta/fechada**: não há job/scheduler; o Service compara o horário atual com o campo `fim` da sessão no momento do voto.
- **Associado**: identificado pelo `cpf`. Não há endpoint de cadastro — o Service de votação cria o associado automaticamente (*find-or-create*) no primeiro voto com aquele CPF.
- **Banco**: schema controlado exclusivamente pelo Flyway; Hibernate configurado com `ddl-auto=validate` (nunca `create`/`update`).

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Data JPA / Hibernate
- PostgreSQL 17
- Flyway
- Bean Validation
- springdoc-openapi (Swagger UI)
- JUnit 5 + Mockito + MockMvc
- JaCoCo (cobertura mínima de 85%)
- Docker Compose
- Maven

## Pré-requisitos

- Java 21
- Maven Wrapper (`./mvnw`, incluído no repositório)
- Docker
- Docker Compose

## Como executar

### Desenvolvimento local

A aplicação Spring Boot roda fora do container (IDE ou `./mvnw spring-boot:run`); apenas o PostgreSQL roda via Docker. Nesse modo a aplicação acessa o banco em `localhost:5432` (configuração padrão em `application.yaml`).

```bash
docker compose up postgres -d
./mvnw spring-boot:run
```

### Execução completa via Docker

Aplicação e PostgreSQL rodam via `docker compose`, com a aplicação executada a partir da imagem definida no `Dockerfile`. Nesse modo a aplicação sobe com o profile `docker` (`SPRING_PROFILES_ACTIVE=docker`, definido no `docker-compose.yml`), que troca o host de conexão com o banco de `localhost` para `postgres` — o hostname do serviço na rede Docker.

```bash
./mvnw clean package
docker compose up --build
```

Em ambos os modos a API sobe em `http://localhost:8080`.

## Docker

Dados de conexão (também usados em `application.yaml`/`application-docker.yaml`): banco `desafio_votacao`, usuário/senha `postgres`/`postgres`.

### Persistência

O PostgreSQL grava os dados em um volume Docker (`postgres_data`), que permanece intacto mesmo após parar/recriar os containers.

```bash
docker compose down       # para e remove os containers; o volume (dados) é preservado
docker compose down -v    # também remove o volume — apaga os dados persistidos
```

> A aplicação está containerizada (`Dockerfile` + `docker-compose.yml`), o que facilita sua execução em qualquer ambiente compatível com Docker, incluindo infraestrutura cloud.

## Documentação da API (Swagger)

Com a aplicação em execução:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Versionamento da API

Estratégia adotada: **versionamento via URI path**, com prefixo `/v1` em todos os endpoints (ex.: `/v1/pautas`). Uma futura versão incompatível seria exposta em controllers próprios sob `/v2`, mantendo `/v1` funcionando sem quebra para clientes existentes.

## Endpoints

| Método | Endpoint                        | Descrição                                  |
|--------|-----------------------------------|---------------------------------------------|
| POST   | `/v1/pautas`                    | Cadastra uma nova pauta                     |
| POST   | `/v1/pautas/{pautaId}/sessao`   | Abre uma sessão de votação (padrão: 1 min)  |
| POST   | `/v1/pautas/{pautaId}/votos`    | Registra o voto de um associado             |
| GET    | `/v1/pautas/{pautaId}/resultado`| Consulta o resultado da votação             |

### Cadastrar pauta

`POST /v1/pautas`

Requisição:
```json
{
  "titulo": "Reforma do estatuto",
  "descricao": "Votação sobre a reforma do estatuto social"
}
```

Resposta `201 Created`:
```json
{
  "id": 1,
  "titulo": "Reforma do estatuto",
  "descricao": "Votação sobre a reforma do estatuto social",
  "createdAt": "2026-08-07T12:00:00Z"
}
```

### Abrir sessão de votação

`POST /v1/pautas/{pautaId}/sessao`

Requisição (corpo opcional — sem corpo ou `duracaoMinutos: null` usa o padrão de 1 minuto):
```json
{
  "duracaoMinutos": 5
}
``` 

Resposta `201 Created`:
```json
{
  "id": 1,
  "pautaId": 1,
  "inicio": "2026-08-07T12:00:00Z",
  "fim": "2026-08-07T12:05:00Z",
  "status": "ABERTA"
}
```

### Registrar voto

`POST /v1/pautas/{pautaId}/votos`

Requisição:
```json
{
  "cpf": "12345678900",
  "opcao": "SIM"
}
```

Resposta `201 Created`:
```json
{
  "id": 1,
  "associadoId": 1,
  "opcao": "SIM",
  "createdAt": "2026-08-07T12:01:00Z"
}
```

### Consultar resultado

`GET /v1/pautas/{pautaId}/resultado`

Resposta `200 OK`:
```json
{
  "totalSim": 7,
  "totalNao": 3,
  "totalVotos": 10
}
```

### Erros

Todas as exceptions de negócio retornam um corpo padronizado:
```json
{
  "timestamp": "2026-08-07T12:01:00Z",
  "status": 409,
  "message": "Associado já votou nesta sessão"
}
```

| Situação                                   | Status |
|---------------------------------------------|--------|
| Dados inválidos (Bean Validation)            | 400    |
| Pauta ou sessão não encontrada               | 404    |
| Sessão já existe / encerrada / voto duplicado| 409    |
| Erro inesperado                              | 500    |

## Testes

```bash
./mvnw verify
```

Executa os testes unitários (Services) e de integração dos Controllers (MockMvc), com verificação de cobertura mínima de 85% via JaCoCo. Relatório em `target/site/jacoco/index.html`. Os testes usam H2 em memória (`src/test/resources/application.yaml`), sem depender do PostgreSQL/Docker.

## Teste de carga (k6)

Script em `load-test/votos-concorrentes.js` simula 50 usuários virtuais registrando votos concorrentes por 30s numa mesma sessão, medindo throughput e latência (p95 < 500ms).

Pré-requisitos: [k6](https://k6.io/docs/get-started/installation/) instalado e a aplicação em execução.

```bash
docker compose up postgres -d
./mvnw spring-boot:run &
k6 run load-test/votos-concorrentes.js
```

O client fake de validação de CPF (bônus) recusa aleatoriamente parte dos votos (`404`) — isso é esperado e contabilizado separadamente (`votos_recusados_client_cpf`), não é falha do teste de carga.
