<template>
  <div class="answer-container">
    <!-- 댓글 목록 -->
    <div v-for="answer in answers" :key="answer.id" class="answer-item">
      <div class="card my-3">
        <div class="card-body">
          <!-- 수정 모드 -->
          <div v-if="editMode === answer.comment.id">
            <textarea
              v-model="editedContent"
              class="form-control"
              rows="3"
            ></textarea>
            <div class="mt-2">
              <button @click="saveEdit(answer)" class="btn btn-success btn-sm">
                저장
              </button>
              <button @click="cancelEdit" class="btn btn-secondary btn-sm">
                취소
              </button>
            </div>
          </div>

          <!-- 일반 모드 -->
          <div v-else>
            <!-- 댓글 내용 -->
            <div class="card-text my-2" style="white-space: pre-line">
              {{ answer.comment?.content || "내용 없음" }}
            </div>

            <!-- 작성자 정보 & 작성 날짜 -->
            <div class="d-flex justify-content-end mt-3">
              <div class="badge bg-light text-dark p-2 text-end">
                <div class="fw-bold">
                  {{ answer.comment?.author?.username || "익명" }}
                </div>
                <div>
                  created at: {{ formatDate(answer.comment?.createDate) }}
                </div>
                <div
                  v-if="
                    answer.comment?.updateDate &&
                    answer.comment?.updateDate !== answer.comment?.createDate
                  "
                >
                  updated at: {{ formatDate(answer.comment?.updateDate) }}
                </div>
              </div>
            </div>

            <!-- 수정/삭제 버튼 -->
            <div class="d-flex justify-content-end mt-2">
              <button
                v-if="isAuthor(answer.comment)"
                @click="startEdit(answer)"
                class="btn btn-sm btn-warning me-1"
              >
                수정
              </button>
              <button
                v-if="isAuthor(answer.comment)"
                @click="deleteAnswer(answer.comment.id)"
                class="btn btn-sm btn-danger"
              >
                삭제
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 댓글 입력 폼 -->
    <div class="answer-form">
      <textarea
        v-model="newAnswer"
        class="form-control"
        rows="3"
        placeholder="답변을 입력하세요"
      ></textarea>
      <button @click="submitAnswer" class="btn btn-primary mt-2">
        답변 등록
      </button>
    </div>
  </div>
</template>

<script>
import { useUserStore } from "@/store/user";
import { ref, computed, onMounted } from "vue";
import axios from "@/api";
import dayjs from "dayjs";

export default {
  props: ["questionId"],
  setup(props) {
    const userStore = useUserStore();
    const answers = ref([]);
    const newAnswer = ref("");
    const editMode = ref(null);
    const editedContent = ref("");

    // 로그인한 사용자 정보
    const currentUser = computed(() => userStore.currentUser);

    // 댓글 목록 불러오기
    const fetchAnswers = async () => {
      try {
        const response = await axios.get(
          `/questions/${props.questionId}/answers`
        );
        answers.value = response.data;
        console.log("댓글 목록:", response.data);
      } catch (error) {
        console.error("답변 목록 불러오기 실패:", error);
      }
    };

    // 현재 로그인한 사용자가 댓글 작성자인지 확인
    const isAuthor = (comment) => {
      return (
        currentUser.value && comment?.author?.username === currentUser.value
      );
    };

    // 댓글 수정 시작
    const startEdit = (answer) => {
      console.log("수정 버튼 클릭됨 - answer ID:", answer.comment.id);
      editMode.value = answer.comment.id;
      editedContent.value = answer.comment?.content || "";
    };

    // 댓글 수정 저장
    const saveEdit = async (answer) => {
      try {
        await axios.put(
          `/answers/${answer.comment.id}`,
          { content: editedContent.value },
          { withCredentials: true }
        );

        await fetchAnswers();
        editMode.value = null;
        editedContent.value = "";
      } catch (error) {
        console.error("수정 실패:", error);
      }
    };

    // 수정 취소
    const cancelEdit = () => {
      editMode.value = null;
      editedContent.value = "";
    };

    // 댓글 삭제
    const deleteAnswer = async (answerId) => {
      if (!confirm("정말 삭제하시겠습니까?")) return;
      try {
        await axios.delete(`/answers/${answerId}`, { withCredentials: true });
        await fetchAnswers();
      } catch (error) {
        console.error("삭제 실패:", error);
      }
    };

    // 댓글 등록
    const submitAnswer = async () => {
      if (!newAnswer.value.trim()) {
        alert("답변을 입력해주세요.");
        return;
      }
      try {
        await axios.post(
          `/answers/${props.questionId}`,
          { content: newAnswer.value },
          { withCredentials: true }
        );

        await fetchAnswers();
        newAnswer.value = "";
      } catch (error) {
        console.error("답변 등록 실패:", error);
      }
    };

    onMounted(async () => {
      await userStore.fetchUser();
      await fetchAnswers();
    });

    return {
      answers,
      newAnswer,
      editMode,
      editedContent,
      submitAnswer,
      deleteAnswer,
      startEdit,
      saveEdit,
      cancelEdit,
      isAuthor,
      formatDate: (dateString) => dayjs(dateString).format("YYYY-MM-DD HH:mm"),
    };
  },
};
</script>

<style scoped>
.answer-container {
  margin-top: 20px;
}
.answer-form {
  margin-top: 15px;
}
</style>
