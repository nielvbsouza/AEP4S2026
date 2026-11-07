// Endereço da API (back-end Spring Boot).
//
// - Rodando no seu computador (localhost): usa o Spring Boot do IntelliJ na porta 8080.
// - Publicado no GitHub Pages: usa o endereço do back-end publicado (Render, Railway etc.).
//
// DEPOIS DE PUBLICAR O BACK-END, troque o endereço abaixo pela URL dele (sem barra no final).
const URL_API_PRODUCAO = "https://SEU-BACKEND.onrender.com";

const API_URL =
  location.hostname === "localhost" || location.hostname === "127.0.0.1"
    ? "http://localhost:8080"
    : URL_API_PRODUCAO;
