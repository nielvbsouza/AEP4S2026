const URL_API_PRODUCAO = "https://recicla-view.onrender.com";

const API_URL =
  location.hostname === "localhost" || location.hostname === "127.0.0.1"
    ? "http://localhost:8080"
    : URL_API_PRODUCAO;
