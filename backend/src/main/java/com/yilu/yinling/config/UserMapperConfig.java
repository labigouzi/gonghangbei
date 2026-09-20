package com.yilu.yinling.config;
import org.mybatis.spring.annotation.MapperScan; import org.springframework.context.annotation.*;
@Configuration @Profile("!test & !demo") @MapperScan("com.yilu.yinling.user.mapper") public class UserMapperConfig {}
