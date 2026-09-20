package com.yilu.yinling.ai.controller;

import com.yilu.yinling.ai.dto.ChatRequest;
import com.yilu.yinling.ai.service.AssistantService;
import com.yilu.yinling.ai.vo.ChatResponse;
import com.yilu.yinling.common.response.Result;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation; import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/assistant")
@Tag(name = "AI助手")
public class AssistantController {
    private final AssistantService assistantService;
    public AssistantController(AssistantService assistantService) { this.assistantService = assistantService; }
    @PostMapping("/chat")
    @Operation(summary = "AI智能问答")
    public Result<ChatResponse> chat(Authentication authentication, @Valid @RequestBody ChatRequest request) {
        return Result.ok(assistantService.chat((Long) authentication.getDetails(), request));
    }
}
