package com.yilu.yinling.user.dto;
import jakarta.validation.constraints.NotBlank;
public class AuthDto {
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record RegisterRequest(@NotBlank String username, @NotBlank String password, String realName, String phone) {}
    public record UserView(Long id, String username, String realName, String phone, String avatarUrl) {}
    public record LoginResponse(String token, UserView userInfo) {}
}
