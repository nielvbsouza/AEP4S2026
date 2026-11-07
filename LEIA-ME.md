# Recicla View – guia de execução e publicação

Arquitetura (opção A):

```
GitHub Pages  ──fetch──►  Back-end Spring Boot  ──JDBC──►  MySQL (Hostinger)
 (pasta docs)             (Render/Railway/Fly)
```

## Estrutura (copie as pastas para a RAIZ do repositório AEP4S2026)

```
AEP4S2026/
├── 1bimestre/        (já existe)
├── docs/             ← front-end (GitHub Pages publica esta pasta)
├── recicla-view/     ← back-end Spring Boot (tem Dockerfile)
├── banco/            ← scripts SQL
└── README.md
```

## 1. Banco de dados (Hostinger)

No phpMyAdmin, aba **SQL**, com o banco selecionado:

1. `banco/estrutura.sql` (cria as 7 tabelas; se você já criou antes, não precisa repetir);
2. `banco/dados_teste.sql` (opcional: categorias e vídeos de exemplo);
3. `banco/promover_moderador.sql` (depois de cadastrar sua conta no site).

## 2. Rodar tudo no seu computador

1. Em `recicla-view/src/main/resources/`, copie `application-local.properties.exemplo` para
   `application-local.properties` e coloque a senha real do banco.
2. No IntelliJ: **Run > Edit Configurations > ReciclaViewApplication > Active profiles: `local`**.
3. Rode a aplicação e teste `http://localhost:8080/api/status`.
4. Abra `docs/index.html` pelo IntelliJ (ícone do navegador no canto do editor) ou pelo Live Server do VS Code.
   Em `localhost` o front usa automaticamente a API em `http://localhost:8080`.

## 3. Publicar o back-end (exemplo: Render)

A interface do serviço pode mudar; a ideia é sempre a mesma.

1. Crie um **Web Service** a partir do repositório do GitHub.
2. **Root Directory:** `recicla-view` · **Runtime/Language:** Docker (ele usa o `Dockerfile`).
3. Em **Environment Variables**, cadastre:

| Variável | Valor |
|---|---|
| `DB_URL` | `jdbc:mysql://srv1603.hstgr.io:3306/u773559975_reciclaView?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8` |
| `DB_USER` | `u773559975_reciclaView` |
| `DB_PASSWORD` | a senha do banco |
| `AUTH_SECRET` | um texto longo e aleatório (mínimo 16 caracteres) – **obrigatório trocar** |
| `CORS_ORIGENS` | opcional; o padrão já libera `https://nielvbsouza.github.io` e `localhost` |

4. Faça o deploy e copie a URL gerada (ex.: `https://recicla-view.onrender.com`).
5. Teste `SUA_URL/api/status` e `SUA_URL/api/categorias` no navegador.

**MySQL remoto na Hostinger:** em **Bancos de Dados > MySQL Remoto**, o serviço de hospedagem
precisa estar autorizado. Como esses serviços costumam trocar de IP, pode ser necessário marcar
**Qualquer Host**. Isso deixa o banco acessível pela internet (protegido só pela senha),
então use uma senha forte e não a divulgue.

## 4. Publicar o front-end (GitHub Pages)

1. Abra `docs/js/config.js` e troque `https://SEU-BACKEND.onrender.com` pela URL real do back-end.
2. Faça commit e push.
3. No GitHub: **Settings > Pages > Build and deployment > Deploy from a branch >
   Branch `main` / pasta `/docs` > Save**.
4. O site fica em `https://nielvbsouza.github.io/AEP4S2026/` (leva alguns minutos na primeira vez).

## Observações

- Planos gratuitos costumam "dormir" quando ficam parados: a primeira visita pode levar cerca de 1 minuto.
- O front só mostra vídeos **aprovados**. Para aprovar, entre com uma conta moderadora (menu **Moderação**).
- Links do YouTube aparecem com player; qualquer outro link http/https aparece como botão "Abrir vídeo".
- Nunca envie a senha do banco nem o `AUTH_SECRET` para o GitHub (o repositório é público).
