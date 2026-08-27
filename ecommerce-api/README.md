# E-commerce API

API REST de e-commerce construída com Spring Boot, Spring Security (JWT) e PostgreSQL, com o banco de dados rodando em Docker.

Projeto de portfólio focado em autenticação, autorização por papéis (roles) e transações de negócio (controle de estoque), desenvolvido como o terceiro e mais avançado de uma série de estudos práticos em Java + Spring Boot.

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Security + JWT (io.jsonwebtoken)
- Spring Data JPA / Hibernate
- PostgreSQL
- Docker / Docker Compose
- Swagger (springdoc-openapi)
- Maven

## Modelo de domínio

```
User 1 ──── N Order         (um usuario faz varios pedidos)
Order 1 ──── N OrderItem    (um pedido tem varios itens)
Product 1 ──── N OrderItem  (um produto aparece em varios itens de pedidos)
```

`OrderItem` é uma entidade de junção: além de ligar `Order` a `Product`, guarda a
**quantidade** e o **preço no momento da compra** (`unitPrice`), preservando o
histórico mesmo que o preço do produto mude depois.

## Autenticação e autorização

- Registro (`/api/auth/register`) e login (`/api/auth/login`) são as únicas rotas públicas.
- Toda senha é armazenada com hash **BCrypt** — nunca em texto puro.
- Login retorna um **token JWT**, que deve ser enviado em toda requisição subsequente
  no header `Authorization: Bearer <token>`.
- Todo usuário autenticado pode navegar produtos e fazer pedidos.
- Apenas usuários com role **ADMIN** podem criar, editar ou remover produtos
  (`@PreAuthorize("hasRole('ADMIN')")`).
- Cada usuário só enxerga os **próprios** pedidos — o pedido é sempre associado ao
  usuário do token, nunca a um id vindo livremente da URL.

## Como rodar o projeto

### Pré-requisitos
- Java 21+
- Maven
- Docker e Docker Compose

### 1. Suba o banco de dados
```bash
docker compose up -d
```

### 2. Rode a aplicação
```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

### 3. Documentação interativa (Swagger)
Acesse: `http://localhost:8080/swagger-ui.html`

Clique em **Authorize** e cole o token retornado pelo login (sem aspas, sem o
prefixo "Bearer") para testar as rotas protegidas.

## Endpoints

### Autenticação
| Método | Rota                | Descrição                          |
|--------|---------------------|--------------------------------------|
| POST   | /api/auth/register  | Cria um novo usuário (role USER)    |
| POST   | /api/auth/login     | Autentica e retorna um token JWT    |

### Produtos
| Método | Rota                | Descrição                          | Acesso        |
|--------|----------------------|--------------------------------------|---------------|
| GET    | /api/products        | Lista todos os produtos             | Autenticado   |
| GET    | /api/products/{id}   | Busca um produto por id             | Autenticado   |
| POST   | /api/products        | Cria um novo produto                | ADMIN         |
| PUT    | /api/products/{id}   | Atualiza um produto                 | ADMIN         |
| DELETE | /api/products/{id}   | Remove um produto                   | ADMIN         |

### Pedidos
| Método | Rota          | Descrição                                    | Acesso      |
|--------|---------------|-------------------------------------------------|-------------|
| POST   | /api/orders   | Cria um pedido com um ou mais itens             | Autenticado |
| GET    | /api/orders   | Lista os pedidos do usuário autenticado         | Autenticado |

### Exemplo de fluxo completo
```json
// 1. Registrar
POST /api/auth/register
{ "name": "Arthur", "email": "arthur@teste.com", "password": "123456" }
// -> retorna token

// 2. Autorizar no Swagger com o token retornado

// 3. Criar um pedido
POST /api/orders
{
  "items": [
    { "productId": 1, "quantity": 2 }
  ]
}
// -> retorna o pedido com itens, subtotal por item e total
```

## Arquitetura

```
Controller  -> recebe/devolve HTTP, valida entrada, exige role via @PreAuthorize
Service     -> regra de negocio, orquestra Repository + conversao Entity<->DTO
Repository  -> acesso ao banco (Spring Data JPA)
Model       -> entidades mapeadas para as tabelas
DTO         -> formato de entrada/saida, separado por dto.request e dto.response
Config      -> SecurityConfig, JwtAuthFilter, PasswordEncoder, OpenApiConfig
```

## Decisões de design

- **JWT sem estado (stateless)**: o servidor não guarda sessão; cada requisição se
  autentica sozinha via token, verificado por assinatura HMAC.
- **Senha nunca comparada diretamente**: login usa `passwordEncoder.matches(...)`,
  nunca compara hashes como strings, já que BCrypt gera um hash diferente a cada
  chamada mesmo para a mesma senha (salt aleatório).
- **Mensagens de erro genéricas em login**: "email ou senha inválidos" é usado tanto
  para email inexistente quanto para senha errada, evitando enumeração de usuários.
- **`@Transactional` na criação de pedidos**: se qualquer item falhar (ex: estoque
  insuficiente), toda a operação é revertida — nunca fica um pedido "pela metade"
  com estoque descontado sem o pedido ter sido de fato criado.
- **`unitPrice` copiado para `OrderItem`**: o preço é congelado no momento da compra,
  para que alterações futuras no preço do produto não afetem pedidos já realizados.
- **Pedidos sempre vinculados ao usuário do token**: `GET /api/orders` nunca aceita
  um id de usuário pela URL, prevenindo que alguém veja pedidos de outra pessoa.

## Próximos passos (roadmap)
- [ ] Endpoint de admin para listar/gerenciar pedidos de todos os usuários
- [ ] Cancelamento de pedido (com devolução de estoque)
- [ ] Refresh token
- [ ] Testes unitários e de integração (incluindo testes de segurança com MockMvc)

---
Desenvolvido como parte de um projeto de estudo para vaga de estágio em Java.