package com.yilu.yinling;

import com.yilu.yinling.security.JwtTokenProvider;
import com.yilu.yinling.user.dto.AuthDto;
import com.yilu.yinling.user.entity.User;
import com.yilu.yinling.user.mapper.UserMapper;
import com.yilu.yinling.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthLoginUnitTest {
    @Test void adminPasswordProducesJwtAndRoleLookupUsesMapper() {
        User user = new User(); user.setId(1L); user.setUsername("admin");
        user.setPassword("$2a$10$WdWW8xEypD0klawAzoBXsuZ1qHb7yu3iwt1gTdTqiQzAuxVSM48y."); user.setStatus(1);
        UserMapper mapper = mock(UserMapper.class); when(mapper.selectOne(any())).thenReturn(user); when(mapper.selectRoleCodes(1L)).thenReturn(List.of("ADMIN"));
        var service = new UserServiceImpl(mapper, new BCryptPasswordEncoder(), new JwtTokenProvider("yinling-demo-secret-key-for-jwt-2026-strong", 86400000));
        var result = service.login(new AuthDto.LoginRequest("admin", "123456"));
        assertNotNull(result.token()); assertEquals("admin", result.userInfo().username()); verify(mapper).selectRoleCodes(1L);
    }
}
