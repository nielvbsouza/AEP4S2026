// Guarda o login no navegador (localStorage) e faz as chamadas para a API.

const Sessao = {
  get token() {
    try { return localStorage.getItem("rv_token"); } catch { return null; }
  },
  get usuario() {
    try { return JSON.parse(localStorage.getItem("rv_usuario") || "null"); } catch { return null; }
  },
  salvar(auth) {
    localStorage.setItem("rv_token", auth.token);
    localStorage.setItem("rv_usuario", JSON.stringify(auth.usuario));
  },
  limpar() {
    try {
      localStorage.removeItem("rv_token");
      localStorage.removeItem("rv_usuario");
    } catch { /* ignora */ }
  }
};

async function chamar(metodo, caminho, corpo) {
  const opcoes = { method: metodo, headers: {} };
  if (corpo !== undefined) {
    opcoes.headers["Content-Type"] = "application/json";
    opcoes.body = JSON.stringify(corpo);
  }
  if (Sessao.token) {
    opcoes.headers["Authorization"] = "Bearer " + Sessao.token;
  }

  let resposta;
  try {
    resposta = await fetch(API_URL + caminho, opcoes);
  } catch (e) {
    throw new Error("Não foi possível conectar à API. Se o servidor estava parado, aguarde cerca de 1 minuto e tente de novo.");
  }

  if (resposta.status === 204) return null;

  let dados = null;
  try { dados = await resposta.json(); } catch { /* resposta sem corpo */ }

  if (!resposta.ok) {
    if (resposta.status === 401 && Sessao.token) {
      Sessao.limpar(); // token vencido ou inválido
      if (typeof desenharMenu === "function") desenharMenu();
    }
    throw new Error((dados && dados.message) || "Erro " + resposta.status);
  }
  return dados;
}

const Api = {
  registrar: (dados) => chamar("POST", "/api/auth/registro", dados),
  login: (dados) => chamar("POST", "/api/auth/login", dados),

  categorias: () => chamar("GET", "/api/categorias"),

  videos: (categoriaId, busca) => {
    const p = new URLSearchParams();
    if (categoriaId) p.set("categoriaId", categoriaId);
    if (busca) p.set("busca", busca);
    const q = p.toString();
    return chamar("GET", "/api/videos" + (q ? "?" + q : ""));
  },
  video: (id) => chamar("GET", "/api/videos/" + id),
  meusVideos: () => chamar("GET", "/api/videos/meus"),
  publicar: (dados) => chamar("POST", "/api/videos", dados),
  excluir: (id) => chamar("DELETE", "/api/videos/" + id),

  comentarios: (videoId) => chamar("GET", "/api/videos/" + videoId + "/comentarios"),
  comentar: (videoId, dados) => chamar("POST", "/api/videos/" + videoId + "/comentarios", dados),

  pendentes: () => chamar("GET", "/api/moderacao/pendentes"),
  moderar: (videoId, dados) => chamar("POST", "/api/moderacao/videos/" + videoId, dados)
};
