<template>
  <div class="container my-3">
    <!-- 수정 모드 -->
    <div v-if="editMode">
      <input v-model="editedSubject" class="form-control my-2" />
      <textarea
        v-model="editedContent"
        class="form-control my-2"
        rows="5"
      ></textarea>
      <div class="mt-2">
        <button @click="saveEdit" class="btn btn-success btn-sm">저장</button>
        <button @click="cancelEdit" class="btn btn-secondary btn-sm">
          취소
        </button>
      </div>
    </div>

    <!-- 일반 모드 -->
    <div v-else>
      <h2 class="border-bottom py-2">{{ question.subject }}</h2>
      <div class="card my-3">
        <div class="card-body">
          <div class="card-text my-2" style="white-space: pre-line">
            {{ question.content }}
          </div>
          <div class="d-flex justify-content-end mt-3">
            <div class="badge bg-light text-dark p-2 text-end">
              <div class="fw-bold">{{ question.author?.username }}</div>
              <div>created at: {{ formatDate(question.createDate) }}</div>
              <div
                v-if="
                  question.updateDate &&
                  question.updateDate !== question.createDate
                "
              >
                updated at: {{ formatDate(question.updateDate) }}
              </div>
            </div>
          </div>

          <div class="d-flex justify-content-end mt-2">
            <button
              v-if="isAuthor"
              @click="startEdit"
              class="btn btn-sm btn-warning me-1"
            >
              수정
            </button>
            <button
              v-if="isAuthor"
              @click="deleteQuestion"
              class="btn btn-sm btn-danger"
            >
              삭제
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 답변 섹션 -->
    <div class="answer-section card shadow-sm">
      <div class="card-header bg-primary text-white">
        <h5 class="mb-0">💬 답변 목록</h5>
      </div>
      <div class="card-body">
        <AnswerList v-if="question.id" :questionId="question.id" />
      </div>
    </div>
  </div>
</template>

<script>
import axios from "@/api";
import dayjs from "dayjs";
import AnswerList from "./AnswerList.vue";
import { useUserStore } from "@/store/user";
import { computed, ref, onMounted } from "vue";

export default {
  components: { AnswerList },
  setup() {
    const userStore = useUserStore();
    const question = ref({});
    const editMode = ref(false);
    const editedSubject = ref("");
    const editedContent = ref("");

    const currentUser = computed(() => userStore.currentUser);
    const isAuthor = computed(
      () =>
        currentUser.value &&
        question.value.author?.username === currentUser.value
    );

    const fetchQuestion = async () => {
      try {
        const id = window.location.pathname.split("/").pop();
        const response = await axios.get(`/questions/${id}`, {
          withCredentials: true,
        });
        question.value = response.data;
      } catch (error) {
        console.error("질문 데이터 불러오기 실패:", error);
      }
    };

    const startEdit = () => {
      editMode.value = true;
      editedSubject.value = question.value.subject;
      editedContent.value = question.value.content;
    };

    const saveEdit = async () => {
      try {
        await axios.put(
          `/questions/${question.value.id}`,
          { subject: editedSubject.value, content: editedContent.value },
          { withCredentials: true }
        );
        await fetchQuestion();
        editMode.value = false;
      } catch (error) {
        console.error("질문 수정 실패:", error);
      }
    };

    const cancelEdit = () => {
      editMode.value = false;
      editedSubject.value = question.value.subject;
      editedContent.value = question.value.content;
    };

    const deleteQuestion = async () => {
      if (!confirm("정말 삭제하시겠습니까?")) return;
      try {
        await axios.delete(`/questions/${question.value.id}`, {
          withCredentials: true,
        });
        window.location.href = "/";
      } catch (error) {
        console.error("질문 삭제 실패:", error);
      }
    };

    onMounted(async () => {
      await userStore.fetchUser();
      await fetchQuestion();
    });

    return {
      question,
      editMode,
      editedSubject,
      editedContent,
      currentUser,
      isAuthor,
      fetchQuestion,
      startEdit,
      saveEdit,
      cancelEdit,
      deleteQuestion,
      formatDate: (dateString) =>
        dayjs(dateString).format("YYYY-MM-DD HH:mm:ss"),
    };
  },
};
</script>

<style scoped>
.container {
  margin-top: 20px;
}

/* 답변 영역 구분 (정상 적용 유지) */
.answer-section {
  margin-top: 30px;
  border-radius: 12px;
}

.answer-section .card-header {
  font-weight: bold;
  font-size: 1.1rem;
  border-bottom: none;
}
</style>
