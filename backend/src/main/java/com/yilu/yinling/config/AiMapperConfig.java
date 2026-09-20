package com.yilu.yinling.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test & !demo")
@MapperScan("com.yilu.yinling.ai.mapper")
public class AiMapperConfig { }
