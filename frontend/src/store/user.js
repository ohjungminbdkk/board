import { defineStore } from "pinia";
import axios from "@/api";

export const useUserStore = defineStore("user", {
  state: () => ({ currentUser: null }),
  actions: {
    async fetchUser() {
      try {
        const response = await axios.get("/user/me");
        this.currentUser = response.data.username;
      } catch {
        this.currentUser = null;
      }
    },
  },
});
