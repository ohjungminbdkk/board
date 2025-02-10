<template>
  <div>
    <h2>질문 목록</h2>
    <ul>
      <li v-for="question in questions" :key="question.id">
        {{ question.subject }} - {{ question.content }}
      </li>
    </ul>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      questions: [],
    };
  },
  mounted() {
    axios
      .get("http://localhost:8080/api/questions?page=0")
      .then((response) => {
        this.questions = response.data.content;
      })
      .catch((error) => console.error("API 요청 오류:", error));
  },
};
</script>
