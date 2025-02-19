<template>
  <nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container-fluid">
      <a class="navbar-brand" href="/">질문과 답변</a>

      <div class="d-flex align-items-center">
        <!-- 로그인한 사용자 이름 표시 -->
        <span v-if="currentUser" class="me-3">
          안녕하세요, {{ currentUser }} 님!
        </span>

        <!-- 로그인하지 않은 경우 회원가입 버튼 추가 -->
        <router-link
          v-if="!isAuthenticated"
          to="/signup"
          class="btn btn-outline-success me-2"
        >
          회원가입
        </router-link>

        <!-- 로그인 / 로그아웃 버튼 -->
        <button
          class="btn btn-outline-danger me-2"
          v-if="isAuthenticated"
          @click="logout"
        >
          로그아웃
        </button>
        <router-link v-else to="/login" class="btn btn-outline-primary">
          로그인
        </router-link>
      </div>
    </div>
  </nav>
</template>

<script>
import axios from "@/api";
import { useUserStore } from "@/store/user";
import { computed, onMounted } from "vue";
import { useRouter } from "vue-router";

export default {
  setup() {
    const userStore = useUserStore();
    const router = useRouter();

    // 로그인한 사용자 이름 가져오기
    const currentUser = computed(() => userStore.currentUser);

    // 로그인 여부 확인
    const isAuthenticated = computed(() => !!userStore.currentUser);

    // 로그인한 사용자 정보 가져오기
    const fetchUser = async () => {
      try {
        await userStore.fetchUser();
      } catch (error) {
        console.error("❌ 사용자 정보 가져오기 실패:", error);
      }
    };

    onMounted(() => {
      fetchUser(); // 컴포넌트 마운트 시 로그인 상태 체크
    });

    // 로그아웃
    const logout = async () => {
      try {
        await axios.post("/user/logout", {}, { withCredentials: true });
        userStore.currentUser = null;
        router.push("/login");
      } catch (error) {
        console.error("로그아웃 실패:", error.response?.data || error.message);
      }
    };

    return {
      userStore,
      currentUser,
      isAuthenticated,
      logout,
    };
  },
};
</script>
