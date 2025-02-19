package com.example.board.boundedContext.user;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SiteUser siteUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 사용자 역할(Role) 설정
        GrantedAuthority authority = new SimpleGrantedAuthority(
                "admin".equals(username) ? UserRole.ADMIN.getValue() : UserRole.USER.getValue());

        // UserDetails 객체 반환 (비밀번호 검증은 AuthenticationManager가 수행)
        return new User(siteUser.getUsername(), siteUser.getPassword(), List.of(authority));
    }
}
