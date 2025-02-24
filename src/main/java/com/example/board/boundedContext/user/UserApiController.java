package com.example.board.boundedContext.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserApiController {

	private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);
	private final UserService userService;
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;

	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Valid @RequestBody UserCreateForm userCreateForm) {

		if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Collections.singletonMap("error", "비밀번호가 일치하지 않습니다."));
		}

		try {
			userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
			return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("message", "회원가입 성공"));
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", "이미 등록된 사용자입니다."));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Collections.singletonMap("error", e.getMessage()));
		}
	}

	// 로그인
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody SiteUser siteUser, HttpServletResponse response) {

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(siteUser.getUsername(), siteUser.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtUtil.generateToken(siteUser.getUsername());
		jwtUtil.addTokenToCookie(response, token);

		return ResponseEntity.ok(Map.of("message", "로그인 성공"));
	}

	// 현재 로그인한 사용자 정보 반환
	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
		String token = jwtUtil.getTokenFromCookie(request);

		if (token == null) {
			// 토큰이 없을 경우 404 반환 -> 회원가입 페이지로 리디렉션 유도
			logger.warn("JWT 토큰 없음! token = null");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "토큰 없음 - 회원가입 필요"));
		}

		if (!jwtUtil.validateToken(token)) {
			logger.warn("JWT 인증 실패! token = {}", token);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "인증 실패 - 토큰 유효하지 않음"));
		}

		String username = jwtUtil.getUsernameFromToken(token);
		return ResponseEntity.ok(Map.of("username", username));
	}

	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		jwtUtil.addTokenToCookie(response, ""); // 쿠키 만료 처리
		return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
	}
}