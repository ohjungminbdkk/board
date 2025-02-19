package com.example.board.boundedContext.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.board.boundedContext.DataNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 사용자 생성 (기본 ROLE_USER 추가)
    public void create(String username, String email, String password) {
         if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }

        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // 비밀번호 암호화 저장
        user.setRole(UserRole.USER);
        userRepository.save(user);
    }

    // 사용자 조회 (보안상 메시지 통일)
    public SiteUser getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("사용자가 존재하지 않습니다."));
    }

    // 사용자 인증 (비밀번호 검증)
    public boolean authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false); // 사용자가 없을 경우 false 반환
    }
}