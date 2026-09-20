package com.yilu.yinling.user.service;
import com.yilu.yinling.user.dto.AuthDto;
public interface UserService { AuthDto.LoginResponse login(AuthDto.LoginRequest request); AuthDto.UserView register(AuthDto.RegisterRequest request); AuthDto.UserView current(Long id); AuthDto.UserView update(Long id, AuthDto.UserView request); }
