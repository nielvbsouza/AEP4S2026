# ♻️ Recicla

Sistema desenvolvido para facilitar o compartilhamento de conteúdos relacionados à reciclagem, meio ambiente e sustentabilidade.

O projeto permite o cadastro de usuários, publicação de vídeos, organização por categorias, comentários e controle dos conteúdos através de um sistema de moderação.

---

## 📌 Sobre o Projeto

O sistema foi desenvolvido como projeto acadêmico, utilizando conceitos de:

- Programação Orientada a Objetos
- Banco de Dados
- Modelagem de Sistemas
- API REST
- Arquitetura em camadas
- Normalização de Banco de Dados

A ideia principal é criar uma plataforma onde os usuários possam compartilhar vídeos relacionados à reciclagem e sustentabilidade.

---

## 🛠️ Tecnologias

### Back-end
- Java
- Spring Boot
- API REST

### Banco de Dados
- MySQL

### Modelagem
- Draw.io

### Front-end
- HTML
- CSS
- JavaScript

---

## 🗄️ Banco de Dados

O banco de dados foi desenvolvido utilizando o modelo relacional.

Principais tabelas:

| Tabela | Descrição |
|---|---|
| `usuario` | Armazena os usuários do sistema |
| `moderador` | Identifica os usuários responsáveis pela moderação |
| `video` | Armazena os vídeos publicados |
| `categoria` | Armazena as categorias dos vídeos |
| `video_categoria` | Relaciona vídeos e categorias |
| `comentario` | Armazena os comentários dos usuários |
| `moderacao` | Registra a análise dos vídeos |

A tabela `video_categoria` é utilizada para representar o relacionamento entre vídeos e categorias. Já `comentario` relaciona usuários aos vídeos e `moderacao` relaciona os vídeos aos moderadores.

---

## 📊 Estrutura do Sistema

```text
                    ┌──────────────┐
                    │   USUARIO    │
                    └──────┬───────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
              ▼            ▼            ▼
        ┌──────────┐ ┌───────────┐ ┌────────────┐
        │  VIDEO   │ │ COMENTARIO│ │ MODERADOR  │
        └────┬─────┘ └───────────┘ └──────┬─────┘
             │                            │
             ▼                            ▼
    ┌─────────────────┐            ┌────────────┐
    │ VIDEO_CATEGORIA │            │ MODERACAO  │
    └────────┬────────┘            └────────────┘
             │
             ▼
       ┌────────────┐
       │ CATEGORIA  │
       └────────────┘
