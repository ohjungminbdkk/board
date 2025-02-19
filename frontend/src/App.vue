<template>
  <div id="app">
    <QuestionNavbar :key="authKey" />
    <div class="container mt-3">
      <router-view @update-auth="updateAuth" />
    </div>
  </div>
</template>

<script>
import QuestionNavbar from "./components/QuestionNavbar.vue";

export default {
  components: {
    QuestionNavbar,
  },
  data() {
    return {
      authKey: 0, // 네비게이션 바를 갱신하기 위한 key
    };
  },
  methods: {
    updateAuth() {
      this.authKey++; // 변경 시마다 네비게이션 바를 강제로 갱신
    },
  },
};
</script>

<style>
@import url("https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css");

body {
  font-family: Arial, sans-serif;
}
.container {
  max-width: 900px;
  margin: 0 auto;
}
</style>
