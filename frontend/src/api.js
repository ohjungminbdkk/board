import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true, // 쿠키 인증 활성화
  headers: { "Content-Type": "application/json" },
});

export default api;
