package com.example.board.boundedContext.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING) // Enum 값을 String 형태로 저장
    private UserRole role; // 역할 추가 (USER, ADMIN)

}