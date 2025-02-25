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
          v-for="question in questions"
          :key="question.id"
          class="text-center"
        >
          <td>{{ question.id }}</td>
          <td class="text-start">
            <router-link :to="`/questions/${question.id}`" class="custom-link">
              {{ question.subject }}
              <span v-if="question.commentCount > 0" class="comment-count">
                [{{ question.commentCount }}]
              </span>
            </router-link>
          </td>
          <td>{{ question.author?.username || "익명" }}</td>
          <td>{{ formatDate(question.createDate) }}</td>
        </tr>
      </tbody>
    </table>

    <!-- 페이징 컨트롤 -->
    <div class="d-flex justify-content-center align-items-center my-3">
      <button
        @click="goToPage(page - 1)"
        :disabled="page === 0"
        class="btn btn-sm btn-secondary mx-1"
      >
        이전
      </button>

      <!-- 좌측 현재 윈도우의 최상위 번호가 전체 최고 번호(totalPages)보다 작으면 표시 -->
      <span v-if="visiblePageNumbers[0] < totalPages" class="mx-1">...</span>

      <!-- 페이지 번호 버튼들 (내림차순) -->
      <button
        v-for="n in visiblePageNumbers"
        :key="n"
        @click="goToPage(totalPages - n)"
        class="btn btn-sm mx-1"
        :class="{
          'btn-primary': page === totalPages - n,
          'btn-light': page !== totalPages - n,
        }"
      >
        {{ n }}
      </button>

      <!-- 우측 현재 윈도우의 최하위 번호가 1보다 크면 표시 -->
      <span
        v-if="visiblePageNumbers[visiblePageNumbers.length - 1] > 1"
        class="mx-1"
        >...</span
      >

      <button
        @click="goToPage(page + 1)"
        :disabled="page >= totalPages - 1"
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
        placeholder="검색어를 입력하세요(제목 & 내용(질문/답변))"
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

    // 전체 페이지 수 (계산된 값, 예: Math.ceil(totalElements / pageSize))
    const totalPages = computed(() =>
      Math.ceil(totalElements.value / pageSize.value)
    );

    // visiblePageNumbers: 최대 10개의 내림차순 페이지 번호 (1-based)
    const visiblePageNumbers = computed(() => {
      const total = totalPages.value;
      const windowSize = 10;
      if (total <= windowSize) {
        // 전체가 10 이하이면 전체를 내림차순으로 반환
        return Array.from({ length: total }, (_, i) => total - i);
      } else {
        // 현재 페이지의 표시 번호 (내림차순)
        // 현재 페이지 index(0-indexed) → displayed = total - page
        const displayedCurrent = total - page.value;
        let startDisplayed = displayedCurrent + Math.floor(windowSize / 2);
        if (startDisplayed > total) startDisplayed = total;
        let endDisplayed = startDisplayed - windowSize + 1;
        if (endDisplayed < 1) {
          endDisplayed = 1;
          startDisplayed = endDisplayed + windowSize - 1;
          if (startDisplayed > total) startDisplayed = total;
        }
        const pages = [];
        for (let n = startDisplayed; n >= endDisplayed; n--) {
          pages.push(n);
        }
        return pages;
      }
    });

    const fetchCommentCount = async (questionId) => {
      try {
        const response = await axios.get(`/questions/${questionId}/answers`);
        return response.data.length;
      } catch (error) {
        console.error(`댓글 수 가져오기 실패 (질문 ID: ${questionId}):`, error);
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
        console.error("질문 목록 불러오기 실패:", error);
      }
    };

    const goToPage = (p) => {
      if (p < 0 || p >= totalPages.value) return;
      page.value = p;
      fetchQuestions();
    };

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
      visiblePageNumbers,
      fetchQuestions,
      prevPage,
      nextPage,
      goToPage,
      formatDate: (dateString) =>
        dayjs(dateString).format("YYYY-MM-DD HH:mm:ss"),
    };
  },
};
</script>

<style scoped>
.custom-link {
  color: black;
  text-decoration: none;
  transition: color 0.2s;
}

.custom-link:hover {
  color: #007bff;
  text-decoration: none;
}

.comment-count {
  color: gray;
  margin-left: 5px;
}
</style>
