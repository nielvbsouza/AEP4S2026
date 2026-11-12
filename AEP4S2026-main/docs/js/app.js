const app = document.getElementById("app");
const menu = document.getElementById("menu");
const modal = document.getElementById("modal");
const avisoToast = document.getElementById("toast");

function h(tag, props = {}, ...filhos) {
  const e = document.createElement(tag);
  for (const [chave, valor] of Object.entries(props)) {
    if (chave === "class") e.className = valor;
    else if (chave.startsWith("on")) e.addEventListener(chave.slice(2).toLowerCase(), valor);
    else if (valor !== false && valor != null) e.setAttribute(chave, valor === true ? "" : valor);
  }
  for (const filho of filhos.flat()) {
    if (filho == null || filho === false) continue;
    e.append(filho.nodeType ? filho : document.createTextNode(String(filho)));
  }
  return e;
}

let temporizadorToast;
function toast(mensagem, erro = false) {
  avisoToast.textContent = mensagem;
  avisoToast.className = "visivel" + (erro ? " erro" : "");
  clearTimeout(temporizadorToast);
  temporizadorToast = setTimeout(() => { avisoToast.className = ""; }, 4500);
}

function carregando() {
  return h("p", { class: "aviso" }, "Carregando... (na primeira visita o servidor pode levar até 1 minuto para acordar)");
}
function aviso(mensagem) { return h("p", { class: "aviso erro" }, mensagem); }

function formatarData(iso) {
  return iso ? new Date(iso).toLocaleDateString("pt-BR") : "";
}

function urlSegura(url) {
  try {
    const u = new URL(url);
    return u.protocol === "http:" || u.protocol === "https:" ? u.href : "#";
  } catch { return "#"; }
}

function youtubeId(url) {
  try {
    const u = new URL(url);
    const host = u.hostname.replace(/^www\.|^m\./, "");
    let id = null;
    if (host === "youtu.be") {
      id = u.pathname.slice(1);
    } else if (host === "youtube.com") {
      id = u.searchParams.get("v");
      if (!id) {
        const m = u.pathname.match(/^\/(?:shorts|embed|live)\/([^/?]+)/);
        if (m) id = m[1];
      }
    }
    return id && /^[\w-]{11}$/.test(id) ? id : null;
  } catch { return null; }
}

function playerDe(video) {
  const id = youtubeId(video.url);
  if (id) {
    return h("div", { class: "player" },
      h("iframe", {
        src: "https://www.youtube-nocookie.com/embed/" + id,
        title: video.titulo,
        allow: "accelerometer; encrypted-media; picture-in-picture",
        allowfullscreen: true,
        referrerpolicy: "strict-origin-when-cross-origin"
      }));
  }
  return h("p", {}, h("a", {
    class: "btn", href: urlSegura(video.url), target: "_blank", rel: "noopener noreferrer"
  }, "Abrir vídeo ↗"));
}

function mostrarErroNaTela(erro) {
  app.replaceChildren(aviso(erro.message));
}

function desenharMenu() {
  const usuario = Sessao.usuario;
  const itens = [h("a", { href: "#inicio" }, "Início")];
  if (usuario) {
    itens.push(h("a", { href: "#publicar" }, "Publicar vídeo"));
    itens.push(h("a", { href: "#meus" }, "Meus vídeos"));
    if (usuario.moderador) itens.push(h("a", { href: "#moderacao" }, "Moderação"));
    itens.push(h("span", { class: "ola" }, "Olá, " + usuario.nome.split(" ")[0]));
    itens.push(h("button", { class: "btn secundario", onclick: sair }, "Sair"));
  } else {
    itens.push(h("a", { href: "#entrar" }, "Entrar"));
    itens.push(h("a", { class: "btn", href: "#cadastrar" }, "Cadastrar"));
  }
  menu.replaceChildren(...itens);
}

function sair() {
  Sessao.limpar();
  if (location.hash === "#inicio" || location.hash === "") rotear();
  else location.hash = "inicio";
}

const rotas = {
  inicio: telaInicio,
  entrar: telaEntrar,
  cadastrar: telaCadastrar,
  publicar: telaPublicar,
  meus: telaMeus,
  moderacao: telaModeracao
};

function rotear() {
  const nome = location.hash.replace("#", "") || "inicio";
  const tela = rotas[nome] || telaInicio;
  desenharMenu();
  window.scrollTo(0, 0);
  tela().catch(mostrarErroNaTela);
}

async function telaInicio() {
  app.replaceChildren(carregando());
  const categorias = await Api.categorias();

  const seletor = h("select", { "aria-label": "Categoria" },
    h("option", { value: "" }, "Todas as categorias"),
    categorias.map((c) => h("option", { value: c.id }, c.nome)));
  const busca = h("input", { type: "search", placeholder: "Buscar por título...", "aria-label": "Buscar" });
  const lista = h("div", { class: "grade" });

  async function carregar() {
    lista.replaceChildren(carregando());
    try {
      const videos = await Api.videos(seletor.value, busca.value.trim());
      lista.replaceChildren(...(videos.length
        ? videos.map(cartao)
        : [h("p", { class: "aviso" }, "Nenhum vídeo encontrado.")]));
    } catch (erro) {
      lista.replaceChildren(aviso(erro.message));
    }
  }

  seletor.addEventListener("change", carregar);
  busca.addEventListener("keydown", (e) => { if (e.key === "Enter") carregar(); });

  app.replaceChildren(
    h("section", { class: "hero" },
      h("h1", {}, "Vídeos sobre reciclagem e sustentabilidade"),
      h("p", {}, "Aprenda, compartilhe e cuide do planeta.")),
    h("div", { class: "filtros" }, seletor, busca, h("button", { class: "btn", onclick: carregar }, "Buscar")),
    lista
  );
  carregar();
}

function cartao(video) {
  const id = youtubeId(video.url);
  const capa = id
    ? h("img", { src: "https://img.youtube.com/vi/" + id + "/hqdefault.jpg", alt: "", loading: "lazy" })
    : h("div", { class: "capa-padrao" }, "♻️");

  return h("article", {
    class: "cartao", tabindex: "0",
    onclick: () => abrirVideo(video.id),
    onkeydown: (e) => { if (e.key === "Enter") abrirVideo(video.id); }
  },
    capa,
    h("div", { class: "cartao-corpo" },
      h("h3", {}, video.titulo),
      h("p", { class: "autor" }, "por " + video.autor),
      h("div", { class: "chips" }, video.categorias.map((c) => h("span", { class: "chip" }, c.nome)))));
}

async function abrirVideo(id) {
  try {
    const [video, comentarios] = await Promise.all([Api.video(id), Api.comentarios(id)]);
    const listaComentarios = h("div", { class: "comentarios" });

    function desenharComentarios(lista) {
      listaComentarios.replaceChildren(...(lista.length
        ? lista.map((c) => h("div", { class: "comentario" },
            h("strong", {}, c.autor),
            h("span", { class: "data" }, " · " + formatarData(c.dataComentario)),
            h("p", {}, c.texto)))
        : [h("p", { class: "aviso" }, "Seja o primeiro a comentar.")]));
    }
    desenharComentarios(comentarios);

    let formulario;
    if (Sessao.usuario) {
      const texto = h("textarea", { rows: "3", maxlength: "1000", placeholder: "Escreva um comentário...", required: true });
      formulario = h("form", {
        class: "form-comentario",
        onsubmit: async (e) => {
          e.preventDefault();
          try {
            await Api.comentar(id, { texto: texto.value.trim() });
            texto.value = "";
            desenharComentarios(await Api.comentarios(id));
          } catch (erro) { toast(erro.message, true); }
        }
      }, texto, h("button", { class: "btn", type: "submit" }, "Comentar"));
    } else {
      formulario = h("p", { class: "aviso" },
        h("a", { href: "#entrar", onclick: () => modal.close() }, "Entre"), " para comentar.");
    }

    modal.replaceChildren(h("div", { class: "modal-corpo" },
      h("button", { class: "fechar", onclick: () => modal.close(), "aria-label": "Fechar" }, "×"),
      h("h2", {}, video.titulo),
      playerDe(video),
      h("p", { class: "autor" }, "por " + video.autor + " · " + formatarData(video.dataPublicacao)),
      video.descricao ? h("p", {}, video.descricao) : null,
      h("h3", {}, "Comentários"),
      listaComentarios,
      formulario));
    modal.showModal();
  } catch (erro) {
    toast(erro.message, true);
  }
}

modal.addEventListener("click", (e) => { if (e.target === modal) modal.close(); });
modal.addEventListener("close", () => modal.replaceChildren());

async function telaEntrar() {
  const email = h("input", { type: "email", required: true, placeholder: "E-mail", autocomplete: "username" });
  const senha = h("input", { type: "password", required: true, placeholder: "Senha", autocomplete: "current-password" });
  app.replaceChildren(h("form", {
    class: "form",
    onsubmit: async (e) => {
      e.preventDefault();
      try {
        Sessao.salvar(await Api.login({ email: email.value.trim(), senha: senha.value }));
        toast("Bem-vindo(a)!");
        location.hash = "inicio";
      } catch (erro) { toast(erro.message, true); }
    }
  },
    h("h2", {}, "Entrar"), email, senha,
    h("button", { class: "btn", type: "submit" }, "Entrar"),
    h("p", {}, "Não tem conta? ", h("a", { href: "#cadastrar" }, "Cadastre-se"))));
}

async function telaCadastrar() {
  const nome = h("input", { required: true, maxlength: "100", placeholder: "Nome", autocomplete: "name" });
  const email = h("input", { type: "email", required: true, maxlength: "150", placeholder: "E-mail", autocomplete: "username" });
  const senha = h("input", { type: "password", required: true, minlength: "6", maxlength: "72", placeholder: "Senha (mínimo 6 caracteres)", autocomplete: "new-password" });
  app.replaceChildren(h("form", {
    class: "form",
    onsubmit: async (e) => {
      e.preventDefault();
      try {
        Sessao.salvar(await Api.registrar({ nome: nome.value.trim(), email: email.value.trim(), senha: senha.value }));
        toast("Conta criada! Bem-vindo(a)!");
        location.hash = "inicio";
      } catch (erro) { toast(erro.message, true); }
    }
  },
    h("h2", {}, "Criar conta"), nome, email, senha,
    h("button", { class: "btn", type: "submit" }, "Cadastrar"),
    h("p", {}, "Já tem conta? ", h("a", { href: "#entrar" }, "Entrar"))));
}

async function telaPublicar() {
  if (!Sessao.usuario) { location.hash = "entrar"; return; }
  app.replaceChildren(carregando());
  const categorias = await Api.categorias();

  const titulo = h("input", { required: true, maxlength: "150", placeholder: "Título" });
  const url = h("input", { type: "url", required: true, maxlength: "500", placeholder: "Link do vídeo (ex.: https://www.youtube.com/watch?v=...)" });
  const descricao = h("textarea", { rows: "4", maxlength: "2000", placeholder: "Descrição (opcional)" });
  const opcoes = categorias.map((c) =>
    h("label", { class: "check" }, h("input", { type: "checkbox", value: c.id }), " " + c.nome));

  const formulario = h("form", {
    class: "form",
    onsubmit: async (e) => {
      e.preventDefault();
      const ids = [...formulario.querySelectorAll("input[type=checkbox]:checked")].map((x) => Number(x.value));
      if (!ids.length) { toast("Escolha ao menos uma categoria.", true); return; }
      try {
        await Api.publicar({
          titulo: titulo.value.trim(),
          url: url.value.trim(),
          descricao: descricao.value.trim(),
          categoriaIds: ids
        });
        toast("Vídeo enviado! Ele aparece para todos depois de aprovado.");
        location.hash = "meus";
      } catch (erro) { toast(erro.message, true); }
    }
  },
    h("h2", {}, "Publicar vídeo"),
    titulo, url, descricao,
    h("fieldset", { class: "categorias" }, h("legend", {}, "Categorias"), opcoes),
    h("button", { class: "btn", type: "submit" }, "Enviar para moderação"));

  app.replaceChildren(formulario);
}

const ROTULO_STATUS = {
  PENDENTE: "Aguardando moderação",
  APROVADO: "Aprovado",
  REPROVADO: "Reprovado"
};

async function telaMeus() {
  if (!Sessao.usuario) { location.hash = "entrar"; return; }
  app.replaceChildren(carregando());
  const videos = await Api.meusVideos();

  const itens = videos.map((v) => h("article", { class: "item" },
    h("div", { class: "item-topo" },
      h("h3", {}, v.titulo),
      h("span", { class: "selo " + v.status.toLowerCase() }, ROTULO_STATUS[v.status] || v.status)),
    h("p", { class: "autor" }, "Enviado em " + formatarData(v.dataPublicacao)),
    v.justificativa ? h("p", { class: "aviso erro" }, "Motivo da reprovação: " + v.justificativa) : null,
    h("button", {
      class: "btn perigo",
      onclick: async () => {
        if (!confirm("Excluir este vídeo?")) return;
        try {
          await Api.excluir(v.id);
          toast("Vídeo excluído.");
          telaMeus().catch(mostrarErroNaTela);
        } catch (erro) { toast(erro.message, true); }
      }
    }, "Excluir")));

  app.replaceChildren(
    h("h2", {}, "Meus vídeos"),
    ...(itens.length ? itens : [h("p", { class: "aviso" }, "Você ainda não publicou nenhum vídeo.")]));
}

async function telaModeracao() {
  if (!Sessao.usuario || !Sessao.usuario.moderador) { location.hash = "inicio"; return; }
  app.replaceChildren(carregando());
  const pendentes = await Api.pendentes();

  const itens = pendentes.map((v) => {
    const justificativa = h("input", { maxlength: "500", placeholder: "Justificativa (obrigatória para reprovar)" });

    async function decidir(decisao) {
      try {
        await Api.moderar(v.id, { decisao, justificativa: justificativa.value.trim() });
        toast(decisao === "APROVADO" ? "Vídeo aprovado." : "Vídeo reprovado.");
        telaModeracao().catch(mostrarErroNaTela);
      } catch (erro) { toast(erro.message, true); }
    }

    return h("article", { class: "item" },
      h("h3", {}, v.titulo),
      h("p", { class: "autor" }, "por " + v.autor + " · " + formatarData(v.dataPublicacao)),
      playerDe(v),
      v.descricao ? h("p", {}, v.descricao) : null,
      justificativa,
      h("div", { class: "acoes" },
        h("button", { class: "btn", onclick: () => decidir("APROVADO") }, "Aprovar"),
        h("button", { class: "btn perigo", onclick: () => decidir("REPROVADO") }, "Reprovar")));
  });

  app.replaceChildren(
    h("h2", {}, "Vídeos aguardando moderação"),
    ...(itens.length ? itens : [h("p", { class: "aviso" }, "Nenhum vídeo pendente. 🎉")]));
}

window.addEventListener("hashchange", rotear);
rotear();
