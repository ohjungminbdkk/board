import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import api from "./api"; // 공통API
import { createPinia } from "pinia"; // pinia import

const app = createApp(App);
// 전역 변수로 API 사용 가능하도록 추가
app.config.globalProperties.$api = api;
app.use(createPinia()); // Pinia 적용
app.use(router); // Vue Router 사용
app.mount("#app"); // 애플리케이션 실행