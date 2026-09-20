package com.yilu.yinling.ai.controller;
import com.yilu.yinling.ai.client.LlmProperties; import com.yilu.yinling.ai.client.LlmRuntimeState; import com.yilu.yinling.ai.vo.AiStatus; import com.yilu.yinling.common.response.Result; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/ai") public class AiStatusController {
 private final LlmProperties properties; private final LlmRuntimeState state; public AiStatusController(LlmProperties p,LlmRuntimeState s){properties=p;state=s;}
 @GetMapping("/status") public Result<AiStatus> status(){String provider=properties.getProvider();String model="mock";if("deepseek".equals(provider))model=properties.getDeepseek().getModel();if("qwen".equals(provider))model=properties.getQwen().getModel();return Result.ok(new AiStatus(provider,model,properties.isEnabled(),state.isAvailable(),state.isFallback(),state.getMessage(),"CONNECTED"));}
}
