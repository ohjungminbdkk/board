<template>
  <div class="container my-3">
    <div class="d-flex justify-content-between align-items-center">
      <h2 class="border-bottom py-2">질문 목록</h2>
      <router-link
        v-if="isAuthenticated"
        to="/questions/new"
        class="btn btn-primary btn-sm"
      >
        질문 작성하기
      </router-link>
    </div>

    <table class="table">
      <thead class="table-dark">
        <tr class="text-center">
          <th>번호</th>
          <th style="width: 50%">제목</th>
          <th>글쓴이</th>
          <th>작성일시</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="(question, index) in questions"
          :key="question.id"
          class="text-center"
        >
          <td>
            {{
              totalElements ? totalElements - (page * pageSize + index) : "-"
            }}
          </td>
          <td class="text-start">
            <router-link :to="`/questions/${question.id}`" class="custom-link">
              {{ question.subject }}
              <span v-if="question.commentCount > 0" class="comment-count"
                >[{{ question.commentCount }}]</span
              >
            </router-link>
          </td>
          <td>{{ question.author?.username || "익명" }}</td>
          <td>{{ formatDate(question.createDate) }}</td>
        </tr>
      </tbody>
    </table>

    <!-- 페이징 컨트롤 -->
    <div class="d-flex justify-content-center">
      <button
        @click="prevPage"
        :disabled="page === 0"
        class="btn btn-sm btn-secondary mx-1"
      >
        이전
      </button>
      <span class="mx-2">페이지 {{ page + 1 }} / {{ totalPages }}</span>
      <button
        @click="nextPage"
        :disabled="page >= totalPages.value - 1"
        class="btn btn-sm btn-secondary mx-1"
      >
        다음
      </button>
    </div>

    <!-- 검색 입력창 -->
    <div class="input-group mt-3">
      <input
        v-model="searchKeyword"
        @keyup.enter="fetchQuestions"
        type="text"
        class="form-control"
        placeholder="검색어를 입력하세요"
      />
      <button @click="fetchQuestions" class="btn btn-outline-secondary">
        검색
      </button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import axios from "@/api";
import dayjs from "dayjs";
import { useUserStore } from "@/store/user";

export default {
  setup() {
    const userStore = useUserStore();
    const questions = ref([]);
    const page = ref(0);
    const pageSize = ref(10);
    const totalElements = ref(0);
    const searchKeyword = ref("");

    const isAuthenticated = computed(() => !!userStore.currentUser);

    const fetchCommentCount = async (questionId) => {
      try {
        const response = await axios.get(`/questions/${questionId}/answers`);
        return response.data.length;
      } catch (error) {
        console.error(
          `❌ 댓글 수 가져오기 실패 (질문 ID: ${questionId}):`,
          error
        );
        return 0;
      }
    };

    const fetchQuestions = async () => {
      try {
        const response = await axios.get(
          `/questions?page=${page.value}&kw=${searchKeyword.value}`,
          { withCredentials: true }
        );
        const questionList = response.data.content;

        const questionsWithCommentCount = await Promise.all(
          questionList.map(async (q) => ({
            ...q,
            commentCount: await fetchCommentCount(q.id),
          }))
        );

        questions.value = questionsWithCommentCount;
        totalElements.value = response.data.totalElements || 0;
      } catch (error) {
        console.error("❌ 질문 목록 불러오기 실패:", error);
      }
    };

    const totalPages = computed(() =>
      Math.ceil(totalElements.value / pageSize.value)
    );

    const prevPage = () => {
      if (page.value > 0) {
        page.value--;
        fetchQuestions();
      }
    };

    const nextPage = () => {
      if (page.value < totalPages.value - 1) {
        page.value++;
        fetchQuestions();
      }
    };

    onMounted(async () => {
      await userStore.fetchUser();
      fetchQuestions();
    });

    return {
      questions,
      page,
      pageSize,
      totalElements,
      searchKeyword,
      isAuthenticated,
      totalPages,
      fetchQuestions,
      prevPage,
      nextPage,
      formatDate: (dateString) =>
        dayjs(dateString).format("YYYY-MM-DD HH:mm:ss"),
    };
  },
};
</script>

<style scoped>
/* 제목 링크 기본: 검정색 + 언더라인 없음 */
.custom-link {
  color: black;
  text-decoration: none;
  transition: color 0.2s;
}

/* 마우스 오버 시: 파란색으로 색상 변경 */
.custom-link:hover {
  color: #007bff;
  text-decoration: none;
}

/* 댓글 수 스타일: 회색 + 한 칸 띄움 */
.comment-count {
  color: gray;
  margin-left: 5px;
}
</style>
