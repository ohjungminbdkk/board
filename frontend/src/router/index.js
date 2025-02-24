import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/store/user";
import QuestionList from "@/components/QuestionList.vue";
import QuestionDetail from "@/components/QuestionDetail.vue";
import QuestionForm from "@/components/QuestionForm.vue";
import UserLogin from "@/components/UserLogin.vue";
import UserSignup from "@/components/UserSignup.vue";

const routes = [
	{ path: "/", component: QuestionList },
	{ path: "/questions/:id", component: QuestionDetail },
	{
		path: "/questions/new",
		component: QuestionForm,
		meta: { requiresAuth: true },
	},
	{ path: "/login", component: UserLogin, meta: { guestOnly: true } },
	{ path: "/signup", component: UserSignup, meta: { guestOnly: true } },
];

const router = createRouter({
	history: createWebHistory(),
	routes,
});

//로그인 필요 페이지 접근 제한 및 게스트 전용 페이지 처리
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  try {
    await userStore.fetchUser(); // 쿠키 기반 사용자 정보 불러오기

    // 로그인 필요 페이지인데 로그인 안 되어 있으면 로그인 페이지로 리디렉션
    if (to.meta.requiresAuth && !userStore.currentUser) {
      next("/login");
    }
    // 로그인된 사용자가 로그인/회원가입 페이지 접근 시 홈으로 리디렉션
    else if (to.meta.guestOnly && userStore.currentUser) {
      next("/");
    } else {
      next(); // 정상 이동
    }
  } catch (error) {
    console.error("유저 확인 중 오류:", error);
    next("/login");
  }
});

export default router;
