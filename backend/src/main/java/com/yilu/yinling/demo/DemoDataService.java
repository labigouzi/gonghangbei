package com.yilu.yinling.demo;

import com.yilu.yinling.common.exception.BusinessException;
import com.yilu.yinling.security.JwtTokenProvider;
import com.yilu.yinling.user.dto.AuthDto;
import com.yilu.yinling.user.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.util.List;

/** In-memory identity provider for exhibition computers. */
@Service
@Profile("demo")
public class DemoDataService implements UserService {
    private static final Long ADMIN_ID = 1L;
    private final JwtTokenProvider jwt;
    public DemoDataService(JwtTokenProvider jwt) { this.jwt = jwt; }
    private AuthDto.UserView admin() { return new AuthDto.UserView(ADMIN_ID, "admin", "演示管理员", "13800000000", null); }
    @Override public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        if (!"admin".equals(request.username()) || !"123456".equals(request.password())) throw new BusinessException("用户名或密码错误");
        return new AuthDto.LoginResponse(jwt.create(ADMIN_ID, "admin", List.of("ADMIN", "USER")), admin());
    }
    @Override public AuthDto.UserView register(AuthDto.RegisterRequest request) { throw new BusinessException("Demo模式不支持注册"); }
    @Override public AuthDto.UserView current(Long id) { return admin(); }
    @Override public AuthDto.UserView update(Long id, AuthDto.UserView request) { return admin(); }
}
