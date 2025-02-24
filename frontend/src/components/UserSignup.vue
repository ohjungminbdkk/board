<template>
  <div class="container my-5">
    <h2 class="text-center mb-4">회원가입</h2>
    <form @submit.prevent="handleSignup" class="p-4 border rounded shadow">
      <div v-if="successMessage" class="alert alert-success">
        {{ successMessage }}
      </div>
      <div v-if="errorMessage" class="alert alert-danger">
        {{ errorMessage }}
      </div>

      <div class="mb-3">
        <label for="username" class="form-label">사용자 ID</label>
        <input
          v-model="username"
          type="text"
          class="form-control"
          id="username"
          required
        />
      </div>

      <div class="mb-3">
        <label for="email" class="form-label">이메일</label>
        <input
          v-model="email"
          type="email"
          class="form-control"
          id="email"
          required
        />
      </div>

      <div class="mb-3">
        <label for="password1" class="form-label">비밀번호</label>
        <input
          v-model="password1"
          type="password"
          class="form-control"
          id="password1"
          required
        />
      </div>

      <div class="mb-3">
        <label for="password2" class="form-label">비밀번호 확인</label>
        <input
          v-model="password2"
          type="password"
          class="form-control"
          id="password2"
          required
        />
      </div>

      <button type="submit" class="btn btn-primary w-100">가입하기</button>
    </form>
  </div>
</template>

<script>
import axios from "@/api";
import { ref } from "vue";
import { useRouter } from "vue-router";

export default {
  setup() {
    const router = useRouter();
    const username = ref("");
    const email = ref("");
    const password1 = ref("");
    const password2 = ref("");
    const errorMessage = ref("");
    const successMessage = ref("");

    // 회원가입 처리 함수
    const handleSignup = async () => {
      if (password1.value !== password2.value) {
        errorMessage.value = "비밀번호가 일치하지 않습니다.";
        return;
      }

      try {
        const response = await axios.post("/user/signup", {
          username: username.value,
          email: email.value,
          password1: password1.value,
          password2: password2.value,
        });

        if (response.status === 201) {
          successMessage.value =
            "회원가입이 완료되었습니다! 로그인 페이지로 이동합니다.";
          setTimeout(() => router.push("/login"), 1500);
        }
      } catch (error) {
        console.error("회원가입 실패:", error);
        errorMessage.value =
          error.response?.data?.error || "회원가입에 실패했습니다.";
      }
    };

    return {
      username,
      email,
      password1,
      password2,
      errorMessage,
      successMessage,
      handleSignup,
    };
  },
};
</script>

<style scoped>
.container {
  max-width: 500px;
}
</style>
