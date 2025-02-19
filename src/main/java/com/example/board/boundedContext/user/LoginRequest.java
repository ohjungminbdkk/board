package com.example.board.boundedContext.user;

public class LoginRequest {
    private String username;
    private String password;

    // 기본 생성자 (JSON -> 객체 변환 시 필요)
    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
