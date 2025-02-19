<template>
  <div class="container my-3">
    <form @submit.prevent="handleLogin">
      <div v-if="errorMessage" class="alert alert-danger">
        {{ errorMessage }}
      </div>
      <div class="mb-3">
        <label for="username" class="form-label">사용자ID</label>
        <input
          type="text"
          id="username"
          class="form-control"
          v-model="username"
        />
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">비밀번호</label>
        <input
          type="password"
          id="password"
          class="form-control"
          v-model="password"
        />
      </div>
      <button type="submit" class="btn btn-primary">로그인</button>
    </form>
  </div>
</template>

<script>
import axios from "@/api";
import { useUserStore } from "@/store/user";
import { useRouter } from "vue-router";
import { ref } from "vue";

export default {
  setup() {
    const username = ref("");
    const password = ref("");
    const errorMessage = ref("");
    const userStore = useUserStore();
    const router = useRouter();

    const handleLogin = async () => {
      try {
        await axios.post("/user/login", {
          username: username.value,
          password: password.value,
        });
        await userStore.fetchUser();
        router.push("/");
      } catch (error) {
        errorMessage.value =
          "로그인 실패: " + (error.response?.data?.error || "서버 오류");
      }
    };

    return { username, password, errorMessage, handleLogin };
  },
};
</script>
