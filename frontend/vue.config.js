const { defineConfig } = require("@vue/cli-service");

module.exports = defineConfig({
  transpileDependencies: true,
  outputDir: "../src/main/resources/static", // Vue 빌드 디렉토리
  devServer: {
    proxy: {
      "/api": {
        target: "http://localhost:8080", // Spring Boot 서버 주소
        changeOrigin: true, // CORS 문제 방지
        pathRewrite: { "^/api": "" }, // "/api" 제거 후 백엔드로 요청
      },
    },
  },
});