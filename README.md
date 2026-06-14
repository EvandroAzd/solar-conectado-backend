# Solar Conectado — Backend

API REST desenvolvida em Spring Boot para o site do **Solar dos Jovens de Ontem**, lar de idosos localizado em Porto Ferreira/SP.

Projeto acadêmico do curso de Desenvolvimento de Software Multiplataforma (DSM) — Fatec Porto Ferreira, 2º semestre.

---

## Tecnologias

- Java 21
- Spring Boot 3.3.0 (Web, Data JPA, Security, Validation)
- MariaDB
- JWT (jjwt 0.12.5)
- Cloudinary (upload de imagens)
- Lombok

---

## Módulos implementados

- **Usuários** — cadastro, login, perfil, inativação
- **Administradores** — hierarquia MASTER / ADM, promoção e gestão
- **Campanhas** — criação, edição, ciclo de vida (ATIVA / INATIVA / ENCERRADA)
- **Comentários** — moderação, curtidas
- **Doadores e Promessas** — doações com e sem cadastro, confirmação pelo ADM
- **Gamificação** — pontos por doação, compartilhamento e indicação
- **Produtos e Categorias** — catálogo interno
- **Bazar** — itens, pedidos multi-item, saídas internas, relatórios
- **Empresas e Representantes** — parceiros com aprovação e gestão de membros
- **Upload de Imagens** — integração com Cloudinary

---

## Como rodar localmente

### Pré-requisitos

- Java 21
- MariaDB rodando localmente
- Conta no [Cloudinary](https://cloudinary.com) (plano gratuito)

### Configuração

Configure as seguintes variáveis de ambiente (ou edite `application.properties`):

```
DB_PASSWORD=sua_senha_do_banco
ADMIN_MASTER_NOME=Nome do Master
ADMIN_MASTER_EMAIL=email@exemplo.com
ADMIN_MASTER_SENHA=SenhaForte@123
ADMIN_MASTER_CPF=00000000000
CLOUDINARY_CLOUD_NAME=seu_cloud_name
CLOUDINARY_API_KEY=sua_api_key
CLOUDINARY_API_SECRET=seu_api_secret
```

### Rodando

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

---

## Níveis de acesso

| Role | Descrição |
|------|-----------|
| Público | Sem token |
| ROLE_USUARIO | Usuário logado |
| ROLE_ADM | Administrador |
| ROLE_MASTER | Administrador master (acesso total) |
| ROLE_REPRESENTANTE | Representante de empresa parceira |