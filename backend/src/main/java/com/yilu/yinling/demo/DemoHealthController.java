package com.yilu.yinling.demo;
import com.yilu.yinling.common.response.Result; import org.springframework.context.annotation.Profile; import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestController @Profile("demo") @RequestMapping("/api/v1/system") public class DemoHealthController { @GetMapping("/health") public Result<Map<String,String>> health(){return Result.ok(Map.of("mysql","MOCK","redis","MOCK","vectorDB","MOCK","llm","MOCK"));} }
