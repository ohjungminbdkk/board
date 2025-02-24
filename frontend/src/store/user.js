import { defineStore } from "pinia";
import axios from "@/api";

export const useUserStore = defineStore("user", {
  state: () => ({ currentUser: null }),
  actions: {
    async fetchUser() {
      try {
        const response = await axios.get("/user/me");
        this.currentUser = response.data.username;
      } catch (error) {
        this.currentUser = null;
        if (error.response && error.response.status === 404) {
          throw new Error("USER_NOT_FOUND"); // 404면 회원가입 페이지로 가도록 에러 발생
        }
      }
    },
  },
});