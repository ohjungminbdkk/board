<template>
  <div class="container my-3">
    <h2 class="border-bottom py-2">질문 작성</h2>
    <div class="card my-3">
      <div class="card-body">
        <input
          v-model="subject"
          class="form-control my-2"
          placeholder="질문의 제목을 입력하세요"
        />
        <textarea
          v-model="content"
          class="form-control my-2"
          rows="5"
          placeholder="질문의 내용을 입력하세요"
        ></textarea>
        <button @click="submitQuestion" class="btn btn-success btn-sm">
          등록
        </button>
        <router-link to="/" class="btn btn-secondary btn-sm mx-2">
          취소
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "@/api";

export default {
  data() {
    return {
      subject: "",
      content: "",
    };
  },
  methods: {
    async submitQuestion() {
      // 빈 칸 체크
      if (!this.subject.trim() || !this.content.trim()) {
        alert("제목과 내용을 입력하세요.");
        return;
      }
      // 길이 제한 체크
      if (this.subject.trim().length > 200) {
        alert("제목은 200자 이내로 입력하세요.");
        return;
      }
      if (this.content.trim().length > 20000) {
        alert("내용은 20000자 이내로 입력하세요.");
        return;
      }

      try {
        await axios.post(
          "/questions/create",
          { subject: this.subject, content: this.content },
          { withCredentials: true }
        );
        this.$router.push("/");
      } catch (error) {
        console.error("질문 등록 실패:", error);
      }
    },
  },
};
</script>
