package com.yilu.yinling.ai.service;
import com.yilu.yinling.ai.dto.ChatRequest;
import com.yilu.yinling.ai.vo.ChatResponse;
public interface AssistantService { ChatResponse chat(Long userId, ChatRequest request); }
